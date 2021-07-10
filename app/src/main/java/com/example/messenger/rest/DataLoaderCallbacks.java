package com.example.messenger.rest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class DataLoaderCallbacks implements LoaderManager.LoaderCallbacks {
    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
