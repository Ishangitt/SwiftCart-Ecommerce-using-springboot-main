import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import userService from "../services/userService";

const UserContext = createContext(null);

export function UserProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadUsers = useCallback(async () => {
    setLoading(true);
    try {
      const data = await userService.getAllUsers();
      setUsers(data);
      if (data.length > 0 && !currentUser) {
        setCurrentUser(data[0]);
      }
    } catch (error) {
      console.error("Failed to load users:", error);
    } finally {
      setLoading(false);
    }
  }, [currentUser]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const selectUser = (user) => {
    setCurrentUser(user);
  };

  const refreshCurrentUser = useCallback(async (userId, updatedData) => {
    try {
      const updatedUser = await userService.updateUser(userId, updatedData);
      setCurrentUser(updatedUser);
      setUsers((prevUsers) =>
        prevUsers.map((u) => (u.id === userId ? updatedUser : u)),
      );
    } catch (error) {
      console.error("Failed to refresh user:", error);
      throw error;
    }
  }, []);

  return (
    <UserContext.Provider
      value={{
        currentUser,
        users,
        loading,
        selectUser,
        loadUsers,
        refreshCurrentUser,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within UserProvider");
  }
  return context;
}
