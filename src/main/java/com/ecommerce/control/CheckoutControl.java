package com.ecommerce.control;

import com.ecommerce.dao.AccountDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.entity.Account;
import com.ecommerce.entity.Order;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CheckoutControl", value = "/checkout")
public class CheckoutControl extends HttpServlet {
    // IMPORTANT: These DAOs need refactoring for proper connection management
    OrderDao orderDao = new OrderDao();
    AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Don't create if not exists

        // Check if user is logged in
        if (session == null || session.getAttribute("account") == null) {
            // User not logged in, store current URL and redirect to login
            String targetUrl = request.getRequestURI(); // This will be /test-1.0-SNAPSHOT/checkout
            // If there were important query parameters for checkout, append them:
            // if (request.getQueryString() != null) {
            //     targetUrl += "?" + request.getQueryString();
            // }
            // No need to store in session here, LoginControl.doGet will pick up "redirect" param
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + response.encodeURL(targetUrl));
            return;
        }

        // User is logged in, forward to checkout.jsp
        // Ensure account and order/total_price are in session from previous steps (e.g., cart)
        if (session.getAttribute("order") == null || session.getAttribute("total_price") == null) {
            // If cart is empty or no total price, maybe redirect to cart or shop
            response.sendRedirect(request.getContextPath() + "/cart.jsp"); // Or wherever appropriate
            return;
        }

        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Double check authentication before processing the order
        if (session == null || session.getAttribute("account") == null) {
            // Should not happen if doGet protected it, but as a safeguard
            String targetUrl = request.getContextPath() + "/checkout"; // Default target for checkout POST
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + response.encodeURL(targetUrl));
            return;
        }

        // Get information from input field.
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        double totalPrice = (double) session.getAttribute("total_price");
        Order order = (Order) session.getAttribute("order");
        Account account = (Account) session.getAttribute("account");

        // Validate that order and totalPrice are present
        if (order == null || order.getCartProducts() == null || order.getCartProducts().isEmpty()) {
            // Cart is empty, redirect to shop or cart page with a message
            request.setAttribute("checkoutError", "Your cart is empty.");
            response.sendRedirect(request.getContextPath() + "/shop"); // Or cart.jsp
            return;
        }

        // Insert information to account.
        int accountId = account.getId();
        accountDao.updateProfileInformation(accountId, firstName, lastName, address, email, phone);
        // Insert order to database.
        orderDao.createOrder(account.getId(), totalPrice, order.getCartProducts());

        session.removeAttribute("order");
        session.removeAttribute("total_price");

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("thankyou.jsp");
        requestDispatcher.forward(request, response);
    }
}