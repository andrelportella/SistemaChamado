package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Aniversariantes;
import Modelo.ArquivoUpload;
import Modelo.BaseConhecimento;
import Modelo.Noticia;
import Modelo.Parametros;
import Modelo.Ramais;
import Modelo.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;
import util.Util;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class NoticiasBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    Long codTec = 0L;//(Long) session.getAttribute("idUser");
    Parametros parametros = new Parametros();
    private String agendados;
    private String atrasado;
    private String aberto;
    private String finalizados;
    private Noticia noticia = new Noticia();
    private Noticia editaNoticia = new Noticia();
    private List<Usuario> autor = new ArrayList<>();
    String diretorio = "C:\\SistemaChamadoBM\\";
    String pasta = "anexosChamado";
    private StreamedContent visualizarImagem;
    ArquivoUpload a = new ArquivoUpload();
    private List<Noticia> noticias = new ArrayList<>();
    private List<String> images;
    private List<Aniversariantes> aniversariantes = new ArrayList<>();
    private List<Ramais> ramais = new ArrayList<>();
    Util util = new Util();
    private List<BaseConhecimento> bases = new ArrayList<>();
    private BaseConhecimento baseSelecionado = new BaseConhecimento();
    private StreamedContent downloadFile;
    private List<Boolean> list;
    private String textoArquivo;
    private UploadedFile file;
    Date data = new Date(System.currentTimeMillis());
    private Ramais ramalSelecionado = new Ramais();
    String codigo_negocio = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio");
    Arquivo arq = new Arquivo();
    ArquivoUtil aU = new ArquivoUtil();

    public NoticiasBean() throws IOException, ClassNotFoundException, SQLException {
        if (cod_negocio == null) {
            if (codigo_negocio != null) {
                cod_negocio = Long.parseLong(codigo_negocio);
            }
        } else {
            codTec = (Long) session.getAttribute("idUser");
        }
        autor = new GenericDAO<>(Usuario.class).listarUsuarios();
        parametros = new GenericDAO<>(Parametros.class).parametro();
        agendados = Agendados();
        atrasado = Atrasado();
        aberto = Aberto();
        finalizados = Finalizados();
        noticia.setDataPublicacao(data);
        cardNoticias();
        listaRamais();
        listaBases();
        aniversariantesDoDia();
        list = Arrays.asList(true, true, true, true, true);

    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void salvar() throws FileNotFoundException, IOException {
        noticia.setNomeArquivo(file.getFileName());
        new GenericDAO<>(Noticia.class).salvar2(noticia);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        File diretorio = new File(servletContext.getRealPath("/resources/anexos/noticias/"), noticia.getId() + file.getFileName());
        OutputStream out = new FileOutputStream(diretorio);
        out.write(file.getContents());
        out.close();
        noticia = new Noticia();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        util.redirecionarIntranet("cadastrados/listaNoticias/noticias");
    }

    public void editar(Noticia n) {
        editaNoticia = n;
    }

    public void editarNoticia() {
        new GenericDAO<>(Noticia.class).update(editaNoticia);
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        editaNoticia = new Noticia();
        util.redirecionarIntranet("cadastrados/listaNoticias/noticias");
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void upload(FileUploadEvent event) throws IOException {
        UploadedFile uploadedFile = event.getFile();
        a.setEnviado(true);
        textoArquivo += uploadedFile.getFileName() + "<br/>";
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        File diretorio = new File(servletContext.getRealPath("/resources/anexos/noticias/"), uploadedFile.getFileName());
        OutputStream out = new FileOutputStream(diretorio + pasta);
        out.write(uploadedFile.getContents());
        out.close();
        noticia.setNomeArquivo(uploadedFile.getFileName());
    }

    public void cardNoticias() throws IOException {
        List<Noticia> noticias = new ArrayList<>();
        sql = " SELECT top 10 u.nome,n.* FROM Noticia n "
                + " JOIN usuario u on u.id = n.cod_autor "
                + " WHERE n.COD_NEGOCIO =" + cod_negocio + " "
                + " ORDER BY dataPublicacao desc ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Noticia n = new Noticia();
            while (rs.next()) {
                n.setAutor(rs.getString(1));
                n.setCod_arquivo(rs.getLong("cod_arquivo"));
                n.setCod_autor(rs.getLong("cod_autor"));
                n.setDataPublicacao(rs.getDate("dataPublicacao"));
                n.setDescricao(rs.getString("descricao"));
                n.setId(rs.getLong("id"));
                n.setNomeArquivo(rs.getString("nomeArquivo"));
                n.setTitulo(rs.getString("titulo"));
                n.setAnexoMovimento(rs.getBoolean("anexoMovimento"));
                File file = new File(diretorio, pasta);
                noticias.add(n);
                n = new Noticia();
            }
            this.noticias = noticias;
            n = null;
            noticias = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "cardNoticias");
            System.out.println(" public void cardNoticias() Erro:" + e);
        }
    }

    public void listaRamais() {
        List<Ramais> rms = new ArrayList<>();
        sql = " SELECT u.nome, SUBSTRING(u.telefone,10,4), c.descricao as site, c2.descricao as setor, u.email,u.celular,u.telefone FROM Usuario u  "
                + " JOIN Campos c on c.cod = u.cod_site and c.tabela = 2 "
                + " JOIN Campos c2 on c2.cod = u.cod_setor and c2.tabela = 4 "
                + " WHERE u.cod_negocio = 1 and c.cod_negocio = " + cod_negocio + "  and c2.cod_negocio = " + cod_negocio + " "
                + " and c.descricao not in ('Bimbarras') and u.status = 1 "
                + " order by site,setor, nome ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Ramais r = new Ramais();
            while (rs.next()) {
                r.setNome(rs.getString(1));
                r.setTelefone(rs.getString(2));
                r.setEmpresa(rs.getString(3));
                r.setSetor(rs.getString(4));
                r.setEmail(rs.getString(5));
                r.setCelular(rs.getString(6));
                r.setSite(rs.getString(7));
                rms.add(r);
                r = new Ramais();
            }
            r = null;
            this.ramais = rms;
            rms = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "listaRamais");
            System.out.println("public void listaRamais() Erro:" + e);
        }

    }

    public void aniversariantesDoDia() {
        List<Aniversariantes> result = new ArrayList();
        sql = "SELECT * FROM Aniversariantes WHERE dataAniversario >= SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),9,2) + '/' + SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) "
                + " and dataAniversario <= (select MAX(dataAniversario) from Aniversariantes where SUBSTRING(dataAniversario,4,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2)) ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Aniversariantes a = new Aniversariantes();
            while (rs.next()) {
                a.setEmpresa(rs.getString("EMPRESA"));
                a.setDataAniversario(rs.getString("DataAniversario"));
                a.setNome(rs.getString("NOME"));
                result.add(a);
                a = new Aniversariantes();
            }
            result = null;
            a = null;
            aniversariantes = result;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "aniversariantesDoDia");
            System.out.println(" public void aniversariantesDoDia() Erro:" + e);
        }
    }

    public StreamedContent exibirImage(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        return visualizarImagem = new DefaultStreamedContent(inputStream,
                Files.probeContentType(file.toPath()), file.getName());
    }

    public void listaBases() {
        List<BaseConhecimento> campos = new ArrayList<>();
        String sql2 = " SELECT t.nome, categoria.descricao, b.* FROM BaseConhecimento b "
                + " LEFT JOIN Usuario t on t.id = b.cod_responsavel "
                + " LEFT JOIN Campos categoria on categoria.cod = b.cod_categoria and categoria.tabela = 22 and categoria.cod_negocio = " + cod_negocio + " "
                + " where b.cod_negocio =" + cod_negocio + " and tipoBase = 2"
                + " order by b.dataRevisao ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
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
                b.setResponsavel(rs.getString(1));
                b.setCategoria(rs.getString(2));
                b.setEdita(!Objects.equals(b.getCod_responsavel(), codTec));
                if (b.isAnexoMovimento()) {
                    b.setArquivo(aU.listar(b.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaBC()));
                }
                campos.add(b);
                b = new BaseConhecimento();
            }
            bases = campos;
            b = null;
            campos = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "listaBases");
            System.out.println("public void listaBases() Erro:" + e);
        }
    }

    public String Atrasado() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and DATEDIFF (DAY,atualizadoHa,GETDATE()) > " + parametros.getQtdDiasChamadoAtrasado() + " and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "Atrasado");
            System.out.println("public String Atrasado() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados em atraso.";
        } else {
            return "Você não tem chamado em atraso.";
        }

    }

    public String Agendados() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and cod_tipoAtendimento = " + parametros.getCodPreventiva() + " and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "Agendados");
            System.out.println(" public String Agendados() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados agendados.";
        } else {
            return "Você não tem chamado agendado.";
        }

    }

    public String Aberto() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and cod_tipoAtendimento <> " + parametros.getCodPreventiva() + " and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "Aberto");
            System.out.println(" public String Aberto() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados em aberto.";
        } else {
            return "Você não tem chamados em aberto.";
        }

    }

    public String Finalizados() {
        int tam = 0;
        sql = " SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + " "
                + " and id_tecnico = " + codTec + "  and statusAtendimento = 0 "
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "NoticiasBean", "Finalizados");
            System.out.println("  public String Finalizados() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados finalizados.";
        } else {
            return "Você não tem chamado finalizados.";
        }

    }

    public String getAgendados() {
        return agendados;
    }

    public void setAgendados(String agendados) {
        this.agendados = agendados;
    }

    public String getAtrasado() {
        return atrasado;
    }

    public void setAtrasado(String atrasado) {
        this.atrasado = atrasado;
    }

    public String getAberto() {
        return aberto;
    }

    public void setAberto(String aberto) {
        this.aberto = aberto;
    }

    public String getFinalizados() {
        return finalizados;
    }

    public void setFinalizados(String finalizados) {
        this.finalizados = finalizados;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public List<Usuario> getAutor() {
        return autor;
    }

    public void setAutor(List<Usuario> autor) {
        this.autor = autor;
    }

    public List<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    public StreamedContent getVisualizarImagem() {
        return visualizarImagem;
    }

    public void setVisualizarImagem(StreamedContent visualizarImagem) {
        this.visualizarImagem = visualizarImagem;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Aniversariantes> getAniversariantes() {
        return aniversariantes;
    }

    public void setAniversariantes(List<Aniversariantes> aniversariantes) {
        this.aniversariantes = aniversariantes;
    }

    public List<Ramais> getRamais() {
        return ramais;
    }

    public void setRamais(List<Ramais> ramais) {
        this.ramais = ramais;
    }

    public List<BaseConhecimento> getBases() {
        return bases;
    }

    public void setBases(List<BaseConhecimento> bases) {
        this.bases = bases;
    }

    public BaseConhecimento getBaseSelecionado() {
        return baseSelecionado;
    }

    public void setBaseSelecionado(BaseConhecimento baseSelecionado) {
        this.baseSelecionado = baseSelecionado;
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

    public String getTextoArquivo() {
        return textoArquivo;
    }

    public void setTextoArquivo(String textoArquivo) {
        this.textoArquivo = textoArquivo;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Ramais getRamalSelecionado() {
        return ramalSelecionado;
    }

    public void setRamalSelecionado(Ramais ramalSelecionado) {
        this.ramalSelecionado = ramalSelecionado;
    }

    public Noticia getEditaNoticia() {
        return editaNoticia;
    }

    public void setEditaNoticia(Noticia editaNoticia) {
        this.editaNoticia = editaNoticia;
    }
}
