package com.almoxaf.api.controller;

import com.almoxaf.api.dto.RelatorioDetalheDTO;
import com.almoxaf.api.dto.RelatorioResumoDTO;
import com.almoxaf.api.exception.ApiExceptionHandler;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.service.RelatorioService;
import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Rotas de relatórios:
 *
 * <ul>
 *   <li>GET  /api/relatorios      → lista os relatórios já gerados (mais recente primeiro)</li>
 *   <li>GET  /api/relatorios/{id} → detalhe completo (entregas, devoluções, posse atual)</li>
 *   <li>POST /api/relatorios/gerar → gera um novo agora (opcionalmente com
 *       ?periodoInicio=...&periodoFim=... em ISO-8601; sem parâmetros, usa
 *       os últimos 15 dias)</li>
 * </ul>
 *
 * <p>Além dessas rotas sob demanda, um relatório quinzenal também é gerado
 * automaticamente em segundo plano — ver {@code AppContextListener}.</p>
 */
@WebServlet("/api/relatorios/*")
public class RelatorioServlet extends HttpServlet {

    private final RelatorioService relatorioService = new RelatorioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<RelatorioResumoDTO> relatorios = relatorioService.listar();
                JsonResponseWriter.writeData(resp, relatorios);
                return;
            }

            long id = extrairId(pathInfo);
            RelatorioDetalheDTO detalhe = relatorioService.buscarDetalhe(id);
            JsonResponseWriter.writeData(resp, detalhe);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.equals("/gerar")) {
                JsonResponseWriter.writeError(resp, "Rota não encontrada.", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String periodoInicioParam = req.getParameter("periodoInicio");
            String periodoFimParam = req.getParameter("periodoFim");

            RelatorioResumoDTO gerado;
            if (periodoInicioParam == null && periodoFimParam == null) {
                gerado = relatorioService.gerarUltimosQuinzeDias();
            } else {
                OffsetDateTime inicio = parseData(periodoInicioParam, "periodoInicio");
                OffsetDateTime fim = parseData(periodoFimParam, "periodoFim");
                gerado = relatorioService.gerar(inicio, fim);
            }

            JsonResponseWriter.writeData(resp, gerado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    private long extrairId(String pathInfo) {
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new RegraDeNegocioException("Id de relatório inválido.");
        }
    }

    private OffsetDateTime parseData(String valor, String nomeCampo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("Informe " + nomeCampo + " (ISO-8601) ou omita ambos os parâmetros.");
        }
        try {
            return OffsetDateTime.parse(valor);
        } catch (DateTimeParseException e) {
            throw new RegraDeNegocioException(nomeCampo + " inválido — use o formato ISO-8601.");
        }
    }
}
