package Modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class Negocio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String login;

    private Long cod_negocio;
    private Long cod_userInseriu;
    private Long cod_userRemoveu;
    private boolean status;
    @Temporal(TemporalType.DATE)
    private Date dataRemocao;
    @Temporal(TemporalType.DATE)
    private Date dataInclusao;
    @Transient
    private String negocio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public String getNegocio() {
        return negocio;
    }

    public void setNegocio(String negocio) {
        this.negocio = negocio;
    }

    public Negocio(Long id, String login, Long cod_negocio, Long cod_userInseriu, Long cod_userRemoveu, boolean status, Date dataRemocao, Date dataInclusao, String negocio) {
        this.id = id;
        this.login = login;
        this.cod_negocio = cod_negocio;
        this.cod_userInseriu = cod_userInseriu;
        this.cod_userRemoveu = cod_userRemoveu;
        this.status = status;
        this.dataRemocao = dataRemocao;
        this.dataInclusao = dataInclusao;
        this.negocio = negocio;
    }

    public Negocio() {
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

}
