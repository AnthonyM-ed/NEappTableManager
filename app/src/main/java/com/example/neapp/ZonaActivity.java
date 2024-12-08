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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neapp.adapter.ZonaAdapter;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ZonaEntity;
import com.example.neapp.viewmodel.ZonaViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ZonaActivity extends AppCompatActivity {
    private ImageView backButton, refreshButton, filterButton;
    private LinearLayout filters;
    private Spinner spinnerCodigo, spinnerNombre, spinnerEstado;
    private FloatingActionButton fabAdd, fabEdit, fabDelete, fabReaddRecord;
    private RecyclerView recyclerView;
    private ZonaAdapter adapter;
    private ZonaViewModel clienteViewModel;
    private ZonaEntity selectedZona;
    private EditText searchBar;
    private TextView textEliminarFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchBar = findViewById(R.id.searchBar);
        filters = findViewById(R.id.filters);
        filterButton = findViewById(R.id.filterButton);
        spinnerCodigo = findViewById(R.id.spinnerCodigo);
        spinnerNombre = findViewById(R.id.spinnerNombre);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        recyclerView = findViewById(R.id.recyclerViewZonas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAdd = findViewById(R.id.fab_add_record);
        fabEdit = findViewById(R.id.fab_edit_record);
        fabDelete = findViewById(R.id.fab_delete_record);
        fabReaddRecord = findViewById(R.id.fab_readd_record);
        textEliminarFiltros = findViewById(R.id.eliminarFiltrosText);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ZonaActivity.this, HomeActivity.class);
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

        AppDatabase database = AppDatabase.getInstance(this);
        clienteViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ZonaViewModel(database);
            }
        }).get(ZonaViewModel.class);

        // Observa los cambios en la lista de zonas
        clienteViewModel.getAllZonas().observe(this, zonas -> {
            if (adapter == null) {
                    adapter = new ZonaAdapter(ZonaActivity.this, zonas);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateZonas(zonas); // Actualiza el adaptador si ya está inicializado
            }
            configurarListeners(); // Configura los listeners después de inicializar el adaptador
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClientes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setupSpinner(spinnerCodigo, R.array.filter_codigo_options, 2); // "Menor a mayor" como default
        setupSpinner(spinnerNombre, R.array.filter_nombre_options, 0); // Ninguno como default
        setupSpinner(spinnerEstado, R.array.filter_estado_options, 0); // Ninguno como default

        spinnerCodigo.setOnItemSelectedListener(createFilterListener(spinnerNombre));
        spinnerNombre.setOnItemSelectedListener(createFilterListener(spinnerCodigo));
        spinnerEstado.setOnItemSelectedListener(createFilterListener(null));
    }

    private void agregarZona() {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "crear_zona");
        startActivityForResult(intent, 1);
    }

    private void editarZona(ZonaEntity zona) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "editar_zona");

        Bundle data = new Bundle();
        data.putInt("zona_cod", zona.getZonCod());
        data.putString("zona_nom", zona.getZonNom());
        data.putString("zona_est", zona.getZonEstReg());
        intent.putExtra(FormularioActivity.EXTRA_DATA, data);
        startActivity(intent);
    }

    private void eliminarZona(ZonaEntity zona) {
        if (zona != null) {
            zona.setZonEstReg("*"); // Marcar como eliminado
            clienteViewModel.updateZona(zona); // Actualiza en la base de datos
            resetFilters();
            Toast.makeText(this, "Zona eliminada: " + zona.getZonNom(), Toast.LENGTH_SHORT).show();
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
        adapter.setOnItemClickListener(zonas -> {
            if (selectedZona != null && selectedZona.equals(zonas)) {
                resetSelection();
            } else {
                selectedZona = zonas;
                if (zonas.getZonEstReg().equals("*")) {
                    fabReaddRecord.setVisibility(View.VISIBLE);
                    fabEdit.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                } else {
                    fabReaddRecord.setVisibility(View.GONE);
                    fabEdit.setVisibility(View.VISIBLE);
                    fabDelete.setVisibility(View.VISIBLE);
                }
                adapter.setSelectedZona(selectedZona);
                adapter.notifyDataSetChanged();

                // Configurar listeners para los botones
                fabEdit.setOnClickListener(v -> { editarZona(selectedZona); resetSelection(); });
                fabDelete.setOnClickListener(v -> { eliminarZona(selectedZona); resetSelection(); });
                fabReaddRecord.setOnClickListener(v -> { reactivarZona(selectedZona); resetSelection(); });
            }
        });

        fabAdd.setOnClickListener(v -> { agregarZona(); resetSelection(); });
    }

    private void reactivarZona(ZonaEntity zona) {
        if (zona != null) {
            zona.setZonEstReg("A"); // Cambia "*" a "A" (activo)
            clienteViewModel.updateZona(zona);
            Toast.makeText(this, "Zona reactivada: " + zona.getZonNom(), Toast.LENGTH_SHORT).show();
            fabReaddRecord.setVisibility(View.GONE); // Ocultar el botón después de la reactivación
            resetFilters();
        }
    }

    private void filterClientes(String query) {
        if (query.isEmpty()) {
            // Si el campo de búsqueda está vacío, mostrar todos los clientes
            clienteViewModel.getAllZonas().observe(this, clientes -> adapter.updateZonas(clientes));
            return;
        }

        List<ZonaEntity> filteredResults = new ArrayList<>();
        clienteViewModel.getAllZonas().observe(this, allZonas -> {
            if (allZonas != null) {
                for (ZonaEntity zona : allZonas) {
                    String cliCod = "ZON" + zona.getZonCod();
                    if (cliCod.toLowerCase().contains(query.toLowerCase()) ||
                            zona.getZonNom().toLowerCase().contains(query.toLowerCase())) {
                        filteredResults.add(zona);
                    }
                }
                adapter.updateZonas(filteredResults);
            }
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResourceId, int defaultSelection) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultSelection);
    }

    private AdapterView.OnItemSelectedListener createFilterListener(Spinner otherSpinner) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
                if (otherSpinner != null) {
                    otherSpinner.setSelection(0); // Resetear el otro spinner si se aplica un filtro
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private void applyFilters() {
        // Eliminar observadores anteriores de clientes eliminados
        clienteViewModel.getDeletedZonas().removeObservers(this);

        // Obtenemos la lista completa de clientes (que es un LiveData)
        clienteViewModel.getAllZonas().observe(this, allZonas -> {
            List<ZonaEntity> filteredZonas = new ArrayList<>();

            // Si hay clientes, procedemos a filtrar
            if (allZonas != null) {
                filteredZonas.addAll(allZonas);

                // Filtrar por estado (Activo/Inactivo/Eliminado)
                String estadoFilter = spinnerEstado.getSelectedItem().toString();
                if (estadoFilter.equals("Eliminado")) {
                    clienteViewModel.getDeletedZonas().observe(this, deletedZonas -> {
                        adapter.updateZonas(deletedZonas);
                    });
                    return; // Salimos aquí porque no aplicamos más filtros
                } else if (!estadoFilter.equals("Ninguno")) {
                    String estadoReal = estadoFilter.equals("Activo") ? "A" : "I";
                    filteredZonas.removeIf(cliente -> !cliente.getZonEstReg().equalsIgnoreCase(estadoReal));
                }

                // Filtrar por código
                String codigoFilter = spinnerCodigo.getSelectedItem().toString();
                if (codigoFilter.equals("Mayor a menor")) {
                    filteredZonas.sort((a, b) -> Integer.compare(b.getZonCod(), a.getZonCod()));
                } else if (codigoFilter.equals("Menor a mayor")) {
                    filteredZonas.sort((a, b) -> Integer.compare(a.getZonCod(), b.getZonCod()));
                }

                // Filtrar por nombre
                String nombreFilter = spinnerNombre.getSelectedItem().toString();
                if (nombreFilter.equals("A-Z")) {
                    filteredZonas.sort((a, b) -> a.getZonNom().compareToIgnoreCase(b.getZonNom()));
                } else if (nombreFilter.equals("Z-A")) {
                    filteredZonas.sort((a, b) -> b.getZonNom().compareToIgnoreCase(a.getZonNom()));
                }

                // Actualiza el adaptador con la lista filtrada
                adapter.updateZonas(filteredZonas);
            }
        });
    }


    private void resetFilters() {
        spinnerCodigo.setSelection(0);
        spinnerNombre.setSelection(0);
        spinnerEstado.setSelection(0);
        clienteViewModel.getAllZonas().observe(this, clientes -> adapter.updateZonas(clientes));
    }

    private void resetSelection() {
        selectedZona = null;
        adapter.setSelectedZona(null);
        fabEdit.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);
        fabReaddRecord.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
