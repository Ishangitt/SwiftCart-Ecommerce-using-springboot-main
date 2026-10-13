import { useState, useEffect } from "react";
import productService from "../services/productService";
import ProductCard from "../components/ProductCard";
import Loading from "../components/Loading";
import { FiSearch, FiChevronLeft, FiChevronRight } from "react-icons/fi";

export default function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [categories, setCategories] = useState(["All"]);

  // Pagination state
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, [page]);

  useEffect(() => {
    // load all products once to extract categories
    const loadCategories = async () => {
      try {
        const all = await productService.getAllProducts();
        const cats = [...new Set(all.map((p) => p.category).filter(Boolean))];
        setCategories(["All", ...cats]);
      } catch {
        // ignore
      }
    };
    loadCategories();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const data = await productService.getProductsPaginated(page, 12);
      setProducts(data.content || []);
      setPageInfo(data);
    } catch (error) {
      console.error("Failed to load products:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchProducts();
      return;
    }
    setLoading(true);
    try {
      const data = await productService.searchProducts(searchTerm, 0, 12);
      setProducts(data.content || []);
      setPageInfo(data);
    } catch (error) {
      console.error("Search failed:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryFilter = async (category) => {
    setActiveCategory(category);
    setLoading(true);
    try {
      if (category === "All") {
        fetchProducts();
        return;
      }
      const data = await productService.getProductsByCategory(category);
      setProducts(data);
      setPageInfo(null); // non-paginated endpoint
    } catch (error) {
      console.error("Category filter failed:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container page-wrapper fade-in">
      <div className="page-header-bar">
        <div className="section-header" style={{ marginBottom: 0 }}>
          <h2>All Products</h2>
          <p>
            {pageInfo
              ? `${pageInfo.totalElements} products found`
              : `${products.length} products`}
          </p>
        </div>

        <div className="search-bar">
          <FiSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          />
        </div>
      </div>

      {/* Category Filter Chips */}
      <div className="filter-bar">
        {categories.map((cat) => (
          <button
            key={cat}
            className={`filter-chip ${activeCategory === cat ? "active" : ""}`}
            onClick={() => handleCategoryFilter(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {loading ? (
        <Loading text="Loading products..." />
      ) : products.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">🔍</div>
          <h3>No products found</h3>
          <p>Try adjusting your search or filters.</p>
        </div>
      ) : (
        <>
          <div className="product-grid stagger">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {/* Pagination */}
          {pageInfo && pageInfo.totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={pageInfo.first}
              >
                <FiChevronLeft />
              </button>
              <span className="btn btn-secondary btn-sm" style={{ cursor: "default" }}>
                Page {pageInfo.pageNumber + 1} of {pageInfo.totalPages}
              </span>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setPage((p) => p + 1)}
                disabled={pageInfo.last}
              >
                <FiChevronRight />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
