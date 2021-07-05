/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ricardo
 */
public class Dashboard implements Serializable{

    private Date dateDe;
    private Date dataATE;  
    private String informacao;
    private String informacao2;
    private int quantidade;
    private int posicao;
    private int codigo;


    public Date getDateDe() {
        return dateDe;
    }

    public Dashboard(Date dateDe, Date dataATE, String informacao, String informacao2, int quantidade, int posicao, int codigo) {
        this.dateDe = dateDe;
        this.dataATE = dataATE;
        this.informacao = informacao;
        this.informacao2 = informacao2;
        this.quantidade = quantidade;
        this.posicao = posicao;
        this.codigo = codigo;
    }

  
    
    public Dashboard() {
    }

    public void setDateDe(Date dateDe) {
        this.dateDe = dateDe;
    }

    public Date getDataATE() {
        return dataATE;
    }

    public void setDataATE(Date dataATE) {
        this.dataATE = dataATE;
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getInformacao2() {
        return informacao2;
    }

    public void setInformacao2(String informacao2) {
        this.informacao2 = informacao2;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    
}
