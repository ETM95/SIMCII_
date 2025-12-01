package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Clase que representa un actuador en el sistema de control de invernaderos.
 * Hereda de la clase Dispositivo y añade propiedades específicas para actuadores IoT.
 * Los actuadores son dispositivos que ejecutan acciones físicas basadas en las lecturas
 * de los sensores, como activar sistemas de riego, ventilación o iluminación.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Dispositivo
 * @see Sensor
 */
@Entity
@Table(name = "actuadores")
public class Actuador extends Dispositivo {

     /**
     * Tipo específico de actuador que define la acción que puede realizar.
     * Ejemplos: "RIEGO", "VENTILACION", "ILUMINACION", "CALEFACCION"
     */
    private String tipoActuador;

    /**
     * Estado actual del actuador que indica si está activo o inactivo.
     * true = actuador activado/en funcionamiento
     * false = actuador desactivado/en reposo
     */
    private Boolean estado;

    /**
     * Modo de operación del actuador que define su comportamiento.
     * Ejemplos: "AUTOMATICO", "MANUAL", "PROGRAMADO", "EMERGENCIA"
     */
    private String modoOperacion;
    
    /**
     * Constructor por defecto que inicializa un actuador.
     * Llama al constructor de la clase padre Dispositivo y establece
     * el estado inicial del actuador como desactivado (false).
     */
    public Actuador() {
        super();
        this.estado = false;
    }
    
    /**
     * Obtiene el tipo específico de actuador.
     * 
     * @return String que representa el tipo de actuador (RIEGO, VENTILACION, etc.)
     */
    public String getTipoActuador() {
        return tipoActuador;
    }
    
     /**
     * Establece el tipo específico de actuador.
     * 
     * @param tipoActuador Nuevo tipo de actuador
     * @throws IllegalArgumentException si el tipoActuador es nulo o vacío
     */
    public void setTipoActuador(String tipoActuador) {
        this.tipoActuador = tipoActuador;
    }
    
    /**
     * Obtiene el estado actual del actuador.
     * 
     * @return Boolean true si el actuador está activado, false si está desactivado
     */
    public Boolean getEstado() {
        return estado;
    }
    
     /**
     * Establece el estado del actuador.
     * 
     * @param estado Nuevo estado del actuador (true = activado, false = desactivado)
     */
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    /**
     * Obtiene el modo de operación del actuador.
     * 
     * @return String con el modo de operación (AUTOMATICO, MANUAL, etc.)
     */
    public String getModoOperacion() {
        return modoOperacion;
    }
    
     /**
     * Establece el modo de operación del actuador.
     * 
     * @param modoOperacion Nuevo modo de operación
     * @throws IllegalArgumentException si el modoOperacion es nulo o vacío
     */
    public void setModoOperacion(String modoOperacion) {
        this.modoOperacion = modoOperacion;
    }
}