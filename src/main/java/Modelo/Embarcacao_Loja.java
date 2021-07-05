/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.Objects;
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
public class Embarcacao_Loja implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nomeBarco;

    private String nomeVagaLoja;

    private Long codigoSTAI;

    private String cliente;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    public Embarcacao_Loja(Long id, String nomeBarco, String nomeVagaLoja, Long codigoSTAI, String cliente) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.nomeBarco = nomeBarco;
        this.nomeVagaLoja = nomeVagaLoja;
        this.codigoSTAI = codigoSTAI;
        this.cliente = cliente;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Embarcacao_Loja other = (Embarcacao_Loja) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Embarcacao_Loja() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNomeBarco() {
        return nomeBarco;
    }

    public void setNomeBarco(String nomeBarco) {
        this.nomeBarco = nomeBarco;
    }

    public String getNomeVagaLoja() {
        return nomeVagaLoja;
    }

    public void setNomeVagaLoja(String nomeVagaLoja) {
        this.nomeVagaLoja = nomeVagaLoja;
    }

    public Long getCodigoSTAI() {
        return codigoSTAI;
    }

    public void setCodigoSTAI(Long codigoSTAI) {
        this.codigoSTAI = codigoSTAI;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

}
