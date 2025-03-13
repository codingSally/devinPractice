import React from 'react';

interface NodeItemProps {
  type: string;
  label: string;
  description: string;
  color: string;
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeItem: React.FC<NodeItemProps> = ({ type, label, description, color, onDragStart }) => {
  return (
    <div
      className="node-item"
      style={{ backgroundColor: color, color: 'white' }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="node-item-title">{label}</div>
      <div className="node-item-description">{description}</div>
    </div>
  );
};

export default NodeItem;
