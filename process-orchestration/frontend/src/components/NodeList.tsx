import React from 'react';
import NodeItem from './NodeItem';
import { NODE_TYPE_DEFINITIONS } from '../utils/processUtils';

interface NodeListProps {
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeList: React.FC<NodeListProps> = ({ onDragStart }) => {
  return (
    <div className="bg-white p-4 rounded-lg shadow-md">
      <h2 className="text-lg font-semibold mb-4">Available Nodes</h2>
      <div className="space-y-2">
        {Object.values(NODE_TYPE_DEFINITIONS).map((nodeType) => (
          <NodeItem
            key={nodeType.type}
            type={nodeType.type}
            label={nodeType.label}
            description={nodeType.description}
            color={nodeType.color}
            onDragStart={onDragStart}
          />
        ))}
      </div>
    </div>
  );
};

export default NodeList;
