import React from 'react';

const ProcessLegend: React.FC = () => {
  return (
    <div className="process-legend mt-6">
      <h3 className="text-lg font-semibold mb-2">Process Legend</h3>
      
      <div className="legend-items text-sm">
        <div className="legend-item flex items-center mb-2">
          <div 
            className="w-4 h-4 mr-2 rounded-full" 
            style={{ backgroundColor: '#4CAF50' }}
          ></div>
          <span>Logging Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-2">
          <div 
            className="w-4 h-4 mr-2 rounded-full" 
            style={{ backgroundColor: '#2196F3' }}
          ></div>
          <span>HTTP Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-2">
          <div 
            className="w-4 h-4 mr-2 rounded-full" 
            style={{ backgroundColor: '#FF9800' }}
          ></div>
          <span>Script Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-4">
          <div 
            className="w-4 h-4 mr-2 rounded-full" 
            style={{ backgroundColor: '#9C27B0' }}
          ></div>
          <span>Conditional Node</span>
        </div>
        
        {/* Math Nodes Section */}
        <div className="legend-section mt-4 mb-4">
          <h4 className="font-semibold mb-2">Math Nodes</h4>
          <div className="grid grid-cols-2 gap-2">
            <div className="legend-item flex items-center">
              <div 
                className="w-4 h-4 mr-2 rounded-full" 
                style={{ backgroundColor: '#E91E63' }}
              ></div>
              <span>Addition (+)</span>
            </div>
            <div className="legend-item flex items-center">
              <div 
                className="w-4 h-4 mr-2 rounded-full" 
                style={{ backgroundColor: '#673AB7' }}
              ></div>
              <span>Subtraction (-)</span>
            </div>
            <div className="legend-item flex items-center">
              <div 
                className="w-4 h-4 mr-2 rounded-full" 
                style={{ backgroundColor: '#00BCD4' }}
              ></div>
              <span>Multiplication (×)</span>
            </div>
            <div className="legend-item flex items-center">
              <div 
                className="w-4 h-4 mr-2 rounded-full" 
                style={{ backgroundColor: '#FF5722' }}
              ></div>
              <span>Division (÷)</span>
            </div>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-2">
          <div className="flex items-center">
            <div 
              className="w-8 h-0.5 mr-2" 
              style={{ backgroundColor: '#555' }}
            ></div>
            <span>Sequential Connection</span>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-2">
          <div className="flex items-center">
            <div 
              className="w-8 h-0.5 mr-2" 
              style={{ backgroundColor: '#2979FF', borderTop: '1px dashed #2979FF' }}
            ></div>
            <span>Parallel Connection</span>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-2">
          <div className="flex items-center">
            <div className="w-3 h-3 rounded-full mr-2 bg-gray-500"></div>
            <span>Level Indicator</span>
          </div>
        </div>
      </div>
      
      <div className="execution-info mt-4 text-xs text-gray-600">
        <p className="mb-1">• Nodes at the same level execute in parallel</p>
        <p className="mb-1">• Execution proceeds to the next level only when all nodes at the current level complete</p>
        <p className="mb-1">• Conditional nodes can branch the execution flow</p>
        <p className="mb-1">• Math nodes show operation values and results</p>
        <p>• Click on nodes to view/edit their properties</p>
      </div>
    </div>
  );
};

export default ProcessLegend;
