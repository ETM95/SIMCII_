package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de entidades Dispositivo en la base de datos.
 * Proporciona operaciones de acceso a datos para todos los tipos de dispositivos
 * (sensores y actuadores) del sistema SIMCII, incluyendo consultas personalizadas
 * basadas en el estado y tipo de dispositivo.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Repository
 * @see Dispositivo
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    /**
     * Encuentra todos los dispositivos que están actualmente activos en el sistema.
     * Los dispositivos activos son aquellos que están operativos y monitoreando/controlando
     * las condiciones del invernadero.
     * 
     * @return List<Dispositivo> con todos los dispositivos activos
     */
    List<Dispositivo> findByActivoTrue();
    
    // @Query("SELECT d FROM Dispositivo d WHERE d INSTANCE OF Sensor")
    // List<Dispositivo> findAllSensores();
    
    /**
     * Encuentra todos los actuadores registrados en el sistema.
     * Utiliza una consulta JPQL personalizada con TYPE() para filtrar por la clase
     * concreta Actuador, aprovechando la estrategia de herencia JOINED.
     * 
     * @return List<Dispositivo> con todos los actuadores del sistema
     * @see com.simcii.javaservice.models.Actuador
     */
    @Query("SELECT d FROM Dispositivo d WHERE TYPE(d) = Actuador")
    List<Dispositivo> findAllActuadores();
}