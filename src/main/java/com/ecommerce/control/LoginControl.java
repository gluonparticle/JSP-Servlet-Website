package com.ecommerce.control;

import com.ecommerce.dao.AccountDao;
import com.ecommerce.entity.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

@WebServlet(name = "LoginControl", value = "/login")
public class LoginControl extends HttpServlet {
    // IMPORTANT: AccountDao needs to be refactored for proper connection management (try-with-resources)
    AccountDao accountDao = new AccountDao();

    // Method to check and retrieve account from cookies
    private Account getAccountFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        String username = null;
        String password = null; // In a real app, store a token, not the raw password
        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.getName())) {
                username = cookie.getValue();
            } else if ("password".equals(cookie.getName())) {
                password = cookie.getValue(); // Again, not ideal for production
            }
        }

        if (username != null && password != null) {
            return accountDao.checkLoginAccount(username, password);
        }
        return null;
    }

    // Method to handle successful login (setting session, cookies, and redirecting)
    private void processSuccessfulLogin(HttpServletRequest request, HttpServletResponse response, Account account) throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("account", account);

        boolean rememberMe = (request.getParameter("remember-me-checkbox") != null);
        if (rememberMe) {
            Cookie usernameCookie = new Cookie("username", account.getUsername());
            usernameCookie.setMaxAge(60 * 60 * 24 * 7); // 7 days
            response.addCookie(usernameCookie);

            Cookie passwordCookie = new Cookie("password", account.getPassword()); // Highly insecure to store raw password
            passwordCookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(passwordCookie);
        }

        // Redirect logic
        String redirectUrl = (String) session.getAttribute("loginRedirectUrl");
        session.removeAttribute("loginRedirectUrl"); // Clear it after use

        if (redirectUrl != null && !redirectUrl.isEmpty() && isValidLocalRedirect(redirectUrl, request.getContextPath())) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/"); // Default to homepage
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If user is already logged in (e.g., via session or valid cookie), redirect them appropriately
        HttpSession session = request.getSession(false); // Don't create session if it doesn't exist
        if (session != null && session.getAttribute("account") != null) {
            response.sendRedirect(request.getContextPath() + "/"); // Already logged in, go to home
            return;
        }

        Account accountFromCookie = getAccountFromCookie(request);
        if (accountFromCookie != null) {
            processSuccessfulLogin(request, response, accountFromCookie); // Log in with cookie and redirect
            return;
        }

        // Capture the URL the user was trying to access if provided as "redirect" parameter
        String targetUrl = request.getParameter("redirect");
        if (targetUrl != null && !targetUrl.isEmpty()) {
            // Store it in session so it survives the POST request from the login form
            // Ensure it's a relative path within the app or a full path starting with context path
            if (targetUrl.startsWith(request.getContextPath() + "/") || !targetUrl.startsWith("http")) {
                request.getSession().setAttribute("loginRedirectUrl", targetUrl);
            }
        } else {
            // If no specific redirect, ensure any old one is cleared
            request.getSession().removeAttribute("loginRedirectUrl");
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        Account account = accountDao.checkLoginAccount(username, pass);

        if (account == null) {
            request.setAttribute("alert", "<div class=\"alert alert-danger\" role=\"alert\">\n" +
                    "                            Wrong username or password!\n" +
                    "                        </div>");
            // Forward back to login.jsp, preserving any redirectUrl that might have been set in session by doGet
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            processSuccessfulLogin(request, response, account);
        }
    }

    // Basic validation for redirect URL to prevent open redirect
    private boolean isValidLocalRedirect(String url, String contextPath) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            URL parsedUrl = new URL(url);
            // If it's an absolute URL, check if it's on the same host/port (not implemented here for simplicity)
            // For now, we'll allow relative paths or paths starting with context path
            if (parsedUrl.getHost() != null) {
                // It's an absolute URL, more checks needed for security (e.g. same host)
                // For this demo, let's be restrictive and only allow relative or context-path based.
                // This part needs to be more robust in a real application.
                // A simple check for local paths would be:
                return url.startsWith(contextPath) || (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("//"));

            }
            // It's a relative path, assume it's local
            return true;
        } catch (MalformedURLException e) {
            // Not a valid URL, could be a relative path
            return (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("//"));
        }
    }
}