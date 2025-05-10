package com.cinepass.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cinepass.Domain.FilmItem;
import com.cinepass.Domain.Ticket;
import com.cinepass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRcode extends AppCompatActivity {

    ImageView backImg, imgvQr;
    TextView txtTicketTitle, txtFilmeNome, txtDataFilme, txtHoraFilme, txtAssento, txtUserName;

    private FirebaseFirestore db;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrcode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Verificação de segurança e inicialização do firestore
        db = FirebaseFirestore.getInstance();
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if(currentUser == null){
            Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(this, Intro.class));
        }

        // Inicializa componentes
        backImg = findViewById(R.id.backImg);
        imgvQr = findViewById(R.id.imgvQr);
        txtTicketTitle = findViewById(R.id.txtTicketTitle);
        txtFilmeNome = findViewById(R.id.txtFilmeNome);
        txtDataFilme = findViewById(R.id.txtDataFilme);
        txtHoraFilme = findViewById(R.id.txtHoraFilme);
        txtAssento = findViewById(R.id.txtAssento);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRcode.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Buscar ticket
        String ticketId = getIntent().getStringExtra("ticketId");
        if (ticketId != null) {
            fetchTicketData(ticketId);
        } else {
            Toast.makeText(this, "ID do ticket não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    //Pega os dados do banco de dados e converte para o objeto Ingresso
    private void fetchTicketData(String ticketId) {
        DocumentReference ticketRef = db.collection("Tickets").document(ticketId);
        ticketRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Ticket ingresso = documentSnapshot.toObject(Ticket.class);
                if (ingresso != null) {
                    ingresso.setId(ticketId);
                    displayTicketData(ingresso);
                }
            } else {
                Toast.makeText(QRcode.this, "Ticket não encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(QRcode.this, "Erro ao buscar dados do ticket", Toast.LENGTH_SHORT).show();
        });


    }

    //Pega as informações do objeto Ingresso e dispões elas na tela
    private void displayTicketData(Ticket ingresso) {
        RequestQueue mrequestQueue = Volley.newRequestQueue(this);
        int idFilme = ingresso.getIdFilme();
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilme, response -> {
            Gson gson = new Gson();
            FilmItem item = gson.fromJson(response, FilmItem.class);
            txtTicketTitle.setText(item.getTitle());
            txtFilmeNome.setText("Filme: " + item.getTitle());
            txtDataFilme.setText("Dia: " + ingresso.getData());
            txtHoraFilme.setText("Hora: " + ingresso.getHorario());
            txtAssento.setText("Assento: " + ingresso.getAssento());

        }, error -> {
            Log.i("Cinepass", "onErrorResponse: " + error.toString());
        });
        mrequestQueue.add(mStringRequest);
        generateAndDisplayQRCode(ingresso);
    }

    //Converte o objeto Ingresso em json e chama a função para gerar o QRCode
    private void generateAndDisplayQRCode(Ticket ingresso) {
        String ingressoJson = gson.toJson(ingresso);
        Log.d("QRCode", "Generated JSON: " + ingressoJson); // Log do JSON gerado
        Bitmap bitmap = generateQRCode(ingressoJson);
        if (bitmap != null) {
            imgvQr.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }


    //Código que gera o QRCode
    private Bitmap generateQRCode(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}