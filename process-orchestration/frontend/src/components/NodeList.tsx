import React from 'react';
import NodeItem from './NodeItem';
import { NODE_TYPE_DEFINITIONS } from '../utils/processUtils';

interface NodeListProps {
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeList: React.FC<NodeListProps> = ({ onDragStart }) => {
  return (
    <div className="node-list">
      <h2 className="text-lg font-semibold mb-2">Available Nodes</h2>
      {Object.entries(NODE_TYPE_DEFINITIONS).map(([type, def]) => (
        <NodeItem
          key={type}
          type={type}
          label={def.label}
          description={def.description}
          color={def.color}
          onDragStart={onDragStart}
        />
      ))}
    </div>
  );
};

export default NodeList;
