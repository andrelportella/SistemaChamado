package FiltroLogin;

import Controle.LoginBean;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.Util;

// @WebFilter
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        // Get the loginBean from session attribute
        try {
            // String contextPath;
            LoginBean loginBean = (LoginBean) ((HttpServletRequest) request).getSession().getAttribute("loginBean");

            // For the first application request there is no loginBean in the session so user needs to log in
            // For other requests loginBean is present but we need to check if user has logged in successfully
            if (loginBean.getLogin() == null) {
                String contextPath = ((HttpServletRequest) request).getContextPath();
                // HttpServletRequest requestx =  getWebRequestCycle().getWebRequest().getHttpServletRequest();
                System.out.println("Usuario deslogado, retirar essa validação, está em FiltroLogin.LoginFilter" + contextPath);

                ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
            }

            chain.doFilter(request, response);

        } catch (IOException | ServletException ex) {
            Util util = new Util();
            System.out.println("" + ex);
            util.redirecionarLogin();
        }

    }

    ServletRequest request;
    FilterChain chain;
    ServletResponse response;

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {

    }

}
