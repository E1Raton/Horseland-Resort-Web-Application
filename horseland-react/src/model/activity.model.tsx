interface Activity {
    id: string;
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    participants: { id: string }[];
}

export type { Activity };