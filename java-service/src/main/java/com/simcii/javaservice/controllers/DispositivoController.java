package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.services.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
@CrossOrigin(origins = "*")
public class DispositivoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DispositivoController.class);
    
    @Autowired
    private DispositivoService dispositivoService;
    
    @GetMapping
    public List<Dispositivo> getAllDispositivos() {
        logger.info("Obteniendo todos los dispositivos");
        return dispositivoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> getDispositivoById(@PathVariable Long id) {
        Optional<Dispositivo> dispositivo = dispositivoService.findById(id);
        return dispositivo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createDispositivo(@RequestBody Dispositivo dispositivo) {
        try {
            logger.info("Creando dispositivo: {}", dispositivo);
            
            // Validaciones básicas
            if (dispositivo.getNombre() == null || dispositivo.getNombre().trim().isEmpty()) {
                logger.warn("Intento de crear dispositivo sin nombre");
                return ResponseEntity.badRequest().body("El nombre es requerido");
            }
            if (dispositivo.getUbicacion() == null || dispositivo.getUbicacion().trim().isEmpty()) {
                logger.warn("Intento de crear dispositivo sin ubicación");
                return ResponseEntity.badRequest().body("La ubicación es requerida");
            }
            
            // El tipo se determina automáticamente por la clase concreta
            // gracias a @JsonTypeInfo, no necesitamos validarlo manualmente
            
            // Asegurar que activo tenga valor por defecto
            if (dispositivo.getActivo() == null) {
                dispositivo.setActivo(true);
            }
            
            Dispositivo savedDispositivo = dispositivoService.save(dispositivo);
            logger.info("Dispositivo creado exitosamente: {}", savedDispositivo.getId());
            
            return ResponseEntity.ok(savedDispositivo);
            
        } catch (Exception e) {
            logger.error("Error al crear dispositivo: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error interno del servidor: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDispositivo(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            logger.info("Actualizando dispositivo ID: {} con datos: {}", id, updates);
            
            Optional<Dispositivo> dispositivoOpt = dispositivoService.findById(id);
            if (dispositivoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"message\": \"Dispositivo no encontrado\"}");
            }
            
            Dispositivo dispositivo = dispositivoOpt.get();
            
            // Actualizar solo los campos que vienen en el request
            if (updates.containsKey("nombre")) {
                dispositivo.setNombre((String) updates.get("nombre"));
            }
            if (updates.containsKey("descripcion")) {
                Object descripcionObj = updates.get("descripcion");
                if (descripcionObj == null) {
                    dispositivo.setDescripcion(null);
                } else {
                    dispositivo.setDescripcion(descripcionObj.toString());
                }
            }
            if (updates.containsKey("ubicacion")) {
                dispositivo.setUbicacion((String) updates.get("ubicacion"));
            }
            if (updates.containsKey("activo")) {
                Object activoObj = updates.get("activo");
                if (activoObj instanceof Boolean) {
                    dispositivo.setActivo((Boolean) activoObj);
                } else if (activoObj instanceof String) {
                    dispositivo.setActivo(Boolean.parseBoolean((String) activoObj));
                }
            }
            
            // Siempre actualizar la fecha de modificación
            dispositivo.setFechaActualizacion(LocalDateTime.now());
            
            Dispositivo updatedDispositivo = dispositivoService.save(dispositivo);
            return ResponseEntity.ok(updatedDispositivo);
            
        } catch (Exception e) {
            logger.error("Error al actualizar dispositivo {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body("{\"message\": \"Error al actualizar: " + e.getMessage() + "\"}");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivo(@PathVariable Long id) {
        try {
            logger.info("Marcando dispositivo como inactivo (DELETE) ID: {}", id);
            
            Optional<Dispositivo> dispositivoOpt = dispositivoService.findById(id);
            if (dispositivoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"message\": \"Dispositivo no encontrado\"}");
            }
            
            Dispositivo dispositivo = dispositivoOpt.get();
            
            // Marcar como inactivo en lugar de eliminar
            dispositivo.setActivo(false);
            dispositivo.setFechaActualizacion(LocalDateTime.now());
            
            Dispositivo updated = dispositivoService.save(dispositivo);
            logger.info("Dispositivo marcado como inactivo exitosamente: {}", id);
            
            return ResponseEntity.ok().body("{\"message\": \"Dispositivo desactivado correctamente\", \"id\": " + id + "}");
            
        } catch (Exception e) {
            logger.error("Error al marcar dispositivo como inactivo {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}