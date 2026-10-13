import api from "./api";

const orderService = {
  // POST /api/orders  body: { userId, notes }
  placeOrder: async (orderData) => {
    const response = await api.post("/orders", orderData);
    return response.data.data;
  },

  // GET /api/orders/{orderId}
  getOrderById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data.data;
  },

  // GET /api/orders/number/{orderNumber}
  getOrderByNumber: async (orderNumber) => {
    const response = await api.get(`/orders/number/${orderNumber}`);
    return response.data.data;
  },

  // GET /api/orders/user/{userId}
  getOrdersByUserId: async (userId) => {
    const response = await api.get(`/orders/user/${userId}`);
    return response.data.data;
  },

  // GET /api/orders?page=0&size=10&sortBy=orderDate&sortDir=desc (paginated)
  getAllOrdersPaginated: async (
    page = 0,
    size = 10,
    sortBy = "orderDate",
    sortDir = "desc"
  ) => {
    const response = await api.get("/orders", {
      params: { page, size, sortBy, sortDir },
    });
    return response.data.data;
  },

  // GET /api/orders/status/{status}
  getOrdersByStatus: async (status) => {
    const response = await api.get(`/orders/status/${status}`);
    return response.data.data;
  },

  // PATCH /api/orders/{orderId}/status  body: { orderStatus, notes }
  updateOrderStatus: async (orderId, statusData) => {
    const response = await api.patch(`/orders/${orderId}/status`, statusData);
    return response.data.data;
  },

  // POST /api/orders/{orderId}/cancel?reason=xxx
  cancelOrder: async (orderId, reason) => {
    const response = await api.post(`/orders/${orderId}/cancel`, null, {
      params: { reason },
    });
    return response.data.data;
  },

  // GET /api/orders/search?keyword=xyz&page=0&size=10
  searchOrders: async (keyword, page = 0, size = 10) => {
    const response = await api.get("/orders/search", {
      params: { keyword, page, size },
    });
    return response.data.data;
  },
};

export default orderService;
