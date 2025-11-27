package org.hlanz.servlets;



import org.hlanz.entity.Pastel;
import org.hlanz.mensaje.JsonUtil;
import org.hlanz.repository.PastelRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/pastelservlet/*")
public class PastelServlet extends HttpServlet {
    private PastelRepository repository = PastelRepository.getInstance();

    // GET - Obtener todos o uno por ID
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/pasteles - Obtener todos
                List<Pastel> pasteles = repository.obtenerTodos();
                String json = JsonUtil.pastelesListToJson(pasteles);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(json);

            } else {
                // GET /api/pasteles/{id} - Obtener uno por ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Pastel pastel = repository.obtenerPorId(id);

                if (pastel != null) {
                    String json = JsonUtil.pastelToJson(pastel);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(json);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Pastel no encontrado\"}");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }

    // POST - Crear nuevo pastel
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Leer el cuerpo de la petición
            String json = leerBody(req);

            // Convertir JSON a objeto
            Pastel nuevoPastel = JsonUtil.jsonToPastel(json);

            // Guardar en el repositorio
            Pastel pastelCreado = repository.crear(nuevoPastel);

            // Responder con el pastel creado
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(JsonUtil.pastelToJson(pastelCreado));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Datos inválidos\"}");
        }
    }

    // PUT - Actualizar pastel existente
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID requerido\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            String json = leerBody(req);
            Pastel pastel = JsonUtil.jsonToPastel(json);

            Pastel actualizado = repository.actualizar(id, pastel);

            if (actualizado != null) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(JsonUtil.pastelToJson(actualizado));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pastel no encontrado\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Datos inválidos\"}");
        }
    }

    // DELETE - Eliminar pastel
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID requerido\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean eliminado = repository.eliminar(id);

            if (eliminado) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pastel no encontrado\"}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }

    // Método auxiliar para leer el body de la petición
    private String leerBody(HttpServletRequest req) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

}
