/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author ricardo
 */
@Entity
public class MovimentacaoProduto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long cod_atendimento;

    @Column
    private Long cod_produto;

    @Column(length = 1)
    private String tipoBaixa;

    @Column
    private double qtdInserida;

    @Column
    private Long id_movimento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovimentacaoProduto() {
    }

    public MovimentacaoProduto(Long id, Long cod_atendimento, Long cod_produto, String tipoBaixa, double qtdInserida, Long id_movimento) {
        this.id = id;
        this.cod_atendimento = cod_atendimento;
        this.cod_produto = cod_produto;
        this.tipoBaixa = tipoBaixa;
        this.qtdInserida = qtdInserida;
        this.id_movimento = id_movimento;
    }



    public Long getCod_atendimento() {
        return cod_atendimento;
    }

    public void setCod_atendimento(Long cod_atendimento) {
        this.cod_atendimento = cod_atendimento;
    }

    public Long getCod_produto() {
        return cod_produto;
    }

    public void setCod_produto(Long cod_produto) {
        this.cod_produto = cod_produto;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public Long getId_movimento() {
        return id_movimento;
    }

    public void setId_movimento(Long id_movimento) {
        this.id_movimento = id_movimento;
    }

    public double getQtdInserida() {
        return qtdInserida;
    }

    public void setQtdInserida(double qtdInserida) {
        this.qtdInserida = qtdInserida;
    }

}
