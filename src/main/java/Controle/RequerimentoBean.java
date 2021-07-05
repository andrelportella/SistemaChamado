/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.ArquivoUpload;
import Modelo.Campos;
import Modelo.Cliente;
import Modelo.Embarcacao_Loja;
import Modelo.Local_Requerimento;
import Modelo.MovimentacaoRequerimento;
import Modelo.Negocio;
import Modelo.Parametros;
import Modelo.Requerente;
import Modelo.Requerimento;
import Modelo.Usuario;
import java.awt.image.BufferedImage;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;
import util.ModeloEmail;
import util.Util;
import util.ValidacoesBanco;

/**
 *
 * @author ricardo
 */
@ViewScoped
@ManagedBean
public class RequerimentoBean implements Serializable {

    private Local_Requerimento cadlocal = new Local_Requerimento();
    private BufferedImage imagem;
    private Requerente requerente = new Requerente();
    private Requerimento requerimento = new Requerimento();
    private Requerimento finalizarRequerimento = new Requerimento();
    private Requerimento editaRequerimento = new Requerimento();
    private Requerimento requerimentoSelecionado = new Requerimento();
    Util util = new Util();
    PreparedStatement stmt;
    ResultSet rs;
    PreparedStatement stmt2;
    ResultSet rs2;
    private Connection ConexaoSQL;
    String sql;
    private List<Requerente> requerentes = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private List<Requerimento> requerimentos = new ArrayList<>();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Embarcacao_Loja> embarcacoes_lojas = new ArrayList<>();
    private List<MovimentacaoRequerimento> listaRequerimentoSelecionado = new ArrayList<>();
    private List<Usuario> tecnicos = new ArrayList<>();
    private List<Campos> natureza = new ArrayList<>();
    private List<Campos> status = new ArrayList<>();
    private List<Campos> statusTab = new ArrayList<>();
    private List<Local_Requerimento> local = new ArrayList<>();
    private List<Campos> categoria = new ArrayList<>();
    private Cliente cliente = new Cliente();
    private List<Usuario> usuarios = new ArrayList<>();
    private Date data = new Date(System.currentTimeMillis());
    private Embarcacao_Loja embarcacao_Loja = new Embarcacao_Loja();
    private MovimentacaoRequerimento movimentacaoRequerimento = new MovimentacaoRequerimento();
    private MovimentacaoRequerimento movimentarRequerimento = new MovimentacaoRequerimento();
    private Usuario movimentarTecnico = new Usuario();
    private boolean opcaoTecnico;
    private List<MovimentacaoRequerimento> listaMovimentos = new ArrayList<>();
    private Usuario tecnico = new Usuario();
    private int tamanho;
    private int tamanhoHist;
    private int tamanhoMov;
    LoginBean login = new LoginBean();
    private boolean statusBotao;
    private Part file;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private Negocio negocio = new Negocio();
    private Campos cadNatureza = new Campos();
    private Requerente cadRequerente = new Requerente();
    private boolean campooTabela;
    private String nomeCampo = "Mostrar Todos os Requerimentos";
    private String nomeTabela = "Requerimentos Abertos Para Você";
    GerarCpfCnpj gerarCpfCnpj = new GerarCpfCnpj();
    private String nomeAnexo;
    private Long cod_tecnico;
    private boolean rendCli;
    Parametros parametros = new Parametros();
    private String textoArquivo = "";
    private String textoArquivoMov = "";
    ArquivoUpload a = new ArquivoUpload();
    ArquivoUpload aMov = new ArquivoUpload();
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();
    private StreamedContent fileDownload;
    ModeloEmail modeloEmail;
    Long cod_user;
    private UploadedFile uploadedFile;
    private StreamedContent downloadFile;
    private MovimentacaoRequerimento requerimentoSelecionadoMov = new MovimentacaoRequerimento();
    private List<Boolean> list = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false);
    boolean carregarCombos;

    public RequerimentoBean() throws ClassNotFoundException, SQLException {
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        campooTabela = false;
        parametros = new GenericDAO<>(Parametros.class).parametro();
        movimentarRequerimento.setDataMovimento(data);
        movimentarRequerimento.setHoraMovimento(data);
        //downloadArquivoPDF();
        requerimento.setDataAbertura(data);
        requerimento.setHoraAbertura(data);
        tecnico.setId((Long) session.getAttribute("idUser"));
        requerimentos = listarRequerimento();
        negocio();
        listCli();
    }

    public void listCli() {
        if (cod_negocio == 3L) {
            rendCli = true;
        } else {
            rendCli = false;
        }
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void atualizaCampos() throws ClassNotFoundException, SQLException, IOException {

        if (cadlocal.getNome() != null) {
            new GenericDAO<>(Local_Requerimento.class).salvar(cadlocal);
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarLocDialog').hide();");
            cadlocal = new Local_Requerimento();
            local = new GenericDAO<>(Campos.class).listarLocalRequerimento();
        } else if (cadNatureza.getDescricao() != null) {
            if (!verificaAcesso()) {
                CamposBean camposBean = new CamposBean();
                cadNatureza.setCod(camposBean.verificaCodigo(cadNatureza) + 1L);
                new GenericDAO<>(Campos.class).salvar(cadNatureza);
                cadNatureza = new Campos();
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarNatDialog').hide();");
                cadNatureza = new Campos();
                natureza = new GenericDAO<>(Campos.class).listarCamposJDBC(18L);
            }
        } else if (cadRequerente.getNome() != null) {
            new GenericDAO<>(Requerente.class).salvar(cadRequerente);
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarReqDialog').hide();");
            cadRequerente = new Requerente();
            requerentes = new GenericDAO<>(Requerente.class).listarRequerentes();
        }
        System.out.println("Passou aqui");
        //util.redirecionarRequerimento("cadastrados/requerimentosCadastrados");
    }

    public boolean verificaAcesso() throws SQLException, ClassNotFoundException {
        try {
            sql = "SELECT tabela FROM campos WHERE Descricao=? and cod_negocio =" + cod_negocio + " ";

            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, cadNatureza.getDescricao());
            rs = stmt.executeQuery();
            if (rs.next()) {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                statusBotao = true;
                org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                return true;
            } else {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                statusBotao = false;
                return false;
            }

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "verificaAcesso");
            return false;
        }

    }

    public Negocio negocio() throws ClassNotFoundException, SQLException {
        Negocio m = new Negocio();
        String sql2 = " SELECT neg.descricao, m.* FROM Negocio m "
                + " LEFT join Campos neg on m.cod_negocio = neg.cod and neg.tabela = 21 "
                + " where m.cod_negocio =" + cod_negocio + " "
                + " order by neg.descricao ";
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setCod_negocio(rs.getLong("Cod_negocio"));
                m.setLogin(rs.getString("login"));
                m.setNegocio(rs.getString("descricao"));
            }
            //ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "negocio");
        }
        return negocio = m;

    }

    public List listaRequerimentoMovimentos(Requerimento r) throws ClassNotFoundException, SQLException {
        List<MovimentacaoRequerimento> campos = new ArrayList<>();
        String sql2 = " SELECT status.descricao, t.nome, m.* FROM MovimentacaoRequerimento m "
                + " LEFT JOIN Usuario t on t.id = m.id_tecnico and t.cod_negocio = " + cod_negocio + " "
                + " LEFT JOIN campos status  on status.cod = m.cod_status and status.tabela =20 and status.cod_negocio = " + cod_negocio + " "
                + " where m.id_requerimento = " + r.getId() + " "
                + "     order by m.DataMovimento desc, horaMovimento desc";
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                MovimentacaoRequerimento m = new MovimentacaoRequerimento();
                m.setId(rs.getLong("id"));
                m.setDataMovimento(rs.getDate("DataMovimento"));
                m.setHoraMovimento(rs.getTime("HoraMovimento"));
                m.setObservacao(rs.getString("observacao"));
                m.setCod_status(rs.getLong("cod_status"));
                m.setDespesa(rs.getDouble("despesa"));
                m.setStatus(rs.getString("descricao"));
                m.setTecnico(rs.getString("nome"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setAnexo(rs.getString("anexo"));
                m.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                m.setEnviaEmailCli(rs.getBoolean("enviaEmailCli"));
                m.setEnviaEmailReq(rs.getBoolean("enviaEmailReq"));
                m.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (m.isAnexoMovimento()) {
                    m.setArquivo(aU.listar(m.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento()));
                }
                campos.add(m);
            }
            //ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "listaRequerimentoMovimentos");
        }
        tamanhoMov = campos.size();
        return campos;

    }

    public void status() throws SQLException, ClassNotFoundException {
        if (cadRequerente.getCpf() != null) {
            verificaCPF();
        }

        if (cadRequerente.getEmail() != null) {
            verificaEmail();
        }

    }

    public List<Requerimento> listarRequerimento() throws ClassNotFoundException, SQLException {
        List<Requerimento> requerimentos = new ArrayList<>();

        String sql2 = "";

        String sql3 = "";
        String cliente = "";
        String embarcacao = "";
        if (cod_negocio == 3L) {
            cliente = " LEFT JOIN Cliente c on r.id_cliente = c.codigoSTAI and c.cod_negocio = " + cod_negocio + " ";
            embarcacao = " LEFT JOIN Embarcacao_Loja e on r.id_embarcacao = e.codigoSTAI and e.cod_negocio = " + cod_negocio + " ";
        } else {
            cliente = " LEFT JOIN Cliente c on r.id_cliente = c.id and c.cod_negocio = " + cod_negocio + " ";
            embarcacao = " LEFT JOIN Embarcacao_Loja e on r.id_embarcacao = e.id and e.cod_negocio = " + cod_negocio + " ";
        }
        if (campooTabela) {
            sql2 = " SELECT t.id as id1,t.nome as tecnico, c.nome as cliente, e.nomeBarco as embarcacao, "
                    + " n.descricao as natureza, req.nome as requerente, l.nome, status.descricao as status, SUBSTRING(r.descricao,1,50) AS RESUMO, "
                    + " DATEDIFF (DAY,r.atualizadoHa,GETDATE()) as data, r.* FROM Requerimento r "
                    + " LEFT JOIN Usuario t on r.id_tecnico = t.id and t.cod_negocio = " + cod_negocio + " "
                    + cliente
                    + embarcacao
                    + " LEFT JOIN Local_Requerimento l on r.id_local = l.id and l.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN Campos n on r.id_natureza = n.cod and n.tabela = 18 and n.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN campos status  on status.cod = r.cod_status and status.tabela =20 and status.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN Requerente req on r.id_requerente= req.id and req.cod_negocio = " + cod_negocio + " "
                    + " where r.cod_negocio=" + cod_negocio + "";
            nomeCampo = "Mostrar Seus Requerimentos em Aberto";
            nomeTabela = "Todos os Requerimentos";
            campooTabela = false;
        } else {
            sql2 = " SELECT t.id as id1,t.nome as tecnico, c.nome as cliente, e.nomeBarco as embarcacao, "
                    + " n.descricao as natureza, req.nome as requerente, l.nome, status.descricao as status, SUBSTRING(r.descricao,1,50) AS RESUMO, "
                    + " DATEDIFF (DAY,r.atualizadoHa,GETDATE()) as data, r.* FROM Requerimento r "
                    + " LEFT JOIN Usuario t on r.id_tecnico = t.id and t.cod_negocio = " + cod_negocio + " "
                    + cliente
                    + embarcacao
                    + " LEFT JOIN Local_Requerimento l on r.id_local = l.id and l.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN Campos n on r.id_natureza = n.cod and n.tabela = 18 and n.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN campos status  on status.cod = r.cod_status and status.tabela =20 and status.cod_negocio = " + cod_negocio + " "
                    + " LEFT JOIN Requerente req on r.id_requerente= req.id and req.cod_negocio = " + cod_negocio + " "
                    + " where (r.cod_status not in (3,4)) and r.cod_negocio =" + cod_negocio + " "
                    + " and r.id_tecnico =" + tecnico.getId() + "";
            nomeCampo = "Mostrar Todos os Requerimentos";
            nomeTabela = "Requerimentos Abertos Para Você";
            campooTabela = true;
        }
        //ConexaoSQL = new ConexaoSQLServer().getConnection();
        stmt = ConexaoSQL.prepareStatement(sql2);
        rs = stmt.executeQuery();

        while (rs.next()) {
            Requerimento r = new Requerimento();
            Usuario t = new Usuario();
            r.setId(rs.getLong("id"));
            r.setSituacao(rs.getBoolean("situacao"));
            r.setDataAbertura(rs.getDate("DataAbertura"));
            r.setDataFechamento(rs.getDate("DataFechamento"));
            r.setDescricao(rs.getString("descricao"));
            r.setDespesa(rs.getDouble("despesa"));
            r.setHoraAbertura(rs.getTime("HoraAbertura"));
            r.setHoraFechamento(rs.getTime("HoraFechamento"));
            r.setId_cliente(rs.getLong("id_cliente"));
            r.setId_embarcacao(rs.getLong("id_embarcacao"));
            r.setId_local(rs.getLong("id_local"));
            r.setId_natureza(rs.getLong("id_natureza"));
            r.setId_requerente(rs.getLong("id_requerente"));
            r.setCod_status(rs.getLong("Cod_status"));
            r.setNomeArquivo(rs.getString("NomeArquivo"));
            r.setAnexo(rs.getString("Anexo"));
            r.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
            r.setEnviaEmailCli(rs.getBoolean("EnviaEmailCli"));
            r.setEnviaEmailReq(rs.getBoolean("EnviaEmailReq"));
            r.setCod_arquivo(rs.getLong("Cod_arquivo"));
            t.setId(rs.getLong(1));
            r.setTecnico(rs.getString(2));
            r.setCliente(rs.getString(3));
            r.setEmbarcacao(rs.getString(4));
            r.setNatureza(rs.getString(5));
            r.setRequerente(rs.getString(6));
            r.setLocal(rs.getString(7));
            r.setStatus(rs.getString(8));
            r.setResumo(rs.getString(9));
            r.setTipoData(rs.getInt(10));
            r.setId_tecnico(t);
            if (r.getCod_status() == 3L || r.getCod_status() == 4L) {
                r.setTipoData(0);
            }

            if (r.isAnexoMovimento()) {
                r.setArquivo(aU.listar(r.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento()));
            }

            requerimentos.add(r);
        }
        //ConexaoSQL.close();
        rs.close();
        //rs2.close();
        stmt.close();
        //stmt2.close();
        tamanho = requerimentos.size();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        return this.requerimentos = requerimentos;
    }

    public List listaRequerimentoMovimentosSelecionado(Requerimento r) throws ClassNotFoundException, SQLException {
        List<MovimentacaoRequerimento> movimentos = new ArrayList<>();
        String sql2 = "SELECT t.nome, status.descricao, m.* FROM MovimentacaoRequerimento m "
                + " LEFT JOIN Usuario t on t.id = m.id_tecnico and t.cod_negocio = " + cod_negocio + " "
                + " LEFT JOIN campos status  on status.cod = m.cod_status and status.tabela =20 and status.cod_negocio = " + cod_negocio + " "
                + " where id_requerimento = " + r.getId() + ""
                + " order by m.DataMovimento, m.horaMovimento";
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                MovimentacaoRequerimento m = new MovimentacaoRequerimento();
                m.setId(rs.getLong("id"));
                m.setDataMovimento(rs.getDate("DataMovimento"));
                m.setHoraMovimento(rs.getTime("HoraMovimento"));
                m.setObservacao(rs.getString("observacao"));
                m.setCod_status(rs.getLong("cod_status"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setAnexo(rs.getString("anexo"));
                m.setDespesa(rs.getDouble("despesa"));
                m.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                m.setTecnico(rs.getString(1));
                m.setStatus(rs.getString(2));
                m.setEnviaEmailCli(rs.getBoolean("EnviaEmailCli"));
                m.setEnviaEmailReq(rs.getBoolean("EnviaEmailReq"));
                m.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (m.isAnexoMovimento()) {
                    m.setArquivo(aU.listar(m.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento()));
                }
                movimentos.add(m);
            }
            //ConexaoSQL.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "listaRequerimentoMovimentosSelecionado");
        }
        tamanhoHist = movimentos.size();
        return this.listaRequerimentoSelecionado = movimentos;

    }

    public void salvar() throws ClassNotFoundException, SQLException {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        data = new Date(System.currentTimeMillis());

        cod_user = (Long) session.getAttribute("idUser");

        tecnico.setId(parametros.getCodSupervisorRequerimento());
        requerimento.setId_tecnico(tecnico);
        requerimento.setCod_status(0L);
        requerimento.setSituacao(false);
        requerimento.setCod_arquivo(a.getId());
        if (a.isEnviado()) {
            requerimento.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
        }
        new GenericDAO<>(Requerimento.class).salvar2(requerimento);

        movimentacaoRequerimento.setId_requerimento(requerimento);
        movimentacaoRequerimento.setId_tecnico(tecnico);
        movimentacaoRequerimento.setCod_status(1L);
        movimentacaoRequerimento.setId_cliente(requerimento.getId_cliente());
        movimentacaoRequerimento.setId_embarcacao(requerimento.getId_embarcacao());
        movimentacaoRequerimento.setId_local(requerimento.getId_local());
        movimentacaoRequerimento.setId_natureza(requerimento.getId_natureza());
        movimentacaoRequerimento.setAnexoMovimento(requerimento.isAnexoMovimento());
        movimentacaoRequerimento.setCod_arquivo(requerimento.getCod_arquivo());
        movimentacaoRequerimento.setDataMovimento(requerimento.getDataAbertura());
        movimentacaoRequerimento.setHoraMovimento(requerimento.getHoraAbertura());
        movimentacaoRequerimento.setObservacao(requerimento.getDescricao());
        movimentacaoRequerimento.setAnexoMovimento(requerimento.isAnexoMovimento());
        movimentacaoRequerimento.setAnexo(requerimento.getAnexo());
        movimentacaoRequerimento.setNomeArquivo(requerimento.getNomeArquivo());
        movimentacaoRequerimento.setId_requerente(requerimento.getId_requerente());
        new GenericDAO<>(MovimentacaoRequerimento.class).salvar(movimentacaoRequerimento);
        sendMail(requerimento);
        campooTabela = false;
        requerimentos = listarRequerimento();
        movimentacaoRequerimento = new MovimentacaoRequerimento();
        requerimento = new Requerimento();
        requerimento.setDataAbertura(data);
        requerimento.setHoraAbertura(data);
        tecnico = new Usuario();
        FacesUtil.addMsgInfo("Sucesso", "Cadastrado com sucesso!");
        a = new ArquivoUpload();
        PrimeFaces.current().executeScript("PF('cadastrarDialog').hide();");
        PrimeFaces.current().ajax().update("req_form:requerimento");
        campooTabela = false;
        textoArquivo = "";
        textoArquivoMov = "";

    }

    public void editarObsMov(Requerimento r) {
        String x = r.getDescricao().replaceAll("[']", "");
        if (r.isAnexoMovimento()) {
            sql = "UPDATE MovimentacaoRequerimento SET OBSERVACAO ='" + x + "', AnexoMovimento = 1 where id = " + pegaId(r.getId()) + " ";
        } else {
            sql = "UPDATE MovimentacaoRequerimento SET OBSERVACAO ='" + x + "', AnexoMovimento = 0 where id = " + pegaId(r.getId()) + " ";
        }
        ValidacoesBanco.update(sql, "RequerimentoBean", "editarObsMov");
    }

    public Long pegaId(Long id) {
        sql = " SELECT MIN(ID) FROM MovimentacaoRequerimento WHERE id_requerimento = " + id + "";
        return ValidacoesBanco.retornaLong(sql, "RequerimentoBean", "pegaId");
    }

    public void sendMail(Requerimento r) {
        modeloEmail = new ModeloEmail();
        modeloEmail.sendMailRequerimento(r);
    }

    public void sendMailMov(MovimentacaoRequerimento r) {
        modeloEmail = new ModeloEmail();
        modeloEmail.sendMailMovRequerimento(r);
    }

    public void editar(Requerimento req) {
        editaRequerimento = req;
        tecnico.setId(req.getId_tecnico().getId());
        carregaLista();

    }

    public void editarRequerimento() throws ClassNotFoundException, SQLException {
        campooTabela = false;
        editaRequerimento.setId_tecnico(tecnico);
        if (a.isEnviado()) {
            editaRequerimento.setAnexoMovimento(true);
        }
        editarObsMov(editaRequerimento);
        new GenericDAO<>(Requerimento.class).update(editaRequerimento);
        requerimentos = listarRequerimento();

        editaRequerimento = new Requerimento();
        a = new ArquivoUpload();
        campooTabela = false;
        textoArquivo = "";
        textoArquivoMov = "";
        PrimeFaces.current().ajax().update("req_form:requerimento");
        PrimeFaces.current().executeScript("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Editado com Sucesso!");
    }

    public void mudarTecnico(MovimentacaoRequerimento r) throws ClassNotFoundException, SQLException {
        sql = "UPDATE requerimento set id_tecnico =" + r.getId_tecnico().getId() + " where id = " + r.getId_requerimento().getId() + "";
        //ConexaoSQL = new ConexaoSQLServer().getConnection();
        stmt = ConexaoSQL.prepareStatement(sql);
        stmt.execute();
        //ConexaoSQL.close();
        stmt.close();
        requerimentos = listarRequerimento();
    }

    public void salvarMovimento() throws ParseException, ParseException, ClassNotFoundException, SQLException {

        finalizarRequerimento.setCod_status(movimentarRequerimento.getCod_status());
        finalizarRequerimento.setAtualizadoHa(movimentarRequerimento.getDataMovimento());
        movimentarRequerimento.setAnexoMovimento(aMov.isEnviado());
        new GenericDAO<>(Requerimento.class).update(finalizarRequerimento);
        if (movimentarRequerimento.getCod_status() == 3L || movimentarRequerimento.getCod_status() == 4L) {
            if (opcaoTecnico) {
                mudarTecnico(movimentarRequerimento);
                if (movimentarRequerimento.getId_tecnico().getId() != cod_tecnico) {
                    sendMailMov(movimentarRequerimento);
                }
                Usuario t = new Usuario();
                t.setId(cod_tecnico);
                movimentarRequerimento.setId_tecnico(t);
                new GenericDAO<>(MovimentacaoRequerimento.class).salvar2(movimentarRequerimento);
                sendMailMov(movimentarRequerimento);

                requerimentos = listarRequerimento();
                finalizarRequerimento.setHoraFechamento(movimentarRequerimento.getHoraMovimento());
                finalizarRequerimento.setDataFechamento(movimentarRequerimento.getDataMovimento());
                finalizarRequerimento.setCod_status(movimentarRequerimento.getCod_status());
                finalizarRequerimento.setSituacao(true);
                new GenericDAO<>(Requerimento.class).update(finalizarRequerimento);
                movimentarRequerimento = new MovimentacaoRequerimento();
                finalizarRequerimento = new Requerimento();
                PrimeFaces.current().ajax().update("req_form:requerimento");
                PrimeFaces.current().executeScript("PF('movimentarDialog').hide();");
                aMov = new ArquivoUpload();
            } else {
                new GenericDAO<>(MovimentacaoRequerimento.class).salvar2(movimentarRequerimento);
                sendMailMov(movimentarRequerimento);
                finalizarRequerimento.setHoraFechamento(movimentarRequerimento.getHoraMovimento());
                finalizarRequerimento.setDataFechamento(movimentarRequerimento.getDataMovimento());
                finalizarRequerimento.setCod_status(movimentarRequerimento.getCod_status());
                finalizarRequerimento.setSituacao(true);
                new GenericDAO<>(Requerimento.class).update(finalizarRequerimento);
                campooTabela = false;
                requerimentos = listarRequerimento();
                movimentarRequerimento = new MovimentacaoRequerimento();
                finalizarRequerimento = new Requerimento();
                PrimeFaces.current().ajax().update("req_form:requerimento");
                PrimeFaces.current().executeScript("PF('movimentarDialog').hide();");
                aMov = new ArquivoUpload();
            }
        } else {
            if (opcaoTecnico) {
                mudarTecnico(movimentarRequerimento);
                if (movimentarRequerimento.getId_tecnico().getId() != cod_tecnico) {
                    sendMailMov(movimentarRequerimento);
                }
                Usuario t = new Usuario();
                t.setId(cod_tecnico);
                movimentarRequerimento.setId_tecnico(t);
                new GenericDAO<>(MovimentacaoRequerimento.class).salvar2(movimentarRequerimento);
                sendMailMov(movimentarRequerimento);
                movimentarRequerimento = new MovimentacaoRequerimento();
                aMov = new ArquivoUpload();
                campooTabela = false;
                requerimentos = listarRequerimento();
                movimentarRequerimento = new MovimentacaoRequerimento();
                finalizarRequerimento = new Requerimento();
                PrimeFaces.current().ajax().update("req_form:requerimento");
                PrimeFaces.current().executeScript("PF('movimentarDialog').hide();");
            } else {
                new GenericDAO<>(MovimentacaoRequerimento.class).salvar2(movimentarRequerimento);
                sendMailMov(movimentarRequerimento);
                aMov = new ArquivoUpload();
                movimentarRequerimento = new MovimentacaoRequerimento();
                movimentar(finalizarRequerimento);
                PrimeFaces.current().ajax().update("movimentos_form:movimentar");
                PrimeFaces.current().ajax().update("formMov");
            }

        }
        opcaoTecnico = false;
        campooTabela = false;
        textoArquivo = "";
        textoArquivoMov = "";

    }

    public void anexaArquivo() {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
        carregaLista();
    }

    public void upload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento());
    }

    public void uploadMov(FileUploadEvent event) throws IOException {
        if (aMov.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(aMov);
        }
        aMov.setEnviado(true);
        textoArquivoMov += arq.upload(event, aMov.getId(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento());
    }

    public void editUpload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, editaRequerimento.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaRequerimento());
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void deletarArquivo(File file) throws IOException, ParseException, ClassNotFoundException, SQLException {
        deletarArquivo(file, editaRequerimento, requerimentoSelecionadoMov, requerimentoSelecionado);
        requerimentos = listarRequerimento();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogMov').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('movimentarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEdit').hide();");
    }

    public void deletarArquivo(File file, Requerimento editaRequerimento, MovimentacaoRequerimento requerimentoSelecionadoMov, Requerimento requerimentoSelecionado) throws IOException, ParseException, ClassNotFoundException {
        (new File(file.getAbsolutePath())).delete();
        File f = new File(file.getParent());
        if (f.listFiles().length == 0) {
            if (editaRequerimento.getId() != null) {
                atualizaArquivo(editaRequerimento.getId(), 1);
                atualizaArquivo(pegaidMovimentacaoMin(editaRequerimento.getId()), 0);
            } else if (requerimentoSelecionadoMov.getId() != null) {
                atualizaArquivo(requerimentoSelecionadoMov.getId(), 0);
            } else {
                atualizaArquivo(requerimentoSelecionado.getId(), 0);
            }
        }
    }

    public void carregaLista() {
        data = new Date(System.currentTimeMillis());
        requerimento.setDataAbertura(data);
        requerimento.setHoraAbertura(data);
        if (!carregarCombos) {
            carregarCombos = true;

            natureza = new GenericDAO<>(Campos.class).listarCamposJDBC(18L);
            local = new GenericDAO<>(Campos.class).listarLocalRequerimento();
            requerentes = new GenericDAO<>(Requerente.class).listarRequerentes();
            clientes = new GenericDAO<>(Cliente.class).listarClientesCombo();
            embarcacoes_lojas = new GenericDAO<>(Embarcacao_Loja.class).listarLojas();
            tecnicos = new GenericDAO<>(Usuario.class).listarTecnicosRequerimento();
            status = new GenericDAO<>(Usuario.class).listarCamposJDBC(20L);
        }
    }

    public void movimentar(Requerimento r) throws ClassNotFoundException, SQLException {
        if (aMov.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(aMov);
        }
        data = new Date(System.currentTimeMillis());
        movimentarRequerimento.setId_requerimento(r);
        movimentarTecnico.setId(r.getId_tecnico().getId());
        movimentarRequerimento.setId_tecnico(movimentarTecnico);
        movimentarRequerimento.setId_requerente(r.getId_requerente());
        movimentarRequerimento.setObsRequerimento(r.getDescricao());
        movimentarRequerimento.setId_natureza(r.getId_natureza());
        movimentarRequerimento.setId_local(r.getId_local());
        movimentarRequerimento.setId_cliente(r.getId_cliente());
        movimentarRequerimento.setId_embarcacao(r.getId_embarcacao());
        movimentarRequerimento.setEnviaEmailCli(r.isEnviaEmailCli());
        movimentarRequerimento.setCod_arquivo(aMov.getId());
        movimentarRequerimento.setDataMovimento(data);
        movimentarRequerimento.setHoraMovimento(data);
        movimentarRequerimento.setCod_status(r.getCod_status());
        cod_tecnico = r.getId_tecnico().getId();
        listaMovimentos = listaRequerimentoMovimentos(r);
        finalizarRequerimento = r;
        carregaLista();
    }

    public Long pegaidMovimentacaoMin(Long id) {
        sql = "select min(id) from movimentacaoRequerimento where id_requerimento = " + id + "";
        return ValidacoesBanco.retornaLong(sql, "RequerimentoDAO", "pegaidMovimentacaoMin");
    }

    public void atualizaArquivo(Long cod, int tipo) {
        if (tipo == 1) {
            sql = "UPDATE REQUERIMENTO SET ANEXOMOVIMENTO = 0 WHERE ID = " + cod + "";
        } else {
            sql = "UPDATE MOVIMENTACAOREQUERIMENTO SET ANEXOMOVIMENTO = 0 WHERE ID = " + cod + "";
        }
        ValidacoesBanco.update(sql, "RequerimentoDAO", "atualizaArquivo");
    }

    public void filtraStatus() {
        Embarcacao_Loja el = new Embarcacao_Loja();
        String sql2 = "";
        if (cod_negocio == 3L) {
            sql2 = "SELECT * FROM Embarcacao_Loja where codigoSTAI=" + requerimento.getId_cliente() + " ";
        } else {
            sql2 = "SELECT * FROM Embarcacao_Loja where id=" + requerimento.getId_cliente() + " ";
        }
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                if (cod_negocio == 3L) {
                    el.setCodigoSTAI(rs.getLong("codigoSTAI"));
                } else {
                    el.setCodigoSTAI(rs.getLong("id"));
                }
            }
            //ConexaoSQL.close();
            rs.close();
            stmt.close();
            //validaEquipamento();

        } catch (SQLException e) {
            System.out.println(e);
            FacesUtil.addMsgFatalSQL(e, "Erro", "RequerimentoBean", "filtraStatus");
        }
        requerimento.setId_embarcacao(el.getCodigoSTAI());
    }

    public void verificaCPF() throws SQLException, ClassNotFoundException {

        if (gerarCpfCnpj.isCPF(cadRequerente.getCpf())) {
            try {
                //ConexaoSQL = new ConexaoSQLServer().getConnection();
                sql = " SELECT cpf FROM Requerente WHERE cpf=?  and cod_negocio =" + cod_negocio + " ";
                stmt = ConexaoSQL.prepareStatement(sql);
                stmt.setString(1, cadRequerente.getCpf());
                rs = stmt.executeQuery();
                if (rs.next()) {
                    //ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    FacesUtil.addMsgError("CPF Já cadastrado", "Erro");
                    statusBotao = true;
                    //return true;
                } else {
                    //ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    statusBotao = false;
                    //return false;
                }
            } catch (SQLException e) {
                FacesUtil.addMsgError(e.toString(), "Erro");
                statusBotao = false;
                //return false;
            }
        } else {
            FacesUtil.addMsgError("Erro", "CPF Inválido!");
            statusBotao = true;
            //return true;
        }

    }

    public void verificaEmail() throws SQLException, ClassNotFoundException {
        try {
            //ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT email FROM Requerente WHERE email=? and cod_negocio =" + cod_negocio + " ";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, cadRequerente.getEmail());
            rs = stmt.executeQuery();
            if (rs.next()) {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                FacesUtil.addMsgError("Email Já cadastrado", "Erro");
                statusBotao = true;
                //return true;
            } else {
                //ConexaoSQL.close();
                rs.close();
                stmt.close();
                statusBotao = false;
                //return false;
            }
        } catch (SQLException e) {
            FacesUtil.addMsgError(e.toString(), "Erro");
            statusBotao = false;
            //return false;
        }

    }

    public void listarArquivos(MovimentacaoRequerimento mr) {
        this.requerimentoSelecionadoMov = mr;
    }

    public List<Campos> getStatus() {
        return status;
    }

    public void setStatus(List<Campos> status) {
        this.status = status;
    }

    public Requerente getRequerente() {
        return requerente;
    }

    public void setRequerente(Requerente requerente) {
        this.requerente = requerente;
    }

    public List<Requerente> getRequerentes() {
        return requerentes;
    }

    public void setRequerentes(List<Requerente> requerentes) {
        this.requerentes = requerentes;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<Requerimento> getRequerimentos() {
        return requerimentos;
    }

    public void setRequerimentos(List<Requerimento> requerimentos) {
        this.requerimentos = requerimentos;
    }

    public List<Campos> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<Campos> categoria) {
        this.categoria = categoria;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Requerimento getRequerimento() {
        return requerimento;
    }

    public void setRequerimento(Requerimento requerimento) {
        this.requerimento = requerimento;
    }

    public Requerimento getRequerimentoSelecionado() {
        return requerimentoSelecionado;
    }

    public void setRequerimentoSelecionado(Requerimento requerimentoSelecionado) {
        this.requerimentoSelecionado = requerimentoSelecionado;
    }

    public List<MovimentacaoRequerimento> getListaRequerimentoSelecionado() {
        return listaRequerimentoSelecionado;
    }

    public void setListaRequerimentoSelecionado(List<MovimentacaoRequerimento> listaRequerimentoSelecionado) {
        this.listaRequerimentoSelecionado = listaRequerimentoSelecionado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Embarcacao_Loja getEmbarcacao_Loja() {
        return embarcacao_Loja;
    }

    public void setEmbarcacao_Loja(Embarcacao_Loja embarcacao_Loja) {
        this.embarcacao_Loja = embarcacao_Loja;
    }

    public List<Campos> getNatureza() {
        return natureza;
    }

    public void setNatureza(List<Campos> natureza) {
        this.natureza = natureza;
    }

    public Requerimento getEditaRequerimento() {
        return editaRequerimento;
    }

    public void setEditaRequerimento(Requerimento editaRequerimento) {
        this.editaRequerimento = editaRequerimento;
    }

    public MovimentacaoRequerimento getMovimentacaoRequerimento() {
        return movimentacaoRequerimento;
    }

    public void setMovimentacaoRequerimento(MovimentacaoRequerimento movimentacaoRequerimento) {
        this.movimentacaoRequerimento = movimentacaoRequerimento;
    }

    public MovimentacaoRequerimento getMovimentarRequerimento() {
        return movimentarRequerimento;
    }

    public void setMovimentarRequerimento(MovimentacaoRequerimento movimentarRequerimento) {
        this.movimentarRequerimento = movimentarRequerimento;
    }

    public boolean isOpcaoTecnico() {
        return opcaoTecnico;
    }

    public void setOpcaoTecnico(boolean opcaoTecnico) {
        this.opcaoTecnico = opcaoTecnico;
    }

    public Requerimento getFinalizarRequerimento() {
        return finalizarRequerimento;
    }

    public void setFinalizarRequerimento(Requerimento finalizarRequerimento) {
        this.finalizarRequerimento = finalizarRequerimento;
    }

    public List<MovimentacaoRequerimento> getListaMovimentos() {
        return listaMovimentos;
    }

    public void setListaMovimentos(List<MovimentacaoRequerimento> listaMovimentos) {
        this.listaMovimentos = listaMovimentos;
    }

    public List<Embarcacao_Loja> getEmbarcacoes_lojas() {
        return embarcacoes_lojas;
    }

    public void setEmbarcacoes_lojas(List<Embarcacao_Loja> embarcacoes_lojas) {
        this.embarcacoes_lojas = embarcacoes_lojas;
    }

    public List<Local_Requerimento> getLocal() {
        return local;
    }

    public void setLocal(List<Local_Requerimento> local) {
        this.local = local;
    }

    public BufferedImage getImagem() {
        return imagem;
    }

    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getTamanhoHist() {
        return tamanhoHist;
    }

    public void setTamanhoHist(int tamanhoHist) {
        this.tamanhoHist = tamanhoHist;
    }

    public int getTamanhoMov() {
        return tamanhoMov;
    }

    public void setTamanhoMov(int tamanhoMov) {
        this.tamanhoMov = tamanhoMov;
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public Local_Requerimento getCadlocal() {
        return cadlocal;
    }

    public void setCadlocal(Local_Requerimento cadlocal) {
        this.cadlocal = cadlocal;
    }

    public Campos getCadNatureza() {
        return cadNatureza;
    }

    public void setCadNatureza(Campos cadNatureza) {
        this.cadNatureza = cadNatureza;
    }

    public Requerente getCadRequerente() {
        return cadRequerente;
    }

    public void setCadRequerente(Requerente cadRequerente) {
        this.cadRequerente = cadRequerente;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public boolean isCampooTabela() {
        return campooTabela;
    }

    public void setCampooTabela(boolean campooTabela) {
        this.campooTabela = campooTabela;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public boolean isRendCli() {
        return rendCli;
    }

    public void setRendCli(boolean rendCli) {
        this.rendCli = rendCli;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public List<Usuario> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<Usuario> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public Usuario getMovimentarTecnico() {
        return movimentarTecnico;
    }

    public void setMovimentarTecnico(Usuario movimentarTecnico) {
        this.movimentarTecnico = movimentarTecnico;
    }

    public String getTextoArquivo() {
        return textoArquivo;
    }

    public void setTextoArquivo(String textoArquivo) {
        this.textoArquivo = textoArquivo;
    }

    public String getTextoArquivoMov() {
        return textoArquivoMov;
    }

    public void setTextoArquivoMov(String textoArquivoMov) {
        this.textoArquivoMov = textoArquivoMov;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
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

    public MovimentacaoRequerimento getRequerimentoSelecionadoMov() {
        return requerimentoSelecionadoMov;
    }

    public void setRequerimentoSelecionadoMov(MovimentacaoRequerimento requerimentoSelecionadoMov) {
        this.requerimentoSelecionadoMov = requerimentoSelecionadoMov;
    }

}
