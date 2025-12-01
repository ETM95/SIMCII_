package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Clase abstracta que representa un dispositivo genérico en el sistema de monitoreo de invernaderos.
 * Esta clase sirve como base para todos los dispositivos IoT (sensores y actuadores) del sistema SIMCII.
 * Utiliza herencia con estrategia JOINED para la persistencia en base de datos y
 * anotaciones Jackson para el manejo de polimorfismo en JSON.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @see Sensor
 * @see Actuador
 * @see SensorTemperatura
 * @see SensorHumedad
 * @see SensorLuz
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "dispositivos")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Sensor.class, name = "SENSOR"),
    @JsonSubTypes.Type(value = Actuador.class, name = "ACTUADOR"),
    @JsonSubTypes.Type(value = SensorTemperatura.class, name = "SENSOR_TEMPERATURA"),
    @JsonSubTypes.Type(value = SensorHumedad.class, name = "SENSOR_HUMEDAD"),
    @JsonSubTypes.Type(value = SensorLuz.class, name = "SENSOR_LUZ")
})
public abstract class Dispositivo {
    /**
     * Identificador único del dispositivo en la base de datos.
     * Se genera automáticamente mediante una estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre descriptivo del dispositivo.
     * Ejemplo: "Sensor Temperatura Zona A"
     */
    private String nombre;

    /**
     * Descripción detallada del dispositivo y su función específica.
     */
    private String descripcion;

    /**
     * Ubicación física del dispositivo en el invernadero.
     * Debe seguir el formato: "Zona A", "Zona B", "Zona C"
     */
    private String ubicacion;

    /**
     * Estado de actividad del dispositivo.
     * true = dispositivo activo y operativo
     * false = dispositivo inactivo o en mantenimiento
     */
    private Boolean activo;

     /**
     * Fecha y hora de creación del registro del dispositivo.
     * Se establece automáticamente al crear el dispositivo.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    /**
     * Fecha y hora de la última actualización del dispositivo.
     * Debe actualizarse cada vez que se modifique el dispositivo.
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    /**
     * Constructor por defecto que inicializa el dispositivo con valores predeterminados.
     * Establece la fecha de creación a la fecha/hora actual y activa el dispositivo.
     */
    public Dispositivo() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    /**
     * Obtiene el identificador único del dispositivo.
     * 
     * @return Long que representa el ID único del dispositivo
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Establece el identificador único del dispositivo.
     * 
     * @param id Nuevo identificador único para el dispositivo
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el nombre descriptivo del dispositivo.
     * 
     * @return String con el nombre del dispositivo
     */
    public String getNombre() {
        return nombre;
    }
    
     /**
     * Establece el nombre descriptivo del dispositivo.
     * 
     * @param nombre Nuevo nombre para el dispositivo
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
     /**
     * Obtiene la descripción detallada del dispositivo.
     * 
     * @return String con la descripción del dispositivo
     */
    public String getDescripcion() {
        return descripcion;
    }
    
     /**
     * Establece la descripción detallada del dispositivo.
     * 
     * @param descripcion Nueva descripción para el dispositivo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
     /**
     * Obtiene la ubicación física del dispositivo en el invernadero.
     * 
     * @return String con la ubicación del dispositivo (Zona A, B o C)
     */
    public String getUbicacion() {
        return ubicacion;
    }
    
    /**
     * Establece la ubicación física del dispositivo en el invernadero.
     * 
     * @param ubicacion Nueva ubicación para el dispositivo
     * @throws IllegalArgumentException si la ubicación no es válida
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    /**
     * Verifica si el dispositivo está activo y operativo.
     * 
     * @return Boolean true si el dispositivo está activo, false en caso contrario
     */
    public Boolean getActivo() {
        return activo;
    }
    
    /**
     * Establece el estado de actividad del dispositivo.
     * 
     * @param activo Nuevo estado de actividad (true = activo, false = inactivo)
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    /**
     * Obtiene la fecha y hora de creación del registro del dispositivo.
     * 
     * @return LocalDateTime con la fecha y hora de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    /**
     * Establece la fecha y hora de creación del registro del dispositivo.
     * Normalmente este método no debería usarse directamente, ya que la fecha
     * se establece automáticamente en el constructor.
     * 
     * @param fechaCreacion Nueva fecha y hora de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
     /**
     * Obtiene la fecha y hora de la última actualización del dispositivo.
     * 
     * @return LocalDateTime con la fecha y hora de última actualización
     */
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    /**
     * Establece la fecha y hora de la última actualización del dispositivo.
     * Este método debería llamarse cada vez que se modifique el dispositivo.
     * 
     * @param fechaActualizacion Nueva fecha y hora de actualización
     */
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}