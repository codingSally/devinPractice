import React from 'react';

interface NodeTypeItemProps {
  type: string;
  label: string;
  description: string;
  color: string;
  onDragStart: (event: React.DragEvent<HTMLDivElement>, nodeType: string) => void;
}

const NodeTypeItem: React.FC<NodeTypeItemProps> = ({
  type,
  label,
  description,
  color,
  onDragStart,
}) => {
  return (
    <div
      className="p-3 rounded-md cursor-move transition-all hover:shadow-md mb-2"
      style={{ backgroundColor: color, color: 'white' }}
      draggable
      onDragStart={(e) => onDragStart(e, type)}
    >
      <div className="font-medium">{label}</div>
      <div className="text-sm opacity-80">{description}</div>
    </div>
  );
};

export default NodeTypeItem;
