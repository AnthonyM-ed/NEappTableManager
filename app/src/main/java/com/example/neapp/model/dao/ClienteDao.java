package com.example.neapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.ClienteEntity;

import java.util.List;

@Dao
public interface ClienteDao {

    @Query("SELECT * FROM Cliente")
    LiveData<List<ClienteEntity>> getAllClientes();

    // Filtrar registros con estado "*"
    @Query("SELECT * FROM Cliente WHERE cliEstReg != '*'")
    LiveData<List<ClienteEntity>> getNonDeletedClientes();

    @Query("SELECT * FROM Cliente WHERE cliEstReg = '*'")
    LiveData<List<ClienteEntity>> getDeletedClientes();

    @Query("SELECT * FROM Cliente WHERE cliEstReg = 'A'")
    LiveData<List<ClienteEntity>> getActiveClientes();

    @Query("SELECT * FROM Cliente WHERE cliCod = :id")
    LiveData<ClienteEntity> getClienteById(int id);

    @Insert
    void insertCliente(ClienteEntity cliente);

    @Update
    void updateCliente(ClienteEntity cliente);

    @Delete
    void deleteCliente(ClienteEntity cliente);

    // Obtener nombre del cliente por su código
    @Query("SELECT cliNom FROM Cliente WHERE cliCod = :codigo")
    LiveData<String> getNombreClienteByCodigo(int codigo);

    // Obtener estado del cliente por su código
    @Query("SELECT cliEstReg FROM Cliente WHERE cliCod = :codigo")
    LiveData<String> getEstadoClienteByCodigo(int codigo);

    @Query("SELECT EXISTS(SELECT 1 FROM Cliente WHERE cliCod = :clienteCod)")
    boolean existsById(int clienteCod);
}
