package com.cinepass.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cinepass.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextView textSignUp;
    private EditText editEmailLogin, editPasswordLogin;
    private Button signInButton;

    String[] messages = {"Preencha todos os campos",
            "Login efetuado com sucesso!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initComponentLogin();


        textSignUp = findViewById(R.id.textSignUp);


        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = editEmailLogin.getText().toString();
                String pass = editPasswordLogin.getText().toString();

                if (email.isEmpty() || pass.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, messages[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    AuthUser(v);
                }

            }
        });
    }

    private void AuthUser(View view){

        String email = editEmailLogin.getText().toString();
        String pass = editPasswordLogin.getText().toString();


        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity();
                        }
                    },0);
                }else{
                    String error;

                    try{
                        throw task.getException();

                    }catch(Exception e){
                        error = "Erro ao realizar login";


                    }
                    Snackbar snackbar = Snackbar.make(view,error, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }

            }
        });

    }

    //mudar a rota pra homepage depois
  private void MainActivity(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();

  }

    private void initComponentLogin() {
        editEmailLogin = findViewById(R.id.editEmailLogin);
        editPasswordLogin = findViewById(R.id.editPasswordLogin);
        signInButton = findViewById(R.id.signInButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
    }
}