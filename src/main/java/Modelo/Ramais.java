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
public class Ramais implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private String celular;

    private String telefone;
    @Transient
    private String setor;
    @Transient
    private String site;
    @Transient
    private String empresa;

    private String email;

    private Long cod_site;

    private Long cod_setor;

    @Transient
    private boolean editar;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    public Ramais(Long id, String nome, String telefone, String setor, String site, String empresa, String email, Long cod_site, Long cod_setor, String celular, boolean editar) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.editar = editar;
        this.nome = nome;
        this.telefone = telefone;
        this.setor = setor;
        this.site = site;
        this.empresa = empresa;
        this.email = email;
        this.cod_site = cod_site;
        this.cod_setor = cod_setor;
        this.celular = celular;
    }

    public Ramais() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getCod_site() {
        return cod_site;
    }

    public void setCod_site(Long cod_site) {
        this.cod_site = cod_site;
    }

    public Long getCod_setor() {
        return cod_setor;
    }

    public void setCod_setor(Long cod_setor) {
        this.cod_setor = cod_setor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

}
