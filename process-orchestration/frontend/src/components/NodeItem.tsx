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
      className="p-3 rounded-md cursor-move transition-all hover:shadow-md"
      style={{ backgroundColor: color, color: '#fff' }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="font-medium">{label}</div>
      <div className="text-sm opacity-80">{description}</div>
    </div>
  );
};

export default NodeItem;
