package com.example.neapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neapp.model.dao.ZonaDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.List;

public class ZonaViewModel extends AndroidViewModel {
    private final ZonaDao zonaDao;
    private final LiveData<List<ZonaEntity>> allZonas;
    private final LiveData<List<ZonaEntity>> deletedZonas;
    private final LiveData<List<ZonaEntity>> activeZonas;

    public ZonaViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        zonaDao = db.zonaDao(); // Inicializa el zonaDao
        allZonas = zonaDao.getNonDeletedZonas(); // Obtiene la lista de zonas desde la base de datos
        deletedZonas = zonaDao.getDeletedZonas();
        activeZonas = zonaDao.getActiveZonas();
    }

    public LiveData<List<ZonaEntity>> getAllZonas() {
        return allZonas;
    }

    public LiveData<List<ZonaEntity>> getDeletedZonas() {
        return deletedZonas; // Método para acceder a las zonas eliminadas
    }

    public LiveData<List<ZonaEntity>> getActiveZonas() {
        return activeZonas; // Método para acceder a las zonas activas
    }

    public void updateZona(ZonaEntity zona) {
        new Thread(() -> zonaDao.updateZona(zona)).start(); // Ejecución en un hilo separado
    }

    // Método para obtener el nombre de la zona por código
    public LiveData<String> getNombreZonaByCodigo(int codigo) {
        return zonaDao.getNombreZonaByCodigo(codigo);
    }

    public LiveData<String> getEstadoZonaByCodigo(int codigo) {
        return zonaDao.getEstadoZonaByCodigo(codigo);
    }
}
