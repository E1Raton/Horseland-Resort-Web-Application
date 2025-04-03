enum Breed {
    AXIOS = "AXIOS",
    HORRO = "HORRO",
    KONIK = "KONIK",
    PINTABIAN = "PINTABIAN",
    MUSTANG = "MUSTANG",
    SENNER = "SENNER"
}

interface Horse {
    id: string;
    name: string;
    birthDate: string | Date;
    breed: Breed;
    ownerId: string;
}

export type { Horse };
export { Breed };