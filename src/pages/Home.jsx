import React from "react";
import "../styles/Home.css";
import heroBus from "../assets/hero-bus.png";
import Footer from "../components/Footer";

const Home = () => {
  return (
    <div className="home">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-text">
          <h1>Welcome to BusBuddy 🚍</h1>
          <p>Your friendly bus booking partner – Fast, Easy & Reliable!</p>
          <button className="book-now-btn">Book Your Seat</button>
        </div>
        <div className="hero-image">
          <img src={heroBus} alt="Bus" />
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <h2>Why Choose BusBuddy?</h2>
        <div className="feature-list">
          <div className="feature-card">
            <h3>🚌 Easy Booking</h3>
            <p>Book tickets in just a few clicks.</p>
          </div>
          <div className="feature-card">
            <h3>💳 Secure Payments</h3>
            <p>Pay online safely & instantly.</p>
          </div>
          <div className="feature-card">
            <h3>📍 Live Tracking</h3>
            <p>Track your bus in real-time.</p>
          </div>
        </div>
      </section>

      {/* Footer Section */}
      <Footer />
    </div>
  );
};

export default Home;
