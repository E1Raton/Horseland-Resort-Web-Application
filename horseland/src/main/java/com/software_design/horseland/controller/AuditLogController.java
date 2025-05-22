package com.software_design.horseland.controller;

import com.software_design.horseland.model.AuditLog;
import com.software_design.horseland.service.AuditLogService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAuditLogs() {
        return auditLogService.getAuditLogs();
    }

    @DeleteMapping("/audit/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAuditLog(@PathVariable UUID uuid) {
        auditLogService.deleteAuditLog(uuid);
    }
}
