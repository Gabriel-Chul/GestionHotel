import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Login extends JFrame {
    private JTextField userText;
    private JPasswordField passwordField;

    public Login() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.out.println("Error al cargar FlatLaf: " + e.getMessage());
        }

        setTitle("Login de Hotel - Ultra Pro 3D");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo con imagen y overlay de color para efecto blur
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(950, 650));
        setContentPane(layeredPane);

        try {
            URL imageUrl = new URL("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=950&q=80");
            ImageIcon backgroundIcon = new ImageIcon(imageUrl);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, 950, 650);
            layeredPane.add(backgroundLabel, Integer.valueOf(0));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }
        // Overlay para efecto blur y color
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 60, 120, 60));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setBounds(0, 0, 950, 650);
        overlay.setOpaque(false);
        layeredPane.add(overlay, Integer.valueOf(1));

        // Carousel de fotos de habitaciones (carga asíncrona robusta)
        JPanel carouselPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, getHeight()-30, new Color(0,0,0,60), 0, getHeight(), new Color(0,0,0,0)));
                g2.fillRect(0, getHeight()-30, getWidth(), 30);
            }
        };
        carouselPanel.setOpaque(false);
        carouselPanel.setBounds(570, 110, 340, 420);
        String[] urls = {
            "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=400&q=80",
            "https://images.unsplash.com/photo-1507089947368-19c1da9775ae?auto=format&fit=crop&w=400&q=80",
            "https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=400&q=80",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=400&q=80"
        };
        JLabel[] photos = new JLabel[urls.length];
        for (int i = 0; i < urls.length; i++) {
            photos[i] = new JLabel("Cargando...");
            photos[i].setBounds(10, 10, 320, 180);
            photos[i].setHorizontalAlignment(SwingConstants.CENTER);
            photos[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
            photos[i].setVisible(i == 0);
            carouselPanel.add(photos[i]);
            final int idxPhoto = i;
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    try {
                        ImageIcon icon = new ImageIcon(new URL(urls[idxPhoto]));
                        Image img = icon.getImage().getScaledInstance(320, 180, Image.SCALE_SMOOTH);
                        return new ImageIcon(img);
                    } catch (Exception e) {
                        return null;
                    }
                }
                @Override
                protected void done() {
                    try {
                        ImageIcon imgIcon = get();
                        if (imgIcon != null) {
                            photos[idxPhoto].setIcon(imgIcon);
                            photos[idxPhoto].setText("");
                        } else {
                            photos[idxPhoto].setText("Error al cargar");
                        }
                    } catch (Exception e) {
                        photos[idxPhoto].setText("Error al cargar");
                    }
                }
            }.execute();
        }
        // Botones para cambiar foto
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        prev.setBounds(10, 200, 50, 40);
        next.setBounds(280, 200, 50, 40);
        prev.setFont(new Font("Segoe UI", Font.BOLD, 22));
        next.setFont(new Font("Segoe UI", Font.BOLD, 22));
        prev.setBackground(new Color(0,0,0,80));
        next.setBackground(new Color(0,0,0,80));
        prev.setForeground(Color.WHITE);
        next.setForeground(Color.WHITE);
        prev.setFocusPainted(false);
        next.setFocusPainted(false);
        prev.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        next.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        carouselPanel.add(prev);
        carouselPanel.add(next);
        layeredPane.add(carouselPanel, Integer.valueOf(2));
        final int[] idx = {0};
        prev.addActionListener(e -> {
            photos[idx[0]].setVisible(false);
            idx[0] = (idx[0] - 1 + photos.length) % photos.length;
            photos[idx[0]].setVisible(true);
        });
        next.addActionListener(e -> {
            photos[idx[0]].setVisible(false);
            idx[0] = (idx[0] + 1) % photos.length;
            photos[idx[0]].setVisible(true);
        });

        // Panel central 3D con reflejo
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra doble para efecto 3D
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(18, 18, getWidth() - 36, getHeight() - 36, 60, 60);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(9, 9, getWidth() - 18, getHeight() - 18, 60, 60);
                // Gradiente translúcido
                GradientPaint gp = new GradientPaint(0, 0, new Color(255,255,255,245), getWidth(), getHeight(), new Color(220,230,255,220));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 60, 60);
                // Reflejo superior
                GradientPaint reflection = new GradientPaint(0, 0, new Color(255,255,255,120), 0, 60, new Color(255,255,255,0));
                g2.setPaint(reflection);
                g2.fillRoundRect(0, 0, getWidth() - 8, 60, 60, 60);
                g2.dispose();
            }
        };
        glassPanel.setLayout(null);
        glassPanel.setBounds(120, 110, 370, 430);
        glassPanel.setOpaque(false);
        layeredPane.add(glassPanel, Integer.valueOf(3));

        // Avatar circular con borde, sombra y brillo (carga asíncrona robusta)
        JLabel avatar = new JLabel("Cargando...");
        avatar.setBounds(135, 10, 100, 100);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        glassPanel.add(avatar);
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    Image img = new ImageIcon(new URL("https://randomuser.me/api/portraits/men/32.jpg")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    return new ImageIcon(makeRoundedCornerWithGlow(img, 100));
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        avatar.setIcon(icon);
                        avatar.setText("");
                    } else {
                        avatar.setText("Error");
                    }
                } catch (Exception e) {
                    avatar.setText("Error");
                }
            }
        }.execute();

        // Título
        JLabel titleLabel = new JLabel("Bienvenido al Hotel");
        titleLabel.setBounds(30, 120, 310, 36);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        glassPanel.add(titleLabel);

        // Usuario con ícono
        JLabel userIcon = new JLabel();
        try {
            userIcon.setIcon(new ImageIcon(new URL("https://cdn-icons-png.flaticon.com/512/1077/1077114.png")));
        } catch (Exception e) {}
        userIcon.setBounds(40, 180, 32, 32);
        glassPanel.add(userIcon);

        userText = new JTextField();
        userText.setBounds(80, 180, 250, 32);
        userText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        userText.setOpaque(false);
        glassPanel.add(userText);

        // Contraseña con ícono
        JLabel passIcon = new JLabel();
        try {
            passIcon.setIcon(new ImageIcon(new URL("https://cdn-icons-png.flaticon.com/512/3064/3064155.png")));
        } catch (Exception e) {}
        passIcon.setBounds(40, 230, 32, 32);
        glassPanel.add(passIcon);

        passwordField = new JPasswordField();
        passwordField.setBounds(80, 230, 250, 32);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passwordField.setOpaque(false);
        glassPanel.add(passwordField);

        // Botón de Login con efecto 3D y animación de presión
        JButton loginButton = new JButton("Iniciar Sesión") {
            private Color hover = new Color(0, 102, 204);
            private Color normal = new Color(0, 123, 255);
            private boolean pressed = false;
            {
                setBackground(normal);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 18));
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                setContentAreaFilled(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setBackground(hover);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setBackground(normal);
                        pressed = false;
                        repaint();
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int yOffset = pressed ? 4 : 0;
                // Sombra
                g2.setColor(new Color(0,0,0,60));
                g2.fillRoundRect(0, 6, getWidth(), getHeight()-4, 20, 20);
                // Botón
                g2.setColor(getBackground());
                g2.fillRoundRect(0, yOffset, getWidth(), getHeight()-6, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        loginButton.setBounds(105, 290, 160, 50);
        loginButton.setHorizontalAlignment(SwingConstants.CENTER);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setOpaque(false);
        glassPanel.add(loginButton);

        // Acción del botón
        loginButton.addActionListener((ActionEvent e) -> {
            String username = userText.getText();
            String password = new String(passwordField.getPassword());
            if ("admin".equals(username) && "1234".equals(password)) {
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Pie de página
        JLabel footer = new JLabel("© 2025 Hotel Pro. Todos los derechos reservados.");
        footer.setBounds(0, 610, 950, 30);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setForeground(new Color(255,255,255,180));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        layeredPane.add(footer, Integer.valueOf(4));

        // Animación de entrada del panel central
        Timer timer = new Timer(1, null);
        timer.addActionListener(new ActionListener() {
            int y = 650;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (y > 110) {
                    y -= 20;
                    glassPanel.setLocation(120, y);
                } else {
                    glassPanel.setLocation(120, 110);
                    timer.stop();
                }
            }
        });
        glassPanel.setLocation(120, 650);
        timer.start();

        setVisible(true);
    }

    // Utilidad para hacer imagen circular con brillo
    private static Image makeRoundedCornerWithGlow(Image image, int diameter) {
        BufferedImage mask = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mask.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillOval(0, 0, diameter, diameter);
        g2.dispose();

        BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2 = masked.createGraphics();
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, diameter, diameter));
        g2.drawImage(image, 0, 0, diameter, diameter, null);
        // Glow
        g2.setClip(null);
        g2.setColor(new Color(0,102,204,60));
        g2.setStroke(new BasicStroke(8f));
        g2.drawOval(4, 4, diameter-8, diameter-8);
        g2.dispose();
        return masked;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}