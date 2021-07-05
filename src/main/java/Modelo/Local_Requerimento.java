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
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Local_Requerimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue()
    private Long id;

    private String nome;

    private String observacao;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    public Local_Requerimento() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Local_Requerimento(Long id, String nome, String observacao) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.nome = nome;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

}
