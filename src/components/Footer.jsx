import React from "react";
import "../styles/Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-content">

        {/* Query Form */}
        <div className="footer-query">
          <h3>Have a Question?</h3>
          <input type="text" placeholder="Type your query here..." />
          <button>Submit</button>
        </div>

        {/* Contact Us */}
        <div className="footer-contact">
          <h3>Contact Us</h3>
          <p>Email: support@busbuddy.com</p>
          <p>Phone: +91 98765 43210</p>
        </div>

      </div>

      <div className="footer-bottom">
        <p>© {new Date().getFullYear()} BusBuddy. All Rights Reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
