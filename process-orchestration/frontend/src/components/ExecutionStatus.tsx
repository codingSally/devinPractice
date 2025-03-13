import React from 'react';

interface ExecutionStatusProps {
  status: {
    id: number;
    processId: number;
    status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
    startTime: string;
    endTime?: string;
    currentNodeId?: string;
    result?: any;
    error?: string;
  } | null;
  isExecuting: boolean;
}

const ExecutionStatus: React.FC<ExecutionStatusProps> = ({ status, isExecuting }) => {
  if (!status && !isExecuting) {
    return null;
  }
  
  const getStatusColor = () => {
    if (!status) return '#f59e0b'; // Amber for unknown
    
    switch (status.status) {
      case 'PENDING':
        return '#f59e0b'; // Amber
      case 'RUNNING':
        return '#3b82f6'; // Blue
      case 'COMPLETED':
        return '#10b981'; // Green
      case 'FAILED':
        return '#ef4444'; // Red
      default:
        return '#6b7280'; // Gray
    }
  };
  
  const formatTime = (timeString?: string) => {
    if (!timeString) return 'N/A';
    const date = new Date(timeString);
    return date.toLocaleTimeString();
  };
  
  const calculateDuration = () => {
    if (!status) return 'N/A';
    
    const start = new Date(status.startTime).getTime();
    const end = status.endTime ? new Date(status.endTime).getTime() : Date.now();
    const durationMs = end - start;
    
    // Format as seconds with 2 decimal places
    return `${(durationMs / 1000).toFixed(2)}s`;
  };
  
  return (
    <div className="execution-status-container" style={{ 
      padding: '16px', 
      border: '1px solid #ddd', 
      borderRadius: '8px', 
      marginBottom: '16px',
      backgroundColor: '#f9fafb'
    }}>
      <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '8px' }}>Execution Status</h3>
      
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
        <div 
          style={{ 
            width: '12px', 
            height: '12px', 
            borderRadius: '50%', 
            marginRight: '8px',
            backgroundColor: getStatusColor()
          }}
        ></div>
        <span style={{ fontWeight: 500 }}>
          {status ? status.status : 'INITIALIZING'}
        </span>
      </div>
      
      <div style={{ fontSize: '14px' }}>
        {status && (
          <>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
              <div style={{ color: '#4b5563' }}>Execution ID:</div>
              <div>{status.id}</div>
              
              <div style={{ color: '#4b5563' }}>Start Time:</div>
              <div>{formatTime(status.startTime)}</div>
              
              <div style={{ color: '#4b5563' }}>Duration:</div>
              <div>{calculateDuration()}</div>
              
              {status.currentNodeId && (
                <>
                  <div style={{ color: '#4b5563' }}>Current Node:</div>
                  <div>{status.currentNodeId}</div>
                </>
              )}
              
              {status.endTime && (
                <>
                  <div style={{ color: '#4b5563' }}>End Time:</div>
                  <div>{formatTime(status.endTime)}</div>
                </>
              )}
            </div>
            
            {status.error && (
              <div style={{ 
                marginTop: '8px', 
                color: '#dc2626'
              }}>
                Error: {status.error}
              </div>
            )}
            
            {status.result && (
              <div style={{ 
                marginTop: '8px', 
                padding: '8px', 
                backgroundColor: '#f3f4f6', 
                borderRadius: '4px'
              }}>
                <div style={{ color: '#4b5563', marginBottom: '4px' }}>Result:</div>
                <pre style={{ 
                  fontSize: '12px', 
                  overflow: 'auto',
                  maxHeight: '100px'
                }}>
                  {JSON.stringify(status.result, null, 2)}
                </pre>
              </div>
            )}
          </>
        )}
        
        {!status && isExecuting && (
          <div style={{ color: '#2563eb' }}>
            Initializing execution...
          </div>
        )}
      </div>
    </div>
  );
};

export default ExecutionStatus;
