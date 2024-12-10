package com.example.neapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neapp.model.dao.MaestroPublicidadDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.MaestroPublicidadEntity;

import java.util.List;

public class MaestroPublicidadViewModel extends AndroidViewModel {
    private final MaestroPublicidadDao maestroPublicidadDao;
    private final LiveData<List<MaestroPublicidadEntity>> allPublicidades;
    private final LiveData<List<MaestroPublicidadEntity>> deletedPublicidades;

    public MaestroPublicidadViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        maestroPublicidadDao = db.maestroPublicidadDao();
        allPublicidades = maestroPublicidadDao.getNonDeletedPublicidades();
        deletedPublicidades = maestroPublicidadDao.getDeletedPublicidades();
    }

    // Método para obtener todas las publicidades activas
    public LiveData<List<MaestroPublicidadEntity>> getAllPublicidades() {
        return allPublicidades;
    }

    // Método para obtener todas las publicidades eliminadas
    public LiveData<List<MaestroPublicidadEntity>> getDeletedPublicidades() {
        return deletedPublicidades;
    }

    // Método para actualizar una publicidad
    public void updatePublicidad(MaestroPublicidadEntity publicidad) {
        new Thread(() -> maestroPublicidadDao.updateMaestroPublicidad(publicidad)).start();
    }
}
