package org.hlanz.repository;

import org.hlanz.entity.Pastel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PastelRepository {
    private static PastelRepository instance;

    // Configura tu URL según el puerto correcto de PostgreSQL
    private final String url = "jdbc:postgresql://localhost:5433/Piononos"; // Cambia 5433 si tu servidor es otro
    private final String user = "postgres"; // tu usuario
    private final String password = "admin"; // tu contraseña

    private PastelRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PastelRepository getInstance() {
        if (instance == null) {
            instance = new PastelRepository();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Pastel> obtenerTodos() {
        List<Pastel> pasteles = new ArrayList<>();
        String sql = "SELECT * FROM pasteles";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pasteles.add(new Pastel(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("sabor"),
                        rs.getDouble("precio"),
                        rs.getInt("porciones")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pasteles;
    }

    public Pastel obtenerPorId(Long id) {
        String sql = "SELECT * FROM pasteles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Pastel(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("sabor"),
                        rs.getDouble("precio"),
                        rs.getInt("porciones")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Pastel crear(Pastel pastel) {
        String sql = "INSERT INTO pasteles(nombre, sabor, precio, porciones) VALUES(?,?,?,?) RETURNING id";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pastel.getNombre());
            pstmt.setString(2, pastel.getSabor());
            pstmt.setDouble(3, pastel.getPrecio());
            pstmt.setInt(4, pastel.getPorciones());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pastel.setId(rs.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pastel;
    }

    public Pastel actualizar(Long id, Pastel pastel) {
        String sql = "UPDATE pasteles SET nombre=?, sabor=?, precio=?, porciones=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pastel.getNombre());
            pstmt.setString(2, pastel.getSabor());
            pstmt.setDouble(3, pastel.getPrecio());
            pstmt.setInt(4, pastel.getPorciones());
            pstmt.setLong(5, id);

            int afectadas = pstmt.executeUpdate();
            if (afectadas > 0) {
                pastel.setId(id);
                return pastel;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean eliminar(Long id) {
        String sql = "DELETE FROM pasteles WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
