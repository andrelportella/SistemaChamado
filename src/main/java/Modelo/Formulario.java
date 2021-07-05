/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.List;
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
public class Formulario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titulo;

    private String obs;

    private boolean slide;
    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    private Long cod_negocio;

    private String link;

    private String corFundo;

    @Column
    private boolean enviaEmail;

    @Transient
    private List<Long> listaAcompanhante;

    @Transient
    private Long resposta_id;

    public Long getId() {
        return id;
    }

    public Formulario() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Formulario(Long id, String titulo, String obs, String link, boolean slide, boolean enviaEmail, String corFundo, List<Long> listaAcompanhante, Long resposta_id) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.titulo = titulo;
        this.obs = obs;
        this.link = link;
        this.slide = slide;
        this.enviaEmail = enviaEmail;
        this.corFundo = corFundo;
        this.listaAcompanhante = listaAcompanhante;
        this.resposta_id = resposta_id;
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

    public boolean isSlide() {
        return slide;
    }

    public void setSlide(boolean slide) {
        this.slide = slide;
    }

    public boolean isEnviaEmail() {
        return enviaEmail;
    }

    public void setEnviaEmail(boolean enviaEmail) {
        this.enviaEmail = enviaEmail;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(String corFundo) {
        this.corFundo = corFundo;
    }

    public List<Long> getListaAcompanhante() {
        return listaAcompanhante;
    }

    public void setListaAcompanhante(List<Long> listaAcompanhante) {
        this.listaAcompanhante = listaAcompanhante;
    }

    public Long getResposta_id() {
        return resposta_id;
    }

    public void setResposta_id(Long resposta_id) {
        this.resposta_id = resposta_id;
    }
}
