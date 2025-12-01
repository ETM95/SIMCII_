package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clase que representa una lectura o medición capturada por un dispositivo del sistema.
 * Almacena los valores históricos de sensores y el estado de actuadores en momentos específicos.
 * Cada lectura está asociada a un dispositivo y contiene el valor medido, la unidad
 * y la marca temporal de cuando se realizó la medición.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Dispositivo
 * @see Sensor
 * @see Actuador
 */
@Entity
@Table(name = "lecturas")
public class Lectura {

     /**
     * Identificador único de la lectura en la base de datos.
     * Se genera automáticamente mediante una estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Dispositivo que generó esta lectura.
     * Relación Many-to-One: muchas lecturas pueden pertenecer a un mismo dispositivo.
     * Puede ser un sensor (capturando valores) o un actuador (registrando estados).
     */
    @ManyToOne
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
    
    /**
     * Valor numérico de la lectura capturada.
     * Para sensores: representa la medición (temperatura, humedad, etc.)
     * Para actuadores: puede representar el estado (0=apagado, 1=encendido) o intensidad
     */
    private Double valor;

    /**
     * Marca temporal que indica cuándo se capturó la lectura.
     * Se establece automáticamente al momento de crear la lectura.
     */
    private LocalDateTime fechaHora;

    /**
     * Unidad de medida en la que se expresa el valor de la lectura.
     * Ejemplos: "°C" para temperatura, "%" para humedad, "lux" para luz, "boolean" para estados
     */
    private String unidad;
    
    /**
     * Constructor por defecto que inicializa una lectura con fecha/hora actual.
     * Útil para crear nuevas lecturas que se registran en tiempo real.
     */
    public Lectura() {
        this.fechaHora = LocalDateTime.now();
    }
    
    /**
     * Constructor parametrizado para crear lecturas con dispositivo, valor y unidad.
     * Establece automáticamente la fecha/hora actual.
     * 
     * @param dispositivo Dispositivo que genera la lectura
     * @param valor Valor numérico de la medición
     * @param unidad Unidad de medida del valor
     */
    public Lectura(Dispositivo dispositivo, Double valor, String unidad) {
        this.dispositivo = dispositivo;
        this.valor = valor;
        this.unidad = unidad;
        this.fechaHora = LocalDateTime.now();
    }
    
    /**
     * Constructor completo para crear lecturas con todos los parámetros.
     * Permite especificar una fecha/hora específica, útil para cargas históricas o simulaciones.
     * 
     * @param dispositivo Dispositivo que genera la lectura
     * @param valor Valor numérico de la medición
     * @param fechaHora Marca temporal específica de la lectura
     * @param unidad Unidad de medida del valor
     */
    public Lectura(Dispositivo dispositivo, Double valor, LocalDateTime fechaHora, String unidad) {
        this.dispositivo = dispositivo;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.unidad = unidad;
    }
    
    /**
     * Obtiene el identificador único de la lectura.
     * 
     * @return Long con el ID único de la lectura
     */
    public Long getId() {
        return id;
    }
    
     /**
     * Establece el identificador único de la lectura.
     * 
     * @param id Nuevo ID para la lectura
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el dispositivo que generó esta lectura.
     * 
     * @return Dispositivo asociado a esta lectura
     */
    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    /**
     * Establece el dispositivo que generó esta lectura.
     * 
     * @param dispositivo Nuevo dispositivo asociado
     * @throws IllegalArgumentException si el dispositivo es nulo
     */
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
    
     /**
     * Obtiene el valor numérico de la lectura.
     * 
     * @return Double con el valor de la medición
     */
    public Double getValor() {
        return valor;
    }
    
    /**
     * Establece el valor numérico de la lectura.
     * 
     * @param valor Nuevo valor de la medición
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }
    
     /**
     * Obtiene la marca temporal de cuando se capturó la lectura.
     * 
     * @return LocalDateTime con la fecha y hora de la lectura
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    /**
     * Establece la marca temporal de cuando se capturó la lectura.
     * 
     * @param fechaHora Nueva fecha y hora para la lectura
     */
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    /**
     * Obtiene la unidad de medida del valor de la lectura.
     * 
     * @return String con la unidad de medida
     */
    public String getUnidad() {
        return unidad;
    }
    
    
    /**
     * Establece la unidad de medida del valor de la lectura.
     * 
     * @param unidad Nueva unidad de medida
     * @throws IllegalArgumentException si la unidad es nula o vacía
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}