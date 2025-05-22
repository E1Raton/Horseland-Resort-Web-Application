// components/UserTable.tsx
import DataTable, { TableColumn } from 'react-data-table-component';
import { User } from '../model/user.model.tsx';

interface UserTableProps {
    data: User[];
    loading: boolean;
    isError: boolean;
    onRowSelected: (state: { selectedRows: User[] }) => void;
    theme: 'light' | 'dark';
}

function UserTable({ data, loading, isError, onRowSelected, theme }: UserTableProps) {
    const columns: TableColumn<User>[] = [
        { name: 'ID', selector: (row: User) => row.id, sortable: true },
        { name: 'First Name', selector: (row: User) => row.firstName, sortable: true },
        { name: 'Last Name', selector: (row: User) => row.lastName, sortable: true },
        {
            name: 'Birth Date',
            selector: (row: User) => row.birthDate instanceof Date ? row.birthDate.toLocaleDateString() : row.birthDate,
            sortable: true
        },
        { name: 'Username', selector: (row: User) => row.username, sortable: true},
        { name: 'Email', selector: (row: User) => row.email, sortable: true },
        { name: 'Role', selector: (row: User) => row.role, sortable: true }
    ];

    return (
        <>
            {loading ? (
                <p className="loading-text">Loading...</p>
            ) : isError ? (
                <p className="error-text">An error occurred while fetching data</p>
            ) : (
                <div className="table-container">
                    <DataTable
                        title="Users"
                        columns={columns}
                        data={data}
                        pagination
                        highlightOnHover
                        selectableRows
                        onSelectedRowsChange={onRowSelected}
                        theme={theme === "dark" ? "dark" : "default"}
                    />
                </div>
            )}
        </>
    );
}

export default UserTable;