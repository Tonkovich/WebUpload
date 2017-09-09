<#macro navbar optionalParam=userData>
<!-- Start Nav Bar -->
<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="/">File Upload</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a href="/">Home</a></li>
            <!-- Possibly hide some of these pages based on login -->
            <#if userData.isLoggedIn()>
                <li><a href="/portal">Portal</a></li>
            </#if>
        </ul>
        <!-- Start Login -->
        <ul class="nav navbar-nav navbar-right">
            <#if !userData.isLoggedIn()>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-user"></span> Login
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <form style="padding: 5px;" action="/login" method="post">
                                <div class="form-group">
                                    <label for="username">Username:</label>
                                    <input type="text" class="form-control" id="username" placeholder="Enter username"
                                           name="username">
                                </div>
                                <div class="form-group">
                                    <label for="password">Password:</label>
                                    <input type="password" class="form-control" id="password"
                                           placeholder="Enter password" name="password">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li>
                    <a href="/register">Register</a>
                </li>
            <#else>
                <li>
                    <a href="/logout">Logout</a>
                </li>
            </#if>
        </ul>
        <!-- End Login -->
    </div>
</nav>
<!-- End Nav Bar -->
</#macro>

<#macro display_page userData>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

    <!-- Bootstrap and jQuery JS files -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <@head/>
</head>
<body>
<div class="container">
    <@navbar userData/>
    <@content/>
    <img style="margin-left: 300px" src="/media/cloud.png">
</div>
</body>
</html>
</#macro>