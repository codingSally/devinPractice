import React from 'react';

const ProcessLegend: React.FC = () => {
  return (
    <div className="process-legend mt-6 bg-white p-4 rounded-lg shadow-sm border border-gray-100">
      <h3 className="text-lg font-semibold mb-3 text-gray-800 border-b pb-2">Process Legend</h3>
      
      <div className="legend-items text-sm">
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div 
            className="w-5 h-5 mr-3 rounded-full shadow-sm" 
            style={{ backgroundColor: '#4CAF50' }}
          ></div>
          <span className="font-medium">Logging Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div 
            className="w-5 h-5 mr-3 rounded-full shadow-sm" 
            style={{ backgroundColor: '#2196F3' }}
          ></div>
          <span className="font-medium">HTTP Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div 
            className="w-5 h-5 mr-3 rounded-full shadow-sm" 
            style={{ backgroundColor: '#FF9800' }}
          ></div>
          <span className="font-medium">Script Node</span>
        </div>
        
        <div className="legend-item flex items-center mb-4 hover:bg-gray-50 p-1 rounded transition-colors">
          <div 
            className="w-5 h-5 mr-3 rounded-full shadow-sm" 
            style={{ backgroundColor: '#9C27B0' }}
          ></div>
          <span className="font-medium">Conditional Node</span>
        </div>
        
        {/* Enhanced Math Nodes Section */}
        <div className="legend-section mt-4 mb-4 bg-gray-50 p-3 rounded-lg border border-gray-100">
          <h4 className="font-semibold mb-3 text-gray-700 border-b pb-1">Math Nodes</h4>
          <div className="grid grid-cols-2 gap-3">
            <div className="legend-item flex items-center p-1 hover:bg-gray-100 rounded transition-colors">
              <div 
                className="w-5 h-5 mr-3 rounded-full shadow-sm" 
                style={{ backgroundColor: '#E91E63' }}
              ></div>
              <span className="font-medium">Addition (+)</span>
            </div>
            <div className="legend-item flex items-center p-1 hover:bg-gray-100 rounded transition-colors">
              <div 
                className="w-5 h-5 mr-3 rounded-full shadow-sm" 
                style={{ backgroundColor: '#673AB7' }}
              ></div>
              <span className="font-medium">Subtraction (-)</span>
            </div>
            <div className="legend-item flex items-center p-1 hover:bg-gray-100 rounded transition-colors">
              <div 
                className="w-5 h-5 mr-3 rounded-full shadow-sm" 
                style={{ backgroundColor: '#00BCD4' }}
              ></div>
              <span className="font-medium">Multiplication (×)</span>
            </div>
            <div className="legend-item flex items-center p-1 hover:bg-gray-100 rounded transition-colors">
              <div 
                className="w-5 h-5 mr-3 rounded-full shadow-sm" 
                style={{ backgroundColor: '#FF5722' }}
              ></div>
              <span className="font-medium">Division (÷)</span>
            </div>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div className="flex items-center">
            <div 
              className="w-10 h-1 mr-3 rounded" 
              style={{ backgroundColor: '#555' }}
            ></div>
            <span className="font-medium">Sequential Connection</span>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div className="flex items-center">
            <div 
              className="w-10 h-1 mr-3 rounded" 
              style={{ backgroundColor: '#2979FF', borderTop: '1px dashed #2979FF' }}
            ></div>
            <span className="font-medium">Parallel Connection</span>
          </div>
        </div>
        
        <div className="legend-item flex items-center mb-3 hover:bg-gray-50 p-1 rounded transition-colors">
          <div className="flex items-center">
            <div className="w-4 h-4 rounded-full mr-3 bg-gray-500 shadow-sm"></div>
            <span className="font-medium">Level Indicator</span>
          </div>
        </div>
      </div>
      
      <div className="execution-info mt-5 text-xs text-gray-600 bg-blue-50 p-3 rounded-lg border border-blue-100">
        <p className="mb-2 font-medium text-blue-800">Execution Flow:</p>
        <p className="mb-1 flex items-center"><span className="w-1 h-1 bg-blue-500 rounded-full mr-2"></span> Nodes at the same level execute in parallel</p>
        <p className="mb-1 flex items-center"><span className="w-1 h-1 bg-blue-500 rounded-full mr-2"></span> Execution proceeds to the next level only when all nodes at the current level complete</p>
        <p className="mb-1 flex items-center"><span className="w-1 h-1 bg-blue-500 rounded-full mr-2"></span> Conditional nodes can branch the execution flow</p>
        <p className="mb-1 flex items-center"><span className="w-1 h-1 bg-blue-500 rounded-full mr-2"></span> Math nodes show operation values and results</p>
        <p className="flex items-center"><span className="w-1 h-1 bg-blue-500 rounded-full mr-2"></span> Click on nodes to view/edit their properties</p>
      </div>
    </div>
  );
};

export default ProcessLegend;
