package com.simcii.javaservice.services;

import com.simcii.javaservice.models.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;  // Cambiado de javax.annotation

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio en memoria para la gestión de dispositivos, lecturas, alertas y umbrales.
 * Proporciona una implementación en memoria para desarrollo y testing del sistema SIMCII,
 * inicializando automáticamente dispositivos de ejemplo para las tres zonas del invernadero.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Dispositivo
 * @see Lectura
 * @see Alerta
 * @see Umbral
 */
@Service
public class DispositivoMemoryService {
    
    /**
     * Mapa concurrente que almacena todos los dispositivos por su ID.
     * Incluye sensores y actuadores de todas las zonas del invernadero.
     */
    private final Map<Long, Dispositivo> dispositivos = new ConcurrentHashMap<>();

    /**
     * Mapa concurrente que almacena el historial de lecturas por dispositivo ID.
     * Cada dispositivo tiene su propia lista de lecturas históricas.
     */
    private final Map<Long, List<Lectura>> lecturas = new ConcurrentHashMap<>();

    /**
     * Mapa concurrente que almacena alertas generadas por dispositivo ID.
     * Organiza las alertas activas e históricas por cada dispositivo.
     */
    private final Map<Long, List<Alerta>> alertas = new ConcurrentHashMap<>();

    /**
     * Mapa concurrente que almacena umbrales de alerta por dispositivo ID.
     * Configura los rangos aceptables para cada dispositivo monitorizado.
     */
    private final Map<Long, Umbral> umbrales = new ConcurrentHashMap<>();
    
    /**
     * Contador para generar IDs únicos secuenciales para nuevos dispositivos.
     */
    private Long nextId = 1L;
    
    /**
     * Método de inicialización que se ejecuta automáticamente después de la construcción.
     * Crea dispositivos de ejemplo para las tres zonas del invernadero (A, B, C)
     * incluyendo sensores de temperatura, humedad, luz y actuadores de riego,
     * ventilación e iluminación para cada zona.
     * 
     * @PostConstruct
     */
    @PostConstruct
    public void inicializarDispositivos() {
        System.out.println("INICIALIZANDO DISPOSITIVOS EN MEMORIA...");
        
        List<String> zonas = Arrays.asList("A", "B", "C");
        
        for (String zona : zonas) {
            // Sensores para la zona
            crearSensorTemperatura(zona);
            crearSensorHumedad(zona);
            crearSensorLuz(zona);
            
            // Actuadores para la zona
            crearActuadorRiego(zona);
            crearActuadorVentilacion(zona);
            crearActuadorIluminacion(zona);
        }
        
        System.out.println(dispositivos.size() + " dispositivos inicializados en memoria");
        listarDispositivos();
    }
    
    /**
     * Crea y configura un sensor de temperatura para una zona específica.
     * Establece rangos operativos típicos para condiciones de invernadero.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see SensorTemperatura
     */
    private void crearSensorTemperatura(String zona) {
        SensorTemperatura sensor = new SensorTemperatura();
        sensor.setId(nextId++);
        sensor.setNombre("Temperatura Zona " + zona);
        sensor.setDescripcion("Sensor de temperatura para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("TEMPERATURA");
        sensor.setUnidadMedida("°C");
        sensor.setRangoMin(-10.0);
        sensor.setRangoMax(50.0);
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(18.0);
        umbral.setValorMax(28.0);
        umbral.setTipoAlerta("TEMPERATURA_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    /**
     * Crea y configura un sensor de humedad para una zona específica.
     * Establece rangos de humedad relativa óptimos para cultivos.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see SensorHumedad
     */
    private void crearSensorHumedad(String zona) {
        SensorHumedad sensor = new SensorHumedad();
        sensor.setId(nextId++);
        sensor.setNombre("Humedad Zona " + zona);
        sensor.setDescripcion("Sensor de humedad para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("HUMEDAD");
        sensor.setUnidadMedida("%");
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        // Crear umbral por defecto
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(40.0);
        umbral.setValorMax(70.0);
        umbral.setTipoAlerta("HUMEDAD_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    /**
     * Crea y configura un sensor de luz para una zona específica.
     * Configura rangos de intensidad lumínica para fotosíntesis.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see SensorLuz
     */
    private void crearSensorLuz(String zona) {
        SensorLuz sensor = new SensorLuz();
        sensor.setId(nextId++);
        sensor.setNombre("Luz Zona " + zona);
        sensor.setDescripcion("Sensor de luz para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("LUZ");
        sensor.setUnidadMedida("lux");
        sensor.setTipoLuz("VISIBLE");
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        // Crear umbral por defecto
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(200.0);
        umbral.setValorMax(800.0);
        umbral.setTipoAlerta("LUZ_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    /**
     * Crea y configura un actuador de riego para una zona específica.
     * Inicializado en modo automático y estado desactivado.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see Actuador
     */
    private void crearActuadorRiego(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Riego Zona " + zona);
        actuador.setDescripcion("Sistema de riego para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("RIEGO");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    /**
     * Crea y configura un actuador de ventilación para una zona específica.
     * Inicializado en modo automático y estado desactivado.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see Actuador
     */
    private void crearActuadorVentilacion(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Ventilación Zona " + zona);
        actuador.setDescripcion("Sistema de ventilación para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("VENTILACION");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    /**
     * Crea y configura un actuador de iluminación para una zona específica.
     * Inicializado en modo automático y estado desactivado.
     * 
     * @param zona Zona del invernadero (A, B o C)
     * @see Actuador
     */
    private void crearActuadorIluminacion(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Iluminación Zona " + zona);
        actuador.setDescripcion("Sistema de iluminación para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("ILUMINACION");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    /**
     * Obtiene todos los dispositivos registrados en el sistema.
     * 
     * @return List<Dispositivo> con todos los dispositivos disponibles
     */
    public List<Dispositivo> obtenerTodosDispositivos() {
        return new ArrayList<>(dispositivos.values());
    }
    
    /**
     * Busca un dispositivo específico por su identificador único.
     * 
     * @param id Identificador único del dispositivo a buscar
     * @return Optional<Dispositivo> con el dispositivo encontrado o vacío si no existe
     */
    public Optional<Dispositivo> obtenerDispositivoPorId(Long id) {
        return Optional.ofNullable(dispositivos.get(id));
    }
    
    /**
     * Obtiene el historial completo de lecturas para un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Lectura> con todas las lecturas del dispositivo
     */
    public List<Lectura> obtenerLecturasPorDispositivo(Long dispositivoId) {
        return lecturas.getOrDefault(dispositivoId, new ArrayList<>());
    }
    
    /**
     * Obtiene las lecturas más recientes de un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @param cantidad Número de lecturas recientes a retornar
     * @return List<Lectura> con las lecturas más recientes ordenadas descendente
     */
    public List<Lectura> obtenerUltimasLecturas(Long dispositivoId, int cantidad) {
        List<Lectura> lecturasDispositivo = lecturas.getOrDefault(dispositivoId, new ArrayList<>());
        return lecturasDispositivo.stream()
                .sorted((l1, l2) -> l2.getFechaHora().compareTo(l1.getFechaHora()))
                .limit(cantidad)
                .toList();
    }
    
    /**
     * Agrega una nueva lectura al historial de un dispositivo.
     * 
     * @param lectura Lectura a agregar al sistema
     */
    public void agregarLectura(Lectura lectura) {
        Long dispositivoId = lectura.getDispositivo().getId();
        lecturas.computeIfAbsent(dispositivoId, k -> new ArrayList<>()).add(lectura);
    }
    
    /**
     * Obtiene todas las alertas activas en el sistema.
     * 
     * @return List<Alerta> con todas las alertas activas
     */
    public List<Alerta> obtenerAlertasActivas() {
        return alertas.values().stream()
                .flatMap(List::stream)
                .filter(Alerta::getActiva)
                .toList();
    }
    
    /**
     * Obtiene las alertas activas para un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Alerta> con las alertas activas del dispositivo
     */
    public List<Alerta> obtenerAlertasPorDispositivo(Long dispositivoId) {
        return alertas.getOrDefault(dispositivoId, new ArrayList<>()).stream()
                .filter(Alerta::getActiva)
                .toList();
    }
    
    /**
     * Agrega una nueva alerta al sistema.
     * 
     * @param alerta Alerta a agregar
     */
    public void agregarAlerta(Alerta alerta) {
        Long dispositivoId = alerta.getDispositivo().getId();
        alertas.computeIfAbsent(dispositivoId, k -> new ArrayList<>()).add(alerta);
    }
    
    /**
     * Obtiene el umbral configurado para un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return Optional<Umbral> con el umbral del dispositivo o vacío si no existe
     */
    public Optional<Umbral> obtenerUmbralPorDispositivo(Long dispositivoId) {
        return Optional.ofNullable(umbrales.get(dispositivoId));
    }
    
    /**
     * Lista todos los dispositivos en la consola para propósitos de debugging.
     * Muestra nombre e ID de cada dispositivo registrado en el sistema.
     */
    private void listarDispositivos() {
        System.out.println("\n📋 LISTA DE DISPOSITIVOS CREADOS:");
        dispositivos.values().forEach(disp -> {
            System.out.println(" - " + disp.getNombre() + " (ID: " + disp.getId() + ")");
        });
        System.out.println("🎯 Total: " + dispositivos.size() + " dispositivos\n");
    }
}