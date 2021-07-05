/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Noticia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String titulo;

    @Transient
    private String autor;

    @Column
    private Long cod_autor;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;

    @Column
    private Long cod_arquivo;

    @Column(length = 8000)
    private String descricao;

    @Column
    private boolean anexoMovimento;

    @Column
    private String anexo;

    @Column
    private String nomeArquivo;

    @Transient
    private File arquivo;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    Long cod_negocio;

    public Noticia() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Long getId() {
        return id;
    }

    public Noticia(Long id, String titulo, String autor, Long cod_autor, Date dataPublicacao, Long cod_arquivo, String descricao, boolean anexoMovimento, String anexo, String nomeArquivo, File arquivo) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.cod_autor = cod_autor;
        this.dataPublicacao = dataPublicacao;
        this.cod_arquivo = cod_arquivo;
        this.descricao = descricao;
        this.anexoMovimento = anexoMovimento;
        this.anexo = anexo;
        this.nomeArquivo = nomeArquivo;
        this.arquivo = arquivo;
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Long getCod_autor() {
        return cod_autor;
    }

    public void setCod_autor(Long cod_autor) {
        this.cod_autor = cod_autor;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Long getCod_arquivo() {
        return cod_arquivo;
    }

    public void setCod_arquivo(Long cod_arquivo) {
        this.cod_arquivo = cod_arquivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

   

}
