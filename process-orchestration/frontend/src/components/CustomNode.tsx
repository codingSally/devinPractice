import React, { useState } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';

interface CustomNodeProps extends NodeProps {
  data: {
    label: string;
    properties: Record<string, string>;
  };
}

const CustomNode: React.FC<CustomNodeProps> = ({ data, id, type }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  
  const getNodeColor = () => {
    switch (type) {
      case 'logging':
        return '#4CAF50';
      case 'http':
        return '#2196F3';
      case 'script':
        return '#FF9800';
      case 'conditional':
        return '#9C27B0';
      default:
        return '#555';
    }
  };
  
  return (
    <div 
      className="px-4 py-2 rounded-md shadow-md"
      style={{ 
        backgroundColor: getNodeColor(),
        color: 'white',
        minWidth: '180px',
        border: '1px solid rgba(0,0,0,0.15)'
      }}
    >
      <Handle type="target" position={Position.Top} />
      
      <div className="flex justify-between items-center">
        <div className="font-medium">{data.label}</div>
        <button 
          className="text-white opacity-80 hover:opacity-100"
          onClick={() => setIsExpanded(!isExpanded)}
        >
          {isExpanded ? '▲' : '▼'}
        </button>
      </div>
      
      {isExpanded && (
        <div className="mt-2 text-sm">
          <div className="font-medium mb-1">Properties:</div>
          {Object.entries(data.properties).map(([key, value]) => (
            <div key={key} className="flex">
              <span className="font-medium mr-1">{key}:</span>
              <span className="opacity-90">{value}</span>
            </div>
          ))}
        </div>
      )}
      
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
};

export default CustomNode;
