package com.example.neapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.neapp.R;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ClienteEntity;

import java.util.concurrent.Executors;

public class EditarClienteFragment extends Fragment {

    private static final String ARG_CLI_COD = "cli_cod";
    private static final String ARG_CLI_NOM = "cli_nom";
    private static final String ARG_CLI_EST = "cli_est";

    private TextView textCodCli;
    private TextView textEstado;
    private EditText editTextName;
    private Switch switchEstado;
    private Button buttonGuardar;
    private ImageView buttonCancel;

    public static EditarClienteFragment newInstance(Bundle args) {
        EditarClienteFragment fragment = new EditarClienteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_cliente, container, false);

        // Enlazar vistas
        textCodCli = view.findViewById(R.id.textCodCli);
        editTextName = view.findViewById(R.id.editTextName);
        switchEstado = view.findViewById(R.id.switchEstado);
        textEstado = view.findViewById(R.id.textViewEstado);
        buttonGuardar = view.findViewById(R.id.buttonGuardar);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // Recuperar datos del cliente
        Bundle args = getArguments();
        if (args != null) {
            int cliCod = args.getInt(ARG_CLI_COD, 0); // Código como entero
            String cliNom = args.getString(ARG_CLI_NOM, "");
            String cliEst = args.getString(ARG_CLI_EST, "A");

            // Mostrar los datos en las vistas
            textCodCli.setText("CLI" + cliCod); // Prefijo solo en la vista
            editTextName.setText(cliNom);
            switchEstado.setChecked("A".equals(cliEst));
            actualizarSwitch();
        }

        // Configurar el botón "Cancelar"
        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        // Configurar el botón "Guardar"
        buttonGuardar.setOnClickListener(v -> actualizarCliente());

        // Cambiar dinámicamente el color del switch
        switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarSwitch());

        return view;
    }

    private void actualizarSwitch() {
        if (switchEstado.isChecked()) {
            switchEstado.setTrackTintList(requireContext().getResources().getColorStateList(R.color.l_green));
            textEstado.setText("Activo");
        } else {
            switchEstado.setTrackTintList(requireContext().getResources().getColorStateList(R.color.d_gray));
            textEstado.setText("Inactivo");
        }
    }

    private void actualizarCliente() {
        String nombre = editTextName.getText().toString().trim();
        boolean estadoActivo = switchEstado.isChecked();
        String estado = estadoActivo ? "A" : "I";

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recuperar código sin prefijo
        int cliCod = Integer.parseInt(textCodCli.getText().toString().replace("CLI", ""));

        ClienteEntity clienteActualizado = new ClienteEntity();
        clienteActualizado.setCliCod(cliCod); // Código como entero
        clienteActualizado.setCliNom(nombre);
        clienteActualizado.setCliEstReg(estado);

        // Actualizar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.clienteDao().updateCliente(clienteActualizado);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Volver al Activity anterior
            });
        });
    }
}

