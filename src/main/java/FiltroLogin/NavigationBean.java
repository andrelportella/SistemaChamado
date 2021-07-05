/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FiltroLogin;

/**
 *
 * @author ricardo
 */
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Simple navigation bean
 *
 * @author itcuties
 *
 */
@ManagedBean
@SessionScoped
public class NavigationBean implements Serializable {

    private static final long serialVersionUID = 1520318172495977648L;

    /**
     * Redirect to login page.
     *
     * @return Login page name.
     */
    public String redirectToLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    /**
     * Go to login page.
     *
     * @return Login page name.
     */
    public String toLogin() {
        return "login?faces-redirect=true";
    }

    /**
     * Redirect to info page.
     *
     * @return Info page name.
     */
    public String redirectToInfo() {
        return "/info.xhtml?faces-redirect=true";
    }

    /**
     * Go to info page.
     *
     * @return Info page name.
     */
    public String toInfo() {
        return "/info.xhtml";
    }

    /**
     * Redirect to welcome page.
     *
     * @return Welcome page name.
     */
    public String redirectToWelcome() {
        return "/home.xhtml?faces-redirect=true";
    }

    /**
     * Go to welcome page.
     *
     * @return Welcome page name.
     */
    public String toWelcome() {
        return "/home.xhtml";
    }

}
