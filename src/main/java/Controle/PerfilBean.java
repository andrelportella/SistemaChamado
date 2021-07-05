/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import DAO.GenericDAO;
import Modelo.Usuario;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import util.FacesUtil;
import util.Util;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class PerfilBean implements Serializable {

    private Usuario usuario = new Usuario();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_user = (Long) session.getAttribute("idUser");
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private boolean desabilitaSalvar = true;

    public PerfilBean() {
        try {
            usuario = new GenericDAO<>(Usuario.class).usuario(cod_user);
        } catch (Exception ex) {
            FacesUtil.addMsgFatalSQL(ex, mErro, "PerfilBean", "Construtor PerfilBean()");
            desabilitaSalvar = false;
            org.primefaces.context.RequestContext.getCurrentInstance().execute("funcao()");
            // Util.redirecionarHome();
        }
    }

    public void salvar() {
        new GenericDAO<>(Usuario.class).update(usuario);
        FacesUtil.addMsgInfo(mSucesso, "Salvo com Sucesso!!");
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isDesabilitaSalvar() {
        return desabilitaSalvar;
    }

    public void setDesabilitaSalvar(boolean desabilitaSalvar) {
        this.desabilitaSalvar = desabilitaSalvar;
    }

}
