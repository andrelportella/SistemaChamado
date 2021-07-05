package Modelo;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column( )
    private String nome;

    @Column()
    private String login;

    @Column
    private Long cod_setor;

    @Column()
    private String telefone;

    @Column()
    private String email;

    @Column()
    private String senha;

    @Column()
    private boolean status;

    @Column
    private Long cod_site;

    @Column
    private String celular;

    @Column
    private String descricao;

    @Column(length = 10)
    private String ramal;

    @Column
    private boolean tecnicoRequerimento;

    @Column
    private boolean tecnicoChamado;

    @Column
    private boolean tecnicoAdministrativo;

    @Column
    private int cod_perfil;

    @Transient
    private String setor;
    @Transient
    private String site;
    @Transient
    private String perfil;
    @Transient
    private String negocio;
    @Transient
    FacesContext fc = FacesContext.getCurrentInstance();
    @Transient
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    @Column
    private Long cod_negocio;

    public Usuario(Long id, String nome, String login, String setor, Long cod_setor, String telefone, String email, String senha, boolean status, String site, String perfil, String negocio, Long cod_site, String celular, boolean tecnicoRequerimento, boolean tecnicoChamado, boolean tecnicoAdministrativo, int cod_perfil, Long cod_negocio, String descricao, String ramal) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.setor = setor;
        this.cod_setor = cod_setor;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.status = status;
        this.site = site;
        this.perfil = perfil;
        this.negocio = negocio;
        this.cod_site = cod_site;
        this.celular = celular;
        this.tecnicoRequerimento = tecnicoRequerimento;
        this.tecnicoChamado = tecnicoChamado;
        this.cod_perfil = cod_perfil;
        this.tecnicoAdministrativo = tecnicoAdministrativo;
        this.descricao = descricao;
        this.ramal = ramal;
    }

    public Usuario() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public Long getCod_setor() {
        return cod_setor;
    }

    public void setCod_setor(Long cod_setor) {
        this.cod_setor = cod_setor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNegocio() {
        return negocio;
    }

    public void setNegocio(String negocio) {
        this.negocio = negocio;
    }

    public Long getCod_site() {
        return cod_site;
    }

    public void setCod_site(Long cod_site) {
        this.cod_site = cod_site;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getCod_perfil() {
        return cod_perfil;
    }

    public void setCod_perfil(int cod_perfil) {
        this.cod_perfil = cod_perfil;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public boolean isTecnicoRequerimento() {
        return tecnicoRequerimento;
    }

    public void setTecnicoRequerimento(boolean tecnicoRequerimento) {
        this.tecnicoRequerimento = tecnicoRequerimento;
    }

    public boolean isTecnicoChamado() {
        return tecnicoChamado;
    }

    public void setTecnicoChamado(boolean tecnicoChamado) {
        this.tecnicoChamado = tecnicoChamado;
    }

    public boolean isTecnicoAdministrativo() {
        return tecnicoAdministrativo;
    }

    public void setTecnicoAdministrativo(boolean tecnicoAdministrativo) {
        this.tecnicoAdministrativo = tecnicoAdministrativo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRamal() {
        return ramal;
    }

    public void setRamal(String ramal) {
        this.ramal = ramal;
    }

}
