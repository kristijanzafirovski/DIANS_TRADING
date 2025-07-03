import React from 'react';
import ReactModal from 'react-modal';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import './Modal.css';

ReactModal.setAppElement('#root');

function Modal({ symbol, data, trend, onClose }) {
  return (
    <ReactModal isOpen={true} onRequestClose={onClose} className="modal" overlayClassName="overlay">
      <button className="close" onClick={onClose}>✕</button>
      <h2>{symbol} Detailed Analysis</h2>
      <div className="chart-container">
        <LineChart width={600} height={300} data={data}>
          <XAxis dataKey="timestamp" tickFormatter={ts => new Date(ts).toLocaleString()}/>
          <YAxis dataKey="price"/>
          <CartesianGrid strokeDasharray="3 3"/>
          <Tooltip labelFormatter={ts => new Date(ts).toLocaleString()}/>
          <Line type="monotone" dataKey="price" stroke="#0052cc" dot={false}/>
        </LineChart>
      </div>
      {trend && (
        <div className="trend-info">
          <p>Slope: {trend.slope.toFixed(6)}</p>
          <p>Intercept: {trend.intercept.toFixed(2)}</p>
        </div>
      )}
    </ReactModal>
  );
}

export default Modal;
