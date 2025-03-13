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
      style={{ backgroundColor: color }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="node-item-title">{label}</div>
      <div className="node-item-description">{description}</div>
    </div>
  );
};

export default NodeTypeItem;
