import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import orderService from "../services/orderService";
import Loading from "../components/Loading";
import toast from "react-hot-toast";
import { FiArrowLeft, FiXCircle, FiCalendar, FiUser, FiMail } from "react-icons/fi";

export default function OrderDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        const data = await orderService.getOrderById(id);
        setOrder(data);
      } catch (error) {
        toast.error(error.message);
        navigate("/orders");
      } finally {
        setLoading(false);
      }
    };
    fetchOrder();
  }, [id, navigate]);

  const handleCancelOrder = async () => {
    if (!window.confirm("Are you sure you want to cancel this order?")) return;
    setCancelling(true);
    try {
      const updated = await orderService.cancelOrder(
        order.id,
        "Customer requested cancellation"
      );
      setOrder(updated);
      toast.success("Order cancelled");
    } catch (error) {
      toast.error(error.message);
    } finally {
      setCancelling(false);
    }
  };

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
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading) return <Loading text="Loading order..." />;
  if (!order) return null;

  const isCancellable =
    order.status &&
    !order.status.toLowerCase().includes("cancel") &&
    !order.status.toLowerCase().includes("deliver") &&
    !order.status.toLowerCase().includes("complet");

  return (
    <div className="container page-wrapper fade-in">
      <button
        className="btn btn-secondary btn-sm"
        onClick={() => navigate("/orders")}
        style={{ marginBottom: "24px" }}
      >
        <FiArrowLeft /> Back to Orders
      </button>

      <div className="order-detail-header">
        <div>
          <h1>Order #{order.orderNumber}</h1>
          <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>
            <FiCalendar size={14} /> {formatDate(order.orderDate)}
          </span>
        </div>
        <div style={{ display: "flex", gap: "12px", alignItems: "center" }}>
          <span className={`order-status ${getStatusClass(order.status)}`}>
            {order.status}
          </span>
          {isCancellable && (
            <button
              className="btn btn-danger btn-sm"
              onClick={handleCancelOrder}
              disabled={cancelling}
            >
              <FiXCircle /> {cancelling ? "Cancelling..." : "Cancel Order"}
            </button>
          )}
        </div>
      </div>

      <div className="order-detail-grid">
        {/* Order Items */}
        <div>
          <div className="section-header">
            <h2>Order Items</h2>
          </div>
          <div className="order-items stagger">
            {order.items?.map((item) => (
              <div key={item.id} className="order-item glass-card">
                <div className="order-item-image">
                  {item.productImage ? (
                    <img src={item.productImage} alt={item.productName} />
                  ) : (
                    <span>📦</span>
                  )}
                </div>
                <div className="order-item-info">
                  <div className="order-item-name">{item.productName}</div>
                  <div className="order-item-meta">
                    {item.productSku && <>SKU: {item.productSku} · </>}
                    {item.quantity} × ₹{item.unitPrice?.toFixed(2)}
                  </div>
                </div>
                <div className="order-item-subtotal">
                  ₹{item.subtotal?.toFixed(2)}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Order Summary */}
        <div className="order-summary-card glass-card">
          <h3>Order Summary</h3>

          <div className="summary-row">
            <span><FiUser size={14} /> Customer</span>
            <span>{order.userName}</span>
          </div>

          <div className="summary-row">
            <span><FiMail size={14} /> Email</span>
            <span style={{ fontSize: "0.8rem" }}>{order.userEmail}</span>
          </div>

          <div className="summary-row">
            <span>Items</span>
            <span>{order.totalItems}</span>
          </div>

          {order.notes && (
            <div style={{ padding: "8px 0", fontSize: "0.85rem", color: "var(--text-secondary)" }}>
              <strong>Notes:</strong> {order.notes}
            </div>
          )}

          <div className="summary-row total">
            <span>Total</span>
            <span className="amount">₹{order.totalAmount?.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
