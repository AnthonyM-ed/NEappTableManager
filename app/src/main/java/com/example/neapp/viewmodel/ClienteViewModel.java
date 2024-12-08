package com.example.neapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.neapp.model.dao.ClienteDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ClienteEntity;

import java.util.List;

public class ClienteViewModel extends ViewModel {
    private final ClienteDao clienteDao;
    private final LiveData<List<ClienteEntity>> allClientes;
    private final LiveData<List<ClienteEntity>> deletedClientes;

    public ClienteViewModel(AppDatabase database) {
        clienteDao = database.clienteDao(); // Inicializa el clienteDao
        allClientes = clienteDao.getNonDeletedClientes(); // Obtiene la lista de clientes desde la base de datos
        deletedClientes = clienteDao.getDeletedClientes();
    }

    public LiveData<List<ClienteEntity>> getAllClientes() {
        return allClientes;
    }

    public LiveData<List<ClienteEntity>> getDeletedClientes() {
        return deletedClientes; // Método para acceder a los clientes eliminados
    }

    public void updateCliente(ClienteEntity cliente) {
        new Thread(() -> clienteDao.updateCliente(cliente)).start(); // Ejecución en un hilo separado
    }
}
