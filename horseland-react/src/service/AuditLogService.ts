import {AuditLog} from "../model/audit.model.tsx";
import {AUDIT_ENDPOINT} from "../constants/api.ts";
import {fetchWithAuth} from "../utils/fetchWithAuth.ts";

export class AuditLogService {
    static async getAuditLogs(): Promise<AuditLog[]> {

        const response = await fetchWithAuth(AUDIT_ENDPOINT, {
            method: 'GET'
        });
        if (!response.ok) {
            throw new Error('Failed to fetch audit');
        }
        return response.json();
    }
}