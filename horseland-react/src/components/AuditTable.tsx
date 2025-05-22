// components/AuditTable.tsx
import React from 'react';
import DataTable, { TableColumn } from 'react-data-table-component';
import { AuditLog } from '../model/audit.model.tsx';

interface AuditTableProps {
    data: AuditLog[];
    loading: boolean;
    isError: boolean;
    onRowSelected: (state: { selectedRows: AuditLog[] }) => void;
    theme: 'light' | 'dark';
}

const AuditTable: React.FC<AuditTableProps> = ({ data, loading, isError, onRowSelected, theme }) => {
    const columns: TableColumn<AuditLog>[] = [
        {
            name: 'Username',
            selector: (row) => row.username,
            sortable: true,
        },
        {
            name: 'Operation',
            selector: (row) => row.operation,
            sortable: true,
        },
        {
            name: 'Timestamp',
            selector: (row) =>
                row.timestamp instanceof Date
                    ? row.timestamp.toLocaleString()
                    : new Date(row.timestamp).toLocaleString(),
            sortable: true,
        },
        {
            name: 'Entity',
            selector: (row) => row.entity,
            sortable: true,
        },
    ];

    return (
        <>
            {loading ? (
                <p className="loading-text">Loading audit log...</p>
            ) : isError ? (
                <p className="error-text">An error occurred while fetching audit entries</p>
            ) : (
                <div className="table-container">
                    <DataTable
                        title="Audit Log"
                        columns={columns}
                        data={data}
                        pagination
                        highlightOnHover
                        selectableRows
                        onSelectedRowsChange={onRowSelected}
                        theme={theme === 'dark' ? 'dark' : 'default'}
                    />
                </div>
            )}
        </>
    );
};

export default AuditTable;
