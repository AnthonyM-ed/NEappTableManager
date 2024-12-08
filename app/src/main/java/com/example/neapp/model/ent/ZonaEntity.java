package com.example.neapp.model.ent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Zona")
public class ZonaEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "zonCod")
    private int zonCod;

    @ColumnInfo(name = "zonNom")
    private String zonNom;

    @ColumnInfo(name = "zonEstReg")
    private String zonEstReg;

    public ZonaEntity() {
    }

    public int getZonCod() {

        return zonCod;
    }

    public void setZonCod(int zonCod) {

        this.zonCod = zonCod;
    }

    public String getZonNom() {
        return zonNom;
    }

    public void setZonNom(String zonNom) {
        if (zonNom == null || zonNom.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la zona no puede estar vacío.");
        }
        this.zonNom = zonNom;
    }

    public String getZonEstReg() {
        return zonEstReg;
    }

    public void setZonEstReg(String zonEstReg) {
        if (zonEstReg == null || zonEstReg.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la zona no puede estar vacío.");
        }
        this.zonEstReg = zonEstReg;
    }
}
