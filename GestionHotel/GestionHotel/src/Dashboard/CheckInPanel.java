package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CheckInPanel extends JPanel {
    private JComboBox<String> cmbReservaciones;
    private JComboBox<String> cmbHora;
    private JButton btnCheckIn;
    private JButton btnCancelar;

    public CheckInPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTitle = new JLabel("CHECK-IN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(lblTitle);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // Componentes del formulario
        formPanel.add(new JLabel("Reservación:"));
        cmbReservaciones = new JComboBox<>();
        cargarReservacionesPendientes();
        formPanel.add(cmbReservaciones);
        
        formPanel.add(new JLabel("Hora de entrada:"));
        cmbHora = new JComboBox<>();
        cargarHorasDisponibles();
        formPanel.add(cmbHora);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCheckIn = new JButton("Aceptar Check-In");
        btnCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckIn.setPreferredSize(new Dimension(180, 40));
        btnCheckIn.addActionListener(e -> registrarCheckIn());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(180, 40));
        btnCancelar.addActionListener(e -> limpiarFormulario());
        
        buttonPanel.add(btnCheckIn);
        buttonPanel.add(btnCancelar);
        
        // Ensamblar la interfaz
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
   private void cargarReservacionesPendientes() {
    cmbReservaciones.removeAllItems();
    try (Connection conn = DBConnection.getConnection()) {
        // Consulta modificada para mostrar reservaciones confirmadas sin check-in activo
        String sql = "SELECT r.id, h.numero, r.nombre_cliente, r.fecha_checkin, r.fecha_checkout " +
                     "FROM Reservaciones r " +
                     "JOIN Habitaciones h ON r.habitacion_id = h.id " +
                     "WHERE r.estado = 'Confirmada' AND NOT EXISTS (" +
                     "    SELECT 1 FROM CheckIns ci WHERE ci.reservacion_id = r.id AND ci.estado = 'Activo'" +
                     ") ORDER BY r.fecha_checkin";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            String item = rs.getString("id") + " - Hab. " + rs.getString("numero") + " - " + 
                         rs.getString("nombre_cliente") + " (" + 
                         new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_checkin")) + " a " +
                         new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fecha_checkout")) + ")";
            cmbReservaciones.addItem(item);
        }
        
        if (cmbReservaciones.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No hay reservaciones confirmadas pendientes de check-in", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar reservaciones: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void cargarHorasDisponibles() {
        cmbHora.removeAllItems();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 8; i <= 22; i++) {
            cmbHora.addItem(LocalTime.of(i, 0).format(formatter));
            cmbHora.addItem(LocalTime.of(i, 30).format(formatter));
        }
    }
    
    private void registrarCheckIn() {
        if (cmbReservaciones.getSelectedItem() == null || cmbHora.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una reservación y hora de entrada", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                
                String selected = cmbReservaciones.getSelectedItem().toString();
                int reservacionId = Integer.parseInt(selected.split(" - ")[0]);
                String horaEntrada = cmbHora.getSelectedItem().toString();

                String sqlCheckIn = "INSERT INTO CheckIns (reservacion_id, hora_entrada, estado) VALUES (?, ?, 'Activo')";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckIn, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, reservacionId);
                    pstmt.setString(2, horaEntrada);
                    pstmt.executeUpdate();

                    String sqlHabitacion = "UPDATE Habitaciones SET estado = 'Ocupada' " +
                                          "WHERE id = (SELECT habitacion_id FROM Reservaciones WHERE id = ?)";
                    try (PreparedStatement pstmtHab = conn.prepareStatement(sqlHabitacion)) {
                        pstmtHab.setInt(1, reservacionId);
                        pstmtHab.executeUpdate();
                    }
                    
                    conn.commit();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Check-In registrado exitosamente\nHora de entrada: " + horaEntrada, 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    limpiarFormulario();
                    cargarReservacionesPendientes();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar Check-In: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        cmbReservaciones.setSelectedItem(null);
        cmbHora.setSelectedItem(null);
    }
}