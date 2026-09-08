package com.almoxaf.api.controller;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint de diagnóstico: confirma que a API subiu e consegue falar com o
 * banco. Útil para checar o ambiente (Docker Compose, Tomcat, etc.) antes de
 * testar qualquer regra de negócio.
 *
 * <p>GET /api/health → { "data": { "status": "ok", "database": "up" } }</p>
 */
@WebServlet("/api/health")
public class HealthCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "ok");

        try (Connection ignored = DataSourceProvider.getConnection()) {
            status.put("database", "up");
        } catch (Exception e) {
            status.put("database", "down");
            status.put("databaseError", e.getMessage());
        }

        JsonResponseWriter.writeData(resp, status);
    }
}
