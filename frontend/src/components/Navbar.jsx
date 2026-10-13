import { Link, useLocation } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useUser } from "../context/UserContext";
import {
  FiShoppingCart,
  FiPackage,
  FiGrid,
  FiHome,
  FiUser,
} from "react-icons/fi";

export default function Navbar() {
  const { pathname } = useLocation();
  const { itemCount } = useCart();
  const { currentUser, users, selectUser } = useUser();

  return (
    <nav className="navbar">
      <div className="container">
        <Link to="/" className="navbar-brand">
          <FiShoppingCart />
          SwiftCart
        </Link>

        <div className="navbar-links">
          <Link
            to="/"
            className={`nav-link ${pathname === "/" ? "active" : ""}`}
          >
            <FiHome size={18} />
            <span>Home</span>
          </Link>

          <Link
            to="/products"
            className={`nav-link ${
              pathname.startsWith("/products") ? "active" : ""
            }`}
          >
            <FiGrid size={18} />
            <span>Products</span>
          </Link>

          <Link
            to="/orders"
            className={`nav-link ${
              pathname.startsWith("/orders") ? "active" : ""
            }`}
          >
            <FiPackage size={18} />
            <span>Orders</span>
          </Link>

          <Link
            to="/profile"
            className={`nav-link ${
              pathname.startsWith("/profile") ? "active" : ""
            }`}
          >
            <FiUser size={18} />
            <span>Profile</span>
          </Link>

          <Link
            to="/cart"
            className={`nav-link nav-cart-badge ${
              pathname === "/cart" ? "active" : ""
            }`}
          >
            <FiShoppingCart size={18} />
            <span>Cart</span>
            {itemCount > 0 && (
              <span className="nav-cart-count">{itemCount}</span>
            )}
          </Link>

          <div className="user-selector">
            <select
              value={currentUser?.id || ""}
              onChange={(e) => {
                const user = users.find((u) => u.id === Number(e.target.value));
                if (user) selectUser(user);
              }}
            >
              <option value="" disabled>
                Select User
              </option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.fullName}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>
    </nav>
  );
}
