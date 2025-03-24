package com.duoc.app_spring.service;

import com.duoc.app_spring.model.Producto;
import com.duoc.app_spring.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Servicio para verificar productos próximos a vencer
 */
@Service
public class VencimientoService {
    
    private static final Logger log = LoggerFactory.getLogger(VencimientoService.class);
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private AzureFunctionService azureFunctionService;
    
    @Value("${inventario.alertas.vencimiento.dias:30}")
    private int diasLimiteVencimiento;
    
    /**
     * Programado para ejecutarse todos los días a las 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void verificarProductosPorVencer() {
        try {
            log.info("Iniciando verificación de productos por vencer...");
            
            // Calcular fecha límite (hoy + días configurados)
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimite = hoy.plusDays(diasLimiteVencimiento);
            
            Date fechaLimiteDate = Date.from(fechaLimite.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // Obtenemos todos los productos y filtramos manualmente
            List<Producto> todosProductos = productoRepository.findAll();
            log.info("Se recuperaron {} productos para analizar", todosProductos.size());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            int contadorProductosPorVencer = 0;
            
            // Enviar alertas para cada producto próximo a vencer
            for (Producto producto : todosProductos) {
                try {
                    if (producto.getFechaVencimiento() != null && producto.getActivo() == 1) {
                        // Verificar si el producto vence antes de la fecha límite
                        if (producto.getFechaVencimiento().before(fechaLimiteDate)) {
                            contadorProductosPorVencer++;
                            
                            // Convertir java.sql.Date a java.util.Date para manejar de forma segura
                            Date fechaVencimientoUtil = new Date(producto.getFechaVencimiento().getTime());
                            
                            // Calcular días restantes hasta el vencimiento de forma segura
                            Calendar calHoy = Calendar.getInstance();
                            Calendar calVencimiento = Calendar.getInstance();
                            calVencimiento.setTime(fechaVencimientoUtil);
                            
                            // Normalizar ambas fechas para comparar solo días
                            calHoy.set(Calendar.HOUR_OF_DAY, 0);
                            calHoy.set(Calendar.MINUTE, 0);
                            calHoy.set(Calendar.SECOND, 0);
                            calHoy.set(Calendar.MILLISECOND, 0);
                            
                            calVencimiento.set(Calendar.HOUR_OF_DAY, 0);
                            calVencimiento.set(Calendar.MINUTE, 0);
                            calVencimiento.set(Calendar.SECOND, 0);
                            calVencimiento.set(Calendar.MILLISECOND, 0);
                            
                            // Calcular diferencia en días
                            long diferenciaMilisegundos = calVencimiento.getTimeInMillis() - calHoy.getTimeInMillis();
                            long diasRestantes = diferenciaMilisegundos / (24 * 60 * 60 * 1000);
                            
                            // Solo alertar si los días restantes son positivos (aún no ha vencido)
                            if (diasRestantes >= 0) {
                                log.info("Enviando alerta para producto próximo a vencer: {} (ID: {}), días restantes: {}",
                                        producto.getNombre(), producto.getIdProducto(), diasRestantes);
                                
                                azureFunctionService.enviarAlertaProductoPorVencer(
                                        producto.getIdProducto(),
                                        producto.getNombre(),
                                        sdf.format(fechaVencimientoUtil),
                                        diasRestantes
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al procesar producto por vencer ID: {}: {}", 
                            producto.getIdProducto(), e.getMessage(), e);
                    // Continuamos con el siguiente producto
                }
            }
            
            log.info("Se encontraron {} productos próximos a vencer en los próximos {} días", 
                    contadorProductosPorVencer, diasLimiteVencimiento);
            log.info("Verificación de productos por vencer completada");
        } catch (Exception e) {
            log.error("Error en la verificación de productos por vencer: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar productos por vencer", e);
        }
    }
}