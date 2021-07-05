package Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

@Entity
public class Evento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column()
    private Long id;

    @Column()
    private Long codUserPrincipal;

    @Column()
    private Long cod_negocio;

    @Column()
    private String titulo;

    @Column()
    private boolean diaTodo;

    @Column()
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicio;

    @Column()
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fim;

    @Column(length = 8000)
    private String descricao;

    @Column()
    private boolean status;

    @Column
    private String tipoEvento;

    @Transient()
    private String nomeUsuario;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Transient
    private List<Long> ListaAcompanhante = new ArrayList<>();

    public Evento() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Evento(Long id, Long codUserPrincipal, Long cod_negocio, String titulo, Date inicio, Date fim, String descricao, boolean status, String nomeUsuario, List<Long> ListaAcompanhante, String tipoEvento, boolean diaTodo) {
        this.id = id;
        this.codUserPrincipal = codUserPrincipal;
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.titulo = titulo;
        this.inicio = inicio;
        this.fim = fim;
        this.descricao = descricao;
        this.status = status;
        this.nomeUsuario = nomeUsuario;
        this.tipoEvento = tipoEvento;
        this.diaTodo = diaTodo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getCodUserPrincipal() {
        return codUserPrincipal;
    }

    public void setCodUserPrincipal(Long codUserPrincipal) {
        this.codUserPrincipal = codUserPrincipal;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public List<Long> getListaAcompanhante() {
        return ListaAcompanhante;
    }

    public void setListaAcompanhante(List<Long> ListaAcompanhante) {
        this.ListaAcompanhante = ListaAcompanhante;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public boolean isDiaTodo() {
        return diaTodo;
    }

    public void setDiaTodo(boolean diaTodo) {
        this.diaTodo = diaTodo;
    }

}
