# app.py
"""
Módulo principal de la aplicación Flask para el sistema de alertas SIMCII.
"""

from flask import Flask, jsonify, Response, request
from flask_cors import CORS
import logging
import threading
import time
import requests
import sys
import os
import csv
import io
from datetime import datetime, timedelta
import random

# Añadir el directorio actual al path para imports
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

# Importar configuración
from config import config

# Configurar logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('logs/app.log', encoding='utf-8')
    ]
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
app.config.from_object(config['default'])
CORS(app)  # Habilitar CORS para todas las rutas

# Crear directorio de logs si no existe
os.makedirs('logs', exist_ok=True)

# Simulación de data_store para alertas
class MockDataStore:
    def __init__(self):
        self.simulacion_activa = True
        self.dispositivos_simulados = []
        self.alertas = self._generar_alertas_iniciales()
        self.estadisticas = {}
    
    def _generar_alertas_iniciales(self):
        """Genera alertas iniciales para pruebas."""
        alertas = []
        zonas = ['A', 'B', 'C']
        tipos_dispositivo = ['SENSOR_TEMPERATURA', 'SENSOR_HUMEDAD', 'SENSOR_LUZ']
        
        for i in range(5):
            zona = random.choice(zonas)
            tipo = random.choice(tipos_dispositivo)
            
            if tipo == 'SENSOR_TEMPERATURA':
                valor = f"{random.uniform(30, 35):.1f}°C"
                tipo_alerta = 'TEMPERATURA_FUERA_RANGO'
                mensaje = f'Temperatura crítica en Zona {zona}'
            elif tipo == 'SENSOR_HUMEDAD':
                valor = f"{random.uniform(85, 95):.1f}%"
                tipo_alerta = 'HUMEDAD_ALTA'
                mensaje = f'Humedad excesiva en Zona {zona}'
            else:
                valor = f"{random.uniform(50, 150):.1f}lux"
                tipo_alerta = 'LUZ_BAJA'
                mensaje = f'Baja luminosidad en Zona {zona}'
            
            alerta = type('Alerta', (), {
                'id': i + 1,
                'dispositivo_id': random.randint(1, 10),
                'dispositivo_nombre': f'Sensor {tipo.split("_")[-1]} {zona}{i+1}',
                'zona': zona,
                'tipo_alerta': tipo_alerta,
                'valor': valor,
                'umbral_min': 20.0 if tipo == 'SENSOR_TEMPERATURA' else 40.0,
                'umbral_max': 28.0 if tipo == 'SENSOR_TEMPERATURA' else 80.0,
                'mensaje': mensaje,
                'fecha_creacion': (datetime.now() - timedelta(hours=random.randint(0, 24))).isoformat(),
                'activa': True,
                'nivel_criticidad': random.randint(1, 3)
            })()
            
            alertas.append(alerta)
        
        return alertas
    
    def obtener_alertas_activas(self):
        """Obtiene alertas activas."""
        return [a for a in self.alertas if getattr(a, 'activa', False)]
    
    def agregar_alerta(self, alerta):
        """Agrega una nueva alerta."""
        self.alertas.append(alerta)

# Crear data_store global
data_store = MockDataStore()

# =============================================
# RUTAS DE LA APLICACIÓN
# =============================================

@app.route('/')
def root():
    """
    Endpoint raíz que proporciona información del servicio.
    """
    return jsonify({
        "servicio": "Modulo Python - Alertas SIMCII",
        "version": "4.0.0",
        "estado": "activo",
        "modo": "MODO_DATOS_SIMULADOS",
        "timestamp": datetime.now().isoformat(),
        "endpoints_disponibles": {
            "raiz": "/",
            "health": "/health",
            "alertas_csv": "/api/alertas/csv",
            "alertas_json": "/api/alertas",
            "test": "/api/alertas/test",
            "simular_alerta": "/api/alertas/simular (POST)"
        }
    })

@app.route('/health')
def health_check():
    """
    Health check endpoint para monitoreo.
    """
    return jsonify({
        "status": "healthy",
        "service": "python-alertas",
        "timestamp": datetime.now().isoformat(),
        "alertas_activas": len(data_store.obtener_alertas_activas()),
        "total_alertas": len(data_store.alertas)
    })

@app.route('/api/alertas/csv', methods=['GET'])
def exportar_reporte_csv():
    """
    Exporta un reporte de alertas en formato CSV.
    """
    logger.info(f"Solicitud de reporte CSV recibida desde {request.remote_addr}")
    
    try:
        output = io.StringIO()
        writer = csv.writer(output, delimiter=',', quoting=csv.QUOTE_MINIMAL)
        
        # Escribir encabezados
        writer.writerow([
            'ID', 'Dispositivo ID', 'Dispositivo Nombre', 'Zona', 
            'Tipo Alerta', 'Valor', 'Umbral Min', 'Umbral Max', 
            'Mensaje', 'Fecha Creacion', 'Activa', 'Nivel Criticidad'
        ])
        
        # Escribir alertas
        alertas_escritas = 0
        for alerta in data_store.alertas:
            writer.writerow([
                getattr(alerta, 'id', 'N/A'),
                getattr(alerta, 'dispositivo_id', 'N/A'),
                getattr(alerta, 'dispositivo_nombre', 'N/A'),
                getattr(alerta, 'zona', 'N/A'),
                getattr(alerta, 'tipo_alerta', 'N/A'),
                getattr(alerta, 'valor', 'N/A'),
                getattr(alerta, 'umbral_min', 'N/A'),
                getattr(alerta, 'umbral_max', 'N/A'),
                getattr(alerta, 'mensaje', 'N/A'),
                getattr(alerta, 'fecha_creacion', datetime.now().isoformat()),
                "Si" if getattr(alerta, 'activa', False) else "No",
                getattr(alerta, 'nivel_criticidad', 'N/A')
            ])
            alertas_escritas += 1
        
        # Si no hay alertas, escribir una fila informativa
        if alertas_escritas == 0:
            timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            writer.writerow([
                '--', '--', '--', '--', 'INFORMATIVO', '--', 
                '--', '--', f'No hay alertas registradas al {timestamp}', 
                datetime.now().isoformat(), 'No', 'INFORMATIVO'
            ])
        
        output.seek(0)
        csv_content = output.getvalue()
        
        # Generar nombre del archivo
        filename = f"reporte_alertas_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        
        logger.info(f"Reporte CSV generado: {filename} ({len(csv_content)} bytes, {alertas_escritas} alertas)")
        
        # Crear respuesta
        response = Response(
            csv_content,
            mimetype="text/csv; charset=utf-8",
            headers={
                "Content-Disposition": f"attachment; filename={filename}",
                "Content-Type": "text/csv; charset=utf-8",
                "Access-Control-Allow-Origin": "*"
            }
        )
        
        return response
        
    except Exception as e:
        logger.error(f"Error al generar reporte CSV: {str(e)}", exc_info=True)
        
        # Generar CSV de error
        output = io.StringIO()
        writer = csv.writer(output)
        writer.writerow(['Error', 'Mensaje', 'Timestamp'])
        writer.writerow(['ERROR', f'Error al generar reporte: {str(e)}', datetime.now().isoformat()])
        output.seek(0)
        
        return Response(
            output.getvalue(),
            mimetype="text/csv; charset=utf-8",
            headers={
                "Content-Disposition": "attachment; filename=error_report.csv",
                "Access-Control-Allow-Origin": "*"
            }
        )

@app.route('/api/alertas', methods=['GET'])
def obtener_alertas():
    """
    Obtiene todas las alertas en formato JSON.
    """
    try:
        alertas_lista = []
        for alerta in data_store.alertas:
            alertas_lista.append({
                'id': getattr(alerta, 'id', None),
                'dispositivo_id': getattr(alerta, 'dispositivo_id', None),
                'dispositivo_nombre': getattr(alerta, 'dispositivo_nombre', ''),
                'zona': getattr(alerta, 'zona', ''),
                'tipo_alerta': getattr(alerta, 'tipo_alerta', ''),
                'valor': getattr(alerta, 'valor', ''),
                'mensaje': getattr(alerta, 'mensaje', ''),
                'fecha_creacion': getattr(alerta, 'fecha_creacion', datetime.now().isoformat()),
                'activa': getattr(alerta, 'activa', False),
                'nivel_criticidad': getattr(alerta, 'nivel_criticidad', 1)
            })
        
        return jsonify({
            'status': 'ok',
            'count': len(alertas_lista),
            'alertas_activas': len(data_store.obtener_alertas_activas()),
            'alertas': alertas_lista,
            'timestamp': datetime.now().isoformat()
        })
    
    except Exception as e:
        logger.error(f"Error al obtener alertas: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/alertas/test', methods=['GET'])
def test_endpoint():
    """
    Endpoint de prueba para verificar que el servicio está funcionando.
    """
    return jsonify({
        "status": "ok",
        "message": "Servicio Python de Alertas funcionando correctamente",
        "timestamp": datetime.now().isoformat(),
        "servidor": {
            "host": app.config['HOST'],
            "port": app.config['PORT'],
            "debug": app.config['DEBUG']
        },
        "alertas": {
            "total": len(data_store.alertas),
            "activas": len(data_store.obtener_alertas_activas())
        }
    })

@app.route('/api/alertas/simular', methods=['POST'])
def simular_alerta():
    """
    Endpoint para simular una nueva alerta (para testing).
    """
    try:
        # Generar nueva alerta
        zonas = ['A', 'B', 'C']
        tipos = ['TEMPERATURA_ALTA', 'TEMPERATURA_BAJA', 'HUMEDAD_ALTA', 'HUMEDAD_BAJA', 'LUZ_BAJA']
        
        nueva_alerta = type('Alerta', (), {
            'id': max([getattr(a, 'id', 0) for a in data_store.alertas] or [0]) + 1,
            'dispositivo_id': random.randint(1, 20),
            'dispositivo_nombre': f'Sensor {random.choice(["Temp", "Hum", "Luz"])} {random.choice(zonas)}{random.randint(1, 5)}',
            'zona': random.choice(zonas),
            'tipo_alerta': random.choice(tipos),
            'valor': f"{random.uniform(20, 40):.1f}{'°C' if 'TEMPERATURA' in random.choice(tipos) else '%'}",
            'umbral_min': 20.0,
            'umbral_max': 30.0,
            'mensaje': f'Alerta simulada en Zona {random.choice(zonas)}',
            'fecha_creacion': datetime.now().isoformat(),
            'activa': True,
            'nivel_criticidad': random.randint(1, 3)
        })()
        
        data_store.agregar_alerta(nueva_alerta)
        
        logger.info(f"Nueva alerta simulada: {nueva_alerta.dispositivo_nombre}")
        
        return jsonify({
            'status': 'ok',
            'message': 'Alerta simulada creada exitosamente',
            'alerta': {
                'id': nueva_alerta.id,
                'dispositivo': nueva_alerta.dispositivo_nombre,
                'tipo': nueva_alerta.tipo_alerta,
                'valor': nueva_alerta.valor,
                'zona': nueva_alerta.zona
            },
            'total_alertas': len(data_store.alertas)
        })
    
    except Exception as e:
        logger.error(f"Error al simular alerta: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/alertas/limpiar', methods=['POST'])
def limpiar_alertas():
    """
    Limpia todas las alertas (para testing).
    """
    try:
        alertas_eliminadas = len(data_store.alertas)
        data_store.alertas = []
        
        # Regenerar algunas alertas básicas
        data_store._generar_alertas_iniciales()
        
        return jsonify({
            'status': 'ok',
            'message': f'Se eliminaron {alertas_eliminadas} alertas',
            'nuevas_alertas': len(data_store.alertas)
        })
    
    except Exception as e:
        logger.error(f"Error al limpiar alertas: {e}")
        return jsonify({'error': str(e)}), 500

# =============================================
# FUNCIONES DE FONDO
# =============================================

def background_simulation():
    """
    Simulación en segundo plano que actualiza alertas periódicamente.
    """
    logger.info("Iniciando simulación en segundo plano")
    
    while True:
        try:
            # Simular cambios en alertas (activar/desactivar aleatoriamente)
            for alerta in data_store.alertas:
                if random.random() < 0.1:  # 10% de probabilidad de cambiar estado
                    alerta.activa = not getattr(alerta, 'activa', True)
            
            # Registrar estado cada minuto
            logger.debug(f"Simulación activa. Alertas: {len(data_store.alertas)}, Activas: {len(data_store.obtener_alertas_activas())}")
            
            time.sleep(60)  # Esperar 60 segundos
        
        except Exception as e:
            logger.error(f"Error en simulación en segundo plano: {e}")
            time.sleep(30)

def obtener_dispositivos_desde_java():
    """
    Intenta obtener dispositivos desde el servicio Java.
    """
    try:
        java_url = app.config['JAVA_SERVICE_URL']
        logger.info(f"Intentando conectar con servicio Java: {java_url}")
        
        response = requests.get(f'{java_url}/api/dispositivos', timeout=5)
        
        if response.status_code == 200:
            dispositivos = response.json()
            logger.info(f"Conectado a Java. Dispositivos obtenidos: {len(dispositivos)}")
            return dispositivos
        else:
            logger.warning(f"Java service respondió con código: {response.status_code}")
    
    except requests.exceptions.ConnectionError:
        logger.warning("No se pudo conectar con el servicio Java. Usando datos simulados.")
    except Exception as e:
        logger.error(f"Error al obtener dispositivos de Java: {e}")
    
    return []

# =============================================
# INICIALIZACIÓN
# =============================================

def init_app():
    """
    Inicializa la aplicación y componentes del sistema.
    """
    logger.info("=" * 60)
    logger.info("INICIANDO SERVICIO PYTHON DE ALERTAS SIMCII")
    logger.info("=" * 60)
    
    # Intentar conectar con Java
    dispositivos = obtener_dispositivos_desde_java()
    if dispositivos:
        logger.info(f"Modo: DATOS_REALES - Conectado a Java Service")
    else:
        logger.info(f"Modo: DATOS_SIMULADOS - Usando datos de prueba")
    
    # Iniciar simulación en segundo plano
    hilo_simulacion = threading.Thread(target=background_simulation)
    hilo_simulacion.daemon = True
    hilo_simulacion.start()
    
    logger.info(f"Servidor Flask iniciado en: http://{app.config['HOST']}:{app.config['PORT']}")
    logger.info(f"Endpoints disponibles:")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/health")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/api/alertas/csv")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/api/alertas")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/api/alertas/test")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/api/alertas/simular (POST)")
    logger.info(f"  • http://{app.config['HOST']}:{app.config['PORT']}/api/alertas/limpiar (POST)")
    logger.info("=" * 60)

# Inicializar la aplicación
init_app()

if __name__ == '__main__':
    try:
        app.run(
            host=app.config['HOST'], 
            port=app.config['PORT'], 
            debug=app.config['DEBUG'],
            use_reloader=False
        )
    except KeyboardInterrupt:
        logger.info("Servidor detenido por el usuario")
    except Exception as e:
        logger.error(f"Error al iniciar servidor: {e}")