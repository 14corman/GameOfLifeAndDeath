/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat (TomEE)/7.0.68 (1.7.4)
 * Generated at: 2017-03-31 01:47:10 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private volatile javax.el.ExpressionFactory _el_expressionfactory;
  private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public javax.el.ExpressionFactory _jsp_getExpressionFactory() {
    if (_el_expressionfactory == null) {
      synchronized (this) {
        if (_el_expressionfactory == null) {
          _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
        }
      }
    }
    return _el_expressionfactory;
  }

  public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {
    if (_jsp_instancemanager == null) {
      synchronized (this) {
        if (_jsp_instancemanager == null) {
          _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
        }
      }
    }
    return _jsp_instancemanager;
  }

  public void _jspInit() {
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("    <head>\r\n");
      out.write("        <title>GOLAD</title>\r\n");
      out.write("        <meta charset=\"utf-8\" />\r\n");
      out.write("        <link rel=\"icon\" href=\"/GameOfLifeWebsite/favicon.ico\" type=\"image/x-icon\">\r\n");
      out.write("        <link rel=\"shortcut icon\" href=\"/GameOfLifeWebsite/favicon.ico\" type=\"image/x-icon\"> \r\n");
      out.write("        <link rel=\"stylesheet\" type=\"text/css\" href=\"/GameOfLifeWebsite/css/main.css\" />\r\n");
      out.write("        <script type=\"text/javascript\" src=\"scripts/jquery-3.1.1.min.js\"></script>\r\n");
      out.write("        <script type=\"text/javascript\" src=\"scripts/main.js\"></script>\r\n");
      out.write("    </head>\r\n");
      out.write("    <body onload=\"takeHighScores();\">\r\n");
      out.write("        <div>\r\n");
      out.write("            <div id=\"contentDiv\">\r\n");
      out.write("                <h1>Conway's Game of Life</h1>\r\n");
      out.write("                <p>Conway&#39;s Game of Life is a simulation of cells being born and dying:</p>\r\n");
      out.write("                <ul>\r\n");
      out.write("                    <li>Each cell has 8 neighbors (right, left, top, bottom, upper left, upper right, lower left, lower right).</li>\r\n");
      out.write("                    <li>If a cell is alive and has either 2 or 3 live neighbors then it will live on to the next generation.</li>\r\n");
      out.write("                    <li>If the cell has less than 2 neighbors it \"starves\" and dies.</li>\r\n");
      out.write("                    <li>If it has more than 3 it dies from \"overcrowding\".</li>\r\n");
      out.write("                    <li>If a dead cell has exactly 3 live neighbors, then it gets reborn.</li>\r\n");
      out.write("                </ul>\r\n");
      out.write("                <br />\r\n");
      out.write("                <p>This is a demonstration of Conway's game of life, so you can understand what can happen before you play the game.</p>\r\n");
      out.write("                <a href=\"/GameOfLifeWebsite/simulation.jsp\" target=\"_blank\">Start Conway's Game of Life simulation</a>\r\n");
      out.write("                <br />\r\n");
      out.write("                <br />\r\n");
      out.write("                <h1>Game of Life and Death (GOLAD)</h1>\r\n");
      out.write("                <p>Game of Life and Death is a 2 player game of Conway's game of life:</p>\r\n");
      out.write("                <ul>\r\n");
      out.write("                    <li>The concept of the game came from carykh found <a href=\"https://www.youtube.com/watch?v=JkGZ2Hl1l8c&t=18s\" target=\"_blank\">here</a></li>\r\n");
      out.write("                    <li>The goal is to get your opponent's cell count down to 0.</li>\r\n");
      out.write("                    <li>You and a player take turns doing 1 of 3 things.\r\n");
      out.write("                        <ol>\r\n");
      out.write("                            <li>Delete 1 of your cells.</li>\r\n");
      out.write("                            <li>Delete an opponent's cell.</li>\r\n");
      out.write("                            <li>Take a dead cell and make it become one of your cells. This will require you to delete 2 of your cells in the process.</li>\r\n");
      out.write("                        </ol>\r\n");
      out.write("                    </li>\r\n");
      out.write("                    <li>Each cell has an:\r\n");
      out.write("                        <ol>\r\n");
      out.write("                            <li>inner square color tells you what the cell will be in the next generation.</li>\r\n");
      out.write("                            <li>outer square color tells you what the cell is in the current generation.</li>\r\n");
      out.write("                        </ol>\r\n");
      out.write("                    </li>\r\n");
      out.write("                    <li>The # of cells is calculated based on the current generation.</li>\r\n");
      out.write("                    <li>When the game is over you can either refresh the page to play again or close the tab.</li>\r\n");
      out.write("                </ul>\r\n");
      out.write("                <p>Player 2:</p>\r\n");
      out.write("                <select id=\"player2\">\r\n");
      out.write("                    <option>Human</option>\r\n");
      out.write("                    <option>Dumb AI</option>\r\n");
      out.write("                    <option>Average AI</option>\r\n");
      out.write("                    <!--<option>Smart AI</option>-->\r\n");
      out.write("                </select>\r\n");
      out.write("                <input id=\"startGame\" type=\"button\" value=\"Start Game of Life and Death\" onclick=\"startGame();\" />\r\n");
      out.write("                <a id=\"startGameLink\" href=\"/GameOfLifeWebsite/game.jsp\" target=\"_blank\" style=\"display: none\"></a>\r\n");
      out.write("                <br />\r\n");
      out.write("                <br />\r\n");
      out.write("                <h1>Training (not in yet)</h1>\r\n");
      out.write("                <ul>\r\n");
      out.write("                    <li>Play GOLAD to help train AIA (smart AI).</li>\r\n");
      out.write("                    <li>No high score will be taken from this game.</li>\r\n");
      out.write("                    <li>Good practice for you as well.</li>\r\n");
      out.write("                </ul>\r\n");
      out.write("                <a href=\"/GameOfLifeWebsite/training.jsp\" target=\"_blank\">Start training</a>\r\n");
      out.write("            </div>\r\n");
      out.write("            <div id=\"highScoresDiv\">\r\n");
      out.write("                <h1>High Scores</h1>\r\n");
      out.write("                <p class=\"scores\" id=\"HUMAN\">human vs human: No one has beaten it yet</p>\r\n");
      out.write("                <p class=\"scores\" id=\"DUMB_AI\">human vs dumb AI: No one has beaten it yet</p>\r\n");
      out.write("                <p class=\"scores\" id=\"AVERAGE_AI\">human vs average AI: No one has beaten it yet</p>\r\n");
      out.write("                <p class=\"scores\" id=\"SMART_AI\">human vs smart AI: Will be added shortly...</p>\r\n");
      out.write("                <br />\r\n");
      out.write("                <br />\r\n");
      out.write("                <h1>Extras</h1>\r\n");
      out.write("                <a href=\"/GameOfLifeWebsite/about.jsp\" target=\"_blank\">Click here to learn more about the project</a>\r\n");
      out.write("            </div>\r\n");
      out.write("        </div>\r\n");
      out.write("    </body>\r\n");
      out.write("</html>\r\n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
