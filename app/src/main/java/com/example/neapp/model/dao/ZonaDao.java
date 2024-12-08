package com.example.neapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.List;

@Dao
public interface ZonaDao {

    @Query("SELECT * FROM Zona")
    LiveData<List<ZonaEntity>> getAllZonas();

    // Filtrar registros con estado "*"
    @Query("SELECT * FROM Zona WHERE zonEstReg != '*'")
    LiveData<List<ZonaEntity>> getNonDeletedZonas();

    @Query("SELECT * FROM Zona WHERE zonEstReg = '*'")
    LiveData<List<ZonaEntity>> getDeletedZonas();

    @Query("SELECT * FROM Zona WHERE zonCod = :id")
    LiveData<ZonaEntity> getZonaById(int id);

    @Insert
    void insertZona(ZonaEntity zona);

    @Update
    void updateZona(ZonaEntity zona);

    @Delete
    void deleteZona(ZonaEntity zona);

    // Obtener nombre de la zona por su código
    @Query("SELECT zonNom FROM Zona WHERE zonCod = :codigo")
    LiveData<String> getNombreZonaByCodigo(int codigo);

    @Query("SELECT EXISTS(SELECT 1 FROM Zona WHERE zonCod = :zonaCod)")
    boolean existsById(int zonaCod);
}
