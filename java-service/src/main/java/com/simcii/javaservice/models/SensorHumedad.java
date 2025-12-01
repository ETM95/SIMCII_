package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Clase que representa un sensor de humedad específico en el sistema de monitoreo.
 * Hereda de la clase Sensor y especializa el comportamiento para mediciones de humedad relativa.
 * Este sensor mide los niveles de humedad ambiental en el invernadero, un factor crítico
 * para el crecimiento óptimo de las plantas en las diferentes zonas de cultivo.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Sensor
 * @see SensorTemperatura
 * @see SensorLuz
 */
@Entity
@Table(name = "sensores_humedad")
public class SensorHumedad extends Sensor {

    /**
     * Constructor por defecto que inicializa un sensor de humedad.
     * Establece automáticamente el tipo de sensor como "HUMEDAD"
     * y la unidad de medida como porcentaje ("%").
     * Los sensores de humedad típicamente miden humedad relativa en un rango de 0% a 100%.
     */
    public SensorHumedad() {
        this.setTipoSensor("HUMEDAD");
        this.setUnidadMedida("%");
    }
}