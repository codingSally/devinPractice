import { v4 as uuidv4 } from 'uuid';

// Node types
export const NODE_TYPES = {
  LOGGING: 'logging',
  HTTP: 'http',
  SCRIPT: 'script',
  CONDITIONAL: 'conditional',
};

// Node type definitions with default properties
export const NODE_TYPE_DEFINITIONS = {
  [NODE_TYPES.LOGGING]: {
    type: NODE_TYPES.LOGGING,
    label: 'Logging Node',
    description: 'Logs a message to the console',
    defaultProperties: {
      message: 'Log message',
    },
    color: '#4CAF50',
  },
  [NODE_TYPES.HTTP]: {
    type: NODE_TYPES.HTTP,
    label: 'HTTP Node',
    description: 'Makes an HTTP request',
    defaultProperties: {
      url: 'https://example.com',
      method: 'GET',
    },
    color: '#2196F3',
  },
  [NODE_TYPES.SCRIPT]: {
    type: NODE_TYPES.SCRIPT,
    label: 'Script Node',
    description: 'Executes a JavaScript script',
    defaultProperties: {
      script: 'var result = 1 + 1; result;',
    },
    color: '#FF9800',
  },
  [NODE_TYPES.CONDITIONAL]: {
    type: NODE_TYPES.CONDITIONAL,
    label: 'Conditional Node',
    description: 'Evaluates a condition',
    defaultProperties: {
      condition: 'true',
    },
    color: '#9C27B0',
  },
};

// Create a new node
export const createNode = (type: string, position: { x: number, y: number }) => {
  const nodeType = NODE_TYPE_DEFINITIONS[type];
  
  if (!nodeType) {
    throw new Error(`Unknown node type: ${type}`);
  }
  
  return {
    id: `node-${uuidv4()}`,
    type,
    position,
    data: {
      label: nodeType.label,
      properties: { ...nodeType.defaultProperties },
    },
    style: {
      background: nodeType.color,
      color: '#fff',
      border: '1px solid #ddd',
      borderRadius: '5px',
      padding: '10px',
      width: 180,
    },
  };
};

// Convert React Flow nodes and edges to process definition
export const convertToProcessDefinition = (nodes: any[], edges: any[], name: string, description: string) => {
  // Create a map of node IDs to child node IDs
  const childNodeMap: Record<string, string[]> = {};
  
  // Initialize all nodes with empty child arrays
  nodes.forEach(node => {
    childNodeMap[node.id] = [];
  });
  
  // Add child nodes based on edges
  edges.forEach(edge => {
    const sourceId = edge.source;
    const targetId = edge.target;
    
    if (childNodeMap[sourceId]) {
      childNodeMap[sourceId].push(targetId);
    }
  });
  
  // Find root nodes (nodes with no incoming edges)
  const rootNodeIds = nodes
    .filter(node => !edges.some(edge => edge.target === node.id))
    .map(node => node.id);
  
  // Create process nodes
  const processNodes = nodes.map(node => ({
    nodeId: node.id,
    name: node.data.label,
    type: node.type,
    properties: node.data.properties,
    childNodeIds: childNodeMap[node.id] || [],
  }));
  
  // Create process definition
  return {
    name,
    description,
    nodes: processNodes,
    rootNodeIds,
  };
};

// Calculate node levels for visualization
export const calculateNodeLevels = (nodes: any[], edges: any[]) => {
  // Create a map of node IDs to nodes
  const nodeMap: Record<string, any> = {};
  nodes.forEach(node => {
    nodeMap[node.id] = { ...node, level: -1 };
  });
  
  // Find root nodes (nodes with no incoming edges)
  const rootNodes = nodes.filter(node => !edges.some(edge => edge.target === node.id));
  
  // Set root nodes to level 0
  rootNodes.forEach(node => {
    nodeMap[node.id].level = 0;
  });
  
  // BFS to calculate levels
  const queue = [...rootNodes];
  const visited = new Set<string>();
  
  while (queue.length > 0) {
    const node = queue.shift()!;
    
    if (visited.has(node.id)) {
      continue;
    }
    
    visited.add(node.id);
    
    // Find outgoing edges
    const outgoingEdges = edges.filter(edge => edge.source === node.id);
    
    // Process child nodes
    outgoingEdges.forEach(edge => {
      const childNode = nodeMap[edge.target];
      
      if (childNode) {
        // Set the level of the child node
        if (childNode.level === -1 || childNode.level > nodeMap[node.id].level + 1) {
          childNode.level = nodeMap[node.id].level + 1;
        }
        
        queue.push(childNode);
      }
    });
  }
  
  // Update node positions based on levels
  return nodes.map(node => ({
    ...node,
    position: {
      ...node.position,
      y: nodeMap[node.id].level * 150,
    },
  }));
};
