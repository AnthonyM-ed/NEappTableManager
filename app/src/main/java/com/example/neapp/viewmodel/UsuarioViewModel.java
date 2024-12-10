package com.example.neapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neapp.model.dao.UsuarioDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.UsuarioEntity;

public class UsuarioViewModel extends AndroidViewModel {

    private final UsuarioDao usuarioDao;

    public UsuarioViewModel(Application application) {
        super(application);
        // Obtiene la instancia de la base de datos y el DAO
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
    }

    /**
     * Verifica las credenciales de un usuario por correo y contraseña.
     *
     * @param correo      el correo del usuario.
     * @param contrasena  la contraseña del usuario.
     * @return un LiveData que contiene la entidad de usuario si las credenciales son válidas.
     */
    public LiveData<UsuarioEntity> verificarCredenciales(String correo, String contrasena) {
        return usuarioDao.verificarCredenciales(correo, contrasena);
    }

    public LiveData<UsuarioEntity> getUsuarioById(int usuarioId) {
        return usuarioDao.getUsuarioById(usuarioId);
    }

    public void updateUsuario(UsuarioEntity usuario) {
        new Thread(() -> usuarioDao.updateUsuario(usuario)).start();
    }
}