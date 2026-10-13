import { useState, useEffect } from "react";
import { useUser } from "../context/UserContext";
import orderService from "../services/orderService";
import OrderCard from "../components/OrderCard";
import Loading from "../components/Loading";
import { FiShoppingBag } from "react-icons/fi";
import { Link } from "react-router-dom";

export default function Orders() {
  const { currentUser } = useUser();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOrders = async () => {
      if (!currentUser) {
        setLoading(false);
        return;
      }
      try {
        const data = await orderService.getOrdersByUserId(currentUser.id);
        setOrders(data);
      } catch (error) {
        console.error("Failed to load orders:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, [currentUser]);

  if (loading) return <Loading text="Loading orders..." />;

  return (
    <div className="container page-wrapper fade-in">
      <div className="section-header">
        <h2>My Orders</h2>
        <p>
          {orders.length} order{orders.length !== 1 ? "s" : ""} found
        </p>
      </div>

      {orders.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📋</div>
          <h3>No orders yet</h3>
          <p>Place your first order today!</p>
          <Link to="/products" className="btn btn-primary">
            <FiShoppingBag /> Browse Products
          </Link>
        </div>
      ) : (
        <div className="orders-grid stagger">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </div>
      )}
    </div>
  );
}
