package com.function.dao;

import com.function.model.DetalleFactura;
import com.function.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetalleFacturaDAO {
    private static final Logger logger = LoggerFactory.getLogger(DetalleFacturaDAO.class);

    // Secuencia para generar IDs
    private static final String SEQ_DETALLES_FACTURA = "SEQ_DETALLES_FACTURA";
    
    // SQL para operaciones CRUD
    private static final String SQL_INSERT_DETALLE = "INSERT INTO DETALLES_FACTURA (id_detalle, id_factura, id_cita, id_producto, descripcion, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_DETALLES_BY_FACTURA = "SELECT id_detalle, id_factura, id_cita, id_producto, descripcion, cantidad, precio_unitario, subtotal FROM DETALLES_FACTURA WHERE id_factura = ?";
    private static final String SQL_GET_NEXT_SEQ_DETALLE = "SELECT " + SEQ_DETALLES_FACTURA + ".NEXTVAL FROM DUAL";

    /**
     * Busca detalles por ID de factura
     */
    public List<DetalleFactura> findByFactura(Long idFactura) throws SQLException {
        List<DetalleFactura> detalles = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_DETALLES_BY_FACTURA)) {
            
            stmt.setLong(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalle(rs));
                }
            }
        }
        
        return detalles;
    }

    /**
     * Inserta un nuevo detalle de factura
     */
    public DetalleFactura insert(Connection conn, DetalleFactura detalle) throws SQLException {
        Long id = getNextSequenceValue(conn);
        detalle.setIdDetalle(id);
        
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_DETALLE)) {
            stmt.setLong(1, detalle.getIdDetalle());
            stmt.setLong(2, detalle.getIdFactura());
            
            if (detalle.getIdCita() != null) {
                stmt.setLong(3, detalle.getIdCita());
            } else {
                stmt.setNull(3, Types.NUMERIC);
            }
            
            if (detalle.getIdProducto() != null) {
                stmt.setLong(4, detalle.getIdProducto());
            } else {
                stmt.setNull(4, Types.NUMERIC);
            }
            
            stmt.setString(5, detalle.getDescripcion());
            stmt.setInt(6, detalle.getCantidad());
            stmt.setDouble(7, detalle.getPrecioUnitario());
            
            // Calcular subtotal si no está establecido
            double subtotal = detalle.getSubtotal() != null ? detalle.getSubtotal() : 
                              detalle.getCantidad() * detalle.getPrecioUnitario();
            detalle.setSubtotal(subtotal);
            
            stmt.setDouble(8, subtotal);
            
            stmt.executeUpdate();
        }
        
        return detalle;
    }

    /**
     * Obtiene el siguiente valor de la secuencia
     */
    private Long getNextSequenceValue(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_NEXT_SEQ_DETALLE)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No se pudo obtener el siguiente valor de la secuencia");
            }
        }
    }

    /**
     * Mapea un ResultSet a un objeto DetalleFactura
     */
    private DetalleFactura mapResultSetToDetalle(ResultSet rs) throws SQLException {
        DetalleFactura detalle = new DetalleFactura();
        
        detalle.setIdDetalle(rs.getLong("id_detalle"));
        detalle.setIdFactura(rs.getLong("id_factura"));
        
        long idCita = rs.getLong("id_cita");
        if (!rs.wasNull()) {
            detalle.setIdCita(idCita);
        }
        
        long idProducto = rs.getLong("id_producto");
        if (!rs.wasNull()) {
            detalle.setIdProducto(idProducto);
        }
        
        detalle.setDescripcion(rs.getString("descripcion"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
        detalle.setSubtotal(rs.getDouble("subtotal"));
        
        return detalle;
    }
}