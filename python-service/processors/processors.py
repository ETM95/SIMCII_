# processors.py
"""
Procesadores de datos para el sistema de alertas.

Este módulo contiene las clases responsables de procesar datos
y generar alertas basadas en umbrales.
"""

from abc import ABC, abstractmethod
from typing import Dict, Any
from models.models import Dispositivo, Alerta, AlertaCritica
import random

class Procesador(ABC):
    """Interfaz abstracta para procesadores de datos."""
    
    @abstractmethod
    def procesar(self, datos: Dict[str, Any]) -> Dict[str, Any]:
        """
        Procesa los datos de entrada y retorna resultado procesado.
        
        Args:
            datos: Datos de entrada a procesar
            
        Returns:
            Datos procesados
        """
        pass

class ProcesadorUmbrales(Procesador):
    """
    Procesador de umbrales para datos de dispositivos.
    
    Nota: Esta clase se mantiene por compatibilidad pero la lógica
    principal está implementada en app.py
    """
    
    def procesar(self, datos: Dict[str, Any]) -> Dict[str, Any]:
        """
        Procesa datos de umbrales (método de compatibilidad).
        
        Args:
            datos: Datos de entrada a procesar
            
        Returns:
            Mismos datos de entrada sin cambios
        """
        return datos

class GeneradorAlertas:
    """
    Generador de alertas basado en lecturas de dispositivos.
    
    Esta clase se encarga de crear alertas cuando las lecturas
    de dispositivos exceden los umbrales configurados.
    """
    
    def __init__(self):
        """Inicializa el generador con contador de alertas."""
        self.contador_alertas = 0

    def generar_alerta(self, dispositivo: Dispositivo, valor: float, tipo_alerta: str):
        """
        Genera una nueva alerta basada en la lectura del dispositivo.
        
        Args:
            dispositivo: Dispositivo que generó la alerta
            valor: Valor de lectura que desencadenó la alerta
            tipo_alerta: Tipo de alerta a generar
            
        Returns:
            Objeto Alerta o AlertaCritica según la severidad
        """
        self.contador_alertas += 1
        
        mensajes = {
            "SENSOR_TEMPERATURA_MINIMO": [f"Temperatura muy baja: {valor:.1f}°C"],
            "SENSOR_TEMPERATURA_MAXIMO": [f"Temperatura muy alta: {valor:.1f}°C"],
            "SENSOR_HUMEDAD_MINIMO": [f"Humedad muy baja: {valor:.1f}%"],
            "SENSOR_HUMEDAD_MAXIMO": [f"Humedad muy alta: {valor:.1f}%"],
            "SENSOR_LUZ_MINIMO": [f"Intensidad luminica muy baja: {valor:.0f} lux"],
            "SENSOR_LUZ_MAXIMO": [f"Intensidad luminica muy alta: {valor:.0f} lux"],
        }

        mensaje = random.choice(mensajes.get(tipo_alerta, [f"Alerta: {valor} fuera de rango"]))

        es_critica = False
        if tipo_alerta == "SENSOR_TEMPERATURA_MINIMO" and valor < 10:
            es_critica = True
        elif tipo_alerta == "SENSOR_TEMPERATURA_MAXIMO" and valor > 35:
            es_critica = True
        elif tipo_alerta == "SENSOR_HUMEDAD_MINIMO" and valor < 20:
            es_critica = True
        elif tipo_alerta == "SENSOR_HUMEDAD_MAXIMO" and valor > 85:
            es_critica = True

        if es_critica:
            alerta = AlertaCritica(
                id=self.contador_alertas,
                dispositivo_id=dispositivo.id,
                dispositivo_nombre=dispositivo.nombre,
                valor=round(valor, 1),
                tipo_alerta=tipo_alerta,
                mensaje=f"CRITICA: {mensaje}",
                zona=dispositivo.ubicacion,
                umbral_min=dispositivo.rango_min,
                umbral_max=dispositivo.rango_max
            )
            
            alerta.notificar(f"Alerta critica en {dispositivo.ubicacion}: {mensaje}")
            alerta.registrar_evento("Alerta critica generada")
        else:
            alerta = Alerta(
                id=self.contador_alertas,
                dispositivo_id=dispositivo.id,
                dispositivo_nombre=dispositivo.nombre,
                valor=round(valor, 1),
                tipo_alerta=tipo_alerta,
                mensaje=mensaje,
                zona=dispositivo.ubicacion,
                umbral_min=dispositivo.rango_min,
                umbral_max=dispositivo.rango_max
            )

        return alerta