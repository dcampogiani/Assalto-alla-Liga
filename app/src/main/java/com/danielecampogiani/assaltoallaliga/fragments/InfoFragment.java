package com.danielecampogiani.assaltoallaliga.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danielecampogiani.assaltoallaliga.R;


/**
 * Created by danielecampogiani on 15/01/15.
 */
public class InfoFragment extends Fragment {

    private static final String TEXT_KEY = "TEXT_KEY";

    private String text;

    public static InfoFragment newInstance(String text) {
        if (text == null)
            throw new NullPointerException("text can't be null");
        else if ("".equals(text))
            throw new IllegalArgumentException("text can't be empty");
        InfoFragment result = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(TEXT_KEY, text);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = getArguments().getString(TEXT_KEY, "");
        if ("".equals(text))
            throw new RuntimeException("text can't be empty");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_info, container, false);
        TextView textView = (TextView) root.findViewById(R.id.textView);
        textView.setText(text);
        return root;
    }
}
