package com.example.neapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.UsuarioEntity;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insertUsuario(UsuarioEntity usuario);

    @Update
    void updateUsuario(UsuarioEntity usuario);

    @Query("SELECT * FROM Usuario WHERE usuEstReg = 'A'")
    List<UsuarioEntity> getUsuariosActivos();

    @Query("SELECT * FROM Usuario WHERE usuCod = :usuarioId")
    UsuarioEntity getUsuarioById(int usuarioId);

    @Query("DELETE FROM Usuario WHERE usuCod = :usuarioId")
    void deleteUsuarioById(int usuarioId);

    @Query("SELECT * FROM Usuario")
    List<UsuarioEntity> getAllUsuarios();
}
