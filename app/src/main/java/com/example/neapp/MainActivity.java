package com.example.neapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.neapp.viewmodel.UsuarioViewModel;

public class MainActivity extends AppCompatActivity {

    private UsuarioViewModel usuarioViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar ViewModel
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);

        // Referencias a vistas
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegist = findViewById(R.id.buttonRegist);

        // Acción para el botón de Ingresar
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validar entrada
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa un correo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar credenciales
                usuarioViewModel.verificarCredenciales(email, password).observe(MainActivity.this, usuario -> {
                    if (usuario != null) {
                        // Credenciales válidas, redirigir a HomeActivity
                        Toast.makeText(MainActivity.this, "Bienvenido, " + usuario.getUsuNombre(), Toast.LENGTH_SHORT).show();

                        // Almacenar el ID del usuario en SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("USER_ID", usuario.getUsuCod());
                        editor.apply();

                        // Iniciar HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Cierra esta actividad para evitar regresar al login
                    } else {
                        // Credenciales inválidas
                        Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Acción para el botón de Regístrate
        buttonRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("ACTION", "CREATE"); // Agregar un extra para indicar que es para crear
                startActivity(intent);
            }
        });
    }
}
