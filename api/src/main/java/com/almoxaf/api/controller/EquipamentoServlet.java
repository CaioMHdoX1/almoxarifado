package com.almoxaf.api.controller;

import com.almoxaf.api.dto.EquipamentoBuscaResultadoDTO;
import com.almoxaf.api.dto.EquipamentoCreateRequestDTO;
import com.almoxaf.api.dto.EquipamentoResponseDTO;
import com.almoxaf.api.exception.ApiExceptionHandler;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.service.EquipamentoService;
import com.almoxaf.api.util.JsonMapperFactory;
import com.almoxaf.api.util.JsonResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/equipamentos/*")
public class EquipamentoServlet extends HttpServlet {

    private final ObjectMapper jsonMapper = JsonMapperFactory.create();
    private final EquipamentoService equipamentoService = new EquipamentoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo(); // null, ou "/codigo/PAT-00812"

            if (pathInfo != null && pathInfo.startsWith("/codigo/")) {
                String codigo = pathInfo.substring("/codigo/".length());
                EquipamentoResponseDTO resultado = equipamentoService.buscarPorCodigo(codigo);
                JsonResponseWriter.writeData(resp, resultado);
                return;
            }

            String nome = req.getParameter("nome");

            if (nome != null && !nome.isBlank()) {
                List<EquipamentoBuscaResultadoDTO> resultado = equipamentoService.buscarPorNome(nome);
                JsonResponseWriter.writeData(resp, resultado);
            } else {
                List<EquipamentoResponseDTO> todos = equipamentoService.listarTodos();
                JsonResponseWriter.writeData(resp, todos);
            }
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            EquipamentoCreateRequestDTO corpo =
                    jsonMapper.readValue(req.getInputStream(), EquipamentoCreateRequestDTO.class);
            EquipamentoResponseDTO criado = equipamentoService.criar(corpo);
            JsonResponseWriter.writeData(resp, criado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = extrairId(req);
            EquipamentoCreateRequestDTO corpo =
                    jsonMapper.readValue(req.getInputStream(), EquipamentoCreateRequestDTO.class);
            EquipamentoResponseDTO atualizado = equipamentoService.editar(id, corpo);
            JsonResponseWriter.writeData(resp, atualizado);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = extrairId(req);
            equipamentoService.remover(id);
            JsonResponseWriter.writeData(resp, Map.of("status", "removido"));
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    private long extrairId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo(); // ex.: "/42"
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new RegraDeNegocioException("Informe o id do equipamento na URL (ex.: /api/equipamentos/42).");
        }
        String idBruto = pathInfo.substring(1); // remove a barra inicial
        try {
            return Long.parseLong(idBruto);
        } catch (NumberFormatException e) {
            throw new RegraDeNegocioException("Id de equipamento inválido: " + idBruto);
        }
    }
}
