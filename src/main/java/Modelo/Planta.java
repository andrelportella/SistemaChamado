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
public class Planta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titulo;

    private String obs;
    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    private Long cod_negocio;
    
    @Column(length = 8000)
    private String link;

    public Planta() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Planta(Long id, String titulo, String obs, Long cod_negocio, String link) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.titulo = titulo;
        this.obs = obs;
        this.cod_negocio = cod_negocio;
        this.link = link;
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

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public FacesContext getFc() {
        return fc;
    }

    public void setFc(FacesContext fc) {
        this.fc = fc;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}