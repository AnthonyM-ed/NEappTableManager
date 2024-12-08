package com.example.neapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.neapp.R;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.model.ent.ZonaEntity;
import com.example.neapp.viewmodel.ClienteViewModel;
import com.example.neapp.viewmodel.ZonaViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EditarPublicidadFragment extends Fragment {

    private static final String ARG_PUB_COD = "pub_cod";
    private static final String ARG_PUB_NOM = "pub_nom";
    private static final String ARG_PUB_UBI = "pub_ubi";
    private static final String ARG_PUB_EST = "pub_est";
    private static final String ARG_PUB_CLI = "pub_cli";
    private static final String ARG_PUB_ZONA = "pub_zona";

    private TextView textCodPub;
    private EditText editTextNombrePub, editTextUbiCalle;
    private Spinner spinnerClientes, spinnerZonas;
    private TextView textEstado;
    private Switch switchEstadoPub;
    private Button buttonGuardarPub;
    private ImageView buttonCancel;

    private ClienteViewModel clienteViewModel;
    private ZonaViewModel zonaViewModel;
    private List<ClienteEntity> listaClientes = new ArrayList<>();
    private List<ZonaEntity> listaZonas = new ArrayList<>();

    public static EditarPublicidadFragment newInstance(Bundle args) {
        EditarPublicidadFragment fragment = new EditarPublicidadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_publicidad, container, false);

        // Enlazar vistas
        textCodPub = view.findViewById(R.id.textCodPub);
        editTextNombrePub = view.findViewById(R.id.editTextNamePub);
        editTextUbiCalle = view.findViewById(R.id.editTextUbiCalle);
        spinnerClientes = view.findViewById(R.id.spinnerCliente);
        spinnerZonas = view.findViewById(R.id.spinnerZona);
        switchEstadoPub = view.findViewById(R.id.switchEstadoPub);
        textEstado = view.findViewById(R.id.textViewEstado);
        buttonGuardarPub = view.findViewById(R.id.buttonGuardarPub);
        buttonCancel = view.findViewById(R.id.buttonCancelPub);

        // Inicializar ViewModels
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        zonaViewModel = new ViewModelProvider(this).get(ZonaViewModel.class);

        // Recuperar datos de la publicidad
        Bundle args = getArguments();
        if (args != null) {
            int pubCod = args.getInt(ARG_PUB_COD, 0);
            String pubNom = args.getString(ARG_PUB_NOM, "");
            String pubUbi = args.getString(ARG_PUB_UBI, "");
            String pubEst = args.getString(ARG_PUB_EST, "A");
            int pubCli = args.getInt(ARG_PUB_CLI, 0);
            int pubZona = args.getInt(ARG_PUB_ZONA, 0);

            textCodPub.setText("PUB" + pubCod); // Prefijo solo en la vista
            editTextNombrePub.setText(pubNom);
            editTextUbiCalle.setText(pubUbi);
            switchEstadoPub.setChecked("A".equals(pubEst));
            actualizarSwitch();
            configurarSpinnerClientes(pubCli);
            configurarSpinnerZonas(pubZona);
        }

        // Configurar el botón "Cancelar"
        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        // Configurar el botón "Guardar"
        buttonGuardarPub.setOnClickListener(v -> actualizarPublicidad());

        // Cambiar dinámicamente el color del switch
        switchEstadoPub.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarSwitch());

        return view;
    }

    private void configurarSpinnerClientes(int pubCli) {
        clienteViewModel.getActiveClientes().observe(getViewLifecycleOwner(), clientes -> {
            listaClientes = clientes;

            List<String> nombresClientes = new ArrayList<>();
            for (ClienteEntity cliente : listaClientes) {
                nombresClientes.add(cliente.getCliNom());
            }

            ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresClientes);
            adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClientes.setAdapter(adapterClientes);

            // Seleccionar cliente relacionado
            for (int i = 0; i < listaClientes.size(); i++) {
                if (listaClientes.get(i).getCliCod() == pubCli) {
                    spinnerClientes.setSelection(i);
                    break;
                }
            }
        });
    }

    private void configurarSpinnerZonas(int pubZona) {
        zonaViewModel.getActiveZonas().observe(getViewLifecycleOwner(), zonas -> {
            listaZonas = zonas;

            List<String> nombresZonas = new ArrayList<>();
            for (ZonaEntity zona : listaZonas) {
                nombresZonas.add(zona.getZonNom());
            }

            ArrayAdapter<String> adapterZonas = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresZonas);
            adapterZonas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerZonas.setAdapter(adapterZonas);

            // Seleccionar zona relacionada
            for (int i = 0; i < listaZonas.size(); i++) {
                if (listaZonas.get(i).getZonCod() == pubZona) {
                    spinnerZonas.setSelection(i);
                    break;
                }
            }
        });
    }

    private void actualizarSwitch() {
        if (switchEstadoPub.isChecked()) {
            switchEstadoPub.setTrackTintList(requireContext().getResources().getColorStateList(R.color.l_green));
            textEstado.setText("Activo");
        } else {
            switchEstadoPub.setTrackTintList(requireContext().getResources().getColorStateList(R.color.d_gray));
            textEstado.setText("Inactivo");
        }
    }

    private void actualizarPublicidad() {
        String nombrePub = editTextNombrePub.getText().toString().trim();
        String ubicacionCalle = editTextUbiCalle.getText().toString().trim();
        boolean estadoActivo = switchEstadoPub.isChecked();
        String estado = estadoActivo ? "A" : "I";

        if (TextUtils.isEmpty(nombrePub)) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre de publicidad", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ubicacionCalle)) {
            Toast.makeText(requireContext(), "Por favor, ingrese la ubicación de la calle", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recuperar código sin prefijo
        int pubCod = Integer.parseInt(textCodPub.getText().toString().replace("PUB", ""));

        int clienteSeleccionadoIndex = spinnerClientes.getSelectedItemPosition();
        int zonaSeleccionadaIndex = spinnerZonas.getSelectedItemPosition();

        if (clienteSeleccionadoIndex < 0) {
            Toast.makeText(requireContext(), "Por favor, seleccione un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (zonaSeleccionadaIndex < 0) {
            Toast.makeText(requireContext(), "Por favor, seleccione una zona válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener códigos de cliente y zona seleccionados
        int clienteCodigo = listaClientes.get(clienteSeleccionadoIndex).getCliCod();
        int zonaCodigo = listaZonas.get(zonaSeleccionadaIndex).getZonCod();

        MaestroPublicidadEntity publicidadActualizada = new MaestroPublicidadEntity();
        publicidadActualizada.setPubCod(pubCod);
        publicidadActualizada.setPubNom(nombrePub);
        publicidadActualizada.setCliCod(clienteCodigo);
        publicidadActualizada.setZonCod(zonaCodigo);
        publicidadActualizada.setPubUbi(ubicacionCalle);
        publicidadActualizada.setPubEstReg(estado);

        // Actualizar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.maestroPublicidadDao().updateMaestroPublicidad(publicidadActualizada); // Método en el DAO

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Publicidad actualizada", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            });
        });
    }
}
