package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Fornecedor;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

import util.FacesUtil;
import util.Util;

@ManagedBean
@ViewScoped
public class FornecedorBean implements Serializable {

    private static final long serialVersionUID = -7263592628030475398L;
    private List<Fornecedor> fornecedores = new ArrayList<>();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Fornecedor fornecedor = new Fornecedor();
    private Connection ConexaoSQL;
    Util util = new Util();
    private Fornecedor fornecedorSelecionado = new Fornecedor();
    private Fornecedor editaFornecedor = new Fornecedor();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private boolean statusBotao;
    private String tipo = "";

    public FornecedorBean() {
        fornecedores = new GenericDAO<>(Fornecedor.class).listarFornecedores();
        list = Arrays.asList(true, true, true, true, true, false, false);
        tipoMask();
    }

    public void tipoMask() {
        
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }
    private List<Boolean> list;

    public void editarFornecedor(Fornecedor fornecedor) {
        this.editaFornecedor = fornecedor;
    }

    public boolean verificaFornecedor(Fornecedor f) {
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = "SELECT cnpj FROM Fornecedor WHERE cnpj=? and cod_negocio=" + cod_negocio + " ";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, f.getCnpj());
            rs = stmt.executeQuery();
            if (rs.next()) {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return true;
            } else {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FornecedorBean", "verificaFornecedor");
            System.out.println(" public boolean verificaFornecedor(Fornecedor f) Erro:" + e);
            return false;
        }

    }

    public void salvar() {
        if (!verificaFornecedor(fornecedor)) {            
            new GenericDAO<>(Fornecedor.class).salvar(fornecedor);
            fornecedor = new Fornecedor();
            fornecedores = new GenericDAO<>(Fornecedor.class).listarFornecedores();
            FacesUtil.addMsgInfo("Sucesso", "Fornecedor cadastrado com sucesso!");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        } else {
            FacesUtil.addMsgError("Fornecedor Já Cadastrado!", "Erro");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");

        }
    }

    public void editar() {
        new GenericDAO<>(Fornecedor.class).salvar(editaFornecedor);
        editaFornecedor = new Fornecedor();
        fornecedores = new GenericDAO<>(Fornecedor.class).listarFornecedores();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Fornecedor editado com sucesso!");
    }

    public Connection getConexaoSQL() {
        return ConexaoSQL;
    }

    public void setConexaoSQL(Connection ConexaoSQL) {
        this.ConexaoSQL = ConexaoSQL;
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

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<Fornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Fornecedor> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public Fornecedor getFornecedorSelecionado() {
        return fornecedorSelecionado;
    }

    public void setFornecedorSelecionado(Fornecedor fornecedorSelecionado) {
        this.fornecedorSelecionado = fornecedorSelecionado;
    }

    public Fornecedor getEditaFornecedor() {
        return editaFornecedor;
    }

    public void setEditaFornecedor(Fornecedor editaFornecedor) {
        this.editaFornecedor = editaFornecedor;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
