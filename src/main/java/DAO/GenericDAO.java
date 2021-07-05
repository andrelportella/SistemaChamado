package DAO;

import Conexao.ConexaoSQLServer;
import Conexao.JPAUtil;
import Modelo.Atendimento;
import Modelo.Campos;
import Modelo.Cliente;
import Modelo.ControleAcesso;
import Modelo.Embarcacao_Loja;
import Modelo.Equipamento;
import Modelo.Formulario;
import Modelo.Fornecedor;
import Modelo.Local_Requerimento;
import Modelo.MovimentacaoAtendimento;
import Modelo.Negocio;
import Modelo.Noticia;
import Modelo.Parametros;
import Modelo.Produto;
import Modelo.Requerente;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ricardo
 * @param <T>
 */
public class GenericDAO<T> implements Serializable {

    private final Class<T> classe;
    private Connection ConexaoSQL;
    private List<Campos> campos = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    Long negocio;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
        if (cod_negocio == null) {
            if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio") != null) {
                cod_negocio = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio"));
            } else {
                cod_negocio = 1L;
            }
        }
    }

    public void salvar(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(t);
        em.getTransaction().commit();
        em.clear();
        em.close();
    }

    public void salvar2(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();
        em.clear();
        em.close();
    }

    public void excluir(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(t));
        em.getTransaction().commit();
        em.clear();
        em.close();
    }

    public void update(T t) {
        //Lembrar sempre de passar o ID para o update
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(t);
        em.getTransaction().commit();
        em.clear();
        em.close();
    }

    public List<T> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        List<T> resultados = em.createQuery(
                "select c from " + classe.getName() + " c where c.cod_negocio =" + cod_negocio + " ", classe).getResultList();
        em.clear();
        em.close();

        return resultados;
    }

    public List<T> listarTodosSQL(String sql) {
        EntityManager em = JPAUtil.getEntityManager();

        List<T> resultados = em.createQuery(
                sql, classe).getResultList();
        em.clear();
        em.close();

        return resultados;
    }

    public List<Campos> listarCampos(Long t) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select c from Campos c where c.tabela = :tabela and c.cod_negocio=" + cod_negocio + " order by c.descricao ";
        List<Campos> resultados = em
                .createQuery(query, Campos.class)
                .setParameter("tabela", t)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Usuario> listarUsuarios() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.status = 1 and u.cod_negocio=" + cod_negocio + " ORDER BY u.nome ";
        List<Usuario> resultados = em
                .createQuery(query, Usuario.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Requerente> listarRequerentes() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Requerente u where u.cod_negocio=" + cod_negocio + " ORDER BY u.nome ";
        List<Requerente> resultados = em
                .createQuery(query, Requerente.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Produto> listarProdutos() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Produto u where u.cod_negocio=" + cod_negocio + " and u.status = 1 ORDER BY u.descricao ";
        List<Produto> resultados = em
                .createQuery(query, Produto.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Local_Requerimento> listarLocalRequerimento() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Local_Requerimento u where u.cod_negocio=" + cod_negocio + " ORDER BY u.nome";
        List<Local_Requerimento> resultados = em
                .createQuery(query, Local_Requerimento.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Embarcacao_Loja> listarLojas() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Embarcacao_Loja u where u.cod_negocio=" + cod_negocio + "  and u.nomeBarco <>'*' ORDER BY u.nomeVagaLoja ";
        List<Embarcacao_Loja> resultados = em
                .createQuery(query, Embarcacao_Loja.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Local_Requerimento> listarLocais() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Local_Requerimento u where u.cod_negocio=" + cod_negocio + " ORDER BY u.nome ";
        List<Local_Requerimento> resultados = em
                .createQuery(query, Local_Requerimento.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Equipamento> listarEquipamentos() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Equipamento u where u.cod_negocio=" + cod_negocio + " order by u.nome";
        List<Equipamento> resultados = em
                .createQuery(query, Equipamento.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Usuario> listarTecnicos() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.cod_negocio=" + cod_negocio + " ORDER BY u.nome";
        List<Usuario> resultados = em
                .createQuery(query, Usuario.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Usuario> listarTecnicosRequerimento() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.tecnicoRequerimento = 1 and u.cod_negocio=" + cod_negocio + " ORDER BY u.nome ";
        List<Usuario> resultados = em
                .createQuery(query, Usuario.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Usuario> listarTecnicosChamado() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.tecnicoChamado = 1 and u.cod_negocio=" + cod_negocio + " ORDER BY u.nome ";
        List<Usuario> resultados = em
                .createQuery(query, Usuario.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Cliente> listarClientesCombo() {
        List<Cliente> campos = new ArrayList<>();
        String sql2 = "SELECT SUBSTRING(nome,1,50) as nome2, * FROM Cliente  where cod_negocio =" + cod_negocio + ""
                + " order by nome";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome2"));
                c.setCodigoSTAI(rs.getInt("CodigoSTAI"));
                campos.add(c);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("" + e);
        }
        return campos;
    }

    public List<Cliente> listarClientes() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u "
                + "from Cliente u where u.cod_negocio=" + cod_negocio + " "
                + "ORDER BY u.nome";
        List<Cliente> resultados = em
                .createQuery(query, Cliente.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<MovimentacaoAtendimento> listarMovimentosAtendimento(Atendimento a) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from MovimentacaoAtendimento u where u.id_atendimento = " + a.getId() + " ";
        List<MovimentacaoAtendimento> resultados = em
                .createQuery(query, MovimentacaoAtendimento.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Fornecedor> listarFornecedores() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Fornecedor u where u.cod_negocio=" + cod_negocio + "  ORDER BY u.razaoSocial ";
        List<Fornecedor> resultados = em
                .createQuery(query, Fornecedor.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public List<ControleAcesso> listarPerfis() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from ControleAcesso u order by u.tipoPerfil ";
        List<ControleAcesso> resultados = em
                .createQuery(query, ControleAcesso.class)
                .getResultList();
        em.clear();
        em.close();
        return resultados;
    }

    public ControleAcesso perfil(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from ControleAcesso u where u.id =" + id + "";
        ControleAcesso resultados = em
                .createQuery(query, ControleAcesso.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Parametros parametro() {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Parametros u where u.cod_negocio =" + cod_negocio + "";

        Parametros resultados = em.createQuery(query, Parametros.class).getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Formulario formulario(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Formulario u where u.id =" + id + "";
        Formulario resultados = em.createQuery(query, Formulario.class).getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Noticia noticia(Long id, Long cod_negocio) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Noticia u where u.id = " + id + "";
        Noticia resultados = em
                .createQuery(query, Noticia.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Usuario tecnico(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.id =" + id + " and u.cod_negocio=" + cod_negocio + "";
        Usuario resultados = em
                .createQuery(query, Usuario.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Requerente requerente(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Requerente u where u.id =" + id + " and u.cod_negocio=" + cod_negocio + "";
        Requerente resultados = em
                .createQuery(query, Requerente.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Usuario usuario(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Usuario u where u.id =" + id + "  and u.cod_negocio=" + cod_negocio + " and u.status = 1";
        Usuario resultados = em
                .createQuery(query, Usuario.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Negocio negocio(Long id, String login) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Negocio u where u.cod_negocio =" + id + " and u.login='" + login + "'";
        Negocio resultados = em
                .createQuery(query, Negocio.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public Local_Requerimento localEmail(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        String query = "select u from Local_Requerimento u where u.id =" + id + " ";
        Local_Requerimento resultados = em
                .createQuery(query, Local_Requerimento.class)
                .getSingleResult();
        em.clear();
        em.close();
        return resultados;
    }

    public List<Campos> listarCamposJDBC(Long tabela) {

        List<Campos> campos = new ArrayList<>();
        String sql2 = "SELECT * FROM campos  where tabela =" + tabela + " and cod <> 0 and cod_negocio =" + cod_negocio + ""
                + " order by descricao";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Campos c = new Campos();
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("" + e);
        }
        return campos;
    }

    public List<Campos> listarCamposJDBCParameter(Long tabela, Long parametro) {

        List<Campos> campos = new ArrayList<>();
        String sql2 = "SELECT * FROM campos  where tabela =" + tabela + " and cod = " + parametro + "  and cod_negocio =" + cod_negocio + ""
                + " order by descricao";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Campos c = new Campos();
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("" + e);
        }
        return campos;
    }

    public Connection getConexaoSQL() {
        return ConexaoSQL;
    }

    public void setConexaoSQL(Connection ConexaoSQL) {
        this.ConexaoSQL = ConexaoSQL;
    }

    public List<Campos> getCampos() {
        return campos;
    }

    public void setCampos(List<Campos> campos) {
        this.campos = campos;
    }

}
