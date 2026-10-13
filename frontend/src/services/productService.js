import api from "./api";

const productService = {
  // POST /api/products
  createProduct: async (productData) => {
    const response = await api.post("/products", productData);
    return response.data.data;
  },

  // GET /api/products/{id}
  getProductById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data.data;
  },

  // GET /api/products/sku/{sku}
  getProductBySku: async (sku) => {
    const response = await api.get(`/products/sku/${sku}`);
    return response.data.data;
  },

  // GET /api/products/all (non-paginated)
  getAllProducts: async () => {
    const response = await api.get("/products/all");
    return response.data.data;
  },

  // GET /api/products?page=0&size=10&sortBy=name&sortDir=asc (paginated)
  getProductsPaginated: async (
    page = 0,
    size = 12,
    sortBy = "id",
    sortDir = "asc"
  ) => {
    const response = await api.get("/products", {
      params: { page, size, sortBy, sortDir },
    });
    return response.data.data;
  },

  // GET /api/products/available
  getAvailableProducts: async () => {
    const response = await api.get("/products/available");
    return response.data.data;
  },

  // GET /api/products/category/{category}
  getProductsByCategory: async (category) => {
    const response = await api.get(`/products/category/${category}`);
    return response.data.data;
  },

  // GET /api/products/price-range?min=100&max=500
  getProductsByPriceRange: async (min, max) => {
    const response = await api.get("/products/price-range", {
      params: { min, max },
    });
    return response.data.data;
  },

  // GET /api/products/search?keyword=xyz&page=0&size=10
  searchProducts: async (keyword, page = 0, size = 10) => {
    const response = await api.get("/products/search", {
      params: { keyword, page, size },
    });
    return response.data.data;
  },

  // PUT /api/products/{id}
  updateProduct: async (id, productData) => {
    const response = await api.put(`/products/${id}`, productData);
    return response.data.data;
  },

  // PATCH /api/products/{id}/stock?quantity=10
  updateStock: async (id, quantity) => {
    const response = await api.patch(`/products/${id}/stock`, null, {
      params: { quantity },
    });
    return response.data;
  },

  // GET /api/products/low-stock?threshold=10
  getLowStockProducts: async (threshold = 10) => {
    const response = await api.get("/products/low-stock", {
      params: { threshold },
    });
    return response.data.data;
  },

  // GET /api/products/check-sku?sku=xyz
  checkSkuExists: async (sku) => {
    const response = await api.get("/products/check-sku", {
      params: { sku },
    });
    return response.data.data;
  },
};

export default productService;
