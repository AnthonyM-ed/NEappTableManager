package com.example.neapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neapp.adapter.MaestroPublicidadAdapter;
import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.model.ent.ZonaEntity;
import com.example.neapp.viewmodel.ClienteViewModel;
import com.example.neapp.viewmodel.MaestroPublicidadViewModel;
import com.example.neapp.viewmodel.ZonaViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaestroPublicidadActivity extends AppCompatActivity {
    private ImageView backButton, refreshButton, filterButton;
    private LinearLayout filters;
    private Spinner spinnerCodigo, spinnerNombre, spinnerEstado, spinnerNombreCliente, spinnerNombreZona, spinnerUbicacion;
    private FloatingActionButton fabAdd, fabEdit, fabDelete, fabReaddRecord;
    private RecyclerView recyclerView;
    private MaestroPublicidadAdapter adapter;
    private MaestroPublicidadViewModel publicidadViewModel;
    private MaestroPublicidadEntity selectedPublicidad;
    private EditText searchBar;
    private TextView textEliminarFiltros;
    private List<Spinner> filterSpinners;
    private ClienteViewModel clienteViewModel;
    private ZonaViewModel zonaViewModel;
    private Map<Integer, String> clienteMap ;
    private Map<Integer, String> zonaMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_publicidad);

        // Inicializa los ViewModels
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        zonaViewModel = new ViewModelProvider(this).get(ZonaViewModel.class);
        publicidadViewModel = new ViewModelProvider(this).get(MaestroPublicidadViewModel.class);

        adapter = new MaestroPublicidadAdapter(this, new ArrayList<>(), clienteViewModel, zonaViewModel, publicidadViewModel);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchBar = findViewById(R.id.searchBar);
        filters = findViewById(R.id.filters);
        filterButton = findViewById(R.id.filterButton);
        spinnerCodigo = findViewById(R.id.spinnerCodigo);
        spinnerNombre = findViewById(R.id.spinnerNombre);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerNombreCliente = findViewById(R.id.spinnerCliente);
        spinnerNombreZona = findViewById(R.id.spinnerZona);
        spinnerUbicacion = findViewById(R.id.spinnerUbicacion);
        recyclerView = findViewById(R.id.recyclerViewPublicidad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAdd = findViewById(R.id.fab_add_record);
        fabEdit = findViewById(R.id.fab_edit_record);
        fabDelete = findViewById(R.id.fab_delete_record);
        fabReaddRecord = findViewById(R.id.fab_readd_record);
        textEliminarFiltros = findViewById(R.id.eliminarFiltrosText);

        filterSpinners = new ArrayList<>();
        filterSpinners.add(spinnerCodigo);
        filterSpinners.add(spinnerNombre);
        filterSpinners.add(spinnerNombreCliente);
        filterSpinners.add(spinnerNombreZona);
        filterSpinners.add(spinnerUbicacion);

        recyclerView.setAdapter(adapter);
        // Mapas para almacenar los nombres de los clientes y zonas
        clienteMap = new HashMap<>();
        zonaMap = new HashMap<>();

        publicidadViewModel.getAllPublicidades().observe(this, publicidad -> {
            if (adapter == null) {
                setupAdapter(); // Asegúrate de configurar el adaptador si es null
            }
            adapter.updatePublicaciones(publicidad);
        });

        // Observa los cambios en la lista de clientes
        clienteViewModel.getAllClientes().observe(this, clientes -> {
            clienteMap.clear();
            for (ClienteEntity cliente : clientes) {
                clienteMap.put(cliente.getCliCod(), cliente.getCliNom());
            }
            adapter.setClienteMap(clienteMap); // Actualiza el mapa en el adaptador
            adapter.updateMaps(clienteMap, zonaMap);
        });

        // Observa los cambios en la lista de zonas
        zonaViewModel.getAllZonas().observe(this, zonas -> {
            // Actualiza el mapa de nombres de clientes
            zonaMap.clear();
            for (ZonaEntity zona : zonas) {
                zonaMap.put(zona.getZonCod(), zona.getZonNom());
            }
            adapter.setZonaMap(zonaMap); // Notifica al adapter sobre los cambios
            adapter.updateMaps(clienteMap, zonaMap);
        });

        loadClientAndZoneNames();
        configurarListeners();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MaestroPublicidadActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        refreshButton.setOnClickListener(v -> {
            resetSelection();
            resetFilters();
            filters.setVisibility(View.GONE);
            textEliminarFiltros.setVisibility(View.GONE);
            filterButton.setImageResource(R.drawable.ic_filter_black);
            filterButton.setTag("filter");
            searchBar.setText("");
        });

        searchBar.setOnClickListener(v -> resetSelection());
        searchBar.setOnFocusChangeListener((v, hasFocus) -> resetSelection());

        filterButton.setOnClickListener(v -> {
            if (filterButton.getTag() == null || filterButton.getTag().equals("filter")) {
                filters.setVisibility(View.VISIBLE);
                textEliminarFiltros.setVisibility(View.VISIBLE);
                filterButton.setImageResource(R.drawable.ic_clear_filter2);
                filterButton.setTag("clear");
            } else {
                filters.setVisibility(View.GONE);
                textEliminarFiltros.setVisibility(View.GONE);
                resetFilters();
                filterButton.setImageResource(R.drawable.ic_filter_black);
                filterButton.setTag("filter");
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPublicidad(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setupSpinner(spinnerCodigo, R.array.filter_codigo_options, 2); // Ninguno como default
        setupSpinner(spinnerNombre, R.array.filter_nombre_publicidad_options, 0); // Ninguno como default
        setupSpinner(spinnerNombreCliente, R.array.filter_nombre_cliente_options, 0); // Ninguno como default
        setupSpinner(spinnerNombreZona, R.array.filter_nombre_zona_options, 0); // Ninguno como default
        setupSpinner(spinnerUbicacion, R.array.filter_ubicacion_options, 0); // Ninguno como default
        setupSpinner(spinnerEstado, R.array.filter_estado_options, 0); // Ninguno como default // Ninguno como default

        spinnerCodigo.setOnItemSelectedListener(createFilterListener(filterSpinners));
        spinnerNombre.setOnItemSelectedListener(createFilterListener(filterSpinners));
        spinnerNombreCliente.setOnItemSelectedListener(createFilterListener(filterSpinners));
        spinnerNombreZona.setOnItemSelectedListener(createFilterListener(filterSpinners));
        spinnerUbicacion.setOnItemSelectedListener(createFilterListener(filterSpinners));
        spinnerEstado.setOnItemSelectedListener(createFilterListener(null)); // El estado puede seguir activo
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter = null; // Limpia la referencia del adaptador si es necesario
    }

    // Pre-cargar los nombres de los clientes y zonas al iniciar la actividad
    private void loadClientAndZoneNames() {
        clienteViewModel.getAllClientes().observe(this, clientes -> {
            clienteMap.clear();
            for (ClienteEntity cliente : clientes) {
                clienteMap.put(cliente.getCliCod(), cliente.getCliNom());
            }
        });

        zonaViewModel.getAllZonas().observe(this, zonas -> {
            zonaMap.clear();
            for (ZonaEntity zona : zonas) {
                zonaMap.put(zona.getZonCod(), zona.getZonNom());
            }
        });
    }

    private void setupAdapter() {
        // Verifica si el adaptador ya ha sido inicializado
        if (adapter == null) {
            adapter = new MaestroPublicidadAdapter(
                    this,
                    new ArrayList<>(),
                    clienteViewModel,
                    zonaViewModel,
                    publicidadViewModel // Pasa el ViewModel al adaptador
            );

            // Establecer los mapas en el adaptador
            adapter.setClienteMap(clienteMap);
            adapter.setZonaMap(zonaMap);

            recyclerView.setAdapter(adapter);
        }
    }


    private void agregarPublicidad() {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "crear_publicidad");
        startActivityForResult(intent, 1);
    }

    private void editarPublicidad(MaestroPublicidadEntity publicidad) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "editar_publicidad");

        Bundle data = new Bundle();
        data.putInt("pub_cod", publicidad.getPubCod());
        data.putString("pub_nom", publicidad.getPubNom());
        data.putInt("pub_cli", publicidad.getCliCod());
        data.putInt("pub_zona", publicidad.getZonCod());
        data.putString("pub_ubi", publicidad.getPubUbi());
        data.putString("pub_est", publicidad.getPubEstReg());
        intent.putExtra(FormularioActivity.EXTRA_DATA, data);
        startActivity(intent);
    }

    private void eliminarPublicidad(MaestroPublicidadEntity publicidad) {
        if (publicidad != null) {
            publicidad.setPubEstReg("*"); // Marcar como eliminado
            publicidadViewModel.updatePublicidad(publicidad); // Actualiza en la base de datos
            resetFilters();
            Toast.makeText(this, "Publicidad eliminada: " + publicidad.getPubNom(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // No es necesario volver a observar, el LiveData ya está observando cambios
            resetSelection(); // Resetear la selección después de agregar o editar
        }
    }

    private void configurarListeners() {
        adapter.setOnItemClickListener(publicidad -> {
            if (selectedPublicidad != null && selectedPublicidad.equals(publicidad)) {
                resetSelection();
            } else {
                selectedPublicidad = publicidad;
                if (publicidad.getPubEstReg().equals("*")) {
                    fabReaddRecord.setVisibility(View.VISIBLE);
                    fabEdit.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                } else {
                    fabReaddRecord.setVisibility(View.GONE);
                    fabEdit.setVisibility(View.VISIBLE);
                    fabDelete.setVisibility(View.VISIBLE);
                }
                adapter.setSelectedPublicidad(selectedPublicidad);
                adapter.notifyDataSetChanged();

                // Configurar listeners para los botones
                fabEdit.setOnClickListener(v -> { editarPublicidad(selectedPublicidad); resetSelection(); });
                fabDelete.setOnClickListener(v -> { eliminarPublicidad(selectedPublicidad); resetSelection(); });
                fabReaddRecord.setOnClickListener(v -> { reactivarPublicidad(selectedPublicidad); resetSelection(); });
            }
        });

        fabAdd.setOnClickListener(v -> { agregarPublicidad(); resetSelection(); });
    }

    private void reactivarPublicidad(MaestroPublicidadEntity publicidad) {
        if (publicidad != null) {
            publicidad.setPubEstReg("A"); // Cambia "*" a "A" (activo)
            publicidadViewModel.updatePublicidad(publicidad);
            Toast.makeText(this, "Publicidad reactivada: " + publicidad.getPubNom(), Toast.LENGTH_SHORT).show();
            fabReaddRecord.setVisibility(View.GONE); // Ocultar el botón después de la reactivación
            resetFilters();
        }
    }

    private void filterPublicidad(String query) {
        if (query.isEmpty()) {
            // Si el campo de búsqueda está vacío, mostrar todas las publicidades
            publicidadViewModel.getAllPublicidades().observe(this, publicidad -> adapter.updatePublicaciones(publicidad));
            return;
        }

        List<MaestroPublicidadEntity> filteredResults = new ArrayList<>();
        publicidadViewModel.getAllPublicidades().observe(this, allPublicidades -> {
            if (allPublicidades != null) {
                for (MaestroPublicidadEntity publicidad : allPublicidades) {
                    String cliCod = "PUB" + publicidad.getPubCod();

                    // Comprobar coincidencias en el código de la publicidad
                    boolean matchesCodigo = cliCod.toLowerCase().contains(query.toLowerCase());

                    // Comprobar coincidencias en el nombre de la publicidad
                    boolean matchesPublicidad = publicidad.getPubNom().toLowerCase().contains(query.toLowerCase());

                    // Obtener el nombre del cliente
                    String nombreCliente = clienteMap.get(publicidad.getCliCod());
                    boolean matchesCliente = nombreCliente != null && nombreCliente.toLowerCase().contains(query.toLowerCase());

                    // Obtener el nombre de la zona
                    String nombreZona = zonaMap.get(publicidad.getZonCod());
                    boolean matchesZona = nombreZona != null && nombreZona.toLowerCase().contains(query.toLowerCase());

                    // Comprobar coincidencias en la ubicación
                    String ubicacion = publicidad.getPubUbi(); // Asegúrate de que este método existe
                    boolean matchesUbicacion = ubicacion != null && ubicacion.toLowerCase().contains(query.toLowerCase());

                    // Añadir a los resultados si hay alguna coincidencia
                    if (matchesCodigo || matchesPublicidad || matchesCliente || matchesZona || matchesUbicacion) {
                        filteredResults.add(publicidad);
                    }
                }
                adapter.updatePublicaciones(filteredResults);
            }
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResourceId, int defaultSelection) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultSelection);
    }

    private AdapterView.OnItemSelectedListener createFilterListener(List<Spinner> spinners) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Aplicar filtros aquí
                applyFilters();

                // Solo resetear si la opción seleccionada no es "Ninguno"
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.equals("Ninguno")) {
                    if (spinners != null) {  // Verifica que spinners no sea null
                        for (Spinner spinner : spinners) {
                            if (spinner != parent) {  // Asegúrate de no deseleccionar el spinner actual
                                spinner.setSelection(0); // Resetear cada spinner en la lista
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Aquí podrías hacer algo si no hay nada seleccionado, si es necesario
            }
        };
    }

    private void applyFilters() {
        publicidadViewModel.getAllPublicidades().observe(this, allPublicidades -> {
            if (allPublicidades == null) {
                adapter.updatePublicaciones(new ArrayList<>()); // Actualiza con lista vacía
                return; // Salimos aquí si no hay publicidades
            }

            List<MaestroPublicidadEntity> filteredPublicidades = new ArrayList<>(allPublicidades);

            // Filtrar por estado
            String estadoFilter = spinnerEstado.getSelectedItem() != null ? spinnerEstado.getSelectedItem().toString() : null;
            if ("Eliminado".equals(estadoFilter)) {
                publicidadViewModel.getDeletedPublicidades().observe(this, deletedPublicidades -> {
                    adapter.updatePublicaciones(deletedPublicidades);
                });
                return; // Salimos aquí porque no aplicamos más filtros
            } else if (estadoFilter != null && !estadoFilter.equals("Ninguno")) {
                String estadoReal = "Activo".equals(estadoFilter) ? "A" : "I"; // A y I representan los estados
                filteredPublicidades.removeIf(publicidad -> !publicidad.getPubEstReg().equalsIgnoreCase(estadoReal));
            }

            // Filtrar por código
            String codigoFilter = spinnerCodigo.getSelectedItem().toString();
            if (!codigoFilter.equals("Ninguno")) {
                if (codigoFilter.equals("Mayor a menor")) {
                    filteredPublicidades.sort((a, b) -> Integer.compare(b.getPubCod(), a.getPubCod()));
                } else if (codigoFilter.equals("Menor a mayor")) {
                    filteredPublicidades.sort((a, b) -> Integer.compare(a.getPubCod(), b.getPubCod()));
                }
            }

            // Filtrar por nombre de publicidad
            String nombrePublicidadFilter = spinnerNombre.getSelectedItem() != null ? spinnerNombre.getSelectedItem().toString() : null;
            if (nombrePublicidadFilter != null && !nombrePublicidadFilter.equals("Ninguno")) {
                if ("A-Z".equals(nombrePublicidadFilter)) {
                    filteredPublicidades.sort((a, b) -> a.getPubNom().compareToIgnoreCase(b.getPubNom()));
                } else if ("Z-A".equals(nombrePublicidadFilter)) {
                    filteredPublicidades.sort((a, b) -> b.getPubNom().compareToIgnoreCase(a.getPubNom()));
                }
            }

            String nombreClienteFilter = spinnerNombreCliente.getSelectedItem() != null ? spinnerNombreCliente.getSelectedItem().toString() : null;
            if (nombreClienteFilter != null && !nombreClienteFilter.equals("Ninguno")) {
                filteredPublicidades.sort((a, b) -> {
                    String nombreA = clienteMap.get(a.getCliCod());
                    String nombreB = clienteMap.get(b.getCliCod());

                    if (nombreA == null) return 1;
                    if (nombreB == null) return -1;

                    return "A-Z".equals(nombreClienteFilter) ?
                            nombreA.compareToIgnoreCase(nombreB) :
                            nombreB.compareToIgnoreCase(nombreA);
                });
            }

            String nombreZonaFilter = spinnerNombreZona.getSelectedItem() != null ? spinnerNombreZona.getSelectedItem().toString() : null;
            if (nombreZonaFilter != null && !nombreZonaFilter.equals("Ninguno")) {
                filteredPublicidades.sort((a, b) -> {
                    String nombreZonaA = zonaMap.get(a.getZonCod());
                    String nombreZonaB = zonaMap.get(b.getZonCod());

                    if (nombreZonaA == null) return 1;
                    if (nombreZonaB == null) return -1;

                    return "A-Z".equals(nombreZonaFilter) ?
                            nombreZonaA.compareToIgnoreCase(nombreZonaB) :
                            nombreZonaB.compareToIgnoreCase(nombreZonaA);
                });
            }

            // Filtrar por ubicación
            String ubicacionFilter = spinnerUbicacion.getSelectedItem() != null ? spinnerUbicacion.getSelectedItem().toString() : null;
            if (ubicacionFilter != null && !ubicacionFilter.equals("Ninguno")) {
                if ("A-Z".equals(ubicacionFilter)) {
                    filteredPublicidades.sort((a, b) -> a.getPubUbi().compareToIgnoreCase(b.getPubUbi()));
                } else if ("Z-A".equals(ubicacionFilter)) {
                    filteredPublicidades.sort((a, b) -> b.getPubUbi().compareToIgnoreCase(a.getPubUbi()));
                }
            }

            // Actualiza el adaptador con la lista filtrada
            adapter.updatePublicaciones(filteredPublicidades);
        });
    }

    private void resetFilters() {
        for (Spinner spinner : filterSpinners) {
            spinner.setSelection(0); // Establecer en "Ninguno"
        }
        publicidadViewModel.getAllPublicidades().observe(this, publicidades -> adapter.updatePublicaciones(publicidades));
    }

    private void resetSelection() {
        selectedPublicidad = null;
        adapter.setSelectedPublicidad(null);
        fabEdit.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);
        fabReaddRecord.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
