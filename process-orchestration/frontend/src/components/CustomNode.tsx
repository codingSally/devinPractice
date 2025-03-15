import React, { useState } from 'react';
import { Handle, Position, NodeProps } from 'reactflow';

interface CustomNodeProps extends NodeProps {
  data: {
    label: string;
    properties: Record<string, string>;
  };
}

const CustomNode: React.FC<CustomNodeProps> = ({ data, id, type }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  
  const getNodeColor = () => {
    switch (type) {
      case 'logging':
        return '#4CAF50';
      case 'http':
        return '#2196F3';
      case 'script':
        return '#FF9800';
      case 'conditional':
        return '#9C27B0';
      case 'math.addition':
        return '#E91E63';
      case 'math.subtraction':
        return '#673AB7';
      case 'math.multiplication':
        return '#00BCD4';
      case 'math.division':
        return '#FF5722';
      default:
        return '#555';
    }
  };
  
  // Get operation symbol based on node type
  const getOperationSymbol = () => {
    switch (type) {
      case 'math.addition':
        return '+';
      case 'math.subtraction':
        return '-';
      case 'math.multiplication':
        return '×';
      case 'math.division':
        return '÷';
      default:
        return '';
    }
  };
  
  // Display values for math nodes
  const renderMathNodeContent = () => {
    if (!type.startsWith('math.')) return null;
    
    // Use the node label if available, as it contains the correct operation display
    if (data.label && data.label.includes(getOperationSymbol())) {
      return (
        <div style={{ fontSize: '14px', marginTop: '2px', color: 'white' }}>
          {data.label}
        </div>
      );
    }
    
    // Fallback to properties if no label is available
    const leftValue = data.properties.leftOperand || data.properties.leftNodeResult || '?';
    const rightValue = data.properties.rightOperand || data.properties.rightNodeResult || '?';
    
    return (
      <div style={{ fontSize: '14px', marginTop: '2px', color: 'white' }}>
        {leftValue} {getOperationSymbol()} {rightValue}
      </div>
    );
  };
  
  // Get result value if available
  const getResultValue = () => {
    if (!type.startsWith('math.') || !data.properties.result) return null;
    
    return (
      <div style={{ 
        fontSize: '12px', 
        marginTop: '4px', 
        color: 'white', 
        backgroundColor: 'rgba(0,0,0,0.2)', 
        padding: '2px 6px', 
        borderRadius: '10px' 
      }}>
        = {data.properties.result}
      </div>
    );
  };
  
  return (
    <div 
      className="node-container"
      style={{ 
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
      }}
    >
      {/* Circle Node */}
      <div
        className="circle-node"
        style={{
          backgroundColor: getNodeColor(),
          color: 'white',
          width: '80px',
          height: '80px',
          borderRadius: '50%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          cursor: 'pointer',
          border: '2px solid rgba(255, 255, 255, 0.3)',
          position: 'relative',
          zIndex: 1
        }}
        onClick={() => setIsExpanded(!isExpanded)}
      >
        {type.startsWith('math.') ? (
          <React.Fragment>
            {renderMathNodeContent()}
            {getResultValue()}
          </React.Fragment>
        ) : (
          <div style={{ textAlign: 'center', padding: '5px', fontSize: '12px', fontWeight: 'bold' }}>
            {data.label}
          </div>
        )}
      </div>
      
      {/* Handles */}
      <Handle 
        type="target" 
        position={Position.Top} 
        style={{ 
          background: '#555',
          width: '10px',
          height: '10px',
          top: '-5px'
        }} 
      />
      
      <Handle 
        type="source" 
        position={Position.Bottom} 
        style={{ 
          background: '#555',
          width: '10px',
          height: '10px',
          bottom: '-5px'
        }} 
      />
      
      {/* Properties Panel */}
      {isExpanded && (
        <div 
          className="properties-panel"
          style={{
            position: 'absolute',
            top: '90px',
            backgroundColor: 'white',
            border: `2px solid ${getNodeColor()}`,
            borderRadius: '8px',
            padding: '10px',
            width: '220px',
            zIndex: 10,
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
          }}
        >
          <div style={{ fontWeight: 'bold', marginBottom: '5px', color: getNodeColor() }}>Properties:</div>
          {Object.entries(data.properties).map(([key, value]) => (
            <div key={key} style={{ display: 'flex', marginBottom: '3px', flexWrap: 'wrap' }}>
              <span style={{ fontWeight: 'bold', marginRight: '5px', color: '#333' }}>{key}:</span>
              <span style={{ color: '#666' }}>{value}</span>
            </div>
          ))}
          
          {type.startsWith('math.') && (
            <div style={{ 
              marginTop: '8px', 
              padding: '5px', 
              backgroundColor: '#f5f5f5', 
              borderRadius: '4px',
              fontSize: '12px'
            }}>
              <div style={{ fontWeight: 'bold', color: getNodeColor() }}>Operation:</div>
              <div>{renderMathNodeContent()}</div>
              {data.properties.result && (
                <div style={{ marginTop: '4px' }}>
                  <span style={{ fontWeight: 'bold' }}>Result:</span> {data.properties.result}
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CustomNode;
