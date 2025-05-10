package com.cinepass.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cinepass.Adapters.ActorsListAdapter;
import com.cinepass.Adapters.CategoryEachFilmListAdapter;
import com.cinepass.Adapters.MovieInfoAdapter;
import com.cinepass.Adapters.SessionDateAdapter;
import com.cinepass.Domain.FilmItem;
import com.cinepass.R;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView sessionDateRecyclerView;
    private RecyclerView movieInfoRecyclerView, movieInfoRecyclerView2, movieInfoRecyclerView3;
    private MovieInfoAdapter movieInfoAdapter, movieInfoAdapter2, movieInfoAdapter3;
    private ProgressBar progressBar;
    private TextView titleText, movieTimeText, movieRateTxt, movieSummaryInfo, movieActorsInfo, titleShowDescription,titleShowSession;
    private int idFilm;
    private ImageView picDetail, backImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory;
    private RecyclerView recyclerViewActor, recyclerViewCategory;
    private NestedScrollView scrollView;
    private String selectedDate;
    private String selectedTime;

    //pra mudar a cor conforme e o clique
    private int selected;
    private int unselected;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        initView(); // Inicialize os componentes de layout primeiro

        //Verificação de segurança
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if(currentUser != null){
            String user = currentUser.getEmail();
        } else{
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
        }

        idFilm = getIntent().getIntExtra("id", 0);
        sendRequest();
        setupSessionDateRecyclerView();
        setupMovieInfoRecyclerView();

    }

    //EVENTO CLIQUE NO HORARIO
    public void onScheduleClick(View view) {
        Intent intent = new Intent(DetailActivity.this, SeatSelection.class);
        startActivity(intent);
    }

    private void sendRequest() {
        RequestQueue mrequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilm, response -> {
            Gson gson = new Gson();
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            FilmItem item = gson.fromJson(response, FilmItem.class);
            Glide.with(DetailActivity.this).load(item.getPoster()).into(picDetail);

            titleText.setText(item.getTitle());
            movieTimeText.setText(item.getRuntime());
            movieSummaryInfo.setText(item.getPlot());
            movieActorsInfo.setText(item.getActors());
            movieRateTxt.setText(item.getImdbRating());

            if (item.getImages() != null) {
                adapterActorList = new ActorsListAdapter(item.getImages());
                recyclerViewActor.setAdapter(adapterActorList);
            }
            if (item.getGenres() != null) {
                adapterCategory = new CategoryEachFilmListAdapter(item.getGenres());
                recyclerViewCategory.setAdapter(adapterCategory);
            }

            List<MovieInfoAdapter.MovieInfo> movieInfoList2D = new ArrayList<>();
            movieInfoList2D.add(new MovieInfoAdapter.MovieInfo("2D", "Dublado", "10:00", "13:00"));

            List<MovieInfoAdapter.MovieInfo> movieInfoList3D = new ArrayList<>();
            movieInfoList3D.add(new MovieInfoAdapter.MovieInfo("3D", "Dublado", "12:00", "15:00"));

            List<MovieInfoAdapter.MovieInfo> movieInfoListIMAX = new ArrayList<>();
            movieInfoListIMAX.add(new MovieInfoAdapter.MovieInfo("IMAX", "Legendado", "14:00", "17:00"));

            movieInfoAdapter = new MovieInfoAdapter(movieInfoList2D, time -> {
                selectedTime = time;
                Toast.makeText(this, "Selected time: " + time, Toast.LENGTH_SHORT).show();
                selectedSession();
            });
            movieInfoAdapter2 = new MovieInfoAdapter(movieInfoList3D, time -> {
                selectedTime = time;
                Toast.makeText(this, "Selected time: " + time, Toast.LENGTH_SHORT).show();
                selectedSession();
            });
            movieInfoAdapter3 = new MovieInfoAdapter(movieInfoListIMAX, time -> {
                selectedTime = time;
                Toast.makeText(this, "Selected time: " + time, Toast.LENGTH_SHORT).show();
                selectedSession();
            });

            movieInfoRecyclerView.setAdapter(movieInfoAdapter);
            movieInfoRecyclerView2.setAdapter(movieInfoAdapter2);
            movieInfoRecyclerView3.setAdapter(movieInfoAdapter3);

        }, volleyError -> progressBar.setVisibility(View.GONE));
        mrequestQueue.add(mStringRequest);
    }

    private void initView() {
        titleText = findViewById(R.id.movieName);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollViewDetail);
        picDetail = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeText = findViewById(R.id.txtUserTickets);
        movieSummaryInfo = findViewById(R.id.movieSummary);
        movieActorsInfo = findViewById(R.id.movieActors);
        backImg = findViewById(R.id.backImg);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActor = findViewById(R.id.imagesRecycler);
        recyclerViewActor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sessionDateRecyclerView = findViewById(R.id.sessionDate);
        movieInfoRecyclerView = findViewById(R.id.infoMovieRecyclerView1);
        movieInfoRecyclerView2 = findViewById(R.id.infoMovieRecyclerView2);
        movieInfoRecyclerView3 = findViewById(R.id.infoMovieRecyclerView3);
        TextView textSessionDate = findViewById(R.id.textSessionDate);


        //mudar a cor com o clique - detalhes e sessões
        titleShowDescription = findViewById(R.id.titleShowDescription);
        titleShowSession = findViewById(R.id.titleShowSession);
        selected = getResources().getColor(R.color.white);
        unselected = getResources().getColor(R.color.yellow);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }


    //ESSE MÉTODO PEGA A DATA DE AMANHÃ E ITERA MAIS 5 DIAS EM TEMPO REAL, JOGA NO BD E TROCA OS DIAS EM TEMPO REAL TBM
    private void setupSessionDateRecyclerView() {
        sessionDateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        LocalDate today = LocalDate.now();
        List<String> sessionDates = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE dd/MM");
        for (int i = 0; i < 6; i++) {
            String sessionDate = today.plusDays(i).format(dateFormatter);
            sessionDates.add(sessionDate);
        }

        // Configura o RecyclerView com as datas das sessões
        SessionDateAdapter sessionDateAdapter = new SessionDateAdapter(sessionDates, date -> {
            selectedDate = date;
            Toast.makeText(this, "Selected date: " + date, Toast.LENGTH_SHORT).show();
        });
        sessionDateRecyclerView.setAdapter(sessionDateAdapter);
    }

    private void setupMovieInfoRecyclerView() {
        movieInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieInfoRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        movieInfoRecyclerView3.setLayoutManager(new LinearLayoutManager(this));

        movieInfoRecyclerView.setAdapter(movieInfoAdapter);
        movieInfoRecyclerView2.setAdapter(movieInfoAdapter2);
        movieInfoRecyclerView3.setAdapter(movieInfoAdapter3);
    }


    public void toggleDescriptionVisibility(View view) {

        titleShowDescription.setTextColor(selected);
        titleShowSession.setTextColor(unselected);

        TextView titleSummary = findViewById(R.id.titleSummary);
        TextView movieSummary = findViewById(R.id.movieSummary);
        TextView titleActors = findViewById(R.id.titleActors);
        TextView movieActors = findViewById(R.id.movieActors);
        RecyclerView imagesRecycler = findViewById(R.id.imagesRecycler);

        RecyclerView sessionDateRecyclerView = findViewById(R.id.sessionDate);
        MaterialCardView[] movieInfoCards = {
                findViewById(R.id.movieInfoCard1),
                findViewById(R.id.movieInfoCard2),
                findViewById(R.id.movieInfoCard3)
        };

        if (titleSummary.getVisibility() == View.VISIBLE) {
            // Se já estiver visível, não faz nada e retorna
            return;
        }

        titleSummary.setVisibility(View.VISIBLE);
        movieSummary.setVisibility(View.VISIBLE);
        titleActors.setVisibility(View.VISIBLE);
        movieActors.setVisibility(View.VISIBLE);
        imagesRecycler.setVisibility(View.VISIBLE);

        sessionDateRecyclerView.setVisibility(View.GONE);
        for (MaterialCardView cardView : movieInfoCards) {
            cardView.setVisibility(View.GONE);
        }
    }

    public void toggleSessionVisibility(View view) {

        titleShowSession.setTextColor(selected);
        titleShowDescription.setTextColor(unselected);

        RecyclerView sessionDateRecyclerView = findViewById(R.id.sessionDate);
        MaterialCardView[] movieInfoCards = {
                findViewById(R.id.movieInfoCard1),
                findViewById(R.id.movieInfoCard2),
                findViewById(R.id.movieInfoCard3)
        };

        TextView titleSummary = findViewById(R.id.titleSummary);
        TextView movieSummary = findViewById(R.id.movieSummary);
        TextView titleActors = findViewById(R.id.titleActors);
        TextView movieActors = findViewById(R.id.movieActors);
        RecyclerView imagesRecycler = findViewById(R.id.imagesRecycler);

        if (sessionDateRecyclerView.getVisibility() == View.VISIBLE) {
            // Se já estiverem visíveis, não faz nada e retorna, pro app n crashar
            return;
        }

        sessionDateRecyclerView.setVisibility(View.VISIBLE);
        for (MaterialCardView cardView : movieInfoCards) {
            cardView.setVisibility(View.VISIBLE);
        }

        for (MaterialCardView cardView : movieInfoCards) {
            if (cardView.getVisibility() == View.VISIBLE) {
                cardView.post(() -> {
                    int scrollY = sessionDateRecyclerView.getBottom() + cardView.getHeight();
                    scrollView.smoothScrollTo(0, scrollY);
                });
            }
        }

        // Oculta os elementos relacionados à descrição
        titleSummary.setVisibility(View.GONE);
        movieSummary.setVisibility(View.GONE);
        titleActors.setVisibility(View.GONE);
        movieActors.setVisibility(View.GONE);
        imagesRecycler.setVisibility(View.GONE);
    }

    public void selectedSession() {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SeatSelection.class);
        intent.putExtra("id", idFilm);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedTime", selectedTime);
        startActivity(intent);

    }
}