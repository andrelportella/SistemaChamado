/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ricardo
 */
@MappedSuperclass
public class AcompanharGeneric implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
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
        final AcompanharGeneric other = (AcompanharGeneric) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long cod_tabela;

    private Long cod_usuario;
    private Long cod_userInseriu;
    private Long cod_userRemoveu;
    private boolean status;
    @Temporal(TemporalType.DATE)
    private Date dataRemocao;
    @Temporal(TemporalType.DATE)
    private Date dataInclusao;

    public AcompanharGeneric(Long cod_usuario, Long cod_userInseriu, Long cod_userRemoveu, boolean status, Date dataRemocao, Date dataInclusao, Long cod_tabela) {

        this.cod_usuario = cod_usuario;
        this.cod_userInseriu = cod_userInseriu;
        this.cod_userRemoveu = cod_userRemoveu;
        this.status = status;
        this.dataRemocao = dataRemocao;
        this.dataInclusao = dataInclusao;
        this.cod_tabela = cod_tabela;
    }

    public AcompanharGeneric() {
    }

    public Long getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(Long cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    @Override
    public String toString() {
        return "Acompanhar{" + "cod_usuario=" + cod_usuario + '}';
    }

    public Long getCod_userInseriu() {
        return cod_userInseriu;
    }

    public void setCod_userInseriu(Long cod_userInseriu) {
        this.cod_userInseriu = cod_userInseriu;
    }

    public Long getCod_userRemoveu() {
        return cod_userRemoveu;
    }

    public void setCod_userRemoveu(Long cod_userRemoveu) {
        this.cod_userRemoveu = cod_userRemoveu;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getDataRemocao() {
        return dataRemocao;
    }

    public void setDataRemocao(Date dataRemocao) {
        this.dataRemocao = dataRemocao;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public Long getCod_tabela() {
        return cod_tabela;
    }

    public void setCod_tabela(Long cod_tabela) {
        this.cod_tabela = cod_tabela;
    }

}
