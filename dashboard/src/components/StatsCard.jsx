export default function StatsCard({ keyCount, uptimeSeconds }) {
  const hours = Math.floor(uptimeSeconds / 3600);
  const minutes = Math.floor((uptimeSeconds % 3600) / 60);
  const seconds = uptimeSeconds % 60;

  const uptime = `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;

  return (
    <div className="grid grid-cols-2 gap-4">
      <div className="bg-gray-800 rounded-lg p-6">
        <p className="text-gray-400 text-sm mb-1">Total Keys</p>
        <p className="text-white text-4xl font-bold">{keyCount}</p>
      </div>
      <div className="bg-gray-800 rounded-lg p-6">
        <p className="text-gray-400 text-sm mb-1">Uptime</p>
        <p className="text-white text-4xl font-bold">{uptime}</p>
      </div>
    </div>
  );
}