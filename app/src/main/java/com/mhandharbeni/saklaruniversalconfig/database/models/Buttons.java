package com.mhandharbeni.saklaruniversalconfig.database.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"uniqueId"}, unique = true)})
public class Buttons implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int uniqueId;

    String position;
    String mode;
    String relay;
    String label;
    int type;
    int status;

    public Buttons() {
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRelay() {
        return relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "Buttons{" +
                "uniqueId=" + uniqueId +
                ", position='" + position + '\'' +
                ", mode='" + mode + '\'' +
                ", relay='" + relay + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
