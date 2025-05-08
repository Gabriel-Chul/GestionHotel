package Dashboard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        super("Sistema de Hotel - Recepción");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menú lateral moderno
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(30, 39, 46));
        menuPanel.setPreferredSize(new Dimension(240, getHeight()));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel menuTitle = new JLabel("Menú Principal");
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        menuTitle.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 10));
        menuPanel.add(menuTitle);

        String[] opciones = {"Reservaciones", "Disponibilidad", "Check-In", "Check-Out", "Reportes"};
        for (String op : opciones) {
            JButton btn = new JButton(op);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 48));
            btn.setMinimumSize(new Dimension(180, 48));
            btn.setBackground(new Color(45, 52, 54));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 18, 0),
                    btn.getBorder()
            ));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(0, 151, 230));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(45, 52, 54));
                }
            });
            menuPanel.add(btn);
        }
        menuPanel.add(Box.createVerticalGlue());
        add(menuPanel, BorderLayout.WEST);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 246, 250));

        // Panel de tarjetas métricas con mejor separación y sombra
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(new Color(245, 246, 250));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 30);

        cardsPanel.add(crearCard("HABITACIONES DISPONIBLES", "12", new Color(46, 204, 113), "🛏️"), gbc);
        gbc.gridx = 1;
        cardsPanel.add(crearCard("EN MANTENIMIENTO", "3", new Color(241, 196, 15), "🛠️"), gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardsPanel.add(crearCard("HUÉSPEDES ACTIVOS", "8", new Color(52, 152, 219), "👤"), gbc);

        mainPanel.add(cardsPanel, BorderLayout.NORTH);

        // Área de trabajo central con sombra y separación
        JPanel areaTrabajo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Sombra inferior
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new Color(0,0,0,30));
                g2.fillRoundRect(8, getHeight()-18, getWidth()-16, 16, 16, 16);
            }
        };
        areaTrabajo.setBackground(Color.WHITE);
        areaTrabajo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 40, 40, 40),
                BorderFactory.createLineBorder(new Color(220, 221, 225), 1)
        ));
        areaTrabajo.setLayout(new BorderLayout());

        JLabel areaLabel = new JLabel("Área de Trabajo");
        areaLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        areaLabel.setForeground(new Color(44, 62, 80));
        areaLabel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        areaTrabajo.add(areaLabel, BorderLayout.NORTH);

        mainPanel.add(areaTrabajo, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel crearCard(String titulo, String valor, Color color, String icono) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225), 2, true),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)
        ));
        card.setLayout(new BorderLayout());

        JLabel lblIcon = new JLabel(icono);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        lblIcon.setHorizontalAlignment(SwingConstants.LEFT);
        card.add(lblIcon, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(127, 140, 141));
        centerPanel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblValor.setForeground(color);
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(lblValor, BorderLayout.CENTER);

        card.add(centerPanel, BorderLayout.CENTER);

        // Sombra
        card.setOpaque(true);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}