package com.almoxaf.api.config;

import com.almoxaf.api.service.RelatorioService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gancho de ciclo de vida da aplicação (equivalente ao "startup"/"shutdown"
 * que um framework como Spring faria por baixo dos panos — aqui é feito à
 * mão, como o resto da API).
 *
 * <p>O Tomcat detecta essa classe automaticamente por causa da anotação
 * {@link WebListener}, sem precisar declarar nada no {@code web.xml}.</p>
 *
 * <p>Também é aqui que mora o agendador do relatório quinzenal automático:
 * em vez de um timer de "daqui a 15 dias" (que reseta toda vez que a
 * aplicação reinicia — nada confiável em desenvolvimento, onde o container
 * sobe e desce o tempo todo), a estratégia é checar UMA VEZ POR DIA se já
 * se passaram 15 dias desde o último relatório salvo no banco. Isso é
 * resistente a reinícios: o estado de "quando foi o último" vive no banco,
 * não na memória do processo.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    private ScheduledExecutorService agendador;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSourceProvider.init();

        agendador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "relatorio-quinzenal-agendador");
            t.setDaemon(true); // não impede o Tomcat de desligar
            return t;
        });

        // Primeira checagem 1 minuto depois do startup (dá tempo do banco
        // estar plenamente pronto), depois repete a cada 24h.
        agendador.scheduleAtFixedRate(this::checarEGerarSeNecessario, 1, 24 * 60, TimeUnit.MINUTES);
    }

    private void checarEGerarSeNecessario() {
        try {
            RelatorioService relatorioService = new RelatorioService();
            if (relatorioService.devePrecisarGerarAutomatico()) {
                relatorioService.gerarUltimosQuinzeDias();
                LOG.info("Relatório quinzenal automático gerado com sucesso.");
            }
        } catch (Exception e) {
            // Nunca deixa uma falha aqui derrubar a aplicação — só loga e
            // tenta de novo na próxima checagem diária.
            LOG.log(Level.WARNING, "Falha ao gerar relatório quinzenal automático.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (agendador != null) {
            agendador.shutdownNow();
        }
        DataSourceProvider.close();
    }
}
