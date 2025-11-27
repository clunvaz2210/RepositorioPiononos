package org.hlanz;

import org.hlanz.servlets.PastelService;

import java.sql.*;


public class Main {

    private static Connection conexion = null;
    private static final String url = "jdbc:postgresql://localhost:5433/Piononos";
    private static final String user = "postgres";
    private static final String password = "admin";

    static public void realizarConexion() {
        try {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión realizada");
        } catch (SQLException e) {
            System.out.println("No se conectó la bbdd " + e.getMessage());
        }
    }

    static public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión");
            }
        }
    }

    public static void insertarPastel(String nombre, String sabor, double precio, int porciones) {
        String sql = "INSERT INTO pasteles(nombre, sabor, precio, porciones) VALUES (?, ?, ?, ?)";
        PreparedStatement consultaInsert = null;

        try {
            consultaInsert = conexion.prepareStatement(sql);
            consultaInsert.setString(1, nombre);
            consultaInsert.setString(2, sabor);
            consultaInsert.setDouble(3, precio);
            consultaInsert.setInt(4, porciones);

            int filasAfectadas = consultaInsert.executeUpdate();
            System.out.println("Se han insertado " + filasAfectadas + " filas");
            System.out.println("Pastel '" + nombre + "' insertado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al insertar pastel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (consultaInsert != null) {
                try {
                    consultaInsert.close();
                } catch (SQLException e) {
                    System.out.println("Error cerrando el PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    public static void seleccionarPasteles() {
        String sql = "SELECT * FROM pasteles";
        Statement consulta = null;
        ResultSet resultado = null;

        try {
            consulta = conexion.createStatement();
            resultado = consulta.executeQuery(sql);

            System.out.println("\n=== LISTADO DE PASTELES ===");
            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String sabor = resultado.getString("sabor");
                double precio = resultado.getDouble("precio");
                int porciones = resultado.getInt("porciones");

                System.out.println("ID: " + id + " | PASTEL: " + nombre +
                        " | SABOR: " + sabor +
                        " | PRECIO: $" + precio +
                        " | PORCIONES: " + porciones);
            }
            System.out.println("===========================\n");
        } catch (SQLException e) {
            System.out.println("Error al seleccionar pasteles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultado != null) resultado.close();
                if (consulta != null) consulta.close();
            } catch (SQLException e) {
                System.out.println("Error cerrando recursos: " + e.getMessage());
            }
        }
    }

    public static void eliminarPastel(int id) {
        String sql = "DELETE FROM pasteles WHERE id = ?";
        PreparedStatement consultaDelete = null;

        try {
            consultaDelete = conexion.prepareStatement(sql);
            consultaDelete.setInt(1, id);

            int filasAfectadas = consultaDelete.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Pastel con ID " + id + " eliminado correctamente. Filas afectadas: " + filasAfectadas);
            } else {
                System.out.println("No se encontró ningún pastel con ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar pastel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (consultaDelete != null) {
                try {
                    consultaDelete.close();
                } catch (SQLException e) {
                    System.out.println("Error cerrando el PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    public static void actualizarPastel(int id, String nombre, String sabor, double precio, int porciones) {
        String sql = "UPDATE pasteles SET nombre = ?, sabor = ?, precio = ?, porciones = ? WHERE id = ?";
        PreparedStatement consultaUpdate = null;

        try {
            consultaUpdate = conexion.prepareStatement(sql);
            consultaUpdate.setString(1, nombre);
            consultaUpdate.setString(2, sabor);
            consultaUpdate.setDouble(3, precio);
            consultaUpdate.setInt(4, porciones);
            consultaUpdate.setInt(5, id);

            int filasAfectadas = consultaUpdate.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Pastel con ID " + id + " actualizado correctamente. Filas afectadas: " + filasAfectadas);
            } else {
                System.out.println("No se encontró ningún pastel con ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar pastel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (consultaUpdate != null) {
                try {
                    consultaUpdate.close();
                } catch (SQLException e) {
                    System.out.println("Error cerrando el PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        realizarConexion();

        insertarPastel("Tres Leches", "Vainilla", 25.50, 12);

        seleccionarPasteles();

        // actualizarPastel(1, "Tres Leches Premium", "Vainilla Extra", 30.00, 12);

        // eliminarPastel(3);

        cerrarConexion();
    }
}