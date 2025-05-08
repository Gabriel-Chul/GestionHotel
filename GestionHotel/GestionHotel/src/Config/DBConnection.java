package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try (InputStream input = DBConnection.class.getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo dbconfig.properties en el paquete Config.");
            }
            Properties prop = new Properties();
            prop.load(input);
            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASS = prop.getProperty("db.pass");

            if (URL == null || USER == null || PASS == null) {
                throw new RuntimeException("Faltan propiedades en dbconfig.properties.");
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la configuración de la base de datos: " + e.getMessage());
            URL = null;
            USER = null;
            PASS = null;
        }
    }

    public static Connection getConnection() {
        if (URL == null || USER == null || PASS == null) {
            System.err.println("Configuración de base de datos inválida. Revise dbconfig.properties.");
            return null;
        }
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de SQL Server.");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al conectar a la base de datos: " + e.getMessage());
        }
        return null;
    }
}