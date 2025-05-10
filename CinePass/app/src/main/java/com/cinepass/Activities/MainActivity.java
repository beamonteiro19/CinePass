package com.cinepass.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cinepass.Adapters.CategoryListAdapter;
import com.cinepass.Adapters.FilmListAdapter;
import com.cinepass.Adapters.SliderAdapter;
import com.cinepass.Domain.GenresItem;
import com.cinepass.Domain.ListFilm;
import com.cinepass.Domain.SliderItem;
import com.cinepass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterNowPlaying, adapterUpComming, adapterCategory;
    private RecyclerView recyclerViewNowPlaying, recyclerViewUpcomming, recycleViewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest, mStringRequest2, mStringRequest3;
    private ProgressBar loading1, loading2, loading3;

    private ImageView imageViewPerfil, imageViewCarrinho;
    private ViewPager2 viewPager2;
    private Handler slideHolder = new Handler();
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        imageViewCarrinho = findViewById(R.id.imageViewCarrinho);

        initView();

        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if(currentUser == null){
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
        }


        banners();
        sendRequestNowPlaying();
        sendRequestUpComming();
        sendRequestCategory();

        imageViewPerfil.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Profile.class));
            finish();
        });
        imageViewCarrinho.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Carrinho.class));
            finish();
        });
    }

    private void sendRequestNowPlaying() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=1", response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            ListFilm items = gson.fromJson(response, ListFilm.class);
            adapterNowPlaying = new FilmListAdapter(items);
            recyclerViewNowPlaying.setAdapter(adapterNowPlaying);
        }, error -> {
            loading1.setVisibility(View.GONE);
            Log.i("Cinepass", "onErrorResponse: " + error.toString());
        });

        mRequestQueue.add(mStringRequest);
    }

    private void sendRequestCategory() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest2 = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/genres", response -> {
            Gson gson = new Gson();
            loading2.setVisibility(View.GONE);
            ArrayList<GenresItem> catList = gson.fromJson(response, new TypeToken<ArrayList<GenresItem>>() {
            }.getType());
            adapterCategory = new CategoryListAdapter(catList);
            recycleViewCategory.setAdapter(adapterCategory);
        }, error -> {
            loading2.setVisibility(View.GONE);
            Log.i("Cinepass", "onErrorResponse: " + error.toString());
        });

        mRequestQueue.add(mStringRequest2);
    }

    private void sendRequestUpComming() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);
        mStringRequest3 = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=2", response -> {
            Gson gson = new Gson();
            loading3.setVisibility(View.GONE);
            ListFilm items = gson.fromJson(response, ListFilm.class);
            adapterUpComming = new FilmListAdapter(items);
            recyclerViewUpcomming.setAdapter(adapterUpComming);
        }, error -> {
            loading3.setVisibility(View.GONE);
            Log.i("Cinepass", "onErrorResponse: " + error.toString());
        });

        mRequestQueue.add(mStringRequest3);
    }

    private void banners() {
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.wide));
        sliderItems.add(new SliderItem(R.drawable.wide1));
        sliderItems.add(new SliderItem(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % sliderItems.size();
                viewPager2.setCurrentItem(nextItem);
                slideHolder.postDelayed(this, 4000);
            }
        };

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHolder.removeCallbacks(sliderRunnable);
                slideHolder.postDelayed(sliderRunnable, 4000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHolder.postDelayed(sliderRunnable, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHolder.removeCallbacks(sliderRunnable);
    }

    private void initView() {
        viewPager2 = findViewById(R.id.viewPageSlider);
        recyclerViewNowPlaying = findViewById(R.id.viewCartaz);
        recyclerViewNowPlaying.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycleViewCategory = findViewById(R.id.viewCategory);
        recycleViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcomming = findViewById(R.id.viewEmBreve);
        recyclerViewUpcomming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loading1 = findViewById(R.id.progressBarNowPlaying);
        loading2 = findViewById(R.id.progressBarCategory);
        loading3 = findViewById(R.id.progressBarUpComming);
    }
}
