import React from 'react';

interface NodeTypeItemProps {
  type: string;
  label: string;
  description: string;
  color: string;
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeTypeItem: React.FC<NodeTypeItemProps> = ({ 
  type, 
  label, 
  description, 
  color, 
  onDragStart 
}) => {
  return (
    <div
      className={`node-item node-type-${type}`}
      style={{ 
        backgroundColor: color,
        padding: '12px',
        marginBottom: '12px',
        borderRadius: '8px',
        cursor: 'grab',
        transition: 'all 0.3s ease',
        boxShadow: '0 2px 5px rgba(0, 0, 0, 0.1)',
        border: '1px solid rgba(255, 255, 255, 0.2)'
      }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="node-item-title" style={{ 
        fontWeight: 'bold', 
        marginBottom: '6px',
        fontSize: '14px',
        textShadow: '0 1px 2px rgba(0,0,0,0.2)'
      }}>{label}</div>
      <div className="node-item-description" style={{ 
        fontSize: '12px', 
        opacity: '0.9',
        lineHeight: '1.4'
      }}>{description}</div>
    </div>
  );
};

export default NodeTypeItem;
