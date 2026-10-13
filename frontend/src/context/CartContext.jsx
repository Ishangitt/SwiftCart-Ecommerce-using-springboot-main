import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import cartService from "../services/cartService";
import { useUser } from "./UserContext";
import toast from "react-hot-toast";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);
  const { currentUser } = useUser();

  const loadCart = useCallback(async () => {
    if (!currentUser) return;
    setLoading(true);
    try {
      const data = await cartService.getCart(currentUser.id);
      setCart(data);
    } catch (error) {
      console.error("Failed to load cart:", error);
    } finally {
      setLoading(false);
    }
  }, [currentUser]);

  useEffect(() => {
    if (currentUser) {
      loadCart();
    } else {
      setCart(null);
    }
  }, [currentUser, loadCart]);

  const addToCart = async (productId, quantity = 1) => {
    if (!currentUser) {
      toast.error("Please select a user first");
      return;
    }
    try {
      const data = await cartService.addToCart(
        currentUser.id,
        productId,
        quantity
      );
      setCart(data);
      toast.success("Added to cart!");
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  const updateQuantity = async (cartItemId, quantity) => {
    if (!currentUser) return;
    try {
      const data = await cartService.updateCartItem(
        currentUser.id,
        cartItemId,
        quantity
      );
      setCart(data);
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  const removeItem = async (cartItemId) => {
    if (!currentUser) return;
    try {
      const data = await cartService.removeFromCart(currentUser.id, cartItemId);
      setCart(data);
      toast.success("Item removed");
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  const clearCart = async () => {
    if (!currentUser) return;
    try {
      const data = await cartService.clearCart(currentUser.id);
      setCart(data);
      toast.success("Cart cleared");
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  const incrementQuantity = async (productId) => {
    if (!currentUser) return;
    try {
      const data = await cartService.incrementQuantity(
        currentUser.id,
        productId
      );
      setCart(data);
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  const decrementQuantity = async (productId) => {
    if (!currentUser) return;
    try {
      const data = await cartService.decrementQuantity(
        currentUser.id,
        productId
      );
      setCart(data);
      return data;
    } catch (error) {
      toast.error(error.message);
      throw error;
    }
  };

  // Use correct field names from CartResponseDTO
  const itemCount = cart?.totalItems || 0;
  const totalAmount = cart?.totalAmount || 0;

  return (
    <CartContext.Provider
      value={{
        cart,
        loading,
        itemCount,
        totalAmount,
        loadCart,
        addToCart,
        updateQuantity,
        removeItem,
        clearCart,
        incrementQuantity,
        decrementQuantity,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within CartProvider");
  }
  return context;
}
