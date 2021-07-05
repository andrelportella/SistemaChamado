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
public class ClienteBean implements Serializable, Validator {

    private Cliente cliente = new Cliente();
    private Cliente editaCliente = new Cliente();
    private Cliente ClienteSelecionado = new Cliente();
    Util util = new Util();
    PreparedStatement stmt;
    PreparedStatement stmt2;
    ResultSet rs;
    ResultSet rs2;
    private Connection ConexaoSRVSistema;
    private Connection ConexaoSQL;
    private List<Cliente> clientes = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO", sql;
    private boolean renderizador;

    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");

    public ClienteBean() throws ClassNotFoundException, SQLException {
        ConexaoSRVSistema = new ConexaoSRVSistema().getConnection();
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        listar();
        renderizar();
    }

    public void listar() {
        clientes = new GenericDAO<>(Cliente.class).listarClientes();
    }

    public final void renderizar() {
        renderizador = cod_negocio == 3L;
    }

    public void salvar() {
        new GenericDAO<>(Cliente.class).salvar(cliente);
        cliente = new Cliente();
        clientes = new GenericDAO<>(Cliente.class).listarClientes();
    }

    public void editar(Cliente r) {
        editaCliente = r;
    }

    public void editarCliente() {
        new GenericDAO<>(Cliente.class).update(editaCliente);
        editaCliente = new Cliente();
        clientes = new GenericDAO<>(Cliente.class).listarClientes();

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

                    Cliente c = new Cliente();

                    c.setCodigoSTAI(rs.getInt("codigo"));
                    c.setCod_tipo(rs.getString("tipo"));
                    c.setNome(rs.getString("nome"));
                    c.setCpf_cnpj(rs.getString("cpfcgc"));
                    c.setEmail(rs.getString("email"));
                    c.setCelular(rs.getString("TEL"));
                    c.setTelefone(rs.getString("TEL2"));
                    c.setCidade(rs.getString("cidade"));
                    c.setUf(rs.getString("uf"));
                    c.setPais(rs.getString("pais"));
                    //c.setAnotacoes(rs.getString("anotacoes"));
                    if (c.getAnotacoes() == null) {
                        c.setAnotacoes(" ");
                    } else if (c.getAnotacoes().contains("'")) {
                        String nome = c.getAnotacoes().replaceAll("[']", " ");
                        c.setAnotacoes(nome);
                    }

                    if (c.getCpf_cnpj() == null) {
                        c.setCpf_cnpj(" ");
                    }

                    if (c.getEmail() == null) {
                        c.setEmail(" ");
                    }

                    if (c.getCelular() == null) {
                        c.setCelular(" ");
                    }

                    if (c.getTelefone() == null) {
                        c.setTelefone(" ");
                    }

                    if (c.getPais() == null) {
                        c.setPais(" ");
                    }

                    if (c.getUf() == null) {
                        c.setUf(" ");
                    }

                    if (c.getCidade() == null) {
                        c.setCidade(" ");
                    }

                    if (c.getNome().contains("'")) {
                        String nome = c.getNome().replaceAll("[']", " ");
                        c.setNome(nome);
                    }

                    if (!verifCli(c)) {
                        entitymanager.getTransaction().begin();
                        entitymanager.merge(c);
                        entitymanager.getTransaction().commit();
                        //System.out.println("Importados: " + imp + "");
                        imp++;
                    } else {
                        //System.out.println("Ignorados: " + ign + "");
                        ign++;

                    }

                }
                entitymanager.clear();
                entitymanager.close();
                emfactory.close();
                //ConexaoSRVSistema.close();
                rs.close();
                stmt.close();
                //ConexaoSQL.close();
                //rs2.close();
                //stmt2.close();
                //System.out.println("Chegou aqui");
                FacesUtil.addMsgInfo("Clientes importados: " + imp, "Importação");
                FacesUtil.addMsgInfo("Clientes ignorados: " + ign, "Importação");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
            } catch (ClassNotFoundException ex) {
                FacesUtil.addMsgFatalSQL(ex, "Erro", "ClienteBean", "importar");
                //FacesUtil.addMsgInfo("Sessão Expirada, por favor, faça o login novamente!", "Erro");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                ConexaoSRVSistema.close();
                rs.close();
                stmt.close();
                ConexaoSQL.close();
                rs2.close();
                stmt2.close();
            }
        } else {
            FacesUtil.addMsgError("Importação", "Negócio não cadastrado para realizar Importação!");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        }
    }

    public boolean verifCli(Cliente c) throws ClassNotFoundException, SQLException {
        boolean r = false;
        String sql2 = " SELECT * FROM CLIENTE "
                + " where cod_tipo ='" + c.getCod_tipo() + "' and "
                + " cpf_cnpj ='" + c.getCpf_cnpj() + "' and "
                + " email='" + c.getEmail() + "' and "
                + " telefone='" + c.getTelefone() + "' and "
                + " celular='" + c.getCelular() + "' and "
                + " codigoSTAI =" + c.getCodigoSTAI() + "and "
                + " cod_negocio =" + cod_negocio + " ";
        //+ " anotacoes ='" + c.getAnotacoes() + "'  ";

        //ConexaoSQL = new ConexaoSQLServer().getConnection();
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente Cliente) {
        this.cliente = Cliente;
    }

    public Cliente getEditaCliente() {
        return editaCliente;
    }

    public void setEditaCliente(Cliente editaCliente) {
        this.editaCliente = editaCliente;
    }

    public Cliente getClienteSelecionado() {
        return ClienteSelecionado;
    }

    public void setClienteSelecionado(Cliente ClienteSelecionado) {
        this.ClienteSelecionado = ClienteSelecionado;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> Clientes) {
        this.clientes = Clientes;
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

    public boolean isRenderizador() {
        return renderizador;
    }

    public void setRenderizador(boolean renderizador) {
        this.renderizador = renderizador;
    }

}
