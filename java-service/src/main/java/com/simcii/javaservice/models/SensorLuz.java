package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensores_luz")
public class SensorLuz extends Sensor {
    private String tipoLuz; // "VISIBLE", "UV", "IR"
    
    public SensorLuz() {
        this.setTipoSensor("LUZ");
        this.setUnidadMedida("lux");
    }
    
    // Implementación del método abstracto
    @Override
    public String getTipo() {
        return "SENSOR_LUZ";
    }
    
    public String getTipoLuz() { return tipoLuz; }
    public void setTipoLuz(String tipoLuz) { this.tipoLuz = tipoLuz; }
}