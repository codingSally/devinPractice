import React, { useState, useCallback, useRef } from 'react';
import { toast } from 'react-toastify';
import ReactFlow, {
  ReactFlowProvider,
  addEdge,
  useNodesState,
  useEdgesState,
  Controls,
  Background,
  Connection,
  MarkerType,
  Node,
} from 'reactflow';
import 'reactflow/dist/style.css';
import { createProcessDefinition, executeProcess } from '../services/api';
import { createNode, convertToProcessDefinition, NODE_TYPE_DEFINITIONS } from '../utils/processUtils';
import CustomNode from '../components/CustomNode';
import NodeTypeItem from '../components/NodeTypeItem';

// Define node types for ReactFlow
const nodeTypes = {
  logging: CustomNode,
  http: CustomNode,
  script: CustomNode,
  conditional: CustomNode,
};

const ProcessDesigner: React.FC = () => {
  const reactFlowWrapper = useRef<HTMLDivElement>(null);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [reactFlowInstance, setReactFlowInstance] = useState<any>(null);
  const [processName, setProcessName] = useState('New Process');
  const [processDescription, setProcessDescription] = useState('Process description');
  const [currentProcessId, setCurrentProcessId] = useState<number | null>(null);
  const [executing, setExecuting] = useState(false);

  // Handle connections between nodes
  const onConnect = useCallback(
    (params: Connection) => {
      // Create edge with animated style and arrow marker
      const edge = {
        ...params,
        type: 'smoothstep',
        animated: true,
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      };
      setEdges((eds) => addEdge(edge, eds));
    },
    [setEdges]
  );

  // Handle drag over for dropping nodes
  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  // Handle drop for creating new nodes
  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      const reactFlowBounds = reactFlowWrapper.current?.getBoundingClientRect();
      const type = event.dataTransfer.getData('application/nodeType');

      // Check if the dropped element is valid
      if (typeof type === 'undefined' || !type) {
        return;
      }

      if (reactFlowBounds && reactFlowInstance) {
        const position = reactFlowInstance.project({
          x: event.clientX - reactFlowBounds.left,
          y: event.clientY - reactFlowBounds.top,
        });
        
        // Create a new node with the dropped type
        const newNode = createNode(type, position);
        setNodes((nds) => nds.concat(newNode));
        
        // Show success toast
        toast.success(`Added ${NODE_TYPE_DEFINITIONS[type as keyof typeof NODE_TYPE_DEFINITIONS].label}`);
      }
    },
    [reactFlowInstance, setNodes]
  );

  // Handle drag start for node types
  const onDragStart = (event: React.DragEvent, nodeType: string) => {
    event.dataTransfer.setData('application/nodeType', nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  // Save process definition
  const handleSaveProcess = async () => {
    if (nodes.length === 0) {
      toast.warning('Please add at least one node to the process');
      return;
    }

    try {
      const processDefinition = convertToProcessDefinition(
        nodes,
        edges,
        processName,
        processDescription
      );
      
      const response = await createProcessDefinition(processDefinition);
      setCurrentProcessId(response.data.id);
      toast.success('Process saved successfully!');
    } catch (error) {
      console.error('Failed to save process:', error);
      toast.error('Failed to save process. Please check your configuration.');
    }
  };

  // Execute process
  const handleExecuteProcess = async () => {
    if (!currentProcessId) {
      toast.warning('Please save the process first');
      return;
    }

    setExecuting(true);
    try {
      await executeProcess(currentProcessId);
      toast.success('Process execution started!');
    } catch (error) {
      console.error('Failed to execute process:', error);
      toast.error('Failed to execute process. Please try again.');
    } finally {
      setExecuting(false);
    }
  };

  // Add example nodes for testing
  const addExampleProcess = () => {
    const loggingNode = createNode('logging', { x: 250, y: 100 });
    const httpNode = createNode('http', { x: 250, y: 250 });
    const scriptNode = createNode('script', { x: 450, y: 250 });
    const conditionalNode = createNode('conditional', { x: 250, y: 400 });
    
    setNodes([loggingNode, httpNode, scriptNode, conditionalNode]);
    
    const newEdges = [
      {
        id: `${loggingNode.id}-${httpNode.id}`,
        source: loggingNode.id,
        target: httpNode.id,
        type: 'smoothstep',
        animated: true,
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      {
        id: `${httpNode.id}-${scriptNode.id}`,
        source: httpNode.id,
        target: scriptNode.id,
        type: 'smoothstep',
        animated: true,
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      {
        id: `${httpNode.id}-${conditionalNode.id}`,
        source: httpNode.id,
        target: conditionalNode.id,
        type: 'smoothstep',
        animated: true,
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
    ];
    
    setEdges(newEdges);
    toast.info('Example process created');
  };

  return (
    <div className="app-container">
      <div className="sidebar">
        <h1 className="text-xl font-bold mb-4">Process Orchestration</h1>
        
        <div className="node-palette">
          <h2 className="text-lg font-semibold mb-2">Available Nodes</h2>
          {Object.entries(NODE_TYPE_DEFINITIONS).map(([type, def]) => (
            <NodeTypeItem
              key={type}
              type={type}
              label={def.label}
              description={def.description}
              color={def.color}
              onDragStart={onDragStart}
            />
          ))}
        </div>
        
        <div className="mt-4">
          <div className="mb-2">
            <label className="block text-sm font-medium mb-1">Process Name</label>
            <input
              type="text"
              value={processName}
              onChange={(e) => setProcessName(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            />
          </div>
          
          <div className="mb-4">
            <label className="block text-sm font-medium mb-1">Description</label>
            <textarea
              value={processDescription}
              onChange={(e) => setProcessDescription(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              rows={3}
            />
          </div>
          
          <button
            onClick={handleSaveProcess}
            className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors mb-2"
          >
            Save Process
          </button>
          
          {currentProcessId ? (
            <button
              onClick={handleExecuteProcess}
              disabled={executing}
              className="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 mb-2"
            >
              {executing ? 'Executing...' : 'Execute Process'}
            </button>
          ) : (
            <button
              onClick={addExampleProcess}
              className="w-full px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors mb-2"
            >
              Create Example Process
            </button>
          )}
        </div>
      </div>
      
      <div className="main-content">
        <div className="process-canvas" ref={reactFlowWrapper}>
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
              nodeTypes={nodeTypes}
              fitView
              attributionPosition="bottom-right"
            >
              <Controls />
              <Background color="#aaa" gap={16} />
            </ReactFlow>
          </ReactFlowProvider>
        </div>
      </div>
    </div>
  );
};

export default ProcessDesigner;
