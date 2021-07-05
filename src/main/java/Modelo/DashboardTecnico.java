/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.Date;

/**
 *
 * @author ricardo
 */
public class DashboardTecnico {

    private Date dateDe;
    private Date dataATE;
    private int chamadosCriados;
    private int emAberto;
    private int finalizado;
    private int zeroHadois;
    private int tresHacinco;
    private int seisHaNove;
    private int dezHaDezenove;
    private int vinteHaVinte;
    private int vinteHaVinteum;
    private int zeroHaZero;
    private String Tecnico;

    public DashboardTecnico() {

    }

    public DashboardTecnico(Date dateDe, Date dataATE, int chamadosCriados, int emAberto, int finalizado, int zeroHadois, int tresHacinco, int seisHaNove, int dezHaDezenove, int vinteHaVinte, int vinteHaVinteum, int zeroHaZero, String Tecnico) {
        this.dateDe = dateDe;
        this.dataATE = dataATE;
        this.chamadosCriados = chamadosCriados;
        this.emAberto = emAberto;
        this.finalizado = finalizado;
        this.zeroHadois = zeroHadois;
        this.tresHacinco = tresHacinco;
        this.seisHaNove = seisHaNove;
        this.dezHaDezenove = dezHaDezenove;
        this.vinteHaVinte = vinteHaVinte;
        this.vinteHaVinteum = vinteHaVinteum;
        this.zeroHaZero = zeroHaZero;
        this.Tecnico = Tecnico;
    }

  

    

    public int getChamadosCriados() {
        return chamadosCriados;
    }

    public void setChamadosCriados(int chamadosCriados) {
        this.chamadosCriados = chamadosCriados;
    }

    public int getEmAberto() {
        return emAberto;
    }

    public void setEmAberto(int emAberto) {
        this.emAberto = emAberto;
    }

    public int getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(int finalizado) {
        this.finalizado = finalizado;
    }

    public int getZeroHadois() {
        return zeroHadois;
    }

    public void setZeroHadois(int zeroHadois) {
        this.zeroHadois = zeroHadois;
    }

    public int getTresHacinco() {
        return tresHacinco;
    }

    public void setTresHacinco(int tresHacinco) {
        this.tresHacinco = tresHacinco;
    }

    public int getSeisHaNove() {
        return seisHaNove;
    }

    public void setSeisHaNove(int seisHaNove) {
        this.seisHaNove = seisHaNove;
    }

    public int getDezHaDezenove() {
        return dezHaDezenove;
    }

    public void setDezHaDezenove(int dezHaDezenove) {
        this.dezHaDezenove = dezHaDezenove;
    }

    public int getVinteHaVinteum() {
        return vinteHaVinteum;
    }

    public void setVinteHaVinteum(int vinteHaVinteum) {
        this.vinteHaVinteum = vinteHaVinteum;
    }

    public int getZeroHaZero() {
        return zeroHaZero;
    }

    public void setZeroHaZero(int zeroHaZero) {
        this.zeroHaZero = zeroHaZero;
    }

    public Date getDateDe() {
        return dateDe;
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

    public int getVinteHaVinte() {
        return vinteHaVinte;
    }

    public void setVinteHaVinte(int vinteHaVinte) {
        this.vinteHaVinte = vinteHaVinte;
    }

    public String getTecnico() {
        return Tecnico;
    }

    public void setTecnico(String Tecnico) {
        this.Tecnico = Tecnico;
    }
}
