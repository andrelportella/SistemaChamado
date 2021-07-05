package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Campos;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.FacesUtil;
import util.Util;
import util.ValidacoesBanco;

@ManagedBean
@ViewScoped
public class CamposBean implements Serializable {

    private List<Campos> camposList = new ArrayList<>();
    private List<Campos> camposListTabela = new ArrayList<>();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Campos campos = new Campos();
    private Connection ConexaoSQL;
    Util util = new Util();
    private Campos editaCampos = new Campos();
    private boolean statusBotao;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private List<Boolean> list;

    public CamposBean() {
        listaCampos();
        listaCamposTabela();
        renderizar();
        list = Arrays.asList(true, true, true, true);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public boolean renderizar() {
        if (1L == Long.parseLong(session.getAttribute("idNeg").toString())) {
            return false;
        } else {
            return true;
        }
    }

    public void listaCampos() {
        List<Campos> campos = new ArrayList<>();
        sql = " SELECT convert(varchar,c.tabela)+' - '+ b.descricao as nome_Tabela, b.descricao as orderByName, c.* FROM campos c "
                + " join campos b on b.cod = c.tabela and b.tabela = 0 "
                + " where c.tabela <> 0 and c.cod_negocio = " + cod_negocio + " and b.cod_negocio = 1 "
                + " order by orderByName ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Campos c = new Campos();
            while (rs.next()) {
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setNomeTabela(rs.getString(1));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
                c = new Campos();
            }
            camposList = campos;
            c = null;
            campos = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "CamposBean", "listaCampos");
            System.out.println("Public void listaCampos() Erro:" + e);
        }
    }

    public void listaCamposTabela() {
        List<Campos> campos = new ArrayList<>();
        if (cod_negocio == 1L) {
            sql = "SELECT * FROM Campos where tabela = 0 "
                    + " order by tabela";
        } else {
            sql = "SELECT * FROM Campos where tabela = 0 and cod not in (21) "
                    + " order by tabela";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Campos c = new Campos();
            while (rs.next()) {
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
                c = new Campos();
            }
            camposListTabela = campos;
            campos = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "CamposBean", "listaCamposTabela");
            System.out.println(" public void listaCamposTabela() Erro:" + e);
        }
    }

    public boolean verificaAcesso() {
        sql = "SELECT tabela FROM campos WHERE Descricao= '" + campos.getDescricao() + "' and tabela = " + campos.getTabela() + " and cod_negocio =" + cod_negocio + " ";
        return ValidacoesBanco.retornaBoolean(sql, "CamposBean", "verificaAcesso");
    }

    public Long verificaCodigo(Campos campos) {
        Long retorno = 0L;
        try {

            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = "SELECT max(cod) FROM campos WHERE tabela=? and cod_negocio = " + cod_negocio + "";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setLong(1, campos.getTabela());
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getLong(1);
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return retorno;
            } else {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return retorno;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "CamposBean", "verificaCodigo");
            System.out.println("public Long verificaCodigo(Campos campos) Erro:" + e);
            return 999999L;
        }

    }

    public void salvar() {
        if (verificaAcesso()) {
            FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
        } else {
            if (campos.getTabela() == 21L) {
                EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ChamadoPU");
                EntityManager entitymanager = emfactory.createEntityManager();
                entitymanager.getTransaction().begin();
                campos.setCod(verificaCodigo(campos) + 1L);
                entitymanager.persist(campos);
                Campos c = new Campos();
                c.setDescricao("Chamado");
                c.setTabela(19L);
                c.setCod(1L);
                c.setCod_negocio(campos.getCod());
                entitymanager.persist(c);
                Campos c2 = new Campos();
                c2.setDescricao("Requerimento");
                c2.setTabela(19L);
                c2.setCod(2L);
                c2.setCod_negocio(campos.getCod());
                entitymanager.persist(c2);

                Campos c3 = new Campos();
                c3.setDescricao("1 - ATENDIMENTO / MANUTENÇÃO INTERNA");
                c3.setTabela(15L);
                c3.setCod(1L);
                c3.setCod_negocio(campos.getCod());
                entitymanager.persist(c3);

                Campos c4 = new Campos();
                c4.setDescricao("2 - PREVENTIVA");
                c4.setTabela(15L);
                c4.setCod(2L);
                c4.setCod_negocio(campos.getCod());
                entitymanager.persist(c4);

                Campos c5 = new Campos();
                c5.setDescricao("Novo");
                c5.setTabela(20L);
                c5.setCod(0L);
                c5.setCod_negocio(campos.getCod());
                entitymanager.persist(c5);

                Campos c6 = new Campos();
                c6.setDescricao("Aberto");
                c6.setTabela(20L);
                c6.setCod(1L);
                c6.setCod_negocio(campos.getCod());
                entitymanager.persist(c6);

                Campos c7 = new Campos();
                c7.setDescricao("Aguardando Requerente/Cliente");
                c7.setTabela(20L);
                c7.setCod(2L);
                c7.setCod_negocio(campos.getCod());
                entitymanager.persist(c7);

                Campos c8 = new Campos();
                c8.setDescricao("Finalizado");
                c8.setTabela(20L);
                c8.setCod(3L);
                c8.setCod_negocio(campos.getCod());
                entitymanager.persist(c8);

                Campos c9 = new Campos();
                c9.setDescricao("Banco de Sugestões");
                c9.setTabela(20L);
                c9.setCod(4L);
                c9.setCod_negocio(campos.getCod());
                entitymanager.persist(c9);

                Campos c10 = new Campos();
                c10.setDescricao("0");
                c10.setTabela(23L);
                c10.setCod(1L);
                c10.setCod_negocio(campos.getCod());

                entitymanager.persist(c10);
                entitymanager.getTransaction().commit();
                entitymanager.close();
                emfactory.close();

                campos = new Campos();
                listaCampos();
                listaCamposTabela();
                FacesUtil.addMsgInfo("Sucesso!", "Novo negocio criado com Sucesso!");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
                c = null;
                c2 = null;
                c3 = null;
                c4 = null;
                c5 = null;
                c6 = null;
                c7 = null;
                c8 = null;
                c9 = null;
                c10 = null;
                campos = null;
            } else {
                campos.setCod(verificaCodigo(campos) + 1L);
                new GenericDAO<>(Campos.class).salvar(campos);
                campos = new Campos();
                listaCampos();
                listaCamposTabela();
                FacesUtil.addMsgInfo("Sucesso!", "Salvo com Sucesso!");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
            }
        }
    }

    public List<Campos> getCamposList() {
        return camposList;
    }

    public void editar(Campos campos) {
        this.editaCampos = campos;
    }

    public void editarCampos() {
        new GenericDAO<>(Campos.class).update(editaCampos);
        editaCampos = new Campos();
        listaCampos();
        listaCamposTabela();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso!", "Atualizado com Sucesso!");
    }

    public void setCamposList(List<Campos> camposList) {
        this.camposList = camposList;
    }

    public Campos getCampos() {
        return campos;
    }

    public void setCampos(Campos campos) {
        this.campos = campos;
    }

    public List<Campos> getCamposListTabela() {
        return camposListTabela;
    }

    public void setCamposListTabela(List<Campos> camposListTabela) {
        this.camposListTabela = camposListTabela;
    }

    public Campos getEditaCampos() {
        return editaCampos;
    }

    public void setEditaCampos(Campos editaCampos) {
        this.editaCampos = editaCampos;
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

}
