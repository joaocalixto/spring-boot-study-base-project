package com.example.demo.controller;

import com.example.demo.client.GitHubClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Mostra o HTTP Client declarativo em ação.
 * Cada request abaixo roda em virtual thread (Loom) graças a spring.threads.virtual.enabled=true.
 */
@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubClient github;

    public GitHubController(GitHubClient github) {
        this.github = github;
    }

    @GetMapping("/{username}")
    public Map<String, Object> getUser(@PathVariable String username) {
        return github.getUser(username);
    }

    @GetMapping("/{username}/repos")
    public List<Map<String, Object>> listRepos(@PathVariable String username) {
        return github.listRepos(username);
    }
}
