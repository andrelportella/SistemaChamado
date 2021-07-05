package Modelo;

import java.io.Serializable;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

@Entity
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String descricao;

    @Column()
    private String obs;

    @Column()
    private String obs2;

    @Column()
    private double valor;

    @Column
    private Long cod_produto;

    @Column
    private double saldo;

    @Transient
    private String produto;

    @Transient
    private Date dataMovimentacao;

    @Transient
    private String solicitante;

    @Transient
    private String tecnico;

    @Transient
    private String tipoMovimentacao;
    
    @Transient
    private String categoria;

    @Transient
    private double qtd;

    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio = (Long) session.getAttribute("idNeg");

    @Column
    private boolean status;

    @Transient
    private Long cod_atendimento;

    public Produto() {
        
    }

    public Produto(Long id, String descricao, String obs, String obs2, double valor, Long cod_produto, double saldo, String produto, Date dataMovimentacao, String solicitante, String tecnico, String tipoMovimentacao, String categoria, double qtd, boolean status, Long cod_atendimento) {
        this.id = id;
        this.descricao = descricao;
        this.obs = obs;
        this.obs2 = obs2;
        this.valor = valor;
        this.cod_produto = cod_produto;
        this.saldo = saldo;
        this.produto = produto;
        this.dataMovimentacao = dataMovimentacao;
        this.solicitante = solicitante;
        this.tecnico = tecnico;
        this.tipoMovimentacao = tipoMovimentacao;
        this.categoria = categoria;
        this.qtd = qtd;
        this.status = status;
        this.cod_atendimento = cod_atendimento;    
    }
      
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }
    
    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }
    
         public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
  
    public Date getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(Date dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getCod_atendimento() {
        return cod_atendimento;
    }

    public void setCod_atendimento(Long cod_atendimento) {
        this.cod_atendimento = cod_atendimento;
    }

    public String getObs2() {
        return obs2;
    }

    public void setObs2(String obs2) {
        this.obs2 = obs2;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getQtd() {
        return qtd;
    }

    public void setQtd(double qtd) {
        this.qtd = qtd;
    }

}
