enum Role {
    ADMIN = 'ADMIN',
    STUDENT = 'STUDENT',
    INSTRUCTOR = 'INSTRUCTOR'
}

interface User {
    id: string;
    firstName: string;
    lastName: string;
    birthDate: string | Date;
    username: string;
    email: string;
    password: string;
    role: Role;
}

export type { User };
export { Role };