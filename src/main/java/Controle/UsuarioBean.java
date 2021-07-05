package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Campos;
import Modelo.ControleAcesso;
import Modelo.Negocio;
import Modelo.Recurso;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.Visibility;
import util.FacesUtil;
import util.Util;

@ManagedBean
@ViewScoped
@FacesValidator(value = "validarEmail")
public class UsuarioBean implements Serializable, Validator {

    private static final long serialVersionUID = -7263592628030475398L;
    private List<Usuario> usuarios = new ArrayList<>();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Usuario usuario = new Usuario();
    private List<Campos> site = new ArrayList<>();
    private List<Campos> setor = new ArrayList<>();
    private Connection ConexaoSQL;
    Util util = new Util();
    private Usuario usuarioSelecionado = new Usuario();
    private Usuario editaUsuario = new Usuario();
    private List<ControleAcesso> perfis = new ArrayList<>();
    private List<Campos> negocio = new ArrayList<>();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private List<Boolean> list;
    private DualListModel<Campos> listModel;
    private List<String> removerNegocio = new ArrayList<>();
    private List<Campos> negocioEmUso = new ArrayList<>();
    private List<Campos> negocioNaoSelecionado = new ArrayList<>();
    private List<Campos> acessoRecursos = new ArrayList<>();
    private List<Recurso> acessoRecursosUsuario = new ArrayList<>();
    Usuario usuarioNegocio = new Usuario();
    Date data = new Date(System.currentTimeMillis());
    Long cod_user = (Long) session.getAttribute("idUser");
    private Boolean[] resposta;
    private String[] respostaObs;
    Long cod_usuario;
    Long id;

    public UsuarioBean() {
        listaUsuario();
        construtorXML();
        list = Arrays.asList(true, true, true, true, true, true, true, false, false, false, false, false, false, false, false);
        listModel = new DualListModel<>(new ArrayList<>(negocioNaoSelecionado), negocioEmUso);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void construtorXML() {
        site = new GenericDAO<>(Campos.class).listarCamposJDBC(2L);
        setor = new GenericDAO<>(Campos.class).listarCamposJDBC(4L);
        negocio = new GenericDAO<>(Campos.class).listarCamposJDBC(21L);
        perfis = new GenericDAO<>(Campos.class).listarPerfis();
        usuario.setStatus(true);
    }

    public void listaUsuario() {
        try {
            List<Usuario> user = new ArrayList<>();
            sql = "SELECT site.descricao as site, setor.descricao as setor, ca.tipoPerfil, negocio.descricao as negocio , u.* FROM usuario u"
                    + " join campos setor on u.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio =" + cod_negocio + ""
                    + " join campos site on u.cod_site = site.cod and site.tabela = 2 and site.cod_negocio =" + cod_negocio + ""
                    + " LEFT JOIN ControleAcesso as ca ON u.cod_perfil = ca.id "
                    + " LEFT JOIN CAMPOS as negocio ON u.cod_negocio = negocio.cod and negocio.tabela = 21 "
                    + " where u.cod_negocio =" + cod_negocio + " "
                    + " order by nome";

            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Usuario u = new Usuario();
            while (rs.next()) {

                u.setId(rs.getLong("ID"));
                u.setNome(rs.getString("nome"));
                u.setSenha(rs.getString("senha"));
                u.setLogin(rs.getString("login"));
                u.setCelular(rs.getString("celular"));
                u.setTelefone(rs.getString("telefone"));
                u.setEmail(rs.getString("email"));
                u.setSetor(rs.getString(2));
                u.setPerfil(rs.getString(3));
                u.setNegocio(rs.getString(4));
                u.setSite(rs.getString(1));
                u.setStatus(rs.getBoolean("status"));
                u.setCod_perfil(rs.getInt("cod_perfil"));
                u.setCod_setor(rs.getLong("cod_setor"));
                u.setCod_site(rs.getLong("cod_site"));
                u.setCod_perfil(rs.getInt("cod_perfil"));
                u.setStatus(rs.getBoolean("status"));
                u.setTecnicoChamado(rs.getBoolean("TecnicoChamado"));
                u.setTecnicoRequerimento(rs.getBoolean("TecnicoRequerimento"));
                u.setTecnicoAdministrativo(rs.getBoolean("TecnicoAdministrativo"));
                u.setRamal(rs.getString("ramal"));
                u.setDescricao(rs.getString("descricao"));
                user.add(u);
                u = new Usuario();

            }
            usuarios = user;
            user = null;
            u = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            sql = null;
            stmt = null;
            rs = null;
            ConexaoSQL = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "listaUsuario");
            System.out.println("public void listaUsuario() Erro:" + e);
        }
    }

    public void editarUsuario(Usuario u) {
        editaUsuario = u;
        u = null;
    }

    public void inserirNegocios(Usuario u) {
        usuarioNegocio = u;
        listaNegocioNaoSelecionado(u);
        listaNegocioEmUso(u);
        listModel = new DualListModel<>(new ArrayList<>(negocioNaoSelecionado), negocioEmUso);
        u = null;
    }

    public void acessos(Usuario u) {
        listaAcessosUsuario(u);
        listaTodosAcessos();
        listaResposta();
        cod_usuario = u.getId();
    }

    public void listaTodosAcessos() {
        List<Campos> campos = new ArrayList<>();
        String sql2 = "SELECT * FROM campos  where tabela = 28 and cod <> 0 and cod_negocio =" + cod_negocio + ""
                + " order by descricao";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();
            Campos c = new Campos();
            int i = 0;
            while (rs.next()) {
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                c.setCont(i++);
                campos.add(c);
                c = new Campos();
            }
            acessoRecursos = campos;
            campos = null;
            c = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("" + e);
        }
        resposta = new Boolean[acessoRecursos.size()];
        respostaObs = new String[acessoRecursos.size()];
    }

    public void listaResposta() {
        int i = 0;
        for (Campos r2 : acessoRecursos) {
            for (Recurso rr : acessoRecursosUsuario) {
                //System.out.println(rr.getCod_recurso() + " == " + r2.getCod());
                if (Objects.equals(rr.getCod_recurso(), r2.getCod())) {
                    resposta[i] = rr.isUsaRecurso();
                    respostaObs[i] = rr.getObs();
                    //System.out.println("Teve uma combinação:" + rr.getCod_recurso());
                }
            }
            i++;
        }
    }

    public void salvarResposta() {
        Recurso r = new Recurso();
        for (int i = 0; i < acessoRecursos.size(); i++) {
            r.setCod_usuario(cod_usuario);
            r.setCod_recurso(acessoRecursos.get(i).getCod());
            r.setObs(respostaObs[i]);
            r.setUsaRecurso(resposta[i]);
            if (verificaAcesso(r)) {
                r.setId(id);
                new GenericDAO<>(Recurso.class).update(r);
            } else {
                new GenericDAO<>(Recurso.class).salvar(r);
            }
            r = new Recurso();
        }
        resposta = new Boolean[acessoRecursos.size()];
        respostaObs = new String[acessoRecursos.size()];
        FacesUtil.addMsgInfo("Sucesso", "Recursos Atualizados com Sucesso!!!!");
        PrimeFaces.current().executeScript("PF('recursoDialog').hide()");
    }

    public boolean verificaAcesso(Recurso r) {
        sql = " select * from Recurso where cod_usuario = " + r.getCod_usuario() + " "
                + " and cod_recurso =" + r.getCod_recurso() + "  ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong("id");
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return true;
            } else {
                id = 0L;
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
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "verificaAcesso");
            System.out.println(" public List verificaAcesso(Recurso r) Erro:" + e);
            return false;
        }
        //return ValidacoesBanco.retornaBoolean(sql, "UsuarioBean", "verificaAcesso");
    }

    public void listaAcessosUsuario(Usuario u) {
        List<Recurso> campos = new ArrayList<>();
        sql = "select * from Recurso c where c.cod_usuario = " + u.getId() + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Recurso r = new Recurso();
            while (rs.next()) {
                r.setId(rs.getLong("id"));
                r.setCod_usuario(rs.getLong("Cod_usuario"));
                r.setCod_recurso(rs.getLong("Cod_recurso"));
                r.setUsaRecurso(rs.getBoolean("UsaRecurso"));
                r.setObs(rs.getString("obs"));
                campos.add(r);
                r = new Recurso();
            }
            acessoRecursosUsuario = campos;
            campos = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "listaSoftwaresNaoSelecionado");
            System.out.println(" public List listaSoftwaresNaoSelecionado() Erro:" + e);
        }
    }

    public void listaNegocioNaoSelecionado(Usuario u) {
        List<Campos> campos = new ArrayList<>();
        sql = " select * from Campos "
                + " WHERE tabela = 21 AND cod_negocio = 1 AND cod NOT IN( "
                + " select c.cod from Negocio n "
                + " JOIN Campos c ON N.cod_negocio = C.cod AND C.tabela = 21 AND C.cod_negocio = 1 "
                + " where login ='" + u.getLogin() + "' and status = 1) ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Campos c = new Campos();
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
            }
            negocioNaoSelecionado = campos;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
            campos = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "listaSoftwaresNaoSelecionado");
            System.out.println(" public List listaSoftwaresNaoSelecionado() Erro:" + e);
        }
    }

    public void listaNegocioEmUso(Usuario u) {
        List<Campos> campos = new ArrayList<>();
        sql = " SELECT c.* FROM Usuario u "
                + " JOIN Negocio n ON u.login = n.login "
                + " JOIN Campos c ON N.cod_negocio = C.cod AND C.tabela = 21 AND C.cod_negocio = 1 "
                + " where u.id = " + u.getId() + " and n.status = 1";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Campos c = new Campos();
                c.setId(rs.getLong("id"));
                c.setTabela(rs.getLong("tabela"));
                c.setCod(rs.getLong("cod"));
                c.setDescricao(rs.getString("descricao"));
                campos.add(c);
            }
            negocioEmUso = campos;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
            campos = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "listaSoftwaresEmUso");
            System.out.println(" public List listaSoftwaresEmUso(Equipamento e) Erro:" + e);
        }

    }

    public void editar() {
        new GenericDAO<>(Usuario.class).update(editaUsuario);
        listaUsuario();
        editaUsuario = new Usuario();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Solicitante editado com Sucesso!", "Sucesso");
    }

    public boolean verificaLogin(Usuario u) {
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT login FROM Usuario WHERE login=?  ";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, u.getLogin());
            rs = stmt.executeQuery();
            if (rs.next()) {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                sql = null;
                stmt = null;
                rs = null;
                ConexaoSQL = null;
                return true;
            } else {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                sql = null;
                stmt = null;
                rs = null;
                ConexaoSQL = null;
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "verificaLogin");
            System.out.println("public boolean verificaLogin(Usuario u) Erro:" + e);
            return false;
        }

    }

    public void salvar() {
        if (!verificaLogin(usuario)) {
            new GenericDAO<>(Usuario.class).salvar(usuario);
            Negocio n = new Negocio();
            n.setCod_negocio(cod_negocio);
            n.setLogin(usuario.getLogin());
            n.setStatus(true);
            n.setCod_userInseriu(cod_user);
            n.setDataInclusao(data);
            new GenericDAO<>(Negocio.class).salvar(n);
            usuario = new Usuario();
            listaUsuario();
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
            FacesUtil.addMsgInfo("Solicitante cadastrado com Sucesso!", "Sucesso");
        } else {
            FacesUtil.addMsgError("Usuário Já Cadastrado!", "Erro");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        }
    }

    public void onTransfer(TransferEvent event) {
        if (event.isAdd()) {
            for (Object item : event.getItems()) {
                if (removerNegocio.contains(item.toString())) {
                    removerNegocio.remove(item.toString());
                }
            }
        } else if (event.isRemove()) {
            for (Object item : event.getItems()) {
                removerNegocio.add(item.toString());
            }
        }
    }

    public void salvarNegocio() {
        Negocio n = new Negocio();
        for (Object s : listModel.getTarget()) {
            n.setCod_negocio(Long.parseLong(s.toString()));
            n.setLogin(usuarioNegocio.getLogin());
            n.setDataInclusao(data);
            n.setCod_userInseriu(cod_user);
            n.setStatus(true);
            if (!verificarNegocio(n)) {
                new GenericDAO<>(Negocio.class).salvar(n);
            }
            n = new Negocio();
        }
        n = null;
        for (Object item : removerNegocio) {
            excluirNegocio(Long.parseLong(item.toString()));
        }
        usuarioNegocio = new Usuario();
        removerNegocio = new ArrayList<>();
        FacesUtil.addMsgInfo("Salvo com Sucesso", mSucesso);
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('negocioDialog').hide();");
    }

    public boolean verificarNegocio(Negocio n) {
        boolean b = false;
        sql = " SELECT * FROM NEGOCIO WHERE LOGIN = '" + n.getLogin() + "' AND STATUS = 1 and cod_negocio = " + n.getCod_negocio() + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                b = true;
            } else {
                b = false;
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "verificarSoftware");
            System.out.println(" public boolean verificarSoftware(Software_Equipamento se) Erro:" + e);
        }
        return b;
    }

    public void excluirNegocio(Long cod) {
        sql = " update Negocio set status = 0, cod_userRemoveu =" + cod_user + ", dataRemocao='" + getFormatData() + "' where cod_negocio =" + cod + " and status = 1 and login = '" + usuarioNegocio.getLogin() + "' ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "UsuarioBean", "excluirNegocio");
            System.out.println(" public void excluirNegocio(Long cod) Erro:" + e);
        }
    }

    public String getFormatData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.format(this.data);
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public Usuario getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
        this.usuarioSelecionado = usuarioSelecionado;
    }

    public Usuario getEditaUsuario() {
        return editaUsuario;
    }

    public void setEditaUsuario(Usuario editaUsuario) {
        this.editaUsuario = editaUsuario;
    }

    public List<ControleAcesso> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<ControleAcesso> perfis) {
        this.perfis = perfis;
    }

    public List<Campos> getNegocio() {
        return negocio;
    }

    public void setNegocio(List<Campos> negocio) {
        this.negocio = negocio;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public List<String> getRemoverNegocio() {
        return removerNegocio;
    }

    public void setRemoverNegocio(List<String> removerNegocio) {
        this.removerNegocio = removerNegocio;
    }

    public DualListModel<Campos> getListModel() {
        return listModel;
    }

    public void setListModel(DualListModel<Campos> listModel) {
        this.listModel = listModel;
    }

    public List<Campos> getNegocioEmUso() {
        return negocioEmUso;
    }

    public void setNegocioEmUso(List<Campos> negocioEmUso) {
        this.negocioEmUso = negocioEmUso;
    }

    public List<Campos> getNegocioNaoSelecionado() {
        return negocioNaoSelecionado;
    }

    public void setNegocioNaoSelecionado(List<Campos> negocioNaoSelecionado) {
        this.negocioNaoSelecionado = negocioNaoSelecionado;
    }

    public List<Campos> getAcessoRecursos() {
        return acessoRecursos;
    }

    public void setAcessoRecursos(List<Campos> acessoRecursos) {
        this.acessoRecursos = acessoRecursos;
    }

    public List<Recurso> getAcessoRecursosUsuario() {
        return acessoRecursosUsuario;
    }

    public void setAcessoRecursosUsuario(List<Recurso> acessoRecursosUsuario) {
        this.acessoRecursosUsuario = acessoRecursosUsuario;
    }

    public Boolean[] getResposta() {
        return resposta;
    }

    public void setResposta(Boolean[] resposta) {
        this.resposta = resposta;
    }

    public String[] getRespostaObs() {
        return respostaObs;
    }

    public void setRespostaObs(String[] respostaObs) {
        this.respostaObs = respostaObs;
    }

}
