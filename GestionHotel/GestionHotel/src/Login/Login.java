package Login;

import Dao.UsuarioDAO;
import Dashboard.Dashboard;
import javax.swing.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Login extends JFrame {
    private final JTextField userText;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public Login() {
        super("Login de Hotel - Ultra Pro 3D");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Error al cargar FlatLaf: " + e.getMessage());
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(950, 650));
        setContentPane(layeredPane);

        // Fondo con imagen
        try {
            URL imageUrl = new URL("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=950&q=80");
            ImageIcon backgroundIcon = new ImageIcon(imageUrl);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, 950, 650);
            layeredPane.add(backgroundLabel, Integer.valueOf(0));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
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

        // Carousel de fotos de habitaciones
        JPanel carouselPanel = crearCarouselPanel();
        layeredPane.add(carouselPanel, Integer.valueOf(2));

        // Panel central 3D con reflejo
        JPanel glassPanel = crearGlassPanel();
        layeredPane.add(glassPanel, Integer.valueOf(3));

        // Pie de página
        JLabel footer = new JLabel("© 2025 Hotel Primavera. Todos los derechos reservados.");
        footer.setBounds(0, 610, 950, 30);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setForeground(new Color(255,255,255,180));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        layeredPane.add(footer, Integer.valueOf(4));

        // Animación de entrada del panel central
        animarEntradaPanel(glassPanel);

        setVisible(true);

        // --- Componentes del panel de login ---
        userText = new JTextField();
        passwordField = new JPasswordField();
        loginButton = crearBotonLogin();

        // Avatar
        JLabel avatar = new JLabel("Cargando...");
        avatar.setBounds(135, 10, 100, 100);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        glassPanel.add(avatar);
        cargarAvatarAsync(avatar);

        // Título
        JLabel titleLabel = new JLabel("Bienvenido al Hotel Primavera");
        titleLabel.setBounds(30, 120, 310, 36);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        glassPanel.add(titleLabel);

        // Usuario
        JLabel userIcon = new JLabel();
        try {
            userIcon.setIcon(new ImageIcon(new URL("https://cdn-icons-png.flaticon.com/512/1077/1077114.png")));
        } catch (Exception e) {}
        userIcon.setBounds(40, 180, 32, 32);
        glassPanel.add(userIcon);

        userText.setBounds(80, 180, 250, 32);
        userText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        userText.setOpaque(false);
        glassPanel.add(userText);

        // Contraseña
        JLabel passIcon = new JLabel();
        try {
            passIcon.setIcon(new ImageIcon(new URL("https://cdn-icons-png.flaticon.com/512/3064/3064155.png")));
        } catch (Exception e) {}
        passIcon.setBounds(40, 230, 32, 32);
        glassPanel.add(passIcon);

        passwordField.setBounds(80, 230, 250, 32);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passwordField.setOpaque(false);
        glassPanel.add(passwordField);

        // Botón de login
        loginButton.setBounds(105, 290, 160, 50);
        glassPanel.add(loginButton);

        // Accesibilidad con teclado
        userText.addActionListener(e -> loginButton.doClick());
        passwordField.addActionListener(e -> loginButton.doClick());
        getRootPane().setDefaultButton(loginButton);

        // Acción del botón de login
        loginButton.addActionListener(e -> procesarLogin());

        // Mover el foco al campo de usuario al iniciar
        userText.requestFocusInWindow();
    }

    private JPanel crearCarouselPanel() {
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
        return carouselPanel;
    }

    private JPanel crearGlassPanel() {
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(18, 18, getWidth() - 36, getHeight() - 36, 60, 60);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(9, 9, getWidth() - 18, getHeight() - 18, 60, 60);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255,255,255,245), getWidth(), getHeight(), new Color(220,230,255,220));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 60, 60);
                GradientPaint reflection = new GradientPaint(0, 0, new Color(255,255,255,120), 0, 60, new Color(255,255,255,0));
                g2.setPaint(reflection);
                g2.fillRoundRect(0, 0, getWidth() - 8, 60, 60, 60);
                g2.dispose();
            }
        };
        glassPanel.setLayout(null);
        glassPanel.setBounds(120, 110, 370, 430);
        glassPanel.setOpaque(false);
        return glassPanel;
    }

    private void cargarAvatarAsync(JLabel avatar) {
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
    }

    private JButton crearBotonLogin() {
        return new JButton("Iniciar Sesión") {
            private final Color hover = new Color(0, 102, 204);
            private final Color normal = new Color(0, 123, 255);
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
                g2.setColor(new Color(0,0,0,60));
                g2.fillRoundRect(0, 6, getWidth(), getHeight()-4, 20, 20);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, yOffset, getWidth(), getHeight()-6, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
    }

    private void animarEntradaPanel(JPanel glassPanel) {
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
    }

    private void procesarLogin() {
        String username = userText.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validación: campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Debe completar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: usuario solo letras y números
        if (!username.matches("[a-zA-Z0-9]+")) {
            mostrarMensaje("El usuario solo puede contener letras y números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: longitud mínima usuario
        if (username.length() < 4) {
            mostrarMensaje("El usuario debe tener al menos 4 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: longitud máxima usuario
        if (username.length() > 20) {
            mostrarMensaje("El usuario no puede tener más de 20 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: contraseña mínima
        if (password.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: contraseña máxima
        if (password.length() > 32) {
            mostrarMensaje("La contraseña no puede tener más de 32 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: contraseña segura (mayúscula, minúscula y número)
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            mostrarMensaje("La contraseña debe contener mayúsculas, minúsculas y números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: no permitir espacios en usuario ni contraseña
        if (username.contains(" ") || password.contains(" ")) {
            mostrarMensaje("Usuario y contraseña no pueden contener espacios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: usuario y contraseña no pueden ser iguales
        if (username.equals(password)) {
            mostrarMensaje("El usuario y la contraseña no pueden ser iguales.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: contraseña no puede ser igual a "password"
        if (password.equalsIgnoreCase("password")) {
            mostrarMensaje("La contraseña no puede ser 'password'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación real contra la base de datos
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.autenticar(username, password)) {
            mostrarMensaje("Inicio de sesión exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Aquí puedes abrir la siguiente ventana
            this.dispose();
            new Dashboard();
        } else {
            mostrarMensaje("Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
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