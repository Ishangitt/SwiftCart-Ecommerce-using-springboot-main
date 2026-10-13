import api from "./api";

const cartService = {
  // GET /api/cart/{userId}
  getCart: async (userId) => {
    const response = await api.get(`/cart/${userId}`);
    return response.data.data;
  },

  // POST /api/cart/{userId}/items  body: { productId, quantity }
  addToCart: async (userId, productId, quantity = 1) => {
    const response = await api.post(`/cart/${userId}/items`, {
      productId,
      quantity,
    });
    return response.data.data;
  },

  // PUT /api/cart/{userId}/items/{cartItemId}  body: { quantity }
  updateCartItem: async (userId, cartItemId, quantity) => {
    const response = await api.put(`/cart/${userId}/items/${cartItemId}`, {
      quantity,
    });
    return response.data.data;
  },

  // DELETE /api/cart/{userId}/items/{cartItemId}
  removeFromCart: async (userId, cartItemId) => {
    const response = await api.delete(`/cart/${userId}/items/${cartItemId}`);
    return response.data.data;
  },

  // DELETE /api/cart/{userId}/clear
  clearCart: async (userId) => {
    const response = await api.delete(`/cart/${userId}/clear`);
    return response.data.data;
  },

  // GET /api/cart/{userId}/items/{cartItemId}
  getCartItem: async (userId, cartItemId) => {
    const response = await api.get(`/cart/${userId}/items/${cartItemId}`);
    return response.data.data;
  },

  // GET /api/cart/{userId}/check-product/{productId}
  isProductInCart: async (userId, productId) => {
    const response = await api.get(
      `/cart/${userId}/check-product/${productId}`
    );
    return response.data.data;
  },

  // PATCH /api/cart/{userId}/products/{productId}/increment?quantity=1
  incrementQuantity: async (userId, productId, quantity = 1) => {
    const response = await api.patch(
      `/cart/${userId}/products/${productId}/increment`,
      null,
      { params: { quantity } }
    );
    return response.data.data;
  },

  // PATCH /api/cart/{userId}/products/{productId}/decrement?quantity=1
  decrementQuantity: async (userId, productId, quantity = 1) => {
    const response = await api.patch(
      `/cart/${userId}/products/${productId}/decrement`,
      null,
      { params: { quantity } }
    );
    return response.data.data;
  },
};

export default cartService;
