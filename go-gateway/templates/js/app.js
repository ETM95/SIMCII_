// app.js - Actualizado para conectar con Java API
const App = {
    // Configuración de APIs
    JAVA_API_BASE_URL: 'http://localhost:8080/api/dispositivos',
    PYTHON_API_BASE_URL: 'http://localhost:8000/api',
    
    // Estado de la aplicación
    currentEditingDevice: null,
    
    // Inicializar la aplicación
    init: () => {
        App.setupEventListeners();
        App.showDashboard();
        App.updateCurrentTime();
        setInterval(App.updateCurrentTime, 1000);
        App.startAutoUpdates();
    },
    
    // Configurar event listeners
    setupEventListeners: () => {
        const logoutBtn = document.getElementById('btn-logout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', App.handleLogout);
        }
        
        const deviceForm = document.getElementById('device-form');
        if (deviceForm) {
            deviceForm.addEventListener('submit', App.handleDeviceSubmit);
            
            const descInput = document.getElementById('deviceDescription');
            if (descInput) {
                descInput.addEventListener('input', App.updateCharCount);
            }
        }
    },
    
    // Manejar logout
    handleLogout: () => {
        window.location.href = '/logout';
    },
    
    // Manejar envío del formulario de dispositivo
    handleDeviceSubmit: async (e) => {
    e.preventDefault();
    
    // Construir objeto según el schema exacto de Java
    const deviceData = {
    nombre: document.getElementById('deviceName').value.trim(),
    tipo: document.getElementById('deviceType').value, // Debe ser: SENSOR_TEMPERATURA, SENSOR_HUMEDAD, etc.
    ubicacion: document.getElementById('deviceLocation').value,
    activo: true
};
    
    // Agregar descripción solo si no está vacía
    const descripcion = document.getElementById('deviceDescription').value.trim();
    if (descripcion) {
        deviceData.descripcion = descripcion;
    }
    
    console.log('Enviando datos:', deviceData);
    
    try {
        if (App.currentEditingDevice) {
            await App.updateDevice(App.currentEditingDevice.id, deviceData);
            Utils.showNotification('Dispositivo actualizado correctamente', 'success');
        } else {
            await App.createDevice(deviceData);
            Utils.showNotification('Dispositivo creado correctamente', 'success');
        }
        
        closeDeviceForm();
        Devices.fetchDevicesFromAPI();
    } catch (error) {
        console.error('Error completo:', error);
        Utils.showNotification('Error al guardar el dispositivo: ' + error.message, 'error');
    }
},
    
    // Crear nuevo dispositivo
    createDevice: async (deviceData) => {
        const response = await fetch(App.JAVA_API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return await response.json();
    },
    
    // Actualizar dispositivo existente
    updateDevice: async (deviceId, deviceData) => {
        const response = await fetch(`${App.JAVA_API_BASE_URL}/${deviceId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return await response.json();
    },
    
    // Eliminar dispositivo
    deleteDevice: async (deviceId) => {
        const response = await fetch(`${App.JAVA_API_BASE_URL}/${deviceId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return true;
    },
    
    // Actualizar contador de caracteres
    updateCharCount: () => {
        const input = document.getElementById('deviceDescription');
        const counter = document.getElementById('chars-remaining');
        const descCounter = document.getElementById('desc-counter');
        
        if (input && counter && descCounter) {
            const remaining = 15 - input.value.length;
            counter.textContent = remaining;
            descCounter.textContent = `(${input.value.length}/15)`;
        }
    },
    
    // Actualizar hora actual
    updateCurrentTime: () => {
        const timeElement = document.getElementById('current-time');
        if (timeElement) {
            const now = new Date();
            const timeString = now.toLocaleTimeString('es-ES', { 
                hour: '2-digit', 
                minute: '2-digit',
                second: '2-digit'
            });
            const dateString = now.toLocaleDateString('es-ES', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
            
            timeElement.textContent = `${dateString} - ${timeString}`;
        }
    },
    
    // Mostrar dashboard
    showDashboard: () => {
        Devices.fetchDevicesFromAPI();
        Alerts.renderAlerts();
        Charts.init();
    },
    
    // Iniciar actualizaciones automáticas
    startAutoUpdates: () => {
        setInterval(() => {
            Devices.fetchDevicesFromAPI();
            Alerts.fetchAlertsFromAPI();
            App.updateStatsFromAPI();
        }, 5000);
        
        Devices.fetchDevicesFromAPI();
        Alerts.fetchAlertsFromAPI();
        App.updateStatsFromAPI();
    },
    
    // Actualizar estadísticas desde API
    updateStatsFromAPI: async () => {
        try {
            const response = await fetch(`${App.PYTHON_API_BASE_URL}/estadisticas/zonas`);
            if (response.ok) {
                const data = await response.json();
                App.updateDashboardStats(data.estadisticas);
            }
        } catch (error) {
            console.error('Error fetching stats:', error);
        }
    },
    
    // Actualizar estadísticas del dashboard
    updateDashboardStats: (stats) => {
        if (!stats) return;
        
        let totalTemp = 0, totalHumidity = 0, tempCount = 0, humidityCount = 0;
        
        Object.values(stats).forEach(zona => {
            if (zona.estadisticas && zona.estadisticas.temperatura) {
                totalTemp += zona.estadisticas.temperatura.promedio;
                tempCount++;
            }
            if (zona.estadisticas && zona.estadisticas.humedad) {
                totalHumidity += zona.estadisticas.humedad.promedio;
                humidityCount++;
            }
        });
        
        const avgTemp = tempCount > 0 ? (totalTemp / tempCount).toFixed(1) : '--';
        const avgHumidity = humidityCount > 0 ? (totalHumidity / humidityCount).toFixed(1) : '--';
        
        document.getElementById('tempPromedio').textContent = avgTemp + '°C';
        document.getElementById('humedadPromedio').textContent = avgHumidity + '%';
    }
};