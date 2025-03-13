import { Node, Edge } from 'reactflow';

// Node type definitions with colors and descriptions
export const NODE_TYPE_DEFINITIONS = {
  logging: { 
    label: 'Logging Node', 
    description: 'Log messages during process execution',
    color: '#4CAF50' 
  },
  http: { 
    label: 'HTTP Node', 
    description: 'Make HTTP requests',
    color: '#2196F3' 
  },
  script: { 
    label: 'Script Node', 
    description: 'Execute custom scripts',
    color: '#FF9800' 
  },
  conditional: { 
    label: 'Conditional Node', 
    description: 'Add conditional logic',
    color: '#9C27B0' 
  }
};

// Default properties for each node type
const DEFAULT_PROPERTIES = {
  logging: {
    message: 'Log message',
    level: 'INFO'
  },
  http: {
    url: 'https://example.com',
    method: 'GET',
    headers: '{}'
  },
  script: {
    script: 'console.log("Hello World");',
    language: 'javascript'
  },
  conditional: {
    condition: 'value == true',
    evaluationType: 'expression'
  }
};

// Create a new node with the given type and position
export const createNode = (type: string, position: { x: number, y: number }) => {
  const nodeId = `${type}-${Date.now()}`;
  const nodeDefinition = NODE_TYPE_DEFINITIONS[type as keyof typeof NODE_TYPE_DEFINITIONS];
  
  return {
    id: nodeId,
    type,
    position,
    data: {
      label: nodeDefinition.label,
      properties: DEFAULT_PROPERTIES[type as keyof typeof DEFAULT_PROPERTIES]
    }
  };
};

// Convert the React Flow nodes and edges to a process definition for the backend
export const convertToProcessDefinition = (
  nodes: Node[],
  edges: Edge[],
  name: string,
  description: string
) => {
  // Create a map of node connections
  const connections: Record<string, string[]> = {};
  
  edges.forEach(edge => {
    if (!connections[edge.source]) {
      connections[edge.source] = [];
    }
    connections[edge.source].push(edge.target);
  });
  
  // Convert nodes to process nodes
  const processNodes = nodes.map(node => {
    return {
      id: node.id,
      type: node.type,
      properties: node.data.properties,
      nextNodes: connections[node.id] || []
    };
  });
  
  return {
    name,
    description,
    nodes: processNodes
  };
};
