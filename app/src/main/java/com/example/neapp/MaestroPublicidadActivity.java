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

import com.example.neapp.adapter.MaestroPublicidadAdapter;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.viewmodel.MaestroPublicidadViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MaestroPublicidadActivity extends AppCompatActivity {
    private ImageView backButton, refreshButton, filterButton;
    private LinearLayout filters;
    private Spinner spinnerCodigo, spinnerNombre, spinnerEstado;
    private FloatingActionButton fabAdd, fabEdit, fabDelete, fabReaddRecord;
    private RecyclerView recyclerView;
    private MaestroPublicidadAdapter adapter;
    private MaestroPublicidadViewModel publicidadViewModel;
    private MaestroPublicidadEntity selectedPublicidad;
    private EditText searchBar;
    private TextView textEliminarFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_publicidad);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchBar = findViewById(R.id.searchBar);
        filters = findViewById(R.id.filters);
        filterButton = findViewById(R.id.filterButton);
        spinnerCodigo = findViewById(R.id.spinnerCodigo);
        spinnerNombre = findViewById(R.id.spinnerNombre);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        recyclerView = findViewById(R.id.recyclerViewPublicidad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAdd = findViewById(R.id.fab_add_record);
        fabEdit = findViewById(R.id.fab_edit_record);
        fabDelete = findViewById(R.id.fab_delete_record);
        fabReaddRecord = findViewById(R.id.fab_readd_record);
        textEliminarFiltros = findViewById(R.id.eliminarFiltrosText);

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
        publicidadViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MaestroPublicidadViewModel(database);
            }
        }).get(MaestroPublicidadViewModel.class);

        // Observa los cambios en la lista de zonas
        publicidadViewModel.getAllPublicidades().observe(this, publicidad -> {
            if (adapter == null) {
                adapter = new MaestroPublicidadAdapter(MaestroPublicidadActivity.this, publicidad);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updatePublicaciones(publicidad); // Actualiza el adaptador si ya está inicializado
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

    private void agregarPublicidad() {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "crear_zona");
        startActivityForResult(intent, 1);
    }

    private void editarPublicidad(MaestroPublicidadEntity publicidad) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "editar_zona");

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
            Toast.makeText(this, "Zona eliminada: " + publicidad.getPubNom(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Zona reactivada: " + publicidad.getPubNom(), Toast.LENGTH_SHORT).show();
            fabReaddRecord.setVisibility(View.GONE); // Ocultar el botón después de la reactivación
            resetFilters();
        }
    }

    private void filterClientes(String query) {
        if (query.isEmpty()) {
            // Si el campo de búsqueda está vacío, mostrar todos los clientes
            publicidadViewModel.getAllPublicidades().observe(this, publicidad -> adapter.updatePublicaciones(publicidad));
            return;
        }

        List<MaestroPublicidadEntity> filteredResults = new ArrayList<>();
        publicidadViewModel.getAllPublicidades().observe(this, allZonas -> {
            if (allZonas != null) {
                for (MaestroPublicidadEntity publicidad : allZonas) {
                    String cliCod = "ZON" + publicidad.getPubCod();
                    if (cliCod.toLowerCase().contains(query.toLowerCase()) ||
                            publicidad.getPubNom().toLowerCase().contains(query.toLowerCase())) {
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
        publicidadViewModel.getDeletedPublicidades().removeObservers(this);

        // Obtenemos la lista completa de clientes (que es un LiveData)
        publicidadViewModel.getAllPublicidades().observe(this, allPublicidad -> {
            List<MaestroPublicidadEntity> filteredPublicidades = new ArrayList<>();

            // Si hay clientes, procedemos a filtrar
            if (allPublicidad != null) {
                filteredPublicidades.addAll(allPublicidad);

                // Filtrar por estado (Activo/Inactivo/Eliminado)
                String estadoFilter = spinnerEstado.getSelectedItem().toString();
                if (estadoFilter.equals("Eliminado")) {
                    publicidadViewModel.getDeletedPublicidades().observe(this, deletedZonas -> {
                        adapter.updatePublicaciones(deletedZonas);
                    });
                    return; // Salimos aquí porque no aplicamos más filtros
                } else if (!estadoFilter.equals("Ninguno")) {
                    String estadoReal = estadoFilter.equals("Activo") ? "A" : "I";
                    filteredPublicidades.removeIf(cliente -> !cliente.getPubEstReg().equalsIgnoreCase(estadoReal));
                }

                // Filtrar por código
                String codigoFilter = spinnerCodigo.getSelectedItem().toString();
                if (codigoFilter.equals("Mayor a menor")) {
                    filteredPublicidades.sort((a, b) -> Integer.compare(b.getZonCod(), a.getZonCod()));
                } else if (codigoFilter.equals("Menor a mayor")) {
                    filteredPublicidades.sort((a, b) -> Integer.compare(a.getZonCod(), b.getZonCod()));
                }

                // Filtrar por nombre
                String nombreFilter = spinnerNombre.getSelectedItem().toString();
                if (nombreFilter.equals("A-Z")) {
                    filteredPublicidades.sort((a, b) -> a.getPubNom().compareToIgnoreCase(b.getPubNom()));
                } else if (nombreFilter.equals("Z-A")) {
                    filteredPublicidades.sort((a, b) -> b.getPubNom().compareToIgnoreCase(a.getPubNom()));
                }

                // Actualiza el adaptador con la lista filtrada
                adapter.updatePublicaciones(filteredPublicidades);
            }
        });
    }


    private void resetFilters() {
        spinnerCodigo.setSelection(0);
        spinnerNombre.setSelection(0);
        spinnerEstado.setSelection(0);
        publicidadViewModel.getAllPublicidades().observe(this, clientes -> adapter.updatePublicaciones(clientes));
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
