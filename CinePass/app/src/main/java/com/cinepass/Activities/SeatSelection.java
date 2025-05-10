package com.cinepass.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinepass.Adapters.SeatsAdapter;
import com.cinepass.Domain.Seat;
import com.cinepass.Domain.Ticket;
import com.cinepass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SeatSelection extends AppCompatActivity implements SeatsAdapter.OnSeatClickListener {

    private RecyclerView recyclerViewSeats;
    private SeatsAdapter seatAdapter;
    private ImageView backImg, nextImg;


    //Qr
    private String selectedSeat;
    private int idFilm;
    private String selectedTime;
    private String selectedDate;
    private String user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        //Verificação de segurança
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if(currentUser != null){
            user = currentUser.getUid();
        } else{
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
        }

        db = FirebaseFirestore.getInstance();

        CollectionReference seatsCollectionRef = db.collection("Seats");
        InitComponentSeatSelection();

        recyclerViewSeats = findViewById(R.id.lstTickets);
        recyclerViewSeats.setLayoutManager(new GridLayoutManager(this, 5));

        List<String> seats = new ArrayList<>();
        char[] rows = {'A', 'B', 'C', 'D'};

        for (char row : rows) {
            for (int i = 1; i <= 5; i++) {
                seats.add(row + "" + i);
            }
            for (String seat : seats) {
                // Extrair o nome e o ID do assento
                String seatName = seat.substring(0, 1); // Extrai a primeira letra como nome do assento
                String seatId = seat.substring(1); // Extrai o restante da string como ID do assento

                // Adicionar o assento ao Firestore
                seatsCollectionRef.document(seat).set(new Seat(seatName, seatId))
                        .addOnSuccessListener(aVoid -> {

                        })
                        .addOnFailureListener(e -> {

                        });
            }
        }
        seatAdapter = new SeatsAdapter(seats, this);
        recyclerViewSeats.setAdapter(seatAdapter);

        idFilm = getIntent().getIntExtra("id", 0);
        selectedTime = getIntent().getStringExtra("selectedTime");
        selectedDate = getIntent().getStringExtra("selectedDate");
    }


    private void InitComponentSeatSelection() {

        backImg = findViewById(R.id.backImg);
        nextImg = findViewById(R.id.nextImg);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSeat == null) {
                    Toast.makeText(SeatSelection.this, "Please select a seat", Toast.LENGTH_SHORT).show();
                    return;
                }
                Ticket ingresso = new Ticket();
                ingresso.setIdUsuario(user);
                ingresso.setIdFilme(idFilm);
                ingresso.setData(selectedDate);
                ingresso.setHorario(selectedTime);
                ingresso.setAssento(selectedSeat);
                saveTicketToFirestore(ingresso);
            }
        });

    }

    private void saveTicketToFirestore(Ticket ticket) {
        db.collection("Tickets").add(ticket)
                .addOnSuccessListener(documentReference -> {
                    String ticketId = documentReference.getId();
                    Toast.makeText(SeatSelection.this, "Ingresso salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    // Redirecionar para outra atividade ou realizar outra ação
                    goToQR(ticketId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SeatSelection.this, "Falha ao salvar o ingresso", Toast.LENGTH_SHORT).show();
                });
    }

    private void goToQR(String ticketId) {
        Intent intent = new Intent(SeatSelection.this, QRcode.class);
        intent.putExtra("ticketId", ticketId);
        startActivity(intent);
    }

    @Override
    public void onSeatClick(int position) {
        selectedSeat = seatAdapter.getSeats().get(position);
        Toast.makeText(this, "Assento selecionado: " + selectedSeat, Toast.LENGTH_SHORT).show();
    }
}