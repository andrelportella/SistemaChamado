/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ricardo
 */
@Entity
public class Resposta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dataResposta;// = new Date(System.currentTimeMillis());

    @Column
    @Temporal(TemporalType.TIME)
    private Date horaResposta;// = new Date(System.currentTimeMillis());

    private Long id_pergunta;

    @Column(length = 8000)
    private String resposta;

    @Transient
    private String pergunta;

    private Long id_resposta;

    private String obsResposta;

    public Resposta(Long id, Long id_pergunta, String resposta, String pergunta, Long id_resposta, String obsResposta) {
        this.id = id;
        this.id_pergunta = id_pergunta;
        this.resposta = resposta;
        this.pergunta = pergunta;
        this.id_resposta = id_resposta;
        this.obsResposta = obsResposta;
    }

    public Resposta() {
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

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public Date getHoraResposta() {
        return horaResposta;
    }

    public Long getId_resposta() {
        return id_resposta;
    }

    public void setId_resposta(Long id_resposta) {
        this.id_resposta = id_resposta;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }

    public void setHoraResposta(Date horaResposta) {
        this.horaResposta = horaResposta;
    }

    public String getObsResposta() {
        return obsResposta;
    }

    public void setObsResposta(String obsResposta) {
        this.obsResposta = obsResposta;
    }

}
