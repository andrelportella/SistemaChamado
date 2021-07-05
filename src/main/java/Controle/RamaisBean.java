package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Campos;
import Modelo.Ramais;
import java.io.IOException;
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

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class RamaisBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    private Connection ConexaoSQL;
    private List<Ramais> ramais = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Ramais ramal = new Ramais();
    private Ramais editaRamal = new Ramais();
    private Ramais ramalSelecionado = new Ramais();
    private List<Campos> setor = new ArrayList<>();
    private List<Campos> site = new ArrayList<>();
    //Util util = new Util();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio; //= (Long) session.getAttribute("idNeg");
    String cod_negocio_noticia = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio");
    private boolean rend = true;
    String sql;

    public RamaisBean() {
        listaRamais();
        list = Arrays.asList(true, true, true, true, true, true);

    }

    public void emp() {
        if (cod_negocio_noticia != null) {
            cod_negocio = Long.parseLong(cod_negocio_noticia);
        } else {
            cod_negocio = (Long) session.getAttribute("idNeg");
            setor = new GenericDAO<>(Campos.class).listarCamposJDBC(4L);
            site = new GenericDAO<>(Campos.class).listarCamposJDBC(2L);
            rend = false;
        }
    }

    private List<Boolean> list;

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void Ramais() {
        emp();
        List<Ramais> result = new ArrayList();
        sql
                = " SELECT DISTINCT 1, email, u.nome, u.telefone, setor.descricao as Setor, site.descricao AS Site, u.celular as celular FROM usuario u "
                + " join campos setor on u.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " "
                + " join campos site on u.cod_site = site.cod and site.tabela = 2 and site.cod_negocio = " + cod_negocio + " "
                + " where u.cod_negocio =" + cod_negocio + " "
                + "UNION "
                + " SELECT DISTINCT 0, email, u.nome, u.telefone, setor.descricao as Setor, site.descricao AS Site, u.celular as celular FROM ramais u "
                + " join campos setor on u.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " "
                + " join campos site on u.cod_site = site.cod and site.tabela = 2 and site.cod_negocio = " + cod_negocio + " "
                + " where u.cod_negocio =" + cod_negocio + "  "
                + "order by nome "
                + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Ramais r = new Ramais();
            while (rs.next()) {
                r.setEditar(rs.getBoolean(1));
                r.setEmpresa(rs.getString("SITE"));
                r.setSetor(rs.getString("SETOR"));
                r.setNome(rs.getString("NOME"));
                r.setTelefone(rs.getString("TELEFONE"));
                r.setEmail(rs.getString("EMAIL"));
                r.setCelular(rs.getString("CELULAR"));
                result.add(r);
                r = new Ramais();
            }
            this.ramais = result;
            result = null;
            r = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RamaisBean", "Ramais");
            System.out.println(" public void Ramais()  Erro:" + e);
        }
    }

    public void listaRamais() {
        emp();
        List<Ramais> rms = new ArrayList<>();
        sql = " SELECT u.nome, SUBSTRING(u.telefone,10,4), c.descricao as site, c2.descricao as setor, u.email,u.celular,u.telefone FROM Usuario u  "
                + " JOIN Campos c on c.cod = u.cod_site and c.tabela = 2 "
                + " JOIN Campos c2 on c2.cod = u.cod_setor and c2.tabela = 4 "
                + " WHERE u.cod_negocio = 1 and c.cod_negocio = " + cod_negocio + "  and c2.cod_negocio = " + cod_negocio + " "
                + " and c.descricao not in ('Bimbarras') and u.status = 1 "
                + " order by site,setor, nome ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Ramais r = new Ramais();
            while (rs.next()) {
                r.setNome(rs.getString(1));
                r.setTelefone(rs.getString(2));
                r.setEmpresa(rs.getString(3));
                r.setSetor(rs.getString(4));
                r.setEmail(rs.getString(5));
                r.setCelular(rs.getString(6));
                r.setSite(rs.getString(7));
                rms.add(r);
                r = new Ramais();
            }
            this.ramais = rms;
            rms = null;
            r = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RamaisBean", "listaRamais");
            System.out.println("  public void listaRamais() Erro:" + e);
        }
    }

    public void editarRamal(Ramais ramal) {
        this.editaRamal = ramal;
    }

    public void editar() throws SQLException {
        new GenericDAO<>(Ramais.class).update(editaRamal);
        editaRamal = new Ramais();
        Ramais();
    }

    public List<Ramais> getRamais() {
        return ramais;
    }

    public void setRamais(List<Ramais> ramais) {
        this.ramais = ramais;
    }

    public Ramais getRamal() {
        return ramal;
    }

    public void setRamal(Ramais ramal) {
        this.ramal = ramal;
    }

    public List<Campos> getSite() {
        return site;
    }

    public void setSite(List<Campos> site) {
        this.site = site;
    }

    public List<Campos> getSetor() {
        return setor;
    }

    public void setSetor(List<Campos> setor) {
        this.setor = setor;
    }

    public void salvar() throws SQLException {
        new GenericDAO<>(Ramais.class).salvar(ramal);
        ramal = new Ramais();
        Ramais();
        //util.redirecionarIntranet("cadastrados/listaRamais");
    }

    public Ramais getRamalSelecionado() {
        return ramalSelecionado;
    }

    public void setRamalSelecionado(Ramais ramalSelecionado) {
        this.ramalSelecionado = ramalSelecionado;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public Ramais getEditaRamal() {
        return editaRamal;
    }

    public void setEditaRamal(Ramais editaRamal) {
        this.editaRamal = editaRamal;
    }

    public boolean isRend() {
        return rend;
    }

    public void setRend(boolean rend) {
        this.rend = rend;
    }

}
