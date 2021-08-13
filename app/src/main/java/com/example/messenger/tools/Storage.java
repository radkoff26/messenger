package com.example.messenger.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.messenger.models.Constants.BASE_URL;

public class Storage {

    // This method will launch only in Thread
    public static void saveImage(String filename, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = Picasso
                    .with(context)
                    .load(BASE_URL + "/getAvatar?url=" + filename)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            return;
        }

        ContextWrapper wrapper = new ContextWrapper(context);

        File directory = wrapper.getCacheDir();
        File path = new File(directory, filename + ".png");

        try (FileOutputStream stream = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getAndSetImageIfExists(String filename, Context context, ImageView imageView) {
        ContextWrapper wrapper = new ContextWrapper(context);

        File directory = wrapper.getCacheDir();
        File path = new File(directory, filename + ".png");

        try (FileInputStream stream = new FileInputStream(path)) {
            if (BitmapFactory.decodeStream(stream) == null) {
                return false;
            }
            Picasso
                    .with(context)
                    .load(path)
                    .into(imageView);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
