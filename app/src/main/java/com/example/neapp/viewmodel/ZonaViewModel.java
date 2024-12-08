package com.example.neapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.neapp.model.dao.ZonaDao;
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.List;

public class ZonaViewModel extends ViewModel {
    private final ZonaDao zonaDao;
    private final LiveData<List<ZonaEntity>> allZonas;
    private final LiveData<List<ZonaEntity>> deletedZonas;
    private final LiveData<List<ZonaEntity>> activeZonas;

    public ZonaViewModel(AppDatabase database) {
        zonaDao = database.zonaDao(); // Inicializa el zonaDao
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
        return activeZonas; // Método para acceder a los clientes eliminados
    }

    public void updateZona(ZonaEntity zona) {
        new Thread(() -> zonaDao.updateZona(zona)).start(); // Ejecución en un hilo separado
    }

    // Método para obtener el nombre de la zona por código
    public LiveData<String> getNombreZonaByCodigo(int codigo) {
        return zonaDao.getNombreZonaByCodigo(codigo);
    }
}
