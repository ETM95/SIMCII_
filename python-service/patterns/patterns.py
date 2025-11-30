# patterns.py
"""
Implementación de patrones de diseño para el sistema de eventos.

Este módulo contiene las implementaciones de Observer y Subject para
la gestión de eventos en el sistema de alertas.
"""

from abc import ABC, abstractmethod
from typing import List, Dict, Any
import threading
from queue import Queue
import time

class ObservadorEvento(ABC):
    """Interfaz abstracta para observadores de eventos."""
    
    @abstractmethod
    def actualizar(self, evento: str, datos: Dict[str, Any]):
        """
        Método abstracto para actualizar al observador con un nuevo evento.
        
        Args:
            evento: Tipo de evento ocurrido
            datos: Datos asociados al evento
        """
        pass

class SujetoEvento(ABC):
    """Clase abstracta para sujetos que notifican eventos a observadores."""
    
    def __init__(self):
        """Inicializa el sujeto con una lista vacía de observadores."""
        self._observadores: List[ObservadorEvento] = []

    def agregar_observador(self, observador: ObservadorEvento):
        """
        Agrega un observador a la lista de notificación.
        
        Args:
            observador: Instancia de ObservadorEvento a agregar
        """
        self._observadores.append(observador)

    def eliminar_observador(self, observador: ObservadorEvento):
        """
        Elimina un observador de la lista de notificación.
        
        Args:
            observador: Instancia de ObservadorEvento a eliminar
        """
        self._observadores.remove(observador)

    def notificar_observadores(self, evento: str, datos: Dict[str, Any]):
        """
        Notifica a todos los observadores sobre un evento.
        
        Args:
            evento: Tipo de evento a notificar
            datos: Datos asociados al evento
        """
        for observador in self._observadores:
            observador.actualizar(evento, datos)

class GestorEventos(SujetoEvento):
    """
    Gestor de eventos con procesamiento asíncrono.
    
    Esta clase gestiona la publicación y procesamiento de eventos
    de manera asíncrona utilizando una cola y threads.
    """
    
    def __init__(self):
        """Inicializa el gestor de eventos con cola y estado de procesamiento."""
        super().__init__()
        self._cola_eventos = Queue()
        self._procesando = False
        self._hilo_procesador = None

    def publicar_evento(self, evento: str, datos: Dict[str, Any]):
        """
        Publica un nuevo evento en la cola para procesamiento asíncrono.
        
        Args:
            evento: Tipo de evento a publicar
            datos: Datos asociados al evento
        """
        self._cola_eventos.put((evento, datos))
        if not self._procesando:
            self._iniciar_procesador()

    def _iniciar_procesador(self):
        """Inicia el procesador de eventos en un thread separado."""
        self._procesando = True
        self._hilo_procesador = threading.Thread(target=self._procesar_eventos)
        self._hilo_procesador.daemon = True
        self._hilo_procesador.start()

    def _procesar_eventos(self):
        """Procesa eventos de la cola de manera asíncrona."""
        while not self._cola_eventos.empty():
            try:
                evento, datos = self._cola_eventos.get_nowait()
                self.notificar_observadores(evento, datos)
                self._cola_eventos.task_done()
            except:
                break
        self._procesando = False

class ObservadorAlertas(ObservadorEvento):
    """
    Observador especializado en eventos de alertas.
    
    Maneja eventos relacionados con la creación y resolución de alertas.
    """
    
    def __init__(self, data_store):
        """
        Inicializa el observador con referencia al data store.
        
        Args:
            data_store: Instancia de DataStore para acceso a datos
        """
        self.data_store = data_store

    def actualizar(self, evento: str, datos: Dict[str, Any]):
        """
        Procesa eventos de alertas según su tipo.
        
        Args:
            evento: Tipo de evento (nueva_alerta, alerta_resuelta)
            datos: Datos de la alerta
        """
        if evento == "nueva_alerta":
            self._manejar_nueva_alerta(datos)
        elif evento == "alerta_resuelta":
            self._manejar_alerta_resuelta(datos)

    def _manejar_nueva_alerta(self, datos: Dict[str, Any]):
        """Maneja eventos de nuevas alertas."""
        print(f"Nueva alerta recibida: {datos.get('tipo_alerta')} - {datos.get('mensaje')}")

    def _manejar_alerta_resuelta(self, datos: Dict[str, Any]):
        """Maneja eventos de alertas resueltas."""
        print(f"Alerta resuelta: {datos.get('id')}")

class ObservadorEstadisticas(ObservadorEvento):
    """
    Observador especializado en eventos de estadísticas.
    
    Maneja eventos relacionados con actualizaciones de estadísticas.
    """
    
    def __init__(self, data_store):
        """
        Inicializa el observador con referencia al data store.
        
        Args:
            data_store: Instancia de DataStore para acceso a datos
        """
        self.data_store = data_store

    def actualizar(self, evento: str, datos: Dict[str, Any]):
        """
        Procesa eventos de estadísticas.
        
        Args:
            evento: Tipo de evento (nueva_lectura)
            datos: Datos de la lectura
        """
        if evento == "nueva_lectura":
            self._actualizar_estadisticas(datos)

    def _actualizar_estadisticas(self, datos: Dict[str, Any]):
        """Actualiza estadísticas basadas en nuevas lecturas."""
        pass