function takeHighScores() {
    $.get('high scores.txt', function (scores) {
        if (scores) {
            scores = scores.replace(/\r?\n|\r/g, "");
            for(let i of scores.split(";"))
            {
                if (i) {
                    var score = i.split(",");
                    document.getElementById(score[0]).innerHTML = "human vs " + score[0] + ": " + score[1];
                }
            }
        }
    }, 'text');
}

function startGame() {
    var link = $("#startGameLink")[0];
    link.href = "/GameOfLifeWebsite/game.jsp?type=" + $("#player2")[0].selectedIndex;
    link.click();
}