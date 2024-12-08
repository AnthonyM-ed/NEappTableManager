package com.example.neapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.neapp.model.dao.MaestroPublicidadDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.MaestroPublicidadEntity;

import java.util.List;

public class MaestroPublicidadViewModel extends ViewModel {
    private final MaestroPublicidadDao maestroPublicidadDao;
    private final LiveData<List<MaestroPublicidadEntity>> allPublicidades;
    private final LiveData<List<MaestroPublicidadEntity>> deletedPublicidades;

    public MaestroPublicidadViewModel(AppDatabase database) {
        maestroPublicidadDao = database.maestroPublicidadDao();
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
