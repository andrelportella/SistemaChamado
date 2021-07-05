package util;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Parametros;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

@ManagedBean
public class FacesUtil {

    public static void addMsgInfo(String mensagem, String cabecalho) {

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, cabecalho, mensagem);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
    }

    public static void addMsgError(String mensagem, String cabecalho) {

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, cabecalho);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
    }

    public static void addMsgWarn(String mensagem, String cabecalho) {

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, cabecalho);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
    }

    public static void addMsgFatal(String cabecalho, String mensagem) {

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL, mensagem, cabecalho);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
    }

    public static void addMsgFatalSQL(Exception mensagem, String cabecalho, String classe, String metodo) {

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL, cabecalho, mensagem.toString());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);

        enviarEmailErroSistema(cabecalho + ": " + mensagem, metodo, classe);
    }

    public static void enviarEmailErroSistema(String erro, String metodo, String classe) {
        Date data = new Date(System.currentTimeMillis());
        Parametros parametros = new GenericDAO<>(Parametros.class).parametro();
        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(data);
        String horaAbertura = fmtHora.format(data);
        String assunto = "Erro no Sistema";
        Usuario u = buscaEmailSolicitante();

        try {
            HtmlEmail email = new HtmlEmail();

            email.setCharset(parametros.getCodificacaoEmailChamado());
            email.setHostName(parametros.getHostNameChamado());
            email.setSmtpPort(parametros.getSmtpPortChamado());
            email.setAuthenticator(
                    new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
            email.setSSLOnConnect(
                    true);
            email.setFrom(parametros.getLoginEmailChamado(), "Sistema Chamado - Erro");
            String cor = "ff0000";
            String assuntoAnexo = "Erro no Método " + metodo + " da Classe " + classe + " ";
            String tipoCabecalho = "Erro";

            email.setSubject(assunto);
            String nome = erro.replaceAll("[\n]", "<br>");

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
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>Status: " + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Usuário Logado: <span style='color: #000000;'>" + u.getNome() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + horaAbertura + "</span></span> <br/> "
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Classe: <span style='color: #000000;'>" + classe + "</span></span> <br/> "
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Metodo: <span style='color: #000000;'>" + metodo + "</span></span> <br/> "
                    + " <p/>"
                    + " <hr width='90%'/> "
                    + " <p style='text-align: left;'> "
                    + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "</span></strong> <br /><p/>"
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
                    + " <div width='100%'> <fieldset>  <legend style='color: #0070C0;'> Descrição </legend> &nbsp; &nbsp; " + nome + " </fieldset> </div> "
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

            email.addTo(parametros.getEmailProgramador());
            email.send();

            dataAbertura = null;
            horaAbertura = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "FacesUtil", "enviarEmailErroSistema");
        }
    }

    public static Usuario buscaEmailSolicitante() {
        Usuario u = new Usuario();
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        Long r = (Long) session.getAttribute("idUser");
        String sql = "SELECT * FROM Usuario where id = " + r + "";
        try {
            PreparedStatement stmt;
            ResultSet rs;
            Connection ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
                u.setTelefone(rs.getString("telefone"));
                u.setCelular(rs.getString("celular"));
            }
            rs.close();
            stmt.close();
            rs = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FacesUtil", "buscaEmailSolicitante");
            System.out.println(" public Usuario buscaEmailSolicitante(Long r) Erro:" + e);
        }
        return u;

    }

}
