import { useCart } from "../context/CartContext";
import { FiTrash2, FiPlus, FiMinus } from "react-icons/fi";

export default function CartItem({ item }) {
  const { incrementQuantity, decrementQuantity, removeItem } = useCart();

  return (
    <div className="cart-item glass-card">
      <div className="cart-item-image">
        {item.productImage ? (
          <img src={item.productImage} alt={item.productName} />
        ) : (
          <span>📦</span>
        )}
      </div>

      <div className="cart-item-info">
        <span className="cart-item-name">{item.productName}</span>
        <span className="cart-item-price">₹{item.unitPrice?.toFixed(2)}</span>
        {item.productSku && (
          <span className="cart-item-sku">SKU: {item.productSku}</span>
        )}
      </div>

      <div className="cart-item-actions">
        <div className="quantity-control">
          <button
            className="btn btn-icon btn-secondary"
            onClick={() => decrementQuantity(item.productId)}
            disabled={item.quantity <= 1}
          >
            <FiMinus size={14} />
          </button>
          <span>{item.quantity}</span>
          <button
            className="btn btn-icon btn-secondary"
            onClick={() => incrementQuantity(item.productId)}
            disabled={item.quantity >= (item.availableStock || 99)}
          >
            <FiPlus size={14} />
          </button>
        </div>

        <span className="cart-item-subtotal">₹{item.subtotal?.toFixed(2)}</span>

        <button
          className="btn btn-icon btn-danger"
          onClick={() => removeItem(item.id)}
          title="Remove item"
        >
          <FiTrash2 size={14} />
        </button>
      </div>
    </div>
  );
}
