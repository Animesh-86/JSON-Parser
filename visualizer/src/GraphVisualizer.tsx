import React, { useMemo } from 'react';
import {
  ReactFlow,
  Controls,
  Background,
  type Edge,
  type Node,
  MarkerType,
  ConnectionLineType,
  Position,
  ReactFlowProvider,
  useReactFlow
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import dagre from 'dagre';
import CustomNode, { type CustomNodeData } from './CustomNode';

const nodeTypes = {
  customNode: CustomNode,
};

const COLORS = [
  '#00e6e6', // neon cyan
  '#cc00ff', // neon magenta
  '#00ff00', // neon green
  '#ff9900', // neon orange
  '#ffff00', // neon yellow
  '#ff0000', // red
];

const getLayoutedElements = (nodes: Node[], edges: Edge[]) => {
  const dagreGraph = new dagre.graphlib.Graph();
  dagreGraph.setDefaultEdgeLabel(() => ({}));
  
  // Rankdir LR for Left-to-Right layout
  dagreGraph.setGraph({ rankdir: 'LR', nodesep: 150, ranksep: 300 });

  nodes.forEach((node) => {
    // Better estimation of node height (Header ~35px + Each row ~35px)
    const propsCount = (node.data as any).properties?.length || 1;
    const estimatedHeight = 35 + (propsCount * 30);
    dagreGraph.setNode(node.id, { width: 280, height: estimatedHeight });
  });

  edges.forEach((edge) => {
    dagreGraph.setEdge(edge.source, edge.target);
  });

  dagre.layout(dagreGraph);

  dagreGraph.nodes().forEach((n) => {
    const nodeWithPosition = dagreGraph.node(n);
    const node = nodes.find((no) => no.id === n);
    if (node) {
      // Offset by half dimensions since dagre positions from center
      node.position = {
        x: nodeWithPosition.x - 140, // Half of 280 width
        y: nodeWithPosition.y - (nodeWithPosition.height / 2),
      };
      node.targetPosition = Position.Left;
      node.sourcePosition = Position.Right;
    }
  });

  return { nodes, edges };
};

interface GraphVisualizerProps {
  data: any; // Raw parsed JSON
  searchQuery?: string;
}

const GraphVisualizer: React.FC<GraphVisualizerProps> = ({ data, searchQuery = '' }) => {

  const { initialNodes, initialEdges } = useMemo(() => {
    const nodes: Node[] = [];
    const edges: Edge[] = [];
    let nodeIdCounter = 0;

    const createNodeId = () => `node_${nodeIdCounter++}`;
    const getColor = (depth: number) => COLORS[depth % COLORS.length];

    const traverse = (obj: any, name: string, parentId: string | null = null, sourceHandleId: string | null = null, depth: number = 0) => {
      if (obj === null || typeof obj !== 'object') {
        return; // primitives are handled inside their parent node
      }

      const id = createNodeId();
      const isArray = Array.isArray(obj);
      const nodeColor = getColor(depth);

      const properties: CustomNodeData['properties'] = [];

      const keys = isArray ? obj.map((_: any, i: number) => String(i)) : Object.keys(obj);

      keys.forEach((k: string) => {
        const val = obj[k as keyof typeof obj];
        const valType = val === null ? 'null' : Array.isArray(val) ? 'array' : typeof val;
        
        const hasChild = val !== null && typeof val === 'object';
        const propId = `${id}_prop_${k}`;

        let displayValue = '';
        if (hasChild) {
          displayValue = Array.isArray(val) ? `[Array(${val.length})]` : '{Object}';
        } else {
          displayValue = val === null ? 'null' : typeof val === 'string' ? `"${val}"` : String(val);
        }

        properties.push({
          key: k,
          value: displayValue,
          type: valType,
          id: propId,
          hasChild
        });

        if (hasChild) {
          // Recursive call for nested objects/arrays
          traverse(val, k, id, propId, depth + 1);
        }
      });

      // Check if node matches search query
      const searchLower = searchQuery.toLowerCase();
      let isHighlighted = false;
      if (searchLower) {
        if (name.toLowerCase().includes(searchLower)) isHighlighted = true;
        properties.forEach(p => {
          if (p.key.toLowerCase().includes(searchLower) || p.value.toLowerCase().includes(searchLower)) {
            isHighlighted = true;
          }
        });
      }

      nodes.push({
        id,
        type: 'customNode',
        position: { x: 0, y: 0 },
        data: {
          name: name,
          type: isArray ? 'array' : 'object',
          color: nodeColor,
          properties,
          isHighlighted
        },
      });

      if (parentId && sourceHandleId) {
        edges.push({
          id: `e_${parentId}-${id}`,
          source: parentId,
          target: id,
          sourceHandle: sourceHandleId,
          type: 'smoothstep',
          style: { stroke: nodeColor, strokeWidth: 1.5, strokeDasharray: '4 4' },
          markerEnd: {
            type: MarkerType.ArrowClosed,
            color: nodeColor,
          },
        });
      }
    };

    if (data !== null) {
      if (typeof data === 'object') {
        traverse(data, 'root', null, null, 0);
      } else {
        // If the root itself is a primitive (rare for valid JSON payload but possible)
        // Check if primitive root matches
        const searchLower = searchQuery.toLowerCase();
        const valueStr = String(data);
        const isHighlighted = searchLower ? (valueStr.toLowerCase().includes(searchLower) || 'root'.includes(searchLower)) : false;

        nodes.push({
          id: 'root',
          type: 'customNode',
          position: { x: 0, y: 0 },
          data: {
            name: 'root',
            type: typeof data,
            color: COLORS[0],
            properties: [{
              key: 'value',
              value: valueStr,
              type: typeof data,
              id: 'root_prop_value',
            }],
            isHighlighted
          }
        });
      }
    }

    return { initialNodes: nodes, initialEdges: edges };
  }, [data, searchQuery]);

  const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(initialNodes, initialEdges);

  return (
    <div style={{ width: '100%', height: '100%' }}>
      <ReactFlowProvider>
        <Flow layoutedNodes={layoutedNodes} layoutedEdges={layoutedEdges} />
      </ReactFlowProvider>
    </div>
  );
};

const Flow: React.FC<{ layoutedNodes: Node[], layoutedEdges: Edge[] }> = ({ layoutedNodes, layoutedEdges }) => {
  const { setCenter } = useReactFlow();

  const onNodeClick = (_: React.MouseEvent, node: Node) => {
    const w = node.measured?.width ?? 280;
    const h = node.measured?.height ?? 100;
    const x = node.position.x + w / 2;
    const y = node.position.y + h / 2;
    setCenter(x, y, { zoom: 1.2, duration: 800 });
  };

  return (
    <ReactFlow
      nodes={layoutedNodes}
      edges={layoutedEdges}
      nodeTypes={nodeTypes}
      onNodeClick={onNodeClick}
        fitView
        fitViewOptions={{ padding: 0.2, maxZoom: 1.2 }}
        minZoom={0.1}
        maxZoom={2}
        connectionLineType={ConnectionLineType.SmoothStep}
      >
        <Background color="#1a1a24" gap={25} size={1} />
        <Controls />
      </ReactFlow>
  );
};

export default GraphVisualizer;
