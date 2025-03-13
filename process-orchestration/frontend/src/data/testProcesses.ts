import { Node, Edge } from 'reactflow';
import { createNode, NODE_TYPE_DEFINITIONS } from '../utils/processUtils';

// Define type for node properties
type NodeProperties = {
  logging: { message: string; level: string };
  http: { url: string; method: string; headers: string };
  script: { script: string; language: string };
  conditional: { condition: string; evaluationType: string };
};

/**
 * Test process configurations for demonstrating different orchestration patterns
 */

// Simple Sequential Process
export const createSequentialProcess = (): { nodes: Node[], edges: Edge[] } => {
  // Create nodes
  const startNode = createNode('logging', { x: 250, y: 100 });
  (startNode.data.properties as NodeProperties['logging']).message = 'Starting sequential process';
  
  const httpNode = createNode('http', { x: 250, y: 250 });
  (httpNode.data.properties as NodeProperties['http']).url = 'https://api.example.com/data';
  (httpNode.data.properties as NodeProperties['http']).method = 'GET';
  
  const scriptNode = createNode('script', { x: 250, y: 400 });
  (scriptNode.data.properties as NodeProperties['script']).script = 'console.log("Processing data from API");';
  
  const endNode = createNode('logging', { x: 250, y: 550 });
  (endNode.data.properties as NodeProperties['logging']).message = 'Sequential process completed';
  
  // Create edges
  const edges: Edge[] = [
    {
      id: `${startNode.id}-${httpNode.id}`,
      source: startNode.id,
      target: httpNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${httpNode.id}-${scriptNode.id}`,
      source: httpNode.id,
      target: scriptNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${scriptNode.id}-${endNode.id}`,
      source: scriptNode.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    }
  ];
  
  return {
    nodes: [startNode, httpNode, scriptNode, endNode],
    edges
  };
};

// Parallel Process with Join
export const createParallelProcess = (): { nodes: Node[], edges: Edge[] } => {
  // Create nodes
  const startNode = createNode('logging', { x: 250, y: 100 });
  (startNode.data.properties as NodeProperties['logging']).message = 'Starting parallel process';
  
  const httpNode1 = createNode('http', { x: 100, y: 250 });
  (httpNode1.data.properties as NodeProperties['http']).url = 'https://api.example.com/users';
  (httpNode1.data.properties as NodeProperties['http']).method = 'GET';
  
  const httpNode2 = createNode('http', { x: 400, y: 250 });
  (httpNode2.data.properties as NodeProperties['http']).url = 'https://api.example.com/products';
  (httpNode2.data.properties as NodeProperties['http']).method = 'GET';
  
  const scriptNode = createNode('script', { x: 250, y: 400 });
  (scriptNode.data.properties as NodeProperties['script']).script = 'console.log("Combining data from parallel requests");';
  
  const endNode = createNode('logging', { x: 250, y: 550 });
  (endNode.data.properties as NodeProperties['logging']).message = 'Parallel process completed';
  
  // Create edges
  const edges: Edge[] = [
    {
      id: `${startNode.id}-${httpNode1.id}`,
      source: startNode.id,
      target: httpNode1.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
    },
    {
      id: `${startNode.id}-${httpNode2.id}`,
      source: startNode.id,
      target: httpNode2.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
    },
    {
      id: `${httpNode1.id}-${scriptNode.id}`,
      source: httpNode1.id,
      target: scriptNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${httpNode2.id}-${scriptNode.id}`,
      source: httpNode2.id,
      target: scriptNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${scriptNode.id}-${endNode.id}`,
      source: scriptNode.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    }
  ];
  
  return {
    nodes: [startNode, httpNode1, httpNode2, scriptNode, endNode],
    edges
  };
};

// Conditional Process with Branching
export const createConditionalProcess = (): { nodes: Node[], edges: Edge[] } => {
  // Create nodes
  const startNode = createNode('logging', { x: 250, y: 100 });
  (startNode.data.properties as NodeProperties['logging']).message = 'Starting conditional process';
  
  const conditionalNode = createNode('conditional', { x: 250, y: 250 });
  (conditionalNode.data.properties as NodeProperties['conditional']).condition = 'data.status === "success"';
  
  const successNode = createNode('script', { x: 100, y: 400 });
  (successNode.data.properties as NodeProperties['script']).script = 'console.log("Processing successful response");';
  
  const failureNode = createNode('script', { x: 400, y: 400 });
  (failureNode.data.properties as NodeProperties['script']).script = 'console.log("Handling error response");';
  
  const endNode = createNode('logging', { x: 250, y: 550 });
  (endNode.data.properties as NodeProperties['logging']).message = 'Conditional process completed';
  
  // Create edges
  const edges: Edge[] = [
    {
      id: `${startNode.id}-${conditionalNode.id}`,
      source: startNode.id,
      target: conditionalNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${conditionalNode.id}-${successNode.id}`,
      source: conditionalNode.id,
      target: successNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
      label: 'True'
    },
    {
      id: `${conditionalNode.id}-${failureNode.id}`,
      source: conditionalNode.id,
      target: failureNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
      label: 'False'
    },
    {
      id: `${successNode.id}-${endNode.id}`,
      source: successNode.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${failureNode.id}-${endNode.id}`,
      source: failureNode.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    }
  ];
  
  return {
    nodes: [startNode, conditionalNode, successNode, failureNode, endNode],
    edges
  };
};

// Complex Multi-Level Process
export const createComplexProcess = (): { nodes: Node[], edges: Edge[] } => {
  // Create nodes
  const startNode = createNode('logging', { x: 250, y: 50 });
  (startNode.data.properties as NodeProperties['logging']).message = 'Starting complex process';
  
  // Level 1 - Parallel HTTP requests
  const httpNode1 = createNode('http', { x: 100, y: 150 });
  (httpNode1.data.properties as NodeProperties['http']).url = 'https://api.example.com/users';
  (httpNode1.data.properties as NodeProperties['http']).method = 'GET';
  
  const httpNode2 = createNode('http', { x: 400, y: 150 });
  (httpNode2.data.properties as NodeProperties['http']).url = 'https://api.example.com/products';
  (httpNode2.data.properties as NodeProperties['http']).method = 'GET';
  
  // Level 2 - Process data
  const scriptNode1 = createNode('script', { x: 100, y: 250 });
  (scriptNode1.data.properties as NodeProperties['script']).script = 'console.log("Processing user data");';
  
  const scriptNode2 = createNode('script', { x: 400, y: 250 });
  (scriptNode2.data.properties as NodeProperties['script']).script = 'console.log("Processing product data");';
  
  // Level 3 - Conditional check
  const conditionalNode = createNode('conditional', { x: 250, y: 350 });
  (conditionalNode.data.properties as NodeProperties['conditional']).condition = 'data.users.length > 0 && data.products.length > 0';
  
  // Level 4 - Parallel processing based on condition
  const successNode1 = createNode('script', { x: 100, y: 450 });
  (successNode1.data.properties as NodeProperties['script']).script = 'console.log("Generating user report");';
  
  const successNode2 = createNode('script', { x: 400, y: 450 });
  (successNode2.data.properties as NodeProperties['script']).script = 'console.log("Generating product report");';
  
  const failureNode = createNode('logging', { x: 250, y: 450 });
  (failureNode.data.properties as NodeProperties['logging']).message = 'No data available for processing';
  (failureNode.data.properties as NodeProperties['logging']).level = 'WARN';
  
  // Level 5 - Final processing
  const endNode = createNode('logging', { x: 250, y: 550 });
  (endNode.data.properties as NodeProperties['logging']).message = 'Complex process completed';
  
  // Create edges
  const edges: Edge[] = [
    // Level 0 to Level 1
    {
      id: `${startNode.id}-${httpNode1.id}`,
      source: startNode.id,
      target: httpNode1.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
    },
    {
      id: `${startNode.id}-${httpNode2.id}`,
      source: startNode.id,
      target: httpNode2.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
    },
    
    // Level 1 to Level 2
    {
      id: `${httpNode1.id}-${scriptNode1.id}`,
      source: httpNode1.id,
      target: scriptNode1.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${httpNode2.id}-${scriptNode2.id}`,
      source: httpNode2.id,
      target: scriptNode2.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    
    // Level 2 to Level 3
    {
      id: `${scriptNode1.id}-${conditionalNode.id}`,
      source: scriptNode1.id,
      target: conditionalNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${scriptNode2.id}-${conditionalNode.id}`,
      source: scriptNode2.id,
      target: conditionalNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    
    // Level 3 to Level 4
    {
      id: `${conditionalNode.id}-${successNode1.id}`,
      source: conditionalNode.id,
      target: successNode1.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
      label: 'True'
    },
    {
      id: `${conditionalNode.id}-${successNode2.id}`,
      source: conditionalNode.id,
      target: successNode2.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#2979FF', strokeDasharray: '5,5' },
      label: 'True'
    },
    {
      id: `${conditionalNode.id}-${failureNode.id}`,
      source: conditionalNode.id,
      target: failureNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
      label: 'False'
    },
    
    // Level 4 to Level 5
    {
      id: `${successNode1.id}-${endNode.id}`,
      source: successNode1.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${successNode2.id}-${endNode.id}`,
      source: successNode2.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    },
    {
      id: `${failureNode.id}-${endNode.id}`,
      source: failureNode.id,
      target: endNode.id,
      type: 'smoothstep',
      animated: true,
      style: { stroke: '#555' },
    }
  ];
  
  return {
    nodes: [
      startNode, 
      httpNode1, httpNode2, 
      scriptNode1, scriptNode2, 
      conditionalNode, 
      successNode1, successNode2, failureNode, 
      endNode
    ],
    edges
  };
};
