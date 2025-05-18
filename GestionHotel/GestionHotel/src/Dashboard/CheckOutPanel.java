package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckOutPanel extends JPanel {
    private JComboBox<String> cmbReservaciones;
    private JLabel lblTotal;
    private JLabel lblDetallePrecio;
    private JLabel lblHoraEntrada;
    private JLabel lblEstadoReserva;
    private JButton btnCheckOut;
    private JButton btnCancelar;

    public CheckOutPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTitle = new JLabel("CHECK-OUT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(lblTitle);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // Componentes del formulario con fuente más grande
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        JLabel lblReservacion = new JLabel("Reservación:");
        lblReservacion.setFont(labelFont);
        formPanel.add(lblReservacion);
        
        cmbReservaciones = new JComboBox<>();
        cmbReservaciones.setFont(fieldFont);
        cargarReservacionesConCheckInActivo();
        cmbReservaciones.addActionListener(e -> cargarDetallesCompletos());
        formPanel.add(cmbReservaciones);
        
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(labelFont);
        formPanel.add(lblEstado);
        
        lblEstadoReserva = new JLabel("-");
        lblEstadoReserva.setFont(fieldFont);
        formPanel.add(lblEstadoReserva);
        
        JLabel lblHoraEntradaLabel = new JLabel("Hora de entrada:");
        lblHoraEntradaLabel.setFont(labelFont);
        formPanel.add(lblHoraEntradaLabel);
        
        lblHoraEntrada = new JLabel("--:--");
        lblHoraEntrada.setFont(fieldFont);
        formPanel.add(lblHoraEntrada);
        
        JLabel lblPrecioNoche = new JLabel("Precio por noche:");
        lblPrecioNoche.setFont(labelFont);
        formPanel.add(lblPrecioNoche);
        
        lblDetallePrecio = new JLabel("$0.00");
        lblDetallePrecio.setFont(fieldFont);
        formPanel.add(lblDetallePrecio);
        
        JLabel lblTotalLabel = new JLabel("Total a pagar:");
        lblTotalLabel.setFont(labelFont);
        formPanel.add(lblTotalLabel);
        
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(lblTotal);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Botón Check-Out
        btnCheckOut = new JButton("Realizar Check-Out");
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckOut.setPreferredSize(new Dimension(180, 40));
        btnCheckOut.addActionListener(e -> realizarCheckOut());
        
        // Botón Cancelar
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(180, 40));
        btnCancelar.addActionListener(e -> limpiarPantalla());
        
        buttonPanel.add(btnCheckOut);
        buttonPanel.add(btnCancelar);
        
        // Ensamble de interfaz
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void limpiarPantalla() {
        cmbReservaciones.setSelectedIndex(-1);
        limpiarDetalles();
        JOptionPane.showMessageDialog(this, 
            "Operación cancelada. Los campos han sido limpiados.", 
            "Cancelado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cargarReservacionesConCheckInActivo() {
        cmbReservaciones.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.id, h.numero, r.nombre_cliente, r.fecha_checkin, r.fecha_checkout, r.estado " +
                         "FROM Reservaciones r " +
                         "JOIN Habitaciones h ON r.habitacion_id = h.id " +
                         "WHERE EXISTS ( " +
                         "    SELECT 1 FROM CheckIns ci " +
                         "    WHERE ci.reservacion_id = r.id AND ci.estado = 'Activo' " +
                         ") " +
                         "ORDER BY h.numero";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int count = 0;
            while (rs.next()) {
                count++;
                String item = rs.getString("id") + " - Hab. " + rs.getString("numero") + " - " + 
                             rs.getString("nombre_cliente") + " (" + 
                             new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_checkin")) + " a " +
                             new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_checkout")) + ")";
                cmbReservaciones.addItem(item);
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay reservaciones con check-in activo disponibles.\n" +
                    "Verifique que:\n" +
                    "1. Existan reservaciones con check-in registrado\n" +
                    "2. El estado del check-in sea 'Activo'", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar reservaciones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDetallesCompletos() {
        if (cmbReservaciones.getSelectedItem() == null) {
            limpiarDetalles();
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String selected = cmbReservaciones.getSelectedItem().toString();
            int reservacionId = Integer.parseInt(selected.split(" - ")[0]);

            String sql = "SELECT r.estado, ci.hora_entrada, h.precio, h.numero, " +
                         "DATEDIFF(day, r.fecha_checkin, r.fecha_checkout) AS dias " +
                         "FROM Reservaciones r " +
                         "JOIN Habitaciones h ON r.habitacion_id = h.id " +
                         "JOIN CheckIns ci ON ci.reservacion_id = r.id " +
                         "WHERE r.id = ? AND ci.estado = 'Activo'";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, reservacionId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    lblEstadoReserva.setText(rs.getString("estado"));
                    lblHoraEntrada.setText(rs.getString("hora_entrada"));

                    double precioPorNoche = rs.getDouble("precio");
                    int dias = rs.getInt("dias");
                    dias = dias < 1 ? 1 : dias;
                    double total = dias * precioPorNoche;
                    
                    lblDetallePrecio.setText(String.format("$%.2f (Hab. %s)", precioPorNoche, rs.getString("numero")));
                    lblTotal.setText(String.format("$%.2f", total));
                } else {
                    limpiarDetalles();
                    JOptionPane.showMessageDialog(this, 
                        "No se encontró check-in activo para esta reservación", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar detalles: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            limpiarDetalles();
        }
    }
    
    private void limpiarDetalles() {
        lblEstadoReserva.setText("-");
        lblHoraEntrada.setText("--:--");
        lblDetallePrecio.setText("$0.00");
        lblTotal.setText("$0.00");
    }
    
    private void realizarCheckOut() {
        if (cmbReservaciones.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una reservación", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea realizar el check-out de esta reservación?",
            "Confirmar Check-Out", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            String selected = cmbReservaciones.getSelectedItem().toString();
            int reservacionId = Integer.parseInt(selected.split(" - ")[0]);
            
            Object[] detalles = obtenerDetallesReserva(conn, reservacionId);
            if (detalles == null) {
                conn.rollback();
                return;
            }
            
            String numeroHabitacion = (String) detalles[0];
            double precioPorNoche = (double) detalles[1];
            int dias = (int) detalles[2];
            double total = dias * precioPorNoche;

            if (!finalizarCheckIn(conn, reservacionId)) {
                conn.rollback();
                throw new SQLException("No se pudo finalizar el check-in");
            }

            if (!actualizarReservacion(conn, reservacionId)) {
                conn.rollback();
                throw new SQLException("No se pudo actualizar la reservación");
            }

            if (!liberarHabitacion(conn, reservacionId)) {
                conn.rollback();
                throw new SQLException("No se pudo liberar la habitación");
            }
            
            conn.commit();
            mostrarResumenCheckOut(numeroHabitacion, precioPorNoche, dias, total);
            actualizarInterfaz();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error durante el check-out: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Object[] obtenerDetallesReserva(Connection conn, int reservacionId) throws SQLException {
        String sql = "SELECT h.numero, h.precio, " +
                     "DATEDIFF(day, r.fecha_checkin, r.fecha_checkout) AS dias " +
                     "FROM Reservaciones r " +
                     "JOIN Habitaciones h ON r.habitacion_id = h.id " +
                     "WHERE r.id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservacionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int dias = rs.getInt("dias");
                dias = dias < 1 ? 1 : dias;
                return new Object[] {
                    rs.getString("numero"),
                    rs.getDouble("precio"),
                    dias
                };
            }
        }
        return null;
    }
    
    private boolean finalizarCheckIn(Connection conn, int reservacionId) throws SQLException {
        String sql = "UPDATE CheckIns SET estado = 'Finalizado'" +
                     "WHERE reservacion_id = ? AND estado = 'Activo'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservacionId);
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No se encontró check-in activo para finalizar");
            }
            return true;
        }
    }
    
    private boolean actualizarReservacion(Connection conn, int reservacionId) throws SQLException {
        String sql = "UPDATE Reservaciones SET estado = 'Finalizada' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservacionId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private boolean liberarHabitacion(Connection conn, int reservacionId) throws SQLException {
        String sql = "UPDATE Habitaciones SET estado = 'Disponible' " +
                     "WHERE id = (SELECT habitacion_id FROM Reservaciones WHERE id = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservacionId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private void mostrarResumenCheckOut(String numeroHabitacion, double precioPorNoche, int dias, double total) {
        String mensaje = String.format(
            "<html><b>Check-Out realizado exitosamente</b><br><br>" +
            "<table>" +
            "<tr><td><b>Habitación:</b></td><td>%s</td></tr>" +
            "<tr><td><b>Precio por noche:</b></td><td>$%.2f</td></tr>" +
            "<tr><td><b>Noches de estadía:</b></td><td>%d</td></tr>" +
            "<tr><td><b>Total a pagar:</b></td><td><font color='green'>$%.2f</font></td></tr>" +
            "</table></html>",
            numeroHabitacion, precioPorNoche, dias, total);
        
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarInterfaz() {
        cargarReservacionesConCheckInActivo();
        limpiarDetalles();
    }
}