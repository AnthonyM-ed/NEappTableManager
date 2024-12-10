package com.example.neapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.neapp.fragment.CreateAccountFragment;
import com.example.neapp.fragment.EditAccountFragment;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account); // Este layout debe tener un FrameLayout o FragmentContainerView

        String action = getIntent().getStringExtra("ACTION");
        int userId = getIntent().getIntExtra("USER_ID", -1); // Obtén el ID del usuario

        if ("EDIT".equals(action)) {
            // Cargar EditAccountFragment con el ID del usuario
            EditAccountFragment fragment = new EditAccountFragment();
            Bundle args = new Bundle();
            args.putInt("userId", userId);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else if ("CREATE".equals(action)) {
            // Cargar CreateAccountFragment
            CreateAccountFragment fragment = new CreateAccountFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Cambia fragment_container por el ID de tu contenedor
                .commit();
    }
}
