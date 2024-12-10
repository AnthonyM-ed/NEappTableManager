package com.example.neapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button buttonGoToCliente = findViewById(R.id.buttonGoToCliente);
        Button buttonGoToZonas = findViewById(R.id.buttonGoToZonas);
        Button buttonGoToMaestro = findViewById(R.id.buttonGoToMaestro);
        ImageView accountConfig = findViewById(R.id.account_config);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        usuarioId = sharedPreferences.getInt("USER_ID", -1);
        if (usuarioId == -1) {
            // Manejar error si el ID no fue pasado correctamente
            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Navegación al hacer clic en el botón de Clientes
        buttonGoToCliente.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClienteActivity.class);
            startActivity(intent);
        });

        // Navegación al hacer clic en el botón de Zonas
        buttonGoToZonas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ZonaActivity.class);
            startActivity(intent);
        });

        // Navegación al hacer clic en el botón de Maestro de publicidad
        buttonGoToMaestro.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MaestroPublicidadActivity.class);
            startActivity(intent);
        });

        accountConfig.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, accountConfig);
            popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_configure_account) {
                    Intent configureIntent = new Intent(HomeActivity.this, AccountActivity.class);
                    configureIntent.putExtra("ACTION", "EDIT");
                    configureIntent.putExtra("USER_ID", usuarioId); // Asegúrate de tener el ID del usuario
                    startActivity(configureIntent);
                    return true;
                } else{
                    return false;
                }
            });
            popupMenu.show();
        });
    }
}
