import React from 'react';
import { NODE_TYPE_DEFINITIONS } from '../utils/processUtils';
import NodeItem from './NodeItem';

interface NodeListProps {
  onDragStart: (event: React.DragEvent, nodeType: string) => void;
}

const NodeList: React.FC<NodeListProps> = ({ onDragStart }) => {
  return (
    <div className="node-list">
      <h2 className="text-lg font-semibold mb-2">Available Nodes</h2>
      <div className="node-items">
        {Object.entries(NODE_TYPE_DEFINITIONS).map(([type, nodeType]) => (
          <NodeItem
            key={type}
            type={type}
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
