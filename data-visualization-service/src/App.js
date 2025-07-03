import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';

// Environment-based back-end URLs (fallback to localhost for dev)
const STORAGE_URL  = process.env.REACT_APP_STORAGE_URL  || 'http://localhost:8000';
const ANALYSIS_URL = process.env.REACT_APP_ANALYSIS_URL || 'http://localhost:8001';

function App() {
  const [data, setData] = useState([]);
  const [analysis, setAnalysis] = useState({});

  // Fetch stored trades
  useEffect(() => {
    axios.get(`${STORAGE_URL}/data`)
        .then(response => {
          setData(response.data);
        })
        .catch(err => console.error('Error fetching data:', err));
  }, []);

  // For each distinct symbol, fetch analysis (trend)
  useEffect(() => {
    const symbols = Array.from(new Set(data.map(item => item.symbol)));
    symbols.forEach(symbol => {
      axios.get(`${ANALYSIS_URL}/analysis/trend/${symbol}`)
          .then(response => {
            setAnalysis(prev => ({
              ...prev,
              [symbol]: response.data
            }));
          })
          .catch(err => console.error(`Error fetching analysis for ${symbol}:`, err));
    });
  }, [data]);

  return (
      <div className="App">
        <header className="App-header">
          <h1>Trading Dashboard</h1>
        </header>
        <main>
          {data.length === 0 && <p>Loading trade data...</p>}
          {data.map((trade, idx) => {
            const { symbol, price } = trade;
            const stats = analysis[symbol] || {};
            return (
                <div key={`${symbol}-${idx}`} className="card">
                  <h2>{symbol}</h2>
                  <p>Price: ${price.toFixed(2)}</p>
                  <p>SMA5: {stats.sma5 ?? '—'} | RSI14: {stats.rsi14 ?? '—'}</p>
                </div>
            );
          })}
        </main>
      </div>
  );
}

export default App;
