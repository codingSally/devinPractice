import React from 'react';

interface ConnectionLineProps {
  fromX: number;
  fromY: number;
  toX: number;
  toY: number;
  fromPosition?: string;
  toPosition?: string;
}

const ConnectionLine: React.FC<ConnectionLineProps> = ({
  fromX,
  fromY,
  toX,
  toY,
  fromPosition = 'bottom',
  toPosition = 'top',
}) => {
  // Calculate control points for the bezier curve
  const midX = (fromX + toX) / 2;
  const midY = (fromY + toY) / 2;
  
  // Create path for the bezier curve
  const path = `M${fromX},${fromY} C${fromX},${midY} ${toX},${midY} ${toX},${toY}`;
  
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
      <path
        d={path}
        stroke="#555"
        strokeWidth={2}
        fill="none"
        markerEnd="url(#arrowhead)"
      />
      <defs>
        <marker
          id="arrowhead"
          markerWidth="10"
          markerHeight="7"
          refX="9"
          refY="3.5"
          orient="auto"
        >
          <polygon points="0 0, 10 3.5, 0 7" fill="#555" />
        </marker>
      </defs>
    </svg>
  );
};

export default ConnectionLine;
