package com.function.dao;

import com.function.model.Notificacion;
import com.function.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificacionDAO {
    private static final Logger logger = LoggerFactory.getLogger(NotificacionDAO.class);

    // Secuencia para generar IDs
    private static final String SEQ_NOTIFICACIONES = "SEQ_NOTIFICACIONES";

    // SQL para operaciones CRUD
    private static final String SQL_INSERT = "INSERT INTO NOTIFICACIONES (id_notificacion, id_cliente, tipo, titulo, mensaje, fecha_creacion, estado, canal) VALUES (" + SEQ_NOTIFICACIONES + ".NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE NOTIFICACIONES SET id_cliente = ?, tipo = ?, titulo = ?, mensaje = ?, fecha_creacion = ?, fecha_envio = ?, estado = ?, canal = ? WHERE id_notificacion = ?";
    private static final String SQL_DELETE = "DELETE FROM NOTIFICACIONES WHERE id_notificacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT id_notificacion, id_cliente, tipo, titulo, mensaje, fecha_creacion, fecha_envio, estado, canal FROM NOTIFICACIONES";
    private static final String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE id_notificacion = ?";
    private static final String SQL_SELECT_BY_CLIENTE = SQL_SELECT_ALL + " WHERE id_cliente = ?";
    private static final String SQL_GET_NEXT_SEQ = "SELECT " + SEQ_NOTIFICACIONES + ".NEXTVAL FROM DUAL";

    /**
     * Obtiene todas las notificaciones
     */
    public List<Notificacion> findAll() throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                notificaciones.add(mapResultSetToNotificacion(rs));
            }
        }
        
        return notificaciones;
    }

    /**
     * Busca una notificación por su ID
     */
    public Optional<Notificacion> findById(Long id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNotificacion(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca notificaciones por ID de cliente
     */
    public List<Notificacion> findByCliente(Long idCliente) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_CLIENTE)) {
            
            stmt.setLong(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        }
        
        return notificaciones;
    }

    /**
     * Inserta una nueva notificación
     */
    public Notificacion save(Notificacion notificacion) throws SQLException {
        if (notificacion.getIdNotificacion() == null) {
            return insert(notificacion);
        } else {
            return update(notificacion);
        }
    }

    /**
     * Elimina una notificación por su ID
     */
    public boolean delete(Long id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
        }
    }

    /**
     * Inserta una nueva notificación
     */
    private Notificacion insert(Notificacion notificacion) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, new String[] {"id_notificacion"})) {
            
            // No necesitamos obtener el ID previamente, Oracle lo generará con la secuencia
            stmt.setLong(1, notificacion.getIdCliente());
            stmt.setString(2, notificacion.getTipo());
            stmt.setString(3, notificacion.getTitulo());
            stmt.setString(4, notificacion.getMensaje());
            stmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaCreacion() != null ? notificacion.getFechaCreacion() : LocalDateTime.now()));
            stmt.setString(6, notificacion.getEstado() != null ? notificacion.getEstado() : "PENDIENTE");
            stmt.setString(7, notificacion.getCanal());
            
            int filasAfectadas = stmt.executeUpdate();
            
            // Obtener el ID generado
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        notificacion.setIdNotificacion(rs.getLong(1));
                    }
                }
            }
            
            return notificacion;
        }
    }

    /**
     * Actualiza una notificación existente
     */
    private Notificacion update(Notificacion notificacion) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setLong(1, notificacion.getIdCliente());
            stmt.setString(2, notificacion.getTipo());
            stmt.setString(3, notificacion.getTitulo());
            stmt.setString(4, notificacion.getMensaje());
            stmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaCreacion() != null ? notificacion.getFechaCreacion() : LocalDateTime.now()));
            
            if (notificacion.getFechaEnvio() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(notificacion.getFechaEnvio()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            stmt.setString(7, notificacion.getEstado());
            stmt.setString(8, notificacion.getCanal());
            stmt.setLong(9, notificacion.getIdNotificacion());
            
            stmt.executeUpdate();
            
            return notificacion;
        }
    }

    /**
     * Obtiene el siguiente valor de la secuencia
     */
    private Long getNextSequenceValue(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_NEXT_SEQ)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No se pudo obtener el siguiente valor de la secuencia");
            }
        }
    }

    /**
     * Mapea un ResultSet a un objeto Notificacion
     */
    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        
        notificacion.setIdNotificacion(rs.getLong("id_notificacion"));
        notificacion.setIdCliente(rs.getLong("id_cliente"));
        notificacion.setTipo(rs.getString("tipo"));
        notificacion.setTitulo(rs.getString("titulo"));
        notificacion.setMensaje(rs.getString("mensaje"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            notificacion.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        Timestamp fechaEnvio = rs.getTimestamp("fecha_envio");
        if (fechaEnvio != null) {
            notificacion.setFechaEnvio(fechaEnvio.toLocalDateTime());
        }
        
        notificacion.setEstado(rs.getString("estado"));
        notificacion.setCanal(rs.getString("canal"));
        
        return notificacion;
    }
}