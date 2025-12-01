package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.services.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de alertas y umbrales en el sistema SIMCII.
 * Expone endpoints para configurar y consultar umbrales de alerta que definen
 * los rangos operativos aceptables para los dispositivos del invernadero.
 * Las alertas se generan automáticamente cuando las lecturas superan estos umbrales.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/alertas")
 * @CrossOrigin(origins = "*")
 * @see Umbral
 * @see AlertaService
 */
@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {
    
    /**
     * Servicio de negocio para la gestión de alertas y umbrales.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private AlertaService alertaService;
    
     /**
     * Configura un nuevo umbral de alerta para un dispositivo específico.
     * Crea o actualiza los rangos de valores aceptables que, al ser superados,
     * generarán alertas en el sistema para notificar condiciones críticas.
     * 
     * @param umbral Objeto Umbral con la configuración completa del umbral
     * @return Umbral el umbral creado o actualizado con su ID asignado
     * @HTTP 200 Éxito, umbral configurado correctamente
     * @HTTP 400 Datos del umbral inválidos o inconsistentes
     * @HTTP 404 Dispositivo asociado no encontrado
     * @see AlertaService#configurarUmbral(Umbral)
     */
    @PostMapping("/umbrales")
    public Umbral configurarUmbral(@RequestBody Umbral umbral) {
        return alertaService.configurarUmbral(umbral);
    }
    
    /**
     * Obtiene todos los umbrales configurados para un dispositivo específico.
     * Retorna la lista de rangos de alerta definidos para monitorear las lecturas
     * de un sensor o actuador particular en las diferentes zonas del invernadero.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Umbral> lista de umbrales configurados para el dispositivo
     * @HTTP 200 Éxito, devuelve la lista de umbrales (puede estar vacía)
     * @HTTP 404 Dispositivo no encontrado
     * @see AlertaService#obtenerUmbralesPorDispositivo(Long)
     */
    @GetMapping("/dispositivo/{dispositivoId}/umbrales")
    public List<Umbral> getUmbralesPorDispositivo(@PathVariable Long dispositivoId) {
        return alertaService.obtenerUmbralesPorDispositivo(dispositivoId);
    }
}