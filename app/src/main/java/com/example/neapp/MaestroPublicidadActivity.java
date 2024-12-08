package com.example.neapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MaestroPublicidadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_publicidad); // Asegúrate de que este sea tu layout de MaestroPublicidadActivity

        ImageView backButton = findViewById(R.id.backButton);

        // Navegación al hacer clic en el botón de retroceso
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaestroPublicidadActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Opcional: llama a finish() si quieres que esta actividad se cierre al regresar
            }
        });
    }
}
