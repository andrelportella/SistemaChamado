/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Parametros implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String hostNameChamado;

    @Column
    private int smtpPortChamado;
    @Column
    private String hostNameRequerimento;

    @Column
    private int smtpPortRequerimento;

    @Column
    private String loginEmailChamado;

    @Column
    private String senhaEmailChamado;

    @Column
    private String assuntoEmailChamado;

    @Column
    private String assuntoEmailRequerimento;

    @Column
    private String loginEmailRequerimento;

    @Column
    private String senhaEmailRequerimento;

    @Column
    private String urlLogoEmail;

    @Column
    private String codificacaoEmailChamado;

    @Column
    private String codificacaoEmailRequerimento;

    @Column
    private String nomePrincipalEmailChamado;

    @Column
    private String nomePrincipalEmailRequerimento;

    @Column
    private String corEmailAbertura;

    @Column
    private String corEmailMovimento;

    @Column
    private String corEmailEncerramento;

    @Column
    private Long codSupervisorRequerimento;

    @Column
    private Long codSuprimentoCompra;

    @Column
    private Long codSuprimentoTroca;

    @Column
    private Long codPerdaRouboVencimento;

    @Column
    private Long codPreventiva;

    @Column
    private Long codTrocaLocal;

    @Column
    private Long codAtividadeAdm;

    @Column
    private Long codRecursoSolic;

    @Column
    private Long codJuridico;
    
    @Column
    private boolean cadGeral;

    @Column
    private boolean cadInfor;

    @Column
    private int qtdDiasAgendamento;

    @Transient
    private FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    private HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    @Column
    private String diretorioArquivo;

    @Column
    private String pastaBC;

    @Column
    private String pastaChamado;

    @Column
    private String pastaEquipamento;

    @Column
    private String pastaRequerimento;

    @Column
    private String pastaNoticia;

    @Column
    private String pastaSoftware;

    @Column
    private int QtdDiasChamadoAtrasado;

    @Column
    private int QtdRegistroApagaChamado;

    @Column
    private String emailProgramador;

    @Column
    private boolean padraoEmailAdm;

    @Column
    private boolean padraoEmailTarefa;

    @Column
    private boolean padraoEmailAgendamento;

    public Parametros() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Parametros(Long id, String hostNameChamado, int smtpPortChamado, String hostNameRequerimento, int smtpPortRequerimento, String loginEmailChamado, String senhaEmailChamado, String loginEmailRequerimento, String senhaEmailRequerimento, String urlLogoEmail, String codificacaoEmailChamado, String codificacaoEmailRequerimento, String nomePrincipalEmailChamado, String nomePrincipalEmailRequerimento, String corEmailAbertura, String corEmailMovimento, String corEmailEncerramento, Long codSupervisorRequerimento, Long codSuprimentoCompra, Long codSuprimentoTroca, Long codPreventiva, Long codTrocaLocal, Long codAtividadeAdm, boolean cadGeral, boolean cadInfor, int qtdDiasAgendamento, Long cod_negocio, String diretorioArquivo, String pastaBC, String pastaChamado, String pastaEquipamento, String pastaNoticia, String pastaSoftware, int QtdDiasChamadoAtrasado, int QtdRegistroApagaChamado, String emailProgramador, boolean padraoEmailAdm, boolean padraoEmailTarefa, boolean padraoEmailAgendamento, Long codPerdaRouboVencimento, Long codRecursoSolic, String pastaRequerimento) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.hostNameChamado = hostNameChamado;
        this.smtpPortChamado = smtpPortChamado;
        this.hostNameRequerimento = hostNameRequerimento;
        this.smtpPortRequerimento = smtpPortRequerimento;
        this.loginEmailChamado = loginEmailChamado;
        this.senhaEmailChamado = senhaEmailChamado;
        this.loginEmailRequerimento = loginEmailRequerimento;
        this.senhaEmailRequerimento = senhaEmailRequerimento;
        this.urlLogoEmail = urlLogoEmail;
        this.codificacaoEmailChamado = codificacaoEmailChamado;
        this.codificacaoEmailRequerimento = codificacaoEmailRequerimento;
        this.nomePrincipalEmailChamado = nomePrincipalEmailChamado;
        this.nomePrincipalEmailRequerimento = nomePrincipalEmailRequerimento;
        this.corEmailAbertura = corEmailAbertura;
        this.corEmailMovimento = corEmailMovimento;
        this.corEmailEncerramento = corEmailEncerramento;
        this.codSupervisorRequerimento = codSupervisorRequerimento;
        this.codSuprimentoCompra = codSuprimentoCompra;
        this.codSuprimentoTroca = codSuprimentoTroca;
        this.codPreventiva = codPreventiva;
        this.codTrocaLocal = codTrocaLocal;
        this.codAtividadeAdm = codAtividadeAdm;
        this.cadGeral = cadGeral;
        this.cadInfor = cadInfor;
        this.qtdDiasAgendamento = qtdDiasAgendamento;
        this.cod_negocio = cod_negocio;
        this.diretorioArquivo = diretorioArquivo;
        this.pastaBC = pastaBC;
        this.pastaChamado = pastaChamado;
        this.pastaEquipamento = pastaEquipamento;
        this.pastaNoticia = pastaNoticia;
        this.pastaSoftware = pastaSoftware;
        this.pastaRequerimento = pastaRequerimento;
        this.QtdDiasChamadoAtrasado = QtdDiasChamadoAtrasado;
        this.QtdRegistroApagaChamado = QtdRegistroApagaChamado;
        this.emailProgramador = emailProgramador;
        this.padraoEmailAdm = padraoEmailAdm;
        this.padraoEmailTarefa = padraoEmailTarefa;
        this.padraoEmailAgendamento = padraoEmailAgendamento;
        this.codPerdaRouboVencimento = codPerdaRouboVencimento;
        this.codRecursoSolic = codRecursoSolic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginEmailChamado() {
        return loginEmailChamado;
    }

    public void setLoginEmailChamado(String loginEmailChamado) {
        this.loginEmailChamado = loginEmailChamado;
    }

    public String getSenhaEmailChamado() {
        return senhaEmailChamado;
    }

    public void setSenhaEmailChamado(String senhaEmailChamado) {
        this.senhaEmailChamado = senhaEmailChamado;
    }

    public String getLoginEmailRequerimento() {
        return loginEmailRequerimento;
    }

    public void setLoginEmailRequerimento(String loginEmailRequerimento) {
        this.loginEmailRequerimento = loginEmailRequerimento;
    }

    public String getSenhaEmailRequerimento() {
        return senhaEmailRequerimento;
    }

    public void setSenhaEmailRequerimento(String senhaEmailRequerimento) {
        this.senhaEmailRequerimento = senhaEmailRequerimento;
    }

    public String getUrlLogoEmail() {
        return urlLogoEmail;
    }

    public void setUrlLogoEmail(String urlLogoEmail) {
        this.urlLogoEmail = urlLogoEmail;
    }

    public Long getCodPreventiva() {
        return codPreventiva;
    }

    public void setCodPreventiva(Long codPreventiva) {
        this.codPreventiva = codPreventiva;
    }

    public Long getCodSupervisorRequerimento() {
        return codSupervisorRequerimento;
    }

    public void setCodSupervisorRequerimento(Long codSupervisorRequerimento) {
        this.codSupervisorRequerimento = codSupervisorRequerimento;
    }

    public String getHostNameChamado() {
        return hostNameChamado;
    }

    public void setHostNameChamado(String hostNameChamado) {
        this.hostNameChamado = hostNameChamado;
    }

    public int getSmtpPortChamado() {
        return smtpPortChamado;
    }

    public void setSmtpPortChamado(int smtpPortChamado) {
        this.smtpPortChamado = smtpPortChamado;
    }

    public String getHostNameRequerimento() {
        return hostNameRequerimento;
    }

    public void setHostNameRequerimento(String hostNameRequerimento) {
        this.hostNameRequerimento = hostNameRequerimento;
    }

    public int getSmtpPortRequerimento() {
        return smtpPortRequerimento;
    }

    public void setSmtpPortRequerimento(int smtpPortRequerimento) {
        this.smtpPortRequerimento = smtpPortRequerimento;
    }

    public String getNomePrincipalEmailChamado() {
        return nomePrincipalEmailChamado;
    }

    public void setNomePrincipalEmailChamado(String nomePrincipalEmailChamado) {
        this.nomePrincipalEmailChamado = nomePrincipalEmailChamado;
    }

    public String getNomePrincipalEmailRequerimento() {
        return nomePrincipalEmailRequerimento;
    }

    public void setNomePrincipalEmailRequerimento(String nomePrincipalEmailRequerimento) {
        this.nomePrincipalEmailRequerimento = nomePrincipalEmailRequerimento;
    }

    public boolean isCadGeral() {
        return cadGeral;
    }

    public void setCadGeral(boolean cadGeral) {
        this.cadGeral = cadGeral;
    }

    public boolean isCadInfor() {
        return cadInfor;
    }

    public void setCadInfor(boolean cadInfor) {
        this.cadInfor = cadInfor;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public Long getCodSuprimentoCompra() {
        return codSuprimentoCompra;
    }

    public void setCodSuprimentoCompra(Long codSuprimentoCompra) {
        this.codSuprimentoCompra = codSuprimentoCompra;
    }

    public Long getCodSuprimentoTroca() {
        return codSuprimentoTroca;
    }

    public void setCodSuprimentoTroca(Long codSuprimentoTroca) {
        this.codSuprimentoTroca = codSuprimentoTroca;
    }

    public String getCorEmailAbertura() {
        return corEmailAbertura;
    }

    public void setCorEmailAbertura(String corEmailAbertura) {
        this.corEmailAbertura = corEmailAbertura;
    }

    public String getCorEmailMovimento() {
        return corEmailMovimento;
    }

    public void setCorEmailMovimento(String corEmailMovimento) {
        this.corEmailMovimento = corEmailMovimento;
    }

    public String getCorEmailEncerramento() {
        return corEmailEncerramento;
    }

    public void setCorEmailEncerramento(String corEmailEncerramento) {
        this.corEmailEncerramento = corEmailEncerramento;
    }

    public Long getCodTrocaLocal() {
        return codTrocaLocal;
    }

    public void setCodTrocaLocal(Long codTrocaLocal) {
        this.codTrocaLocal = codTrocaLocal;
    }

    public Long getCodAtividadeAdm() {
        return codAtividadeAdm;
    }

    public void setCodAtividadeAdm(Long codAtividadeAdm) {
        this.codAtividadeAdm = codAtividadeAdm;
    }

    public String getCodificacaoEmailChamado() {
        return codificacaoEmailChamado;
    }

    public void setCodificacaoEmailChamado(String codificacaoEmailChamado) {
        this.codificacaoEmailChamado = codificacaoEmailChamado;
    }

    public String getCodificacaoEmailRequerimento() {
        return codificacaoEmailRequerimento;
    }

    public void setCodificacaoEmailRequerimento(String codificacaoEmailRequerimento) {
        this.codificacaoEmailRequerimento = codificacaoEmailRequerimento;
    }

    public int getQtdDiasAgendamento() {
        return qtdDiasAgendamento;
    }

    public void setQtdDiasAgendamento(int qtdDiasAgendamento) {
        this.qtdDiasAgendamento = qtdDiasAgendamento;
    }

    public FacesContext getFc() {
        return fc;
    }

    public void setFc(FacesContext fc) {
        this.fc = fc;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public String getDiretorioArquivo() {
        return diretorioArquivo;
    }

    public void setDiretorioArquivo(String diretorioArquivo) {
        this.diretorioArquivo = diretorioArquivo;
    }

    public String getPastaBC() {
        return pastaBC;
    }

    public void setPastaBC(String pastaBC) {
        this.pastaBC = pastaBC;
    }

    public String getPastaChamado() {
        return pastaChamado;
    }

    public void setPastaChamado(String pastaChamado) {
        this.pastaChamado = pastaChamado;
    }

    public String getPastaEquipamento() {
        return pastaEquipamento;
    }

    public void setPastaEquipamento(String pastaEquipamento) {
        this.pastaEquipamento = pastaEquipamento;
    }

    public String getPastaNoticia() {
        return pastaNoticia;
    }

    public void setPastaNoticia(String pastaNoticia) {
        this.pastaNoticia = pastaNoticia;
    }

    public String getPastaSoftware() {
        return pastaSoftware;
    }

    public void setPastaSoftware(String pastaSoftware) {
        this.pastaSoftware = pastaSoftware;
    }

    public int getQtdDiasChamadoAtrasado() {
        return QtdDiasChamadoAtrasado;
    }

    public void setQtdDiasChamadoAtrasado(int QtdDiasChamadoAtrasado) {
        this.QtdDiasChamadoAtrasado = QtdDiasChamadoAtrasado;
    }

    public int getQtdRegistroApagaChamado() {
        return QtdRegistroApagaChamado;
    }

    public void setQtdRegistroApagaChamado(int QtdRegistroApagaChamado) {
        this.QtdRegistroApagaChamado = QtdRegistroApagaChamado;
    }

    public String getEmailProgramador() {
        return emailProgramador;
    }

    public void setEmailProgramador(String emailProgramador) {
        this.emailProgramador = emailProgramador;
    }

    public boolean isPadraoEmailAdm() {
        return padraoEmailAdm;
    }

    public void setPadraoEmailAdm(boolean padraoEmailAdm) {
        this.padraoEmailAdm = padraoEmailAdm;
    }

    public boolean isPadraoEmailTarefa() {
        return padraoEmailTarefa;
    }

    public void setPadraoEmailTarefa(boolean padraoEmailTarefa) {
        this.padraoEmailTarefa = padraoEmailTarefa;
    }

    public boolean isPadraoEmailAgendamento() {
        return padraoEmailAgendamento;
    }

    public void setPadraoEmailAgendamento(boolean padraoEmailAgendamento) {
        this.padraoEmailAgendamento = padraoEmailAgendamento;
    }

    public String getAssuntoEmailChamado() {
        return assuntoEmailChamado;
    }

    public void setAssuntoEmailChamado(String assuntoEmailChamado) {
        this.assuntoEmailChamado = assuntoEmailChamado;
    }

    public String getAssuntoEmailRequerimento() {
        return assuntoEmailRequerimento;
    }

    public void setAssuntoEmailRequerimento(String assuntoEmailRequerimento) {
        this.assuntoEmailRequerimento = assuntoEmailRequerimento;
    }

    public Long getCodPerdaRouboVencimento() {
        return codPerdaRouboVencimento;
    }

    public void setCodPerdaRouboVencimento(Long codPerdaRouboVencimento) {
        this.codPerdaRouboVencimento = codPerdaRouboVencimento;
    }

    public Long getCodRecursoSolic() {
        return codRecursoSolic;
    }

    public void setCodRecursoSolic(Long codRecursoSolic) {
        this.codRecursoSolic = codRecursoSolic;
    }

    public String getPastaRequerimento() {
        return pastaRequerimento;
    }

    public void setPastaRequerimento(String pastaRequerimento) {
        this.pastaRequerimento = pastaRequerimento;
    }

    public Long getCodJuridico() {
        return codJuridico;
    }

    public void setCodJuridico(Long codJuridico) {
        this.codJuridico = codJuridico;
    }

}
