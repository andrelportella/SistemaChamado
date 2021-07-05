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
public class MovimentacaoAtendimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 8000)
    private String observacao;

    private boolean anexoMovimento;
    @Column
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;

    @Column
    private boolean statusMovimento;

    @Transient
    private List<Long> listaAcompanhante;

    @ManyToOne
    @JoinColumn(nullable = true, name = "id_atendimento")
    private Atendimento id_atendimento = new Atendimento();

    @Column
    @Temporal(TemporalType.TIME)
    private Date horaMovimento;

    @ManyToOne
    @JoinColumn(nullable = true, name = "id_tecnico")
    private Usuario id_tecnico = new Usuario();

    @Transient
    private String tecnico;

    @Column
    private String anexo;
    @Column
    private String nomeArquivo;

    @Column
    private boolean enviaSolic;

    @Transient
    private String obsAtendimento;

    private Long cod_equipamento;

    @Transient
    private Long cod_categoria;

    private Long cod_arquivo;

    private Long cod_produto;

    private double qtdProduto;

    @Transient
    private String produto;

    @Transient
    private List<File> arquivo;

    private boolean passouEmail;

    @Transient
    private String codB2c;

    @Transient
    private double valorProduto;

    @Transient
    private Integer rating;

    @Transient
    private String tipoAtendimento;

    @Transient
    private Long cod_tipoAtendimento;

    public MovimentacaoAtendimento(Long id, String observacao, boolean anexoMovimento, Date dataMovimento, boolean statusMovimento, List<Long> listaAcompanhante, Date horaMovimento, String tecnico, String anexo, String nomeArquivo, boolean enviaSolic, String obsAtendimento, Long cod_equipamento, Long cod_categoria, Long cod_arquivo, Long cod_produto, double qtdProduto, String produto, List<File> arquivo, boolean passouEmail, String codB2c, double valorProduto, Integer rating, String tipoAtendimento, Long cod_tipoAtendimento) {
        this.id = id;
        this.observacao = observacao;
        this.anexoMovimento = anexoMovimento;
        this.dataMovimento = dataMovimento;
        this.statusMovimento = statusMovimento;
        this.listaAcompanhante = listaAcompanhante;
        this.horaMovimento = horaMovimento;
        this.tecnico = tecnico;
        this.anexo = anexo;
        this.nomeArquivo = nomeArquivo;
        this.enviaSolic = enviaSolic;
        this.obsAtendimento = obsAtendimento;
        this.cod_equipamento = cod_equipamento;
        this.cod_categoria = cod_categoria;
        this.cod_arquivo = cod_arquivo;
        this.cod_produto = cod_produto;
        this.qtdProduto = qtdProduto;
        this.produto = produto;
        this.arquivo = arquivo;
        this.passouEmail = passouEmail;
        this.codB2c = codB2c;
        this.valorProduto = valorProduto;
        this.rating = rating;
        this.tipoAtendimento = tipoAtendimento;
        this.cod_tipoAtendimento = cod_tipoAtendimento;
    }

    public Long getCod_tipoAtendimento() {
        return cod_tipoAtendimento;
    }

    public void setCod_tipoAtendimento(Long cod_tipoAtendimento) {
        this.cod_tipoAtendimento = cod_tipoAtendimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public boolean isStatusMovimento() {
        return statusMovimento;
    }

    public void setStatusMovimento(boolean statusMovimento) {
        this.statusMovimento = statusMovimento;
    }

    public MovimentacaoAtendimento() {
    }

    public Atendimento getId_atendimento() {
        return id_atendimento;
    }

    public void setId_atendimento(Atendimento id_atendimento) {
        this.id_atendimento = id_atendimento;
    }

    public Date getHoraMovimento() {
        return horaMovimento;
    }

    public void setHoraMovimento(Date horaMovimento) {
        this.horaMovimento = horaMovimento;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
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

    public boolean isEnviaSolic() {
        return enviaSolic;
    }

    public void setEnviaSolic(boolean enviaSolic) {
        this.enviaSolic = enviaSolic;
    }

    public String getObsAtendimento() {
        return obsAtendimento;
    }

    public void setObsAtendimento(String obsAtendimento) {
        this.obsAtendimento = obsAtendimento;
    }

    public Long getCod_equipamento() {
        return cod_equipamento;
    }

    public void setCod_equipamento(Long cod_equipamento) {
        this.cod_equipamento = cod_equipamento;
    }

    public boolean isPassouEmail() {
        return passouEmail;
    }

    public void setPassouEmail(boolean passouEmail) {
        this.passouEmail = passouEmail;
    }

    public Long getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(Long cod_categoria) {
        this.cod_categoria = cod_categoria;
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

    public Long getCod_produto() {
        return cod_produto;
    }

    public void setCod_produto(Long cod_produto) {
        this.cod_produto = cod_produto;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getCodB2c() {
        return codB2c;
    }

    public void setCodB2c(String codB2c) {
        this.codB2c = codB2c;
    }

    public double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public double getQtdProduto() {
        return qtdProduto;
    }

    public void setQtdProduto(double qtdProduto) {
        this.qtdProduto = qtdProduto;
    }

    public String getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(String tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

}
