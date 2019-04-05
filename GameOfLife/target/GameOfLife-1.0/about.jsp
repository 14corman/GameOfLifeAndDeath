<%-- 
    Document   : about
    Created on : Mar 15, 2017, 10:36:23 PM
    Author     : Cory Edwards
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="icon" href="/GameOfLife/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="/GameOfLife/favicon.ico" type="image/x-icon"> 
        <link rel="stylesheet" type="text/css" href="/GameOfLife/css/main.css" />
        <script type="text/javascript" src="/GameOfLife/script/jquery-3.1.1.min.js"></script>
        <script src="/GameOfLife/script/sigma.js"></script>
        <script src="/GameOfLife/script/sigmaGraph.js"></script>
        <title>GOLAD about</title>
    </head>
    <body onload="start()">
        <div id="main">
            <h1>About the project / website</h1>
            <ul>
                <li>The main focus of this project is to test the validity of a new algorithm I created called ADMEA (Artificial Intelligence Algorithm) by testing:
                    <ol>
                        <li>H<sub>0</sub>: p<sub>x player 2 type</sub> = p<sub>y player 2 type</sub></li>
                        <li>H<sub>0</sub>: &mu;<sub>x player 2 type</sub> = &mu;<sub>y player 2 type</sub></li>
                        <li>That ADMEA's score slowly increases over time to show that it learned.</li>
                    </ol>
                </li>
                <li>The results will tell whether ADMEA is faulty and needs reworked, or that ADMEA is ready for the next step.</li>
                <li>This website was built in order to collect game data of the AIs and human second player.</li>
                <li>AI types:
                    <ol>
                        <li>dumb AI: makes completely random moves</li>
                        <li>average AI: knows the rules of Conway's Game of Life, and looks at every possible move to make on every turn to make the move that will make the biggest impact.</li>
                        <li>smart AI: ADMEA</li>
                    </ol>
                </li>
                <li>The scores that are collected are calculated as:&nbsp;<b>&Sigma;(# of computer cells - # of player cells) &divide; # of moves took to end game</b></li>
                <li>If a player wins, their final cell # is sent and is checked to see if it is a high score.</li>
            </ul>
            <br />
            <br />
            <h1>Brief about ADMEA</h1>
            <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ADMEA is a culmination of 3 years of research, and a step to hopefully a more human like AI (Artificial Intelligence).If ADMEA succeeds in this game, then it will be put into a custom environment that</p>
            <p> will hopefully create that AI.ADMEA can be described as having control over Nodes. It keeps all of the Nodes held up in something that could be analogous to a jar.</p>
            <p>When ADMEA receives an abstract unique state, it finds the Node that holds that state, or makes one if it cannot find it.If a new Node</p>
            <p> is created, that Node then also has its children created as well as a score given to it based on the state that it has.A child of a Node is basically the next logical state. That means</p>
            <p> state step between themselves. States cannot go backwards though, so a parent cannot be the child of one of its children.</p>
            <p>When all of the Nodes are created, it then goes through and performs deterministic Q learning against all of the Nodes as a whole body.If a Node is not a child of</p>
            <p> another Node, then its score is 0. Each Node also has a probability of success (p), # of occurrence (n), # of successes (k). These culminate to predicting p = k / n. Then </p>
            <p>we calculate the binomial distribution value given those 3 parameters. Finally, when everything is said and done, the final step is to see which is the best child state to take based on the state given to it.</p>
            <p>This is done by going over every possible path for every child of the given state. Each path will have a score from: <b>&Pi;(Binomial pdf with variables p, n, k)<sub>i</sub> &times; &Sigma;(Q learning score)<sub>i</sub></b></p>
            <p>The winning path for each child is handed up to the parent, then the parent picks the highest score and chooses that child to be the state that is returned.</p>
            <br />
            <br />
            <h1>Acknowledgement</h1>
            <ul>
                <li>Thank you to
                    <ol>
                        <li>Dr. Calderhead for help with some of the statistical and math side.</li>
                        <li>Dr. Glasgow for helping in the concept and theory proving process.</li>
                        <li>Adam Klemann and the whole of Malone's Information Technology department for letting me show this site as well as giving means to collect, hold, and observe the data.</li>
                        <li>everyone that will help train ADMEA before the website goes public.</li>
                        <li>everyone that will play the game and help me in collecting data.</li>
                    </ol>
                </li>
            </ul>
            <br />
            <br />
            <h1>For programmers</h1>
            <p>Here is the documentation for the algorithm ADMEA I built that is currently running the smart AI.</p>
            <p>ADMEA javadoc coming soon!</p><!--<a href="/GameOfLife/javadoc/index.html" target="_blank">API documentation</a>-->
            <br />
            <a href="https://github.com/14corman/ADMEA" target="_blank">The github for the algorithm</a>
            <br />
            <br />
            <h1>Node graph</h1>
            <div class="tabcontent">
                <div id="nodePanel">
                    <label id="nodeId">Id:</label><br /><br />
                    <label id="nodeProbability">Probability:</label><br /><br />
                    <label id="nodeN">N:</label><br /><br />
                    <label id="nodeK">K:</label><br /><br />
                    <label id="nodeScore">Score:</label>
                </div>
                <div id="graph-container"></div>
            </div>
        </div>
    </body>
</html>
