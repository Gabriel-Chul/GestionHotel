package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CheckInPanel extends JPanel {
    private JComboBox<String> cmbReservas;
    private JComboBox<String> cmbHora;
    private JButton btnCheckIn;
    private JButton btnCancelar;

    public CheckInPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblTitle = new JLabel("CHECK-IN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(lblTitle);

        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        formPanel.add(new JLabel("Reserva:"));
        cmbReservas = new JComboBox<>();
        cargarReservasPendientes();
        formPanel.add(cmbReservas);

        formPanel.add(new JLabel("Hora de entrada:"));
        cmbHora = new JComboBox<>();
        cargarHorasDisponibles();
        formPanel.add(cmbHora);

        // Botones
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

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cargarReservasPendientes() {
        cmbReservas.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT r.ID_Reserva, h.Numero, c.Nombres, c.Apellidos, r.Fecha_Entrada, r.Fecha_Salida
                FROM Reservaciones r
                JOIN Habitaciones h ON r.ID_Habitacion = h.ID_Habitacion
                JOIN Clientes c ON r.ID_Cliente = c.ID_Cliente
                WHERE r.Estado = 'Confirmada'
                  AND NOT EXISTS (
                    SELECT 1 FROM CheckIns ci
                    WHERE ci.ID_Reserva = r.ID_Reserva AND ci.Estado = 'Activo'
                  )
                ORDER BY r.Fecha_Entrada
            """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String item = rs.getInt("ID_Reserva") + " - Hab. " + rs.getInt("Numero") + " - " +
                        rs.getString("Nombres") + " " + rs.getString("Apellidos") + " (" +
                        new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("Fecha_Entrada")) + " a " +
                        new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("Fecha_Salida")) + ")";
                cmbReservas.addItem(item);
            }

            if (cmbReservas.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay reservas confirmadas pendientes de check-in",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar reservas: " + e.getMessage(),
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
        if (cmbReservas.getSelectedItem() == null || cmbHora.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una reserva y hora de entrada",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String selected = cmbReservas.getSelectedItem().toString();
                int reservaId = Integer.parseInt(selected.split(" - ")[0]);
                String horaEntrada = cmbHora.getSelectedItem().toString();

                String sqlCheckIn = "INSERT INTO CheckIns (ID_Reserva, Hora_Entrada, Estado) VALUES (?, ?, 'Activo')";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCheckIn)) {
                    pstmt.setInt(1, reservaId);
                    pstmt.setString(2, horaEntrada);
                    pstmt.executeUpdate();
                }

                String sqlUpdateHabitacion = """
                    UPDATE Habitaciones
                    SET Estado = 'Ocupada'
                    WHERE ID_Habitacion = (
                        SELECT ID_Habitacion FROM Reservas WHERE ID_Reserva = ?
                    )
                """;
                try (PreparedStatement pstmtHab = conn.prepareStatement(sqlUpdateHabitacion)) {
                    pstmtHab.setInt(1, reservaId);
                    pstmtHab.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this,
                        "Check-In registrado exitosamente\nHora de entrada: " + horaEntrada,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarReservasPendientes();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar Check-In: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        cmbReservas.setSelectedItem(null);
        cmbHora.setSelectedItem(null);
    }
}
