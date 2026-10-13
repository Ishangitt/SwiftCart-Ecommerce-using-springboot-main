import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { FiShoppingCart, FiEye } from "react-icons/fi";

export default function ProductCard({ product }) {
  const navigate = useNavigate();
  const { addToCart } = useCart();

  const handleAddToCart = async (e) => {
    e.stopPropagation();
    try {
      await addToCart(product.id, 1);
    } catch {
      // error handled in context
    }
  };

  return (
    <div
      className="product-card glass-card"
      onClick={() => navigate(`/products/${product.id}`)}
    >
      <div className="product-card-image">
        {product.imageUrl ? (
          <img src={product.imageUrl} alt={product.name} />
        ) : (
          <span className="placeholder-icon">📦</span>
        )}
        <span
          className={`product-card-badge ${product.inStock ? "badge-available" : "badge-out-of-stock"}`}
        >
          {product.inStock ? "In Stock" : "Out of Stock"}
        </span>
      </div>

      <div className="product-card-body">
        {product.category && (
          <span className="product-card-category">{product.category}</span>
        )}
        <h3 className="product-card-name">{product.name}</h3>
        <span className="product-card-price">
          ₹{product.price?.toFixed(2)}
        </span>
      </div>

      <div className="product-card-footer">
        <button className="btn btn-secondary btn-sm" onClick={(e) => { e.stopPropagation(); navigate(`/products/${product.id}`); }}>
          <FiEye size={14} /> View
        </button>
        <button
          className="btn btn-primary btn-sm"
          onClick={handleAddToCart}
          disabled={!product.inStock}
        >
          <FiShoppingCart size={14} /> Add
        </button>
      </div>
    </div>
  );
}
