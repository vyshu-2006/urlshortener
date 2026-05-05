import React, { useState } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell } from 'recharts';
import { Activity, Smartphone, MapPin, Search } from 'lucide-react';

const COLORS = ['#58a6ff', '#3fb950', '#d29922', '#f85149'];

function App() {
  const [shortCode, setShortCode] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchAnalytics = async () => {
    if (!shortCode) return;
    
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8082/api/v1/analytics/${shortCode}`);
      if (!response.ok) {
        throw new Error('Analytics not found or rate limited');
      }
      const rawData = await response.json();

      // Transform generic output to recharts formats
      // Usually time data might have dates, we'll format it
      const formattedData = {
         timeSeries: rawData.timeSeries || [],
         deviceStats: rawData.deviceStats || [],
         geoStats: rawData.geoStats || []
      };

      setData(formattedData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-container">
      <header className="header">
        <h1 className="title">
          <Activity size={32} color="#58a6ff" style={{ verticalAlign: 'middle', marginRight: '10px'}}/>
          Analytics Engine
        </h1>
        <div className="search-bar">
          <input 
            type="text" 
            className="search-input" 
            placeholder="Enter short code (e.g. j9X)" 
            value={shortCode}
            onChange={(e) => setShortCode(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && fetchAnalytics()}
          />
          <button className="btn" onClick={fetchAnalytics}>
            <Search size={18} style={{ verticalAlign: 'middle' }}/> Search
          </button>
        </div>
      </header>

      {loading && <div className="loading">Fetching stream data...</div>}
      
      {error && <div className="error">Error: {error}</div>}

      {data && !loading && (
        <div className="grid-container">
          
          <div className="card" style={{ gridColumn: '1 / -1' }}>
            <h2 className="card-title">
              <Activity size={24} color="#58a6ff"/> 
              Clicks over Time
            </h2>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <AreaChart data={data.timeSeries}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#30363d" />
                  <XAxis dataKey="hour" stroke="#8b949e" tickFormatter={(tick) => {
                     const date = new Date(tick);
                     return `${date.getHours()}:00`;
                  }}/>
                  <YAxis stroke="#8b949e"/>
                  <RechartsTooltip 
                    contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#c9d1d9' }}
                    labelFormatter={(label) => new Date(label).toLocaleString()}
                  />
                  <Area type="monotone" dataKey="clicks" stroke="#58a6ff" fill="#58a6ff" fillOpacity={0.2} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="card">
            <h2 className="card-title">
              <Smartphone size={24} color="#3fb950"/>
              Device Breakdown
            </h2>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie
                    data={data.deviceStats}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="clicks"
                    nameKey="device"
                    label
                  >
                    {data.deviceStats.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <RechartsTooltip contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#c9d1d9' }}/>
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="card">
            <h2 className="card-title">
              <MapPin size={24} color="#d29922"/>
              Geographic Distribution
            </h2>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <BarChart data={data.geoStats}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#30363d" />
                  <XAxis dataKey="region" stroke="#8b949e"/>
                  <YAxis stroke="#8b949e"/>
                  <RechartsTooltip contentStyle={{ backgroundColor: '#161b22', borderColor: '#30363d', color: '#c9d1d9' }}/>
                  <Bar dataKey="clicks" fill="#d29922" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

        </div>
      )}
    </div>
  );
}

export default App;
