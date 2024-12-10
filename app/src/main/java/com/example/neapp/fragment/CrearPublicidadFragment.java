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

public class CrearPublicidadFragment extends Fragment {

    private EditText editTextNombrePublicidad, editTextUbicacionCalle;
    private Spinner spinnerClientes, spinnerZonas;
    private Button buttonRegistrarPublicidad;
    private ImageView buttonCancelarPublicidad;

    private ClienteViewModel clienteViewModel;
    private ZonaViewModel zonaViewModel;

    private List<ClienteEntity> listaClientes = new ArrayList<>();
    private List<ZonaEntity> listaZonas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_publicidad, container, false);

        // Enlazar vistas
        editTextNombrePublicidad = view.findViewById(R.id.editTextNamePub);
        editTextUbicacionCalle = view.findViewById(R.id.editTextUbiCalle);
        spinnerClientes = view.findViewById(R.id.spinnerCliente);
        spinnerZonas = view.findViewById(R.id.spinnerZona);
        buttonRegistrarPublicidad = view.findViewById(R.id.buttonRegisterZona);
        buttonCancelarPublicidad = view.findViewById(R.id.buttonCancelZona);

        // Inicializar ViewModels
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        zonaViewModel = new ViewModelProvider(this).get(ZonaViewModel.class);

        // Configurar spinners
        configurarSpinnerClientes();
        configurarSpinnerZonas();

        // Configurar botones
        buttonCancelarPublicidad.setOnClickListener(v -> requireActivity().finish());
        buttonRegistrarPublicidad.setOnClickListener(v -> agregarPublicidad());

        return view;
    }

    private void configurarSpinnerClientes() {
        clienteViewModel.getActiveClientes().observe(getViewLifecycleOwner(), clientes -> {
            listaClientes = clientes;

            List<String> nombresClientes = new ArrayList<>();
            nombresClientes.add("Seleccionar Cliente"); // Agrega el hint
            for (ClienteEntity cliente : listaClientes) {
                nombresClientes.add(cliente.getCliNom());
            }

            ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresClientes);
            adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClientes.setAdapter(adapterClientes);
            spinnerClientes.setSelection(0);
        });
    }

    private void configurarSpinnerZonas() {
        zonaViewModel.getActiveZonas().observe(getViewLifecycleOwner(), zonas -> {
            listaZonas = zonas;

            List<String> nombresZonas = new ArrayList<>();
            nombresZonas.add("Seleccionar Zona"); // Agrega el hint
            for (ZonaEntity zona : listaZonas) {
                nombresZonas.add(zona.getZonNom());
            }

            ArrayAdapter<String> adapterZonas = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresZonas);
            adapterZonas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerZonas.setAdapter(adapterZonas);
            spinnerZonas.setSelection(0);
        });
    }

    private void agregarPublicidad() {
        String nombrePublicidad = editTextNombrePublicidad.getText().toString().trim();
        String ubicacionCalle = editTextUbicacionCalle.getText().toString().trim();

        if (TextUtils.isEmpty(nombrePublicidad) || TextUtils.isEmpty(ubicacionCalle)) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int clienteSeleccionadoIndex = spinnerClientes.getSelectedItemPosition();
        int zonaSeleccionadaIndex = spinnerZonas.getSelectedItemPosition();

        if (clienteSeleccionadoIndex == 0) { // Cambiado a 0 para el hint
            Toast.makeText(requireContext(), "Por favor, seleccione un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (zonaSeleccionadaIndex == 0) { // Cambiado a 0 para el hint
            Toast.makeText(requireContext(), "Por favor, seleccione una zona válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener códigos de cliente y zona seleccionados
        int clienteCodigo = listaClientes.get(clienteSeleccionadoIndex-1).getCliCod();
        int zonaCodigo = listaZonas.get(zonaSeleccionadaIndex-1).getZonCod();

        // Crear publicidad
        MaestroPublicidadEntity nuevaPublicidad = new MaestroPublicidadEntity();
        nuevaPublicidad.setPubNom(nombrePublicidad);
        nuevaPublicidad.setPubUbi(ubicacionCalle);
        nuevaPublicidad.setCliCod(clienteCodigo);
        nuevaPublicidad.setZonCod(zonaCodigo);
        nuevaPublicidad.setPubEstReg("A");

        // Insertar en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.maestroPublicidadDao().insertMaestroPublicidad(nuevaPublicidad);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Publicidad agregada", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Volver al Activity anterior
            });
        });
    }

    public static CrearPublicidadFragment newInstance() {
        return new CrearPublicidadFragment();
    }
}
