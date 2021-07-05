/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.ControleAcesso;
import Modelo.Negocio;
import Modelo.Parametros;
import Modelo.Usuario;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.primefaces.PrimeFaces;
import util.FacesUtil;
import util.Util;
import util.ValidacoesBanco;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    Util util = new Util();
    private static final long serialVersionUID = 7765876811740798583L;
    private String username = null;
    private String password = null;
    private String login = null;
    private Usuario usuario;
    private ControleAcesso acesso;// = new ControleAcesso();
    PreparedStatement stmt;
    ResultSet rs;
    ResultSet u;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Connection ConexaoSQL;
    private Negocio negocio = new Negocio();
    private Negocio negocioView;// = new Negocio();
    private List<Negocio> negocios = new ArrayList<>();
    private String lojas;
    private String nome_Lojas;

    private String equipamento;
    private String nome_equipamentos;

    private boolean ativo = false;
    private boolean tecnico = false;

    public List listarNegocio(String n) {
        List<Negocio> campos = new ArrayList<>();
        sql = " SELECT neg.descricao, m.* FROM Negocio m "
                + " LEFT join Campos neg on m.cod_negocio = neg.cod and neg.tabela = 21 "
                + " where m.login ='" + n + "' and m.status = 1 "
                + " order by neg.descricao ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Negocio m = new Negocio();
                m.setId(rs.getLong("id"));
                m.setCod_negocio(rs.getLong("Cod_negocio"));
                m.setLogin(rs.getString("login"));
                m.setNegocio(rs.getString("descricao"));
                campos.add(m);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            sql = null;
            stmt = null;
            rs = null;
            ConexaoSQL = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "LoginBean", "listarNegocio");
            System.out.println("public List listarNegocio(String n) Erro:" + e);
        }
        return negocios = campos;
    }

    public Negocio negocio(String n, Long cod) {
        Negocio m = new Negocio();
        sql = " SELECT neg.descricao, m.* FROM Negocio m "
                + " LEFT join Campos neg on m.cod_negocio = neg.cod and neg.tabela = 21 "
                + " where m.login ='" + n + "' and m.cod_negocio =" + cod + " and m.status = 1 "
                + " order by neg.descricao ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setCod_negocio(rs.getLong("Cod_negocio"));
                m.setLogin(rs.getString("login"));
                m.setNegocio(rs.getString("descricao"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            sql = null;
            stmt = null;
            rs = null;
            ConexaoSQL = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "LoginBean", "negocio");
            System.out.println("  public Negocio negocio(String n, Long cod) Erro:" + e);
        }
        return negocioView = m;

    }

    public void listaNegocio(String nome) {
        listarNegocio(nome);
    }

    public void verificaUsuarioLogado() {
        if (ativo) {
            if (usuario.isTecnicoRequerimento()) {
                tecnico = true;
                util.redirecionarRequerimento("cadastrados/requerimentos/requerimentos");
            } else if (usuario.isTecnicoAdministrativo()) {
                tecnico = true;
                util.redirecionarAdmin("cadastrados/tarefas/tarefas", 1);
            } else if (usuario.isTecnicoChamado()) {
                tecnico = true;
                util.redirecionarAdmin("cadastrados/tarefas/tarefas", 0);
            } else {
                tecnico = false;
                util.redirecionarHome();
            }
        }
    }

    public String validaUsuario() {
        if (usuario == null) {
            usuario = new Usuario();
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT COUNT(n.cod_negocio), u.status, u.email, u.id, u.nome,u.cod_perfil,u.tecnicoAdministrativo, u.tecnicoChamado, u.tecnicoRequerimento, u.login,u.cod_negocio FROM Usuario u "
                    + " join negocio n on n.login = u.login "
                    + " WHERE u.login='" + username + "' and u.senha='" + password + "' and n.status = 1 "
                    + " group by u.status, u.email, u.id, u.nome,u.cod_perfil,u.tecnicoAdministrativo, u.tecnicoChamado, u.tecnicoRequerimento, u.login,u.cod_negocio ";

            stmt = ConexaoSQL.prepareStatement(sql);
            u = stmt.executeQuery();
            if (u.next()) {
                if (u.getInt(1) <= 1 && u.getBoolean(2)) {
                    usuario.setEmail(u.getString("email"));
                    usuario.setId(u.getLong("id"));
                    usuario.setNome(u.getString("nome"));
                    usuario.setLogin(u.getString("login"));
                    usuario.setCod_perfil(u.getInt("Cod_perfil"));
                    usuario.setTecnicoRequerimento(u.getBoolean("TecnicoRequerimento"));
                    usuario.setTecnicoChamado(u.getBoolean("TecnicoChamado"));
                    usuario.setTecnicoAdministrativo(u.getBoolean("TecnicoAdministrativo"));
                    usuario.setCod_site(u.getLong("cod_negocio"));
                    username = u.getString("nome");
                    login = u.getString("login");
                    ConexaoSQL.close();
                    u.close();
                    stmt.close();
                    sql = null;
                    stmt = null;
                    u = null;
                    ConexaoSQL = null;
                    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                    session.setAttribute("userName", usuario.getNome());
                    session.setAttribute("idUser", usuario.getId());
                    session.setAttribute("idNeg", usuario.getCod_site());
                    acesso = new GenericDAO<>(ControleAcesso.class).perfil(usuario.getCod_perfil());
                    session.setAttribute("cadCampos", acesso.isCadastraCampos());
                    session.setAttribute("baseInfor", acesso.isAcessaBaseConhecimento());
                    negocio(usuario.getLogin(), usuario.getCod_site());
                    nomeLojas();
                    nomeEquipamento();
                    ativo = true;
                    if (usuario.isTecnicoRequerimento()) {
                        tecnico = true;
                        util.redirecionarRequerimento("cadastrados/requerimentos/requerimentos");
                    } else if (usuario.isTecnicoAdministrativo()) {
                        tecnico = true;
                        util.redirecionarAdmin("cadastrados/tarefas/tarefas", 1);
                    } else if (usuario.isTecnicoChamado()) {
                        tecnico = true;
                        util.redirecionarAdmin("cadastrados/tarefas/tarefas", 0);
                    } else {
                        tecnico = false;
                        util.redirecionarHome();
                    }
                } else if (u.getBoolean(2)) {
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').show();");
                    ConexaoSQL.close();
                    u.close();
                    stmt.close();
                    sql = null;
                    stmt = null;
                    u = null;
                    ConexaoSQL = null;
                } else {
                    ConexaoSQL.close();
                    u.close();
                    stmt.close();
                    sql = null;
                    stmt = null;
                    u = null;
                    ConexaoSQL = null;
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                    FacesUtil.addMsgError("Erro", "Login ou Senha incorretos!");
                }
                return "";
            } else {
                ConexaoSQL.close();
                u.close();
                stmt.close();
                sql = null;
                stmt = null;
                u = null;
                ConexaoSQL = null;
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                FacesUtil.addMsgError("Erro", "Login ou Senha incorretos!");
                return "";
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "LoginBean", "validaUsuario");
            System.out.println("validaUsuario() Erro:" + e);
            return "";
        }
    }

    public String doLogin() {
        if (usuario == null) {
            usuario = new Usuario();
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT * FROM Usuario WHERE (login='" + username + "') and senha='" + password + "' ";
            stmt = ConexaoSQL.prepareStatement(sql);
            u = stmt.executeQuery();
            u.next();
            if (u.getBoolean("status")) {
                usuario.setEmail(u.getString("email"));
                usuario.setId(u.getLong("id"));
                usuario.setNome(u.getString("nome"));
                usuario.setLogin(u.getString("login"));
                usuario.setCod_perfil(u.getInt("Cod_perfil"));
                usuario.setTecnicoRequerimento(u.getBoolean("TecnicoRequerimento"));
                usuario.setTecnicoChamado(u.getBoolean("TecnicoChamado"));
                usuario.setTecnicoAdministrativo(u.getBoolean("TecnicoAdministrativo"));
                username = u.getString("nome");
                login = u.getString("login");
                ConexaoSQL.close();
                u.close();
                stmt.close();
                sql = null;
                stmt = null;
                u = null;
                ConexaoSQL = null;
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                session.setAttribute("userName", usuario.getNome());
                session.setAttribute("idUser", usuario.getId());
                session.setAttribute("idNeg", negocio.getCod_negocio());
                acesso = new GenericDAO<>(ControleAcesso.class).perfil(usuario.getCod_perfil());
                session.setAttribute("cadCampos", acesso.isCadastraCampos());
                session.setAttribute("baseInfor", acesso.isAcessaBaseConhecimento());
                negocio(usuario.getLogin(), negocio.getCod_negocio());
                nomeLojas();
                nomeEquipamento();
                ativo = true;
                if (usuario.isTecnicoRequerimento()) {
                    tecnico = true;
                    util.redirecionarRequerimento("cadastrados/requerimentos/requerimentos");
                } else if (usuario.isTecnicoAdministrativo()) {
                    tecnico = true;
                    util.redirecionarAdmin("cadastrados/tarefas/tarefas", 1);
                } else if (usuario.isTecnicoChamado()) {
                    tecnico = true;
                    util.redirecionarAdmin("cadastrados/tarefas/tarefas", 0);
                } else {
                    tecnico = false;
                    util.redirecionarHome();
                }
                return "";
            } else {
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                FacesUtil.addMsgError("Erro", "Usuario Inativo!");
                ConexaoSQL.close();
                u.close();
                stmt.close();
                sql = null;
                stmt = null;
                u = null;
                ConexaoSQL = null;
            }
            return "";
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "LoginBean", "doLogin");
            System.out.println(" public String doLogin() Erro:" + e);
            return "";
        }
    }

    public void nomeLojas() {

        if ((negocio.getCod_negocio() != null && negocio.getCod_negocio() == 3L) || (usuario.getCod_site() != null && usuario.getCod_site() == 3L)) {
            lojas = "Embarcações | Lojas";
            nome_Lojas = "Loja | Vaga | Armário";
        } else {
            lojas = "Nome a Definir";
            nome_Lojas = "Nome a Definir";
        }
    }

    public void nomeEquipamento() {

        if ((negocio.getCod_negocio() != null && negocio.getCod_negocio() == 4L) || (usuario.getCod_site() != null && usuario.getCod_site() == 4L)) {
            equipamento = "Bens";
            nome_equipamentos = "2 - Bens Patrimoniais";
        } else {
            equipamento = "Equipamentos";
            nome_equipamentos = "2 - Equipamentos Em Geral";
        }
    }

    public String usuarioLogado() {
        if (username == null) {
            return " ";
        } else {
            return "Seja bem vindo " + username + " !";
        }

    }

    public String negocioLogado() {
        if (username == null) {
            return " ";
        } else {
            return "Negocio: " + negocioView.getNegocio();
        }

    }

    public String mensagemBoasVindas() {
        if (username == null) {
            return "Seja bem vindo a INTRANET do Grupo Cidade";
        } else {
            return "";
        }

    }
    /**
     * Logout operation.
     *
     * @param request
     * @param response
     * @param chain
     * @return
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    ServletRequest request;
    FilterChain chain;
    ServletResponse response;

    public void doLogout() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            session.invalidate();
            session.setMaxInactiveInterval(1);
            FacesContext.getCurrentInstance().getExternalContext().setSessionMaxInactiveInterval(1);
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            util.redirecionarLogin();

        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, "Caiu aqui Login" + ex);
        }

    }

    public String pegarIp() {
        String ip = "DESCONHECIDO";
        InetAddress ia = null;
        Enumeration nis = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {

                ia = (InetAddress) ias.nextElement();

                if (ia.getHostAddress().contains(".") && !ia.getHostAddress().contains("127.0.0.1")) {
                    ip = ia.getHostAddress();
                }
            }
        }
        return ip;
    }

    public String pegarNomePC() {
        String nome = "DESCONHECIDO";
        InetAddress ia = null;
        Enumeration nis = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                ia = (InetAddress) ias.nextElement();
                if (ia.getHostName().contains("grupocm.net")) {
                    nome = ia.getHostName();
                }
            }
        }
        return nome;
    }

    public void solicitarRecuperacao() throws IOException {
        ehValido();
        if (ehValido()) {
            Usuario u = buscaUsuario();
            enviarEmailErroSistema("Olá " + u.getNome() + ", Sua Senha é: " + u.getSenha() + "");
            PrimeFaces.current().executeScript("swal('Sucesso!', 'Email para recuperação de senha enviado!', 'success');");
            PrimeFaces.current().executeScript("PF('senhaDialog').hide();");
        } else {
            PrimeFaces.current().executeScript("swal('Erro!', 'Usuario ou Email não cadastrado!', 'error');");
        }
    }

    public boolean ehValido() {
        sql = "SELECT * FROM Usuario WHERE (LOGIN = '" + username + "' or email = '" + username + "')";
        return ValidacoesBanco.retornaBoolean(sql, "RecuperarSenhaBean", "solicitarRecuperacao");
    }

    public Usuario buscaUsuario() {
        Usuario u = new Usuario();
        sql = "SELECT * FROM Usuario where (LOGIN = '" + username + "' or email = '" + username + "')";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
                u.setTelefone(rs.getString("telefone"));
                u.setCelular(rs.getString("celular"));
                u.setSenha(rs.getString("senha"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "buscaEmailSolicitante");
            System.out.println(" public Usuario buscaEmailSolicitante(Long r) Erro:" + e);
        }
        return u;
    }

    public Parametros parametros() {
        Parametros u = new Parametros();
        sql = "SELECT * FROM Parametros where cod_negocio = 1";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setId(rs.getLong("id"));
                u.setHostNameChamado(rs.getString("HostNameChamado"));
                u.setCodificacaoEmailChamado(rs.getString("CodificacaoEmailChamado"));
                u.setSmtpPortChamado(rs.getInt("SmtpPortChamado"));
                u.setLoginEmailChamado(rs.getString("LoginEmailChamado"));
                u.setSenhaEmailChamado(rs.getString("SenhaEmailChamado"));
                u.setUrlLogoEmail(rs.getString("UrlLogoEmail"));
                u.setNomePrincipalEmailChamado(rs.getString("NomePrincipalEmailChamado"));
                u.setEmailProgramador(rs.getString("EmailProgramador"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "buscaEmailSolicitante");
            System.out.println(" public Usuario buscaEmailSolicitante(Long r) Erro:" + e);
        }
        return u;
    }

    public void enviarEmailErroSistema(String descricao) {
        Date data = new Date(System.currentTimeMillis());
        Parametros parametros = parametros();
        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(data);
        String horaAbertura = fmtHora.format(data);
        String assunto = "Recuperação de Senha - Intranet Grupo Cidade";
        Usuario u = buscaUsuario();

        try {
            HtmlEmail email = new HtmlEmail();

            email.setCharset(parametros.getCodificacaoEmailChamado());
            email.setHostName(parametros.getHostNameChamado());
            email.setSmtpPort(parametros.getSmtpPortChamado());
            email.setAuthenticator(
                    new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
            email.setSSLOnConnect(
                    true);
            email.setFrom(parametros.getLoginEmailChamado(), "Sistema Chamado - Intranet");
            String cor = "ff0000";
            String assuntoAnexo = "Recuperação de Senha";

            email.setSubject(assunto);
            String nome = descricao.replaceAll("[\n]", "<br>");

            email.setHtmlMsg(
                    " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td align='center' valign='top' style='background-color: #245269;' bgcolor='#53636e;' > "
                    + " <table width='583' border='0' cellspacing='0' cellpadding='0' > "
                    + " <tbody> "
                    + " <tr style='text-align: center;'> "
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff;'><img src='" + parametros.getUrlLogoEmail() + "' width='583' height='118' /></td> "
                    + " </tr> "
                    + " <tr>"
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff; text-align: center;'> "
                    + " <p><em><strong><span style='color:#0070C0; font-size:larger; '>" + parametros.getNomePrincipalEmailChamado() + "</span></strong></em>"
                    + " <p style='text-align: left;'> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='35' align='left' valign='top'></td> "
                    + " <td align='left' valign='top'> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td align='center' valign='top' style='width: 437px;'> "
                    + " <div style='font-family: Verdana, Geneva, sans-serif; color: #898989; font-size: 12px;'><b> </b></div> "
                    + " </td> "
                    + " </tr> "
                    + " <tr> "
                    + " <td align='left' valign='top' style='font-family: Arial, Helvetica, sans-serif; font-size: 14px; color: #525252; width: 800px;'> "
                    + " <div width='100%'> <fieldset>  <legend style='color: #0070C0;'> Descrição </legend> &nbsp; &nbsp; " + nome + "</fieldset> </div> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='87%' style='font-size: 11px; color: #525252; font-family: Arial, Helvetica, sans-serif;'><b><br/> "
                    + " <p> <span style='color: #ff0000;'>* Este email foi gerado automaticamente pelo Sistema, favor n&atilde;o responder.</span><br /><span style='color: #ff0000;'> Em caso de d&uacute;vidas entre em contato com o t&eacute;cnico.</span></b></p></td> "
                    + " </tr> "
                    + " </tbody> "
                    + " </table> "
                    + " </td> "
                    + " </tr> "
                    + " <tr> "
                    + " <td align='left' valign='top' style='font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #525252; width: 437px;'></td> "
                    + " </tr> "
                    + " </tbody> "
                    + " </table> "
                    + " </td> "
                    + " <td width='35' align='left' valign='top'></td> "
                    + " </tr> "
                    + " </tbody> "
                    + " </table> "
                    + " </td> "
                    + " </tr> "
                    + " <tr> "
                    + " <td align='left' valign='top' bgcolor='#3d90bd' style='background-color: #3d90bd;'> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='35'></td> "
                    + " <td height='50' valign='middle' style='color: #ffffff; font-size: 11px; font-family: Arial, Helvetica, sans-serif;'><b>Sistema de Gest&atilde;o de Atendimentos, Chamados e Requerimentos do Grupo Cidade. </b> <br />Desenvolvido pelo setor do TI do Grupo Cidade.<br /> &copy; Todos os direitos Reservados.</td> "
                    + " <td width='35'></td> "
                    + " </tr> "
                    + " </tbody> "
                    + " <br/> "
                    + " </table> "
                    + " </td> "
                    + " </tr> "
                    + " </tbody> "
                    + " </table> "
                    + " </td> "
                    + " </tr> "
                    + " </tbody> "
                    + " </table> ");

            email.addTo(u.getEmail());
            email.send();
            dataAbertura = null;
            horaAbertura = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "FacesUtil", "enviarEmailErroSistema");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onIdle() throws IOException {
        doLogout();
    }

    public void onActive() throws IOException {
        doLogout();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ControleAcesso getAcesso() {
        return acesso;
    }

    public void setAcesso(ControleAcesso acesso) {
        this.acesso = acesso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Negocio> getNegocios() {
        return negocios;
    }

    public void setNegocios(List<Negocio> negocios) {
        this.negocios = negocios;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public Negocio getNegocioView() {
        return negocioView;
    }

    public void setNegocioView(Negocio negocioView) {
        this.negocioView = negocioView;
    }

    public String getLojas() {
        return lojas;
    }

    public void setLojas(String lojas) {
        this.lojas = lojas;
    }

    public String getNome_Lojas() {
        return nome_Lojas;
    }

    public void setNome_Lojas(String nome_Lojas) {
        this.nome_Lojas = nome_Lojas;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isTecnico() {
        return tecnico;
    }

    public void setTecnico(boolean tecnico) {
        this.tecnico = tecnico;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getNome_equipamentos() {
        return nome_equipamentos;
    }

    public void setNome_equipamentos(String nome_equipamentos) {
        this.nome_equipamentos = nome_equipamentos;
    }

}
