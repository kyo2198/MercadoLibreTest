package com.mercadolibre.test.Activities;

import com.mercadolibre.test.R;
import com.mercadolibre.test.Model.Item;
import com.mercadolibre.test.Utils.InternetCheck;
import com.mercadolibre.test.Adapters.ItemsAdapter;
import com.mercadolibre.test.Services.MercadoLibreService;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.content.Intent;
import android.content.Context;

import android.content.res.Configuration;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private LinearLayout noResultsMessagePortrait;
    private LinearLayout noResultsMessageLandscape;

    private LinearLayout noInternetMessagePortrait;
    private LinearLayout noInternetMessageLandscape;

    private LinearLayout progressBar;

    private RecyclerView recyclerView;

    private List<Item> items;

    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Seteo del tema para Splash inicial
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        //Una vez realizado el onCreate se setea el layout para la actividad
        setContentView(R.layout.activity_main);

        context = this;

        //Inicialización de la Toolbar, la cual contiene la barra de busqueda y NavigationDrawer
        initializeToolbarAndSearchBar();

        //Obtengo los layouts (en portrait y landscape) para los mensajes de "No hay internet"
        //y "No hay resultados"
        noResultsMessagePortrait = findViewById(R.id.no_results_message_portrait);
        noResultsMessageLandscape = findViewById(R.id.no_results_message_landscape);

        noInternetMessagePortrait = findViewById(R.id.no_internet_message_portrait);
        noInternetMessageLandscape = findViewById(R.id.no_internet_message_landscape);

        //Obtengo el spinner de loading
        progressBar = findViewById(R.id.progress_bar);

        //Seteo del ReciclerView
        initializeRecyclerView();

        //Este metodo permite saber si estamos conectados a internet, de lo contrario
        //muestra un mensaje informando sobre tal hecho
        InternetChecker();
    }

    private void initializeToolbarAndSearchBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,0,0);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GetItems(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }

    private void initializeRecyclerView() {
        //Obtengo el RecyclerView para cargar las view pertenecientes a cada uno de los resultados
        recyclerView = findViewById(R.id.recycler_view);

        //Se inicializa la lista que contendra los resultados
        items = new ArrayList<>();

        //Se inicializa el Listener para el ItemsAdapter del RecylerView, de esta manera se podra
        //acceder al detalle de cada uno de los resultados
        ItemsAdapter.ItemViewClickListener itemViewClickListener = new ItemsAdapter.ItemViewClickListener() {
            @Override
            public void OnClick(View view, int position) {
                //Obtengo el item correspondiente
                Item item = items.get(position);

                //Creo el Intent para poder llamar a la nueva actividad, al cual previamente
                //le paso los datos que voy a necesitar para mostrar en la nueva actividad
                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                intent.putExtra("Thumbnail", item.GetThumbnail());
                intent.putExtra("Description", item.GetDescription());
                intent.putExtra("Price", item.GetPrice());
                intent.putExtra("Shipping", item.GetShipping());
                intent.putExtra("Condition", item.GetCondition());
                intent.putExtra("Quantity", item.GetQuantity());
                intent.putExtra("Sold quantity", item.GetSoldQuantity());

                startActivity(intent);
            }
        };

        //Se inicializa el ItemsAdapter con la lista de items obtenidos (al momento de la creacion
        //aun no tiene ningun elemetno) y su correspondiente Listener
        itemsAdapter = new ItemsAdapter(items, itemViewClickListener);

        //Seteo del adapter al RecylerView
        recyclerView.setAdapter(itemsAdapter);

        //Se agrega un separador para cada una las views agregadas al Recyler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        //Este metodo permite saber si estamos conectados a internet, de lo contrario
        //muestra un mensaje informando sobre tal hecho
        InternetChecker();

        //Luego de chequear la conectividad a internet, recargarmos los layouts de acuerdo a la orientacion
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
            recyclerView.getVisibility() == View.GONE &&
            noInternetMessagePortrait.getVisibility() == View.GONE &&
            noInternetMessageLandscape.getVisibility() == View.GONE) {
            noResultsMessagePortrait.setVisibility(View.VISIBLE);
            noResultsMessageLandscape.setVisibility(View.GONE);
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            recyclerView.getVisibility() == View.GONE &&
            noInternetMessagePortrait.getVisibility() == View.GONE &&
            noInternetMessageLandscape.getVisibility() == View.GONE) {
            noResultsMessagePortrait.setVisibility(View.GONE);
            noResultsMessageLandscape.setVisibility(View.VISIBLE);
        }
    }

    void GetItems(String query) {
        //Se muestra la ProgressBar mientras se buscan resultados
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //Este metodo permite saber si estamos conectados a internet, de lo contrario
        //muestra un mensaje informando sobre tal hecho
        InternetChecker();

        //Antes de cada busqueda limpiamos la lista de items
        items.clear();
        recyclerView.removeAllViews();

        //Pedimos al ItemsAdapter que refresque sus datos
        itemsAdapter.notifyDataSetChanged();

        //Se realiza la llamada al serivicio para asi obtener los resultados de la busqueda
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://api.mercadolibre.com/sites/MLA/search?q=" + query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("results");

                            //De haber resultados, vamos iterando uno a uno para asi cargar la lista con los items correspondientes
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectResults = jsonArray.getJSONObject(i);

                                Item item = new Item();
                                item.SetThumbnail(jsonObjectResults.getString("thumbnail"));
                                item.SetDescription(jsonObjectResults.getString("title"));
                                item.SetPrice(jsonObjectResults.getString("price"));
                                item.SetShipping(jsonObjectResults.getJSONObject("shipping").getString("free_shipping").equals("true") ? true : false);

                                String condition = jsonObjectResults.getJSONArray("attributes").getJSONObject(0).getString("value_name");
                                item.SetCondition(condition.equals("Nuevo") ? "Nuevo" : "");

                                item.SetQuantity(Integer.parseInt(jsonObjectResults.getString("available_quantity")));
                                item.SetSoldQuantity(Integer.parseInt(jsonObjectResults.getString("sold_quantity")));

                                items.add(item);

                                itemsAdapter.notifyDataSetChanged();
                            }

                            //Una vez terminada la carga de los posibles resultados, ocultamos la ProgressBar
                            progressBar.setVisibility(View.GONE);

                            //En base a los resultados obtenidos ve analiza si se muestra o no
                            //el mensaje que indica que no hubo resultados
                            if(jsonArray.length() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);

                                noResultsMessagePortrait.setVisibility(View.GONE);
                                noResultsMessageLandscape.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.GONE);

                                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    noResultsMessagePortrait.setVisibility(View.VISIBLE);
                                    noResultsMessageLandscape.setVisibility(View.GONE);
                                } else {
                                    noResultsMessagePortrait.setVisibility(View.GONE);
                                    noResultsMessageLandscape.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d("ML_TEST", "Ocurrio un error al procesar la solicitud - Error: " + e.getMessage());

                            new AlertDialog.Builder(context)
                                    .setTitle("Error al procesar la solicitud")
                                    .setMessage("Ocurrio un error al procesar la solicitud, por favor vuelva a intentarlo.")
                                    .setPositiveButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ML_TEST", "Ocurrio un error al procesar la solicitud - Error: " + error.getMessage());
                    }
                });

        MercadoLibreService.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void OnClickRetryButton(View view) {
        InternetChecker();
    }

    public void InternetChecker() {
        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(Boolean internetResponse) {
                if(internetResponse)  {
                    if(noResultsMessagePortrait.getVisibility() == View.GONE &&
                       noResultsMessageLandscape.getVisibility() == View.GONE)
                       recyclerView.setVisibility(View.VISIBLE);

                    noInternetMessagePortrait.setVisibility(View.GONE);
                    noInternetMessageLandscape.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);

                    recyclerView.setVisibility(View.GONE);

                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        noInternetMessagePortrait.setVisibility(View.VISIBLE);
                        noInternetMessageLandscape.setVisibility(View.GONE);
                    } else {
                        noInternetMessagePortrait.setVisibility(View.GONE);
                        noInternetMessageLandscape.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}