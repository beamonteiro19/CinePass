package com.cinepass.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cinepass.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {


    private TextView textSignIn;
    private EditText editUsername, editEmail, editPassword;
    private Button signUpButton;

    String[] messages = {"Preencha todos os campos",
            "Cadastro realizado com sucesso!"};

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        initComponent();


        textSignIn = findViewById(R.id.textSignIn);

        textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = editUsername.getText().toString();
                String email = editEmail.getText().toString();
                String pass = editPassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, messages[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {

                    SignUpUser(v);

                }
            }
        });


    }

    private void SignUpUser(View v) {
        String email = editEmail.getText().toString();
        String pass = editPassword.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //salvar os dados do user no banco
                    SaveDataUser();

                    Snackbar snackbar = Snackbar.make(v, messages[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    // Navegar para MainActivity
                    startActivity(new Intent(SignUp.this, MainActivity.class));
                    finish();

                } else {
                    String error;
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Digite uma senha com no mínimo 6 caracteres";

                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Este e-mail já foi cadastrado";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "E-mail ou senha inválido";

                    } catch (Exception e) {
                        error = "Erro ao cadastrar usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, error, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }


    //salvar os dados do user no banco
    private void SaveDataUser() {
        String name = editUsername.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> users = new HashMap<>();
        users.put("name", name);


        // Obter o usuário atual e pegar o id dele
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("db", "Sucesso ao salvar os dados");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("db_error", "Erro ao salvar os dados" + e.toString());

                    }
                });


    }

    private void initComponent() {
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        signUpButton = findViewById(R.id.signUpButton);
    }

}