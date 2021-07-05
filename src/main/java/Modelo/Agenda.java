/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.Date;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Agenda extends AbstractEntity {

    @Column()
    private String titulo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDe;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAte;

    @Column()
    private boolean diaTodo;

    @Column()
    private Long id_usuario;

    private String descricao;

    @Transient()
    private String nomeUsuario;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    public Agenda(String titulo, Date dataDe, Date dataAte, boolean diaTodo, Long id_usuario, String nomeUsuario, String descricao) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.titulo = titulo;
        this.dataDe = dataDe;
        this.dataAte = dataAte;
        this.diaTodo = diaTodo;
        this.id_usuario = id_usuario;
        this.nomeUsuario = nomeUsuario;
        this.descricao = descricao;
    }

    public Agenda() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getDataDe() {
        return dataDe;
    }

    public void setDataDe(Date dataDe) {
        this.dataDe = dataDe;
    }

    public Date getDataAte() {
        return dataAte;
    }

    public void setDataAte(Date dataAte) {
        this.dataAte = dataAte;
    }

    public boolean isDiaTodo() {
        return diaTodo;
    }

    public void setDiaTodo(boolean diaTodo) {
        this.diaTodo = diaTodo;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
