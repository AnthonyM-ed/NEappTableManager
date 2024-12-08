package com.example.neapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account); // Asegúrate de que este sea tu layout de registro

        ImageView buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        // Acción para el botón de Cancelar
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Opcional: finaliza esta actividad si no deseas volver a ella
            }
        });

        // Acción para el botón de Registrarse
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes agregar la lógica para registrar al usuario
                // Después de registrar al usuario, puedes llevarlo al MainActivity
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Opcional: finaliza esta actividad si no deseas volver a ella
            }
        });
    }
}

