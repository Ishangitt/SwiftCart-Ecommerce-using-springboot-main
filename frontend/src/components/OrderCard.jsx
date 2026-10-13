import { useNavigate } from "react-router-dom";
import { FiCalendar, FiHash } from "react-icons/fi";

export default function OrderCard({ order }) {
  const navigate = useNavigate();

  const getStatusClass = (status) => {
    if (!status) return "";
    const s = status.toLowerCase();
    if (s.includes("cancel")) return "status-cancelled";
    if (s.includes("deliver") || s.includes("complet")) return "status-delivered";
    if (s.includes("confirm") || s.includes("process")) return "status-confirmed";
    return "status-pending";
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleDateString("en-IN", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  return (
    <div
      className="order-card glass-card"
      onClick={() => navigate(`/orders/${order.id}`)}
    >
      <div className="order-card-header">
        <span className="order-card-number">
          <FiHash size={14} /> {order.orderNumber}
        </span>
        <span className={`order-status ${getStatusClass(order.status)}`}>
          {order.status}
        </span>
      </div>

      <div className="order-card-body">
        <div className="order-card-info">
          <span>
            <FiCalendar size={14} /> {formatDate(order.orderDate)}
          </span>
          <span>{order.totalItems} item{order.totalItems !== 1 ? "s" : ""}</span>
        </div>
        <span className="order-card-total">₹{order.totalAmount?.toFixed(2)}</span>
      </div>
    </div>
  );
}
