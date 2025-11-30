# config.py
"""
Configuración de la aplicación para diferentes entornos.

Este módulo define las configuraciones para desarrollo, producción
y despliegue en Docker.
"""

import os

class Config:
    """Configuración base para todos los entornos."""
    
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'dev-secret-key'
    JAVA_SERVICE_URL = os.environ.get('JAVA_SERVICE_URL') or 'http://localhost:8080'
    SIMULATION_INTERVAL = int(os.environ.get('SIMULATION_INTERVAL') or 30)
    DEBUG = os.environ.get('DEBUG', 'False').lower() == 'true'
    HOST = os.environ.get('HOST', '0.0.0.0')
    PORT = int(os.environ.get('PORT', 8000))

class DevelopmentConfig(Config):
    """Configuración para entorno de desarrollo."""
    DEBUG = True

class ProductionConfig(Config):
    """Configuración para entorno de producción."""
    DEBUG = False

class DockerConfig(Config):
    """Configuración para despliegue en Docker."""
    DEBUG = os.environ.get('DEBUG', 'False').lower() == 'true'
    JAVA_SERVICE_URL = os.environ.get('JAVA_SERVICE_URL') or 'http://java-service:8080'

config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'docker': DockerConfig,
    'default': DockerConfig
}