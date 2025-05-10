package com.cinepass.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cinepass.Domain.FilmItem;

import com.cinepass.Domain.Ticket;
import com.cinepass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Carrinho extends AppCompatActivity {

    private String user;
    private FirebaseFirestore db;

    TextView txtUserTickets;
    ImageView imageViewExplorar, imageViewProfile, backImg, imageViewPerfil;
    ListView lstTickets;
    List<Ticket> ingressos;
    List<String> dadosExibicao;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carrinho);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização
        initView();

        // Verificação de segurança
        db = FirebaseFirestore.getInstance();
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if (currentUser != null) {
            user = currentUser.getUid();
        } else {
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
            return; // Adicionado para evitar execução do código abaixo se o usuário não estiver logado
        }

        DocumentReference documentReference = db.collection("Users").document(user);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    String userName = documentSnapshot.getString("name");
                    txtUserTickets.setText("Carrinho de " + userName);
                }
            }
        });
        listar();
    }
    public void listar() {
        ingressos = new ArrayList<>();
        dadosExibicao = new ArrayList<>();

        db.collection("Tickets")
                .whereEqualTo("idUsuario", user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Ticket ticket = document.toObject(Ticket.class);
                            ticket.setId(document.getId());
                            ingressos.add(ticket);

                        }
                        carregarDadosFilmes();
                    } else {
                        Toast.makeText(Carrinho.this, "Erro ao carregar tickets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void carregarDadosFilmes() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        for (Ticket ticket : ingressos) {
            int idFilme = ticket.getIdFilme();
            StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilme, response -> {
                Gson gson = new Gson();
                FilmItem item = gson.fromJson(response, FilmItem.class);

                String dados = "Filme: " + item.getTitle() + "\n" +
                        "Data: " + ticket.getData() + "\n" +
                        "Horario: " + ticket.getHorario() + "\n" +
                        "Assento: " + ticket.getAssento() + "\n";

                dadosExibicao.add(dados);
                if (dadosExibicao.size() == ingressos.size()) {
                    atualizarListView();
                }
            }, error -> {
                Log.i("Cinepass", "onErrorResponse: " + error.toString());
            });
            mRequestQueue.add(mStringRequest);
        }
    }

    private void atualizarListView() {
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dadosExibicao);
        lstTickets.setAdapter(adaptador);
    }

    private void initView(){
        txtUserTickets = findViewById(R.id.txtUserTickets);
        imageViewExplorar = findViewById(R.id.imageViewExplorar);
        imageViewProfile = findViewById(R.id.imageViewPerfil);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        backImg = findViewById(R.id.backImg);
        lstTickets = findViewById(R.id.lstTickets);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrinho.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrinho.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        imageViewExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrinho.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrinho.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        lstTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ingressos != null && position < ingressos.size()) {
                    Ticket ingresso = ingressos.get(position);

                    Intent intent = new Intent(Carrinho.this, QRcode.class);
                    intent.putExtra("ticketId", ingresso.getId());
                    startActivity(intent);
                }
            }
        });
    }
}
