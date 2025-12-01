package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Actuador;
import com.simcii.javaservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de actuadores en el sistema SIMCII.
 * Expone endpoints para operaciones CRUD y control de actuadores como sistemas
 * de riego, ventilación e iluminación en las diferentes zonas del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/actuadores")
 * @CrossOrigin(origins = "*")
 * @see Actuador
 * @see ActuadorService
 */
@RestController
@RequestMapping("/api/actuadores")
@CrossOrigin(origins = "*")
public class ActuadorController {
    
    /**
     * Servicio de negocio para la gestión de actuadores.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private ActuadorService actuadorService;
    
    /**
     * Obtiene la lista completa de todos los actuadores registrados en el sistema.
     * 
     * @return List<Actuador> con todos los actuadores disponibles
     * @HTTP 200 Éxito, devuelve la lista de actuadores (puede estar vacía)
     * @see ActuadorService#findAllActuadores()
     */
    @GetMapping
    public List<Actuador> getAllActuadores() {
        return actuadorService.findAllActuadores();
    }
    
    /**
     * Obtiene un actuador específico por su identificador único.
     * 
     * @param id Identificador único del actuador a consultar
     * @return ResponseEntity<Actuador> con el actuador encontrado o error 404
     * @HTTP 200 Éxito, devuelve el actuador solicitado
     * @HTTP 404 No se encontró el actuador con el ID especificado
     * @see ActuadorService#findActuadorById(Long)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Actuador> getActuadorById(@PathVariable Long id) {
        return actuadorService.findActuadorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Activa un actuador específico cambiando su estado a activo.
     * Este endpoint inicia la operación del actuador (ej: encender riego, activar ventilación).
     * 
     * @param id Identificador único del actuador a activar
     * @return ResponseEntity<Actuador> con el actuador actualizado o error 404
     * @HTTP 200 Éxito, actuador activado correctamente
     * @HTTP 404 No se encontró el actuador con el ID especificado
     * @see ActuadorService#activarActuador(Long)
     */
    @PostMapping("/{id}/activar")
    public ResponseEntity<Actuador> activarActuador(@PathVariable Long id) {
        try {
            Actuador actuador = actuadorService.activarActuador(id);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Desactiva un actuador específico cambiando su estado a inactivo.
     * Este endpoint detiene la operación del actuador (ej: apagar riego, desactivar ventilación).
     * 
     * @param id Identificador único del actuador a desactivar
     * @return ResponseEntity<Actuador> con el actuador actualizado o error 404
     * @HTTP 200 Éxito, actuador desactivado correctamente
     * @HTTP 404 No se encontró el actuador con el ID especificado
     * @see ActuadorService#desactivarActuador(Long)
     */
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Actuador> desactivarActuador(@PathVariable Long id) {
        try {
            Actuador actuador = actuadorService.desactivarActuador(id);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cambia el modo de operación de un actuador específico.
     * Permite configurar diferentes modos como AUTOMÁTICO, MANUAL, PROGRAMADO, etc.
     * 
     * @param id Identificador único del actuador a modificar
     * @param modo Nuevo modo de operación para el actuador
     * @return ResponseEntity<Actuador> con el actuador actualizado o error 404
     * @HTTP 200 Éxito, modo de operación cambiado correctamente
     * @HTTP 404 No se encontró el actuador con el ID especificado
     * @HTTP 400 El modo especificado no es válido
     * @see ActuadorService#cambiarModoOperacion(Long, String)
     */
    @PutMapping("/{id}/modo")
    public ResponseEntity<Actuador> cambiarModoOperacion(@PathVariable Long id, @RequestBody String modo) {
        try {
            Actuador actuador = actuadorService.cambiarModoOperacion(id, modo);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}