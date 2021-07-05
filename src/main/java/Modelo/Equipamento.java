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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@Entity
public class Equipamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Transient
    private String Site;

    @Transient
    private String tipoMaq;

    @Transient
    private String status;

    @Column()
    private String nome;

    @Column()
    private String ip;

    @Transient
    private String setor;

    @Transient
    private String usuario;

    @Transient
    private String placaMae;

    @Transient
    private String processador;

    @Transient
    private String empresa;

    @Transient
    private String marca;

    @Transient
    private String HD;

    @Transient
    private String memoria;

    @Transient
    private String SO;

    @Column()
    private String tagLicenca;

    @Column()
    private String nfSoftware;

    @Column()
    private String tamanho;

    @Column()
    private String peso;

    @Column()
    private String capacidade;

    @Column()
    private String numSerie;

    @Column()
    private String modelo;

    @Transient
    private String fornecedor;

    @Column()
    private boolean revisadoRenato;

    @Column()
    private Long cod_site;

    @Column()
    private Long cod_tipoMaq;

    @Column()
    private Long cod_Status;

    @Column()
    private Long cod_setor;

    @Column()
    private Long cod_usrResp;

    @Column()
    private Long cod_placaMae;

    @Column()
    private Long cod_marca;

    @Column()
    private Long cod_tamHD;

    @Column()
    private Long cod_mem;

    @Column()
    private Long cod_sOp;

    @Column()
    private Long cod_Processador;

    @Column()
    private Date dtCompra;

    @Column()
    private Long cod_tipoEquipamento;

    @Column()
    private String descricao;

    @Column(length = 8000)
    private String observacao;

    @Column()
    private String matriculaCartorio;

    @Column()
    private String incra;

    @Column()
    private String iptu;

    @Column()
    private String renavam;

    @Column()
    private String itr;

    @Column()
    private double valorItr;
    @Column()
    private double valorIptu;
    @Column()
    private double valorForo;
    @Column()
    private double valorIncra;
    @Column()
    private String seguro;

    @Column()
    private double valorSeguro;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio = (Long) session.getAttribute("idNeg");

    // @OneToOne(optional = false, fetch = FetchType.LAZY)
    @OneToOne
    @JoinColumn(name = "id_fornecedor")
    private Fornecedor id_fornecedor = new Fornecedor();

    @Column
    private double valor;

    @Column
    private boolean anexoMovimento;

    @Transient
    private List<File> arquivo;

    private Long cod_arquivo;

    private Long cod_empresa;

    @Column
    private boolean restricao;

    @Column
    private String resctricaoObs;

    public Equipamento(Long id, String Site, String tipoMaq, String status, String nome, String ip, String setor, String usuario, String placaMae, String processador, String empresa, String marca, String HD, String memoria, String SO, String tagLicenca, String nfSoftware, String tamanho, String peso, String capacidade, String numSerie, String modelo, String fornecedor, boolean revisadoRenato, Long cod_site, Long cod_tipoMaq, Long cod_Status, Long cod_setor, Long cod_usrResp, Long cod_placaMae, Long cod_marca, Long cod_tamHD, Long cod_mem, Long cod_sOp, Long cod_Processador, Date dtCompra, Long cod_tipoEquipamento, String descricao, String observacao, String matriculaCartorio, String incra, String iptu, String renavam, String itr, double valorItr, double valorIptu, double valorForo, double valorIncra, String seguro, double valorSeguro, double valor, boolean anexoMovimento, List<File> arquivo, Long cod_arquivo, Long cod_empresa, boolean restricao, String resctricaoObs) {
        this.id = id;
        this.Site = Site;
        this.tipoMaq = tipoMaq;
        this.status = status;
        this.nome = nome;
        this.ip = ip;
        this.setor = setor;
        this.usuario = usuario;
        this.placaMae = placaMae;
        this.processador = processador;
        this.empresa = empresa;
        this.marca = marca;
        this.HD = HD;
        this.memoria = memoria;
        this.SO = SO;
        this.tagLicenca = tagLicenca;
        this.nfSoftware = nfSoftware;
        this.tamanho = tamanho;
        this.peso = peso;
        this.capacidade = capacidade;
        this.numSerie = numSerie;
        this.modelo = modelo;
        this.fornecedor = fornecedor;
        this.revisadoRenato = revisadoRenato;
        this.cod_site = cod_site;
        this.cod_tipoMaq = cod_tipoMaq;
        this.cod_Status = cod_Status;
        this.cod_setor = cod_setor;
        this.cod_usrResp = cod_usrResp;
        this.cod_placaMae = cod_placaMae;
        this.cod_marca = cod_marca;
        this.cod_tamHD = cod_tamHD;
        this.cod_mem = cod_mem;
        this.cod_sOp = cod_sOp;
        this.cod_Processador = cod_Processador;
        this.dtCompra = dtCompra;
        this.cod_tipoEquipamento = cod_tipoEquipamento;
        this.descricao = descricao;
        this.observacao = observacao;
        this.matriculaCartorio = matriculaCartorio;
        this.incra = incra;
        this.iptu = iptu;
        this.renavam = renavam;
        this.itr = itr;
        this.valorItr = valorItr;
        this.valorIptu = valorIptu;
        this.valorForo = valorForo;
        this.valorIncra = valorIncra;
        this.seguro = seguro;
        this.valorSeguro = valorSeguro;
        this.valor = valor;
        this.anexoMovimento = anexoMovimento;
        this.arquivo = arquivo;
        this.cod_arquivo = cod_arquivo;
        this.cod_empresa = cod_empresa;
        this.restricao = restricao;
        this.resctricaoObs = resctricaoObs;
    }

    public Equipamento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String Site) {
        this.Site = Site;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPlacaMae() {
        return placaMae;
    }

    public void setPlacaMae(String placaMae) {
        this.placaMae = placaMae;
    }

    public String getProcessador() {
        return processador;
    }

    public void setProcessador(String processador) {
        this.processador = processador;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getHD() {
        return HD;
    }

    public void setHD(String HD) {
        this.HD = HD;
    }

    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }

    public String getSistemaOperacional() {
        return SO;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.SO = sistemaOperacional;
    }

    public String getTagLicenca() {
        return tagLicenca;
    }

    public void setTagLicenca(String tagLicenca) {
        this.tagLicenca = tagLicenca;
    }

    public String getNfSoftware() {
        return nfSoftware;
    }

    public void setNfSoftware(String nfSoftware) {
        this.nfSoftware = nfSoftware;
    }

    public boolean isRevisadoRenato() {
        return revisadoRenato;
    }

    public void setRevisadoRenato(boolean revisadoRenato) {
        this.revisadoRenato = revisadoRenato;
    }

    public String getTipoMaq() {
        return tipoMaq;
    }

    public void setTipoMaq(String tipoMaq) {
        this.tipoMaq = tipoMaq;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCod() {
        return nome;
    }

    public void setCod(String Cod) {
        this.nome = Cod;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(String capacidade) {
        this.capacidade = capacidade;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Long getCod_site() {
        return cod_site;
    }

    public void setCod_site(Long cod_site) {
        this.cod_site = cod_site;
    }

    public Long getCod_tipoMaq() {
        return cod_tipoMaq;
    }

    public void setCod_tipoMaq(Long cod_tipoMaq) {
        this.cod_tipoMaq = cod_tipoMaq;
    }

    public Long getCod_Status() {
        return cod_Status;
    }

    public void setCod_Status(Long cod_Status) {
        this.cod_Status = cod_Status;
    }

    public Long getCod_setor() {
        return cod_setor;
    }

    public void setCod_setor(Long cod_setor) {
        this.cod_setor = cod_setor;
    }

    public Long getCod_usrResp() {
        return cod_usrResp;
    }

    public void setCod_usrResp(Long cod_usrResp) {
        this.cod_usrResp = cod_usrResp;
    }

    public Long getCod_placaMae() {
        return cod_placaMae;
    }

    public void setCod_placaMae(Long cod_placaMae) {
        this.cod_placaMae = cod_placaMae;
    }

    public Long getCod_marca() {
        return cod_marca;
    }

    public void setCod_marca(Long cod_marca) {
        this.cod_marca = cod_marca;
    }

    public Long getCod_tamHD() {
        return cod_tamHD;
    }

    public void setCod_tamHD(Long cod_tamHD) {
        this.cod_tamHD = cod_tamHD;
    }

    public Long getCod_mem() {
        return cod_mem;
    }

    public void setCod_mem(Long cod_mem) {
        this.cod_mem = cod_mem;
    }

    public Long getCod_sOp() {
        return cod_sOp;
    }

    public void setCod_sOp(Long cod_sOp) {
        this.cod_sOp = cod_sOp;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSO() {
        return SO;
    }

    public void setSO(String SO) {
        this.SO = SO;
    }

    public Long getCod_Processador() {
        return cod_Processador;
    }

    public void setCod_Processador(Long cod_Processador) {
        this.cod_Processador = cod_Processador;
    }

    public Date getDtCompra() {
        return dtCompra;
    }

    public void setDtCompra(Date dtCompra) {
        this.dtCompra = dtCompra;
    }

    public Long getCod_tipoEquipamento() {
        return cod_tipoEquipamento;
    }

    public void setCod_tipoEquipamento(Long cod_tipoEquipamento) {
        this.cod_tipoEquipamento = cod_tipoEquipamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Fornecedor getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(Fornecedor id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    public Long getCod_arquivo() {
        return cod_arquivo;
    }

    public void setCod_arquivo(Long cod_arquivo) {
        this.cod_arquivo = cod_arquivo;
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

    public String getMatriculaCartorio() {
        return matriculaCartorio;
    }

    public void setMatriculaCartorio(String matriculaCartorio) {
        this.matriculaCartorio = matriculaCartorio;
    }

    public String getIncra() {
        return incra;
    }

    public void setIncra(String incra) {
        this.incra = incra;
    }

    public String getIptu() {
        return iptu;
    }

    public void setIptu(String iptu) {
        this.iptu = iptu;
    }

    public String getRenavam() {
        return renavam;
    }

    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }

    public String getItr() {
        return itr;
    }

    public void setItr(String itr) {
        this.itr = itr;
    }

    public String getSeguro() {
        return seguro;
    }

    public void setSeguro(String seguro) {
        this.seguro = seguro;
    }

    public double getValorItr() {
        return valorItr;
    }

    public void setValorItr(double valorItr) {
        this.valorItr = valorItr;
    }

    public double getValorIptu() {
        return valorIptu;
    }

    public void setValorIptu(double valorIptu) {
        this.valorIptu = valorIptu;
    }

    public double getValorForo() {
        return valorForo;
    }

    public void setValorForo(double valorForo) {
        this.valorForo = valorForo;
    }

    public double getValorIncra() {
        return valorIncra;
    }

    public void setValorIncra(double valorIncra) {
        this.valorIncra = valorIncra;
    }

    public double getValorSeguro() {
        return valorSeguro;
    }

    public void setValorSeguro(double valorSeguro) {
        this.valorSeguro = valorSeguro;
    }

    public boolean isRestricao() {
        return restricao;
    }

    public void setRestricao(boolean restricao) {
        this.restricao = restricao;
    }

    public String getResctricaoObs() {
        return resctricaoObs;
    }

    public void setResctricaoObs(String resctricaoObs) {
        this.resctricaoObs = resctricaoObs;
    }

}
