import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useUser } from "../context/UserContext";
import orderService from "../services/orderService";
import Loading from "../components/Loading";
import toast from "react-hot-toast";
import { FiCheckCircle, FiArrowLeft } from "react-icons/fi";

export default function Checkout() {
  const navigate = useNavigate();
  const { cart, itemCount, totalAmount, loadCart } = useCart();
  const { currentUser } = useUser();
  const [notes, setNotes] = useState("");
  const [placing, setPlacing] = useState(false);

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="container page-wrapper fade-in">
        <div className="empty-state">
          <div className="empty-state-icon">🛒</div>
          <h3>Nothing to checkout</h3>
          <p>Add some items to your cart first.</p>
          <button
            className="btn btn-primary"
            onClick={() => navigate("/products")}
          >
            Browse Products
          </button>
        </div>
      </div>
    );
  }

  const handlePlaceOrder = async () => {
    if (!currentUser) {
      toast.error("Please select a user first");
      return;
    }
    setPlacing(true);
    try {
      const order = await orderService.placeOrder({
        userId: currentUser.id,
        notes: notes || undefined,
      });
      toast.success("Order placed successfully!");
      await loadCart(); // refresh cart (should be empty now)
      navigate(`/orders/${order.id}`);
    } catch (error) {
      toast.error(error.message);
    } finally {
      setPlacing(false);
    }
  };

  return (
    <div className="container page-wrapper fade-in">
      <button
        className="btn btn-secondary btn-sm"
        onClick={() => navigate("/cart")}
        style={{ marginBottom: "24px" }}
      >
        <FiArrowLeft /> Back to Cart
      </button>

      <div className="section-header">
        <h2>Checkout</h2>
        <p>Review your order and confirm</p>
      </div>

      <div className="checkout-layout">
        {/* Order Form */}
        <div className="checkout-form glass-card">
          <h2>Order Details</h2>

          <div className="form-group" style={{ marginBottom: "16px" }}>
            <label>Customer</label>
            <input
              className="input-field"
              type="text"
              value={currentUser?.fullName || "No user selected"}
              disabled
            />
          </div>

          <div className="form-group" style={{ marginBottom: "16px" }}>
            <label>Email</label>
            <input
              className="input-field"
              type="text"
              value={currentUser?.email || ""}
              disabled
            />
          </div>

          <div className="form-group" style={{ marginBottom: "24px" }}>
            <label>Order Notes (optional)</label>
            <textarea
              className="input-field"
              rows={3}
              placeholder="Any special instructions..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              style={{ resize: "vertical" }}
            />
          </div>

          <button
            className="btn btn-primary btn-lg"
            onClick={handlePlaceOrder}
            disabled={placing}
            style={{ width: "100%" }}
          >
            <FiCheckCircle />
            {placing ? "Placing Order..." : `Place Order — ₹${totalAmount?.toFixed(2)}`}
          </button>
        </div>

        {/* Cart Items Preview */}
        <div className="checkout-items glass-card">
          <h3>Cart Items ({itemCount})</h3>

          {cart.items.map((item) => (
            <div key={item.id} className="checkout-item">
              <div>
                <div style={{ fontWeight: 600, fontSize: "0.9rem" }}>
                  {item.productName}
                </div>
                <div style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>
                  {item.quantity} × ₹{item.unitPrice?.toFixed(2)}
                </div>
              </div>
              <div style={{ fontWeight: 600 }}>₹{item.subtotal?.toFixed(2)}</div>
            </div>
          ))}

          <div
            className="summary-row total"
            style={{ marginTop: "16px", paddingTop: "16px" }}
          >
            <span>Total</span>
            <span className="amount">₹{totalAmount?.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
