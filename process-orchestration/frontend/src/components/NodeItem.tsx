import React from 'react';

interface NodeItemProps {
  type: string;
  label: string;
  description: string;
  color: string;
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeItem: React.FC<NodeItemProps> = ({
  type,
  label,
  description,
  color,
  onDragStart,
}) => {
  return (
    <div
      className="p-3 rounded-md cursor-move transition-all hover:shadow-md mb-2"
      style={{ 
        backgroundColor: color, 
        color: 'white',
        border: '1px solid rgba(0,0,0,0.1)',
        boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
      }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="font-medium">{label}</div>
      <div className="text-sm opacity-80">{description}</div>
    </div>
  );
};

export default NodeItem;
