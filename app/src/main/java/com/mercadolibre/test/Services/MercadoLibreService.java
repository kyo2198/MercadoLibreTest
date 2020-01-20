package com.mercadolibre.test.Services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

public final class MercadoLibreService {

    private static Context context;

    private static MercadoLibreService mercadoLibreServiceSingleton;

    private RequestQueue requestQueue;

    private MercadoLibreService(Context context) {
        MercadoLibreService.context = context;

        requestQueue = getRequestQueue();
    }

    public static synchronized MercadoLibreService getInstance(Context context) {
        if (mercadoLibreServiceSingleton == null) {
            mercadoLibreServiceSingleton = new MercadoLibreService(context);
        }

        return mercadoLibreServiceSingleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public void addToRequestQueue(Request request) {
        getRequestQueue().add(request);
    }
}