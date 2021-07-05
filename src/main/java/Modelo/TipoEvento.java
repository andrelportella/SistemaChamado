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
public enum TipoEvento {
    /* TIPO DE EVENTO - CSS */
    PADRAO("Padrão", "padrao"),
    URGENTE("Urgente", "urgente"),
    CANCELADO("Cancelado", "cancelado");

    private final String descricao;
    private final String css;

    private TipoEvento(String descricao, String css) {
        this.css = css;
        this.descricao = descricao;
    }

    public String getCss() {
        return css;
    }

    public String getDescricao() {
        return descricao;
    }

}
