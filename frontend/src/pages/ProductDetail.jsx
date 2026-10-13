import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import productService from "../services/productService";
import { useCart } from "../context/CartContext";
import Loading from "../components/Loading";
import toast from "react-hot-toast";
import {
  FiShoppingCart,
  FiArrowLeft,
  FiPlus,
  FiMinus,
  FiPackage,
  FiTag,
} from "react-icons/fi";

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const data = await productService.getProductById(id);
        setProduct(data);
      } catch (error) {
        toast.error(error.message);
        navigate("/products");
      } finally {
        setLoading(false);
      }
    };
    fetchProduct();
  }, [id, navigate]);

  const handleAddToCart = async () => {
    setAdding(true);
    try {
      await addToCart(product.id, quantity);
    } catch {
      // handled in context
    } finally {
      setAdding(false);
    }
  };

  if (loading) return <Loading text="Loading product..." />;
  if (!product) return null;

  return (
    <div className="container page-wrapper fade-in">
      <button
        className="btn btn-secondary btn-sm"
        onClick={() => navigate("/products")}
        style={{ marginBottom: "24px" }}
      >
        <FiArrowLeft /> Back to Products
      </button>

      <div className="product-detail">
        {/* Image */}
        <div className="product-detail-image glass-card">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <span className="placeholder-icon">📦</span>
          )}
        </div>

        {/* Info */}
        <div className="product-detail-info">
          <div>
            {product.category && (
              <span className="product-card-category">{product.category}</span>
            )}
            <h1>{product.name}</h1>
          </div>

          <div className="product-detail-meta">
            {product.brand && (
              <span className="meta-tag">
                <FiTag size={14} /> {product.brand}
              </span>
            )}
            {product.sku && (
              <span className="meta-tag">
                <FiPackage size={14} /> SKU: {product.sku}
              </span>
            )}
            <span
              className={`stock-info ${product.inStock ? "in-stock" : "out-of-stock"}`}
            >
              ● {product.inStock ? `${product.stockQuantity} in stock` : "Out of stock"}
            </span>
          </div>

          <span className="product-detail-price">
            ₹{product.price?.toFixed(2)}
          </span>

          {product.description && (
            <p className="product-detail-description">{product.description}</p>
          )}

          {/* Quantity Selector */}
          <div className="quantity-control">
            <button
              className="btn btn-icon btn-secondary"
              onClick={() => setQuantity((q) => Math.max(1, q - 1))}
            >
              <FiMinus />
            </button>
            <span>{quantity}</span>
            <button
              className="btn btn-icon btn-secondary"
              onClick={() =>
                setQuantity((q) => Math.min(product.stockQuantity || 99, q + 1))
              }
            >
              <FiPlus />
            </button>
          </div>

          {/* Actions */}
          <div className="product-detail-actions">
            <button
              className="btn btn-primary btn-lg"
              onClick={handleAddToCart}
              disabled={!product.inStock || adding}
            >
              <FiShoppingCart />
              {adding ? "Adding..." : "Add to Cart"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
