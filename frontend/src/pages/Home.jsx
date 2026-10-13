import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import productService from "../services/productService";
import ProductCard from "../components/ProductCard";
import Loading from "../components/Loading";
import { FiArrowRight, FiShoppingBag } from "react-icons/fi";

export default function Home() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const data = await productService.getAvailableProducts();
        setProducts(data.slice(0, 8)); // show up to 8 on home
      } catch (error) {
        console.error("Failed to load products:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  return (
    <div className="fade-in">
      {/* Hero Section */}
      <section className="hero">
        <div className="container hero-content">
          <h1>Shop Smarter, Not Harder</h1>
          <p>
            Discover premium products at unbeatable prices. Your one-stop
            destination for everything you need.
          </p>
          <Link to="/products" className="btn btn-primary btn-lg">
            <FiShoppingBag /> Browse Products <FiArrowRight />
          </Link>
        </div>
      </section>

      {/* Featured Products */}
      <section className="container page-wrapper">
        <div className="section-header">
          <h2>Featured Products</h2>
          <p>Handpicked items just for you</p>
        </div>

        {loading ? (
          <Loading text="Loading products..." />
        ) : products.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">📦</div>
            <h3>No products available</h3>
            <p>Check back later for new arrivals!</p>
          </div>
        ) : (
          <>
            <div className="product-grid stagger">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
            <div style={{ textAlign: "center", marginTop: "40px" }}>
              <Link to="/products" className="btn btn-secondary btn-lg">
                View All Products <FiArrowRight />
              </Link>
            </div>
          </>
        )}
      </section>
    </div>
  );
}
