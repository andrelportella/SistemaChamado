package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.ArquivoUpload;
import Modelo.BaseConhecimento;
import Modelo.Campos;
import Modelo.Parametros;
import Modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;
import util.Util;
import util.ValidacoesBanco;

/**
 * @author ricardo
 *
 */
@ManagedBean
@ViewScoped
public class BaseConhecimentoBean implements Serializable {

    private BaseConhecimento base = new BaseConhecimento();
    private BaseConhecimento editaBase = new BaseConhecimento();
    private BaseConhecimento baseSelecionado = new BaseConhecimento();
    private Part file;
    Util util = new Util();
    PreparedStatement stmt;
    ResultSet rs;
    private Connection ConexaoSQL;
    private List<BaseConhecimento> bases = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO", sql;
    private boolean statusBotao;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    Long cod_responsavel = (Long) session.getAttribute("idUser");
    boolean admin = verificaAdmin(cod_responsavel);
    private List<Campos> categorias = new ArrayList<>();
    private List<Usuario> tecnicos = new ArrayList<>();
    Date data = new Date(System.currentTimeMillis());
    private Campos cadCategoria = new Campos();
    private UploadedFile uploadedFile;
    private StreamedContent downloadFile;
    ArquivoUpload a = new ArquivoUpload();
    private boolean edita = false;
    private int qtdRegistro = totalAtendimento();
    private String textoArquivo = "";
    private List<Boolean> list;
    Parametros parametros = new Parametros();
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();
    Long tipoBase;
    Usuario u;

    public BaseConhecimentoBean() throws ClassNotFoundException, SQLException {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        tipoBase = 1L;
        u = new GenericDAO<>(Usuario.class).tecnico(cod_responsavel);
        listaBases();
        categorias = new GenericDAO<>(BaseConhecimento.class).listarCamposJDBC(22L);
        tecnicos = new GenericDAO<>(Usuario.class).listarTecnicos();
        list = Arrays.asList(true, true, true, true, true, true);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public int totalAtendimento() {
        sql = "SELECT count(id) FROM BaseConhecimento where cod_negocio = " + cod_negocio + "  and tipoBase = 1";
        return ValidacoesBanco.retornaInt(sql, "BaseConhecimentoBean", "totalAtendimento");
    }

    public void listaBases() {
        List<BaseConhecimento> campos = new ArrayList<>();
        sql = " SELECT t.nome, categoria.descricao as categoria, setor.descricao as setor, b.*  "
                + " FROM BaseConhecimento b "
                + " LEFT JOIN Usuario t on t.id = b.cod_responsavel "
                + " LEFT JOIN Campos categoria on categoria.cod = b.cod_categoria and categoria.tabela = 22 and categoria.cod_negocio = " + cod_negocio + ""
                + " LEFT JOIN Campos setor on setor.cod = b.cod_setor and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " ";
        if (admin) {
            sql = sql + " where b.cod_negocio =" + cod_negocio + " ";
        } else {
            sql = sql + " where b.cod_negocio =" + cod_negocio + "  and tipoBase = " + tipoBase + " and b.cod_setor =" + u.getCod_setor() + "";

        }
        sql = sql + " order by b.dataRevisao ";

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            BaseConhecimento b = new BaseConhecimento();
            while (rs.next()) {
                b.setId(rs.getLong("id"));
                b.setCod_categoria(rs.getLong("cod_categoria"));
                b.setCod_responsavel(rs.getLong("cod_responsavel"));
                b.setCod_arquivo(rs.getLong("Cod_arquivo"));
                b.setDataRevisao(rs.getDate("dataRevisao"));
                b.setObservacoes(rs.getString("observacoes"));
                b.setResumo(rs.getString("resumo"));
                b.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                b.setCod_setor(rs.getLong("Cod_setor"));
                b.setTipoBase(rs.getLong("tipoBase"));
                b.setResponsavel(rs.getString(1));
                b.setCategoria(rs.getString(2));
                b.setSetor(rs.getString(3));
                if (admin) {
                    b.setEdita(false);
                } else {
                    b.setEdita(!Objects.equals(b.getCod_responsavel(), cod_responsavel));
                }
                if (b.isAnexoMovimento()) {
                    b.setArquivo(aU.listar(b.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaBC()));
                }

                campos.add(b);
                b = new BaseConhecimento();
            }
            bases = campos;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            campos = null;
            b = null;
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "BaseConhecimentoBean", "listaBases");
            System.out.println("public void listaBases() Erro:" + e);
        }
    }

    public void salvar() throws IOException {
        base.setCod_arquivo(a.getId());
        base.setCod_setor(u.getCod_setor());
        if (a.isEnviado()) {
            base.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
            a = new ArquivoUpload();
        }
        base.setTipoBase(1L);
        base.setDataRevisao(data);
        base.setCod_responsavel((Long) session.getAttribute("idUser"));
        new GenericDAO<>(BaseConhecimento.class).salvar(base);
        listaBases();
        totalAtendimento();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        FacesUtil.addMsgInfo(mSucesso, "Cadastrado com sucesso!");
        base = new BaseConhecimento();

    }

    public void anexaArquivo() throws IOException {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
    }

    public void upload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaBC());
    }

    public void editUpload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, editaBase.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaBC());
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void deletarArquivo(File file) throws IOException, ParseException {
        arq.delete(file, editaBase.getId(), "baseconhecimento");
        listaBases();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEdit').hide();");

    }

    public void atualizaCampos() {
        if (cadCategoria.getDescricao() != null) {
            if (!verificaAcesso(cadCategoria.getDescricao())) {
                CamposBean camposBean = new CamposBean();
                cadCategoria.setCod(camposBean.verificaCodigo(cadCategoria) + 1L);
                new GenericDAO<>(Campos.class).salvar(cadCategoria);
                cadCategoria = new Campos();
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarCatDialog').hide();");
                categorias = new GenericDAO<>(BaseConhecimento.class).listarCamposJDBC(22L);
            }
        }
    }

    public boolean verificaAdmin(Long id) {
        sql = "select * from Usuario where id = " + id + " and cod_perfil = 3 and cod_negocio =" + cod_negocio + " ";
        return ValidacoesBanco.retornaBoolean(sql, "BaseConhecimentoBean", "verificaAdmin");
    }

    public boolean verificaAcesso(String nome) {
        sql = "SELECT tabela FROM campos WHERE Descricao='" + nome + "' and cod_negocio =" + cod_negocio + " ";
        return ValidacoesBanco.retornaBoolean(sql, "BaseConhecimentoBean", "verificaAcesso");
    }

    public void editar(BaseConhecimento b) {
        editaBase = b;
        if (editaBase.getCod_arquivo() == null || editaBase.getCod_arquivo() == 0) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
            editaBase.setCod_arquivo(a.getId());
            new GenericDAO<>(BaseConhecimento.class).update(editaBase);
        }
    }

    public void deletar(BaseConhecimento b) throws ClassNotFoundException, SQLException {
        new GenericDAO<>(BaseConhecimento.class).excluir(b);
        listaBases();
        totalAtendimento();
    }

    public void editarBase() throws ClassNotFoundException, SQLException {
        if (a.isEnviado()) {
            editaBase.setAnexoMovimento(a.isEnviado());
        }
        editaBase.setDataRevisao(data);
        new GenericDAO<>(BaseConhecimento.class).update(editaBase);
        listaBases();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        editaBase = new BaseConhecimento();
        a = new ArquivoUpload();
        textoArquivo = "";
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public BaseConhecimento getBase() {
        return base;
    }

    public void setBase(BaseConhecimento base) {
        this.base = base;
    }

    public BaseConhecimento getEditaBase() {
        return editaBase;
    }

    public void setEditaBase(BaseConhecimento editaBase) {
        this.editaBase = editaBase;
    }

    public BaseConhecimento getBaseSelecionado() {
        return baseSelecionado;
    }

    public void setBaseSelecionado(BaseConhecimento baseSelecionado) {
        this.baseSelecionado = baseSelecionado;
    }

    public List<BaseConhecimento> getBases() {
        return bases;
    }

    public void setBases(List<BaseConhecimento> bases) {
        this.bases = bases;
    }

    public List<Campos> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Campos> categorias) {
        this.categorias = categorias;
    }

    public boolean isEdita() {
        return edita;
    }

    public void setEdita(boolean edita) {
        this.edita = edita;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public Campos getCadCategoria() {
        return cadCategoria;
    }

    public void setCadCategoria(Campos cadCategoria) {
        this.cadCategoria = cadCategoria;
    }

    public List<Usuario> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<Usuario> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public int getQtdRegistro() {
        return qtdRegistro;
    }

    public void setQtdRegistro(int qtdRegistro) {
        this.qtdRegistro = qtdRegistro;
    }

    public String getTextoArquivo() {
        return textoArquivo;
    }

    public void setTextoArquivo(String textoArquivo) {
        this.textoArquivo = textoArquivo;
    }

}
