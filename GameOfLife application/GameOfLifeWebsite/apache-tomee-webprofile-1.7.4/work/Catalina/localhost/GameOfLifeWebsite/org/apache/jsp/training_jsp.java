/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat (TomEE)/7.0.68 (1.7.4)
 * Generated at: 2017-07-09 17:14:16 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class training_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <title>smart AI training</title>\n");
      out.write("\t<meta charset=\"utf-8\" />\n");
      out.write("        <link rel=\"icon\" href=\"/GameOfLifeWebsite/favicon.ico\" type=\"image/x-icon\">\n");
      out.write("        <link rel=\"shortcut icon\" href=\"/GameOfLifeWebsite/favicon.ico\" type=\"image/x-icon\"> \n");
      out.write("        <link rel=\"stylesheet\" type=\"text/css\" href=\"/GameOfLifeWebsite/css/training.css\" />\n");
      out.write("        <script type=\"text/javascript\" src=\"scripts/jquery-3.1.1.min.js\"></script>\n");
      out.write("        <script type=\"text/javascript\" src=\"scripts/training script.js\"></script>\n");
      out.write("    </head>\n");
      out.write("    <body onload=\"setupGame();\">\n");
      out.write("        <div id=\"ending\"></div>\n");
      out.write("        <div id=\"gameBoard\"></div>\n");
      out.write("        <div id=\"menuContainer\">\n");
      out.write("            <input id=\"Button1\" type=\"button\" onclick=\"applyMove();\" value=\"Submit move\" />\n");
      out.write("            <div style=\"height: 33%; width: 100%;\">\n");
      out.write("                <div class=\"playerTile\" style=\"background-color: #ef0000\">\n");
      out.write("                    <p class=\"player\">Player 1</p>\n");
      out.write("                    <p id=\"1\" class=\"player\" style=\"font-size: 400%\"></p>\n");
      out.write("                    <br />\n");
      out.write("                    <p id=\"player1\" class=\"player\">Cells=</p>\n");
      out.write("                </div>\n");
      out.write("                <div class=\"playerTile\" style=\"background-color: #0065ff; width: 51%\">\n");
      out.write("                    <p id=\"player2Name\" class=\"player\">Player 2</p>\n");
      out.write("                    <p id=\"2\" class=\"player\" style=\"font-size: 400%\"></p>\n");
      out.write("                    <br />\n");
      out.write("                    <p id=\"player2\" class=\"player\">Cells=</p>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            <input id=\"Button2\" type=\"button\" onclick=\"GOLADUndo();\" value=\"Undo move\" />\n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
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
