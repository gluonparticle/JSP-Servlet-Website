<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); %>

<!DOCTYPE html>
<html lang="en">

<head>
    <title>Login</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/png" href="<c:url value='/static/images/logo.png'/>"/>

    <%-- CSS files specific to login/register page template --%>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/bootstrap/css/bootstrap.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/fonts/font-awesome-4.7.0/css/font-awesome.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/fonts/Linearicons-Free-v1.0.0/icon-font.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/animate/animate.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/css-hamburgers/hamburgers.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/animsition/css/animsition.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/select2/select2.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/vendor/daterangepicker/daterangepicker.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/util.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/main.css'/>">

    <%-- Common CSS files (if these are also used by login page, otherwise they might be redundant here if head.jsp isn't included) --%>
    <%-- If login.jsp is standalone and doesn't include templates/head.jsp, then these are needed. --%>
    <%-- If it DOES include templates/head.jsp, then these are redundant here and should be removed from login.jsp's head. --%>
    <%-- Assuming for now it's standalone for CSS based on its original structure: --%>
    <link rel="stylesheet" href="<c:url value='/static/css/bootstrap.min.css'/>"> <%-- Potentially redundant if also in vendor above --%>
    <link rel="stylesheet" href="<c:url value='/static/css/magnific-popup.css'/>">
    <link rel="stylesheet" href="<c:url value='/static/css/jquery-ui.css'/>">
    <link rel="stylesheet" href="<c:url value='/static/css/owl.carousel.min.css'/>">
    <link rel="stylesheet" href="<c:url value='/static/css/owl.theme.default.min.css'/>">
    <link rel="stylesheet" href="<c:url value='/static/css/aos.css'/>">
    <link rel="stylesheet" href="<c:url value='/static/css/style.css'/>">
</head>

<body>
<div class="limiter">
    <div class="container-login100">
        <div class="shadow-lg p-2 p-lg-5 rounded" data-aos="fade-up">
            <div class="wrap-login100 p-t-50 p-b-90">
                <form action="<c:url value='/login?status=typed'/>" method="post" class="login100-form validate-form flex-sb flex-w">
                    <span class="login100-form-title p-b-51">
                        Login
                    </span>

                    ${alert}

                    <div class="wrap-input100 validate-input m-b-16" data-validate="Username is required">
                        <input class="input100" type="text" name="username" placeholder="Username">
                    </div>

                    <div class="wrap-input100 validate-input m-b-16" data-validate="Password is required">
                        <input class="input100" type="password" name="password" placeholder="Password">
                    </div>

                    <div class="flex-sb-m w-full p-t-3 p-b-24">
                        <div class="contact100-form-checkbox">
                            <input class="input-checkbox100" id="ckb1" type="checkbox" name="remember-me-checkbox">
                            <label class="label-checkbox100" for="ckb1">
                                Remember me
                            </label>
                        </div>

                        <div>
                            <a href="#" class="txt1">
                                Forgot?
                            </a>
                        </div>
                    </div>

                    <div class="container-login100-form-btn m-t-17">
                        <button type="submit" class="login100-form-btn">
                            Login
                        </button>
                    </div>
                </form>
            </div>

            <div class="text-center">
                <p class="txt1" style="color: #999999">
                    Don't have an account?
                    <a href="<c:url value='/register.jsp'/>" class="txt1">
                        Create here
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>

<div id="dropDownSelect1"></div>

<script src="<c:url value='/static/vendor/jquery/jquery-3.2.1.min.js'/>"></script>
<script src="<c:url value='/static/vendor/animsition/js/animsition.min.js'/>"></script>
<script src="<c:url value='/static/vendor/bootstrap/js/popper.js'/>"></script>
<script src="<c:url value='/static/vendor/bootstrap/js/bootstrap.min.js'/>"></script>
<script src="<c:url value='/static/vendor/select2/select2.min.js'/>"></script>
<script src="<c:url value='/static/vendor/daterangepicker/moment.min.js'/>"></script>
<script src="<c:url value='/static/vendor/daterangepicker/daterangepicker.js'/>"></script>
<script src="<c:url value='/static/vendor/countdowntime/countdowntime.js'/>"></script>

<%-- Common JS files (if these are also used by login page, otherwise they might be redundant here if scripts.jsp isn't included) --%>
<%-- Assuming for now it's standalone for JS based on its original structure: --%>
<script src="<c:url value='/static/js/jquery-3.3.1.min.js'/>"></script> <%-- Potentially redundant --%>
<script src="<c:url value='/static/js/jquery-ui.js'/>"></script>
<script src="<c:url value='/static/js/popper.min.js'/>"></script> <%-- Potentially redundant --%>
<script src="<c:url value='/static/js/bootstrap.min.js'/>"></script> <%-- Potentially redundant --%>
<script src="<c:url value='/static/js/owl.carousel.min.js'/>"></script>
<script src="<c:url value='/static/js/jquery.magnific-popup.min.js'/>"></script>
<script src="<c:url value='/static/js/aos.js'/>"></script>
<script src="<c:url value='/static/js/main.js'/>"></script>
</body>
</html>