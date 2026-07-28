import React from 'react';
import { Handle, Position } from '@xyflow/react';
import './CustomNode.css';

export interface CustomNodeData {
  name: string;
  type: string;
  color: string;
  properties: Array<{
    key: string;
    value: string;
    type: string;
    id: string;
    hasChild?: boolean;
  }>;
  isHighlighted?: boolean;
}

const CustomNode: React.FC<{ data: CustomNodeData }> = ({ data }) => {
  return (
    <div 
      className={`custom-node ${data.isHighlighted ? 'highlighted' : ''}`} 
      style={{ 
        borderColor: data.color,
        '--node-accent-color': data.color
      } as React.CSSProperties}
    >
      <div 
        className="custom-node-header" 
        style={{ 
          borderBottomColor: `${data.color}55`,
          background: `linear-gradient(90deg, ${data.color}22 0%, rgba(13, 17, 23, 0.8) 100%)`
        }}
      >
        <span className="custom-node-title" style={{ color: data.color }}>
          <span className="node-indicator-dot" style={{ backgroundColor: data.color }}></span>
          {data.name}
        </span>
        <span className="custom-node-type" style={{ color: `${data.color}bb` }}>
          {data.type}
        </span>
      </div>
      
      <div className="custom-node-body">
        {/* Target handle for incoming edges to this node */}
        <Handle 
          type="target" 
          position={Position.Left} 
          style={{ top: 20, borderColor: data.color, backgroundColor: '#0d1117' }} 
        />

        {data.properties.map((prop) => (
          <div key={prop.key} className="custom-node-row">
            <div className="custom-node-key" title={prop.key}>
              {prop.key}
            </div>
            <div className={`custom-node-value ${prop.type}`} title={prop.value}>
              {prop.value}
            </div>
            {/* Source handle for each property that links to a child node */}
            {prop.hasChild && (
              <Handle
                type="source"
                position={Position.Right}
                id={prop.id}
                style={{ top: '50%', transform: 'translateY(-50%)', borderColor: data.color, backgroundColor: '#0d1117' }}
              />
            )}
          </div>
        ))}
        {data.properties.length === 0 && (
          <div className="custom-node-row" style={{ color: '#8b949e', fontStyle: 'italic' }}>
            Empty {data.type}
          </div>
        )}
      </div>
    </div>
  );
};

export default CustomNode;
