var player2Type = window.location.search.substring(1).split("=")[1];
var currentPlayer = 1;
var training = false;
var timer = null;
var dotTimer;
var game;

function setupGame()
{
    var board = $("#gameBoard")[0];
    
    if(player2Type == 0)
        $("#player2Name")[0].innerHTML = "Player 2";
    else if(player2Type == 1)
        $("#player2Name")[0].innerHTML = "Dumb AI";
    else if(player2Type == 2)
        $("#player2Name")[0].innerHTML = "Average AI";
    else if(player2Type == 3)
        $("#player2Name")[0].innerHTML = "Smart AI";
    else
    {
        player2Type = 3;
        training = true;
        $("#player2Name")[0].innerHTML = "Smart AI training";
    }
    
    for(var i = 0; i < 10;)
    {
        var p = document.createElement("p");
        p.className = "cells";
        for(var j = 0; j < 10;)
        {
            var outer = document.createElement("div");
            outer.id = i.toString() + j.toString();
            outer.setAttribute("class", "out");
            outer.setAttribute("onclick", "makeMove(this);");
            outer.setAttribute("style", "background-color: #DDD");
            
            var inner = document.createElement("div");
            inner.setAttribute("class", "in");
            inner.setAttribute("style", "background-color: #DDD");
            
            outer.appendChild(inner);
            p.appendChild(outer);
            j++;
        }
        board.appendChild(p);
        i++;
    }
    $.post('/GameOfLife/GOLADGame', { setup: "true", player2: player2Type }, function(response)
    {
        game = response;
        $.post('/GameOfLife/GOLADGame', {paint : true, game : game}, function(data)
        {
            if(!data["player"])
                window.location = "/GameOfLife/access_denied.jsp";
            paintSquares(data["grid"], "setup");
            currentPlayer = data["player"];
            dotTimer = window.setInterval(function () { dots(); }, 500);
        });
    });
}

var numberOfDots = 0;
function dots()
{
    $("#2")[0].innerHTML = "";
    $("#1")[0].innerHTML = "";

    var dotString = "";

    if (numberOfDots >= 4)
        numberOfDots = 0;
    else
        numberOfDots++;
    
    for(var i = 0; i < numberOfDots;)
    {
        dotString += ".";
        i++;
    }

    $("#" + currentPlayer)[0].innerHTML = dotString;
}

function makeMove(element)
{
    var tempRow = element.id[0];
    var tempColumn = element.id[1];

    $.post('/GameOfLife/GOLADGame', { player: currentPlayer, move: "make", row: tempRow, column: tempColumn, game : game, training: training }, function(response)
    {
        game = response;
        $.post("/GameOfLife/GOLADGame", {paint : true, game : game}, function(data)
        {
            if(!data["player"])
                window.location = "/GameOfLife/access_denied.jsp";
            paintSquares(data["grid"], "make");
            currentPlayer = data["player"];
        });
    });
}

function applyMove()
{
    $.post('/GameOfLife/GOLADGame', { player: currentPlayer, move: "apply", game : game, training: training }, function(response)
    {
        game = response;
        $.post("/GameOfLife/GOLADGame", {paint : true, game : game}, function(data)
        {
            if(!data["player"])
                window.location = "/GameOfLife/access_denied.jsp";
            paintSquares(data["grid"], "apply");
            currentPlayer = data["player"];

            if (player2Type !== 0 && currentPlayer !== 1)
            {
                $.post("/GameOfLife/GOLADGame", {player: currentPlayer, move : "made", game : game}, function(response)
                {
                    game = response;
                    $.post("/GameOfLife/GOLADGame", {paint : true, game : game}, function(data)
                    {
                        if(!data["player"])
                            window.location = "/GameOfLife/access_denied.jsp";
                        paintSquares(data["grid"], "apply");
                        currentPlayer = data["player"];
                    });
                });
                
                if (timer == null)
                    timer = window.setInterval(function () { check(); }, 500);
            }
        });
    });
}

function GOLADUndo()
{
    $.post('/GameOfLife/GOLADGame', { player: currentPlayer, move: "undo", game : game, training: training }, function(response)
    {
        game = response;
        $.post("/GameOfLife/GOLADGame", {paint : true, game : game}, function(data)
        {
            if(!data["player"])
                window.location = "/GameOfLife/access_denied.jsp";
            paintSquares(data["grid"], "undo");
            currentPlayer = data["player"];
        });
    });
}

function check()
{
    if (currentPlayer !== 0)
    {
//        $.post('/GameOfLife/GOLADGame', { player: currentPlayer, move: "check", game : game }, function(response)
//        {
//            game = response;
//            $.post("/GameOfLife/GOLADGame", {paint : true, game : game}, function(data)
//            {
//                paintSquares(data["grid"], "check");
//                currentPlayer = data["player"];
//
//                if (currentPlayer == 1)
//                {
//                    window.clearInterval(timer);
//                    timer = null;
//                }
//            });
//        });
    }
}

function paintSquares(data, move)
{
    var player1Cells = 0;
    var player2Cells = 0;
    for(var i in data)
    {
        for(var j in data[i])
        {
            if (data[i][j]["out"] === "Red")
                player1Cells++;
            else if (data[i][j]["out"] === "Blue")
                player2Cells++;
            
            var outColor;
            var inColor;
            
            switch(data[i][j]["out"])
            {
                case "Original":
                    outColor = "#DDD";
                    break;
                case "Red":
                    outColor = "#ef0000";
                    break;
                case "LightRed":
                    outColor = "#FFC1C2";
                    break;
                case "Blue":
                    outColor = "#0065ff";
                    break;
                case "LightBlue":
                    outColor = "#80B3FF";
                    break;
                default:
                    break;
            }
            
            switch(data[i][j]["in"])
            {
                case "Original":
                    inColor = "#DDD";
                    break;
                case "Red":
                    inColor = "#ef0000";
                    break;
                case "Blue":
                    inColor = "#0065ff";
                    break;
                default:
                    break;
            }
            
            
            var parent = $("#" + i + j)[0];
            parent.setAttribute("style", "background-color: " + outColor);
            parent.getElementsByTagName('div')[0].setAttribute("style", "background-color: " + inColor);
        }
    }

    $("#player2")[0].innerHTML = "Cells=" + player2Cells;
    $("#player1")[0].innerHTML = "Cells=" + player1Cells;
    
    $('#gameBoard').hide().show(0);

    if (player2Cells == 0 && player1Cells == 0 && (move === "apply" || move === "check"))
    {
        $("#ending")[0].innerHTML = "<strong>It's a tie!</strong>";
        $("#ending")[0].style.display = "flex";
        currentPlayer = 0;
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
    else if (player2Cells == 0 && (move === "apply" || move === "check"))
    {
        $("#ending")[0].innerHTML = "<strong>Player 1 wins!</strong>";
        $("#ending")[0].style.display = "flex";
        currentPlayer = 0;
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
    else if (player1Cells == 0 && (move === "apply" || move === "check"))
    {
        if(player2Type == 0)
            $("#ending")[0].innerHTML = "<strong>Player 2 wins!</strong>";
        else if(player2Type == 1)
            $("#ending")[0].innerHTML = "<strong>Dumb AI wins!</strong>";
        else if(player2Type == 2)
            $("#ending")[0].innerHTML = "<strong>Average AI wins!</strong>";
        else if(player2Type == 3)
            $("#ending")[0].innerHTML = "<strong>Smart AI wins!</strong>";
        else
            $("#ending")[0].innerHTML = "<strong>Smart AI wins!</strong>";
        
        $("#ending")[0].style.display = "flex";
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        currentPlayer = 0;
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
}