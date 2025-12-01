package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Sensor;
import com.simcii.javaservice.models.SensorHumedad;
import com.simcii.javaservice.models.SensorLuz;
import com.simcii.javaservice.models.SensorTemperatura;
import com.simcii.javaservice.repositories.LecturaRepository;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de lecturas de sensores en el sistema SIMCII.
 * Proporciona funcionalidades para registro automático periódico de lecturas,
 * consulta de historial y gestión del ciclo de vida de datos de sensores.
 * Incluye un scheduler que ejecuta lecturas cada 10 segundos para simulación
 * en tiempo real del monitoreo del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Lectura
 * @see Sensor
 * @see Scheduled
 */
@Service
public class LecturaService {
    
    /**
     * Repositorio para acceso a datos de lecturas.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private LecturaRepository lecturaRepository;
    
    /**
     * Repositorio para acceso a datos de dispositivos.
     * Utilizado para obtener la lista de sensores activos.
     */
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    /**
     * Servicio para gestión de alertas.
     * Utilizado para verificar violaciones de umbrales en las lecturas.
     */
    @Autowired
    private AlertaService alertaService;

    /**
     * Servicio para gestión de umbrales.
     * Utilizado para generar valores de lectura realistas basados en configuración.
     */
    @Autowired
    private UmbralService umbralService;
    
    /**
     * Generador de números aleatorios para simulación de lecturas.
     */
    private Random random = new Random();
    
    /**
     * Ejecuta el registro automático de lecturas cada 10 segundos.
     * Obtiene todos los sensores activos del sistema, genera lecturas realistas
     * basadas en los umbrales configurados y las persiste en la base de datos.
     * Incluye logging detallado para monitoreo del proceso.
     * 
     * @Scheduled(fixedRate = 10000) Ejecución cada 10 segundos (10000 ms)
     * @see UmbralService#generarValorSegunUmbral(Dispositivo)
     */
    @Scheduled(fixedRate = 10000)
public void registrarLecturasAutomaticas() {
    System.out.println("=== INICIANDO LECTURAS AUTOMÁTICAS ===");
    
    try {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .filter(Dispositivo::getActivo)
            .collect(Collectors.toList());
            
        System.out.println("Sensores activos encontrados: " + sensores.size());
        
        for (Dispositivo sensor : sensores) {
            
            Double valor = umbralService.generarValorSegunUmbral(sensor);
            System.out.println("Generando lectura: " + valor + " para " + sensor.getNombre() + " (ID: " + sensor.getId() + ")");
            
            Lectura lectura = new Lectura();
            lectura.setDispositivo(sensor);
            lectura.setValor(valor);
            lectura.setUnidad(obtenerUnidad(sensor));
            
            Lectura saved = lecturaRepository.save(lectura);
            System.out.println("Lectura guardada ID: " + saved.getId());
        }
        
        System.out.println("=== LECTURAS COMPLETADAS ===");
        
    } catch (Exception e) {
        System.err.println("ERROR en lecturas automáticas: " + e.getMessage());
        e.printStackTrace();
    }
}

    /**
     * Obtiene la unidad de medida apropiada para un dispositivo sensor.
     * 
     * @param dispositivo Dispositivo del cual obtener la unidad
     * @return String con la unidad de medida del sensor
     * @see Sensor#getUnidadMedida()
     */
    private String obtenerUnidad(Dispositivo dispositivo) {
        if (dispositivo instanceof Sensor) {
            return ((Sensor) dispositivo).getUnidadMedida();
        }
        return "unidad";
    }
    
    /**
     * Obtiene el historial completo de lecturas para un dispositivo específico.
     * Las lecturas se retornan ordenadas por fecha/hora descendente (más recientes primero).
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Lectura> con el historial completo de lecturas ordenado
     * @see LecturaRepository#findByDispositivoIdOrderByFechaHoraDesc(Long)
     */
    public List<Lectura> obtenerHistorialPorDispositivo(Long dispositivoId) {
        return lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(dispositivoId);
    }
    
    /**
     * Obtiene las lecturas más recientes de un dispositivo específico.
     * Utiliza una consulta nativa optimizada con LIMIT para mejor rendimiento.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @param cantidad Número de lecturas recientes a retornar
     * @return List<Lectura> con las lecturas más recientes del dispositivo
     * @see LecturaRepository#findUltimasLecturas(Long, int)
     */
    public List<Lectura> obtenerUltimasLecturas(Long dispositivoId, int cantidad) {
        return lecturaRepository.findUltimasLecturas(dispositivoId, cantidad);
    }
}