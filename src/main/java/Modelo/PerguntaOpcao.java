/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author ricardo
 */
@Entity
public class PerguntaOpcao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long id_pergunta;

    private String opcao;

    public PerguntaOpcao() {
    }

    public PerguntaOpcao(Long id, Long id_pergunta, String opcao) {
        this.id = id;
        this.id_pergunta = id_pergunta;
        this.opcao = opcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_pergunta() {
        return id_pergunta;
    }

    public void setId_pergunta(Long id_pergunta) {
        this.id_pergunta = id_pergunta;
    }

    public String getOpcao() {
        return opcao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    @Override
    public String toString() {
        return opcao;
    }

}
