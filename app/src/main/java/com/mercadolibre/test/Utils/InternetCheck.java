package com.mercadolibre.test.Utils;

import android.os.AsyncTask;

import java.io.IOException;

import java.net.Socket;
import java.net.InetSocketAddress;

public class InternetCheck extends AsyncTask<Void, Void, Boolean> {

    private Consumer consumer;

    public interface Consumer {
        void accept(Boolean response);
    }

    public InternetCheck(Consumer consumer) {
        this.consumer = consumer;

        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();

            return true;
        } catch(IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean response) {
        consumer.accept(response);
    }
}