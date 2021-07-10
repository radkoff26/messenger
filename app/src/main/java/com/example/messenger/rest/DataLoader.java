package com.example.messenger.rest;


import android.content.Context;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.messenger.MainActivity;
import com.example.messenger.R;

public class DataLoader extends AsyncTaskLoader<Void> {

    public DataLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Void loadInBackground() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
