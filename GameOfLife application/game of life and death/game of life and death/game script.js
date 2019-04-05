var player2Type = window.location.search.substring(1).split("=")[1];
var currentPlayer = 1;
var timer = null;
var dotTimer;

function setupGame()
{
    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ setup: "true", player2: player2Type }),
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

    console.log("row: " + tempRow);
    console.log("column: " + tempColumn);

    $.ajax({
        url: '/GameOfLifeWebsite/GOLADGame',
        type: 'POST',
        data: jQuery.param({ player: currentPlayer, move: "make", row: tempRow, column: tempColumn }),
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
        data: jQuery.param({ player: currentPlayer, move: "apply" }),
        dataType: "json",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (response) {
            paintSquares(response["grid"], "apply");
            currentPlayer = response["player"];

            if (player2Type != 0 && currentPlayer !== 1)
            {
                console.log(player2Type);
                console.log(currentPlayer);
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
        data: jQuery.param({ player: currentPlayer, move: "undo" }),
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
            data: jQuery.param({ player: currentPlayer, move: "check" }),
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
            
            var parent = $("#" + i + j)[0];
            parent.className = "button" + data[i][j]["out"];
            parent.getElementsByTagName('div')[0].className = "innerButton" + data[i][j]["in"];
        }
    }

    $("#player2")[0].innerHTML = "Cells=" + player2Cells;
    $("#player1")[0].innerHTML = "Cells=" + player1Cells;

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
        $("#ending")[0].innerHTML = "<strong>Player 2 wins!</strong>";
        $("#ending")[0].style.display = "flex";
        window.clearInterval(dotTimer);
        window.clearInterval(timer);
        currentPlayer = 0;
        $("#2")[0].innerHTML = "";
        $("#1")[0].innerHTML = "";
    }
}