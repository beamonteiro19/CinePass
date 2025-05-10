package com.cinepass.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cinepass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {

    private TextView textUserName, textUserEmail, textProfileUSer;
    private Button btnLogout;

    private ImageView imageViewExplorar, backImg, imageViewCarrinho;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userId;

    //NATHAN
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        InitComponentProfile();

        //NATHAN

        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if (currentUser != null) {
            user = currentUser.getUid();
        } else {
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
        }

        //ao deslogar ele retorna pra tela de login
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profile.this, Login.class);

                startActivity(intent);
                finish();
            }
        });

    }


    //ciclo de vida ao inciar a aplicação
    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    String userName = documentSnapshot.getString("name");
                    textUserName.setText(userName);
                    textProfileUSer.setText("Olá, " + userName);
                    textUserEmail.setText(email);
                }
            }
        });
    }


    private void InitComponentProfile() {
        textUserName = findViewById(R.id.textUserName);
        textProfileUSer = findViewById(R.id.textProfileUSer);
        textUserEmail = findViewById(R.id.textUserEmail);
        btnLogout = findViewById(R.id.btnLogout);
        backImg = findViewById(R.id.backImg);
        imageViewExplorar = findViewById(R.id.imageViewExplorar);
        //NATHAN
        imageViewCarrinho = findViewById(R.id.imageViewCarrinho);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageViewExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        imageViewCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Carrinho.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Profile.this, MainActivity.class));
        finish();
    }
}




