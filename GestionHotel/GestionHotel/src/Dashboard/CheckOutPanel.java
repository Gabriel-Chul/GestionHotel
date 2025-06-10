package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

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

        JPanel titlePanel = new JPanel();
        JLabel lblTitle = new JLabel("CHECK-OUT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(lblTitle);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        formPanel.add(new JLabel("Reservación:")).setFont(labelFont);
        cmbReservaciones = new JComboBox<>();
        cmbReservaciones.setFont(fieldFont);
        cmbReservaciones.addActionListener(e -> cargarDetallesCompletos());
        formPanel.add(cmbReservaciones);

        formPanel.add(new JLabel("Estado:")).setFont(labelFont);
        lblEstadoReserva = new JLabel("-");
        lblEstadoReserva.setFont(fieldFont);
        formPanel.add(lblEstadoReserva);

        formPanel.add(new JLabel("Hora de entrada:")).setFont(labelFont);
        lblHoraEntrada = new JLabel("--:--");
        lblHoraEntrada.setFont(fieldFont);
        formPanel.add(lblHoraEntrada);

        formPanel.add(new JLabel("Precio:")).setFont(labelFont);
        lblDetallePrecio = new JLabel("$0.00");
        lblDetallePrecio.setFont(fieldFont);
        formPanel.add(lblDetallePrecio);

        formPanel.add(new JLabel("Total a pagar:")).setFont(labelFont);
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(lblTotal);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCheckOut = new JButton("Realizar Check-Out");
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckOut.setPreferredSize(new Dimension(180, 40));
        btnCheckOut.addActionListener(e -> realizarCheckOut());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(180, 40));
        btnCancelar.addActionListener(e -> limpiarPantalla());

        buttonPanel.add(btnCheckOut);
        buttonPanel.add(btnCancelar);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        cargarReservacionesConCheckInActivo();
    }

    private void limpiarPantalla() {
        cmbReservaciones.setSelectedIndex(-1);
        limpiarDetalles();
        JOptionPane.showMessageDialog(this, "Operación cancelada.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarReservacionesConCheckInActivo() {
        cmbReservaciones.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT ci.ID_Reserva, h.Numero, c.Nombres, c.Apellidos, r.Fecha_Entrada, r.Fecha_Salida
                FROM CheckIns ci
                JOIN Reservaciones r ON ci.ID_Reserva = r.ID_Reserva
                JOIN Habitaciones h ON r.ID_Habitacion = h.ID_Habitacion
                JOIN Clientes c ON r.ID_Cliente = c.ID_Cliente
                WHERE ci.Estado = 'Activo'
                ORDER BY h.Numero
            """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String item = rs.getInt("ID_Reserva") + " - Hab. " + rs.getInt("Numero") + " - " +
                              rs.getString("Nombres") + " " + rs.getString("Apellidos") + " (" +
                              new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("Fecha_Entrada")) + ")";
                cmbReservaciones.addItem(item);
            }

            if (cmbReservaciones.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay check-ins activos.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar check-ins: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDetallesCompletos() {
        limpiarDetalles();
        if (cmbReservaciones.getSelectedItem() == null) return;

        try (Connection conn = DBConnection.getConnection()) {
            int reservaId = Integer.parseInt(cmbReservaciones.getSelectedItem().toString().split(" - ")[0]);

            String sql = """
                SELECT r.Estado, r.Tipo_Reserva, h.Precio_Dia, h.Precio_Hora, h.Numero, ci.Hora_Entrada
                FROM CheckIns ci
                JOIN Reservaciones r ON ci.ID_Reserva = r.ID_Reserva
                JOIN Habitaciones h ON r.ID_Habitacion = h.ID_Habitacion
                WHERE ci.ID_Reserva = ? AND ci.Estado = 'Activo'
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, reservaId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String estado = rs.getString("Estado");
                    String tipo = rs.getString("Tipo_Reserva");
                    Timestamp entrada = rs.getTimestamp("Hora_Entrada");
                    double precio = tipo.equals("Dia") ? rs.getDouble("Precio_Dia") : rs.getDouble("Precio_Hora");

                    long unidades = tipo.equals("Dia")
                        ? Math.max(1, (System.currentTimeMillis() - entrada.getTime()) / (1000 * 60 * 60 * 24))
                        : Math.max(1, (System.currentTimeMillis() - entrada.getTime()) / (1000 * 60 * 60));

                    double total = unidades * precio;

                    lblEstadoReserva.setText(estado);
                    lblHoraEntrada.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(entrada));
                    lblDetallePrecio.setText(String.format("$%.2f por %s", precio, tipo.equals("Dia") ? "día" : "hora"));
                    lblTotal.setText(String.format("$%.2f", total));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione una reservación", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar check-out?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int reservaId = Integer.parseInt(cmbReservaciones.getSelectedItem().toString().split(" - ")[0]);

            String detallesSQL = """
                SELECT h.Numero, r.Tipo_Reserva, h.Precio_Dia, h.Precio_Hora, ci.Hora_Entrada
                FROM Reservaciones r
                JOIN Habitaciones h ON r.ID_Habitacion = h.ID_Habitacion
                JOIN CheckIns ci ON r.ID_Reserva = ci.ID_Reserva
                WHERE r.ID_Reserva = ?
            """;

            String numero = "";
            double precio = 0;
            long unidades = 0;
            String tipo = "";

            try (PreparedStatement ps = conn.prepareStatement(detallesSQL)) {
                ps.setInt(1, reservaId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    numero = rs.getString("Numero");
                    tipo = rs.getString("Tipo_Reserva");
                    precio = tipo.equals("Dia") ? rs.getDouble("Precio_Dia") : rs.getDouble("Precio_Hora");
                    Timestamp entrada = rs.getTimestamp("Hora_Entrada");
                    unidades = tipo.equals("Dia")
                        ? Math.max(1, (System.currentTimeMillis() - entrada.getTime()) / (1000 * 60 * 60 * 24))
                        : Math.max(1, (System.currentTimeMillis() - entrada.getTime()) / (1000 * 60 * 60));
                }
            }

            PreparedStatement finCheckIn = conn.prepareStatement(
                "UPDATE CheckIns SET Estado = 'Finalizado', Hora_Salida = GETDATE() WHERE ID_Reserva = ? AND Estado = 'Activo'");
            finCheckIn.setInt(1, reservaId);
            finCheckIn.executeUpdate();

            PreparedStatement updateReserva = conn.prepareStatement(
                "UPDATE Reservaciones SET Estado = 'Finalizada' WHERE ID_Reserva = ?");
            updateReserva.setInt(1, reservaId);
            updateReserva.executeUpdate();

            PreparedStatement liberar = conn.prepareStatement("""
                UPDATE Habitaciones SET Estado = 'Disponible' 
                WHERE ID_Habitacion = (SELECT ID_Habitacion FROM Reservaciones WHERE ID_Reserva = ?)
            """);
            liberar.setInt(1, reservaId);
            liberar.executeUpdate();

            conn.commit();

            mostrarResumenCheckOut(numero, precio, unidades, precio * unidades);
            actualizarInterfaz();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al realizar check-out: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarResumenCheckOut(String habitacion, double precio, long unidades, double total) {
        String mensaje = String.format("""
            <html><b>Check-Out realizado con éxito</b><br><br>
            <table>
            <tr><td>Habitación:</td><td>%s</td></tr>
            <tr><td>Precio por unidad:</td><td>$%.2f</td></tr>
            <tr><td>Unidades:</td><td>%d</td></tr>
            <tr><td>Total a pagar:</td><td><b>$%.2f</b></td></tr>
            </table></html>""", habitacion, precio, unidades, total);
        JOptionPane.showMessageDialog(this, mensaje, "Resumen", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarInterfaz() {
        cargarReservacionesConCheckInActivo();
        limpiarDetalles();
    }
}
