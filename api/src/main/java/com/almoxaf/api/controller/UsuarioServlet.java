package com.almoxaf.api.controller;

import com.almoxaf.api.dto.UsuarioComEquipamentosDTO;
import com.almoxaf.api.dto.UsuarioCreateRequestDTO;
import com.almoxaf.api.dto.UsuarioResponseDTO;
import com.almoxaf.api.exception.ApiExceptionHandler;
import com.almoxaf.api.service.UsuarioService;
import com.almoxaf.api.util.JsonMapperFactory;
import com.almoxaf.api.util.JsonResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet("/api/usuarios")
public class UsuarioServlet extends HttpServlet {

    private final ObjectMapper jsonMapper = JsonMapperFactory.create();
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String nome = req.getParameter("nome");
            List<UsuarioComEquipamentosDTO> resultado = usuarioService.buscarPorNome(nome);
            JsonResponseWriter.writeData(resp, resultado);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UsuarioCreateRequestDTO corpo = jsonMapper.readValue(req.getInputStream(), UsuarioCreateRequestDTO.class);
            UsuarioResponseDTO usuarioCriado = usuarioService.criar(corpo);
            JsonResponseWriter.writeData(resp, usuarioCriado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }
}
