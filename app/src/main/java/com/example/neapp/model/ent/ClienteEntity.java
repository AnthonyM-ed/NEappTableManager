package com.example.neapp.model.ent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cliente")
public class ClienteEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cliCod")
    private int cliCod;

    @ColumnInfo(name = "cliNom")
    private String cliNom;

    @ColumnInfo(name = "cliEstReg")
    private String cliEstReg;

    public ClienteEntity() {
    }

    public int getCliCod() {
        return cliCod;
    }

    public void setCliCod(int cliCod) {
        this.cliCod = cliCod;
    }

    public String getCliNom() {
        return cliNom;
    }

    public void setCliNom(String cliNom) {
        if (cliNom == null || cliNom.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.cliNom = cliNom;
    }

    public String getCliEstReg() {
        return cliEstReg;
    }

    public void setCliEstReg(String cliEstReg) {
        if (cliEstReg == null || cliEstReg.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del cliente no puede estar vacío.");
        }
        this.cliEstReg = cliEstReg;
    }
}
