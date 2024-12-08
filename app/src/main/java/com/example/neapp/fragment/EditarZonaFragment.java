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
import com.example.neapp.model.ent.ZonaEntity;

import java.util.concurrent.Executors;

public class EditarZonaFragment extends Fragment {

    private static final String ARG_ZONA_COD = "zona_cod";
    private static final String ARG_ZONA_NOM = "zona_nom";
    private static final String ARG_ZONA_EST = "zona_est";

    private TextView textCodZona;
    private TextView textEstado;
    private EditText editTextNombreZona;
    private Switch switchEstado;
    private Button buttonGuardar;
    private ImageView buttonCancel;

    public static EditarZonaFragment newInstance(Bundle args) {
        EditarZonaFragment fragment = new EditarZonaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_zona, container, false);

        // Enlazar vistas
        textCodZona = view.findViewById(R.id.textCodZona);
        editTextNombreZona = view.findViewById(R.id.editTextNameZona);
        textEstado = view.findViewById(R.id.textViewEstado);
        switchEstado = view.findViewById(R.id.switchEstadoZona);
        buttonGuardar = view.findViewById(R.id.buttonGuardarZona);
        buttonCancel = view.findViewById(R.id.buttonCancela);

        // Recuperar datos de la zona
        Bundle args = getArguments();
        if (args != null) {
            int zonaCod = args.getInt(ARG_ZONA_COD, 0); // Código como entero
            String zonaNom = args.getString(ARG_ZONA_NOM, "");
            String zonaEst = args.getString(ARG_ZONA_EST, "A");

            // Mostrar los datos en las vistas
            textCodZona.setText("ZON" + zonaCod); // Prefijo solo en la vista
            editTextNombreZona.setText(zonaNom);
            switchEstado.setChecked("A".equals(zonaEst));
            actualizarSwitch();
        }

        // Configurar el botón "Cancelar"
        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        // Configurar el botón "Guardar"
        buttonGuardar.setOnClickListener(v -> actualizarZona());

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

    private void actualizarZona() {
        String nombreZona = editTextNombreZona.getText().toString().trim();
        boolean estadoActivo = switchEstado.isChecked();
        String estado = estadoActivo ? "A" : "I";

        if (TextUtils.isEmpty(nombreZona)) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre de zona", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recuperar código sin prefijo
        int zonaCod = Integer.parseInt(textCodZona.getText().toString().replace("ZON", ""));

        ZonaEntity zonaActualizada = new ZonaEntity();
        zonaActualizada.setZonCod(zonaCod); // Código como entero
        zonaActualizada.setZonNom(nombreZona); // Asegúrate de que este método exista en tu entidad
        zonaActualizada.setZonEstReg(estado); // Asegúrate de que este método exista en tu entidad

        // Actualizar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.zonaDao().updateZona(zonaActualizada); // Asegúrate de que el método updateZona exista en tu DAO

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Zona actualizada", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Volver al Activity anterior
            });
        });
    }
}
