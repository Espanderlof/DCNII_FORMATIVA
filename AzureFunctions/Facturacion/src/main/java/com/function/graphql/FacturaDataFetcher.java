package com.function.graphql;

import com.function.dao.FacturaDAO;
import com.function.dao.DetalleFacturaDAO;
import com.function.model.Factura;
import com.function.model.DetalleFactura;
import graphql.schema.DataFetcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FacturaDataFetcher {

    private final FacturaDAO facturaDAO;
    private final DetalleFacturaDAO detalleFacturaDAO;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FacturaDataFetcher() {
        this.facturaDAO = new FacturaDAO();
        this.detalleFacturaDAO = new DetalleFacturaDAO();
    }

    public DataFetcher<Factura> getFacturaPorId() {
        return dataFetchingEnvironment -> {
            String idStr = dataFetchingEnvironment.getArgument("id");
            Long id = Long.parseLong(idStr);
            return facturaDAO.findById(id).orElse(null);
        };
    }

    public DataFetcher<List<Factura>> getFacturasPorCliente() {
        return dataFetchingEnvironment -> {
            String idClienteStr = dataFetchingEnvironment.getArgument("idCliente");
            Long idCliente = Long.parseLong(idClienteStr);
            return facturaDAO.findByCliente(idCliente);
        };
    }

    public DataFetcher<List<Factura>> getFacturasPorEstado() {
        return dataFetchingEnvironment -> {
            String estado = dataFetchingEnvironment.getArgument("estado");
            return facturaDAO.findByEstado(estado);
        };
    }

    public DataFetcher<List<Factura>> getFacturasPorFecha() {
        return dataFetchingEnvironment -> {
            String fechaInicioStr = dataFetchingEnvironment.getArgument("fechaInicio");
            String fechaFinStr = dataFetchingEnvironment.getArgument("fechaFin");

            LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr, DATE_TIME_FORMATTER);
            LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr, DATE_TIME_FORMATTER);

            return facturaDAO.findByFechaEmision(fechaInicio, fechaFin);
        };
    }

    public DataFetcher<List<Factura>> getAllFacturas() {
        return dataFetchingEnvironment -> facturaDAO.findAll();
    }

    public DataFetcher<List<DetalleFactura>> getDetallesPorFactura() {
        return dataFetchingEnvironment -> {
            Factura factura = dataFetchingEnvironment.getSource();
            return detalleFacturaDAO.findByFactura(factura.getIdFactura());
        };
    }

    public DataFetcher<Factura> createFactura() {
        return dataFetchingEnvironment -> {
            Map<String, Object> facturaInput = dataFetchingEnvironment.getArgument("factura");
            List<Map<String, Object>> detallesInput = dataFetchingEnvironment.getArgument("detalles");

            // Crear objeto Factura a partir del input
            Factura factura = new Factura();
            factura.setIdCliente(Long.parseLong(facturaInput.get("idCliente").toString()));

            if (facturaInput.containsKey("numeroFactura")) {
                factura.setNumeroFactura((String) facturaInput.get("numeroFactura"));
            }

            if (facturaInput.containsKey("fechaEmision")) {
                factura.setFechaEmision(
                        LocalDateTime.parse((String) facturaInput.get("fechaEmision"), DATE_TIME_FORMATTER));
            } else {
                factura.setFechaEmision(LocalDateTime.now());
            }

            if (facturaInput.containsKey("montoSubtotal")) {
                factura.setMontoSubtotal(Double.parseDouble(facturaInput.get("montoSubtotal").toString()));
            }

            if (facturaInput.containsKey("montoIva")) {
                factura.setMontoIva(Double.parseDouble(facturaInput.get("montoIva").toString()));
            }

            if (facturaInput.containsKey("montoTotal")) {
                factura.setMontoTotal(Double.parseDouble(facturaInput.get("montoTotal").toString()));
            }

            if (facturaInput.containsKey("estado")) {
                factura.setEstado((String) facturaInput.get("estado"));
            } else {
                factura.setEstado("PENDIENTE");
            }

            if (facturaInput.containsKey("formaPago")) {
                factura.setFormaPago((String) facturaInput.get("formaPago"));
            }

            if (facturaInput.containsKey("observaciones")) {
                factura.setObservaciones((String) facturaInput.get("observaciones"));
            }

            // Crear objetos DetalleFactura a partir del input
            List<DetalleFactura> detalles = null;
            if (detallesInput != null && !detallesInput.isEmpty()) {
                detalles = detallesInput.stream().map(detInput -> {
                    DetalleFactura detalle = new DetalleFactura();

                    if (detInput.containsKey("idCita") && detInput.get("idCita") != null) {
                        detalle.setIdCita(Long.parseLong(detInput.get("idCita").toString()));
                    }

                    if (detInput.containsKey("idProducto") && detInput.get("idProducto") != null) {
                        detalle.setIdProducto(Long.parseLong(detInput.get("idProducto").toString()));
                    }

                    detalle.setDescripcion((String) detInput.get("descripcion"));
                    detalle.setCantidad(Integer.parseInt(detInput.get("cantidad").toString()));
                    detalle.setPrecioUnitario(Double.parseDouble(detInput.get("precioUnitario").toString()));

                    if (detInput.containsKey("subtotal") && detInput.get("subtotal") != null) {
                        detalle.setSubtotal(Double.parseDouble(detInput.get("subtotal").toString()));
                    } else {
                        // Calcular subtotal
                        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                    }

                    return detalle;
                }).collect(Collectors.toList());

                // Calcular totales si no se proporcionaron
                if (factura.getMontoSubtotal() == null) {
                    double subtotal = detalles.stream()
                            .mapToDouble(DetalleFactura::getSubtotal)
                            .sum();
                    factura.setMontoSubtotal(subtotal);
                }

                if (factura.getMontoIva() == null) {
                    // Calcular IVA (19% por ejemplo)
                    double iva = factura.getMontoSubtotal() * 0.19;
                    factura.setMontoIva(iva);
                }

                if (factura.getMontoTotal() == null) {
                    double total = factura.getMontoSubtotal() + factura.getMontoIva();
                    factura.setMontoTotal(total);
                }
            }

            // Guardar factura y detalles
            return facturaDAO.save(factura, detalles);
        };
    }

    public DataFetcher<Factura> updateEstadoFactura() {
        return dataFetchingEnvironment -> {
            String idStr = dataFetchingEnvironment.getArgument("id");
            String estado = dataFetchingEnvironment.getArgument("estado");

            Long id = Long.parseLong(idStr);
            Optional<Factura> facturaOpt = facturaDAO.findById(id);

            if (facturaOpt.isPresent()) {
                Factura factura = facturaOpt.get();
                factura.setEstado(estado);
                return facturaDAO.save(factura, null);
            }

            return null;
        };
    }
}