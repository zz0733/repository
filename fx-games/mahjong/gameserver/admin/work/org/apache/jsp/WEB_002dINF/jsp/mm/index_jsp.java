/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.47
 * Generated at: 2017-03-14 07:19:34 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.WEB_002dINF.jsp.mm;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
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
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\r');
      out.write('\n');
request.setAttribute("ctx", request.getContextPath());
      out.write("\r\n");
      out.write("<!-- 后台管理主页面 -->\r\n");
      out.write("<div class=\"manage_content mod_cdb\">\r\n");
      out.write("\t<!-- 左边导航开始 -->\r\n");
      out.write("\t<div class=\"mod_slidenav\" id=\"slide_nav\">\r\n");
      out.write("\t\t<h3><div><em>后台管理</em></div></h3>\r\n");
      out.write("\t\t<div class=\"slidenav_area\">\r\n");
      out.write("\t\t\t<ul>\r\n");
      out.write("\t\t\t\t<li>\r\n");
      out.write("                \t<a href=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${ctx}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("/mm/platform/list.do\">平台管理<i class=\"ico ico_arrowright\"></i></a>\r\n");
      out.write("            \t</li>\r\n");
      out.write("            \t<li>\r\n");
      out.write("                \t<a href=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${ctx}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("/mm/user/list.do\">账号管理<i class=\"ico ico_arrowright\"></i></a>\r\n");
      out.write("            \t</li>\r\n");
      out.write("            \t<li>\r\n");
      out.write("                \t<a href=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${ctx}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("/mm/role/list.do\">权限管理<i class=\"ico ico_arrowright\"></i></a>\r\n");
      out.write("            \t</li>\r\n");
      out.write("            \t<li>\r\n");
      out.write("                \t<a href=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${ctx}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("/mm/operationLog/list.do?isSearch=false\">操作日志<i class=\"ico ico_arrowright\"></i></a>\r\n");
      out.write("            \t</li>\r\n");
      out.write("        \t</ul>\r\n");
      out.write("    \t</div>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<!-- 左边导航结束啦 -->\r\n");
      out.write("\t<div class=\"mod_slidenav slidenav_min\" id=\"slide_nav_min\" style=\"display:none;\">\r\n");
      out.write("\t\t<a href=\"javascript:void(0);\" id=\"btn_show_left_nav\" class=\"slidenav_btn_op\" title=\"点击展开\"></a>\r\n");
      out.write("\t</div>\r\n");
      out.write("    \r\n");
      out.write("    <!--运营系统主页面-->\r\n");
      out.write("    <div class=\"mod_content\" id=\"mod_content_wrapper\">\r\n");
      out.write("        <a class=\"btn_op\" href=\"javascript:void(0);\" id=\"btn_hide_left_nav\" title=\"点击收起\"><span class=\"visually_hidden\">openclose</span></a>\r\n");
      out.write("\t\t<div class=\"content_main\" style=\"height: 800px\">\r\n");
      out.write("\t\t\t<div class=\"title_area\">\r\n");
      out.write("\t\t\t\t<h2>后台管理</h2>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("        </div>\r\n");
      out.write("    </div>\r\n");
      out.write("</div>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("$(function(){\r\n");
      out.write("\t// AJAX去加载页面, 显示加载图片，加载目标页面，隐藏加载图片\r\n");
      out.write("\t$(\"div.slidenav_area ul li a\").click(function(){\r\n");
      out.write("\t\tif($(this).hasClass('parentmenu')){\r\n");
      out.write("\t\t\tif(!$(this).parent().hasClass(\"selected\")){\r\n");
      out.write("\t\t\t\t$(this).parent().parent().find(\".selected\")\t.removeClass(\"selected\");\r\n");
      out.write("\t\t\t}\r\n");
      out.write("\t\t\t$(this).parent().toggleClass(\"selected\");\r\n");
      out.write("\t\t\t//$(this).parent().addClass(\"selected\");\r\n");
      out.write("\t\t}else{\r\n");
      out.write("\t\t\t$(this).parent().parent().find(\".selected\")\t.removeClass(\"selected\");\r\n");
      out.write("\t\t\t$(this).parent().addClass(\"selected\");\r\n");
      out.write("\t\t\t$(\".content_main\").load($(this).attr('href'));\r\n");
      out.write("\t\t}\r\n");
      out.write("\t\treturn false;\r\n");
      out.write("\t});\r\n");
      out.write("\t\r\n");
      out.write("\t$(\"#btn_show_left_nav\").click(function(){\r\n");
      out.write("\t\t$('div[id=slide_nav]').show();\r\n");
      out.write("\t\t$('div[id=slide_nav_min]').hide();\r\n");
      out.write("\t\t$('#btn_hide_left_nav').show();\r\n");
      out.write("\t\t$('div[id=mod_content_wrapper]').css(\"margin-left\", 210);\r\n");
      out.write("\t});\r\n");
      out.write("\t$('#btn_hide_left_nav').click(function(){\r\n");
      out.write("\t\t$(this).hide();\r\n");
      out.write("\t\t$('div[id=slide_nav]').hide();\r\n");
      out.write("\t\t$('div[id=slide_nav_min]').show();\r\n");
      out.write("\t\t$('div[id=mod_content_wrapper]').css(\"margin-left\", 12);\r\n");
      out.write("\t});\r\n");
      out.write("});\r\n");
      out.write("</script>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
