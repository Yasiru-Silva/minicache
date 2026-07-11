import { useState, useEffect } from "react";
import { fetchStats, fetchKeys } from "./services/api";
import StatusBadge from "./components/StatusBadge";
import StatsCard from "./components/StatsCard";
import KeysTable from "./components/KeysTable";

export default function App() {
  const [stats, setStats] = useState(null);
  const [keys, setKeys] = useState([]);
  const [online, setOnline] = useState(false);

  async function refresh() {
    try {
      const [statsData, keysData] = await Promise.all([
        fetchStats(),
        fetchKeys(),
      ]);
      setStats(statsData);
      setKeys(keysData);
      setOnline(true);
    } catch (error) {
      setOnline(false);
    }
  }

  // Poll every 2 seconds
  useEffect(() => {
    refresh();
    const interval = setInterval(refresh, 2000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-gray-900 text-white p-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold">MiniCache</h1>
          <p className="text-gray-400 mt-1">In-memory key-value store dashboard</p>
        </div>
        <StatusBadge online={online} />
      </div>

      {/* Stats */}
      {stats && (
        <div className="mb-6">
          <StatsCard
            keyCount={stats.keyCount}
            uptimeSeconds={stats.uptimeSeconds}
          />
        </div>
      )}

      {/* Keys Table */}
      <div>
        <h2 className="text-lg font-semibold text-gray-300 mb-3">Live Keys</h2>
        <KeysTable keys={keys} />
      </div>

      {/* Polling indicator */}
      <p className="text-gray-600 text-xs mt-6 text-center">
        Auto-refreshing every 2 seconds
      </p>
    </div>
  );
}