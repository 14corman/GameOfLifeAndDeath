<%-- 
    Document   : simulation
    Created on : Mar 4, 2017, 5:36:30 AM
    Author     : Cory Edwards
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Conway's GoL</title>
        <meta charset="utf-8" />
        <link rel="icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLifeWebsite/css/simulation.css" />
        <script type="text/javascript" src="scripts/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="scripts/simulation script.js"></script>
    </head>
    <body onload="start();">
        <div id="holder">
        </div>
        <input type="button" onclick="toggle()" value="Toggle cell borders" />
    </body>
</html>

