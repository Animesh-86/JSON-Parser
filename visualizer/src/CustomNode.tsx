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
    <div className={`custom-node ${data.isHighlighted ? 'highlighted' : ''}`} style={{ borderColor: data.color }}>
      <div className="custom-node-header" style={{ borderBottomColor: data.color, backgroundColor: 'rgba(0,0,0,0.4)' }}>
        <span style={{ color: data.color }}>{data.name}</span>
        <span className="custom-node-type" style={{ color: 'rgba(255,255,255,0.5)' }}>{data.type}</span>
      </div>
      
      <div className="custom-node-body">
        {/* Target handle for incoming edges to this node */}
        <Handle type="target" position={Position.Left} style={{ top: 15 }} />

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
                style={{ top: '50%', transform: 'translateY(-50%)', borderColor: data.color }}
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
