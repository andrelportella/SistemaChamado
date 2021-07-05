/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import util.Util;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        synchronized (this) {
            HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
            System.out.println("Passou aqui:2");
            System.out.println("Passou aqui:2");
            System.out.println(".0." + session.getMaxInactiveInterval());
            System.out.println(session.getCreationTime());
            System.out.println(session.getLastAccessedTime());
            System.out.println(session.getAttribute("idTec"));
            System.out.println(session.getAttribute("userName"));
            System.out.println(session.getAttribute("idNeg"));
            //session.getAttribute(string)
            System.out.println(session.getServletContext().getSessionCookieConfig());
            System.out.println(session.getServletContext().getServletContextName());
            System.out.println(session.getServletContext().getServerInfo());
            //session.getServletContext().get

        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        synchronized (this) {
            try {
                System.out.println("Passou aqui:1");
                Util util = new Util();
                util.redirecionarLogin();
            } catch (IOException ex) {
                Logger.getLogger(SessionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        HttpSession session = event.getSession();
    }
}
