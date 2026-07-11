export default function KeysTable({ keys }) {
  return (
    <div className="bg-gray-800 rounded-lg overflow-hidden">
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-700">
            <th className="text-left text-gray-400 text-sm font-medium px-6 py-3">Key</th>
            <th className="text-left text-gray-400 text-sm font-medium px-6 py-3">TTL</th>
            <th className="text-left text-gray-400 text-sm font-medium px-6 py-3">Status</th>
          </tr>
        </thead>
        <tbody>
          {keys.length === 0 ? (
            <tr>
              <td colSpan="3" className="text-center text-gray-500 px-6 py-8">
                No keys stored
              </td>
            </tr>
          ) : (
            keys.map((item) => (
              <tr key={item.key} className="border-b border-gray-700 last:border-0">
                <td className="px-6 py-4 text-white font-mono">{item.key}</td>
                <td className="px-6 py-4 text-gray-300">
                  {item.ttl === -1 ? "No expiry" : `${item.ttl}s`}
                </td>
                <td className="px-6 py-4">
                  <span className={`text-xs px-2 py-1 rounded-full ${
                    item.ttl === -1
                      ? "bg-blue-900 text-blue-300"
                      : "bg-yellow-900 text-yellow-300"
                  }`}>
                    {item.ttl === -1 ? "persistent" : "expiring"}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}