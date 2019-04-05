<%-- 
    Document   : game
    Created on : Mar 4, 2017, 6:56:31 AM
    Author     : Cory Edwards
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>GOLAD game</title>
	<meta charset="utf-8" />
        <link rel="stylesheet" type="text/css" href="/Core/bootstrap-3.3.7-dist/css/bootstrap.min.css">	
        <link rel="icon" href="/GameOfLife/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLife/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLife/css/game.css" />
        <link rel="stylesheet" type="text/css" href="/Core/css/options.css">
        <script type="text/javascript" src="script/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="/Core/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="script/game script.js"></script>
        <script type="text/javascript" src="/Core/script/options.js"></script>
    </head>
    <body onload="setupGame();">
        <div class="upperDiv">
            <div class="dropdown" id="dropOpt"></div>
        </div>
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

