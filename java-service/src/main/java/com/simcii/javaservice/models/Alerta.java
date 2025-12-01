package com.simcii.javaservice.models;

import java.time.LocalDateTime;

/**
 * Clase que representa una alerta generada en el sistema SIMCII.
 * Las alertas se crean automáticamente cuando las lecturas de los dispositivos
 * superan los umbrales configurados, notificando condiciones críticas o fuera
 * de rango en el invernadero.
 * 
 * @author Jonathan Vega 
 * @version 1.0
 * @since 2025
 * @see Dispositivo
 * @see Umbral
 * @see Lectura
 */
public class Alerta {

     /**
     * Identificador único de la alerta en el sistema.
     */
    private Long id;

    /**
     * Dispositivo que generó la alerta.
     * Referencia al sensor o actuador cuyas lecturas violaron los umbrales.
     */
    private Dispositivo dispositivo;

    /**
     * Umbral específico que fue violado y generó esta alerta.
     * Define los rangos de valores que fueron excedidos.
     */
    private Umbral umbral;

    /**
     * Valor actual que provocó la alerta.
     * Representa la lectura específica que superó los límites del umbral.
     */
    private Double valorActual;

    /**
     * Mensaje descriptivo de la alerta.
     * Proporciona información legible sobre la naturaleza de la condición crítica.
     * Ejemplo: "Temperatura crítica: 35°C en Zona A"
     */

    private String mensaje;

    /**
     * Estado de actividad de la alerta.
     * true = alerta activa y requiriendo atención
     * false = alerta resuelta o archivada
     */
    private Boolean activa;

    /**
     * Fecha y hora en que se generó la alerta.
     * Se establece automáticamente al crear la alerta.
     */
    private LocalDateTime fechaCreacion;
    
    /**
     * Constructor por defecto que inicializa una alerta con valores predeterminados.
     * Establece la fecha de creación actual y activa la alerta por defecto.
     */
    public Alerta() {
        this.fechaCreacion = LocalDateTime.now();
        this.activa = true;
    }
    
    /**
     * Constructor parametrizado para crear alertas con información específica.
     * Útil para generar alertas programáticamente cuando se detectan violaciones de umbrales.
     * 
     * @param dispositivo Dispositivo que generó la alerta
     * @param umbral Umbral que fue violado
     * @param valorActual Valor que provocó la alerta
     * @param mensaje Mensaje descriptivo de la alerta
     */
    public Alerta(Dispositivo dispositivo, Umbral umbral, Double valorActual, String mensaje) {
        this();
        this.dispositivo = dispositivo;
        this.umbral = umbral;
        this.valorActual = valorActual;
        this.mensaje = mensaje;
    }
    
    /**
     * Obtiene el identificador único de la alerta.
     * 
     * @return Long con el ID único de la alerta
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Establece el identificador único de la alerta.
     * 
     * @param id Nuevo ID para la alerta
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el dispositivo que generó la alerta.
     * 
     * @return Dispositivo asociado a esta alerta
     */
    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    /**
     * Establece el dispositivo que generó la alerta.
     * 
     * @param dispositivo Nuevo dispositivo asociado
     * @throws IllegalArgumentException si el dispositivo es nulo
     */
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
    
    /**
     * Obtiene el umbral que fue violado.
     * 
     * @return Umbral asociado a esta alerta
     */
    public Umbral getUmbral() {
        return umbral;
    }
    
    /**
     * Establece el umbral que fue violado.
     * 
     * @param umbral Nuevo umbral asociado
     * @throws IllegalArgumentException si el umbral es nulo
     */
    public void setUmbral(Umbral umbral) {
        this.umbral = umbral;
    }
    
    /**
     * Obtiene el valor actual que provocó la alerta.
     * 
     * @return Double con el valor que violó el umbral
     */
    public Double getValorActual() {
        return valorActual;
    }
    
    /**
     * Establece el valor actual que provocó la alerta.
     * 
     * @param valorActual Nuevo valor que generó la alerta
     */
    public void setValorActual(Double valorActual) {
        this.valorActual = valorActual;
    }
    
    /**
     * Obtiene el mensaje descriptivo de la alerta.
     * 
     * @return String con el mensaje de la alerta
     */
    public String getMensaje() {
        return mensaje;
    }
    
    /**
     * Establece el mensaje descriptivo de la alerta.
     * 
     * @param mensaje Nuevo mensaje para la alerta
     * @throws IllegalArgumentException si el mensaje es nulo o vacío
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    /**
     * Verifica si la alerta está activa y requiriendo atención.
     * 
     * @return Boolean true si la alerta está activa, false si está resuelta
     */
    public Boolean getActiva() {
        return activa;
    }
    
    /**
     * Establece el estado de actividad de la alerta.
     * 
     * @param activa Nuevo estado de la alerta (true = activa, false = resuelta)
     */
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
    
    /**
     * Obtiene la fecha y hora de creación de la alerta.
     * 
     * @return LocalDateTime con la fecha y hora de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    /**
     * Establece la fecha y hora de creación de la alerta.
     * Normalmente este método no debería usarse directamente después de la creación.
     * 
     * @param fechaCreacion Nueva fecha y hora de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}