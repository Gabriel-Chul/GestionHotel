# 🏨 Hotel Primavera - Sistema de Gestión Interna (App de Escritorio en Java)

**Hotel Primavera** es una aplicación de escritorio desarrollada en Java, diseñada específicamente para optimizar la gestión operativa del hotel desde el lado del empleado.  
Este sistema intuitivo y eficiente permite al personal realizar tareas clave como la gestión de reservas, control de habitaciones, atención al cliente y operaciones internas, todo desde una interfaz ágil y moderna.

---

## 🚀 Características destacadas (v1.0)

- 🔐 **Sistema de autenticación seguro** (login de empleados).
- 🗄️ **Estructura inicial de base de datos** implementada para almacenamiento confiable.
- 📊 **Dashboard de control** en desarrollo, con visualización de operaciones y estado general del hotel.

> Este proyecto está en fase de desarrollo y se encuentra en constante evolución para adaptarse a las necesidades reales del entorno hotelero.

---

## ✅ Requisitos del sistema

Para ejecutar esta aplicación, asegúrate de contar con lo siguiente:

- ☕ **Java JDK 17 o superior**  
- 🧰 **NetBeans IDE 12.6 o superior**  
- 🗃️ **Microsoft SQL Server** (cualquier edición compatible)  
- 📦 **Dependencias externas** (ya incluidas en el proyecto):  
  - `jfreechart-1.0.19.jar`  
  - `jcommon-1.0.23.jar`  
  - `jbcrypt-0.4.jar`  
  - `flatlaf-3.6.jar`  
  - Drivers JDBC para SQL Server (`mssql-jdbc-12.10.0.*.jar` y `.dll`)

---

## 🧭 Instrucciones para ejecutar el proyecto

1. **Clonar o descargar el repositorio**
   ```bash
   git clone https://github.com/usuario/hotel-primavera.git
   ```

2. **Abrir el proyecto en NetBeans**

3. **Configurar la base de datos**
   - Inicia SQL Server y crea la base de datos utilizando el script incluido (por ejemplo, en la carpeta `/sql`)
   - Asegúrate de que las credenciales y cadena de conexión estén correctas en la clase de conexión (ej: `DBConnection.java`)

4. **Ejecutar el proyecto**
   - Desde NetBeans, haz clic derecho en el proyecto y selecciona **"Run"**
   - La aplicación se abrirá mostrando la ventana de login

---

## 💡 Nota para colaboradores

Para realizar commits, push y otras operaciones con Git, **se recomienda usar [GitHub Desktop](https://desktop.github.com/)**, ya que la integración directa de Git con NetBeans puede generar problemas de autenticación y conexión con GitHub.

> ⚠️ Si tienes experiencia utilizando Git desde la terminal o desde NetBeans y te funciona sin errores, también es totalmente válido. Lo importante es mantener una buena sincronización con el repositorio remoto.

---

## 📌 Estado actual

- [x] Estructura general del proyecto
- [x] Base de datos creada y conectada
- [x] Login funcional
- [ ] Dashboard en progreso
- [ ] Módulos de reservas, habitaciones y clientes (pendientes)

---

## 📄 Licencia

Este proyecto es de uso académico. Todos los derechos reservados © 2025 Alfredo Gabriel Chul Moreno.
