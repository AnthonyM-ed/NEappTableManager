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
import com.example.neapp.model.ent.ClienteEntity;

import java.util.concurrent.Executors;

public class CrearClienteFragment extends Fragment {

    private EditText editTextName;
    private Button buttonRegister;
    private ImageView buttonCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_cliente, container, false);

        // Enlazar vistas
        editTextName = view.findViewById(R.id.editTextName);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // Configurar acciones de los botones
        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        buttonRegister.setOnClickListener(v -> agregarCliente());

        return view;
    }

    private void agregarCliente() {
        String nombre = editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el cliente
        ClienteEntity nuevoCliente = new ClienteEntity();
        nuevoCliente.setCliNom(nombre);
        nuevoCliente.setCliEstReg("A"); // Estado por defecto

        // Insertar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.clienteDao().insertCliente(nuevoCliente);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Cliente agregado", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Volver al Activity anterior
            });
        });
    }

    public static CrearClienteFragment newInstance() {
        return new CrearClienteFragment();
    }
}
