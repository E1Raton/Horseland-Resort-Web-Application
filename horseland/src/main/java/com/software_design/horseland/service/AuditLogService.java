package com.software_design.horseland.service;

import com.software_design.horseland.model.AuditLog;
import com.software_design.horseland.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditRepository;

    public List<AuditLog> getAuditLogs() {
        return auditRepository.findAll();
    }

    public void deleteAuditLog(UUID uuid) {
        auditRepository.deleteById(uuid);
    }
}
