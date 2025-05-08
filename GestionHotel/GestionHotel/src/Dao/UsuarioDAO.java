package Dao;

import Config.DBConnection;
import java.sql.*;

public class UsuarioDAO {
    public boolean autenticar(String usuario, String password) {
        String sql = "SELECT password_hash, activo FROM Usuarios WHERE usuario = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("No hay conexión a la base de datos.");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hash = rs.getString("password_hash");
                        boolean activo = rs.getBoolean("activo");
                        if (!activo) return false;
                        // Aquí deberías usar BCrypt para comparar el hash
                        // return BCrypt.checkpw(password, hash);
                        // Por ahora, solo para pruebas:
                        return password.equals(hash);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return false;
    }
}