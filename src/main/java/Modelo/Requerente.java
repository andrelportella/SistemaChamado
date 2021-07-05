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
public class Requerente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    //@Column(length = 15, unique = false)
    private String cpf;
    @Column
    //@Column(length = 150, unique = false)
    private String nome;
    @Column
    //@Column(length = 100, unique = false)
    private String email;
    @Column
    //@Column(length = 20, unique = false)
    private String telefone;
    @Column
    //@Column(length = 20, unique = false)
    private String celular;

    @Column()
    private String observacao;

    @Transient()
    private String tecnico;

    @Transient()
    private String cliente;

    @Transient()
    private String embarcacao;

    @Transient()
    private String natureza;

    @Transient()
    private String requerente;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    public Requerente(Long id, String cpf, String nome, String email, String telefone, String celular, String observacao, String tecnico, String cliente, String embarcacao, String natureza, String requerente) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.celular = celular;
        this.observacao = observacao;
        this.tecnico = tecnico;
        this.cliente = cliente;
        this.embarcacao = embarcacao;
        this.natureza = natureza;
        this.requerente = requerente;
    }

    public Requerente() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEmbarcacao() {
        return embarcacao;
    }

    public void setEmbarcacao(String embarcacao) {
        this.embarcacao = embarcacao;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    
    
}
