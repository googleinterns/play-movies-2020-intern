package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import java.util.Objects;

/**
 * A resource backed by both the local Room database and a remote server.
 * @param <ServerType> The type of the resource on the server.
 * @param <LocalType> The type of the resource locally.
 */
public abstract class NetworkBoundResource<ServerType, LocalType> {

    private final MediatorLiveData<Resource<LocalType>> result = new MediatorLiveData<>();

    /**
     * Constructs a NetworkBoundResource that observes the local Room value for changes that
     * may or may not trigger a network request.
     */
    public NetworkBoundResource() {
        result.setValue(Resource.loading(null));
        LiveData<LocalType> dbSource = loadFromRoom();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    /**
     * Returns a LiveData wrapper around the requested resource.
     */
    public LiveData<Resource<LocalType>> getResult() {
        return result;
    }

    /**
     * Returns the ServerType object contained within the provided ApiResponse. The default
     * implementation of this method just returns the ApiResponse's body. Override this method if
     * the body needs to be processed in some way to get the correct value.
     * @param response The ApiResponse to process.
     * @return The ServerType object contained within the ApiResponse.
     */
    protected ServerType processResponse(ApiResponse<ServerType> response) {
        return response.getBody();
    }

    /**
     * Saves the result of the network call in the Room database.
     * @param item The return value from the network call.
     */
    protected abstract void saveCallResult(ServerType item);

    /**
     * Returns true if the resource should be updated by fetching a new copy from the network.
     * @param data The local copy of the resource stored in the Room database.
     * @return True if the resource should be fetched from the network.
     */
    protected abstract boolean shouldFetch(LocalType data);

    /**
     * Loads the requested resource from the Room database and returns a LiveData object wrapping
     * the resource.
     * @return A LiveData object wrapping the resource loaded from the Room database.
     */
    protected abstract LiveData<LocalType> loadFromRoom();

    /**
     * Performs the network call to the server and returns a LiveData object wrapping the response.
     * @return A LiveData object wrapping the response from the server.
     */
    protected abstract LiveData<ApiResponse<ServerType>> performNetworkCall();

    /**
     * Sets the value of the LiveData result object if it has changed.
     * @param newValue The new value of the result object.
     */
    private void setValue(Resource<LocalType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    /**
     * Fetches the resource from the network and, if successful, saves the result in the Room
     * database. The new result is then reloaded from the Room database. If unsuccessful, the Room
     * database will continue to be the source for the result.
     * @param dbSource The LiveData object returned by the Room database.
     */
    private void fetchFromNetwork(final LiveData<LocalType> dbSource) {
        LiveData<ApiResponse<ServerType>> apiResponse = performNetworkCall();
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response.isSuccessful()) {
                saveCallResult(processResponse(response));
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            } else {
                result.addSource(dbSource,
                        newData -> setValue(Resource.error(newData, response.getError())));
            }
        });
    }
}
