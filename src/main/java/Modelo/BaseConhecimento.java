package Modelo;

import java.io.Serializable;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@Entity
public class BaseConhecimento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long cod_categoria;

    @Column(length = 50)
    private String resumo;

    @Column(length = 8000)
    private String observacoes;

    @Transient
    private FacesContext fc = FacesContext.getCurrentInstance();

    @Transient
    private HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    @Column
    private Long cod_negocio;

    @Temporal(TemporalType.DATE)
    private Date dataRevisao;

    @Column()
    private Long cod_responsavel;

    @Transient
    private String responsavel;

    @Transient
    private String setor;

    @Transient
    private String categoria;

    @Column()
    private boolean anexoMovimento;

    @Transient()
    private boolean edita;

    @Transient()
    private List<File> arquivo;

    @Column
    private Long cod_arquivo;

    @Column
    private Long tipoBase;

    @Column
    private Long cod_setor;

    public BaseConhecimento() {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCod_categoria() {
        return cod_categoria;
    }

    public BaseConhecimento(Long id, Long cod_categoria, String resumo, String observacoes, Date dataRevisao, Long cod_responsavel, String responsavel, String categoria, boolean anexoMovimento, boolean edita, List<File> arquivo, Long cod_arquivo, Long tipoBase, Long cod_setor, String setor) {
        this.cod_negocio = (Long) session.getAttribute("idNeg");
        this.id = id;
        this.cod_categoria = cod_categoria;
        this.resumo = resumo;
        this.observacoes = observacoes;
        this.dataRevisao = dataRevisao;
        this.cod_responsavel = cod_responsavel;
        this.responsavel = responsavel;
        this.categoria = categoria;
        this.anexoMovimento = anexoMovimento;
        this.edita = edita;
        this.arquivo = arquivo;
        this.cod_arquivo = cod_arquivo;
        this.tipoBase = tipoBase;
        this.cod_setor = cod_setor;
        this.setor = setor;
    }

    public void setCod_categoria(Long cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getCod_negocio() {
        return cod_negocio;
    }

    public Date getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(Date dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public Long getCod_responsavel() {
        return cod_responsavel;
    }

    public void setCod_responsavel(Long cod_responsavel) {
        this.cod_responsavel = cod_responsavel;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isAnexoMovimento() {
        return anexoMovimento;
    }

    public void setAnexoMovimento(boolean anexoMovimento) {
        this.anexoMovimento = anexoMovimento;
    }

    public boolean isEdita() {
        return edita;
    }

    public void setEdita(boolean edita) {
        this.edita = edita;
    }

    public List<File> getArquivo() {
        return arquivo;
    }

    public void setArquivo(List<File> arquivo) {
        this.arquivo = arquivo;
    }

    public FacesContext getFc() {
        return fc;
    }

    public void setFc(FacesContext fc) {
        this.fc = fc;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public Long getCod_arquivo() {
        return cod_arquivo;
    }

    public void setCod_arquivo(Long cod_arquivo) {
        this.cod_arquivo = cod_arquivo;
    }

    public Long getTipoBase() {
        return tipoBase;
    }

    public void setTipoBase(Long tipoBase) {
        this.tipoBase = tipoBase;
    }

    public Long getCod_setor() {
        return cod_setor;
    }

    public void setCod_setor(Long cod_setor) {
        this.cod_setor = cod_setor;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

}
