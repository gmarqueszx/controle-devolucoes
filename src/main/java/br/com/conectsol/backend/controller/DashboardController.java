package br.com.conectsol.backend.controller;

import br.com.conectsol.backend.dto.DashboardStatsDTO;
import br.com.conectsol.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDTO stats() {
        return dashboardService.gerarStats();
    }
}
