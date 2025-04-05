// model/Notification.ts

interface Notification {
    id: string; // UUID
    userId: string;
    activityId: string;
    title: string;
    message: string;
    dateTime: string; // ISO 8601 format from backend, e.g., "2025-04-05T15:30:00"
}

export type { Notification };
