import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import NodeList from '../components/NodeList';
import ProcessCanvas from '../components/ProcessCanvas';
import { getNodeTypes, createProcessDefinition, executeProcess } from '../services/api';

const ProcessDesigner: React.FC = () => {
  const [nodeTypes, setNodeTypes] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [executing, setExecuting] = useState<boolean>(false);
  const [currentProcessId, setCurrentProcessId] = useState<number | null>(null);

  useEffect(() => {
    // Load available node types
    const loadNodeTypes = async () => {
      try {
        const response = await getNodeTypes();
        setNodeTypes(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Failed to load node types:', error);
        toast.error('Failed to load node types. Please try again.');
        setLoading(false);
      }
    };

    loadNodeTypes();
  }, []);

  const handleDragStart = (event: React.DragEvent, nodeType: string) => {
    event.dataTransfer.setData('application/reactflow', nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  const handleSaveProcess = async (processDefinition: any) => {
    try {
      const response = await createProcessDefinition(processDefinition);
      setCurrentProcessId(response.data.id);
      toast.success('Process saved successfully!');
    } catch (error) {
      console.error('Failed to save process:', error);
      toast.error('Failed to save process. Please check your configuration.');
    }
  };

  const handleExecuteProcess = async () => {
    if (!currentProcessId) {
      toast.warning('Please save the process first.');
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

  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }

  return (
    <div className="flex h-screen bg-gray-100">
      <div className="w-64 p-4 bg-white shadow-md overflow-auto">
        <h1 className="text-xl font-bold mb-6">Process Orchestration</h1>
        <NodeList onDragStart={handleDragStart} />
        
        {currentProcessId && (
          <div className="mt-6">
            <button
              onClick={handleExecuteProcess}
              disabled={executing}
              className="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors disabled:opacity-50"
            >
              {executing ? 'Executing...' : 'Execute Process'}
            </button>
          </div>
        )}
      </div>
      
      <div className="flex-1 overflow-hidden">
        <ProcessCanvas onSave={handleSaveProcess} />
      </div>
    </div>
  );
};

export default ProcessDesigner;
