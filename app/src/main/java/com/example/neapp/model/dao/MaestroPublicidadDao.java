package com.example.neapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.MaestroPublicidadEntity;

import java.util.List;

@Dao
public interface MaestroPublicidadDao {

    @Query("SELECT * FROM MaestroPublicidad")
    LiveData<List<MaestroPublicidadEntity>> getAllMaestroPublicidad();

    @Query("SELECT * FROM MaestroPublicidad WHERE pubCod = :id")
    LiveData<MaestroPublicidadEntity> getMaestroPublicidadById(int id);

    @Query("SELECT * FROM MaestroPublicidad WHERE cliCod = :clienteId")
    LiveData<List<MaestroPublicidadEntity>> getMaestroPublicidadByClienteId(int clienteId);

    @Query("SELECT * FROM MaestroPublicidad WHERE zonCod = :zonaId")
    LiveData<List<MaestroPublicidadEntity>> getMaestroPublicidadByZonaId(int zonaId);

    @Insert
    void insertMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);

    @Update
    void updateMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);

    @Delete
    void deleteMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);
}
