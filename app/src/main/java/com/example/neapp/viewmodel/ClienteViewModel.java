package com.example.neapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.neapp.model.dao.ClienteDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ClienteEntity;

import java.util.List;

public class ClienteViewModel extends AndroidViewModel {
    private final ClienteDao clienteDao;
    private final LiveData<List<ClienteEntity>> allClientes;
    private final LiveData<List<ClienteEntity>> deletedClientes;
    private final LiveData<List<ClienteEntity>> activeClientes;

    // Constructor que acepta Application
    public ClienteViewModel(@NonNull Application application) {
        super(application); // Llama al constructor del AndroidViewModel
        AppDatabase db = AppDatabase.getInstance(application);
        clienteDao = db.clienteDao(); // Inicializa el clienteDao
        allClientes = clienteDao.getNonDeletedClientes(); // Obtiene la lista de clientes desde la base de datos
        deletedClientes = clienteDao.getDeletedClientes();
        activeClientes = clienteDao.getActiveClientes();
    }

    public LiveData<List<ClienteEntity>> getAllClientes() {
        return allClientes;
    }

    public LiveData<List<ClienteEntity>> getDeletedClientes() {
        return deletedClientes;
    }

    public LiveData<List<ClienteEntity>> getActiveClientes() {
        return activeClientes;
    }

    public void updateCliente(ClienteEntity cliente) {
        new Thread(() -> clienteDao.updateCliente(cliente)).start();
    }

    public LiveData<String> getNombreClienteByCodigo(int codigo) {
        return clienteDao.getNombreClienteByCodigo(codigo);
    }

    public LiveData<String> getEstadoClienteByCodigo(int codigo) {
        return clienteDao.getEstadoClienteByCodigo(codigo);
    }
}
