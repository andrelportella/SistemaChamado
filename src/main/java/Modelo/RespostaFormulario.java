/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author ricardo
 */
public class RespostaFormulario {

    private String nome;
    private int qtdAvaliacoes;
    private double media;
    private Long id_resposta;
    private double nota;

    public RespostaFormulario(String nome, int qtdAvaliacoes, double media, Long id_resposta, double nota) {
        this.nome = nome;
        this.qtdAvaliacoes = qtdAvaliacoes;
        this.media = media;
        this.id_resposta = id_resposta;
        this.nota = nota;
    }

    public String getNome() {
        return nome;
    }

    public RespostaFormulario() {
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getQtdAvaliacoes() {
        return qtdAvaliacoes;
    }

    public void setQtdAvaliacoes(int qtdAvaliacoes) {
        this.qtdAvaliacoes = qtdAvaliacoes;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public Long getId_resposta() {
        return id_resposta;
    }

    public void setId_resposta(Long id_resposta) {
        this.id_resposta = id_resposta;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }
}
