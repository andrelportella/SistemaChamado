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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

@Entity
public class Atendimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private String equipamento;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataAbertura;

    @Temporal(TemporalType.DATE)
    private Date dataFechamento;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date dataReAtivacao;

    @Temporal(TemporalType.DATE)
    private Date atualizadoHa;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaAbertura;

    @Temporal(TemporalType.TIME)
    @Column()
    private Date horaFechamento;

    @Column()
    @Temporal(TemporalType.TIME)
    private Date horaReativacao;

    @Transient
    private String Tecnico;

    @Transient
    private String status;

    @Column(length = 8000)
    private String obs;

    @Transient
    private String Fornecedor;

    @Column()
    private long cod_fornecedor;

    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = true)
    private Usuario id_tecnico = new Usuario();

    @Column()
    private long cod_equipamento;

    @Column()
    private long cod_status;

    @Column()
    private long cod_tipoAtendimento;

    @Transient
    private String tipoAtendimento;

    @Column()
    private boolean statusAtendimento;

    @OneToOne
    @JoinColumn(name = "id_solicitante", nullable = true)
    private Usuario id_solicitante = new Usuario();

    @Transient
    private String solicitante;

    @Transient
    private String categoria;

    private Long cod_categoria;

    private Long cod_arquivo;

    private boolean anexoMovimento;

    private boolean passouEmail;

    @Transient
    private String resumo;

    @Transient
    private int dia;

    @Transient
    private int hora;

    @Transient
    private int minuto;

    @Column
    private Long acompanhaSolic;

    @Transient
    private List<Long> listaAcompanhante;

    @Transient
    private String acompanha;

    @Transient
    private int tipoData;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Transient()
    private List<File> arquivo;

    @Transient
    private String statusChamado;

    @Column
    private Long cod_produto;

    @Column
    private double qtdProduto;

    @Column
    private Long cod_bairro;

    @Column
    private Long cod_destino;

    @Transient
    private String bairro;

    @Transient
    private String destino;

    @Transient
    private String produto;

    @Column
    private double custo;

    @Column
    private Long cod_usuarioReativou;

    @Column
    private double valor;

    @Column
    private Long cod_negocio = (Long) session.getAttribute("idNeg");

    @Column
    private Integer rating;

    @Column
    private int qtdDiasAgendamento;

    @Column
    private Long cod_posicaoProcesso;

    @Transient
    private String posicaoProcesso;
    
    @Column
    private Long codChamadoExterno;
    

    public Atendimento() {
    }

    public Atendimento(Long id, String equipamento, Date dataAbertura, Date dataFechamento, Date dataReAtivacao, Date atualizadoHa, Date horaAbertura, Date horaFechamento, Date horaReativacao, String Tecnico, String status, String obs, String Fornecedor, long cod_fornecedor, long cod_equipamento, long cod_status, long cod_tipoAtendimento, String tipoAtendimento, boolean statusAtendimento, String solicitante, String categoria, Long cod_categoria, Long cod_arquivo, boolean anexoMovimento, boolean passouEmail, String resumo, int dia, int hora, int minuto, Long acompanhaSolic, List<Long> listaAcompanhante, String acompanha, int tipoData, List<File> arquivo, String statusChamado, Long cod_produto, double qtdProduto, Long cod_bairro, Long cod_destino, String bairro, String destino, String produto, double custo, Long cod_usuarioReativou, double valor, Integer rating, int qtdDiasAgendamento, Long cod_posicaoProcesso, String posicaoProcesso, Long codChamadoExterno) {
        this.id = id;
        this.equipamento = equipamento;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
        this.dataReAtivacao = dataReAtivacao;
        this.atualizadoHa = atualizadoHa;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.horaReativacao = horaReativacao;
        this.Tecnico = Tecnico;
        this.status = status;
        this.obs = obs;
        this.Fornecedor = Fornecedor;
        this.cod_fornecedor = cod_fornecedor;
        this.cod_equipamento = cod_equipamento;
        this.cod_status = cod_status;
        this.cod_tipoAtendimento = cod_tipoAtendimento;
        this.tipoAtendimento = tipoAtendimento;
        this.statusAtendimento = statusAtendimento;
        this.solicitante = solicitante;
        this.categoria = categoria;
        this.cod_categoria = cod_categoria;
        this.cod_arquivo = cod_arquivo;
        this.anexoMovimento = anexoMovimento;
        this.passouEmail = passouEmail;
        this.resumo = resumo;
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
        this.acompanhaSolic = acompanhaSolic;
        this.listaAcompanhante = listaAcompanhante;
        this.acompanha = acompanha;
        this.tipoData = tipoData;
        this.arquivo = arquivo;
        this.statusChamado = statusChamado;
        this.cod_produto = cod_produto;
        this.qtdProduto = qtdProduto;
        this.cod_bairro = cod_bairro;
        this.cod_destino = cod_destino;
        this.bairro = bairro;
        this.destino = destino;
        this.produto = produto;
        this.custo = custo;
        this.cod_usuarioReativou = cod_usuarioReativou;
        this.valor = valor;
        this.rating = rating;
        this.qtdDiasAgendamento = qtdDiasAgendamento;
        this.cod_posicaoProcesso = cod_posicaoProcesso;
        this.posicaoProcesso = posicaoProcesso;
        this.codChamadoExterno = codChamadoExterno;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final Atendimento other = (Atendimento) obj;
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

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public long getCod_equipamento() {
        return cod_equipamento;
    }

    public void setCod_equipamento(long cod_equipamento) {
        this.cod_equipamento = cod_equipamento;
    }

    public long getCod_status() {
        return cod_status;
    }

    public void setCod_status(long cod_status) {
        this.cod_status = cod_status;
    }

    public String getFornecedor() {
        return Fornecedor;
    }

    public void setFornecedor(String Fornecedor) {
        this.Fornecedor = Fornecedor;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public long getCod_fornecedor() {
        return cod_fornecedor;
    }

    public void setCod_fornecedor(long cod_fornecedor) {
        this.cod_fornecedor = cod_fornecedor;
    }

    public Date getHoraAbertura() {
        return horaAbertura;
    }

    public void setHoraAbertura(Date horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public Date getHoraFechamento() {
        return horaFechamento;
    }

    public void setHoraFechamento(Date horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public String getTecnico() {
        return Tecnico;
    }

    public void setTecnico(String Tecnico) {
        this.Tecnico = Tecnico;
    }

    public long getCod_tipoAtendimento() {
        return cod_tipoAtendimento;
    }

    public void setCod_tipoAtendimento(long cod_tipoAtendimento) {
        this.cod_tipoAtendimento = cod_tipoAtendimento;
    }

    public boolean isStatusAtendimento() {
        return statusAtendimento;
    }

    public void setStatusAtendimento(boolean statusAtendimento) {
        this.statusAtendimento = statusAtendimento;
    }

    public String getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(String tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public Usuario getId_solicitante() {
        return id_solicitante;
    }

    public void setId_solicitante(Usuario id_solicitante) {
        this.id_solicitante = id_solicitante;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(Long cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public Long getAcompanhaSolic() {
        return acompanhaSolic;
    }

    public void setAcompanhaSolic(Long acompanhaSolic) {
        this.acompanhaSolic = acompanhaSolic;
    }

    public String getAcompanha() {
        return acompanha;
    }

    public void setAcompanha(String acompanha) {
        this.acompanha = acompanha;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public Date getAtualizadoHa() {
        return atualizadoHa;
    }

    public void setAtualizadoHa(Date atualizadoHa) {
        this.atualizadoHa = atualizadoHa;
    }

    public int getTipoData() {
        return tipoData;
    }

    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }

    public boolean isPassouEmail() {
        return passouEmail;
    }

    public void setPassouEmail(boolean passouEmail) {
        this.passouEmail = passouEmail;
    }

    public String getStatusChamado() {
        return statusChamado;
    }

    public void setStatusChamado(String statusChamado) {
        this.statusChamado = statusChamado;
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

    public Long getCod_produto() {
        return cod_produto;
    }

    public void setCod_produto(Long cod_produto) {
        this.cod_produto = cod_produto;
    }

    public Long getCod_bairro() {
        return cod_bairro;
    }

    public void setCod_bairro(Long cod_bairro) {
        this.cod_bairro = cod_bairro;
    }

    public Long getCod_destino() {
        return cod_destino;
    }

    public void setCod_destino(Long cod_destino) {
        this.cod_destino = cod_destino;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Long getCod_usuarioReativou() {
        return cod_usuarioReativou;
    }

    public void setCod_usuarioReativou(Long cod_usuarioReativou) {
        this.cod_usuarioReativou = cod_usuarioReativou;
    }

    public Date getDataReAtivacao() {
        return dataReAtivacao;
    }

    public void setDataReAtivacao(Date dataReAtivacao) {
        this.dataReAtivacao = dataReAtivacao;
    }

    public Date getHoraReativacao() {
        return horaReativacao;
    }

    public void setHoraReativacao(Date horaReativacao) {
        this.horaReativacao = horaReativacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<Long> getListaAcompanhante() {
        return listaAcompanhante;
    }

    public void setListaAcompanhante(List<Long> listaAcompanhante) {
        this.listaAcompanhante = listaAcompanhante;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public double getQtdProduto() {
        return qtdProduto;
    }

    public void setQtdProduto(double qtdProduto) {
        this.qtdProduto = qtdProduto;
    }

    public int getQtdDiasAgendamento() {
        return qtdDiasAgendamento;
    }

    public void setQtdDiasAgendamento(int qtdDiasAgendamento) {
        this.qtdDiasAgendamento = qtdDiasAgendamento;
    }

    public Long getCod_posicaoProcesso() {
        return cod_posicaoProcesso;
    }

    public void setCod_posicaoProcesso(Long cod_posicaoProcesso) {
        this.cod_posicaoProcesso = cod_posicaoProcesso;
    }

    public String getPosicaoProcesso() {
        return posicaoProcesso;
    }

    public void setPosicaoProcesso(String posicaoProcesso) {
        this.posicaoProcesso = posicaoProcesso;
    }

    public Long getCodChamadoExterno() {
        return codChamadoExterno;
    }

    public void setCodChamadoExterno(Long codChamadoExterno) {
        this.codChamadoExterno = codChamadoExterno;
    }

}
