package com.example.neapp.model.ent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "MaestroPublicidad",
        foreignKeys = {
                @ForeignKey(
                        entity = ClienteEntity.class,
                        parentColumns = "cliCod",
                        childColumns = "cliCod",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = ZonaEntity.class,
                        parentColumns = "zonCod",
                        childColumns = "zonCod",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class MaestroPublicidadEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pubCod")
    private int pubCod;

    @ColumnInfo(name = "cliCod")
    private int cliCod;

    @ColumnInfo(name = "zonCod")
    private int zonCod;

    @ColumnInfo(name = "pubNom")
    private String pubNom;

    @ColumnInfo(name = "pubUbi")
    private String pubUbi;

    @ColumnInfo(name = "pubEstReg")
    private String pubEstReg;

    public MaestroPublicidadEntity() {
    }

    public int getPubCod() {
        return pubCod;
    }

    public void setPubCod(int pubCod) {
        this.pubCod = pubCod;
    }

    public int getCliCod() {
        return cliCod;
    }

    public void setCliCod(int cliCod) {
        this.cliCod = cliCod;
    }

    public int getZonCod() {
        return zonCod;
    }

    public void setZonCod(int zonCod) {
        this.zonCod = zonCod;
    }

    public String getPubNom() {
        return pubNom;
    }

    public void setPubNom(String pubNom) {
        if (pubNom == null || pubNom.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la publicidad no puede estar vacío.");
        }
        this.pubNom = pubNom;
    }

    public String getPubUbi() {
        return pubUbi;
    }

    public void setPubUbi(String pubUbi) {
        if (pubUbi == null || pubUbi.trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación de la publicidad no puede estar vacía.");
        }
        this.pubUbi = pubUbi;
    }

    public String getPubEstReg() {
        return pubEstReg;
    }

    public void setPubEstReg(String pubEstReg) {
        if (pubEstReg == null || pubEstReg.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la publicidad no puede estar vacío.");
        }
        this.pubEstReg = pubEstReg;
    }
}
