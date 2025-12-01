package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.services.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión integral de dispositivos en el sistema SIMCII.
 * Expone operaciones CRUD completas para todos los tipos de dispositivos (sensores y actuadores)
 * que monitorean y controlan las condiciones ambientales del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/dispositivos")
 * @CrossOrigin(origins = "*")
 * @see Dispositivo
 * @see DispositivoService
 */
@RestController
@RequestMapping("/api/dispositivos")
@CrossOrigin(origins = "*")
public class DispositivoController {
    
    /**
     * Servicio de negocio para la gestión de dispositivos.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private DispositivoService dispositivoService;
    
    /**
     * Obtiene la lista completa de todos los dispositivos registrados en el sistema.
     * Incluye tanto sensores como actuadores de todas las zonas del invernadero.
     * 
     * @return List<Dispositivo> con todos los dispositivos disponibles
     * @HTTP 200 Éxito, devuelve la lista de dispositivos (puede estar vacía)
     * @see DispositivoService#findAll()
     */
    @GetMapping
    public List<Dispositivo> getAllDispositivos() {
        return dispositivoService.findAll();
    }
    
    /**
     * Obtiene un dispositivo específico por su identificador único.
     * 
     * @param id Identificador único del dispositivo a consultar
     * @return ResponseEntity<Dispositivo> con el dispositivo encontrado o error 404
     * @HTTP 200 Éxito, devuelve el dispositivo solicitado
     * @HTTP 404 No se encontró el dispositivo con el ID especificado
     * @see DispositivoService#findById(Long)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> getDispositivoById(@PathVariable Long id) {
        Optional<Dispositivo> dispositivo = dispositivoService.findById(id);
        return dispositivo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crea un nuevo dispositivo en el sistema.
     * Soporta la creación de cualquier tipo de dispositivo (sensor o actuador)
     * con la configuración inicial proporcionada en el cuerpo de la petición.
     * 
     * @param dispositivo Objeto Dispositivo con los datos del nuevo dispositivo
     * @return Dispositivo el dispositivo creado con su ID asignado
     * @HTTP 200 Éxito, dispositivo creado correctamente
     * @HTTP 400 Datos del dispositivo inválidos o incompletos
     * @see DispositivoService#save(Dispositivo)
     */
    @PostMapping
    public Dispositivo createDispositivo(@RequestBody Dispositivo dispositivo) {
        return dispositivoService.save(dispositivo);
    }
    
    /**
     * Actualiza la información de un dispositivo existente.
     * Permite modificar propiedades como nombre, descripción, ubicación y estado
     * de cualquier dispositivo registrado en el sistema.
     * 
     * @param id Identificador único del dispositivo a actualizar
     * @param dispositivoDetails Objeto Dispositivo con los nuevos datos
     * @return ResponseEntity<Dispositivo> con el dispositivo actualizado o error 404
     * @HTTP 200 Éxito, dispositivo actualizado correctamente
     * @HTTP 404 No se encontró el dispositivo con el ID especificado
     * @HTTP 400 Datos de actualización inválidos
     * @see DispositivoService#update(Long, Dispositivo)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> updateDispositivo(@PathVariable Long id, @RequestBody Dispositivo dispositivoDetails) {
        try {
            Dispositivo updatedDispositivo = dispositivoService.update(id, dispositivoDetails);
            return ResponseEntity.ok(updatedDispositivo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina un dispositivo del sistema.
     * Remove permanentemente un dispositivo y todas sus asociaciones
     * (lecturas, umbrales) según la configuración de cascada.
     * 
     * @param id Identificador único del dispositivo a eliminar
     * @return ResponseEntity<?> confirmación de eliminación exitosa
     * @HTTP 200 Éxito, dispositivo eliminado correctamente
     * @HTTP 404 No se encontró el dispositivo con el ID especificado
     * @see DispositivoService#deleteById(Long)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivo(@PathVariable Long id) {
        dispositivoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}