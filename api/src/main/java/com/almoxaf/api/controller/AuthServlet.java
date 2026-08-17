package com.almoxaf.api.controller;

import com.almoxaf.api.config.SessionKeys;
import com.almoxaf.api.dto.LoginRequestDTO;
import com.almoxaf.api.dto.UsuarioResponseDTO;
import com.almoxaf.api.exception.ApiExceptionHandler;
import com.almoxaf.api.exception.CredenciaisInvalidasException;
import com.almoxaf.api.exception.MuitasTentativasException;
import com.almoxaf.api.mapper.UsuarioMapper;
import com.almoxaf.api.repository.UsuarioRepository;
import com.almoxaf.api.service.AuthService;
import com.almoxaf.api.util.JsonMapperFactory;
import com.almoxaf.api.util.JsonResponseWriter;
import com.almoxaf.api.util.RateLimiter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getName());

    private static final RateLimiter LOGIN_RATE_LIMITER = new RateLimiter(10, Duration.ofMinutes(5));

    private final ObjectMapper jsonMapper = JsonMapperFactory.create();
    private final AuthService authService = new AuthService();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String caminho = req.getPathInfo(); // ex.: "/login", "/logout"

        try {
            if ("/login".equals(caminho)) {
                login(req, resp);
            } else if ("/logout".equals(caminho)) {
                logout(req, resp);
            } else {
                JsonResponseWriter.writeError(resp, "Rota não encontrada.", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String caminho = req.getPathInfo();

        try {
            if ("/me".equals(caminho)) {
                me(req, resp);
            } else {
                JsonResponseWriter.writeError(resp, "Rota não encontrada.", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            ApiExceptionHandler.responder(resp, e);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ip = obterIpCliente(req);

        if (!LOGIN_RATE_LIMITER.permitir(ip)) {
            LOG.warning("Rate limit de login excedido para IP " + ip);
            throw new MuitasTentativasException(
                    "Muitas tentativas de login. Aguarde alguns minutos antes de tentar novamente.");
        }

        LoginRequestDTO corpo = jsonMapper.readValue(req.getInputStream(), LoginRequestDTO.class);

        UsuarioResponseDTO usuario;
        try {
            usuario = authService.autenticar(corpo.getEmail(), corpo.getSenha());
        } catch (CredenciaisInvalidasException e) {
            LOG.info(() -> "Tentativa de login falhou — email=" + corpo.getEmail() + " ip=" + ip);
            throw e;
        }

        HttpSession sessaoExistente = req.getSession(false);
        if (sessaoExistente != null) {
            sessaoExistente.invalidate();
        }

        HttpSession sessao = req.getSession(true);
        sessao.setAttribute(SessionKeys.USUARIO_ID, usuario.getId());

        JsonResponseWriter.writeData(resp, Map.of("usuario", usuario));
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false); // false = não cria uma nova à toa
        if (sessao != null) {
            sessao.invalidate();
        }
        JsonResponseWriter.writeData(resp, Map.of("status", "ok"));
    }

    private void me(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false);
        Long usuarioId = sessao != null ? (Long) sessao.getAttribute(SessionKeys.USUARIO_ID) : null;

        if (usuarioId == null) {
            throw new CredenciaisInvalidasException("Nenhuma sessão ativa.");
        }

        UsuarioResponseDTO usuario = usuarioRepository.buscarPorId(usuarioId)
                .map(UsuarioMapper::paraResponseDTO)
                .orElseThrow(() -> new CredenciaisInvalidasException("Sessão inválida."));

        JsonResponseWriter.writeData(resp, Map.of("usuario", usuario));
    }

    private String obterIpCliente(HttpServletRequest req) {
        String xForwardedFor = req.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
