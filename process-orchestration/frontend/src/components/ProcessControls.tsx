import React from 'react';

interface ProcessControlsProps {
  onZoomIn: () => void;
  onZoomOut: () => void;
  onFitView: () => void;
  onClear: () => void;
}

const ProcessControls: React.FC<ProcessControlsProps> = ({
  onZoomIn,
  onZoomOut,
  onFitView,
  onClear
}) => {
  return (
    <div className="process-controls absolute top-4 right-4 bg-white rounded-md shadow-md p-2 flex flex-col">
      <button
        className="control-button mb-2 w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-md"
        onClick={onZoomIn}
        title="Zoom In"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          <line x1="11" y1="8" x2="11" y2="14"></line>
          <line x1="8" y1="11" x2="14" y2="11"></line>
        </svg>
      </button>
      
      <button
        className="control-button mb-2 w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-md"
        onClick={onZoomOut}
        title="Zoom Out"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          <line x1="8" y1="11" x2="14" y2="11"></line>
        </svg>
      </button>
      
      <button
        className="control-button mb-2 w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-md"
        onClick={onFitView}
        title="Fit View"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"></path>
        </svg>
      </button>
      
      <button
        className="control-button w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-md"
        onClick={onClear}
        title="Clear Canvas"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
        </svg>
      </button>
    </div>
  );
};

export default ProcessControls;
