import React from 'react';
import { LineChart, Line, ResponsiveContainer } from 'recharts';
import './TickerCard.css';

function TickerCard({ symbol, data, trend, onClick }) {
  const current = data[data.length - 1] || { price: 0 };
  const prev = data[data.length - 2] || current;
  const change = ((current.price - prev.price) / prev.price * 100).toFixed(2);
  const sma = data.length ? (data.slice(-5).reduce((a,b)=>a+b.price,0)/Math.min(5,data.length)).toFixed(2) : '—';
  const rsi = (() => {
    if (data.length < 15) return '—';
    let gains=0, losses=0;
    for (let i = data.length-14; i < data.length; i++) {
      const diff = data[i].price - data[i-1].price;
      if (diff>0) gains+=diff; else losses-=diff;
    }
    const rs = gains/14 / (losses/14||1);
    return (100 - 100/(1+rs)).toFixed(0);
  })();

  return (
    <div className="card" onClick={onClick}>
      <h3>{symbol}</h3>
      <p>${current.price.toFixed(2)} ({change}%)</p>
      <p>SMA5: {sma} | RSI14: {rsi}</p>
      <ResponsiveContainer width="100%" height={50}>
        <LineChart data={data}>
          <Line type="monotone" dataKey="price" stroke="#0052cc" dot={false} strokeWidth={2}/>
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TickerCard;
