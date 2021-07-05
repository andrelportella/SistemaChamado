/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import Conexao.ConexaoSRVSistema;
import DAO.GenericDAO;
import Modelo.Cliente;
import Modelo.Embarcacao_Loja;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
public class EmbarcaoLojaBean implements Serializable, Validator {

    private Embarcacao_Loja embarcacaoLoja = new Embarcacao_Loja();
    private Embarcacao_Loja editaEmbarcacaoLoja = new Embarcacao_Loja();
    private Embarcacao_Loja embarcacaoLojaSelecionado = new Embarcacao_Loja();
    Util util = new Util();
    PreparedStatement stmt;
    PreparedStatement stmt2;
    ResultSet rs;
    ResultSet rs2;
    private Connection ConexaoSRVSistema;
    private Connection ConexaoSQL;
    private List<Embarcacao_Loja> embarcacaoLojas = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO", sql;
    private List<Cliente> clientes = new ArrayList<>();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private boolean renderizador;

    public EmbarcaoLojaBean() throws ClassNotFoundException, SQLException {
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        ConexaoSRVSistema = new ConexaoSRVSistema().getConnection();
        listar();
        renderizar();
    }

    public final void renderizar() {
        renderizador = cod_negocio == 3L;
    }

    public void listarLojas() {
        List<Embarcacao_Loja> campos = new ArrayList<>();
        String sql2 = "";
        if (cod_negocio == 3L) {
            sql2 = "SELECT el.id, EL.cliente, el.codigoSTAI, el.nomeBarco, el.nomeVagaLoja FROM Embarcacao_Loja EL"
                    + " where el.cod_negocio=" + cod_negocio + "  ";
        } else {
            sql2 = " SELECT el.id, c.nome as cliente, el.codigoSTAI,el.nomeBarco,el.nomeVagaLoja FROM Embarcacao_Loja EL   "
                    + " JOIN CLIENTE C ON C.id = EL.codigoSTAI "
                    + " where C.cod_negocio =" + cod_negocio + " and el.cod_negocio = " + cod_negocio + "";
        }
        try {

            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Embarcacao_Loja el = new Embarcacao_Loja();
                el.setId(rs.getLong("id"));
                el.setCliente(rs.getString(2));
                el.setCodigoSTAI(rs.getLong("CODIGOSTAI"));
                el.setNomeBarco(rs.getString("nomeBarco"));
                el.setNomeVagaLoja(rs.getString("NomeVagaLoja"));
                campos.add(el);
            }
            this.embarcacaoLojas = campos;
            campos = null;
            //ConexaoSQL.close();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EmbarcaoLojaBean", "listarLojas");
        }
        //return campos;
    }

    public void listar() {
        listarLojas();
        clientes = new GenericDAO<>(Cliente.class).listarClientes();
    }

    public void salvar() {
        embarcacaoLoja.setNomeBarco("");
        new GenericDAO<>(Embarcacao_Loja.class).salvar(embarcacaoLoja);
        embarcacaoLoja = new Embarcacao_Loja();
        listarLojas();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        FacesUtil.addMsgInfo("Cadastrado com Sucesso!", "Cadastro");

    }

    public void editar(Embarcacao_Loja r) {
        editaEmbarcacaoLoja = r;
    }

    public void editarEmbarcacao_Loja() {
        new GenericDAO<>(Embarcacao_Loja.class).update(editaEmbarcacaoLoja);
        editaEmbarcacaoLoja = new Embarcacao_Loja();
        listarLojas();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Editado com Sucesso!", "Editar");

    }

    public void importar() throws SQLException {
        if (cod_negocio == 3L) {
            try {
                int imp = 0, ign = 0;
                boolean r = true;
                EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ChamadoPU");
                EntityManager entitymanager = emfactory.createEntityManager();
                String sql = " SELECT u.codigo, 'PF' tipo, co.vaga vagalojabox, b.nome barco, u.nome , u.cpfcgc, u.email, co.status , u.email_cobranca, c.telefonecelular tel, c.telefoneresidenciaL tel2, u.cidade, u.uf, u.pais, u.anotacoes\n"
                        + "FROM usuario u\n"
                        + "INNER JOIN complementopf c ON c.codigo = u.codigo\n"
                        + "INNER JOIN contrato co ON co.usuario = u.codigo\n"
                        + "LEFT JOIN ocupacao oc ON co.codigo=oc.contrato AND oc.datatermino IS NULL\n"
                        + "LEFT JOIN barco b ON oc.codigobarco=b.codigo\n"
                        + "WHERE CO.STATUS=1\n"
                        + "\n"
                        + "union\n"
                        + "\n"
                        + "SELECT u.codigo, 'PJ' tipo, co.vaga vagalojabox, '' barco, u.nome , u.cpfcgc, u.email, co.status ,u.email_cobranca, u.telefonecomercial tel, u.fax tel2, u.cidade, u.uf, u.pais, u.anotacoes\n"
                        + "FROM usuario u\n"
                        + "INNER JOIN complementopj c ON c.codigo = u.codigo\n"
                        + "INNER JOIN contrato co ON co.usuario = u.codigo\n"
                        + "LEFT JOIN ocupacao oc ON co.codigo=oc.contrato AND oc.datatermino IS NULL\n"
                        + "left join contratoarmario ca on co.codigo = ca.contratovaga and  ca.datatermino IS NULL and ca.armario is not null\n"
                        + "WHERE CO.STATUS=1\n"
                        + "\n"
                        + "union\n"
                        + "\n"
                        + "SELECT u.codigo, 'PJ' tipo, ca.armario vagalojabox, '' barco, u.nome , u.cpfcgc, u.email, co.status ,u.email_cobranca, u.telefonecomercial tel, u.fax tel2, u.cidade, u.uf, u.pais, u.anotacoes\n"
                        + "from contratoarmario ca\n"
                        + "INNER JOIN contrato co ON co.codigo = ca.contratovaga and  ca.datatermino IS NULL and ca.armario is not null\n"
                        + "inner join usuario u on co.usuario = u.codigo\n"
                        + "WHERE CO.STATUS=1";

                stmt = ConexaoSRVSistema.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {

                    Embarcacao_Loja c = new Embarcacao_Loja();

                    c.setCodigoSTAI(rs.getLong("codigo"));
                    c.setCliente(rs.getString("nome"));
                    c.setNomeBarco(rs.getString("barco"));
                    c.setNomeVagaLoja(rs.getString("vagalojabox"));
                    //c.setAnotacoes(rs.getString("anotacoes"));
                    if (c.getNomeVagaLoja() == null && c.getNomeBarco() == null) {
                        c.setNomeBarco("*");
                    }

                    if (c.getNomeBarco() == null) {
                        c.setNomeBarco("");
                    }

                    if (c.getNomeVagaLoja() == null) {
                        c.setNomeVagaLoja("");
                    }

                    if (c.getCliente().contains("'")) {
                        String nome = c.getCliente().replaceAll("[']", " ");
                        c.setCliente(nome);
                    }

                    if (c.getNomeBarco().contains("'")) {
                        String nome = c.getNomeBarco().replaceAll("[']", " ");
                        c.setNomeBarco(nome);
                    }

                    if (!verifCli(c)) {
                        //new GenericDAO<>(Cliente.class).salvar(c);
                        entitymanager.getTransaction().begin();
                        entitymanager.merge(c);
                        entitymanager.getTransaction().commit();
                        //System.out.println("Importados: " + imp + "");
                        imp++;
                    } else {
                        c = null;
                        //System.out.println("Ignorados: " + ign + "");
                        ign++;
                        //}

                    }

                }
                entitymanager.clear();
                entitymanager.close();
                emfactory.close();
                //ConexaoSRVSistema.close();
                rs.close();
                stmt.close();
                //ConexaoSQL.close();
                rs2.close();
                stmt2.close();
                System.out.println("Chegou aqui");
                FacesUtil.addMsgInfo("Clientes importados: " + imp, "Importação");
                //FacesUtil.addMsgInfo("Clientes ignorados: " + ign, "Importação");
                listar();
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");

            } catch (ClassNotFoundException e) {
                FacesUtil.addMsgFatalSQL(e, "Erro", "EmbarcaoLojaBean", "importar");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                //ConexaoSRVSistema.close();
                rs.close();
                stmt.close();
                //ConexaoSQL.close();
                rs2.close();
                stmt2.close();
            }
        } else {
            FacesUtil.addMsgError("Importação", "Negócio não cadastrado para realizar Importação!");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        }
    }

    public boolean verifCli(Embarcacao_Loja c) throws ClassNotFoundException, SQLException {
        boolean r = false;
        String sql2 = " SELECT * FROM Embarcacao_Loja "
                + " where "
                + " NomeVagaLoja ='" + c.getNomeVagaLoja() + "' and "
                + " NomeBarco ='" + c.getNomeBarco() + "' and "
                + " cliente='" + c.getCliente() + "' and "
                + " codigoSTAI =" + c.getCodigoSTAI() + " and "
                + " cod_negocio =" + cod_negocio + " ";

        stmt2 = ConexaoSQL.prepareStatement(sql2);
        rs2 = stmt2.executeQuery();
        if (rs2.next()) {
            System.out.println("x:x");
            return true;
        } else {
            System.out.println("y:y");
            return false;
        }
    }

    public Embarcacao_Loja getEditaEmbarcacao_Loja() {
        return editaEmbarcacaoLoja;
    }

    public void setEditaEmbarcacao_Loja(Embarcacao_Loja editaEmbarcacao_Loja) {
        this.editaEmbarcacaoLoja = editaEmbarcacao_Loja;
    }

    public List<Embarcacao_Loja> getEmbarcacao_Lojas() {
        return embarcacaoLojas;
    }

    public void setEmbarcacao_Lojas(List<Embarcacao_Loja> Embarcacao_Lojas) {
        this.embarcacaoLojas = Embarcacao_Lojas;
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

    public Embarcacao_Loja getEmbarcacaoLoja() {
        return embarcacaoLoja;
    }

    public void setEmbarcacaoLoja(Embarcacao_Loja embarcacaoLoja) {
        this.embarcacaoLoja = embarcacaoLoja;
    }

    public List<Embarcacao_Loja> getEmbarcacaoLojas() {
        return embarcacaoLojas;
    }

    public void setEmbarcacaoLojas(List<Embarcacao_Loja> embarcacaoLojas) {
        this.embarcacaoLojas = embarcacaoLojas;
    }

    public Embarcacao_Loja getEditaEmbarcacaoLoja() {
        return editaEmbarcacaoLoja;
    }

    public void setEditaEmbarcacaoLoja(Embarcacao_Loja editaEmbarcacaoLoja) {
        this.editaEmbarcacaoLoja = editaEmbarcacaoLoja;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Embarcacao_Loja getEmbarcacaoLojaSelecionado() {
        return embarcacaoLojaSelecionado;
    }

    public void setEmbarcacaoLojaSelecionado(Embarcacao_Loja embarcacaoLojaSelecionado) {
        this.embarcacaoLojaSelecionado = embarcacaoLojaSelecionado;
    }

    public boolean isRenderizador() {
        return renderizador;
    }

    public void setRenderizador(boolean renderizador) {
        this.renderizador = renderizador;
    }

}
