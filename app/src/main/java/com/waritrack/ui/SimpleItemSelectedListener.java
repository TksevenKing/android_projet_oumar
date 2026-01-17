package com.waritrack.ui;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public interface OnItemSelected {
        void onItemSelected(int position);
    }

    private final OnItemSelected callback;

    public SimpleItemSelectedListener(OnItemSelected callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        callback.onItemSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
