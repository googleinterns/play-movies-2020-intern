package com.google.moviestvsentiments.service.web;

import static com.google.common.truth.Truth.assertThat;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import retrofit2.Response;

@RunWith(JUnitParamsRunner.class)
public class NetworkBoundResourceTest {

    private static final String LOCAL_VALUE = "Local Value";
    private static final String SERVER_VALUE = "Server Value";

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private Object[] invokesCorrectMethodsParameters() {
        return new Object[] {
            new Object[] {false, new boolean[] {false, true, true, false} },
            new Object[] {true, new boolean[] {true, true, true, true} }
        };
    }

    @Test
    @Parameters(method = "invokesCorrectMethodsParameters")
    public void shouldFetchResult_invokesCorrectMethods(boolean shouldFetch,
                                                        boolean[] expectedInvocations) {
        final boolean[] invocations = {false, false, false, false};
        NetworkBoundResource resource = new NetworkBoundResource<String, String>() {
            @Override protected void saveCallResult(String item) {
                invocations[0] = true;
            }

            @Override protected boolean shouldFetch(String data) {
                invocations[1] = true;
                return shouldFetch;
            }

            @Override protected LiveData<String> loadFromRoom() {
                invocations[2] = true;
                return new MutableLiveData<>("testValue");
            }

            @Override protected LiveData<ApiResponse<String>> performNetworkCall() {
                invocations[3] = true;
                return new MutableLiveData<>(new ApiResponse(Response.success("Server value")));
            }
        };

        resource.getResult().observeForever(data -> {});

        assertThat(invocations).isEqualTo(expectedInvocations);
    }

    @Test
    public void networkBoundResource_passesCorrectParameters() {
        NetworkBoundResource resource = new NetworkBoundResource<String, String>() {
            @Override protected void saveCallResult(String item) {
                assertThat(item).isEqualTo(SERVER_VALUE);
            }

            @Override protected boolean shouldFetch(String data) {
                assertThat(data).isEqualTo(LOCAL_VALUE);
                return true;
            }

            @Override protected LiveData<String> loadFromRoom() {
                return new MutableLiveData<>(LOCAL_VALUE);
            }

            @Override protected LiveData<ApiResponse<String>> performNetworkCall() {
                return new MutableLiveData<>(new ApiResponse(Response.success(SERVER_VALUE)));
            }
        };

        resource.getResult().observeForever(data -> {});
    }

    private Object[] returnsCorrectValueParameters() {
        return new Object[] {
            new Object[] { false, null, Resource.success(LOCAL_VALUE) },
            new Object[] { true, new ApiResponse(new RuntimeException("Error message")),
                    Resource.error(LOCAL_VALUE, "Error message") },
            new Object[] { true, new ApiResponse(Response.success(SERVER_VALUE)),
                    Resource.success(SERVER_VALUE) }
        };
    }

    @Test
    @Parameters(method = "returnsCorrectValueParameters")
    public void networkBoundResource_returnsCorrectValue(boolean shouldFetch,
                                     ApiResponse<String> networkResponse, Resource expectedResult) {
        MutableLiveData<String> roomValue = new MutableLiveData<>(LOCAL_VALUE);
        NetworkBoundResource resource = new NetworkBoundResource<String, String>() {
            @Override protected void saveCallResult(String item) {
                roomValue.setValue(item);
            }

            @Override protected boolean shouldFetch(String data) {
                return shouldFetch;
            }

            @Override protected LiveData<String> loadFromRoom() {
                return roomValue;
            }

            @Override protected LiveData<ApiResponse<String>> performNetworkCall() {
                return new MutableLiveData<>(networkResponse);
            }
        };

        Resource<String> result = (Resource<String>)LiveDataTestUtil.getValue(resource.getResult());

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void networkBoundResource_noRoomResult_returnsLoadingResource() {
        NetworkBoundResource resource = new NetworkBoundResource<String, String>() {
            @Override protected void saveCallResult(String item) {}

            @Override protected boolean shouldFetch(String data) {
                return false;
            }

            @Override protected LiveData<String> loadFromRoom() {
                return new MutableLiveData<>();
            }

            @Override protected LiveData<ApiResponse<String>> performNetworkCall() {
                return null;
            }
        };

        Resource<String> result = (Resource<String>)LiveDataTestUtil.getValue(resource.getResult());

        assertThat(result).isEqualTo(Resource.loading(null));
    }

    @Test
    public void networkBoundResource_noServerResult_returnsLoadingResource() {
        NetworkBoundResource resource = new NetworkBoundResource<String, String>() {
            @Override protected void saveCallResult(String item) {}

            @Override protected boolean shouldFetch(String data) {
                return true;
            }

            @Override protected LiveData<String> loadFromRoom() {
                return new MutableLiveData<>(LOCAL_VALUE);
            }

            @Override protected LiveData<ApiResponse<String>> performNetworkCall() {
                return new MutableLiveData<>();
            }
        };

        Resource<String> result = (Resource<String>)LiveDataTestUtil.getValue(resource.getResult());

        assertThat(result).isEqualTo(Resource.loading(LOCAL_VALUE));
    }
}
