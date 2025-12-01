package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Clase que representa un sensor en el sistema de monitoreo de invernaderos.
 * Hereda de la clase Dispositivo y añade propiedades específicas para sensores IoT.
 * Los sensores son dispositivos que capturan datos ambientales como temperatura,
 * humedad y niveles de luz en las diferentes zonas del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @see Dispositivo
 * @see SensorTemperatura
 * @see SensorHumedad
 * @see SensorLuz
 */
@Entity
@Table(name = "sensores")
public class Sensor extends Dispositivo {

    /**
     * Tipo específico de sensor que define la naturaleza de las mediciones.
     * Ejemplos: "TEMPERATURA", "HUMEDAD", "LUZ", "CO2", etc.
     */
    private String tipoSensor;

     /**
     * Unidad de medida en la que se expresan las lecturas del sensor.
     * Ejemplos: "°C" para temperatura, "%" para humedad, "LUX" para luz.
     */
    private String unidadMedida;

    /**
     * Constructor por defecto que inicializa un sensor.
     * Llama al constructor de la clase padre Dispositivo para establecer
     * los valores predeterminados comunes.
     */
    public Sensor() {
        super();
    }
    
    /**
     * Obtiene el tipo específico de sensor.
     * 
     * @return String que representa el tipo de sensor (TEMPERATURA, HUMEDAD, LUZ, etc.)
     */
    public String getTipoSensor() {
        return tipoSensor;
    }
    
    /**
     * Establece el tipo específico de sensor.
     * 
     * @param tipoSensor Nuevo tipo de sensor
     * @throws IllegalArgumentException si el tipoSensor es nulo o vacío
     */
    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }
    
     /**
     * Obtiene la unidad de medida utilizada por el sensor.
     * 
     * @return String con la unidad de medida (ej: "°C", "%", "LUX")
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Establece la unidad de medida utilizada por el sensor.
     * 
     * @param unidadMedida Nueva unidad de medida
     * @throws IllegalArgumentException si la unidadMedida es nula o vacía
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}