// components/HorseTable.tsx
import DataTable, { TableColumn } from 'react-data-table-component';
import { Horse } from '../model/horse.model.tsx';

interface HorseTableProps {
    data: Horse[];
    loading: boolean;
    isError: boolean;
    onRowSelected: (state: { selectedRows: Horse[] }) => void;
    theme: 'light' | 'dark';
}

function HorseTable({ data, loading, isError, onRowSelected, theme }: HorseTableProps) {
    const columns: TableColumn<Horse>[] = [
        { name: 'ID', selector: (row: Horse) => row.id, sortable: true },
        { name: 'Name', selector: (row: Horse) => row.name, sortable: true },
        {
            name: 'Birth Date',
            selector: (row: Horse) => row.birthDate instanceof Date ? row.birthDate.toLocaleDateString() : row.birthDate,
            sortable: true
        },
        { name: 'Breed', selector: (row: Horse) => row.breed, sortable: true }
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
                        title="Horses"
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

export default HorseTable;