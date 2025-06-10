package Dashboard;

import Config.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DisponibilidadPanel extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblEstadoActual;
    private JComboBox<String> comboEstados;
    private JButton btnActualizar, btnCambiar;

    public DisponibilidadPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Gestión de Habitaciones - Disponibilidad");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        add(titulo, BorderLayout.NORTH);

        // Modelo y tabla
        modelo = new DefaultTableModel(new Object[]{"ID", "N°", "Tipo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);

        // Alinear al centro
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Panel Izquierdo - Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Habitaciones"));
        panelTabla.setBackground(Color.WHITE);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel Derecho - Detalles y acciones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Acciones"));
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setPreferredSize(new Dimension(300, 0));

        lblEstadoActual = new JLabel("Estado actual: ");
        lblEstadoActual.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEstadoActual.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEstadoActual.setForeground(new Color(50, 50, 50));

        comboEstados = new JComboBox<>(new String[]{"Disponible", "Ocupada", "Mantenimiento"});
        comboEstados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboEstados.setMaximumSize(new Dimension(200, 30));
        comboEstados.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCambiar = new JButton("Cambiar Estado");
        btnActualizar = new JButton("Actualizar Listado");

        for (JButton btn : new JButton[]{btnCambiar, btnActualizar}) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(new Color(33, 150, 243));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(200, 35));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        panelDerecho.add(Box.createVerticalStrut(20));
        panelDerecho.add(lblEstadoActual);
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(comboEstados);
        panelDerecho.add(Box.createVerticalStrut(20));
        panelDerecho.add(btnCambiar);
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(btnActualizar);
        panelDerecho.add(Box.createVerticalGlue());

        // Panel central dividido
        JPanel panelCentral = new JPanel(new BorderLayout(15, 0));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        panelCentral.add(panelDerecho, BorderLayout.EAST);

        add(panelCentral, BorderLayout.CENTER);

        // Eventos
        btnActualizar.addActionListener(e -> cargarHabitaciones());

        btnCambiar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una habitación.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) modelo.getValueAt(fila, 0);
            String nuevoEstado = (String) comboEstados.getSelectedItem();

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Habitaciones SET Estado = ? WHERE ID_Habitacion = ?");
                stmt.setString(1, nuevoEstado);
                stmt.setInt(2, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Estado actualizado.");
                cargarHabitaciones();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                String estado = (String) modelo.getValueAt(fila, 3);
                lblEstadoActual.setText("Estado actual: " + estado);
                comboEstados.setSelectedItem(estado);
            }
        });

        cargarHabitaciones();
    }

    private void cargarHabitaciones() {
        modelo.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT H.ID_Habitacion, H.Numero, T.Nombre AS Tipo, H.Estado FROM Habitaciones H JOIN Tipos_Habitacion T ON H.ID_Tipo = T.ID_Tipo";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("ID_Habitacion"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getString("Estado")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar habitaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

