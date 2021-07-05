/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataReserva;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaInicio;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaFim;

    @Column
    private Long cod_solicitante;

    @Transient
    private String solicitante;

    @Transient
    private String participante;

    @Column
    private Long cod_recurso;

    @Transient
    private String recurso;

    @Column(length = 8000)
    private String observacao;
    
    @Column()
    private boolean indeterminado;

    @Column()
    private boolean status;

    @Transient
    private FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    private HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    @Column
    private Long cod_negocio;
    @Transient
    private List<Long> listaAcompanhante;

    public Reserva() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Reserva(Long id, Date dataReserva, Date horaInicio, Date horaFim, Long cod_solicitante, String solicitante, Long cod_sala, String sala, String observacao, String participante, List<Long> listaAcompanhante, boolean indeterminado, boolean status) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.dataReserva = dataReserva;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.cod_solicitante = cod_solicitante;
        this.solicitante = solicitante;
        this.cod_recurso = cod_sala;
        this.recurso = sala;
        this.observacao = observacao;
        this.participante = participante;
        this.listaAcompanhante = listaAcompanhante;
        this.indeterminado = indeterminado;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCod_solicitante() {
        return cod_solicitante;
    }

    public void setCod_solicitante(Long cod_solicitante) {
        this.cod_solicitante = cod_solicitante;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Long getCod_recurso() {
        return cod_recurso;
    }

    public void setCod_recurso(Long cod_recurso) {
        this.cod_recurso = cod_recurso;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(Date horaFim) {
        this.horaFim = horaFim;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public String getParticipante() {
        return participante;
    }

    public void setParticipante(String participante) {
        this.participante = participante;
    }

    public List<Long> getListaAcompanhante() {
        return listaAcompanhante;
    }

    public void setListaAcompanhante(List<Long> listaAcompanhante) {
        this.listaAcompanhante = listaAcompanhante;
    }

    public boolean isIndeterminado() {
        return indeterminado;
    }

    public void setIndeterminado(boolean indeterminado) {
        this.indeterminado = indeterminado;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
