import { useState, useEffect } from 'react';
import { Box, Code, Search, Download, Server, RefreshCw } from 'lucide-react';
import { toPng } from 'html-to-image';
import GraphVisualizer from './GraphVisualizer';
import './App.css';

const JAVA_API_URL = 'https://json-parser-api.onrender.com/api/parse';

function App() {
  const [jsonText, setJsonText] = useState('{\n  // JSON5 comments supported by Java Engine!\n  project: "JSON Parser",\n  version: 1.0,\n  features: ["JSON5", "Zero-Alloc", "Visualizer"],\n  active: true,\n  nested: {\n    speed: "blazing fast",\n    types: [1, 2, 3]\n  }\n}');
  const [parsedData, setParsedData] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleExport = () => {
    const el = document.querySelector('.react-flow__viewport') as HTMLElement;
    if (el) {
      toPng(el, { backgroundColor: '#1a1a24' })
        .then((dataUrl) => {
          const a = document.createElement('a');
          a.setAttribute('download', 'json-graph.png');
          a.setAttribute('href', dataUrl);
          a.click();
        })
        .catch((err) => {
          console.error('Failed to export image', err);
        });
    }
  };

  // Exclusively parsed via Java JSON Parser Engine on Render
  useEffect(() => {
    if (!jsonText.trim()) {
      setParsedData(null);
      setError(null);
      return;
    }

    const timer = setTimeout(() => {
      setIsLoading(true);
      fetch(JAVA_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: jsonText,
      })
        .then(async (res) => {
          const data = await res.json();
          if (!res.ok) {
            throw new Error(data.error || 'Parse Error');
          }
          setParsedData(data);
          setError(null);
        })
        .catch((err) => {
          setError(err.message || 'Error connecting to Java Parser Backend');
        })
        .finally(() => {
          setIsLoading(false);
        });
    }, 300);

    return () => clearTimeout(timer);
  }, [jsonText]);

  return (
    <div className="app-container">
      <div className="header">
        <div className="header-title">
          <Box size={24} className="header-icon" />
          JSON Node Graph Visualizer
        </div>

        {/* Java Engine Badge */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: '#0d1117', padding: '6px 12px', borderRadius: '6px', border: '1px solid #30363d' }}>
          <Server size={14} style={{ color: '#238636' }} />
          <span style={{ fontSize: '0.8rem', color: '#c9d1d9', fontWeight: 600 }}>
            Powered by <span style={{ color: '#3fb950' }}>Java Parser Engine</span>
          </span>
        </div>

        <div className="legend" style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
          <div style={{ position: 'relative' }}>
            <Search size={14} style={{ position: 'absolute', left: 8, top: '50%', transform: 'translateY(-50%)', color: '#8b949e' }} />
            <input
              type="text"
              placeholder="Search graph..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{
                backgroundColor: '#0d1117',
                border: '1px solid #30363d',
                borderRadius: '4px',
                padding: '4px 8px 4px 28px',
                color: '#c9d1d9',
                fontSize: '0.8rem',
                outline: 'none',
                width: '180px'
              }}
            />
          </div>
          <button onClick={handleExport} style={{
            display: 'flex', alignItems: 'center', gap: '6px',
            backgroundColor: '#238636', color: '#fff', border: 'none',
            padding: '6px 12px', borderRadius: '4px', cursor: 'pointer',
            fontSize: '0.8rem', fontWeight: 600
          }}>
            <Download size={14} /> Export PNG
          </button>
        </div>
      </div>

      <div className="main-content">
        <div className="pane editor-pane" style={{ maxWidth: '400px' }}>
          <div className="pane-header">
            <div className="header-title" style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Code size={16} /> Input JSON / JSON5
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.75rem', color: '#3fb950' }}>
              {isLoading ? <RefreshCw size={12} className="spin" /> : <span style={{ width: 8, height: 8, borderRadius: '50%', backgroundColor: '#3fb950', display: 'inline-block' }}></span>}
              Java Lexer & Parser
            </div>
          </div>
          <textarea
            className="json-textarea"
            value={jsonText}
            onChange={(e) => setJsonText(e.target.value)}
            spellCheck="false"
            placeholder="Paste your JSON or JSON5 here..."
          />
        </div>

        <div className="pane visualizer-pane">
          {error && (
            <div className="error-message">
              <strong>Java Parser Error:</strong> {error}
            </div>
          )}
          {!error && parsedData !== null && (
            <GraphVisualizer data={parsedData} searchQuery={searchQuery} />
          )}
          {!error && parsedData === null && (
            <div style={{ padding: '2rem', color: 'var(--text-secondary)' }}>
              Enter JSON to visualize via Java Engine
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
