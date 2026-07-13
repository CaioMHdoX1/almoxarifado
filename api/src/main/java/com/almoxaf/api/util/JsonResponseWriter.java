package com.almoxaf.api.util;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
 
/**
 * Ponto único de escrita de respostas HTTP em JSON. Garante que toda
 * resposta da API siga o mesmo formato:
 *
 * <pre>
 * sucesso:  { "data": ... }
 * erro:     { "error": { "message": "...", "status": 404 } }
 * </pre>
 *
 * <p>Nenhum {@code controller} deve montar JSON na mão — sempre passa pelos
 * métodos daqui, assim o contrato da API fica garantido em um único lugar.</p>
 */
public final class JsonResponseWriter {
 
    private static final ObjectMapper MAPPER = JsonMapperFactory.create();
 
    private JsonResponseWriter() {
    }
 
    /** Escreve { "data": payload } com status 200. */
    public static void writeData(HttpServletResponse resp, Object payload) throws IOException {
        writeData(resp, payload, HttpServletResponse.SC_OK);
    }
 
    /** Escreve { "data": payload } com o status informado (ex.: 201 Created). */
    public static void writeData(HttpServletResponse resp, Object payload, int httpStatus)
            throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", payload);
        write(resp, body, httpStatus);
    }
 
    /** Escreve { "error": { message, status } } com o status informado. */
    public static void writeError(HttpServletResponse resp, String message, int httpStatus)
            throws IOException {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("message", message);
        error.put("status", httpStatus);
 
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
 
        write(resp, body, httpStatus);
    }
 
    private static void write(HttpServletResponse resp, Object body, int httpStatus)
            throws IOException {
        resp.setStatus(httpStatus);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), body);
    }
}