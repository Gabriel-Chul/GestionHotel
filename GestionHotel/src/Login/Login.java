import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.net.URL;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Login extends JFrame {
    public Login() {
        try {
            // Activar tema moderno FlatLaf
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.out.println("Error al cargar FlatLaf: " + e.getMessage());
        }

        // Configuración del JFrame
        setTitle("Login de Hotel - Profesional");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        // Crear panel principal
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 240, 240)); // Fondo claro
        add(panel);

        try {
            // Imagen de fondo desde URL
            URL imageUrl = new URL("https://example.com/hotel-background.jpg"); // Cambia esta URL por una válida
            ImageIcon backgroundIcon = new ImageIcon(imageUrl);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, 600, 400);
            panel.add(backgroundLabel);
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }

        // Etiqueta de título
        JLabel titleLabel = new JLabel("Bienvenido al Hotel");
        titleLabel.setBounds(200, 30, 300, 40);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204)); // Azul elegante
        panel.add(titleLabel);

        // Etiqueta Usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setBounds(150, 120, 100, 30);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(userLabel);

        // Campo de texto para usuario
        JTextField userText = new JTextField();
        userText.setBounds(250, 120, 200, 30);
        panel.add(userText);

        // Etiqueta Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(150, 180, 100, 30);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(passwordLabel);

        // Campo de texto para contraseña
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(250, 180, 200, 30);
        panel.add(passwordField);

        // Botón de Login
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(250, 240, 200, 40);
        loginButton.setBackground(new Color(0, 123, 255)); // Azul moderno
        loginButton.setForeground(Color.WHITE); // Texto blanco
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        panel.add(loginButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }
}