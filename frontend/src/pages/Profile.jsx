import { useState, useEffect } from "react";
import { useUser } from "../context/UserContext";
import toast from "react-hot-toast";
import { FiUser, FiMail, FiPhone, FiSave, FiEdit } from "react-icons/fi";
import Loading from "../components/Loading";

export default function Profile() {
  const { currentUser, loading: userLoading, refreshCurrentUser } = useUser();
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    address: "",
  });
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (currentUser) {
      setFormData({
        fullName: currentUser.fullName || "",
        email: currentUser.email || "",
        phone: currentUser.phone || "",
        address: currentUser.address || "",
      });
    }
  }, [currentUser]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!currentUser) return;

    const changedData = Object.keys(formData).reduce((acc, key) => {
      if (formData[key] !== currentUser[key]) {
        acc[key] = formData[key];
      }
      return acc;
    }, {});

    if (Object.keys(changedData).length === 0) {
      toast.success("No changes to save.");
      setIsEditing(false);
      return;
    }

    setSaving(true);
    try {
      await refreshCurrentUser(currentUser.id, changedData);
      toast.success("Profile updated successfully!");
      setIsEditing(false);
    } catch (error) {
      toast.error(error.message);
    } finally {
      setSaving(false);
    }
  };

  if (userLoading) return <Loading text="Loading profile..." />;
  if (!currentUser) {
    return (
      <div className="container page-wrapper fade-in">
        <div className="empty-state">
          <h3>Please select a user to view their profile.</h3>
        </div>
      </div>
    );
  }

  return (
    <div className="container page-wrapper fade-in">
      <div className="section-header">
        <h2>User Profile</h2>
        <p>View and manage your personal information.</p>
      </div>

      <div className="profile-card glass-card">
        <div className="profile-header">
          <h3>
            <FiUser /> {currentUser.fullName}
          </h3>
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setIsEditing(!isEditing)}
          >
            <FiEdit /> {isEditing ? "Cancel" : "Edit Profile"}
          </button>
        </div>

        <form onSubmit={handleSubmit} className="profile-form">
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="fullName">
                <FiUser /> Full Name
              </label>
              <input
                type="text"
                id="fullName"
                name="fullName"
                className="input-field"
                value={formData.fullName}
                onChange={handleChange}
                disabled={!isEditing}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">
                <FiMail /> Email Address
              </label>
              <input
                type="email"
                id="email"
                name="email"
                className="input-field"
                value={formData.email}
                onChange={handleChange}
                disabled={!isEditing}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">
                <FiPhone /> Phone Number
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                className="input-field"
                value={formData.phone}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            <div className="form-group">
              <label htmlFor="address">
                <FiUser /> Address
              </label>
              <input
                type="text"
                id="address"
                name="address"
                className="input-field"
                value={formData.address}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>
          </div>

          {isEditing && (
            <div className="form-actions">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={saving}
              >
                <FiSave /> {saving ? "Saving..." : "Save Changes"}
              </button>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
