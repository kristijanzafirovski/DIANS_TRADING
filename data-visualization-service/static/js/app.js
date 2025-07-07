import 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js';

const STORAGE_URL  = 'http://localhost:8000';
const ANALYSIS_URL = 'http://localhost:8001';

const searchBtn     = document.getElementById('searchBtn');
const tickerInput   = document.getElementById('tickerInput');
const cardsContainer = document.getElementById('cardsContainer');
const modal         = new bootstrap.Modal(document.getElementById('detailsModal'));

searchBtn.addEventListener('click', () => {
  const ticker = tickerInput.value.trim().toUpperCase();
  if (!ticker) return;
  fetchData(ticker);
});

async function fetchData(ticker) {
  // disable & show spinner
  searchBtn.disabled = true;
  searchBtn.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Pulling data for ${ticker}&hellip;
  `;

  let data;
  // poll storage until we get 200 (or bail on error)
  while (true) {
    const res = await fetch(`${STORAGE_URL}/symbols/${ticker}`);
    if (res.status === 202) {
      // ingestion kicked off, wait and retry
      await new Promise(r => setTimeout(r, 2000));
      continue;
    }
    if (res.status === 200) {
      data = await res.json();
      break;
    }
    console.error('Storage error', res.status);
    restoreButton();
    return;
  }

  // now fetch analysis signals
  let signals = [];
  const sigRes = await fetch(`${ANALYSIS_URL}/symbol/${ticker}`);
  if (sigRes.ok) signals = await sigRes.json();

  restoreButton();
  renderCard(ticker, data, signals);
  renderModal(ticker, data, signals);
}

function restoreButton() {
  searchBtn.disabled = false;
  searchBtn.textContent = 'Search';
}

function renderCard(ticker, data, signals) {
  cardsContainer.innerHTML = '';
  const latest       = data[data.length - 1] || {};
  const latestSignal = signals[signals.length - 1]?.signal || 'HOLD';
  const colorClass   = latestSignal === 'BUY'  ? 'bg-success'
      : latestSignal === 'SELL' ? 'bg-danger'
          :                            'bg-secondary';

  cardsContainer.innerHTML = `
    <div class="col-md-4">
      <div class="card position-relative">
        <div class="card-body">
          <h5 class="card-title">${ticker}</h5>
          <p class="card-text">Price: $${(latest.close||0).toFixed(2)}</p>
        </div>
        <span class="card-signal ${colorClass} text-white">${latestSignal}</span>
      </div>
    </div>`;
}

function renderModal(ticker, data, signals) {
  document.getElementById('modalTicker').textContent = ticker;

  document.getElementById('trendInfo').innerHTML = `
    <p><strong>Short MA:</strong> ${signals.at(-1)?.shortMa.toFixed(2)}</p>
    <p><strong>Long MA:</strong> ${signals.at(-1)?.longMa.toFixed(2)}</p>
    <p><strong>Signal:</strong> ${signals.at(-1)?.signal}</p>
  `;

  document.getElementById('charts').innerHTML = `
    <div class="col-md-6"><div id="maChart"></div></div>
    <div class="col-md-6"><div id="rsiChart"></div></div>`;

  const dates  = data.map(d => new Date(d.timestamp));
  const closes = data.map(d => d.close);

  // MA chart
  Plotly.newPlot('maChart', [
    { x: dates, y: closes, mode: 'lines', name: 'Close' },
    { x: dates, y: movingAverage(closes, 5), mode: 'lines', name: 'MA5' }
  ], { title: 'Moving Averages' });

  // RSI chart (example)
  const rsi = data.map((_, i) => 50 + 10 * Math.sin(i/5));
  Plotly.newPlot('rsiChart', [
    { x: dates, y: rsi, mode: 'lines', name: 'RSI' }
  ], { title: 'RSI (14)' });

  modal.show();
}

function movingAverage(arr, window) {
  return arr.map((_, i) => {
    const start = Math.max(0, i - window + 1);
    const slice = arr.slice(start, i + 1);
    return slice.reduce((a,b) => a + b, 0) / slice.length;
  });
}
