const DEFAULT_TICKERS = ['GOOGL','TSLA','AAPL'];
const STORAGE_URL  = 'http://zafirovski.ddns.net:8000';
const ANALYSIS_URL = 'http://zafirovski.ddns.net:8001';

const overlay = document.createElement('div');
overlay.id = 'loading-overlay';
overlay.style.cssText = `
  position:fixed;top:0;left:0;
  width:100%;height:100%;
  background:rgba(0,0,0,0.5);
  display:flex;align-items:center;
  justify-content:center;
  flex-direction:column;
  z-index:9999;
  color:#fff;font-size:1.5rem;
  visibility:hidden;
`;
overlay.innerHTML = `
  <div class="spinner-border" role="status"></div>
  <div id="overlay-text" style="margin-top:1rem;"></div>
`;
document.body.appendChild(overlay);

function showOverlay(text) {
  document.getElementById('overlay-text').innerText = text;
  overlay.style.visibility = 'visible';
}
function updateOverlay(text) {
  document.getElementById('overlay-text').innerText = text;
}
function hideOverlay() {
  overlay.style.visibility = 'hidden';
}

function renderCard(ticker, latestBar, latestSignal, container) {
  const colorClass = latestSignal === 'BUY'
      ? 'bg-success'
      : latestSignal === 'SELL'
          ? 'bg-danger'
          : 'bg-secondary';
  const html = `
    <div class="card position-relative">
      <div class="card-body">
        <h5 class="card-title">${ticker}</h5>
        <p class="card-text">Price: $${latestBar.close.toFixed(2)}</p>
      </div>
      <span class="card-signal ${colorClass}">${latestSignal}</span>
    </div>
  `;
  container.insertAdjacentHTML('beforeend', html);
}

async function fetchData(ticker) {
  try {
    showOverlay('Loading data');
    let barsRes;
    while (true) {
      barsRes = await fetch(`${STORAGE_URL}/symbols/${ticker}`);
      if (barsRes.status === 202) {
        await new Promise(r => setTimeout(r, 2000));
        continue;
      }
      if (barsRes.status === 200) break;
      throw new Error(`Storage error: ${barsRes.status}`);
    }
    const bars = await barsRes.json();
    updateOverlay('Analyzing data');
    const sigRes = await fetch(`${ANALYSIS_URL}/symbol/${ticker}`);
    const signals = await sigRes.json();
    hideOverlay();

    const recentDiv = document.getElementById('recent-container');
    const btn = document.createElement('button');
    btn.className = 'btn btn-outline-secondary btn-sm';
    btn.textContent = ticker;
    btn.addEventListener('click', () => showDetail(ticker, bars, signals));
    recentDiv.appendChild(btn);

    const latestBar    = bars[bars.length - 1];
    const latestSignal = signals[signals.length - 1]?.signal || 'HOLD';
    renderCard(ticker, latestBar, latestSignal, document.getElementById('cards-container'));

  } catch (err) {
    console.error(err);
    alert(`Failed to load data for ${ticker}: ${err.message}`);
    hideOverlay();
  }
}

function showDetail(ticker, bars = null, signals = null) {
  Promise.resolve(
      bars ? bars : fetch(`${STORAGE_URL}/symbols/${ticker}`).then(r => r.json())
  )
      .then(fullBars => {
        bars = fullBars;
        return signals
            ? signals
            : fetch(`${ANALYSIS_URL}/symbol/${ticker}`).then(r => r.json());
      })
      .then(fullSignals => {
        signals = fullSignals;

        document.querySelector('#detailModal .modal-dialog')
            .classList.add('modal-fullscreen');

        const chartsDiv = document.getElementById('charts');
        chartsDiv.innerHTML = '';
        document.getElementById('modalTitle').innerText = ticker;
        new bootstrap.Modal(document.getElementById('detailModal')).show();

        const dates   = bars.map(b => new Date(b.timestamp));
        const opens   = bars.map(b => b.open);
        const highs   = bars.map(b => b.high);
        const lows    = bars.map(b => b.low);
        const closes  = bars.map(b => b.close);
        const volumes = bars.map(b => b.volume);

        const latest   = bars[bars.length-1];
        const { open: openVal, high: highVal, low: lowVal, close: closeVal, volume } = latest;
        const prevClose = bars[bars.length-2]?.close ?? openVal;
        const changePct = ((closeVal - prevClose)/prevClose*100).toFixed(2);

        const H       = 200, W = 40, tickLen = 20, margin = 10;
        const topPad  = 30, bottomPad = 20;
        const H2      = H + topPad + bottomPad;
        const range   = (highVal - lowVal) || 1;
        const scale   = (H - 2*margin) / range;
        const x0      = W/2;
        const yHigh2  = topPad;
        const yLow2   = topPad + (highVal - lowVal)*scale;
        const yOpen2  = topPad + (highVal - openVal)*scale;
        const yClose2 = topPad + (highVal - closeVal)*scale;
        const rectY   = Math.min(yOpen2, yClose2);
        const rectH   = Math.abs(yOpen2 - yClose2);
        const color   = closeVal >= openVal ? '#28a745' : '#dc3545';
        const fontSize= 14;

        const svg = `
  <svg width="${W + tickLen + 120}" height="${H2}" overflow="visible">
    <line x1="${x0}" y1="${yHigh2}" x2="${x0}" y2="${yLow2}"
          stroke="${color}" stroke-width="4"/>
    <rect x="${x0 - W/2}" y="${rectY}" width="${W}" height="${rectH}"
          fill="${color}" stroke="${color}" stroke-width="2"/>
    <g font-size="${fontSize}" font-family="Arial, sans-serif" text-anchor="middle">
      <text x="${x0}" y="${yHigh2 - 5}">high: ${highVal}</text>
      <text x="${x0}" y="${yLow2 + fontSize + 5}">low: ${lowVal}</text>
      <line x1="${x0}" x2="${x0+tickLen}" y1="${yOpen2}" y2="${yOpen2}"
            stroke="black" stroke-width="1"/>
      <text text-anchor="start" x="${x0+tickLen+5}" y="${yOpen2 + fontSize/2}">
        open: ${openVal}
      </text>
      <line x1="${x0}" x2="${x0+tickLen}" y1="${yClose2}" y2="${yClose2}"
            stroke="black" stroke-width="1"/>
      <text text-anchor="start" x="${x0+tickLen+5}" y="${yClose2 + fontSize/2}">
        close: ${closeVal}
      </text>
    </g>
  </svg>
`;

        const statsTable = `
      <table class="table table-sm table-borderless mb-0" style="font-size:1rem;">
        <tbody>
          <tr><th>Open</th><td>$${openVal.toFixed(2)}</td></tr>
          <tr><th>Close</th><td>$${closeVal.toFixed(2)}</td></tr>
          <tr><th>High</th><td>$${highVal.toFixed(2)}</td></tr>
          <tr><th>Low</th><td>$${lowVal.toFixed(2)}</td></tr>
          <tr><th>Volume</th><td>${volume.toLocaleString()}</td></tr>
          <tr><th>Change %</th><td>${changePct}%</td></tr>
        </tbody>
      </table>
    `;

          const currentSignal = signals[signals.length - 1]?.signal || 'HOLD';
          const signalClass = currentSignal === 'BUY'
              ? 'text-success'
              : currentSignal === 'SELL'
                  ? 'text-danger'
                  : 'text-secondary';

          chartsDiv.insertAdjacentHTML('afterbegin', `
  <div class="col-12 text-center mb-3">
    <strong>Current signal for ${ticker}: </strong>
    <span class="${signalClass}" style="font-size:1.25rem;">
      ${currentSignal}
    </span>
  </div>
`);

        chartsDiv.insertAdjacentHTML('afterbegin', `
      <div class="col-12 mb-4 d-flex justify-content-center align-items-start" style="gap:2rem;">
        <div>${svg}</div>
        <div>${statsTable}</div>
      </div>
    `);





        let cumPV=0, cumV=0;
        const vwap = bars.map(b => {
          cumPV += b.close * b.volume;
          cumV  += b.volume;
          return cumPV / cumV;
        });
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-12"><div id="candleChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('candleChart', [
          { x: dates, open: opens, high: highs, low: lows, close: closes, type:'candlestick', name:'Price' },
          { x: dates, y: vwap, type:'scatter', mode:'lines', name:'VWAP' }
        ], { title:'Price & VWAP', xaxis:{rangeslider:{visible:false}} }, { responsive:true });
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-12"><div id="volumeChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('volumeChart', [{
          x: dates, y: volumes, type:'bar', name:'Volume'
        }], { title:'Volume' }, { responsive:true });

        function sma(arr, p) {
          return arr.map((_,i,a) => i<p-1?null: a.slice(i+1-p,i+1).reduce((s,v)=>s+v,0)/p);
        }
        function std(arr,p,ma) {
          return arr.map((_,i,a) => {
            if(i<p-1) return null;
            const slice = a.slice(i+1-p,i+1), mean=ma[i];
            return Math.sqrt(slice.reduce((s,v)=>s+(v-mean)**2,0)/p);
          });
        }
        const sma20 = sma(closes,20), sd20 = std(closes,20,sma20);
        const upper = sma20.map((m,i)=> m===null?null: m+2*sd20[i]);
        const lower = sma20.map((m,i)=> m===null?null: m-2*sd20[i]);
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-md-6"><div id="bbChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('bbChart', [
          { x: dates, y: closes, mode:'lines', name:'Close' },
          { x: dates, y: sma20, mode:'lines', name:'SMA20' },
          { x: dates, y: upper, mode:'lines', name:'Upper BB' },
          { x: dates, y: lower, mode:'lines', name:'Lower BB' }
        ], { title:'Bollinger Bands' }, { responsive:true });
        const calcEMA = (data, p) => {
          const k = 2/(p+1), emaArr = [];
          data.forEach((v,i,a) => {
            if(i===p-1) emaArr[i] = a.slice(0,p).reduce((s,x)=>s+ x,0)/p;
            else if(i>=p) emaArr[i] = v*k + emaArr[i-1]*(1-k);
            else emaArr[i] = null;
          });
          return emaArr;
        };
        const ema12 = calcEMA(closes,12), ema26 = calcEMA(closes,26);
        const macd  = closes.map((_,i)=> ema12[i]!=null&&ema26[i]!=null?ema12[i]-ema26[i]:null);
        const sig   = calcEMA(macd.filter(v=>v!=null),9);
        const signalArr = macd.map((_,i)=> i<26+8?null:sig[i-(26-1)]);
        const hist = macd.map((v,i)=> v!=null&&signalArr[i]!=null?v-signalArr[i]:null);
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-md-6"><div id="macdChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('macdChart', [
          { x: dates, y: macd, mode:'lines', name:'MACD' },
          { x: dates, y: signalArr, mode:'lines', name:'Signal' },
          { x: dates, y: hist, type:'bar', name:'Hist' }
        ], { title:'MACD (12,26,9)' }, { responsive:true });
        const obv = [0];
        for(let i=1; i<closes.length; i++){
          obv[i] = obv[i-1] + (closes[i]>closes[i-1]? volumes[i] : -volumes[i]);
        }
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-md-6"><div id="obvChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('obvChart', [
          { x: dates, y: obv, mode:'lines', name:'OBV' }
        ], { title:'On-Balance Volume' }, { responsive:true });
        const tr = [];
        for(let i=1; i<bars.length; i++){
          tr.push(Math.max(
              highs[i]-lows[i],
              Math.abs(highs[i]-closes[i-1]),
              Math.abs(lows[i]-closes[i-1])
          ));
        }
        const atrArr = sma(tr,14);
        chartsDiv.insertAdjacentHTML('beforeend', `
      <div class="col-md-6"><div id="atrChart" class="chart-container"></div></div>
    `);
        Plotly.newPlot('atrChart', [
          { x: dates.slice(1), y: atrArr, mode:'lines', name:'ATR' }
        ], { title:'Average True Range' }, { responsive:true });
      });
}
const modalEl = document.getElementById('detailModal');
modalEl.addEventListener('shown.bs.modal', () => {
    // either dispatch a window resize…
    window.dispatchEvent(new Event('resize'));
    // …or explicitly call Plotly resize on each chart:
    ['candleChart','maChart','rsiChart','volumeChart','bbChart',
        'macdChart','obvChart','atrChart'].forEach(id => {
        const gd = document.getElementById(id);
        if (gd) Plotly.Plots.resize(gd);
    });
});


document.getElementById('search-btn').addEventListener('click', () => {
  const t = document.getElementById('ticker-input').value.trim().toUpperCase();
  if (t) fetchData(t);
});

window.addEventListener('DOMContentLoaded', () => {
  DEFAULT_TICKERS.forEach(fetchData);
});
