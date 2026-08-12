package com.almoxaf.api.util;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RateLimiter {

    private final int maxTentativas;
    private final long janelaMs;
    private final ConcurrentMap<String, Deque<Long>> tentativasPorChave = new ConcurrentHashMap<>();

    public RateLimiter(int maxTentativas, Duration janela) {
        this.maxTentativas = maxTentativas;
        this.janelaMs = janela.toMillis();
    }

    public boolean permitir(String chave) {
        long agora = System.currentTimeMillis();
        Deque<Long> tentativas = tentativasPorChave.computeIfAbsent(chave, k -> new ArrayDeque<>());

        synchronized (tentativas) {
            while (!tentativas.isEmpty() && agora - tentativas.peekFirst() > janelaMs) {
                tentativas.pollFirst();
            }
            if (tentativas.size() >= maxTentativas) {
                return false;
            }
            tentativas.addLast(agora);
            return true;
        }
    }
}
