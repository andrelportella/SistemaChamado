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

@Entity
public class Aniversariantes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private static final long serialVersionUID = 1L;
    private String nome;

    private String empresa;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    private String dataAniversario;

    public Aniversariantes() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Aniversariantes(Long id, String nome, String empresa, String dataAniversario) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.nome = nome;
        this.empresa = empresa;
        this.dataAniversario = dataAniversario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDataAniversario() {
        return dataAniversario;
    }

    public void setDataAniversario(String dataAniversario) {
        this.dataAniversario = dataAniversario;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
