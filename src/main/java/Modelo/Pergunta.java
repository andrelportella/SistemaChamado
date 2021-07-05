/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author ricardo
 */
@Entity
public class Pergunta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long id_formulario;

    private int ordem;

    private String tipo;

    private String descricao;

    private String pergunta;

    private boolean obrigatorio = true;

    private String respostaCerta;

    private boolean visualizaSlide = true;

    private boolean exibeTexto;

    private String textoExibicao;

    @Transient()
    private List<PerguntaOpcao> opcoes = new ArrayList<>();

    @Transient()
    private int contador;

    public Pergunta(Long id, Long id_formulario, int ordem, String tipo, String descricao, String pergunta, String respostaCerta, boolean exibeTexto, String textoExibicao, int contador) {
        this.id = id;
        this.id_formulario = id_formulario;
        this.ordem = ordem;
        this.tipo = tipo;
        this.descricao = descricao;
        this.pergunta = pergunta;
        this.respostaCerta = respostaCerta;
        this.exibeTexto = exibeTexto;
        this.textoExibicao = textoExibicao;
        this.contador = contador;
    }

    public Pergunta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(Long id_formulario) {
        this.id_formulario = id_formulario;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public boolean isObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public List<PerguntaOpcao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<PerguntaOpcao> opcoes) {
        this.opcoes = opcoes;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public String getRespostaCerta() {
        return respostaCerta;
    }

    public void setRespostaCerta(String respostaCerta) {
        this.respostaCerta = respostaCerta;
    }

    public boolean isVisualizaSlide() {
        return visualizaSlide;
    }

    public void setVisualizaSlide(boolean visualizaSlide) {
        this.visualizaSlide = visualizaSlide;
    }

    public boolean isExibeTexto() {
        return exibeTexto;
    }

    public void setExibeTexto(boolean exibeTexto) {
        this.exibeTexto = exibeTexto;
    }

    public String getTextoExibicao() {
        return textoExibicao;
    }

    public void setTextoExibicao(String textoExibicao) {
        this.textoExibicao = textoExibicao;
    }

}
