import {AuditLog} from "../model/audit.model.tsx";
import {AUDIT_ENDPOINT} from "../constants/api.ts";

export class AuditLogService {
    static async getAuditLogs(): Promise<AuditLog[]> {
        const token = sessionStorage.getItem('token');

        const response = await fetch(AUDIT_ENDPOINT, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });
        if (!response.ok) {
            throw new Error('Failed to fetch audit');
        }
        return response.json();
    }

    static async deleteAuditLog(id: string): Promise<void> {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`${AUDIT_ENDPOINT}/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });
        if (!response.ok) {
            throw new Error('Failed to delete user');
        }
    }
}