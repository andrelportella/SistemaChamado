package Modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Software_Equipamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long cod_equipamento;
    private Long cod_software;
    private Long cod_userInseriu;
    private Long cod_userRemoveu;
    private boolean status;
    @Temporal(TemporalType.DATE)
    private Date dataRemocao;
    @Temporal(TemporalType.DATE)
    private Date dataInclusao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Software_Equipamento() {
    }

    public Software_Equipamento(Long id, Long cod_equipamento, Long cod_software, Long cod_userInseriu, Long cod_userRemoveu, boolean status, Date dataRemocao, Date dataInclusao) {
        this.id = id;
        this.cod_equipamento = cod_equipamento;
        this.cod_software = cod_software;
        this.cod_userInseriu = cod_userInseriu;
        this.cod_userRemoveu = cod_userRemoveu;
        this.status = status;
        this.dataRemocao = dataRemocao;
        this.dataInclusao = dataInclusao;
    }

    public Long getCod_equipamento() {
        return cod_equipamento;
    }

    public void setCod_equipamento(Long cod_equipamento) {
        this.cod_equipamento = cod_equipamento;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getCod_software() {
        return cod_software;
    }

    public void setCod_software(Long cod_software) {
        this.cod_software = cod_software;
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
