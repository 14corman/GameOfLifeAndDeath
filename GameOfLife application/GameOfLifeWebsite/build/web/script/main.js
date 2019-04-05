function takeHighScores() 
{
    $.get("/GameOfLife/GOLADGame", function(scores) 
    {
        if(typeof scores["HUMAN"] !== 'undefined')
            $("#HUMAN")[0].innerHTML = "human vs human: " + scores["HUMAN"] + " cells";
        
        if(typeof scores["DUMB_AI"] !== 'undefined')
            $("#DUMB_AI")[0].innerHTML = "human vs dumb AI: " + scores["DUMB_AI"] + " cells";
        
        if(typeof scores["AVERAGE_AI"] !== 'undefined')
            $("#AVERAGE_AI")[0].innerHTML = "human vs average AI: " + scores["AVERAGE_AI"] + " cells";
        
        if(typeof scores["SMART_AI"] !== 'undefined')
            $("#SMART_AI")[0].innerHTML = "human vs smart AI: " + scores["SMART_AI"] + " cells";
    }, "json");
}

function startGame() 
{
    var link = $("#startGameLink")[0];
    link.href = "/GameOfLife/game.jsp?type=" + $("#player2")[0].selectedIndex;
    link.click();
}