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

// Calculate node levels for breadth-first traversal
export const calculateNodeLevels = (nodes: Node[], edges: Edge[]) => {
  const levels: Record<string, number> = {};
  const connections: Record<string, string[]> = {};
  
  // Build connections map
  edges.forEach(edge => {
    if (!connections[edge.source]) {
      connections[edge.source] = [];
    }
    connections[edge.source].push(edge.target);
  });
  
  // Find root nodes (nodes with no incoming edges)
  const hasIncomingEdge: Record<string, boolean> = {};
  edges.forEach(edge => {
    hasIncomingEdge[edge.target] = true;
  });
  
  const rootNodes = nodes.filter(node => !hasIncomingEdge[node.id]).map(node => node.id);
  
  // Assign level 0 to root nodes
  rootNodes.forEach(nodeId => {
    levels[nodeId] = 0;
  });
  
  // Breadth-first traversal to assign levels
  const queue = [...rootNodes];
  while (queue.length > 0) {
    const currentId = queue.shift()!;
    const currentLevel = levels[currentId];
    
    // Process children
    const children = connections[currentId] || [];
    children.forEach(childId => {
      // Assign level + 1 to children
      if (levels[childId] === undefined || levels[childId] < currentLevel + 1) {
        levels[childId] = currentLevel + 1;
        queue.push(childId);
      }
    });
  }
  
  return levels;
};

// Check if two nodes are on the same level
export const areSameLevel = (node1Id: string, node2Id: string, levels: Record<string, number>) => {
  return levels[node1Id] === levels[node2Id];
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
  
  // Calculate node levels for breadth-first traversal
  const levels = calculateNodeLevels(nodes, edges);
  
  // Group nodes by level for parallel execution
  const nodesByLevel: Record<number, string[]> = {};
  Object.entries(levels).forEach(([nodeId, level]) => {
    if (!nodesByLevel[level]) {
      nodesByLevel[level] = [];
    }
    nodesByLevel[level].push(nodeId);
  });
  
  // Convert nodes to process nodes with level information
  const processNodes = nodes.map(node => {
    return {
      id: node.id,
      type: node.type,
      properties: node.data.properties,
      nextNodes: connections[node.id] || [],
      level: levels[node.id] || 0,
      parallelExecutionGroup: nodesByLevel[levels[node.id] || 0]
    };
  });
  
  return {
    name,
    description,
    nodes: processNodes,
    executionLevels: nodesByLevel
  };
};
