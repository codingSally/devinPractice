import React, { useState, useCallback, useRef } from 'react';
import ReactFlow, {
  ReactFlowProvider,
  addEdge,
  useNodesState,
  useEdgesState,
  Controls,
  Background,
  Connection,
  Edge,
  Node,
} from 'react-flow-renderer';
import { createNode, convertToProcessDefinition } from '../utils/processUtils';

interface ProcessCanvasProps {
  onSave: (processDefinition: any) => void;
}

const ProcessCanvas: React.FC<ProcessCanvasProps> = ({ onSave }) => {
  const reactFlowWrapper = useRef<HTMLDivElement>(null);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [reactFlowInstance, setReactFlowInstance] = useState<any>(null);
  const [processName, setProcessName] = useState('New Process');
  const [processDescription, setProcessDescription] = useState('Process description');

  const onConnect = useCallback(
    (params: Connection) => setEdges((eds) => addEdge(params, eds)),
    [setEdges]
  );

  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      const reactFlowBounds = reactFlowWrapper.current?.getBoundingClientRect();
      const type = event.dataTransfer.getData('application/reactflow');

      // Check if the dropped element is valid
      if (typeof type === 'undefined' || !type) {
        return;
      }

      if (reactFlowBounds && reactFlowInstance) {
        const position = reactFlowInstance.project({
          x: event.clientX - reactFlowBounds.left,
          y: event.clientY - reactFlowBounds.top,
        });
        const newNode = createNode(type, position);

        setNodes((nds) => nds.concat(newNode));
      }
    },
    [reactFlowInstance, setNodes]
  );

  const handleSave = () => {
    const processDefinition = convertToProcessDefinition(
      nodes,
      edges,
      processName,
      processDescription
    );
    onSave(processDefinition);
  };

  return (
    <div className="h-full flex flex-col">
      <div className="bg-white p-4 border-b flex justify-between items-center">
        <div className="flex-1">
          <input
            type="text"
            value={processName}
            onChange={(e) => setProcessName(e.target.value)}
            className="text-xl font-semibold border-none focus:outline-none focus:ring-2 focus:ring-blue-500 rounded px-2 py-1 w-full"
            placeholder="Process Name"
          />
          <input
            type="text"
            value={processDescription}
            onChange={(e) => setProcessDescription(e.target.value)}
            className="text-sm text-gray-600 border-none focus:outline-none focus:ring-2 focus:ring-blue-500 rounded px-2 py-1 w-full mt-1"
            placeholder="Process Description"
          />
        </div>
        <button
          onClick={handleSave}
          className="ml-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
        >
          Save Process
        </button>
      </div>
      <div className="flex-1" ref={reactFlowWrapper}>
        <ReactFlowProvider>
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onInit={setReactFlowInstance}
            onDrop={onDrop}
            onDragOver={onDragOver}
            fitView
          >
            <Controls />
            <Background color="#aaa" gap={16} />
          </ReactFlow>
        </ReactFlowProvider>
      </div>
    </div>
  );
};

export default ProcessCanvas;
