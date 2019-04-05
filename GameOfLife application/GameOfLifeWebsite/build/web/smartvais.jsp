<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>GOLAD</title>
        <meta charset="utf-8" />
        <script type="text/javascript" src="script/jquery-3.1.1.min.js"></script>
    </head>
    <body>
        <input type="button" value="start" onclick="startSimulation()" />
        <script>
            function startSimulation()
            {
                $.get('/GameOfLife/smart_v_ais');
            }
        </script>
    </body>
</html>
