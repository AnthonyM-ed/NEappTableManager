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

import com.example.neapp.adapter.ClienteAdapter;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.viewmodel.ClienteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    private ImageView backButton, refreshButton, filterButton;
    private LinearLayout filters;
    private Spinner spinnerCodigo, spinnerNombre, spinnerEstado;
    private FloatingActionButton fabAdd, fabEdit, fabDelete, fabReaddRecord;
    private RecyclerView recyclerView;
    private ClienteAdapter adapter;
    private ClienteViewModel clienteViewModel;
    private ClienteEntity selectedCliente;
    private EditText searchBar;
    private TextView textEliminarFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        searchBar = findViewById(R.id.searchBar);
        filters = findViewById(R.id.filters);
        filterButton = findViewById(R.id.filterButton);
        spinnerCodigo = findViewById(R.id.spinnerCodigo);
        spinnerNombre = findViewById(R.id.spinnerNombre);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        recyclerView = findViewById(R.id.recyclerViewClientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabAdd = findViewById(R.id.fab_add_record);
        fabEdit = findViewById(R.id.fab_edit_record);
        fabDelete = findViewById(R.id.fab_delete_record);
        fabReaddRecord = findViewById(R.id.fab_readd_record);
        textEliminarFiltros = findViewById(R.id.eliminarFiltrosText);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteActivity.this, HomeActivity.class);
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
                return (T) new ClienteViewModel(database);
            }
        }).get(ClienteViewModel.class);

        // Observa los cambios en la lista de clientes
        clienteViewModel.getAllClientes().observe(this, clientes -> {
            if (adapter == null) {
                adapter = new ClienteAdapter(ClienteActivity.this, clientes);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateClientes(clientes); // Actualiza el adaptador si ya está inicializado
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

    private void agregarCliente() {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "crear_cliente");
        startActivityForResult(intent, 1);
    }

    private void editarCliente(ClienteEntity cliente) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_FRAGMENT_TYPE, "editar_cliente");

        Bundle data = new Bundle();
        data.putInt("cli_cod", cliente.getCliCod());
        data.putString("cli_nom", cliente.getCliNom());
        data.putString("cli_est", cliente.getCliEstReg());
        intent.putExtra(FormularioActivity.EXTRA_DATA, data);
        startActivity(intent);
    }

    private void eliminarCliente(ClienteEntity cliente) {
        if (cliente != null) {
            cliente.setCliEstReg("*"); // Marcar como eliminado
            clienteViewModel.updateCliente(cliente); // Actualiza en la base de datos
            resetFilters();
            Toast.makeText(this, "Cliente eliminado: " + cliente.getCliNom(), Toast.LENGTH_SHORT).show();
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
        adapter.setOnItemClickListener(cliente -> {
            if (selectedCliente != null && selectedCliente.equals(cliente)) {
                resetSelection();
            } else {
                selectedCliente = cliente;
                if (cliente.getCliEstReg().equals("*")) {
                    fabReaddRecord.setVisibility(View.VISIBLE);
                    fabEdit.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                } else {
                    fabReaddRecord.setVisibility(View.GONE);
                    fabEdit.setVisibility(View.VISIBLE);
                    fabDelete.setVisibility(View.VISIBLE);
                }
                adapter.setSelectedCliente(selectedCliente);
                adapter.notifyDataSetChanged();

                // Configurar listeners para los botones
                fabEdit.setOnClickListener(v -> { editarCliente(selectedCliente); resetSelection(); });
                fabDelete.setOnClickListener(v -> { eliminarCliente(selectedCliente); resetSelection(); });
                fabReaddRecord.setOnClickListener(v -> { reactivarCliente(selectedCliente); resetSelection(); });
            }
        });

        fabAdd.setOnClickListener(v -> { agregarCliente(); resetSelection(); });
    }

    private void reactivarCliente(ClienteEntity cliente) {
        if (cliente != null) {
            cliente.setCliEstReg("A"); // Cambia "*" a "A" (activo)
            clienteViewModel.updateCliente(cliente);
            Toast.makeText(this, "Cliente reactivado: " + cliente.getCliNom(), Toast.LENGTH_SHORT).show();
            fabReaddRecord.setVisibility(View.GONE); // Ocultar el botón después de la reactivación
            resetFilters();
        }
    }

    private void filterClientes(String query) {
        if (query.isEmpty()) {
            // Si el campo de búsqueda está vacío, mostrar todos los clientes
            clienteViewModel.getAllClientes().observe(this, clientes -> adapter.updateClientes(clientes));
            return;
        }

        List<ClienteEntity> filteredResults = new ArrayList<>();
        clienteViewModel.getAllClientes().observe(this, allClientes -> {
            if (allClientes != null) {
                for (ClienteEntity cliente : allClientes) {
                    String cliCod = "CLI" + cliente.getCliCod();
                    if (cliCod.toLowerCase().contains(query.toLowerCase()) ||
                            cliente.getCliNom().toLowerCase().contains(query.toLowerCase())) {
                        filteredResults.add(cliente);
                    }
                }
                adapter.updateClientes(filteredResults);
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
        clienteViewModel.getDeletedClientes().removeObservers(this);

        // Obtenemos la lista completa de clientes (que es un LiveData)
        clienteViewModel.getAllClientes().observe(this, allClientes -> {
            List<ClienteEntity> filteredClientes = new ArrayList<>();

            // Si hay clientes, procedemos a filtrar
            if (allClientes != null) {
                filteredClientes.addAll(allClientes);

                // Filtrar por estado (Activo/Inactivo/Eliminado)
                String estadoFilter = spinnerEstado.getSelectedItem().toString();
                if (estadoFilter.equals("Eliminado")) {
                    clienteViewModel.getDeletedClientes().observe(this, deletedClientes -> {
                        adapter.updateClientes(deletedClientes);
                    });
                    return; // Salimos aquí porque no aplicamos más filtros
                } else if (!estadoFilter.equals("Ninguno")) {
                    String estadoReal = estadoFilter.equals("Activo") ? "A" : "I";
                    filteredClientes.removeIf(cliente -> !cliente.getCliEstReg().equalsIgnoreCase(estadoReal));
                }

                // Filtrar por código
                String codigoFilter = spinnerCodigo.getSelectedItem().toString();
                if (codigoFilter.equals("Mayor a menor")) {
                    filteredClientes.sort((a, b) -> Integer.compare(b.getCliCod(), a.getCliCod()));
                } else if (codigoFilter.equals("Menor a mayor")) {
                    filteredClientes.sort((a, b) -> Integer.compare(a.getCliCod(), b.getCliCod()));
                }

                // Filtrar por nombre
                String nombreFilter = spinnerNombre.getSelectedItem().toString();
                if (nombreFilter.equals("A-Z")) {
                    filteredClientes.sort((a, b) -> a.getCliNom().compareToIgnoreCase(b.getCliNom()));
                } else if (nombreFilter.equals("Z-A")) {
                    filteredClientes.sort((a, b) -> b.getCliNom().compareToIgnoreCase(a.getCliNom()));
                }

                // Actualiza el adaptador con la lista filtrada
                adapter.updateClientes(filteredClientes);
            }
        });
    }


    private void resetFilters() {
        spinnerCodigo.setSelection(0);
        spinnerNombre.setSelection(0);
        spinnerEstado.setSelection(0);
        clienteViewModel.getAllClientes().observe(this, clientes -> adapter.updateClientes(clientes));
    }

    private void resetSelection() {
        selectedCliente = null;
        adapter.setSelectedCliente(null);
        fabEdit.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);
        fabReaddRecord.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
