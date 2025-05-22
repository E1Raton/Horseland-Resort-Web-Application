// components/AuditView.tsx
import React, { useState, useEffect } from 'react';
import { DARK_THEME, LIGHT_THEME } from '../constants/theme.ts';
import { AuditLogService } from '../service/AuditLogService.ts';
import AuditTable from './AuditTable.tsx';
import { AuditLog } from '../model/audit.model.tsx';

const AuditView: React.FC = () => {
    const [data, setData] = useState<AuditLog[]>([]);
    const [loading, setLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [, setSelectedAuditLog] = useState<AuditLog | null>(null);
    const [currentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        setIsError(false);
        try {
            const logs = await AuditLogService.getAuditLogs();
            setData(logs);
        } catch (error) {
            console.error('Error fetching audit logs:', error);
            setIsError(true);
        } finally {
            setLoading(false);
        }
    };

    const handleRowSelected = (state: { selectedRows: AuditLog[] }) => {
        setSelectedAuditLog(state.selectedRows[0] || null);
    };

    return (
        <div className="app-container">
            <AuditTable
                data={data}
                loading={loading}
                isError={isError}
                onRowSelected={handleRowSelected}
                theme={currentTheme === DARK_THEME ? 'dark' : 'light'}
            />
        </div>
    );
};

export default AuditView;