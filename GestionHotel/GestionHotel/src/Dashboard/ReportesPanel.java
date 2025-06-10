package Dashboard;

import Config.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportesPanel extends JPanel {

    private JLabel lblReservas, lblCheckInsActivos, lblHabitacionesDisponibles, lblCheckInsFinalizados;
    private DefaultPieDataset pieDataset;

    public ReportesPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Título
        JLabel lblTitle = new JLabel("REPORTES GENERALES");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Panel lateral de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Resumen numérico"));
        statsPanel.setBackground(Color.WHITE);

        lblReservas = crearLabel("Cargando...");
        lblCheckInsActivos = crearLabel("Cargando...");
        lblHabitacionesDisponibles = crearLabel("Cargando...");
        lblCheckInsFinalizados = crearLabel("Cargando...");

        statsPanel.add(lblReservas);
        statsPanel.add(lblCheckInsActivos);
        statsPanel.add(lblCheckInsFinalizados);
        statsPanel.add(lblHabitacionesDisponibles);

        // Dataset inicial para gráfico
        pieDataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
            "Distribución General", // título
            pieDataset,             // datos
            true,                   // leyenda
            true,                   // tooltips
            false                   // URLs
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        // Contenedor central
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(statsPanel, BorderLayout.WEST);
        centerPanel.add(chartPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        cargarDatos();
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private void cargarDatos() {
        String sqlReservas   = "SELECT COUNT(*) FROM Reservaciones";
        String sqlActivos    = "SELECT COUNT(*) FROM CheckIns WHERE Estado = 'Activo'";
        String sqlFinalizados= "SELECT COUNT(*) FROM CheckIns WHERE Estado = 'Finalizado'";
        String sqlDisponibles= "SELECT COUNT(*) FROM Habitaciones WHERE Estado = 'Disponible'";

        try (Connection conn = DBConnection.getConnection()) {
            int totalReservas = contar(conn, sqlReservas);
            int activos = contar(conn, sqlActivos);
            int finalizados = contar(conn, sqlFinalizados);
            int disponibles = contar(conn, sqlDisponibles);

            // Actualizar etiquetas
            lblReservas.setText("Reservaciones totales: " + totalReservas);
            lblCheckInsActivos.setText("Check-Ins activos: " + activos);
            lblCheckInsFinalizados.setText("Check-Ins finalizados: " + finalizados);
            lblHabitacionesDisponibles.setText("Habitaciones disponibles: " + disponibles);

            // Actualizar gráfico
            pieDataset.setValue("Reservas", totalReservas);
            pieDataset.setValue("Check-Ins Activos", activos);
            pieDataset.setValue("Check-Ins Finalizados", finalizados);
            pieDataset.setValue("Habitaciones Disponibles", disponibles);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar reportes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int contar(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
