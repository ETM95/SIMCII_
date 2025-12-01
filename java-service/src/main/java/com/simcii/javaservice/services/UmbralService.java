package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.models.SensorTemperatura;
import com.simcii.javaservice.models.SensorHumedad;
import com.simcii.javaservice.models.SensorLuz;
import com.simcii.javaservice.repositories.UmbralRepository;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * Servicio para la gestión de umbrales y generación de valores de lectura en el sistema SIMCII.
 * Proporciona funcionalidades para inicialización automática de umbrales por defecto,
 * generación de lecturas realistas basadas en configuración de umbrales, y operaciones
 * CRUD para la gestión de rangos de alerta en el sistema de monitoreo.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Umbral
 * @see Dispositivo
 * @see PostConstruct
 */
@Service
public class UmbralService {
    
    /**
     * Repositorio para acceso a datos de umbrales.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private UmbralRepository umbralRepository;
    
    /**
     * Repositorio para acceso a datos de dispositivos.
     * Utilizado para obtener sensores durante la inicialización.
     */
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    /**
     * Generador de números aleatorios para simulación de lecturas realistas.
     */
    private Random random = new Random();
    
    /**
     * Inicializa umbrales por defecto para todos los sensores activos del sistema.
     * Se ejecuta automáticamente después de la construcción del servicio.
     * Crea configuraciones específicas según el tipo de sensor (temperatura, humedad, luz)
     * solo si no existen umbrales previamente configurados.
     * 
     * @PostConstruct
     */
    @PostConstruct
    public void inicializarUmbralesPorDefecto() {
        System.out.println("=== INICIALIZANDO UMBRALES POR DEFECTO ===");
        
        try {
            List<Dispositivo> sensores = dispositivoRepository.findAll().stream()
                .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
                .filter(Dispositivo::getActivo)
                .toList();
            
            System.out.println("Sensores encontrados para umbrales: " + sensores.size());
            
            for (Dispositivo sensor : sensores) {
                // Verificar si ya existe umbral para este sensor
                if (umbralRepository.findByDispositivo(sensor).isEmpty()) {
                    Umbral umbral = crearUmbralPorDefecto(sensor);
                    umbralRepository.save(umbral);
                    System.out.println("Umbral creado para: " + sensor.getNombre() + " | Rango: " + 
                                     umbral.getValorMin() + " - " + umbral.getValorMax());
                }
            }
            
            System.out.println("=== INICIALIZACIÓN DE UMBRALES COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("Error inicializando umbrales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un umbral por defecto específico para el tipo de sensor.
     * Configura rangos realistas basados en el tipo de sensor para condiciones
     * óptimas de cultivo en invernadero.
     * 
     * @param sensor Sensor para el cual crear el umbral
     * @return Umbral configurado con valores por defecto apropiados
     * @see SensorTemperatura
     * @see SensorHumedad
     * @see SensorLuz
     */
    private Umbral crearUmbralPorDefecto(Dispositivo sensor) {
        Umbral umbral = new Umbral();
        umbral.setDispositivo(sensor);
        umbral.setActivo(true);
        
        if (sensor instanceof SensorTemperatura) {
            umbral.setValorMin(18.0);
            umbral.setValorMax(28.0);
            umbral.setTipoAlerta("TEMPERATURA_FUERA_RANGO");
        } else if (sensor instanceof SensorHumedad) {
            umbral.setValorMin(40.0);
            umbral.setValorMax(70.0);
            umbral.setTipoAlerta("HUMEDAD_FUERA_RANGO");
        } else if (sensor instanceof SensorLuz) {
            umbral.setValorMin(200.0);
            umbral.setValorMax(800.0);
            umbral.setTipoAlerta("LUZ_FUERA_RANGO");
        } else {
            // Sensor genérico
            umbral.setValorMin(0.0);
            umbral.setValorMax(100.0);
            umbral.setTipoAlerta("VALOR_FUERA_RANGO");
        }
        
        return umbral;
    }
    
    /**
     * Genera un valor de lectura realista basado en los umbrales configurados para un dispositivo.
     * Utiliza los rangos mínimo y máximo del umbral activo para generar valores coherentes
     * que simulan lecturas reales de sensores en condiciones de invernadero.
     * 
     * @param dispositivo Dispositivo para el cual generar la lectura
     * @return Double con un valor de lectura realista dentro del rango configurado
     * @implNote Incluye fallback a valores por defecto si no se encuentran umbrales configurados
     */
    public Double generarValorSegunUmbral(Dispositivo dispositivo) {
        try {
            List<Umbral> umbrales = umbralRepository.findByDispositivo(dispositivo);
            
            if (!umbrales.isEmpty()) {
                Umbral umbral = umbrales.get(0); // Tomar el primer umbral activo
                double rango = umbral.getValorMax() - umbral.getValorMin();
                double valor = umbral.getValorMin() + (random.nextDouble() * rango);
                
                System.out.println("Valor generado desde umbral: " + valor + 
                                 " | Rango: " + umbral.getValorMin() + "-" + umbral.getValorMax() +
                                 " | Sensor: " + dispositivo.getNombre());
                
                return valor;
            }
        } catch (Exception e) {
            System.err.println("Error generando valor desde umbral: " + e.getMessage());
        }
        
        return generarValorPorDefecto(dispositivo);
    }
    
    /**
     * Genera un valor de lectura por defecto cuando no hay umbrales configurados.
     * Proporciona rangos genéricos apropiados para cada tipo de sensor como respaldo.
     * 
     * @param dispositivo Dispositivo para el cual generar el valor por defecto
     * @return Double con un valor de lectura genérico
     */
    private Double generarValorPorDefecto(Dispositivo dispositivo) {
        if (dispositivo instanceof SensorTemperatura) {
            return 15 + (random.nextDouble() * 20);
        } else if (dispositivo instanceof SensorHumedad) {
            return 30 + (random.nextDouble() * 50);
        } else if (dispositivo instanceof SensorLuz) {
            return random.nextDouble() * 1000;
        }
        return random.nextDouble() * 100;
    }

    /**
     * Guarda o actualiza un umbral en el sistema.
     * 
     * @param umbral Umbral a guardar o actualizar
     * @return Umbral el umbral guardado con su ID asignado
     * @see UmbralRepository#save(Object)
     */
    public Umbral guardarUmbral(Umbral umbral) {
        return umbralRepository.save(umbral);
    }
    
    /**
     * Obtiene un umbral específico por su identificador único.
     * 
     * @param id Identificador único del umbral a buscar
     * @return Umbral encontrado o null si no existe
     * @see UmbralRepository#findById(Long)
     */
    public Umbral obtenerUmbralPorId(Long id) {
        return umbralRepository.findById(id).orElse(null);
    }
    
    /**
     * Desactiva un umbral específico sin eliminarlo permanentemente.
     * El umbral permanece en la base de datos pero deja de generar alertas.
     * 
     * @param id Identificador único del umbral a desactivar
     */
    public void desactivarUmbral(Long id) {
        Umbral umbral = umbralRepository.findById(id).orElse(null);
        if (umbral != null) {
            umbral.setActivo(false);
            umbralRepository.save(umbral);
        }
    }
    
    /**
     * Obtiene todos los umbrales configurados para un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Umbral> con todos los umbrales del dispositivo
     * @see UmbralRepository#findByDispositivoId(Long)
     */
    public List<Umbral> obtenerUmbralesPorDispositivo(Long dispositivoId) {
        return umbralRepository.findByDispositivoId(dispositivoId);
    }
}