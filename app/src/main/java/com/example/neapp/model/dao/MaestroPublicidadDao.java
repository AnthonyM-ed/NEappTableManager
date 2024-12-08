package com.example.neapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.List;

@Dao
public interface MaestroPublicidadDao {

    @Query("SELECT * FROM MaestroPublicidad")
    LiveData<List<MaestroPublicidadEntity>> getAllMaestroPublicidad();

    // Filtrar registros con estado "*"
    @Query("SELECT * FROM MaestroPublicidad WHERE pubEstReg != '*'")
    LiveData<List<MaestroPublicidadEntity>> getNonDeletedPublicidades();

    @Query("SELECT * FROM MaestroPublicidad WHERE pubEstReg = '*'")
    LiveData<List<MaestroPublicidadEntity>> getDeletedPublicidades();

    @Query("SELECT * FROM MaestroPublicidad WHERE pubCod = :id")
    LiveData<MaestroPublicidadEntity> getMaestroPublicidadById(int id);

    @Insert
    void insertMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);

    @Update
    void updateMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);

    @Delete
    void deleteMaestroPublicidad(MaestroPublicidadEntity maestroPublicidad);
}
