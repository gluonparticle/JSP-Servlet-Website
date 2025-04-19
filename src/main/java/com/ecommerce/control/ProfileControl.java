package com.ecommerce.control;

import com.ecommerce.dao.AccountDao;
import com.ecommerce.entity.Account;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "ProfileControl", value = "/profile-page")
@MultipartConfig
public class ProfileControl extends HttpServlet {
    private final AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Don't create new session

        // Redirect to login if not authenticated
        if (session == null || session.getAttribute("account") == null) {
            String targetUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + response.encodeURL(targetUrl));
            return;
        }

        // Forward to JSP if authenticated
        RequestDispatcher dispatcher = request.getRequestDispatcher("profile-page.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Ensure user is authenticated
        if (session == null || session.getAttribute("account") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to update your profile.");
            return;
        }

        Account account = (Account) session.getAttribute("account");
        int accountId = account.getId();

        // Read form fields
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String summary = request.getParameter("summary");

        // Handle uploaded profile image
        Part imagePart = request.getPart("profile-image");
        InputStream imageStream = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            imageStream = imagePart.getInputStream();
        }

        // Update profile in DB
        accountDao.editProfileInformation(accountId, firstName, lastName, address, email, phone, summary, imageStream);

        // Refresh session with updated account
        Account updatedAccount = accountDao.getAccount(accountId);
        if (updatedAccount != null) {
            session.setAttribute("account", updatedAccount);
        } else {
            System.err.println("ProfileControl: Failed to refresh account after update.");
        }

        // Redirect to profile page
        response.sendRedirect(request.getContextPath() + "/profile-page");
    }
}
