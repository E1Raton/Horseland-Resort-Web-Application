interface Activity {
    id: string;
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    participants: { id: string }[]; // assuming participants is an array of user objects with an id field
}

export type { Activity };