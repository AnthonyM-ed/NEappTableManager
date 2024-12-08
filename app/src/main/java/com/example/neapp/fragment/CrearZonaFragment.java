package com.example.neapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.neapp.R;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.concurrent.Executors;

public class CrearZonaFragment extends Fragment {

    private EditText editTextNombreZona;
    private Button buttonRegistrarZona;
    private ImageView buttonCancelarZona;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_zona, container, false);

        // Enlazar vistas
        editTextNombreZona = view.findViewById(R.id.editTextNameZona);
        buttonRegistrarZona = view.findViewById(R.id.buttonRegisterZona);
        buttonCancelarZona = view.findViewById(R.id.buttonCancelZona);

        // Configurar acciones de los botones
        buttonCancelarZona.setOnClickListener(v -> requireActivity().finish());

        buttonRegistrarZona.setOnClickListener(v -> agregarZona());

        return view;
    }

    private void agregarZona() {
        String nombreZona = editTextNombreZona.getText().toString().trim();

        if (TextUtils.isEmpty(nombreZona)) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre de zona", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear la zona
        ZonaEntity nuevaZona = new ZonaEntity();
        nuevaZona.setZonNom(nombreZona); // Asegúrate de que este método exista en tu entidad
        nuevaZona.setZonEstReg("A");

        // Insertar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.zonaDao().insertZona(nuevaZona); // Asegúrate de que el método insertZona exista en tu DAO

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Zona agregada", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Volver al Activity anterior
            });
        });
    }

    public static CrearZonaFragment newInstance() {
        return new CrearZonaFragment();
    }
}
