package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.services.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión y consulta de lecturas en el sistema SIMCII.
 * Expone endpoints para acceder al historial de mediciones capturadas por los
 * dispositivos del invernadero, permitiendo análisis temporal de los datos.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/lecturas")
 * @CrossOrigin(origins = "*")
 * @see Lectura
 * @see LecturaService
 */
@RestController
@RequestMapping("/api/lecturas")
@CrossOrigin(origins = "*")
public class LecturaController {
    
    /**
     * Servicio de negocio para la gestión de lecturas.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private LecturaService lecturaService;
    
    /**
     * Obtiene el historial completo de lecturas para un dispositivo específico.
     * Retorna todas las mediciones registradas ordenadas cronológicamente,
     * proporcionando una visión completa del comportamiento del dispositivo.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Lectura> con el historial completo de lecturas del dispositivo
     * @HTTP 200 Éxito, devuelve el historial de lecturas (puede estar vacío)
     * @HTTP 404 Dispositivo no encontrado
     * @see LecturaService#obtenerHistorialPorDispositivo(Long)
     */
    @GetMapping("/dispositivo/{dispositivoId}")
    public List<Lectura> getHistorialPorDispositivo(@PathVariable Long dispositivoId) {
        return lecturaService.obtenerHistorialPorDispositivo(dispositivoId);
    }
    
    /**
     * Obtiene las lecturas más recientes para un dispositivo específico.
     * Útil para dashboards en tiempo real y monitoreo de condiciones actuales
     * sin cargar todo el historial del dispositivo.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @param cantidad Número de lecturas recientes a retornar
     * @return List<Lectura> con las lecturas más recientes del dispositivo
     * @HTTP 200 Éxito, devuelve las lecturas solicitadas (puede ser menos si no hay suficientes)
     * @HTTP 404 Dispositivo no encontrado
     * @HTTP 400 Cantidad inválida (menor o igual a 0)
     * @see LecturaService#obtenerUltimasLecturas(Long, int)
     */
    @GetMapping("/dispositivo/{dispositivoId}/ultimas/{cantidad}")
    public List<Lectura> getUltimasLecturas(@PathVariable Long dispositivoId, @PathVariable int cantidad) {
        return lecturaService.obtenerUltimasLecturas(dispositivoId, cantidad);
    }
}