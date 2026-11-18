package com.example.demo.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.Map;

/**
 * HTTP Service Client declarativo.
 *
 * Não há implementação — o Spring gera em runtime.
 * Substitui RestTemplate, WebClient manual e Feign.
 */
public interface GitHubClient {

    @GetExchange("/users/{username}")
    Map<String, Object> getUser(@PathVariable String username);

    @GetExchange("/users/{username}/repos")
    List<Map<String, Object>> listRepos(@PathVariable String username);
}
