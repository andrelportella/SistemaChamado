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
public class Software implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 8000)
    private String descricao;
    @Column(length = 100)
    private String chave;
    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataAquisicao;
    private Long cod_empresa;
    @Transient()
    private String empresa;
    @Column()
    private String numNota;
    @Column(length = 100)
    private Long cod_tipoLicenca;
    @Transient()
    private String tipoLicenca;
    @Temporal(TemporalType.DATE)
    private Date validade;
    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    private Long cod_negocio;
    @Column()
    private String nome;
    @Column()
    private boolean status;
    @Column()
    private Long cod_arquivo;
    @Column()
    private boolean anexoMovimento;
    @Transient()
    private List<File> arquivo;
    @Transient
    private String nomeEquipamento;

    public Software() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Software(Long id, String descricao, String chave, Date dataAquisicao, Long cod_empresa, String empresa, String numNota, Long cod_tipoLicenca, String tipoLicenca, Date validade, String nome, boolean status, Long cod_arquivo, boolean anexoMovimento, List<File> arquivo, String nomeEquipamento) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.descricao = descricao;
        this.chave = chave;
        this.dataAquisicao = dataAquisicao;
        this.cod_empresa = cod_empresa;
        this.empresa = empresa;
        this.numNota = numNota;
        this.cod_tipoLicenca = cod_tipoLicenca;
        this.tipoLicenca = tipoLicenca;
        this.validade = validade;
        this.nome = nome;
        this.status = status;
        this.cod_arquivo = cod_arquivo;
        this.anexoMovimento = anexoMovimento;
        this.arquivo = arquivo;
        this.nomeEquipamento = nomeEquipamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public Long getCod_empresa() {
        return cod_empresa;
    }

    public void setCod_empresa(Long cod_empresa) {
        this.cod_empresa = cod_empresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNumNota() {
        return numNota;
    }

    public void setNumNota(String numNota) {
        this.numNota = numNota;
    }

    public Long getCod_tipoLicenca() {
        return cod_tipoLicenca;
    }

    public void setCod_tipoLicenca(Long cod_tipoLicenca) {
        this.cod_tipoLicenca = cod_tipoLicenca;
    }

    public String getTipoLicenca() {
        return tipoLicenca;
    }

    public void setTipoLicenca(String tipoLicenca) {
        this.tipoLicenca = tipoLicenca;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(Long cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    @Override
    public String toString() {
        return getNome(); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getCod_arquivo() {
        return cod_arquivo;
    }

    public void setCod_arquivo(Long cod_arquivo) {
        this.cod_arquivo = cod_arquivo;
    }

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
    }

    public List<File> getArquivo() {
        return arquivo;
    }

    public void setArquivo(List<File> arquivo) {
        this.arquivo = arquivo;
    }

    public String getNomeEquipamento() {
        return nomeEquipamento;
    }

    public void setNomeEquipamento(String nomeEquipamento) {
        this.nomeEquipamento = nomeEquipamento;
    }
}
