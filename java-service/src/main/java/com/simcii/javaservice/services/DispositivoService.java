package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión integral de dispositivos en el sistema SIMCII.
 * Proporciona operaciones CRUD y lógica de negocio para todos los tipos de dispositivos
 * (sensores y actuadores) que monitorean y controlan las condiciones del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Dispositivo
 * @see DispositivoRepository
 */
@Service
public class DispositivoService {
    
    /**
     * Repositorio para acceso a datos de dispositivos.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    /**
     * Obtiene todos los dispositivos registrados en el sistema.
     * Incluye tanto sensores como actuadores de todas las zonas del invernadero.
     * 
     * @return List<Dispositivo> con todos los dispositivos disponibles
     * @see DispositivoRepository#findAll()
     */
    public List<Dispositivo> findAll() {
        return dispositivoRepository.findAll();
    }
    
    /**
     * Busca un dispositivo específico por su identificador único.
     * 
     * @param id Identificador único del dispositivo a buscar
     * @return Optional<Dispositivo> con el dispositivo encontrado o vacío si no existe
     * @see DispositivoRepository#findById(Long)
     */
    public Optional<Dispositivo> findById(Long id) {
        return dispositivoRepository.findById(id);
    }
    
    /**
     * Guarda un nuevo dispositivo en el sistema.
     * Establece automáticamente la fecha de creación antes de persistir.
     * 
     * @param dispositivo Dispositivo a guardar en el sistema
     * @return Dispositivo el dispositivo guardado con su ID asignado
     * @throws IllegalArgumentException si el dispositivo es nulo
     * @see DispositivoRepository#save(Object)
     */
    public Dispositivo save(Dispositivo dispositivo) {
        dispositivo.setFechaCreacion(LocalDateTime.now());
        return dispositivoRepository.save(dispositivo);
    }
    
    /**
     * Actualiza la información de un dispositivo existente.
     * Permite modificar propiedades básicas del dispositivo y actualiza
     * automáticamente la fecha de actualización.
     * 
     * @param id Identificador único del dispositivo a actualizar
     * @param dispositivoDetails Objeto con los nuevos datos del dispositivo
     * @return Dispositivo el dispositivo actualizado
     * @throws RuntimeException si no se encuentra el dispositivo con el ID especificado
     * @see DispositivoRepository#save(Object)
     */
    public Dispositivo update(Long id, Dispositivo dispositivoDetails) {
        return dispositivoRepository.findById(id)
            .map(dispositivo -> {
                dispositivo.setNombre(dispositivoDetails.getNombre());
                dispositivo.setDescripcion(dispositivoDetails.getDescripcion());
                dispositivo.setUbicacion(dispositivoDetails.getUbicacion());
                dispositivo.setActivo(dispositivoDetails.getActivo());
                dispositivo.setFechaActualizacion(LocalDateTime.now());
                return dispositivoRepository.save(dispositivo);
            })
            .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));
    }
    
    /**
     * Elimina permanentemente un dispositivo del sistema.
     * Remove el dispositivo y todas sus asociaciones según la configuración de cascada.
     * 
     * @param id Identificador único del dispositivo a eliminar
     * @see DispositivoRepository#deleteById(Long)
     */
    public void deleteById(Long id) {
        dispositivoRepository.deleteById(id);
    }
    
    /**
     * Obtiene todos los dispositivos que están actualmente activos en el sistema.
     * Los dispositivos activos son aquellos que están operativos y participando
     * en el monitoreo o control del invernadero.
     * 
     * @return List<Dispositivo> con todos los dispositivos activos
     * @see DispositivoRepository#findByActivoTrue()
     */
    public List<Dispositivo> findActivos() {
        return dispositivoRepository.findByActivoTrue();
    }
}