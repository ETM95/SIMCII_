package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clase que representa un umbral de alerta para un dispositivo en el sistema de monitoreo.
 * Define los rangos aceptables de operación para los dispositivos y genera alertas cuando
 * las lecturas se salen de estos rangos. Los umbrales son configurables por dispositivo
 * y permiten diferentes tipos de alertas según la criticidad de las condiciones.
 * 
 * @author Jonathan Vega
 * @version 1.0 
 * @since 2025
 * @see Dispositivo
 * @see Lectura
 * @see Sensor
 */

@Entity
@Table(name = "umbrales")
public class Umbral {

    /**
     * Identificador único del umbral en la base de datos.
     * Se genera automáticamente mediante una estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Dispositivo al que se aplica este umbral.
     * Relación Many-to-One: un dispositivo puede tener múltiples umbrales configurados
     * para diferentes tipos de alertas o condiciones.
     */
    @ManyToOne
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
    
    /**
     * Valor mínimo aceptable para las lecturas del dispositivo.
     * Las lecturas por debajo de este valor activarán una alerta.
     * Ejemplo: Para temperatura, podría ser 15.0°C
     */
    private Double valorMin;

    /**
     * Valor máximo aceptable para las lecturas del dispositivo.
     * Las lecturas por encima de este valor activarán una alerta.
     * Ejemplo: Para temperatura, podría ser 30.0°C
     */
    private Double valorMax;

    /**
     * Tipo de alerta que se genera cuando se viola este umbral.
     * Define la criticidad y el comportamiento del sistema.
     * Ejemplos: "CRITICA", "ADVERTENCIA", "INFORMATIVA", "EMERGENCIA"
     */
    private String tipoAlerta;

    /**
     * Indica si el umbral está activo y monitoreando lecturas.
     * true = umbral activo y generando alertas
     * false = umbral inactivo, no genera alertas
     */
    private Boolean activo;

    /**
     * Fecha y hora de creación del umbral.
     * Se establece automáticamente al crear el registro.
     */
    private LocalDateTime fechaCreacion;
    
    /**
     * Constructor por defecto que inicializa un umbral con valores predeterminados.
     * Establece la fecha de creación actual y activa el umbral por defecto.
     */
    public Umbral() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    /**
     * Constructor parametrizado para crear umbrales activos.
     * Establece automáticamente el umbral como activo y la fecha de creación actual.
     * 
     * @param dispositivo Dispositivo al que se aplica el umbral
     * @param valorMin Valor mínimo aceptable
     * @param valorMax Valor máximo aceptable
     * @param tipoAlerta Tipo de alerta a generar
     */
    public Umbral(Dispositivo dispositivo, Double valorMin, Double valorMax, String tipoAlerta) {
        this.dispositivo = dispositivo;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoAlerta = tipoAlerta;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    /**
     * Constructor completo para crear umbrales con control explícito de estado.
     * Permite especificar si el umbral está activo o inactivo desde su creación.
     * 
     * @param dispositivo Dispositivo al que se aplica el umbral
     * @param valorMin Valor mínimo aceptable
     * @param valorMax Valor máximo aceptable
     * @param tipoAlerta Tipo de alerta a generar
     * @param activo Estado inicial del umbral
     */
    public Umbral(Dispositivo dispositivo, Double valorMin, Double valorMax, String tipoAlerta, Boolean activo) {
        this.dispositivo = dispositivo;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoAlerta = tipoAlerta;
        this.activo = activo;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    /**
     * Obtiene el identificador único del umbral.
     * 
     * @return Long con el ID único del umbral
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Establece el identificador único del umbral.
     * 
     * @param id Nuevo ID para el umbral
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el dispositivo asociado a este umbral.
     * 
     * @return Dispositivo al que se aplica este umbral
     */
    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    /**
     * Establece el dispositivo asociado a este umbral.
     * 
     * @param dispositivo Nuevo dispositivo para el umbral
     * @throws IllegalArgumentException si el dispositivo es nulo
     */
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
    
    /**
     * Obtiene el valor mínimo aceptable configurado.
     * 
     * @return Double con el valor mínimo del umbral
     */
    public Double getValorMin() {
        return valorMin;
    }
    
    /**
     * Establece el valor mínimo aceptable para el umbral.
     * 
     * @param valorMin Nuevo valor mínimo
     * @throws IllegalArgumentException si valorMin es mayor que valorMax
     */
    public void setValorMin(Double valorMin) {
        this.valorMin = valorMin;
    }
    
    /**
     * Obtiene el valor máximo aceptable configurado.
     * 
     * @return Double con el valor máximo del umbral
     */
    public Double getValorMax() {
        return valorMax;
    }
    
    /**
     * Establece el valor máximo aceptable para el umbral.
     * 
     * @param valorMax Nuevo valor máximo
     * @throws IllegalArgumentException si valorMax es menor que valorMin
     */
    public void setValorMax(Double valorMax) {
        this.valorMax = valorMax;
    }
    
    /**
     * Obtiene el tipo de alerta que genera este umbral.
     * 
     * @return String con el tipo de alerta
     */
    public String getTipoAlerta() {
        return tipoAlerta;
    }
    
    /**
     * Establece el tipo de alerta que genera este umbral.
     * 
     * @param tipoAlerta Nuevo tipo de alerta
     * @throws IllegalArgumentException si tipoAlerta es nulo o vacío
     */
    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }
    
    /**
     * Verifica si el umbral está activo y monitoreando lecturas.
     * 
     * @return Boolean true si el umbral está activo, false en caso contrario
     */
    public Boolean getActivo() {
        return activo;
    }
    
    /**
     * Establece el estado de actividad del umbral.
     * 
     * @param activo Nuevo estado (true = activo, false = inactivo)
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    /**
     * Obtiene la fecha y hora de creación del umbral.
     * 
     * @return LocalDateTime con la fecha y hora de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    /**
     * Establece la fecha y hora de creación del umbral.
     * Normalmente este método no debería usarse directamente después de la creación.
     * 
     * @param fechaCreacion Nueva fecha y hora de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}