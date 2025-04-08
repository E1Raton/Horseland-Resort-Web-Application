// components/ActivityTable.tsx
import DataTable, { TableColumn } from 'react-data-table-component';
import { Activity } from '../model/activity.model.tsx';

interface ActivityTableProps {
    data: Activity[];
    loading: boolean;
    isError: boolean;
    onRowSelected: (state: { selectedRows: Activity[] }) => void;
    theme: 'light' | 'dark';
}

function ActivityTable({ data, loading, isError, onRowSelected, theme }: ActivityTableProps) {
    const columns: TableColumn<Activity>[] = [
        { name: 'ID', selector: (row: Activity) => row.id, sortable: true },
        { name: 'Name', selector: (row: Activity) => row.name, sortable: true },
        { name: 'Description', selector: (row: Activity) => row.description, sortable: true },
        {
            name: 'Start Date',
            selector: (row: Activity) => row.startDate,
            sortable: true
        },
        {
            name: 'End Date',
            selector: (row: Activity) => row.endDate,
            sortable: true
        }
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
                        title="Activities"
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

export default ActivityTable;