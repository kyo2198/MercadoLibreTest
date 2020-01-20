package com.mercadolibre.test.Activities;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.content.Context;

import android.os.Bundle;

import android.view.View;

import android.widget.TextView;
import android.widget.RelativeLayout;

import com.mercadolibre.test.R;
import com.mercadolibre.test.Utils.InternetCheck;
import com.mercadolibre.test.Adapters.ImagePagerAdapter;

import java.util.List;
import java.util.ArrayList;

public class ItemDetailActivity extends AppCompatActivity {

    private Context context;

    private RelativeLayout noInterneMessase;

    private PagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private TextView imageCount;

    private List<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Seteo del tema para Splash inicial
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        //Una vez realizado el onCreate se setea el layout para la actividad
        setContentView(R.layout.activity_item_detail);

        context = this;

        //Inicialización de la Toolbar
        initializeToolbar();

        //Obtengo el TextView donde se muestra la cantidad de imagenes que posee la publicacion
        imageCount = findViewById(R.id.image_count);

        //Obtengo el layouts para el mensaje de "No hay internet"
        noInterneMessase = findViewById(R.id.no_internet_messase);

        //Obtengo el intent enviado por la actividad anterior y saco los datos a mostrar
        Intent intent = getIntent();

        //Como no pude encontrar en la documentacion el metodo correspondiente para obtener
        //todas las imagenes de una publicacion, decidi cargar en una lista la misma imagen
        //3 veces, para asi poder cargar un ImagePager como la hace la App real de MercadoLibre
        urls = new ArrayList<>();
        urls.add(intent.getStringExtra("Thumbnail"));
        urls.add(intent.getStringExtra("Thumbnail"));
        urls.add(intent.getStringExtra("Thumbnail"));

        pagerAdapter = new ImagePagerAdapter(this, urls);

        viewPager = findViewById(R.id.image_view_pager);
        viewPager.setAdapter(pagerAdapter);

        //Cargo los datos enviados a travez del intent en sus correspondientes TextViews
        TextView conditionAndSoldQuantity = findViewById(R.id.condition_and_sold_quantity);

        String conditionString = intent.getStringExtra("Condition");
        String soldQuantityString = String.valueOf(intent.getIntExtra("Sold quantity", 0) > 0 ? intent.getIntExtra("Sold quantity", 0) : "");

        String conditionAndSoldQuantityString = conditionString.isEmpty() ? (soldQuantityString.isEmpty() ? "" : soldQuantityString + (soldQuantityString.equals("1") ? " vendido" : " vendidos")) : conditionString + (soldQuantityString.isEmpty() ? "" : (" - " + soldQuantityString + (soldQuantityString.equals("1") ? " vendido" : " vendidos")));

        if(conditionAndSoldQuantityString.isEmpty() == false)
            conditionAndSoldQuantity.setText(conditionAndSoldQuantityString);
        else
            conditionAndSoldQuantity.setVisibility(View.GONE);

        TextView description = findViewById(R.id.description);
        description.setText(intent.getStringExtra("Description"));

        TextView price = findViewById(R.id.price);
        price.setText("$" + intent.getStringExtra("Price"));

        TextView quantity = findViewById(R.id.quantity);
        quantity.setText(intent.getIntExtra("Quantity", 0) + "");

        //Este metodo permite saber si estamos conectados a internet, de lo contrario
        //muestra un mensaje informando sobre tal hecho
        InternetChecker();
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,0,0);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
    }

    public void OnClickRetryButton(View view) {
        InternetChecker();
    }

    public void InternetChecker() {
        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(Boolean internetResponse) {
                if(internetResponse) {
                    noInterneMessase.setVisibility(View.GONE);

                    imageCount.setVisibility(View.VISIBLE);

                    //Si hay conectividad o se recupero luego de una caida,
                    //volvemos a cargar las imagenes al ImagePager
                    pagerAdapter = new ImagePagerAdapter(context, urls);
                    viewPager.setAdapter(pagerAdapter);
                    pagerAdapter.notifyDataSetChanged();
                }
                else {
                    noInterneMessase.setVisibility(View.VISIBLE);

                    imageCount.setVisibility(View.GONE);
                }
            }
        });
    }
}