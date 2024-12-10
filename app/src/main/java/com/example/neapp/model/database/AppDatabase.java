package com.example.neapp.model.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.neapp.model.dao.ClienteDao;
import com.example.neapp.model.dao.MaestroPublicidadDao;
import com.example.neapp.model.dao.UsuarioDao;
import com.example.neapp.model.dao.ZonaDao;
import com.example.neapp.model.ent.ClienteEntity;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.model.ent.UsuarioEntity;
import com.example.neapp.model.ent.ZonaEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

@Database(entities = {ClienteEntity.class, ZonaEntity.class, MaestroPublicidadEntity.class, UsuarioEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClienteDao clienteDao();
    public abstract ZonaDao zonaDao();
    public abstract MaestroPublicidadDao maestroPublicidadDao();
    public abstract UsuarioDao usuarioDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "neapp_database"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("AppDatabase", "Base de datos creada.");
                                    // Aquí puedes llamar a los métodos de carga de datos
                                    loadInitialData(context);
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Método para cargar datos iniciales
    private static void loadInitialData(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            cargarClientesDesdeArchivo(context);
            cargarZonasDesdeArchivo(context);
            cargarUsuariosDesdeArchivo(context);
            //cargarMaestroPublicidadDesdeArchivo(context);
        });
    }

    // Método para cargar clientes desde el archivo txt
    private static void cargarClientesDesdeArchivo(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("clientes.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) { // Nombre|Estado de registro
                    ClienteEntity cliente = new ClienteEntity();
                    cliente.setCliNom(parts[0]);
                    cliente.setCliEstReg(parts[1]);
                    // Insertar el cliente en la base de datos
                    INSTANCE.clienteDao().insertCliente(cliente);
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo clientes.txt", e);
        }
    }

    // Método para cargar zonas desde el archivo txt
    private static void cargarZonasDesdeArchivo(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("zonas.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) { // Nombre|Estado de registro
                    ZonaEntity zona = new ZonaEntity();
                    zona.setZonNom(parts[0]);
                    zona.setZonEstReg(parts[1]);
                    // Insertar la zona en la base de datos
                    INSTANCE.zonaDao().insertZona(zona);
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo zonas.txt", e);
        }
    }

    public static void cargarMaestroPublicidadDesdeArchivo(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("maestroPublicidad.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) { // Validar el formato correcto
                    String nombrePublicidad = parts[0];
                    int clienteCod = Integer.parseInt(parts[1]);
                    int zonaCod = Integer.parseInt(parts[2]);
                    String ubicacion = parts[3];
                    String estadoRegistro = parts[4];

                    // Validar que clienteCod y zonaCod existan
                    boolean clienteExiste = INSTANCE.clienteDao().existsById(clienteCod);
                    boolean zonaExiste = INSTANCE.zonaDao().existsById(zonaCod);

                    if (clienteExiste && zonaExiste) {
                        // Crear la entidad MaestroPublicidad
                        MaestroPublicidadEntity publicidad = new MaestroPublicidadEntity();
                        publicidad.setPubNom(nombrePublicidad);
                        publicidad.setCliCod(clienteCod);
                        publicidad.setZonCod(zonaCod);
                        publicidad.setPubUbi(ubicacion);
                        publicidad.setPubEstReg(estadoRegistro);

                        // Insertar en la base de datos
                        INSTANCE.maestroPublicidadDao().insertMaestroPublicidad(publicidad);
                    } else {
                        Log.w("AppDatabase", "No se pudo insertar: Cliente o Zona no existen. ClienteCod=" + clienteCod + ", ZonaCod=" + zonaCod);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo MaestroPublicidad.txt", e);
        }
    }

    // Método para cargar usuarios desde el archivo txt
    private static void cargarUsuariosDesdeArchivo(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("usuarios.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) { // Nombre|Apellido|Correo Electronico|Contraseña
                    // Crear un objeto UsuarioEntity (debes tener la entidad UsuarioEntity y su DAO)
                    UsuarioEntity usuario = new UsuarioEntity();
                    usuario.setUsuNombre(parts[0]);
                    usuario.setUsuApellido(parts[1]);
                    usuario.setUsuCorreo(parts[2]);
                    usuario.setUsuContrasena(parts[3]);
                    usuario.setUsuEstReg("A"); // Estado por defecto

                    // Insertar el usuario en la base de datos
                    INSTANCE.usuarioDao().insertUsuario(usuario);
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo usuarios.txt", e);
        }
    }
}
