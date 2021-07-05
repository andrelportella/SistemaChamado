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
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column()
    private String cod_tipo;
    @Column()
    private String cpf_cnpj;
    @Column()
    private String nome;
    @Column()
    private String email;
    @Column()
    private String telefone;
    @Column()
    private String celular;
    @Column()
    private String observacao;
    @Column()
    private String cidade;
    @Column()
    private String uf;
    @Column()
    private String pais;
    @Column()
    private String anotacoes;
    @Column()
    private int codigoSTAI;
    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    @Column
    private Long cod_negocio;

    public Cliente() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Cliente(Long id, String cod_tipo, String cpf_cnpj, String nome, String email, String telefone, String celular, String observacao, String cidade, String uf, String pais, String anotacoes, int codigoSTAI) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.cod_tipo = cod_tipo;
        this.cpf_cnpj = cpf_cnpj;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.celular = celular;
        this.observacao = observacao;
        this.cidade = cidade;
        this.uf = uf;
        this.pais = pais;
        this.anotacoes = anotacoes;
        this.codigoSTAI = codigoSTAI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public void setCpf_cnpj(String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCod_tipo() {
        return cod_tipo;
    }

    public void setCod_tipo(String cod_tipo) {
        this.cod_tipo = cod_tipo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public int getCodigoSTAI() {
        return codigoSTAI;
    }

    public void setCodigoSTAI(int codigoSTAI) {
        this.codigoSTAI = codigoSTAI;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

}
