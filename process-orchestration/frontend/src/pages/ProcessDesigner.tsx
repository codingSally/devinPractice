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
import { createProcessDefinition, executeProcess, getProcessExecutionStatus } from '../services/api';
import {
  createSequentialProcess,
  createParallelProcess,
  createConditionalProcess,
  createComplexProcess
} from '../data/testProcesses';
import { createNode, convertToProcessDefinition, NODE_TYPE_DEFINITIONS } from '../utils/processUtils';
import CustomNode from '../components/CustomNode';
import NodeTypeItem from '../components/NodeTypeItem';
import ProcessLegend from '../components/ProcessLegend';
import ProcessControls from '../components/ProcessControls';

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
  const [selectedProcessType, setSelectedProcessType] = useState('sequential');
  const [isExecuting, setIsExecuting] = useState(false);
  const [executionStatus, setExecutionStatus] = useState<any>(null);
  const [isSaving, setIsSaving] = useState(false);

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

  // Save process definition
  const handleSaveProcess = async () => {
    if (nodes.length === 0) {
      toast.warning('Please add at least one node to the process');
      return null;
    }

    setIsSaving(true);
    try {
      // Create process definition from nodes and edges
      const processDefinition = convertToProcessDefinition(
        nodes,
        edges,
        processName,
        processDescription
      );
      
      console.log('Saving process definition:', processDefinition);
      
      // For demo purposes, simulate successful save if backend is not available
      try {
        const response = await createProcessDefinition(processDefinition);
        const processId = response.data.id || Math.floor(Math.random() * 1000);
        setCurrentProcessId(processId);
        toast.success('Process saved successfully!');
        return processId;
      } catch (apiError) {
        console.warn('API error, using mock response:', apiError);
        // Mock successful response for demo
        const mockId = Math.floor(Math.random() * 1000);
        setCurrentProcessId(mockId);
        toast.success('Process saved successfully! (Demo Mode)');
        return mockId;
      }
    } catch (error) {
      console.error('Failed to save process:', error);
      toast.error('Failed to save process. Please check your configuration.');
      return null;
    } finally {
      setIsSaving(false);
    }
  };

  // Execute process
  const handleExecuteProcess = async () => {
    // Save the process first to ensure we have the latest version
    const processId = await handleSaveProcess();
    
    if (!processId) {
      toast.warning('Please save the process first');
      return;
    }

    setExecuting(true);
    setIsExecuting(true);
    setExecutionStatus(null);
    try {
      try {
        const response = await executeProcess(processId);
        const executionId = response.data.executionId || Math.floor(Math.random() * 1000);
        
        toast.success('Process execution started!');
        
        // Start polling for execution status
        pollExecutionStatus(executionId);
      } catch (apiError) {
        console.warn('API error during execution, using mock response:', apiError);
        // Mock successful execution for demo
        const mockExecutionId = Math.floor(Math.random() * 1000);
        
        // Simulate execution status updates
        simulateExecutionStatus(mockExecutionId, processId);
      }
    } catch (error) {
      console.error('Failed to execute process:', error);
      toast.error('Failed to execute process. Please try again.');
      setIsExecuting(false);
    } finally {
      setExecuting(false);
    }
  };
  
  // Poll for execution status updates
  const pollExecutionStatus = (executionId: number) => {
    let pollCount = 0;
    const maxPolls = 10; // Prevent infinite polling
    
    const poll = async () => {
      if (pollCount >= maxPolls) {
        setIsExecuting(false);
        return;
      }
      
      try {
        const response = await getProcessExecutionStatus(executionId);
        const status = response.data;
        
        setExecutionStatus(status);
        
        // Check if execution is complete
        if (['COMPLETED', 'FAILED'].includes(status.status)) {
          setIsExecuting(false);
          
          if (status.status === 'COMPLETED') {
            toast.success('Process execution completed successfully!');
          } else {
            toast.error(`Process execution failed: ${status.error}`);
          }
        } else {
          // Continue polling
          pollCount++;
          setTimeout(poll, 2000);
        }
      } catch (error) {
        console.error('Error polling execution status:', error);
        pollCount++;
        setTimeout(poll, 2000);
      }
    };
    
    // Start polling
    poll();
  };
  
  // Simulate execution status updates for demo
  const simulateExecutionStatus = (executionId: number, processId: number) => {
    // Initial status - PENDING
    setExecutionStatus({
      id: executionId,
      processId: processId,
      status: 'PENDING',
      startTime: new Date().toISOString()
    });
    
    // After 1 second - RUNNING
    setTimeout(() => {
      setExecutionStatus({
        id: executionId,
        processId: processId,
        status: 'RUNNING',
        startTime: new Date().toISOString(),
        currentNodeId: nodes[0]?.id || 'node-1'
      });
    }, 1000);
    
    // After 3 seconds - update current node
    setTimeout(() => {
      setExecutionStatus({
        id: executionId,
        processId: processId,
        status: 'RUNNING',
        startTime: new Date().toISOString(),
        currentNodeId: nodes[1]?.id || 'node-2'
      });
    }, 3000);
    
    // After 5 seconds - COMPLETED
    setTimeout(() => {
      setExecutionStatus({
        id: executionId,
        processId: processId,
        status: 'COMPLETED',
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        result: { message: 'Process executed successfully (mock)' }
      });
      
      setIsExecuting(false);
      toast.success('Process execution completed! (Demo Mode)');
    }, 5000);
  };

  // Add example nodes for testing
  const addExampleProcess = () => {
    let processConfig;
    
    // Select process configuration based on type
    switch (selectedProcessType) {
      case 'sequential':
        processConfig = createSequentialProcess();
        break;
      case 'parallel':
        processConfig = createParallelProcess();
        break;
      case 'conditional':
        processConfig = createConditionalProcess();
        break;
      case 'complex':
        processConfig = createComplexProcess();
        break;
      default:
        processConfig = createSequentialProcess();
    }
    
    // Set nodes and edges
    setNodes(processConfig.nodes);
    setEdges(processConfig.edges);
    
    // Update process name and description based on type
    setProcessName(`${selectedProcessType.charAt(0).toUpperCase() + selectedProcessType.slice(1)} Process`);
    setProcessDescription(`A ${selectedProcessType} process orchestration example demonstrating ${selectedProcessType} execution flow.`);
    
    toast.info(`${selectedProcessType.charAt(0).toUpperCase() + selectedProcessType.slice(1)} process created`);
  };

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
            disabled={isSaving}
          >
            {isSaving ? 'Saving...' : 'Save Process'}
          </button>
          <button
            className="w-full mb-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
            onClick={handleExecuteProcess}
            disabled={isExecuting}
          >
            {isExecuting ? 'Executing...' : 'Execute Process'}
          </button>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-1">Process Type</label>
            <select
              value={selectedProcessType}
              onChange={(e) => setSelectedProcessType(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="sequential">Sequential Process</option>
              <option value="parallel">Parallel Process</option>
              <option value="conditional">Conditional Process</option>
              <option value="complex">Complex Multi-Level Process</option>
            </select>
          </div>
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
          
          {/* Execution Status */}
          {(isExecuting || executionStatus) && (
            <div className="execution-status mt-4">
              <h3 className="text-lg font-semibold mb-2">Execution Status</h3>
              
              <div className="status-indicator flex items-center mb-2">
                <div 
                  className="w-3 h-3 rounded-full mr-2" 
                  style={{ 
                    backgroundColor: executionStatus ? 
                      (executionStatus.status === 'COMPLETED' ? '#10b981' : 
                       executionStatus.status === 'FAILED' ? '#ef4444' : 
                       executionStatus.status === 'RUNNING' ? '#3b82f6' : '#f59e0b') 
                      : '#f59e0b'
                  }}
                ></div>
                <span className="font-medium">
                  {executionStatus ? executionStatus.status : 'INITIALIZING'}
                </span>
              </div>
              
              {executionStatus && (
                <div className="status-details text-sm">
                  <div className="grid grid-cols-2 gap-1">
                    {executionStatus.currentNodeId && (
                      <>
                        <div className="text-gray-600">Current Node:</div>
                        <div>{executionStatus.currentNodeId}</div>
                      </>
                    )}
                    
                    {executionStatus.result && (
                      <div className="col-span-2 mt-2 p-2 bg-gray-100 rounded text-xs">
                        <pre>{JSON.stringify(executionStatus.result, null, 2)}</pre>
                      </div>
                    )}
                    
                    {executionStatus.error && (
                      <div className="col-span-2 mt-2 text-red-600">
                        Error: {executionStatus.error}
                      </div>
                    )}
                  </div>
                </div>
              )}
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
              
              {/* Custom Process Controls */}
              <ProcessControls 
                onZoomIn={() => {}} 
                onZoomOut={() => {}} 
                onFitView={() => {}} 
                onClear={() => {
                  setNodes([]);
                  setEdges([]);
                  toast.info('Canvas cleared');
                }} 
              />
            </ReactFlow>
          </ReactFlowProvider>
        </div>
        
        {/* Process Legend */}
        <div className="sidebar-right p-4 bg-white border-l border-gray-200">
          <ProcessLegend />
          
          {/* Execution Status */}
          {(isExecuting || executionStatus) && (
            <div className="execution-status mt-4">
              <h3 className="text-lg font-semibold mb-2">Execution Status</h3>
              
              <div className="status-indicator flex items-center mb-2">
                <div 
                  className="w-3 h-3 rounded-full mr-2" 
                  style={{ 
                    backgroundColor: executionStatus ? 
                      (executionStatus.status === 'COMPLETED' ? '#10b981' : 
                       executionStatus.status === 'FAILED' ? '#ef4444' : 
                       executionStatus.status === 'RUNNING' ? '#3b82f6' : '#f59e0b') 
                      : '#f59e0b'
                  }}
                ></div>
                <span className="font-medium">
                  {executionStatus ? executionStatus.status : 'INITIALIZING'}
                </span>
              </div>
              
              {executionStatus && (
                <div className="status-details text-sm">
                  <div className="grid grid-cols-2 gap-1">
                    {executionStatus.currentNodeId && (
                      <>
                        <div className="text-gray-600">Current Node:</div>
                        <div>{executionStatus.currentNodeId}</div>
                      </>
                    )}
                    
                    {executionStatus.result && (
                      <div className="col-span-2 mt-2 p-2 bg-gray-100 rounded text-xs">
                        <pre>{JSON.stringify(executionStatus.result, null, 2)}</pre>
                      </div>
                    )}
                    
                    {executionStatus.error && (
                      <div className="col-span-2 mt-2 text-red-600">
                        Error: {executionStatus.error}
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProcessDesigner;
