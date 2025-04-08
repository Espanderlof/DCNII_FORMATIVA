package com.function.dao;

import com.function.model.Factura;
import com.function.model.DetalleFactura;
import com.function.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacturaDAO {
    private static final Logger logger = LoggerFactory.getLogger(FacturaDAO.class);

    // Secuencia para generar IDs
    private static final String SEQ_FACTURAS = "SEQ_FACTURAS";
    
    // SQL para operaciones CRUD
    private static final String SQL_INSERT_FACTURA = "INSERT INTO FACTURAS (id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FACTURA = "UPDATE FACTURAS SET numero_factura = ?, id_cliente = ?, fecha_emision = ?, monto_subtotal = ?, monto_iva = ?, monto_total = ?, estado = ?, forma_pago = ?, observaciones = ? WHERE id_factura = ?";
    private static final String SQL_SELECT_FACTURA_BY_ID = "SELECT id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones FROM FACTURAS WHERE id_factura = ?";
    private static final String SQL_SELECT_FACTURAS_BY_CLIENTE = "SELECT id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones FROM FACTURAS WHERE id_cliente = ?";
    private static final String SQL_SELECT_FACTURAS_BY_ESTADO = "SELECT id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones FROM FACTURAS WHERE estado = ?";
    private static final String SQL_SELECT_FACTURAS_BY_FECHA = "SELECT id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones FROM FACTURAS WHERE fecha_emision BETWEEN ? AND ?";
    private static final String SQL_SELECT_ALL_FACTURAS = "SELECT id_factura, numero_factura, id_cliente, fecha_emision, monto_subtotal, monto_iva, monto_total, estado, forma_pago, observaciones FROM FACTURAS";
    private static final String SQL_GET_NEXT_SEQ_FACTURA = "SELECT " + SEQ_FACTURAS + ".NEXTVAL FROM DUAL";

    private final DetalleFacturaDAO detalleFacturaDAO = new DetalleFacturaDAO();

    /**
     * Obtiene todas las facturas
     */
    public List<Factura> findAll() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_FACTURAS)) {
            
            while (rs.next()) {
                facturas.add(mapResultSetToFactura(rs));
            }
        }
        
        return facturas;
    }

    /**
     * Busca una factura por su ID
     */
    public Optional<Factura> findById(Long id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_FACTURA_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFactura(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca facturas por cliente
     */
    public List<Factura> findByCliente(Long idCliente) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_FACTURAS_BY_CLIENTE)) {
            
            stmt.setLong(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        }
        
        return facturas;
    }

    /**
     * Busca facturas por estado
     */
    public List<Factura> findByEstado(String estado) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_FACTURAS_BY_ESTADO)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        }
        
        return facturas;
    }

    /**
     * Busca facturas por rango de fechas
     */
    public List<Factura> findByFechaEmision(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_FACTURAS_BY_FECHA)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        }
        
        return facturas;
    }

    /**
     * Guarda o actualiza una factura
     */
    public Factura save(Factura factura, List<DetalleFactura> detalles) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnectionManager.getConnection();
            conn.setAutoCommit(false);
            
            if (factura.getIdFactura() == null) {
                factura = insert(conn, factura);
                
                // Asignar id de factura a los detalles
                if (detalles != null) {
                    for (DetalleFactura detalle : detalles) {
                        detalle.setIdFactura(factura.getIdFactura());
                        detalleFacturaDAO.insert(conn, detalle);
                    }
                }
            } else {
                factura = update(conn, factura);
                
                // Implementar lógica para actualizar detalles si es necesario
            }
            
            conn.commit();
            return factura;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error al hacer rollback", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error al cerrar conexión", e);
                }
            }
        }
    }

    /**
     * Inserta una nueva factura
     */
    private Factura insert(Connection conn, Factura factura) throws SQLException {
        Long id = getNextSequenceValue(conn);
        factura.setIdFactura(id);
        
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_FACTURA)) {
            stmt.setLong(1, factura.getIdFactura());
            stmt.setString(2, factura.getNumeroFactura());
            stmt.setLong(3, factura.getIdCliente());
            
            if (factura.getFechaEmision() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(factura.getFechaEmision()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            stmt.setDouble(5, factura.getMontoSubtotal() != null ? factura.getMontoSubtotal() : 0.0);
            stmt.setDouble(6, factura.getMontoIva() != null ? factura.getMontoIva() : 0.0);
            stmt.setDouble(7, factura.getMontoTotal() != null ? factura.getMontoTotal() : 0.0);
            stmt.setString(8, factura.getEstado() != null ? factura.getEstado() : "PENDIENTE");
            stmt.setString(9, factura.getFormaPago());
            stmt.setString(10, factura.getObservaciones());
            
            stmt.executeUpdate();
        }
        
        return factura;
    }

    /**
     * Actualiza una factura existente
     */
    private Factura update(Connection conn, Factura factura) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_FACTURA)) {
            stmt.setString(1, factura.getNumeroFactura());
            stmt.setLong(2, factura.getIdCliente());
            
            if (factura.getFechaEmision() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(factura.getFechaEmision()));
            } else {
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            stmt.setDouble(4, factura.getMontoSubtotal() != null ? factura.getMontoSubtotal() : 0.0);
            stmt.setDouble(5, factura.getMontoIva() != null ? factura.getMontoIva() : 0.0);
            stmt.setDouble(6, factura.getMontoTotal() != null ? factura.getMontoTotal() : 0.0);
            stmt.setString(7, factura.getEstado());
            stmt.setString(8, factura.getFormaPago());
            stmt.setString(9, factura.getObservaciones());
            stmt.setLong(10, factura.getIdFactura());
            
            stmt.executeUpdate();
        }
        
        return factura;
    }

    /**
     * Obtiene el siguiente valor de la secuencia
     */
    private Long getNextSequenceValue(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_NEXT_SEQ_FACTURA)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No se pudo obtener el siguiente valor de la secuencia");
            }
        }
    }

    /**
     * Mapea un ResultSet a un objeto Factura
     */
    private Factura mapResultSetToFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        
        factura.setIdFactura(rs.getLong("id_factura"));
        factura.setNumeroFactura(rs.getString("numero_factura"));
        factura.setIdCliente(rs.getLong("id_cliente"));
        
        Timestamp fechaEmision = rs.getTimestamp("fecha_emision");
        if (fechaEmision != null) {
            factura.setFechaEmision(fechaEmision.toLocalDateTime());
        }
        
        factura.setMontoSubtotal(rs.getDouble("monto_subtotal"));
        factura.setMontoIva(rs.getDouble("monto_iva"));
        factura.setMontoTotal(rs.getDouble("monto_total"));
        factura.setEstado(rs.getString("estado"));
        factura.setFormaPago(rs.getString("forma_pago"));
        factura.setObservaciones(rs.getString("observaciones"));
        
        return factura;
    }
}