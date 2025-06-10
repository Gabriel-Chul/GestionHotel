package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

public class ReservacionesPanel extends JPanel {
    private JTextField txtNombres, txtApellidos, txtDUI, txtTelefono, txtEmail;
    private JComboBox<String> cmbHabitaciones;
    private JSpinner fechaEntrada, fechaSalida;
    private JButton btnGuardar;

    public ReservacionesPanel() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nueva Reservación"));

        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtDUI = new JTextField();
        txtTelefono = new JTextField();
        txtEmail = new JTextField();
        cmbHabitaciones = new JComboBox<>();

        fechaEntrada = new JSpinner(new SpinnerDateModel());
        fechaEntrada.setEditor(new JSpinner.DateEditor(fechaEntrada, "yyyy-MM-dd HH:mm"));

        fechaSalida = new JSpinner(new SpinnerDateModel());
        fechaSalida.setEditor(new JSpinner.DateEditor(fechaSalida, "yyyy-MM-dd HH:mm"));

        btnGuardar = new JButton("Guardar Reservación");
        btnGuardar.addActionListener(e -> guardarReservacion());

        formPanel.add(new JLabel("Nombres:"));
        formPanel.add(txtNombres);

        formPanel.add(new JLabel("Apellidos:"));
        formPanel.add(txtApellidos);

        formPanel.add(new JLabel("DUI:"));
        formPanel.add(txtDUI);

        formPanel.add(new JLabel("Teléfono:"));
        formPanel.add(txtTelefono);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Habitación:"));
        formPanel.add(cmbHabitaciones);

        formPanel.add(new JLabel("Fecha Entrada:"));
        formPanel.add(fechaEntrada);

        formPanel.add(new JLabel("Fecha Salida:"));
        formPanel.add(fechaSalida);

        add(formPanel, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);

        cargarHabitacionesDisponibles();
    }

    private void cargarHabitacionesDisponibles() {
        cmbHabitaciones.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ID_Habitacion, Numero FROM Habitaciones WHERE Estado = 'Disponible'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cmbHabitaciones.addItem(rs.getInt("ID_Habitacion") + " - Habitación " + rs.getInt("Numero"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar habitaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarReservacion() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dui = txtDUI.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || dui.isEmpty() || telefono.isEmpty() || email.isEmpty() || cmbHabitaciones.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date entrada = (Date) fechaEntrada.getValue();
        Date salida = (Date) fechaSalida.getValue();
        if (!salida.after(entrada)) {
            JOptionPane.showMessageDialog(this, "La fecha de salida debe ser posterior a la de entrada.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Insertar cliente
                String insertClienteSQL = "INSERT INTO Clientes (Nombres, Apellidos, DUI, Email, Telefono) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psCliente = conn.prepareStatement(insertClienteSQL, Statement.RETURN_GENERATED_KEYS);
                psCliente.setString(1, nombres);
                psCliente.setString(2, apellidos);
                psCliente.setString(3, dui);
                psCliente.setString(4, email);
                psCliente.setString(5, telefono);
                psCliente.executeUpdate();

                ResultSet rsCliente = psCliente.getGeneratedKeys();
                if (!rsCliente.next()) throw new SQLException("No se pudo obtener el ID del cliente.");
                int idCliente = rsCliente.getInt(1);

                // Insertar reservación
                String idHab = cmbHabitaciones.getSelectedItem().toString().split(" - ")[0];
                String tipoReserva = "Dia"; // Por defecto
                String estado = "Confirmada";
                BigDecimal totalPagar = BigDecimal.ZERO;
                int usuarioRegistro = 2; // ID del usuario logueado (puede adaptarse)

                String insertReservaSQL = "INSERT INTO Reservaciones (ID_Cliente, ID_Habitacion, Fecha_Entrada, Fecha_Salida, Tipo_Reserva, Estado, Total_Pagar, Usuario_Registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psReserva = conn.prepareStatement(insertReservaSQL);
                psReserva.setInt(1, idCliente);
                psReserva.setInt(2, Integer.parseInt(idHab));
                psReserva.setTimestamp(3, new java.sql.Timestamp(entrada.getTime()));
                psReserva.setTimestamp(4, new java.sql.Timestamp(salida.getTime()));
                psReserva.setString(5, tipoReserva);
                psReserva.setString(6, estado);
                psReserva.setBigDecimal(7, totalPagar);
                psReserva.setInt(8, usuarioRegistro);
                psReserva.executeUpdate();

                conn.commit();

                JOptionPane.showMessageDialog(this, "Reservación guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarHabitacionesDisponibles();

            } catch (SQLException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Error al guardar reservación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDUI.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        fechaEntrada.setValue(new Date());
        fechaSalida.setValue(new Date());
    }
}
