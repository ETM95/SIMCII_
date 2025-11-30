# data_store.py
from typing import List, Dict, Any
from datetime import datetime
import threading
from models.models import Dispositivo, Alerta
from patterns.patterns import GestorEventos

class DataStore:
    """
    Almacén centralizado de datos para el sistema de alertas.
    
    Esta clase gestiona el estado de la aplicación, incluyendo dispositivos,
    alertas y estadísticas, proporcionando sincronización thread-safe.
    
    Attributes:
        alertas: Lista de alertas generadas por el sistema
        dispositivos_simulados: Lista de dispositivos monitoreados
        estadisticas: Diccionario con estadísticas del sistema
        ultima_actualizacion_dispositivos: Timestamp de la última actualización
        simulacion_activa: Estado del procesamiento de datos
        gestor_eventos: Instancia para gestionar eventos del sistema
        _lock: Lock para sincronización de threads
    """
    
    def __init__(self, gestor_eventos: GestorEventos):
        """Inicializa el almacén de datos con el gestor de eventos proporcionado."""
        self.alertas: List[Alerta] = []
        self.dispositivos_simulados: List[Dispositivo] = []
        self.estadisticas = {}
        self.ultima_actualizacion_dispositivos = None
        self.simulacion_activa = False
        self.gestor_eventos = gestor_eventos
        self._lock = threading.Lock()

    def actualizar_dispositivos_simulados(self, dispositivos_reales: List[Dict[str, Any]]):
        """
        Actualiza la lista de dispositivos simulados con datos reales desde Java.
        
        Args:
            dispositivos_reales: Lista de diccionarios con datos de dispositivos
        """
        if not dispositivos_reales:
            logger.warning("No se recibieron dispositivos desde Java")
            return

        with self._lock:
            nuevos_dispositivos = []
            for dispositivo_data in dispositivos_reales:
                try:
                    dispositivo = Dispositivo(
                        id=dispositivo_data.get('id'),
                        nombre=dispositivo_data.get('nombre', 'Dispositivo Desconocido'),
                        tipo=dispositivo_data.get('tipo', 'SENSOR_GENERICO'),
                        ubicacion=dispositivo_data.get('ubicacion', 'A'),
                        activo=dispositivo_data.get('activo', True),
                        rango_min=None,
                        rango_max=None,
                        unidad_medida=self._obtener_unidad_medida(dispositivo_data.get('tipo'))
                    )
                    nuevos_dispositivos.append(dispositivo)
                except Exception as e:
                    print(f"Error creando dispositivo: {e}")

            self.dispositivos_simulados = nuevos_dispositivos
            self.ultima_actualizacion_dispositivos = datetime.now()
            print(f"Actualizados {len(nuevos_dispositivos)} dispositivos en DataStore")

    def _obtener_unidad_medida(self, tipo_dispositivo: str) -> str:
        """
        Obtiene la unidad de medida basada en el tipo de dispositivo.
        
        Args:
            tipo_dispositivo: Tipo del dispositivo (SENSOR_TEMPERATURA, etc.)
            
        Returns:
            String con la unidad de medida correspondiente
        """
        unidades = {
            'SENSOR_TEMPERATURA': '°C',
            'SENSOR_HUMEDAD': '%',
            'SENSOR_LUZ': 'lux',
            'ACTUADOR': 'estado'
        }
        return unidades.get(tipo_dispositivo, 'unidad')

    def obtener_alertas_activas(self) -> List[Alerta]:
        """
        Obtiene la lista de alertas activas en el sistema.
        
        Returns:
            Lista de objetos Alerta que están activos
        """
        with self._lock:
            return [alerta for alerta in self.alertas if alerta.activa]

    def agregar_alerta(self, alerta: Alerta):
        """
        Agrega una nueva alerta al sistema y publica el evento.
        
        Args:
            alerta: Objeto Alerta a agregar
        """
        with self._lock:
            self.alertas.append(alerta)
        self.gestor_eventos.publicar_evento("nueva_alerta", alerta.to_dict())

    def limpiar_alertas_antiguas(self):
        """Desactiva alertas que han estado activas por más de 5 minutos."""
        ahora = datetime.now()
        with self._lock:
            for alerta in self.alertas:
                if alerta.activa:
                    if isinstance(alerta.fecha_creacion, str):
                        fecha_creacion = datetime.fromisoformat(alerta.fecha_creacion.replace('Z', '+00:00'))
                    else:
                        fecha_creacion = alerta.fecha_creacion
                    
                    if (ahora - fecha_creacion).total_seconds() > 300:
                        alerta.activa = False
                        self.gestor_eventos.publicar_evento("alerta_resuelta", alerta.to_dict())