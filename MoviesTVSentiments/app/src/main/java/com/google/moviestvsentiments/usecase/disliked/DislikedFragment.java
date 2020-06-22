package com.google.moviestvsentiments.usecase.disliked;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.moviestvsentiments.R;

/**
 * A fragment that displays the list of disliked assets.
 */
public class DislikedFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_disliked, container, false);
        final TextView textView = root.findViewById(R.id.text_disliked);
        textView.setText("This is the disliked fragment.");
        return root;
    }
}