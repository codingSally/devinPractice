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
  
  // Display values for math nodes with enhanced styling
  const renderMathNodeContent = () => {
    if (!type.startsWith('math.')) return null;
    
    // Use the node label if available, as it contains the correct operation display
    if (data.label && data.label.includes(getOperationSymbol())) {
      return (
        <div style={{ 
          fontSize: '16px', 
          fontWeight: 'bold',
          marginTop: '2px', 
          color: 'white',
          textShadow: '0 1px 2px rgba(0,0,0,0.3)'
        }}>
          {data.label}
        </div>
      );
    }
    
    // Fallback to properties if no label is available
    const leftValue = data.properties.leftOperand || data.properties.leftNodeResult || '?';
    const rightValue = data.properties.rightOperand || data.properties.rightNodeResult || '?';
    
    return (
      <div style={{ 
        fontSize: '16px', 
        fontWeight: 'bold',
        marginTop: '2px', 
        color: 'white',
        textShadow: '0 1px 2px rgba(0,0,0,0.3)'
      }}>
        {leftValue} {getOperationSymbol()} {rightValue}
      </div>
    );
  };
  
  // Get result value with enhanced styling
  const getResultValue = () => {
    if (!type.startsWith('math.') || !data.properties.result) return null;
    
    return (
      <div style={{ 
        fontSize: '14px', 
        marginTop: '6px', 
        color: 'white', 
        backgroundColor: 'rgba(255,255,255,0.2)', 
        padding: '3px 8px', 
        borderRadius: '12px',
        boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
        fontWeight: 'bold'
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
      {/* Enhanced Circle Node */}
      <div
        className="circle-node"
        style={{
          backgroundColor: getNodeColor(),
          color: 'white',
          width: '90px',
          height: '90px',
          borderRadius: '50%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          boxShadow: '0 6px 10px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.2) inset',
          cursor: 'pointer',
          border: '2px solid rgba(255, 255, 255, 0.4)',
          position: 'relative',
          zIndex: 1,
          transition: 'all 0.3s ease',
          transform: isExpanded ? 'scale(1.05)' : 'scale(1)'
        }}
        onClick={() => setIsExpanded(!isExpanded)}
      >
        {type.startsWith('math.') ? (
          <React.Fragment>
            {renderMathNodeContent()}
            {getResultValue()}
          </React.Fragment>
        ) : (
          <div style={{ 
            textAlign: 'center', 
            padding: '5px', 
            fontSize: '14px', 
            fontWeight: 'bold',
            textShadow: '0 1px 2px rgba(0,0,0,0.3)'
          }}>
            {data.label}
          </div>
        )}
      </div>
      
      {/* Enhanced Handles */}
      <Handle 
        type="target" 
        position={Position.Top} 
        style={{ 
          background: '#fff',
          border: '2px solid #555',
          width: '12px',
          height: '12px',
          top: '-6px'
        }} 
      />
      
      <Handle 
        type="source" 
        position={Position.Bottom} 
        style={{ 
          background: '#fff',
          border: '2px solid #555',
          width: '12px',
          height: '12px',
          bottom: '-6px'
        }} 
      />
      
      {/* Enhanced Properties Panel */}
      {isExpanded && (
        <div 
          className="properties-panel"
          style={{
            position: 'absolute',
            top: '100px',
            backgroundColor: 'white',
            border: `2px solid ${getNodeColor()}`,
            borderRadius: '12px',
            padding: '12px',
            width: '240px',
            zIndex: 10,
            boxShadow: '0 8px 16px rgba(0, 0, 0, 0.15)',
            animation: 'fadeIn 0.3s ease-in-out',
            overflow: 'hidden'
          }}
        >
          <div style={{ 
            fontWeight: 'bold', 
            marginBottom: '8px', 
            color: getNodeColor(),
            borderBottom: `2px solid ${getNodeColor()}`,
            paddingBottom: '4px'
          }}>Properties:</div>
          {Object.entries(data.properties).map(([key, value]) => (
            <div key={key} style={{ display: 'flex', marginBottom: '5px', flexWrap: 'wrap' }}>
              <span style={{ 
                fontWeight: 'bold', 
                marginRight: '5px', 
                color: '#333',
                minWidth: '100px'
              }}>{key}:</span>
              <span style={{ color: '#666' }}>{value}</span>
            </div>
          ))}
          
          {type.startsWith('math.') && (
            <div style={{ 
              marginTop: '12px', 
              padding: '8px', 
              backgroundColor: '#f8f8f8', 
              borderRadius: '8px',
              fontSize: '13px',
              boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontWeight: 'bold', color: getNodeColor(), marginBottom: '4px' }}>Operation:</div>
              <div style={{ fontSize: '14px' }}>{renderMathNodeContent()}</div>
              {data.properties.result && (
                <div style={{ 
                  marginTop: '8px',
                  padding: '4px 8px',
                  backgroundColor: 'rgba(0,0,0,0.05)',
                  borderRadius: '4px'
                }}>
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
