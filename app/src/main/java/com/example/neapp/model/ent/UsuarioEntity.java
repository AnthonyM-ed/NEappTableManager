package com.example.neapp.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class UsuarioEntity {
    @PrimaryKey(autoGenerate = true)
    private int usuCod; // Código único del usuario
    private String usuNombre; // Nombre completo del usuario
    private String usuCorreo; // Correo electrónico del usuario
    private String usuContrasena; // Contraseña del usuario
    private String usuEstReg; // Estado de registro (A = Activo, I = Inactivo)

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
