package com.example.neapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.neapp.model.ent.UsuarioEntity;

import java.util.List;

@Dao
public interface UsuarioDao {

    @Query("SELECT * FROM Usuario")
    LiveData<List<UsuarioEntity>> getAllUsuarios();
    @Insert
    void insertUsuario(UsuarioEntity usuario);

    @Update
    void updateUsuario(UsuarioEntity usuario);

    @Query("SELECT * FROM Usuario WHERE usuCod = :usuarioId")
    LiveData<UsuarioEntity> getUsuarioById(int usuarioId);

    @Delete
    void deleteUsuario(UsuarioEntity usuario);

    @Query("SELECT * FROM Usuario WHERE usuCorreo = :correo AND usuContrasena = :contrasena AND usuEstReg = 'A'")
    LiveData<UsuarioEntity> verificarCredenciales(String correo, String contrasena);
}
