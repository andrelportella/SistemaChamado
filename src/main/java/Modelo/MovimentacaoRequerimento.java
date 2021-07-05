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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ricardo
 */
@Entity
public class MovimentacaoRequerimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean anexoMovimento;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaMovimento;
    private Long id_cliente;

    @ManyToOne
    @JoinColumn(name = "id_requerimento")
    private Requerimento id_requerimento = new Requerimento();

    @ManyToOne
    @JoinColumn(name = "id_tecnico")
    private Usuario id_tecnico = new Usuario();

    private String anexo;

    private String nomeArquivo;

    private Long cod_status;

    private Long cod_arquivo;

    @Transient
    private String obsRequerimento;

    @Transient
    private String status;

    private Long id_requerente;

    private Long id_natureza;

    private Long id_local;

    private Long id_embarcacao;

    @Transient
    private List<File> arquivo;

    @Transient
    private String tecnico;

    @Column(length = 8000)
    private String observacao;

    private double despesa;

    private boolean enviaEmailReq;

    private boolean enviaEmailCli;

    public MovimentacaoRequerimento() {
    }

    public MovimentacaoRequerimento(Long id, boolean anexoMovimento, Date dataMovimento, Date horaMovimento, Long id_cliente, String anexo, String nomeArquivo, Long cod_status, Long cod_arquivo, String obsRequerimento, String status, Long id_requerente, Long id_natureza, Long id_local, Long id_embarcacao, List<File> arquivo, String tecnico, String observacao, double despesa, boolean enviaEmailReq, boolean enviaEmailCli) {
        this.id = id;
        this.anexoMovimento = anexoMovimento;
        this.dataMovimento = dataMovimento;
        this.horaMovimento = horaMovimento;
        this.id_cliente = id_cliente;
        this.anexo = anexo;
        this.nomeArquivo = nomeArquivo;
        this.cod_status = cod_status;
        this.cod_arquivo = cod_arquivo;
        this.obsRequerimento = obsRequerimento;
        this.status = status;
        this.id_requerente = id_requerente;
        this.id_natureza = id_natureza;
        this.id_local = id_local;
        this.id_embarcacao = id_embarcacao;
        this.arquivo = arquivo;
        this.tecnico = tecnico;
        this.observacao = observacao;
        this.despesa = despesa;
        this.enviaEmailReq = enviaEmailReq;
        this.enviaEmailCli = enviaEmailCli;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Requerimento getId_requerimento() {
        return id_requerimento;
    }

    public void setId_requerimento(Requerimento id_requerimento) {
        this.id_requerimento = id_requerimento;
    }

    public double getDespesa() {
        return despesa;
    }

    public void setDespesa(double despesa) {
        this.despesa = despesa;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Date getHoraMovimento() {
        return horaMovimento;
    }

    public void setHoraMovimento(Date horaMovimento) {
        this.horaMovimento = horaMovimento;
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

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
    }

    public Long getCod_status() {
        return cod_status;
    }

    public void setCod_status(Long cod_status) {
        this.cod_status = cod_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Long getId_requerente() {
        return id_requerente;
    }

    public void setId_requerente(Long id_requerente) {
        this.id_requerente = id_requerente;
    }

    public String getObsRequerimento() {
        return obsRequerimento;
    }

    public void setObsRequerimento(String obsRequerimento) {
        this.obsRequerimento = obsRequerimento;
    }

    public Long getId_natureza() {
        return id_natureza;
    }

    public void setId_natureza(Long id_natureza) {
        this.id_natureza = id_natureza;
    }

    public Long getId_local() {
        return id_local;
    }

    public void setId_local(Long id_local) {
        this.id_local = id_local;
    }

    public Long getId_embarcacao() {
        return id_embarcacao;
    }

    public void setId_embarcacao(Long id_embarcacao) {
        this.id_embarcacao = id_embarcacao;
    }

    public boolean isEnviaEmailReq() {
        return enviaEmailReq;
    }

    public void setEnviaEmailReq(boolean enviaEmailReq) {
        this.enviaEmailReq = enviaEmailReq;
    }

    public boolean isEnviaEmailCli() {
        return enviaEmailCli;
    }

    public void setEnviaEmailCli(boolean enviaEmailCli) {
        this.enviaEmailCli = enviaEmailCli;
    }

    public Usuario getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(Usuario id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public Long getCod_arquivo() {
        return cod_arquivo;
    }

    public void setCod_arquivo(Long cod_arquivo) {
        this.cod_arquivo = cod_arquivo;
    }

    public List<File> getArquivo() {
        return arquivo;
    }

    public void setArquivo(List<File> arquivo) {
        this.arquivo = arquivo;
    }

}
