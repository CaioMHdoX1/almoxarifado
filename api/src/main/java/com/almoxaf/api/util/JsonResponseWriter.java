package com.almoxaf.api.util;
 
//import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.json.JsonMapper;
 

public final class JsonResponseWriter {
 
    private static final JsonMapper mapper =JsonMapperFactory.create();
 
    private JsonResponseWriter() {
    }
 
    public static void writeData(HttpServletResponse resp, Object payload) throws IOException {
        writeData(resp, payload, HttpServletResponse.SC_OK);
    }
 
    public static void writeData(HttpServletResponse resp, Object payload, int httpStatus)
            throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", payload);
        write(resp, body, httpStatus);
    }
 

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
        //MAPPER.writeValue(resp.getWriter(), body);
    }
}