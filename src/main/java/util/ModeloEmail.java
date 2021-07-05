/* mov/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Atendimento;
import Modelo.Cliente;
import Modelo.Embarcacao_Loja;
import Modelo.Equipamento;
import Modelo.Formulario;
import Modelo.Local_Requerimento;
import Modelo.MovimentacaoAtendimento;
import Modelo.MovimentacaoRequerimento;
import Modelo.Negocio;
import Modelo.Parametros;
import Modelo.Produto;
import Modelo.Requerente;
import Modelo.Requerimento;
import Modelo.Reserva;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author ricardo
 */


public class ModeloEmail {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    Parametros parametros = new GenericDAO<>(Parametros.class).parametro();
    Negocio negocio = negocio(cod_negocio);

    public void sendMail(Atendimento r) {
        Usuario u;
        Usuario t;
        Usuario a;
        Produto p;
        Equipamento e;
        String d;
        String b;
        t = buscaEmailSolicitante(r.getId_tecnico().getId());
        u = buscaEmailSolicitante(r.getId_solicitante().getId());
        String cat = tabelaCamposEmail(r.getCod_categoria(), 14L);
        String tipoMov = tabelaCamposEmail(r.getCod_tipoAtendimento(), 15L);
        String assunto = "";
        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(r.getDataAbertura());
        String horaAbertura = fmtHora.format(r.getHoraAbertura());
        String tipoCabecalho;
        String equipamento = "";
        String assuntoAnexo = "";
        String cor = "008000";
        String produto = "";
        String bairro = "";
        String destino = "";
        String valor = "";
        try {
            HtmlEmail email = new HtmlEmail();
            email.setCharset(parametros.getCodificacaoEmailChamado());//Codificação X acentos  ISO-8859-1
            email.setHostName(parametros.getHostNameChamado());
            email.setSmtpPort(parametros.getSmtpPortChamado());
            email.setAuthenticator(new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
            email.setSSLOnConnect(true);
            email.setFrom(parametros.getLoginEmailChamado(), parametros.getAssuntoEmailChamado() + " - " + negocio.getNegocio() + " - " + t.getNome());

            if (r.isStatusAtendimento()) {
                assunto = "Abertura - " + r.getId() + ": ";
                tipoCabecalho = "Abertura!";
                cor = "008000";
            } else {
                assunto = "Encerrado - " + r.getId() + ": ";
                tipoCabecalho = "Encerrado!";
                cor = "ff0000";
            }
            if (r.getObs().length() < 50) {
                assunto = assunto + r.getObs().substring(0, r.getObs().length()) + " ";
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObs().substring(0, r.getObs().length()) + " ";
            } else {
                assunto = assunto + r.getObs().substring(0, 50) + " ";
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObs().substring(0, 50);
            }

            if (r.getCod_equipamento() == 0) {
                equipamento = " ";
            } else if (r.getCod_equipamento() != 178L) {
                e = buscaEquipamento(r.getCod_equipamento());
                equipamento = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Equipamento: <span style='color: #000000;'>" + e.getNome() + " - " + e.getDescricao() + "</span></span> <br/>";
            }

            if (r.getCod_produto() != null && r.getCod_produto() != 0) {
                p = buscaProduto(r.getCod_produto());
                produto = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Produto: <span style='color: #000000;'>" + p.getDescricao() + "</span></span> <br/>"
                        + "<span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Quantidade: <span style='color: #000000;'>" + r.getQtdProduto() + "</span></span> <br/>";
            } else {
                produto = "";
            }

            if (r.getValor() != 0) {
                valor = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Valor: <span style='color: #000000;'>" + r.getValor() + "</span></span> <br/>";
            } else {
                valor = "";
            }

            if ((r.getCod_tipoAtendimento() == parametros.getCodAtividadeAdm())) {
                b = tabelaCamposEmail(r.getCod_bairro(), 25L);
                bairro = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Bairro: <span style='color: #000000;'>" + b + "</span></span> <br/>";
            } else {
                bairro = "";
            }

            if ((r.getCod_tipoAtendimento() == parametros.getCodAtividadeAdm())) {
                d = tabelaCamposEmail(r.getCod_destino(), 24L);
                destino = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Destino: <span style='color: #000000;'>" + d + "</span></span> <br/>";
            } else {
                destino = "";
            }

            email.setSubject(assunto);

            String nome = r.getObs().replaceAll("[\n]", "<br>");

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
                    + " <tr>\n"
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff; text-align: center;'> "
                    + " <p><em><strong><span style='color:#0070C0; font-size:larger; '>" + parametros.getNomePrincipalEmailChamado() + "</span></strong></em>"
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>Status: " + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Espécie de Atendimento: <span style='color: #000000;'>" + tipoMov + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                    + equipamento
                    + produto
                    + destino
                    + bairro
                    + valor
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Categoria: <span style='color: #000000;'>" + cat + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Solicitante: <span style='color: #000000;'>" + u.getNome() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;T&eacute;cnico: <span style='color: #000000;'>" + t.getNome() + " | " + t.getTelefone() + " | " + t.getEmail() + "  </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + horaAbertura + "</span></span> <br/> "
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Chamado: <span style='color: #000000;'>" + r.getId() + "</span></span> <br/></p>"
                    + " <hr width='90%'/> "
                    + " <p style='text-align: left;'> "
                    + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "...</span></strong> <br /><p/>"
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
                    + " "
                    + " <div width='100%'> <fieldset>  <legend style='color: #0070C0;'> Descrição </legend> &nbsp; &nbsp; " + nome + " </fieldset> </div> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='87%' style='font-size: 11px; color: #525252; font-family: Arial, Helvetica, sans-serif;'><b><br />"
                    + " <p> <a href='http://bahiamarina-cidade.dyndns.info:9090/SistemaChamadoBM/informacoes.xhtml?negocio=" + cod_negocio + "&amp;id=" + r.getId() + "' target='_blank' rel='noopener'> Clique aqui para acompanhar esse chamado.</a></p>"
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
            email.addTo(t.getEmail());
            List<Long> acomp = acompanhantes(r.getId(), "Acompanhar");
            if (!acomp.isEmpty()) {
                for (int i = 0; i < acomp.size(); i++) {
                    a = buscaEmailSolicitante(Long.parseLong("" + acomp.get(i)));
                    email.addCc(a.getEmail());
                }
            }

            if (r.getId_solicitante().getId() != 88) {
                email.addCc(u.getEmail());
            }
            email.send();
            enviarEmail(r.getId(), 1, true);
            enviarEmail(r.getId(), 3, true);
            dataAbertura = null;
            horaAbertura = null;
            fmtData = null;
            fmtHora = null;
            u = null;
            t = null;
            a = null;
            p = null;
            e = null;
            d = null;
            b = null;
            cat = null;
            tipoMov = null;
            assunto = null;
            fmtData = null;
            fmtHora = null;
            dataAbertura = null;
            horaAbertura = null;
            tipoCabecalho = null;
            equipamento = null;
            assuntoAnexo = null;
            cor = null;
            produto = null;
            bairro = null;
            destino = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "AtendimentoBean", "sendMail");
            enviarEmail(r.getId(), 1, false);
            enviarEmail(r.getId(), 3, false);
        }

    }

    public void sendMailMov(MovimentacaoAtendimento r) {
        if (r.isEnviaSolic() == true) {
            Usuario u;
            Usuario t;
            Usuario a;
            Produto p;
            Equipamento e;
            t = buscaEmailSolicitante(r.getId_tecnico().getId());
            u = buscaEmailSolicitante(r.getId_atendimento().getId_solicitante().getId());
            e = buscaEquipamento(r.getCod_equipamento());
            SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
            String dataAbertura = fmtData.format(r.getDataMovimento());
            String horaAbertura = fmtHora.format(r.getHoraMovimento());
            String cat = tabelaCamposEmail(r.getCod_categoria(), 14L);
            String assunto = "";
            String produto = "";
            String valor = "";
            String tipoMov = tabelaCamposEmail(r.getCod_tipoAtendimento(), 15L);
            try {
                HtmlEmail email = new HtmlEmail();
                email.setCharset(parametros.getCodificacaoEmailChamado());
                email.setHostName(parametros.getHostNameChamado());
                email.setSmtpPort(parametros.getSmtpPortChamado());
                email.setAuthenticator(new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
                email.setSSLOnConnect(true);
                email.setFrom(parametros.getLoginEmailChamado(), parametros.getAssuntoEmailChamado() + " - " + negocio.getNegocio() + " - " + t.getNome());
                String cor = "008000";
                String assuntoAnexo = "";
                String tipoCabecalho = " ";

                if (r.isStatusMovimento()) {
                    tipoCabecalho = "Movimentação!";
                    assunto = "Movimentação - " + r.getId_atendimento().getId() + ": ";
                    cor = "0000ff";
                } else {
                    tipoCabecalho = "Encerrado!";
                    assunto = "Encerrado - " + r.getId_atendimento().getId() + ": ";
                    cor = "ff0000";
                }
                if (r.getCod_produto() != null) {
                    p = buscaProduto(r.getCod_produto());
                    produto = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Produto: <span style='color: #000000;'>" + p.getDescricao() + "</span></span> <br/>"
                            + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Quantidade: <span style='color: #000000;'>" + r.getQtdProduto() + "</span></span> <br/>";
                } else {
                    produto = "";
                }

                if (r.getValorProduto() != 0) {
                    valor = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Valor: <span style='color: #000000;'>" + r.getValorProduto() + "</span></span> <br/>";
                } else {
                    valor = "";
                }

                if (r.getObsAtendimento().length() < 50) {
                    assunto = assunto + r.getObsAtendimento().substring(0, r.getObsAtendimento().length()) + " ";
                    assunto = assunto.replaceAll("\n", "");
                    assunto = assunto.replaceAll("\r", "");
                    assuntoAnexo = r.getObsAtendimento().substring(0, r.getObsAtendimento().length()) + " ";
                } else {
                    assunto = assunto + r.getObsAtendimento().substring(0, 50);
                    assunto = assunto.replaceAll("\n", "");
                    assunto = assunto.replaceAll("\r", "");
                    assuntoAnexo = r.getObsAtendimento().substring(0, 50);
                }
                email.setSubject(assunto);
                String equipamento = "";
                if (r.getCod_equipamento() == 0) {
                    equipamento = " ";
                } else if (r.getCod_equipamento() != 178L) {
                    equipamento = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Equipamento: <span style='color: #000000;'>" + e.getNome() + " - " + e.getDescricao() + "</span></span> <br/>";
                }

        
                String nome = r.getObservacao().replaceAll("[\n]", "<br>");

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
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Espécie de Atendimento: <span style='color: #000000;'>" + tipoMov + "</span></span> <br/>"
                        + equipamento
                        + produto
                        + valor
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Categoria: <span style='color: #000000;'>" + cat + "</span></span> <br/>"
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Solicitante: <span style='color: #000000;'>" + u.getNome() + "</span></span> <br/>"
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;T&eacute;cnico: <span style='color: #000000;'>" + t.getNome() + " | " + t.getTelefone() + " | " + t.getEmail() + "  </span></span> <br/>"
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + horaAbertura + "</span></span> <br/> "
                        + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Chamado: <span style='color: #000000;'>" + r.getId_atendimento().getId() + "</span></span> <br/><p/>"
                        + " <hr width='90%'/> "
                        + " <p style='text-align: left;'> "
                        + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "...</span></strong> <br /><p/>"
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
                        //+ " <p> <a href='http://vmsistemas:9090/SistemaChamadoBM/informacoes.xhtml?negocio=" + cod_negocio + "&amp;id=" + r.getId_atendimento().getId() + "' target='_blank' rel='noopener'> (Link Interno) Clique aqui para acompanhar esse chamado.</a></p> "
                        + " <p> <a href='http://bahiamarina-cidade.dyndns.info:9090/SistemaChamadoBM/informacoes.xhtml?negocio=" + cod_negocio + "&amp;id=" + r.getId_atendimento().getId() + "' target='_blank' rel='noopener'> Clique aqui para acompanhar esse chamado.</a></p> "
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
                List<Long> acomp = acompanhantes(r.getId_atendimento().getId(), "Acompanhar");
                if (!acomp.isEmpty()) {
                    for (int i = 0; i < acomp.size(); i++) {
                        a = buscaEmailSolicitante(Long.parseLong("" + acomp.get(i)));
                        email.addCc(a.getEmail());
                    }
                }
                if (r.getId_atendimento().getId_solicitante().getId() != 88) {
                    email.addTo(u.getEmail());
                    email.send();
                }
                enviarEmail(r.getId(), 2, true);
                dataAbertura = null;
                horaAbertura = null;
                fmtData = null;
                fmtHora = null;
            } catch (EmailException ex) {
                FacesUtil.addMsgFatalSQL(ex, "Erro", "AtendimentoBean", "sendMailMov");
                enviarEmail(r.getId(), 2, false);
            }
        }
    }

    public void sendMailReserva(Reserva r) {
        // if (r.isEnviaSolic() == true) {
        Usuario u;
        Usuario t;
        Usuario a;
        Equipamento e;
        //t = buscaEmailSolicitante(r.getId_tecnico().getId());
        u = buscaEmailSolicitante(r.getCod_solicitante());
        //e = buscaEquipamento(r.getCod_equipamento());
        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(r.getDataReserva());
        String horaInicio = fmtHora.format(r.getHoraInicio());

        String horaFim = "Indeterminado";
        if (!r.isIndeterminado()) {
            horaFim = fmtHora.format(r.getHoraFim());
        }
        String sala = tabelaCamposEmail(r.getCod_recurso(), 27L);
        String assunto = "";
        try {
            HtmlEmail email = new HtmlEmail();
            email.setCharset(parametros.getCodificacaoEmailChamado());
            email.setHostName(parametros.getHostNameChamado());
            email.setSmtpPort(parametros.getSmtpPortChamado());
            email.setAuthenticator(new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
            email.setSSLOnConnect(true);
            email.setFrom(parametros.getLoginEmailChamado(), parametros.getAssuntoEmailChamado() + " - " + negocio.getNegocio());
            String cor = "008000";
            String assuntoAnexo = "";
            String tipoCabecalho = " ";

            tipoCabecalho = "Reserva de Recurso!";
            assunto = "Reserva de " + sala + " ";

            if (r.getObservacao().length() < 50) {
                assunto = assunto + r.getObservacao().substring(0, r.getObservacao().length()) + " ";
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObservacao().substring(0, r.getObservacao().length()) + " ";
            } else {
                assunto = assunto + r.getObservacao().substring(0, 50);
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObservacao().substring(0, 50);
            }
            email.setSubject(assunto);
            String nome = r.getObservacao().replaceAll("[\n]", "<br>");
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
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>" + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Recurso: <span style='color: #000000;'>" + sala + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Solicitante: <span style='color: #000000;'>" + u.getNome() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora Inicio: <span style='color: #000000;'>" + horaInicio + "</span></span> <br/> "
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora Fim: <span style='color: #000000;'>" + horaFim + "</span></span> <br/> "
                    + " <hr width='90%'/> "
                    + " <p style='text-align: left;'> "
                    + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "...</span></strong> <br /><p/>"
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
                    //+ " <p> <a href='http://bahiamarina-cidade.dyndns.info:9090/SistemaChamadoBM/informacoes.xhtml?negocio=" + cod_negocio + "&amp;id=" + r.getId_atendimento().getId() + "' target='_blank' rel='noopener'> Clique aqui para acompanhar esse chamado.</a></p> "
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
            List<Long> acomp = acompanhantes(r.getId(), "AcompanharReserva");
            if (!acomp.isEmpty()) {
                for (int i = 0; i < acomp.size(); i++) {
                    a = buscaEmailSolicitante(Long.parseLong("" + acomp.get(i)));
                    email.addCc(a.getEmail());
                }
            }
            email.send();
            enviarEmail(r.getId(), 2, true);
            dataAbertura = null;
            horaInicio = null;
            horaFim = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "ModeloEmail", "sendMailReserva");
            enviarEmail(r.getId(), 2, false);
        }
        //}
    }

    public void sendMailFormulario(Formulario r) {
        // if (r.isEnviaSolic() == true) {
        Usuario u;
        Usuario t;
        Usuario a;
        //e = buscaEquipamento(r.getCod_equipamento());
        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(new Date(System.currentTimeMillis()));
        String hora = fmtHora.format(new Date(System.currentTimeMillis()));
        negocio(r.getCod_negocio());
        String assunto = "";
        try {
            HtmlEmail email = new HtmlEmail();
            email.setCharset(parametros.getCodificacaoEmailChamado());
            email.setHostName(parametros.getHostNameChamado());
            email.setSmtpPort(parametros.getSmtpPortChamado());
            email.setAuthenticator(new DefaultAuthenticator(parametros.getLoginEmailChamado(), parametros.getSenhaEmailChamado()));
            email.setSSLOnConnect(true);
            email.setFrom(parametros.getLoginEmailChamado(), parametros.getAssuntoEmailChamado() + " - " + negocio.getNegocio());
            String cor = "008000";
            String assuntoAnexo = "";
            String tipoCabecalho = " ";

            tipoCabecalho = "Formulário Respondido";
            assunto = r.getTitulo();

            if (r.getObs().length() < 50) {
                assunto = assunto + r.getObs().substring(0, r.getObs().length()) + " ";
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObs().substring(0, r.getObs().length()) + " ";
            } else {
                assunto = assunto + r.getObs().substring(0, 50);
                assunto = assunto.replaceAll("\n", "");
                assunto = assunto.replaceAll("\r", "");
                assuntoAnexo = r.getObs().substring(0, 50);
            }
            email.setSubject(assunto);
            String nome = r.getObs().replaceAll("[\n]", "<br>");
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
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>" + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Formulário: <span style='color: #000000;'>" + r.getTitulo() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + hora + "</span></span> <br/> "
                    + " <p style='text-align: left;'>"
                    + " <hr width='90%'/> "
                    + " <p> <a href='http://bahiamarina-cidade.dyndns.info:9090/SistemaChamadoBM/respostaFormulario.xhtml?formulario_id=" + r.getId() + "&amp;resposta_id=" + r.getResposta_id() + "' target='_blank' rel='noopener'> Clique aqui para visualizar a resposta do formulário.</a></p> "
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
            email.addTo("ricardo@bahiamarina.com.br");
            List<Long> acomp = acompanhantes(r.getId(), "AcompanharFormulario");
            if (!acomp.isEmpty()) {
                for (int i = 0; i < acomp.size(); i++) {
                    a = buscaEmailSolicitante(Long.parseLong("" + acomp.get(i)));
                    email.addCc(a.getEmail());
                }
            }
            email.send();
            enviarEmail(r.getId(), 2, true);
            dataAbertura = null;
            hora = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "ModeloEmail", "sendMailReserva");
            enviarEmail(r.getId(), 2, false);
        }
        //}
    }

    public void sendMailRequerimento(Requerimento r) {
        Requerente req = new Requerente();
        Cliente cli = new Cliente();
        Usuario t;
        Local_Requerimento local = new Local_Requerimento();
        Embarcacao_Loja el = new Embarcacao_Loja();
        local = new GenericDAO<>(Local_Requerimento.class).localEmail(r.getId_local());
        t = new GenericDAO<>(Usuario.class).tecnico(r.getId_tecnico().getId());

        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(r.getDataAbertura());
        String horaAbertura = fmtHora.format(r.getHoraAbertura());
        String nomePrincipal = "SGR - Sistema de Gerenciamento de Requerimentos";
        String assuntoAnexo = "";
        String cor = "008000";
        String tipoCabecalho;
        String assunto;

        String natureza = tabelaCamposEmail(r.getId_natureza(), 18L);
        String cliente = "";
        String vaga = "";
        String embarcacao = "";
        String tipoVaga;
        String requerente = "";
        try {
            HtmlEmail email = new HtmlEmail();
            if (negocio.getCod_negocio() == 6L) {
                email.setHostName("scl06net.netdom.net.br");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("sgr@bahiamarina.com.br", "requisicoes10"));
                email.setSSLOnConnect(true);
                email.setFrom("sgr@bahiamarina.com.br", "SGR - " + negocio.getNegocio());
            } else {
                email.setHostName("scl06net.netdom.net.br");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("sgr@bahiamarina.com.br", "requisicoes10"));
                email.setSSLOnConnect(true);
                email.setFrom("sgr@bahiamarina.com.br", "SGR - " + negocio.getNegocio());
            }
            if (r.getDescricao().length() < 50) {
                email.setSubject("Abertura - " + r.getId() + ": " + r.getDescricao().substring(0, r.getDescricao().length()));
                assuntoAnexo = r.getDescricao().substring(0, r.getDescricao().length()) + " ";
            } else {
                email.setSubject("Abertura - " + r.getId() + ": " + r.getDescricao().substring(0, 50));
                assuntoAnexo = r.getDescricao().substring(0, 50) + " ";
            }

            tipoCabecalho = "Abertura";
            cor = "008000";

            if (cod_negocio == 3L) {
                tipoVaga = "Armário | Loja | Vaga:";
            } else {
                tipoVaga = "Nome a Definir:";
            }

            String url = "";
            if (negocio.getCod_negocio() == 1L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/TI.png";
            } else if (negocio.getCod_negocio() == 2L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/SelectPNB.png";
            } else if (negocio.getCod_negocio() == 3L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Marina.png";
            } else if (negocio.getCod_negocio() == 4L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Cidade.png";
            } else if (negocio.getCod_negocio() == 5L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/TI.png";
            } else if (negocio.getCod_negocio() == 6L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Imperial.png";
            }

            if (r.getId_cliente() != null && r.getId_cliente() != 0) {
                cli = buscaEmailCliente(r.getId_cliente(), negocio.getCod_negocio());
                cliente = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Cliente: <span style='color: #000000;'>" + cli.getNome() + "</span></span> <br/>";
            }

            if (r.getId_requerente() != null && r.getId_requerente() != 0L) {
                req = buscaEmailRequerente(r.getId_requerente());
                requerente = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Requerente: <span style='color: #000000;'>" + req.getNome() + "</span></span> <br/>";
            }

            if (r.getId_embarcacao() != 0 && r.getId_embarcacao() != null) {
                el = buscaLoja(r.getId_embarcacao(), negocio.getCod_negocio());
                //if (!el.getNomeVagaLoja().equals("")) {
                if (el.getNomeVagaLoja() != null) {
                    vaga = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;" + tipoVaga + " <span style='color: #000000;'>" + el.getNomeVagaLoja() + "</span></span> <br/>";
                }
                if (el.getNomeBarco() != null) {
                    //if (!el.getNomeBarco().equals("") || !el.getNomeBarco().equals("*")) {
                    embarcacao = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Embarcação: <span style='color: #000000;'>" + el.getNomeBarco() + "</span></span> <br/>";
                }
            }

            String nome = r.getDescricao().replaceAll("[\n]", "<br>");

            email.setHtmlMsg(
                    " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td align='center' valign='top' style='background-color: #245269;' bgcolor='#53636e;' > "
                    + " <table width='583' border='0' cellspacing='0' cellpadding='0' > "
                    + " <tbody> "
                    + " <tr style='text-align: center;'> "
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff;'><img src='" + url + "' width='583' height='118' /></td> "
                    + " </tr> "
                    + " <tr>\n"
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff; text-align: center;'> "
                    + " <p><em><strong><span style='color:#0070C0; font-size:larger; '>" + nomePrincipal + "</span></strong></em>"
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>Status: " + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                    + requerente
                    + cliente
                    + vaga
                    + embarcacao
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Responsável: <span style='color: #000000;'>" + t.getNome() + " |  " + t.getEmail() + " | " + t.getTelefone() + "  </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Natureza: <span style='color: #000000;'>" + natureza + " </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Local: <span style='color: #000000;'>" + local.getNome() + local.getObservacao() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + horaAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Requerimento: <span style='color: #000000;'>" + r.getId() + "</span></span> <br/></p>"
                    + " <hr width='90%'/> "
                    + " <p style='text-align: left;'> "
                    + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "...</span></strong> <br /><p/>"
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
                    + " "
                    + " <div width='100%'> <fieldset>  <legend style='color: #0070C0;'> Descrição </legend> &nbsp; &nbsp; " + nome + " </fieldset> </div> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='87%' style='font-size: 11px; color: #525252; font-family: Arial, Helvetica, sans-serif;'><b><br /> <span style='color: #ff0000;'>* Este email foi gerado automaticamente pelo Sistema, favor n&atilde;o responder.</span><br /><span style='color: #ff0000;'> Em caso de d&uacute;vidas entre em contato com o t&eacute;cnico. <br/></span></b></td> "
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

            email.addTo(t.getEmail());

            if (r.isEnviaEmailReq() && r.getId_requerente() != null) {
                email.addCc(req.getEmail());
            }

            if (r.isEnviaEmailCli() && r.getId_cliente() != null && r.getId_cliente() != 0) {
                email.addCc(cli.getEmail());
            }
            email.send();

            dataAbertura = null;
            horaAbertura = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            System.out.println("Erro: " + ex);
            FacesUtil.addMsgFatalSQL(ex, "Erro", "RequerimentoBean", "verificaAcesso");
        }

    }

    public void sendMailMovRequerimento(MovimentacaoRequerimento r) {
        Requerente req;
        Usuario t;
        Cliente cli = null;
        Local_Requerimento local;
        Embarcacao_Loja el;
        local = new GenericDAO<>(Usuario.class).localEmail(r.getId_local());
        t = new GenericDAO<>(Usuario.class).tecnico(r.getId_tecnico().getId());
        req = buscaEmailRequerente(r.getId_requerente());

        SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
        String dataAbertura = fmtData.format(r.getDataMovimento());
        String horaAbertura = fmtHora.format(r.getHoraMovimento());
        String nomePrincipal = "SGR - Sistema de Gerenciamento de Requerimentos";
        String assuntoAnexo = "";
        String cor = "008000";
        String tipoCabecalho;
        String assunto;
        String natureza = tabelaCamposEmail(r.getId_natureza(), 18L);
        String cliente = "";
        String vaga = "";
        String embarcacao = "";
        String tipoVaga;
        String requerente = "";
        if (cod_negocio == 3L) {
            tipoVaga = "Armário | Loja | Vaga:";
        } else {
            tipoVaga = "Nome a Definir:";
        }

        try {
            HtmlEmail email = new HtmlEmail();
            if (negocio.getCod_negocio() == 6L) {
                email.setHostName("scl06net.netdom.net.br");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("sgr@bahiamarina.com.br", "requisicoes10"));
                email.setSSLOnConnect(true);
                email.setFrom("sgr@bahiamarina.com.br", "SGR - " + negocio.getNegocio());
            } else {
                email.setHostName("scl06net.netdom.net.br");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("sgr@bahiamarina.com.br", "requisicoes10"));
                email.setSSLOnConnect(true);
                email.setFrom("sgr@bahiamarina.com.br", "SGR - " + negocio.getNegocio());
            }
            if (r.getCod_status() == 3L || r.getCod_status() == 4L) {
                tipoCabecalho = tabelaCamposEmail(r.getCod_status(), 20L);
                assunto = "Encerrado " + r.getId_requerimento().getId();
                cor = "ff0000";
            } else {
                tipoCabecalho = tabelaCamposEmail(r.getCod_status(), 20L);
                assunto = "Movimentação " + r.getId_requerimento().getId();
                cor = "0000ff";
            }

            if (r.getObsRequerimento().length() < 50) {
                email.setSubject(assunto + " - " + r.getObsRequerimento().substring(0, r.getObsRequerimento().length()) + "");
                assuntoAnexo = r.getObsRequerimento().substring(0, r.getObsRequerimento().length());
            } else {
                email.setSubject(assunto + " - " + r.getObsRequerimento().substring(0, 50) + "");
                assuntoAnexo = r.getObsRequerimento().substring(0, 50);
            }

            String url = "";
            if (negocio.getCod_negocio() == 1L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/TI.png";
            } else if (negocio.getCod_negocio() == 2L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/SelectPNB.png";
            } else if (negocio.getCod_negocio() == 3L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Marina.png";
            } else if (negocio.getCod_negocio() == 4L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Cidade.png";
            } else if (negocio.getCod_negocio() == 5L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/TI.png";
            } else if (negocio.getCod_negocio() == 6L) {
                url = "http://www.bahiamarina.com.br/intranet/Email/imagens/Imperial.png";
            }

            if (r.getId_cliente() != null && r.getId_cliente() != 0) {
                cli = buscaEmailCliente(r.getId_cliente(), negocio.getCod_negocio());
                cliente = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Cliente: <span style='color: #000000;'>" + cli.getNome() + "</span></span> <br/>";
            }
            if (r.getId_requerente() != null && r.getId_requerente() != 0L) {
                req = buscaEmailRequerente(r.getId_requerente());
                requerente = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Requerente: <span style='color: #000000;'>" + req.getNome() + "</span></span> <br/> ";
            }

            if (r.getId_embarcacao() != 0 && r.getId_embarcacao() != null) {
                el = buscaLoja(r.getId_embarcacao(), negocio.getCod_negocio());
                if (el.getNomeVagaLoja() != null) {
                    vaga = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;" + tipoVaga + " <span style='color: #000000;'>" + el.getNomeVagaLoja() + "</span></span> <br/>";
                }
                //System.out.println("EXFT" + el.getNomeBarco());
                if (el.getNomeBarco() != null) {
                    //if (!el.getNomeBarco().equals("") || !el.getNomeBarco().equals("*")) {
                    embarcacao = " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Embarcação: <span style='color: #000000;'>" + el.getNomeBarco() + "</span></span> <br/>";
                }
            }

            String nome = r.getObservacao().replaceAll("[\n]", "<br>");

            email.setHtmlMsg(
                    " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td align='center' valign='top' style='background-color: #245269;' bgcolor='#53636e;' > "
                    + " <table width='583' border='0' cellspacing='0' cellpadding='0' > "
                    + " <tbody> "
                    + " <tr style='text-align: center;'> "
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff;'><img src='" + url + "' width='583' height='118' /></td> "
                    + " </tr> "
                    + " <tr>\n"
                    + " <td align='left' valign='top' bgcolor='#FFFFFF' style='background-color: #ffffff; text-align: center;'> "
                    + " <p><em><strong><span style='color:#0070C0; font-size:larger; '>" + nomePrincipal + "</span></strong></em>"
                    + " <p><strong><span style='color: #" + cor + "; font-size: larger;'>Status: " + tipoCabecalho + "</span></strong>"
                    + " <hr width='90%' />  "
                    + " <p style='text-align: left;'>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Neg&oacute;cio: <span style='color: #000000;'>" + negocio.getCod_negocio() + " - " + negocio.getNegocio() + " </span></span> <br/>"
                    + requerente
                    + cliente
                    + vaga
                    + embarcacao
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Responsável: <span style='color: #000000;'>" + t.getNome() + " |  " + t.getTelefone() + " | " + t.getEmail() + "  </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Natureza: <span style='color: #000000;'>" + natureza + " </span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Local: <span style='color: #000000;'>" + local.getNome() + local.getObservacao() + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Data: <span style='color: #000000;'>" + dataAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Hora: <span style='color: #000000;'>" + horaAbertura + "</span></span> <br/>"
                    + " <span style='color: #0070C0; '> &nbsp; &nbsp; &nbsp; &nbsp;Requerimento: <span style='color: #000000;'>" + r.getId_requerimento().getId() + "</span></span> <br/></p>"
                    + " <hr width='90%'/> "
                    + " <p style='text-align: left;'> "
                    + " <strong><span style='font-size: 18px; color: #0070c0;'>&nbsp; &nbsp; &nbsp; Assunto: </span> <span style='font-size: font-size: 18px; color: #000000;'> " + assuntoAnexo + "...</span></strong> <br /><p/>"
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
                    + " "
                    + " <div width='100%'> <fieldset>  <legend style='color: #0070C0;'> Descrição </legend> &nbsp; &nbsp; " + nome + " </fieldset> </div> "
                    + " <table width='100%' border='0' cellspacing='0' cellpadding='0'> "
                    + " <tbody> "
                    + " <tr> "
                    + " <td width='87%' style='font-size: 11px; color: #525252; font-family: Arial, Helvetica, sans-serif;'><b><br /> <span style='color: #ff0000;'>* Este email foi gerado automaticamente pelo Sistema, favor n&atilde;o responder.</span><br /><span style='color: #ff0000;'> Em caso de d&uacute;vidas entre em contato com o t&eacute;cnico. <br/></span></b></td> "
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

            email.addTo(t.getEmail());

            if (r.isEnviaEmailReq() && r.getId_requerente() != null) {
                email.addCc(req.getEmail());
            }

            if (r.isEnviaEmailCli() && r.getId_cliente() != null && r.getId_cliente() != 0) {
                //System.out.println((r.isEnviaEmailCli() && (r.getId_cliente() != null || r.getId_cliente() != 0)));
                //System.out.println((r.getId_cliente() != null || r.getId_cliente() != 0));
                //System.out.println(r.isEnviaEmailCli() && r.getId_cliente() != 0);
                email.addCc(cli.getEmail());
            }

            email.send();
            dataAbertura = null;
            horaAbertura = null;
            fmtData = null;
            fmtHora = null;
        } catch (EmailException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "RequerimentoBean", "sendMailMov");
        }

    }

    public final List<Long> acompanhantes(Long id, String tabela) {
        try {
            List<Long> cod = new ArrayList<>();

            sql = " SELECT * FROM " + tabela + " WHERE cod_tabela = " + id + " ";

            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cod.add(rs.getLong("cod_usuario"));
            }
            rs.close();
            ConexaoSQL.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            return cod;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ModeloEmail", "acompanhantes");
            System.out.println("public List<Long> acompanhantes(Long id) Erro:" + e);
            return null;
        }
    }

    public final void enviarEmail(Long a, int i, boolean b) {

        try {
            switch (i) {
                case 1:
                    sql = "update Atendimento set passouEmail = ? where id =  " + a + "";
                    break;
                case 2:
                    sql = "update movimentacaoAtendimento set passouEmail = ? where id =  " + a + "";
                    break;
                case 3:
                    sql = "update movimentacaoAtendimento set passouEmail = ? where id_atendimento =  " + a + "";
                    break;
                default:
                    break;
            }

            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setBoolean(1, b);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            ConexaoSQL = null;
            stmt = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "enviarEmail");
            System.out.println("public void enviarEmail(Long a, int i, boolean b) Erro:" + e);
        }
    }

    public final Usuario buscaEmailSolicitante(Long r) {
        Usuario u = new Usuario();
        sql = "SELECT * FROM Usuario where id = " + r + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
                u.setTelefone(rs.getString("telefone"));
                u.setCelular(rs.getString("celular"));
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

    public final Produto buscaProduto(Long r) {
        Produto p = new Produto();
        sql = "SELECT * FROM Produto where id = " + r + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                p.setDescricao(rs.getString("descricao"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "buscaProduto");
            System.out.println("public Produto buscaProduto(Long r) Erro:" + e);
        }
        return p;

    }

    public final Equipamento buscaEquipamento(Long r) {
        Equipamento e = new Equipamento();
        sql = "SELECT * FROM Equipamento where id = " + r + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                e.setNome(rs.getString("nome"));
                e.setDescricao(rs.getString("descricao"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "AtendimentoBean", "buscaEquipamento");
            System.out.println("public Equipamento buscaEquipamento(Long r)  Erro:" + ex);
        }
        return e;

    }

    public final String tabelaCamposEmail(Long cod, Long tabela) {
        String id = "";
        sql = "SELECT descricao FROM Campos where cod = " + cod + " and tabela =" + tabela + " and cod_negocio=" + cod_negocio + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                id = rs.getString("descricao");
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "tabelaCamposEmail");
            System.out.println(" public String tabelaCamposEmail(Long cod, Long tabela) Erro:" + e);
        }
        return id;

    }

    public final Negocio negocio(Long codigo_negocio) {

        if (cod_negocio == null) {
            cod_negocio = codigo_negocio;
        }

        Negocio m = new Negocio();
        sql = " SELECT neg.descricao, m.* FROM Negocio m "
                + " LEFT join Campos neg on m.cod_negocio = neg.cod and neg.tabela = 21 "
                + " where m.cod_negocio =" + cod_negocio + " "
                + " order by neg.descricao ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setCod_negocio(rs.getLong("cod_negocio"));
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
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoBean", "negocio");
            System.out.println("public Negocio negocio() Erro:" + e);
        }
        return negocio = m;
    }

    public Usuario buscaEmailTec(Long r) {
        Usuario t = new Usuario();
        String sql2 = "SELECT * FROM Tecnico where id = " + r + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();
            while (rs.next()) {
                t.setEmail(rs.getString("email"));
                t.setNome(rs.getString("nome"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "buscaEmailTec");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ModeloEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;

    }

    public Embarcacao_Loja buscaLoja(Long r, Long cod_negocio) {
        Embarcacao_Loja t = new Embarcacao_Loja();
        String sql2 = "";
        if (cod_negocio == 3L) {
            sql2 = "SELECT * FROM Embarcacao_Loja where codigoSTAI = " + r + "";
        } else {
            sql2 = "SELECT * FROM Embarcacao_Loja where id = " + r + "";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                t.setId(rs.getLong("id"));
                t.setNomeVagaLoja(rs.getString("nomeVagaLoja"));
                t.setNomeBarco(rs.getString("nomeBarco"));
                if (rs.getString("nomeVagaLoja").equals("")) {
                    t.setNomeVagaLoja(null);
                }

                if (rs.getString("nomeBarco").equals("") || rs.getString("nomeBarco").equals("*")) {
                    t.setNomeBarco(null);
                }
            }

            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "buscaLoja");
            System.out.println(e);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ModeloEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;

    }

    public Requerente buscaEmailRequerente(Long r) {
        Requerente u = new Requerente();
        String sql2 = "SELECT * FROM Requerente where id = " + r + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "buscaEmailRequerente");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ModeloEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;

    }

    public Cliente buscaEmailCliente(Long r, Long cod_negocio) {
        Cliente u = new Cliente();

        String sql2 = "";
        if (cod_negocio == 3L) {
            sql2 = "SELECT * FROM Cliente where codigoSTAI = " + r + "";
        } else {
            sql2 = "SELECT * FROM Cliente where id = " + r + "";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();
            while (rs.next()) {
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "buscaEmailCliente");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ModeloEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;

    }

}
