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
import java.util.Objects;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Requerimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column()
    private Long id;

    @Column()
    private Long id_requerente;

    private boolean anexoMovimento;
    @Transient
    private String requerente;

    private boolean situacao;
    @Column()
    private Long id_cliente;
    private String email;

    @Transient
    private String cliente;

    @Transient
    private String status;

    @Column()
    private Long cod_status;

    @Column()
    private Long id_embarcacao;

    @Transient
    private String embarcacao;

    @Column()
    private Long id_natureza;

    @Transient
    private String natureza;

    @Column()
    private Long id_local;

    @Transient
    private String local;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataAbertura;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaAbertura;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataFechamento;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaFechamento;

    @Temporal(TemporalType.DATE)
    private Date atualizadoHa;

    @Column(length = 8000)
    private String descricao;

    @Transient()
    private List<File> arquivo;

    private String nomeArquivo;

    @OneToOne
    @JoinColumn(name = "id_tecnico")
    private Usuario id_tecnico = new Usuario();

    @Transient
    private String tecnico;

    @Transient
    private int TipoData;

    private double despesa;

    private String anexo;

    @Transient
    private String resumo;

    private boolean enviaEmailReq;
    private boolean enviaEmailCli;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    @Column
    private Long cod_arquivo;

    public Requerimento(Long id, Long id_requerente, boolean anexoMovimento, String requerente, boolean situacao, Long id_cliente, String email, String cliente, String status, Long cod_status, Long id_embarcacao, String embarcacao, Long id_natureza, String natureza, Long id_local, String local, Date dataAbertura, Date horaAbertura, Date dataFechamento, Date horaFechamento, Date atualizadoHa, String descricao, String nomeArquivo, String tecnico, int TipoData, double despesa, String anexo, String resumo, boolean enviaEmailReq, boolean enviaEmailCli, Long cod_arquivo, List<File> arquivo) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.id_requerente = id_requerente;
        this.anexoMovimento = anexoMovimento;
        this.requerente = requerente;
        this.situacao = situacao;
        this.id_cliente = id_cliente;
        this.email = email;
        this.cliente = cliente;
        this.status = status;
        this.cod_status = cod_status;
        this.id_embarcacao = id_embarcacao;
        this.embarcacao = embarcacao;
        this.id_natureza = id_natureza;
        this.natureza = natureza;
        this.id_local = id_local;
        this.local = local;
        this.dataAbertura = dataAbertura;
        this.horaAbertura = horaAbertura;
        this.dataFechamento = dataFechamento;
        this.horaFechamento = horaFechamento;
        this.atualizadoHa = atualizadoHa;
        this.descricao = descricao;
        this.arquivo = arquivo;
        this.nomeArquivo = nomeArquivo;
        this.tecnico = tecnico;
        this.TipoData = TipoData;
        this.despesa = despesa;
        this.anexo = anexo;
        this.resumo = resumo;
        this.enviaEmailReq = enviaEmailReq;
        this.enviaEmailCli = enviaEmailCli;
        this.cod_arquivo = cod_arquivo;
    }

    public Requerimento() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final Requerimento other = (Requerimento) obj;
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

    public Long getId_requerente() {
        return id_requerente;
    }

    public void setId_requerente(Long id_requerente) {
        this.id_requerente = id_requerente;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Long getId_embarcacao() {
        return id_embarcacao;
    }

    public void setId_embarcacao(Long id_embarcacao) {
        this.id_embarcacao = id_embarcacao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public double getDespesa() {
        return despesa;
    }

    public void setDespesa(double despesa) {
        this.despesa = despesa;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getHoraAbertura() {
        return horaAbertura;
    }

    public void setHoraAbertura(Date horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Date getHoraFechamento() {
        return horaFechamento;
    }

    public void setHoraFechamento(Date horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
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

    public boolean isSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public int getTipoData() {
        return TipoData;
    }

    public void setTipoData(int TipoData) {
        this.TipoData = TipoData;
    }

    public Date getAtualizadoHa() {
        return atualizadoHa;
    }

    public void setAtualizadoHa(Date atualizadoHa) {
        this.atualizadoHa = atualizadoHa;
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
