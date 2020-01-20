package com.mercadolibre.test.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;

import android.os.AsyncTask;

import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlToDisplay = urls[0];

        Bitmap bitmap = null;

        try {
            InputStream inputStream = new java.net.URL(urlToDisplay).openStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e("ML_ERROR", "Ocurrio un error al intentar procesar la URL para obtener el InputStream - Error: " + e.getMessage());
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}