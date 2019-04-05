<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>GOLAD</title>
        <meta charset="utf-8" />
        <link rel="stylesheet" type="text/css" href="/Core/bootstrap-3.3.7-dist/css/bootstrap.min.css">	 
        <link rel="icon" href="/GameOfLife/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLife/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLife/css/main.css" />
        <script type="text/javascript" src="script/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="/Core/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        <link rel="stylesheet" type="text/css" href="/Core/css/options.css">
        <script type="text/javascript" src="script/main.js"></script>
        <script type="text/javascript" src="/Core/script/options.js"></script>
    </head>
    <body onload="takeHighScores();">
        <div>
            <div class="upperDiv">
                <div class="dropdown" id="dropOpt"></div>
            </div>
            <div id="contentDiv">
                <h1>Conway's Game of Life</h1>
                <p>Conway&#39;s Game of Life is a simulation of cells being born and dying:</p>
                <ul>
                    <li>Each cell has 8 neighbors (right, left, top, bottom, upper left, upper right, lower left, lower right).</li>
                    <li>If a cell is alive and has either 2 or 3 live neighbors then it will live on to the next generation.</li>
                    <li>If the cell has less than 2 neighbors it "starves" and dies.</li>
                    <li>If it has more than 3 it dies from "overcrowding".</li>
                    <li>If a dead cell has exactly 3 live neighbors, then it gets reborn.</li>
                </ul>
                <br />
                <p>This is a demonstration of Conway's game of life, so you can understand what can happen before you play the game.</p>
                <a href="/GameOfLife/simulation.jsp" target="_blank">Start Conway's Game of Life simulation</a>
                <br />
                <br />
                <h1>Game of Life and Death (GOLAD)</h1>
                <p>Game of Life and Death is a 2 player game of Conway's game of life:</p>
                <ul>
                    <li>The concept of the game came from carykh found <a href="https://www.youtube.com/watch?v=JkGZ2Hl1l8c&t=18s" target="_blank">here</a></li>
                    <li>The goal is to get your opponent's cell count down to 0.</li>
                    <li>You and a player take turns doing 1 of 3 things.
                        <ol>
                            <li>Delete 1 of your cells.</li>
                            <li>Delete an opponent's cell.</li>
                            <li>Take a dead cell and make it become one of your cells. This will require you to delete 2 of your cells in the process.</li>
                        </ol>
                    </li>
                    <li>Each cell has an:
                        <ol>
                            <li>inner square color tells you what the cell will be in the next generation.</li>
                            <li>outer square color tells you what the cell is in the current generation.</li>
                        </ol>
                    </li>
                    <li>The # of cells is calculated based on the current generation.</li>
                    <li>When the game is over you can either refresh the page to play again or close the tab.</li>
                </ul>
                <p>Player 2:</p>
                <select id="player2">
                    <option>Human</option>
                    <option>Dumb AI</option>
                    <option>Average AI</option>
                    <option>Smart AI</option>
                </select>
                <input id="startGame" type="button" value="Start Game of Life and Death" onclick="startGame();" />
                <a id="startGameLink" href="/GameOfLife/game.jsp" target="_blank" style="display: none"></a>
                <br />
                <br />
                <h1>Training</h1>
                <ul>
                    <li>Play GOLAD to help train ADMEA (smart AI).</li>
                    <li>No high score will be taken from this game.</li>
                    <li>Good practice for you as well.</li>
                </ul>
                <a href="/GameOfLife/game.jsp?type=training" target="_blank">Start training</a>
            </div>
            <div id="highScoresDiv">
                <h1>High Scores</h1>
                <p class="scores" id="HUMAN">human vs human: No one has beaten it yet</p>
                <p class="scores" id="DUMB_AI">human vs dumb AI: No one has beaten it yet</p>
                <p class="scores" id="AVERAGE_AI">human vs average AI: No one has beaten it yet</p>
                <p class="scores" id="SMART_AI">human vs smart AI: No one has beaten it yet</p>
                <br />
                <br />
                <h1>Extras</h1>
                <a href="/GameOfLife/about.jsp" target="_blank">Click here to learn more about the project</a>
                <br />
                <br />
                <h1>Known bugs</h1>
                <p>There are 2 known bugs that happen <b>ONLY</b> to the Smart AI.</p>
                <ul>
                    <li>The Smart AI never takes its turn. <br />If you sit for over a minute and the Smart AI does not move then restart the game.</li>
                    <br />
                    <li>The Smart AI tried to add a square which shows up as a faded blue square, but then does nothing.<br /> This bug also means that you have to restart the game. </li>
                </ul>
                <br />
                <p>Other than these 2 bugs, you may encounter glitching and stuttering. If this happens then please wait for the website to process things before you click again.</p>
            </div>
        </div>
    </body>
</html>
