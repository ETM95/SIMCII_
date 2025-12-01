package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.repositories.DispositivoRepository;
import com.simcii.javaservice.repositories.LecturaRepository;
import com.simcii.javaservice.services.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de depuración y diagnóstico para el sistema SIMCII.
 * Expone endpoints de utilidad para desarrollo, testing y monitoreo del estado
 * interno del sistema. NO debe estar habilitado en entornos de producción.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/debug")
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    /**
     * Repositorio para acceso a datos de dispositivos.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    /**
     * Repositorio para acceso a datos de lecturas.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private LecturaRepository lecturaRepository;
    
    /**
     * Servicio de negocio para gestión de lecturas.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private LecturaService lecturaService;
    
    /**
     * Endpoint básico de prueba para verificar que el controlador está funcionando.
     * 
     * @return String con mensaje de confirmación y timestamp actual
     * @HTTP 200 Siempre retorna éxito con mensaje de estado
     */
    @GetMapping("/test")
    public String test() {
        return "DebugController funcionando - " + java.time.LocalDateTime.now();
    }
    
    /**
     * Obtiene la lista de todos los sensores registrados en el sistema.
     * Filtra los dispositivos por nombre de clase para identificar sensores.
     * Incluye logging detallado para diagnóstico.
     * 
     * @return List<Dispositivo> con todos los sensores encontrados
     * @HTTP 200 Éxito, devuelve lista de sensores (puede estar vacía)
     */
    @GetMapping("/sensores")
    public List<Dispositivo> listarSensores() {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .collect(Collectors.toList());
            
        System.out.println("🔍 Sensores encontrados: " + sensores.size());
        for (Dispositivo s : sensores) {
            System.out.println(" - " + s.getNombre() + " | Tipo: " + s.getClass().getSimpleName() + " | ID: " + s.getId());
        }
        return sensores;
    }
    
    /**
     * Proporciona un resumen del estado actual del sistema.
     * Incluye conteos de dispositivos, sensores, actuadores y timestamp.
     * 
     * @return String con reporte de estado formateado
     * @HTTP 200 Éxito, devuelve reporte de estado del sistema
     */
    @GetMapping("/estado")
    public String estado() {
        long totalDispositivos = dispositivoRepository.count();
        List<Dispositivo> todos = dispositivoRepository.findAll();
        long sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .count();
        long actuadores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Actuador"))
            .count();
            
        return "Estado del Sistema:\n" +
               " - Total dispositivos: " + totalDispositivos + "\n" +
               " - Sensores: " + sensores + "\n" + 
               " - Actuadores: " + actuadores + "\n" +
               " - Hora: " + java.time.LocalDateTime.now();
    }
    
    /**
     * Ejecuta manualmente el proceso de registro de lecturas automáticas.
     * Útil para testing y desarrollo sin esperar el scheduler programado.
     * 
     * @return String con resultado de la operación
     * @HTTP 200 Éxito, lecturas forzadas correctamente
     * @HTTP 500 Error interno durante el proceso de lecturas
     * @see LecturaService#registrarLecturasAutomaticas()
     */
    @PostMapping("/forzar-lecturas")
    public String forzarLecturas() {
        try {
            System.out.println("EJECUTANDO LECTURAS MANUALMENTE...");
            lecturaService.registrarLecturasAutomaticas();
            return "Lecturas forzadas exitosamente - Revisa los logs";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene el historial de lecturas para un dispositivo específico.
     * Las lecturas se ordenan por fecha/hora descendente (más recientes primero).
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Lectura> con el historial de lecturas del dispositivo
     * @HTTP 200 Éxito, devuelve lista de lecturas (puede estar vacía)
     * @HTTP 404 Dispositivo no encontrado
     */
    @GetMapping("/lecturas/{dispositivoId}")
    public List<Lectura> getLecturas(@PathVariable Long dispositivoId) {
        List<Lectura> lecturas = lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(dispositivoId);
        System.out.println("Lecturas para dispositivo " + dispositivoId + ": " + lecturas.size());
        return lecturas;
    }
    
    /**
     * Genera un reporte detallado de logs para todos los sensores activos.
     * Incluye información de cada sensor y el conteo de lecturas asociadas.
     * 
     * @return String con reporte formateado de sensores activos
     * @HTTP 200 Éxito, devuelve reporte de logs de sensores
     */
    @GetMapping("/logs-sensores")
    public String logsSensores() {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .filter(Dispositivo::getActivo)
            .collect(Collectors.toList());
            
        StringBuilder log = new StringBuilder();
        log.append("SENSORES ACTIVOS:\n");
        for (Dispositivo sensor : sensores) {
            long countLecturas = lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(sensor.getId()).size();
            log.append(" - ").append(sensor.getNombre())
               .append(" (ID: ").append(sensor.getId())
               .append(") | Lecturas: ").append(countLecturas)
               .append(" | Tipo: ").append(sensor.getClass().getSimpleName())
               .append("\n");
        }
        return log.toString();
    }
}