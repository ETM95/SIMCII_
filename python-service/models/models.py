# models.py
"""
Modelos de datos para el sistema de alertas.

Este módulo define las clases principales que representan
los dispositivos y alertas del sistema.
"""

from typing import Optional
from datetime import datetime
import random

class Dispositivo:
    """
    Representa un dispositivo del sistema de monitoreo.
    
    Attributes:
        id: Identificador único del dispositivo
        nombre: Nombre descriptivo del dispositivo
        tipo: Tipo de dispositivo (SENSOR_TEMPERATURA, etc.)
        ubicacion: Zona donde se encuentra el dispositivo
        activo: Estado de actividad del dispositivo
        rango_min: Valor mínimo del rango de operación
        rango_max: Valor máximo del rango de operación
        unidad_medida: Unidad de medida de las lecturas
        ultima_lectura: Último valor leído del dispositivo
        probabilidad_alerta: Probabilidad de generar alertas (no usado en modo real)
    """
    
    def __init__(self, id: int, nombre: str, tipo: str, ubicacion: str, 
                 activo: bool = True, rango_min: Optional[float] = None, 
                 rango_max: Optional[float] = None, unidad_medida: Optional[str] = None):
        """Inicializa un dispositivo con sus propiedades básicas."""
        self.id = id
        self.nombre = nombre
        self.tipo = tipo
        self.ubicacion = ubicacion
        self.activo = activo
        self.rango_min = rango_min
        self.rango_max = rango_max
        self.unidad_medida = unidad_medida
        self.ultima_lectura = None
        self.probabilidad_alerta = 0.0

    def to_dict(self):
        """
        Convierte el dispositivo a diccionario para serialización.
        
        Returns:
            Diccionario con todas las propiedades del dispositivo
        """
        return {
            'id': self.id,
            'nombre': self.nombre,
            'tipo': self.tipo,
            'ubicacion': self.ubicacion,
            'activo': self.activo,
            'rango_min': self.rango_min,
            'rango_max': self.rango_max,
            'unidad_medida': self.unidad_medida,
            'ultima_lectura': self.ultima_lectura
        }

class Alerta:
    """
    Representa una alerta generada por el sistema.
    
    Attributes:
        id: Identificador único de la alerta
        dispositivo_id: ID del dispositivo que generó la alerta
        dispositivo_nombre: Nombre del dispositivo
        valor: Valor que desencadenó la alerta
        tipo_alerta: Tipo de alerta generada
        mensaje: Mensaje descriptivo de la alerta
        zona: Zona donde se generó la alerta
        umbral_min: Umbral mínimo violado
        umbral_max: Umbral máximo violado
        fecha_creacion: Timestamp de creación de la alerta
        activa: Estado de actividad de la alerta
        nivel_criticidad: Nivel de criticidad calculado
    """
    
    def __init__(self, id: int, dispositivo_id: int, dispositivo_nombre: str, 
                 valor: float, tipo_alerta: str, mensaje: str, zona: str,
                 umbral_min: Optional[float] = None, umbral_max: Optional[float] = None):
        """Inicializa una alerta con sus propiedades básicas."""
        self.id = id
        self.dispositivo_id = dispositivo_id
        self.dispositivo_nombre = dispositivo_nombre
        self.valor = valor
        self.tipo_alerta = tipo_alerta
        self.mensaje = mensaje
        self.zona = zona
        self.umbral_min = umbral_min
        self.umbral_max = umbral_max
        self.fecha_creacion = datetime.now().isoformat()
        self.activa = True
        self.nivel_criticidad = self._calcular_criticidad()

    def _calcular_criticidad(self):
        """
        Calcula el nivel de criticidad basado en el tipo de alerta y valores.
        
        Returns:
            Entero representando el nivel de criticidad (1-3)
        """
        if self.tipo_alerta == "TEMPERATURA_FUERA_RANGO":
            return 3 if (self.valor > 32 or self.valor < 14) else 2
        elif self.tipo_alerta == "HUMEDAD_FUERA_RANGO":
            return 3 if (self.valor > 80 or self.valor < 25) else 2
        elif self.tipo_alerta == "LUZ_FUERA_RANGO":
            return 3 if (self.valor > 1000 or self.valor < 50) else 2
        else:
            return 1

    def to_dict(self):
        """
        Convierte la alerta a diccionario para serialización.
        
        Returns:
            Diccionario con todas las propiedades de la alerta
        """
        return {
            'id': self.id,
            'dispositivo_id': self.dispositivo_id,
            'dispositivo_nombre': self.dispositivo_nombre,
            'valor': self.valor,
            'tipo_alerta': self.tipo_alerta,
            'mensaje': self.mensaje,
            'zona': self.zona,
            'umbral_min': self.umbral_min,
            'umbral_max': self.umbral_max,
            'fecha_creacion': self.fecha_creacion,
            'activa': self.activa,
            'nivel_criticidad': self.nivel_criticidad
        }

class AlertaCritica(Alerta):
    """
    Representa una alerta crítica con nivel máximo de criticidad.
    
    Extiende la clase Alerta con funcionalidades específicas para alertas críticas.
    """
    
    def __init__(self, *args, **kwargs):
        """Inicializa una alerta crítica con nivel de criticidad máximo."""
        super().__init__(*args, **kwargs)
        self.nivel_criticidad = 4

    def notificar(self, mensaje: str):
        """
        Notifica una alerta crítica mediante salida estándar.
        
        Args:
            mensaje: Mensaje de la alerta crítica
        """
        print(f"ALERTA CRITICA: {mensaje}")

    def registrar_evento(self, evento: str):
        """
        Registra un evento relacionado con la alerta crítica.
        
        Args:
            evento: Descripción del evento a registrar
        """
        print(f"Evento registrado: {evento}")