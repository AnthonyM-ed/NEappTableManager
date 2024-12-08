package com.example.neapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Asegúrate de que este sea tu layout de HomeActivity

        Button buttonGoToCliente = findViewById(R.id.buttonGoToCliente);
        Button buttonGoToZonas = findViewById(R.id.buttonGoToZonas);
        Button buttonGoToMaestro = findViewById(R.id.buttonGoToMaestro);
        ImageView menuDisplay = findViewById(R.id.menu_display);
        ImageView accountConfig = findViewById(R.id.account_config);

        // Navegación al hacer clic en el botón de Clientes
        buttonGoToCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        });

        // Navegación al hacer clic en el botón de Zonas
        buttonGoToZonas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ZonaActivity.class);
                startActivity(intent);
            }
        });

        // Navegación al hacer clic en el botón de Maestro de publicidad
        buttonGoToMaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MaestroPublicidadActivity.class);
                startActivity(intent);
            }
        });

        // Opcional: acciones para el menú y configuración de cuenta
        menuDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para abrir el menú
            }
        });

        accountConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para abrir la configuración de cuenta
            }
        });
    }
}
