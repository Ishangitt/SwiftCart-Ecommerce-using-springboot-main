import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import CartItem from "../components/CartItem";
import Loading from "../components/Loading";
import { FiShoppingBag, FiTrash2, FiArrowRight } from "react-icons/fi";

export default function Cart() {
  const { cart, loading, clearCart, itemCount, totalAmount } = useCart();

  if (loading) return <Loading text="Loading cart..." />;

  const items = cart?.items || [];

  return (
    <div className="container page-wrapper fade-in">
      <div className="section-header">
        <h2>Shopping Cart</h2>
        <p>{itemCount} item{itemCount !== 1 ? "s" : ""} in your cart</p>
      </div>

      {items.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">🛒</div>
          <h3>Your cart is empty</h3>
          <p>Start shopping to add items to your cart!</p>
          <Link to="/products" className="btn btn-primary">
            <FiShoppingBag /> Browse Products
          </Link>
        </div>
      ) : (
        <div className="cart-layout">
          <div className="cart-items stagger">
            {items.map((item) => (
              <CartItem key={item.id} item={item} />
            ))}
          </div>

          <div className="cart-summary glass-card">
            <h3>Order Summary</h3>

            <div className="summary-row">
              <span>Items ({itemCount})</span>
              <span>₹{totalAmount?.toFixed(2)}</span>
            </div>

            <div className="summary-row total">
              <span>Total</span>
              <span className="amount">₹{totalAmount?.toFixed(2)}</span>
            </div>

            <div className="summary-actions">
              <Link to="/checkout" className="btn btn-primary">
                Proceed to Checkout <FiArrowRight />
              </Link>
              <button className="btn btn-danger" onClick={clearCart}>
                <FiTrash2 /> Clear Cart
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
