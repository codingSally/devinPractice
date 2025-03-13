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
import { 
  createNode, 
  convertToProcessDefinition, 
  NODE_TYPE_DEFINITIONS,
  calculateNodeLevels,
  areSameLevel
} from '../utils/processUtils';
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
      // Calculate node levels to determine if nodes are on the same level
      const levels = calculateNodeLevels(nodes, edges);
      const sourceNode = nodes.find(n => n.id === params.source);
      const targetNode = nodes.find(n => n.id === params.target);
      
      // Determine if the connection is between nodes on the same level
      const sameLevel = sourceNode && targetNode && 
        levels[sourceNode.id] === levels[targetNode.id];
      
      // Create edge with appropriate style based on connection type
      const edge = {
        ...params,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel },
        style: { 
          stroke: sameLevel ? '#2979FF' : '#555',
          strokeDasharray: sameLevel ? '5,5' : 'none',
          strokeWidth: sameLevel ? 1.5 : 2
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: sameLevel ? '#2979FF' : '#555',
        },
      };
      setEdges((eds) => addEdge(edge, eds));
    },
    [nodes, edges, setEdges]
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
    // Create nodes for different levels to demonstrate breadth-first traversal
    const loggingNode = createNode('logging', { x: 250, y: 100 });
    const httpNode = createNode('http', { x: 150, y: 250 });
    const scriptNode = createNode('script', { x: 350, y: 250 });
    const conditionalNode = createNode('conditional', { x: 250, y: 400 });
    
    // Add additional nodes to demonstrate parallel execution
    const loggingNode2 = createNode('logging', { x: 550, y: 100 });
    const httpNode2 = createNode('http', { x: 550, y: 250 });
    
    setNodes([loggingNode, httpNode, scriptNode, conditionalNode, loggingNode2, httpNode2]);
    
    // Create edges with appropriate styling for level relationships
    const newEdges = [
      // Level 0 to Level 1 connections (sequential)
      {
        id: `${loggingNode.id}-${httpNode.id}`,
        source: loggingNode.id,
        target: httpNode.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: false },
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      {
        id: `${loggingNode.id}-${scriptNode.id}`,
        source: loggingNode.id,
        target: scriptNode.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: false },
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      // Level 1 to Level 2 connections (sequential)
      {
        id: `${httpNode.id}-${conditionalNode.id}`,
        source: httpNode.id,
        target: conditionalNode.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: false },
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      {
        id: `${scriptNode.id}-${conditionalNode.id}`,
        source: scriptNode.id,
        target: conditionalNode.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: false },
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      // Same level connection (parallel) - Level 0
      {
        id: `${loggingNode.id}-${loggingNode2.id}`,
        source: loggingNode.id,
        target: loggingNode2.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: true },
        style: { 
          stroke: '#2979FF',
          strokeDasharray: '5,5',
          strokeWidth: 1.5
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#2979FF',
        },
      },
      // Level 0 to Level 1 connection
      {
        id: `${loggingNode2.id}-${httpNode2.id}`,
        source: loggingNode2.id,
        target: httpNode2.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: false },
        style: { stroke: '#555' },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#555',
        },
      },
      // Same level connection (parallel) - Level 1
      {
        id: `${httpNode.id}-${httpNode2.id}`,
        source: httpNode.id,
        target: httpNode2.id,
        type: 'smoothstep',
        animated: true,
        data: { sameLevel: true },
        style: { 
          stroke: '#2979FF',
          strokeDasharray: '5,5',
          strokeWidth: 1.5
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: '#2979FF',
        },
      },
    ];
    
    setEdges(newEdges);
    toast.info('Example process created with breadth-first traversal visualization');
  };

  // Calculate node levels for visualization
  const nodeLevels = calculateNodeLevels(nodes, edges);
  
  // Group nodes by level for display
  const nodesByLevel: Record<number, Node[]> = {};
  nodes.forEach(node => {
    const level = nodeLevels[node.id] || 0;
    if (!nodesByLevel[level]) {
      nodesByLevel[level] = [];
    }
    nodesByLevel[level].push(node);
  });
  
  return (
    <div className="app-container">
      <div className="sidebar">
        <h2 className="text-xl font-bold mb-4">Process Orchestration</h2>
        
        <div className="node-palette mb-6">
          <h3 className="text-lg font-semibold mb-2">Available Nodes</h3>
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
        
        <div className="actions mb-6">
          <button
            className="w-full mb-2 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            onClick={handleSaveProcess}
          >
            Save Process
          </button>
          <button
            className="w-full mb-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
            onClick={handleExecuteProcess}
            disabled={!currentProcessId || executing}
          >
            {executing ? 'Executing...' : 'Execute Process'}
          </button>
          <button
            className="w-full px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors"
            onClick={addExampleProcess}
          >
            Create Example Process
          </button>
        </div>
        
        <div className="process-info">
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
          
          {/* Execution Level Legend */}
          <div className="execution-legend mb-4">
            <h4 className="text-sm font-semibold mb-2">Execution Legend</h4>
            <div className="flex items-center mb-1">
              <div className="w-4 h-1 bg-gray-600 mr-2"></div>
              <span className="text-xs">Sequential Execution</span>
            </div>
            <div className="flex items-center">
              <div className="w-4 h-1 bg-blue-500 mr-2" style={{ borderStyle: 'dashed' }}></div>
              <span className="text-xs">Parallel Execution (Same Level)</span>
            </div>
          </div>
          
          {/* Node Level Information */}
          {Object.keys(nodesByLevel).length > 0 && (
            <div className="level-info">
              <h4 className="text-sm font-semibold mb-2">Execution Levels</h4>
              {Object.entries(nodesByLevel).map(([level, levelNodes]) => (
                <div key={level} className="mb-2">
                  <div className="text-xs font-medium">Level {level}:</div>
                  <div className="text-xs text-gray-600">
                    {levelNodes.map(node => node.data.label).join(', ')}
                    <span className="text-xs text-gray-500"> (Parallel)</span>
                  </div>
                </div>
              ))}
            </div>
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
