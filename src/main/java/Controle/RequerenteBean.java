/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Requerente;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;
import util.FacesUtil;
import util.Util;

/**
 *
 * @author ricardo
 */
@FacesValidator(value = "validarEmail")
@ManagedBean
@ViewScoped
public class RequerenteBean implements Serializable, Validator {

    private Requerente requerente = new Requerente();
    private Requerente editaRequerente = new Requerente();
    private Requerente requerenteSelecionado = new Requerente();
    Util util = new Util();
    PreparedStatement stmt;
    ResultSet rs;
    private Connection ConexaoSQL;
    private List<Requerente> requerentes = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO", sql;
    private boolean statusBotao;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    GerarCpfCnpj gerador = new GerarCpfCnpj();

    public RequerenteBean() throws ClassNotFoundException, SQLException {
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        requerentes = new GenericDAO<>(Requerente.class).listarRequerentes();
        //requerente.setCpf(gerador.cpf(true));
    }

    public void validaMail(String email) {
        boolean valid = true;
        if (email == null) {
            valid = false;
        } else if (!email.contains("@")) {
            valid = false;
        } else if (!email.contains(".")) {
            valid = false;
        } else if (email.contains(" ")) {
            valid = false;
        }
        if (!valid) {
            if (email.length() < 10) {
                FacesUtil.addMsgError("Erro", "Por favor, informe um email válido para o Fornecedor com no mínimo 10 caracteres.");
            } else {
                FacesUtil.addMsgError("Erro", "Email invalido!");
            }
            statusBotao = true;
        } else {
            statusBotao = false;
        }

    }

    public void status() throws SQLException, ClassNotFoundException {
        if (requerente.getCpf() != null) {
            verificaCPF();
        }

        if (requerente.getEmail() != null) {
            verificaEmail();
        }

    }

    public void verificaCPF() throws SQLException, ClassNotFoundException {

        if (gerador.isCPF(requerente.getCpf())) {
            try {
                //ConexaoSQL = new ConexaoSQLServer().getConnection();
                sql = " SELECT cpf FROM Requerente WHERE cpf=?  and cod_negocio =" + cod_negocio + " ";
                stmt = ConexaoSQL.prepareStatement(sql);
                stmt.setString(1, requerente.getCpf());
                rs = stmt.executeQuery();
                if (rs.next()) {
                    ////ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    FacesUtil.addMsgError("CPF Já cadastrado", "Erro");
                    statusBotao = true;
                    //return true;
                } else {
                    //ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    statusBotao = false;
                    //return false;
                }
            } catch (SQLException e) {
                FacesUtil.addMsgError(e.toString(), "Erro");
                statusBotao = false;
                //return false;
            }
        } else {
            FacesUtil.addMsgError("Erro", "CPF Inválido!");
            statusBotao = true;
            //return true;
        }

    }

    public void verificaEmail() throws SQLException, ClassNotFoundException {
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT email FROM Requerente WHERE email=? and cod_negocio =" + cod_negocio + " ";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, requerente.getEmail());
            rs = stmt.executeQuery();
            if (rs.next()) {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                FacesUtil.addMsgError("Email Já cadastrado", "Erro");
                statusBotao = true;
                //return true;
            } else {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                statusBotao = false;
                //return false;
            }
        } catch (SQLException e) {
            FacesUtil.addMsgError(e.toString(), "Erro");
            statusBotao = false;
            //return false;
        }

    }

    public void salvar() throws SQLException, ClassNotFoundException {
        if (!statusBotao) {
            new GenericDAO<>(Requerente.class).salvar(requerente);
            requerente = new Requerente();
            requerentes = new GenericDAO<>(Requerente.class).listarRequerentes();
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
            FacesUtil.addMsgInfo("Cadastrado com Sucesso!", "Cadastro");
        } else {
            // status();
            verificaCPF();
            verificaEmail();
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        }
    }

    public void editar(Requerente r) {
        editaRequerente = r;
    }

    public void editarRequerente() {
        new GenericDAO<>(Requerente.class).update(editaRequerente);
        editaRequerente = new Requerente();
        requerentes = new GenericDAO<>(Requerente.class).listarRequerentes();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Editado com Sucesso!", "Edição");

    }

    public Requerente getRequerente() {
        return requerente;
    }

    public void setRequerente(Requerente requerente) {
        this.requerente = requerente;
    }

    public Requerente getEditaRequerente() {
        return editaRequerente;
    }

    public void setEditaRequerente(Requerente editaRequerente) {
        this.editaRequerente = editaRequerente;
    }

    public Requerente getRequerenteSelecionado() {
        return requerenteSelecionado;
    }

    public void setRequerenteSelecionado(Requerente requerenteSelecionado) {
        this.requerenteSelecionado = requerenteSelecionado;
    }

    public List<Requerente> getRequerentes() {
        return requerentes;
    }

    public void setRequerentes(List<Requerente> requerentes) {
        this.requerentes = requerentes;
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

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

}
