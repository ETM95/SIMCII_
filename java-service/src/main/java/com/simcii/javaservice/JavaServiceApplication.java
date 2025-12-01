package com.simcii.javaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Java Service del sistema SIMCII.
 * Punto de entrada para el microservicio de gestión de dispositivos IoT que
 * maneja sensores y actuadores del invernadero inteligente.
 * 
 * <p>Esta aplicación proporciona:</p>
 * <ul>
 *   <li>Gestión CRUD de dispositivos (sensores y actuadores)</li>
 *   <li>Registro automático de lecturas cada 10 segundos</li>
 *   <li>Configuración de umbrales de alerta</li>
 *   <li>API REST para operaciones de monitoreo y control</li>
 *   <li>Comunicación con el módulo Python para análisis de datos</li>
 * </ul>
 * 
 * <p>Tecnologías utilizadas:</p>
 * <ul>
 *   <li>Spring Boot 3.x</li>
 *   <li>Spring Data JPA</li>
 *   <li>PostgreSQL</li>
 *   <li>Spring Scheduling</li>
 *   <li>RESTful Web Services</li>
 * </ul>
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @SpringBootApplication
 * @EnableScheduling
 * @see <a href="http://localhost:8080/api/dispositivos">API Dispositivos</a>
 * @see <a href="http://localhost:8080/api/lecturas">API Lecturas</a>
 * @see <a href="http://localhost:8080/api/alertas">API Alertas</a>
 */
@SpringBootApplication
@EnableScheduling
public class JavaServiceApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     * Configura y lanza el servidor embebido Tomcat en el puerto 8080 por defecto.
     * 
     * @param args Argumentos de línea de comandos para la configuración de la aplicación
     */
    public static void main(String[] args) {
        SpringApplication.run(JavaServiceApplication.class, args);
    }
}