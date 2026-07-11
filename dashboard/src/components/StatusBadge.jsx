export default function StatusBadge({ online }) {
  return (
    <div className="flex items-center gap-2">
      <div className={`w-3 h-3 rounded-full ${online ? "bg-green-500" : "bg-red-500"}`} />
      <span className={`text-sm font-medium ${online ? "text-green-400" : "text-red-400"}`}>
        {online ? "Online" : "Offline"}
      </span>
    </div>
  );
}