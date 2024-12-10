package com.example.neapp.model.ent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Usuario")
public class UsuarioEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "usuCod")
    private int usuCod; // Código único del usuario

    @ColumnInfo(name = "usuNombre")
    private String usuNombre; // Nombre completo del usuario

    @ColumnInfo(name = "usuApellido")
    private String usuApellido; // Apellido completo del usuario

    @ColumnInfo(name = "usuCorreo")
    private String usuCorreo; // Correo electrónico del usuario

    @ColumnInfo(name = "usuContrasena")
    private String usuContrasena; // Contraseña del usuario

    @ColumnInfo(name = "usuEstReg")
    private String usuEstReg; // Estado de registro (A = Activo, I = Inactivo)

    public UsuarioEntity() {
    }

    // Getters y Setters
    public int getUsuCod() {
        return usuCod;
    }

    public void setUsuCod(int usuCod) {
        this.usuCod = usuCod;
    }

    public String getUsuNombre() {
        return usuNombre;
    }

    public void setUsuNombre(String usuNombre) {
        this.usuNombre = usuNombre;
    }

    public String getUsuApellido() { return usuApellido; }

    public void setUsuApellido(String usuApellido) { this.usuApellido = usuApellido; }
    public String getUsuCorreo() {
        return usuCorreo;
    }

    public void setUsuCorreo(String usuCorreo) {
        this.usuCorreo = usuCorreo;
    }

    public String getUsuContrasena() {
        return usuContrasena;
    }

    public void setUsuContrasena(String usuContrasena) {
        this.usuContrasena = usuContrasena;
    }

    public String getUsuEstReg() {
        return usuEstReg;
    }

    public void setUsuEstReg(String usuEstReg) {
        this.usuEstReg = usuEstReg;
    }
}
