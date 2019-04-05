<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>GOLAD</title>
        <meta charset="utf-8" />
        <link rel="icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLifeWebsite/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLifeWebsite/css/main.css" />
        <script type="text/javascript" src="scripts/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="scripts/main.js"></script>
    </head>
    <body onload="takeHighScores();">
        <div>
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
                <a href="/GameOfLifeWebsite/simulation.jsp" target="_blank">Start Conway's Game of Life simulation</a>
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
                    <!--<option>Smart AI</option>-->
                </select>
                <input id="startGame" type="button" value="Start Game of Life and Death" onclick="startGame();" />
                <a id="startGameLink" href="/GameOfLifeWebsite/game.jsp" target="_blank" style="display: none"></a>
                <br />
                <br />
                <h1>Training (not in yet)</h1>
                <ul>
                    <li>Play GOLAD to help train AIA (smart AI).</li>
                    <li>No high score will be taken from this game.</li>
                    <li>Good practice for you as well.</li>
                </ul>
                <a href="/GameOfLifeWebsite/training.jsp" target="_blank">Start training</a>
            </div>
            <div id="highScoresDiv">
                <h1>High Scores</h1>
                <p class="scores" id="HUMAN">human vs human: No one has beaten it yet</p>
                <p class="scores" id="DUMB_AI">human vs dumb AI: No one has beaten it yet</p>
                <p class="scores" id="AVERAGE_AI">human vs average AI: No one has beaten it yet</p>
                <p class="scores" id="SMART_AI">human vs smart AI: Will be added shortly...</p>
                <br />
                <br />
                <h1>Extras</h1>
                <a href="/GameOfLifeWebsite/about.jsp" target="_blank">Click here to learn more about the project</a>
            </div>
        </div>
    </body>
</html>
