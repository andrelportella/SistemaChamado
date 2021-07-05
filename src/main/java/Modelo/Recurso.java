package Modelo;

import javax.persistence.Entity;

@Entity
public class Recurso extends AbstractEntity {

    private Long cod_usuario;
    private boolean usaRecurso;
    private Long cod_recurso;
    private String obs;

    public Recurso(Long cod_usuario, boolean usaRecurso, Long cod_recurso, String obs) {
        this.cod_usuario = cod_usuario;
        this.usaRecurso = usaRecurso;
        this.cod_recurso = cod_recurso;
        this.obs = obs;
    }

    public Recurso() {
    }

    public Long getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(Long cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public boolean isUsaRecurso() {
        return usaRecurso;
    }

    public void setUsaRecurso(boolean usaRecurso) {
        this.usaRecurso = usaRecurso;
    }

    public Long getCod_recurso() {
        return cod_recurso;
    }

    public void setCod_recurso(Long cod_recurso) {
        this.cod_recurso = cod_recurso;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

}
