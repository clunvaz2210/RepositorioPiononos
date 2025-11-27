package org.hlanz;

import org.hlanz.servlets.PastelService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

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
            conexion = DriverManager.getConnection(url,user,password);
            System.out.println("Conexión realizada");
        } catch (SQLException e) {
            System.out.println("No se conecto la bbdd " + e.getMessage());
        }
    }

    static public void cerrarConexion(){
        if(conexion!=null){
            try {
                conexion.close();
                System.out.println("Conexión cerrada");
            }catch (SQLException e){
                System.out.println("Error al cerrar conexion");
            }
        }
    }

    public static void main(String[] args){

        DriverManager.drivers().forEach(driver -> System.out.println(driver.toString()));
        realizarConexion();
        cerrarConexion();
        PastelService service = new PastelService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("||||||||||||API REST DE PASTELES - PRUEBA SIN SERVIDOR ||||||||||||\n");

        /*
        Vamos a simular las peticiones HTTP que le hariamos a un servidor web

        Lo siguiente seria hacer esto con servlets
        (clase de Java que se ejecuta en un servidor web)
         */
        while (true) {
            int opcion = Integer.parseInt(scanner.nextLine());
            System.out.println();

            switch (opcion) {
                case 1:
                    // 1. GET - Obtener todos los pasteles
                    System.out.println("1. GET /api/pasteles - Obtener todos los pasteles:");
                    System.out.println(formatJson(service.obtenerTodos()));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 2:
                    // 2. GET - Obtener un pastel específico (ID 2)
                    System.out.println("2. GET /api/pasteles/2 - Obtener pastel con ID 2:");
                    System.out.println(formatJson(service.obtenerPorId(2L)));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 3:
                    // 3. POST - Crear un nuevo pastel
                    System.out.println("3. POST /api/pasteles - Crear nuevo pastel:");
                    String nuevoPastelJson = "{\"nombre\":\"Cheesecake\",\"sabor\":\"Frutos rojos\",\"precio\":32.50,\"porciones\":16}";
                    System.out.println("Body enviado: " + formatJson(nuevoPastelJson));
                    String respuestaCrear = service.crear(nuevoPastelJson);
                    System.out.println("Respuesta: " + formatJson(respuestaCrear));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 4:
                    // 4. GET - Verificar que se creó (igual que opcion 1)
                    System.out.println("4. GET /api/pasteles - Verificar todos los pasteles:");
                    System.out.println(formatJson(service.obtenerTodos()));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 5:
                    // 5. PUT - Actualizar el pastel con ID 1
                    System.out.println("5. PUT /api/pasteles/1 - Actualizar pastel con ID 1:");
                    String actualizarJson = "{\"nombre\":\"Tres Leches Premium\",\"sabor\":\"Vainilla Extra\",\"precio\":35.00,\"porciones\":15}";
                    System.out.println("Body enviado: " + formatJson(actualizarJson));
                    String respuestaActualizar = service.actualizar(1L, actualizarJson);
                    System.out.println("Respuesta: " + formatJson(respuestaActualizar));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 6:
                    // 6. GET - Verificar actualización
                    System.out.println("6. GET /api/pasteles/1 - Verificar actualización:");
                    System.out.println(formatJson(service.obtenerPorId(1L)));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 7:
                    // 7. DELETE - Eliminar el pastel con ID 3
                    System.out.println("7. DELETE /api/pasteles/3 - Eliminar pastel con ID 3:");
                    String respuestaEliminar = service.eliminar(3L);
                    System.out.println("Respuesta: " + formatJson(respuestaEliminar));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 8:
                    // 8. GET - Verificar eliminación (igual que opcion 1)
                    System.out.println("8. GET /api/pasteles - Verificar que se eliminó:");
                    System.out.println(formatJson(service.obtenerTodos()));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 9:
                    // 9. GET - Intentar obtener pastel eliminado
                    System.out.println("9. GET /api/pasteles/3 - Intentar obtener pastel eliminado:");
                    System.out.println(formatJson(service.obtenerPorId(3L)));
                    System.out.println("\n" + "=".repeat(60) + "\n");
                    break;
                case 10:
                    // 10. POST - Crear con datos inválidos
                    System.out.println("10. POST /api/pasteles - Crear con datos inválidos:");
                    String jsonInvalido = "{\"nombre\":\"Pastel sin datos}";
                    System.out.println("Body enviado: " + jsonInvalido);
                    String respuestaError = service.crear(jsonInvalido);
                    System.out.println("Respuesta: " + formatJson(respuestaError));
                    break;
                default:
                    System.out.println("❌ Opción inválida. Intenta de nuevo.\n");
            }
        }//fin while

    }// fin Main


    // Método auxiliar para formatear JSON (indentación simple)
    private static String formatJson(String json) {
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        boolean inString = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{' || c == '[') {
                    formatted.append(c).append('\n');
                    indentLevel++;
                    formatted.append("  ".repeat(indentLevel));
                } else if (c == '}' || c == ']') {
                    formatted.append('\n');
                    indentLevel--;
                    formatted.append("  ".repeat(indentLevel));
                    formatted.append(c);
                } else if (c == ',') {
                    formatted.append(c).append('\n');
                    formatted.append("  ".repeat(indentLevel));
                } else if (c == ':') {
                    formatted.append(c).append(' ');
                } else if (c != ' ') {
                    formatted.append(c);
                }
            } else {
                formatted.append(c);
            }
        }
        return formatted.toString();
    }//Fin metodo auxiliar
}//fin clase
