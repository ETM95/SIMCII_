package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Clase que representa un sensor de luz específico en el sistema de monitoreo.
 * Hereda de la clase Sensor y especializa el comportamiento para mediciones de intensidad lumínica.
 * Este sensor mide los niveles de luz en el invernadero, controlando la iluminación natural
 * y artificial para optimizar la fotosíntesis en las diferentes zonas de cultivo.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Sensor
 * @see SensorTemperatura
 * @see SensorHumedad
 */
@Entity
@Table(name = "sensores_luz")
public class SensorLuz extends Sensor {

    /**
     * Tipo específico de luz que detecta el sensor.
     * Permite diferenciar entre diferentes fuentes o espectros de luz.
     * Ejemplos: "NATURAL", "ARTIFICIAL", "UV", "FOTOSINTETICA"
     */
    private String tipoLuz;

    /**
     * Constructor por defecto que inicializa un sensor de luz.
     * Establece automáticamente el tipo de sensor como "LUZ"
     * y la unidad de medida como "lux" (unidad estándar de iluminancia).
     */    
    public SensorLuz() {
        this.setTipoSensor("LUZ");
        this.setUnidadMedida("lux");
    }
    
    /**
     * Obtiene el tipo específico de luz que detecta el sensor.
     * 
     * @return String que representa el tipo de luz (NATURAL, ARTIFICIAL, UV, etc.)
     */
    public String getTipoLuz() { 
        return tipoLuz; 
    }

    /**
     * Establece el tipo específico de luz que detecta el sensor.
     * 
     * @param tipoLuz Nuevo tipo de luz para el sensor
     * @throws IllegalArgumentException si tipoLuz es nulo o vacío
     */
    public void setTipoLuz(String tipoLuz) { 
        this.tipoLuz = tipoLuz; 
    }
}