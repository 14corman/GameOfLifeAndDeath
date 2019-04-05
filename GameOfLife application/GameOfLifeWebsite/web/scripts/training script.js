var currentPlayer = 1;
var timer = null;
var dotTimer;

function setupGame()
{
    var board = $("#gameBoard")[0];
    
    $("#player2Name")[0].innerHTML = "smart AI training";
    
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
    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ setup: "true", player2: 3 }),
        dataType: "json",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (response) {
            paintSquares(response["grid"], "setup");
            currentPlayer = response["player"];
            dotTimer = window.setInterval(function () { dots(); }, 500);
        }
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

    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ player: currentPlayer, move: "make", row: tempRow, column: tempColumn, training: true }),
        dataType: "json",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (response) {
            paintSquares(response["grid"], "make");
            currentPlayer = response["player"];
        }
    });
}

function applyMove()
{
    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ player: currentPlayer, move: "apply", training: true }),
        dataType: "json",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (response) {
            paintSquares(response["grid"], "apply");
            currentPlayer = response["player"];

            if (currentPlayer !== 1)
            {
                if (timer == null)
                    timer = window.setInterval(function () { check(); }, 500);
            }
        }
    });
}

function GOLADUndo()
{
    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ player: currentPlayer, move: "undo", training: true }),
        dataType: "json",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (response) {
            paintSquares(response["grid"], "undo");
            currentPlayer = response["player"];
        }
    });
}

function check()
{
    if (currentPlayer !== 0)
    {
        $.ajax({
            url: '/GameOfLifeWebsite/GOLADGame',
            type: 'POST',
            data: jQuery.param({ player: currentPlayer, move: "check", training: true }),
            dataType: "json",
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function (response) {
                paintSquares(response["grid"], "check");
                currentPlayer = response["player"];

                if (currentPlayer === 1)
                {
                    window.clearInterval(timer);
                    timer = null;
                }
            }
        });
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

    if (player2Cells === 0 && player1Cells === 0 && (move === "apply" || move === "check"))
    {
        $("#ending")[0].innerHTML = "<strong>It's a tie!</strong>";
        $("#ending")[0].style.display = "flex";
        currentPlayer = 0;
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
    else if (player2Cells === 0 && (move === "apply" || move === "check"))
    {
        $("#ending")[0].innerHTML = "<strong>Player 1 wins!</strong>";
        $("#ending")[0].style.display = "flex";
        currentPlayer = 0;
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
    else if (player1Cells === 0 && (move === "apply" || move === "check"))
    {
	$("#ending")[0].innerHTML = "<strong>Smart AI wins!</strong>";
		
        $("#ending")[0].style.display = "flex";
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        currentPlayer = 0;
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
}