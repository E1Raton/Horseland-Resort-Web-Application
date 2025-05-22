export interface AuditLog {
    id?: string;
    username: string;
    operation: string;
    timestamp: string | Date;
    entity: string;
}
