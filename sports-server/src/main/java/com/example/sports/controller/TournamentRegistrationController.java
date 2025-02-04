package com.example.sports.controller;

import com.example.sports.common.Result;
import com.example.sports.entity.TournamentRegistration;
import com.example.sports.service.TournamentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments/{tournamentId}/registrations")
public class TournamentRegistrationController {

    @Autowired
    private TournamentRegistrationService registrationService;

    // 获取报名列表
    @GetMapping
    @PreAuthorize("hasAuthority('tournament:view')")
    public Result<List<TournamentRegistration>> getRegistrations(@PathVariable Long tournamentId) {
        List<TournamentRegistration> registrations = registrationService.getRegistrations(tournamentId);
        return Result.success(registrations);
    }

    // 报名
    @PostMapping("/register")
    public Result<Void> register(@PathVariable Long tournamentId) {
        registrationService.register(tournamentId);
        return Result.success();
    }

    // 取消报名
    @DeleteMapping("/register")
    public Result<Void> cancelRegistration(@PathVariable Long tournamentId) {
        registrationService.cancelRegistration(tournamentId);
        return Result.success();
    }

    // 审核报名
    @PutMapping("/{userId}/audit")
    @PreAuthorize("hasAuthority('tournament:audit')")
    public Result<Void> auditRegistration(
            @PathVariable Long tournamentId,
            @PathVariable Long userId,
            @RequestBody AuditRequest request
    ) {
        registrationService.auditRegistration(tournamentId, userId, request.getStatus());
        return Result.success();
    }
}

class AuditRequest {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 