<%-- 
    Document   : training
    Created on : Mar 16, 2017, 6:31:53 AM
    Author     : cjedwards1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>smart AI training</title>
	<meta charset="utf-8" />
        <link rel="icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLifeWebsite/css/training.css" />
        <script type="text/javascript" src="scripts/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="scripts/training script.js"></script>
    </head>
    <body onload="setupGame();">
        <div id="ending"></div>
        <div id="gameBoard"></div>
        <div id="menuContainer">
            <input id="Button1" type="button" onclick="applyMove();" value="Submit move" />
            <div style="height: 33%; width: 100%;">
                <div class="playerTile" style="background-color: #ef0000">
                    <p class="player">Player 1</p>
                    <p id="1" class="player" style="font-size: 400%"></p>
                    <br />
                    <p id="player1" class="player">Cells=</p>
                </div>
                <div class="playerTile" style="background-color: #0065ff; width: 51%">
                    <p id="player2Name" class="player">Player 2</p>
                    <p id="2" class="player" style="font-size: 400%"></p>
                    <br />
                    <p id="player2" class="player">Cells=</p>
                </div>
            </div>
            <input id="Button2" type="button" onclick="GOLADUndo();" value="Undo move" />
        </div>
    </body>
</html>
