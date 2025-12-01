package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Clase que representa un sensor de temperatura específico en el sistema de monitoreo.
 * Hereda de la clase Sensor y especializa el comportamiento para mediciones de temperatura.
 * Este sensor mide temperaturas ambientales en el invernadero y define rangos operativos
 * específicos para condiciones de cultivo.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Sensor
 * @see SensorHumedad
 * @see SensorLuz
 */
@Entity
@Table(name = "sensores_temperatura")
public class SensorTemperatura extends Sensor {

    /**
     * Temperatura mínima del rango operativo del sensor en grados Celsius.
     * Valor por defecto: -10.0°C
     */
    private Double rangoMin;

    /**
     * Temperatura máxima del rango operativo del sensor en grados Celsius.
     * Valor por defecto: 50.0°C
     */
    private Double rangoMax;
    
     /**
     * Constructor por defecto que inicializa un sensor de temperatura.
     * Establece automáticamente el tipo de sensor como "TEMPERATURA",
     * la unidad de medida como "°C" y define rangos operativos por defecto
     * apropiados para condiciones de invernadero.
     */
    public SensorTemperatura() {
        this.setTipoSensor("TEMPERATURA");
        this.setUnidadMedida("°C");
        this.rangoMin = -10.0;
        this.rangoMax = 50.0;
    }
    
    /**
     * Obtiene la temperatura mínima del rango operativo del sensor.
     * 
     * @return Double que representa la temperatura mínima en grados Celsius
     */
    public Double getRangoMin() { 
        return rangoMin; 
    }

    /**
     * Establece la temperatura mínima del rango operativo del sensor.
     * 
     * @param rangoMin Nueva temperatura mínima en grados Celsius
     * @throws IllegalArgumentException si rangoMin es nulo o mayor que rangoMax
     */
    public void setRangoMin(Double rangoMin) { 
        this.rangoMin = rangoMin; 
    }
    
     /**
     * Obtiene la temperatura máxima del rango operativo del sensor.
     * 
     * @return Double que representa la temperatura máxima en grados Celsius
     */
    public Double getRangoMax() { 
        return rangoMax; 
    }

    /**
     * Establece la temperatura máxima del rango operativo del sensor.
     * 
     * @param rangoMax Nueva temperatura máxima en grados Celsius
     * @throws IllegalArgumentException si rangoMax es nulo o menor que rangoMin
     */
    public void setRangoMax(Double rangoMax) { 
        this.rangoMax = rangoMax; 
    }
}