/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import DAO.GenericDAO;
import Modelo.Cliente;
import Modelo.Embarcacao_Loja;
import Modelo.Local_Requerimento;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import util.FacesUtil;
import util.Util;

/**
 *
 * @author ricardo
 */
@FacesValidator(value = "validarEmail")
@ManagedBean
@ViewScoped
public class LocalBean implements Serializable, Validator {

    private Local_Requerimento local = new Local_Requerimento();
    private Local_Requerimento localSelecionado = new Local_Requerimento();
    private Local_Requerimento editaLocal = new Local_Requerimento();
    Util util = new Util();
    //PreparedStatement stmt;
    //ResultSet rs;
    //private Connection ConexaoSQL;
    private List<Local_Requerimento> locais = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO", sql;
    private List<Cliente> clientes = new ArrayList<>();

    public LocalBean() {
        locais = new GenericDAO<>(Local_Requerimento.class).listarLocais();
    }

    public void salvar() {
        new GenericDAO<>(Local_Requerimento.class).salvar(local);
        local = null;
        locais = new GenericDAO<>(Local_Requerimento.class).listarLocais();
        //util.redirecionarRequerimento("cadastros/locaisCadastrados");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        FacesUtil.addMsgInfo("Cadastrado com Sucesso!", "Cadastro");
    }

    public void editar(Local_Requerimento r) {
        editaLocal = r;
    }

    public void editarLocal() {
        new GenericDAO<>(Local_Requerimento.class).update(editaLocal);
        local = null;
        locais = new GenericDAO<>(Local_Requerimento.class).listarLocais();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Editado com Sucesso!", "Editar");

    }

    public Local_Requerimento getLocal() {
        return local;
    }

    public void setLocal(Local_Requerimento local) {
        this.local = local;
    }

    public Local_Requerimento getLocalSelecionado() {
        return localSelecionado;
    }

    public void setLocalSelecionado(Local_Requerimento localSelecionado) {
        this.localSelecionado = localSelecionado;
    }

    public Local_Requerimento getEditaLocal() {
        return editaLocal;
    }

    public void setEditaLocal(Local_Requerimento editaLocal) {
        this.editaLocal = editaLocal;
    }

    public List<Local_Requerimento> getLocais() {
        return locais;
    }

    public void setLocais(List<Local_Requerimento> locais) {
        this.locais = locais;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String email = String.valueOf(value);
        boolean valid = true;
        if (value == null) {
            valid = false;
        } else if (!email.contains("@")) {
            valid = false;
        } else if (!email.contains(".")) {
            valid = false;
        } else if (email.contains(" ")) {
            valid = false;
        }
        if (!valid) {
            FacesUtil.addMsgError("Email invalido!", "Erro");
            //throw new ValidatorException();
        }
    }

}
