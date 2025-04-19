package com.ecommerce.control;

import com.ecommerce.dao.OrderDao;
import com.ecommerce.entity.Account;
import com.ecommerce.entity.Order;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderHistoryControl", value = "/order-history")
public class OrderHistoryControl extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Redirect to login if not authenticated
        if (session == null || session.getAttribute("account") == null) {
            String targetUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + response.encodeURL(targetUrl));
            return;
        }

        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Session error: Account is missing.");
            return;
        }

        // Fetch and show order history
        List<Order> orderList = orderDao.getOrderHistory(account.getId());
        request.setAttribute("order_list", orderList);
        request.setAttribute("order_history_active", "active");

        RequestDispatcher dispatcher = request.getRequestDispatcher("order-history.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect POST to GET for now
        doGet(request, response);
    }
}
