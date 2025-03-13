import React from 'react';

interface ConnectionLineProps {
  fromX: number;
  fromY: number;
  toX: number;
  toY: number;
  fromPosition?: string;
  toPosition?: string;
  fromNode?: any;
  toNode?: any;
  sameLevel?: boolean;
}

const ConnectionLine: React.FC<ConnectionLineProps> = ({
  fromX,
  fromY,
  toX,
  toY,
  fromPosition = 'bottom',
  toPosition = 'top',
  fromNode,
  toNode,
  sameLevel = false,
}) => {
  // Calculate control points for the bezier curve
  const midX = (fromX + toX) / 2;
  const midY = (fromY + toY) / 2;
  
  // Create path for the bezier curve
  // Use different curve styles for same-level vs different-level connections
  let path;
  
  if (sameLevel) {
    // For same-level nodes (parallel execution), use a more horizontal curve
    path = `M${fromX},${fromY} C${fromX + 50},${fromY} ${toX - 50},${toY} ${toX},${toY}`;
  } else {
    // For different-level nodes (sequential execution), use a more vertical curve
    path = `M${fromX},${fromY} C${fromX},${midY} ${toX},${midY} ${toX},${toY}`;
  }
  
  // Determine stroke color and style based on connection type
  const strokeColor = sameLevel ? '#2979FF' : '#555';
  const strokeDasharray = sameLevel ? '5,5' : 'none';
  const strokeWidth = sameLevel ? 1.5 : 2;
  
  return (
    <svg
      style={{
        position: 'absolute',
        width: '100%',
        height: '100%',
        top: 0,
        left: 0,
        pointerEvents: 'none',
        zIndex: 1,
      }}
    >
      {/* Connection path */}
      <path
        d={path}
        stroke={strokeColor}
        strokeWidth={strokeWidth}
        strokeDasharray={strokeDasharray}
        fill="none"
        markerEnd={`url(#${sameLevel ? 'parallel-arrowhead' : 'sequential-arrowhead'})`}
      />
      
      {/* Level indicator for sequential connections */}
      {!sameLevel && (
        <circle
          cx={midX}
          cy={midY}
          r={6}
          fill="#555"
          stroke="white"
          strokeWidth={1}
        />
      )}
      
      {/* Arrowhead definitions */}
      <defs>
        <marker
          id="sequential-arrowhead"
          markerWidth="10"
          markerHeight="7"
          refX="9"
          refY="3.5"
          orient="auto"
        >
          <polygon points="0 0, 10 3.5, 0 7" fill="#555" />
        </marker>
        <marker
          id="parallel-arrowhead"
          markerWidth="10"
          markerHeight="7"
          refX="9"
          refY="3.5"
          orient="auto"
        >
          <polygon points="0 0, 10 3.5, 0 7" fill="#2979FF" />
        </marker>
      </defs>
    </svg>
  );
};

export default ConnectionLine;
