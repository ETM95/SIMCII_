package com.simcii.javaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración Spring para el cliente REST del servicio Java SIMCII.
 * Configura y provee instancias de RestTemplate para la comunicación HTTP
 * con otros microservicios del ecosistema, específicamente el servicio Python.
 * 
 * <p>Esta configuración es esencial para:</p>
 * <ul>
 *   <li>Comunicación asincrónica con el módulo Python</li>
 *   <li>Envío de eventos para análisis de datos y alertas</li>
 *   <li>Integración entre los microservicios del sistema SIMCII</li>
 * </ul>
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Configuration
 * @see RestTemplate
 * @see com.simcii.javaservice.services.EventoService
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Crea y configura una instancia de RestTemplate para comunicación HTTP.
     * El RestTemplate es el cliente HTTP principal de Spring para realizar
     * peticiones RESTful a servicios externos.
     * 
     * <p>Características de esta configuración:</p>
     * <ul>
     *   <li>Instancia básica sin configuración adicional personalizada</li>
     *   <li>Adecuada para desarrollo y entornos con timeout por defecto</li>
     *   <li>Puede extenderse con interceptores, converters personalizados</li>
     * </ul>
     * 
     * @return RestTemplate instancia configurada para uso en toda la aplicación
     * @Bean
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html">RestTemplate Documentation</a>
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}