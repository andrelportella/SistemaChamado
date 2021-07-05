package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.ArquivoUpload;
import Modelo.Campos;
import Modelo.Equipamento;
import Modelo.Fornecedor;
import Modelo.Parametros;
import Modelo.Software;
import Modelo.Software_Equipamento;
import Modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;
import util.Util;
import util.ValidacoesBanco;

@ManagedBean
@ViewScoped
public class EquipamentoBean implements Serializable {

    private Connection ConexaoSQL;
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private boolean renderizador = false;
    private Fornecedor fornecedor = new Fornecedor();
    private Equipamento equipamentoGeral = new Equipamento();
    private Equipamento equipamentoInfor = new Equipamento();
    private Equipamento equipamentoOperacao = new Equipamento();
    private Equipamento selecEquipGeral = new Equipamento();
    private Equipamento selecEquipInfor = new Equipamento();
    private Equipamento selecEquipOperacao = new Equipamento();
    private Equipamento editaEquipGeral = new Equipamento();
    private Equipamento editaEquipInfor = new Equipamento();
    private Equipamento editaEquipOperacao = new Equipamento();
    private List<Equipamento> equipamentosGeral = new ArrayList<>();
    private List<Equipamento> equipamentosInfor = new ArrayList<>();
    private List<Equipamento> equipamentosOperacao = new ArrayList<>();
    private StreamedContent file;
    Util util = new Util();
    private Campos camposSite = new Campos();
    private Campos camposTipoEquip = new Campos();
    private Campos camposStatus = new Campos();
    private Campos camposSetor = new Campos();
    private Campos camposMarca = new Campos();
    private Fornecedor cadfornecedor = new Fornecedor();
    private List<Campos> HD = new ArrayList<>();
    private List<Campos> site = new ArrayList<>();
    private List<Campos> sisOp = new ArrayList<>();
    private List<Campos> marca = new ArrayList<>();
    private List<Campos> setor = new ArrayList<>();
    private List<Campos> status = new ArrayList<>();
    private List<Campos> memoria = new ArrayList<>();
    private List<Campos> tipoMaq = new ArrayList<>();
    private List<Campos> placaMae = new ArrayList<>();
    private List<Campos> processador = new ArrayList<>();
    private List<Campos> empresa = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Fornecedor> fornecedores = new ArrayList<>();
    private List<Software> softwaresEmUso = new ArrayList<>();
    private List<Software> softwaresNaoSelecionado = new ArrayList<>();
    private DualListModel<Software> dualListModelSoftwares = new DualListModel<>(softwaresNaoSelecionado, softwaresEmUso);
    private ArquivoUpload a = new ArquivoUpload();
    private UploadedFile uploadedFile;
    private StreamedContent downloadFile;
    private Parametros parametros = new Parametros();
    private String textoArquivoInfor = "";
    private String textoArquivoGeral = "";
    private List<Boolean> list;
    private List<Boolean> listGeral;
    private DualListModel<Software> listModel;
    Long cod_equipamento;
    List<String> removerSoftwares = new ArrayList<>();
    private boolean statusBotao;
    Long cod_user = (Long) session.getAttribute("idUser");
    Date data = new Date(System.currentTimeMillis());
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();
    private boolean bens;

    public EquipamentoBean() throws IOException {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        ListaEquipamentosGeral();
        ListaEquipamentosInfor();
        metodoCampo();
        construtorXML();

        list = Arrays.asList(
                //10
                true, true, true, true, true, false, false, false, false, false,
                //20
                false, false, false, false, false, false, false, false, false, false,
                //30
                false, false, false, false, false, false, false, false);

        listGeral = Arrays.asList(
                //10
                true, true, true, true, true, false, false, false, false, false,
                //20
                false, false, false, false, false, false, false, false, false, false,
                //30
                false, false, false, false, false, false, false, false, false, false,
                //40
                false);

        listModel = new DualListModel<>(new ArrayList<>(softwaresNaoSelecionado), softwaresEmUso);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggleGeral(ToggleEvent e) {
        listGeral.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public Long verificaCodigo(Campos campos) {
        Long retorno = 0L;
        try {

            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = "SELECT max(cod) FROM campos WHERE tabela=? and cod_negocio = " + cod_negocio + "";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setLong(1, campos.getTabela());
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getLong(1);
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return retorno;
            } else {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return retorno;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "CamposBean", "verificaCodigo");
            System.out.println("public Long verificaCodigo(Campos campos) Erro:" + e);
            return 999999L;
        }

    }

    public void atualizaCampos() throws IOException {
        if (Boolean.parseBoolean(session.getAttribute("cadCampos").toString()) == true) {
            if (camposSite.getDescricao() != null) {
                if (!verificaAcesso(camposSite)) {
                    camposSite.setCod(verificaCodigo(camposSite) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposSite);
                    camposSite = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarSiteDialog').hide();");
                    site = new GenericDAO<>(Campos.class).listarCamposJDBC(2L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }
            } else if (camposSetor.getDescricao() != null) {
                if (!verificaAcesso(camposSetor)) {
                    camposSetor.setCod(verificaCodigo(camposSetor) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposSetor);
                    camposSetor = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarSetorDialog').hide();");
                    setor = new GenericDAO<>(Campos.class).listarCamposJDBC(4L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }

            } else if (camposMarca.getDescricao() != null) {
                if (!verificaAcesso(camposMarca)) {
                    camposMarca.setCod(verificaCodigo(camposMarca) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposMarca);
                    camposMarca = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarMarcaDialog').hide();");
                    marca = new GenericDAO<>(Campos.class).listarCamposJDBC(13L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }

            } else if (camposStatus.getDescricao() != null) {
                if (!verificaAcesso(camposStatus)) {
                    camposStatus.setCod(verificaCodigo(camposStatus) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposStatus);
                    camposStatus = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarStatusDialog').hide();");
                    status = new GenericDAO<>(Campos.class).listarCamposJDBC(8L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }

            } else if (camposTipoEquip.getDescricao() != null) {
                if (!verificaAcesso(camposTipoEquip)) {
                    camposTipoEquip.setCod(verificaCodigo(camposTipoEquip) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposTipoEquip);
                    camposTipoEquip = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarTipoEquipDialog').hide();");
                    tipoMaq = new GenericDAO<>(Campos.class).listarCamposJDBC(9L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }
            }
        } else {
            FacesUtil.addMsgError("Erro", "Você não tem permissão para cadastrar esse tipo de Item!");
        }
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public final void metodoCampo() {
        if (cod_negocio == 1L) {
            renderizador = true;
        }
        if (cod_negocio == 5L) {
            renderizador = true;
        }
        if (cod_negocio == 4L) {
            bens = true;
        }

    }

    public void construtorXML() {
        marca = new GenericDAO<>(Campos.class).listarCamposJDBC(13L);
        placaMae = new GenericDAO<>(Campos.class).listarCamposJDBC(10L);
        tipoMaq = new GenericDAO<>(Campos.class).listarCamposJDBC(9L);
        status = new GenericDAO<>(Campos.class).listarCamposJDBC(8L);
        setor = new GenericDAO<>(Campos.class).listarCamposJDBC(4L);
        site = new GenericDAO<>(Campos.class).listarCamposJDBC(2L);
        HD = new GenericDAO<>(Campos.class).listarCamposJDBC(5L);
        memoria = new GenericDAO<>(Campos.class).listarCamposJDBC(6L);
        sisOp = new GenericDAO<>(Campos.class).listarCamposJDBC(7L);
        processador = new GenericDAO<>(Campos.class).listarCamposJDBC(11L);
        usuarios = new GenericDAO<>(Usuario.class).listarUsuarios();
        fornecedores = new GenericDAO<>(Fornecedor.class).listarFornecedores();
        empresa = new GenericDAO<>(Campos.class).listarCamposJDBC(16L);
    }

    public void ListaEquipamentosGeral() {
        try {
            List<Equipamento> equips = new ArrayList();
            sql = "	SELECT setor.descricao as Setor, site.descricao AS Site, hd.descricao as HD , memoria.descricao AS MEMORIA ,sisOp.descricao AS Sistema_Operacional, "
                    + " status.descricao as Status ,tipoMaq.descricao as Tipo_Maquina , processador.descricao as Processador , fornecedor.nome as Fornecedor, "
                    + " marca.descricao as Marca, usuario.nome as usuario, usuario.id as tid ,fornecedor.id as tid2, empresa.descricao as empresa, e.* "
                    + " FROM Equipamento e "
                    + " LEFT join campos site on e.cod_site = site.cod and site.tabela = 2 and site.cod_negocio = " + cod_negocio + " "
                    + " LEFT join campos setor on e.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos HD on e.cod_tamHD = HD.cod and HD.tabela = 5 and HD.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos memoria on e.cod_mem = memoria.cod and memoria.tabela = 6 and memoria.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos sisOp on e.cod_sOp = sisOp.cod and sisOp.tabela = 7 and sisOp.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos status on e.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos tipoMaq on e.cod_tipoMaq = tipoMaq.cod and tipoMaq.tabela = 9 and tipoMaq.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos processador on e.cod_Processador = processador.cod and processador.tabela = 11 and processador.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos empresa on e.cod_Processador = empresa.cod and empresa.tabela = 16 and empresa.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Fornecedor fornecedor on e.id_Fornecedor = fornecedor.id and fornecedor.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos marca on e.cod_marca = marca.cod and marca.tabela = 13 and marca.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario usuario on e.cod_usrResp = usuario.id and usuario.cod_negocio = " + cod_negocio + " "
                    + " where e.cod_tipoEquipamento = 1 and e.cod_negocio =" + cod_negocio + " "
                    + " ORDER BY e.nome ";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Equipamento e = new Equipamento();
            Fornecedor f = new Fornecedor();
            while (rs.next()) {
                e.setSetor(rs.getString(1));
                e.setSite(rs.getString(2));
                e.setHD(rs.getString(3));
                e.setMemoria(rs.getString(4));
                e.setSistemaOperacional(rs.getString(5));
                e.setStatus(rs.getString(6));
                e.setTipoMaq(rs.getString(7));
                e.setProcessador(rs.getString(8));
                e.setFornecedor(rs.getString(9));
                e.setMarca(rs.getString(10));
                e.setUsuario(rs.getString(11));
                e.setCod_usrResp(rs.getLong(12));
                f.setId(rs.getLong(13));
                e.setCod_empresa(rs.getLong(14));
                e.setId_fornecedor(f);
                e.setModelo(rs.getString("modelo"));
                e.setPeso(rs.getString("peso"));
                e.setNumSerie(rs.getString("numSerie"));
                e.setNfSoftware(rs.getString("NfSoftware"));
                e.setDtCompra(rs.getDate("dtCompra"));
                e.setNome(rs.getString("nome"));
                e.setCapacidade(rs.getString("Capacidade"));
                e.setIp(rs.getString("ip"));
                e.setRevisadoRenato(rs.getBoolean("revisadoRenato"));
                e.setTagLicenca(rs.getString("tagLicenca"));
                e.setTamanho(rs.getString("tamanho"));
                e.setId(rs.getLong("id"));
                e.setCod_Processador(rs.getLong("COD_processador"));
                e.setCod_Status(rs.getLong("cod_status"));
                e.setCod_marca(rs.getLong("cod_marca"));
                e.setCod_mem(rs.getLong("cod_mem"));
                e.setCod_placaMae(rs.getLong("cod_placamae"));
                e.setCod_sOp(rs.getLong("cod_sop"));
                e.setCod_setor(rs.getLong("cod_setor"));
                e.setCod_site(rs.getLong("cod_site"));
                e.setCod_tamHD(rs.getLong("cod_tamHD"));
                e.setCod_tipoEquipamento(rs.getLong("cod_tipoEquipamento"));
                e.setCod_tipoMaq(rs.getLong("cod_tipoMaq"));
                e.setDescricao(rs.getString("descricao"));
                e.setValor(rs.getDouble("Valor"));
                e.setCod_arquivo(rs.getLong("Cod_arquivo"));
                e.setCod_empresa(rs.getLong("Cod_empresa"));
                e.setObservacao(rs.getString("observacao"));
                e.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                e.setMatriculaCartorio(rs.getString("MatriculaCartorio"));
                e.setSeguro(rs.getString("seguro"));
                e.setIncra(rs.getString("Incra"));
                e.setValorItr(rs.getDouble("ValorItr"));
                e.setValorSeguro(rs.getDouble("ValorSeguro"));
                e.setIptu(rs.getString("Iptu"));
                e.setItr(rs.getString("ITR"));
                e.setValorIptu(rs.getDouble("ValorIptu"));
                e.setValorForo(rs.getDouble("ValorForo"));
                e.setValorIncra(rs.getDouble("ValorIncra"));
                e.setRenavam(rs.getString("Renavam"));
                e.setResctricaoObs(rs.getString("ResctricaoObs"));
                e.setRestricao(rs.getBoolean("Restricao"));
                if (e.isAnexoMovimento()) {
                    e.setArquivo(aU.listar(e.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento()));
                }
                equips.add(e);
                f = new Fornecedor();
                e = new Equipamento();
            }
            equipamentosGeral = equips;
            equips = null;
            e = null;
            f = null;

            ConexaoSQL.close();
            rs.close();
            stmt.close();

            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "ListaEquipamentosGeral");
            System.out.println(" public void ListaEquipamentosGeral() Erro:" + e);
        }
    }

    public String abrirRelatorio() {
        File file;
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=boleto.pdf");
            OutputStream output = response.getOutputStream();
            response.flushBuffer();
            output.flush();
            output.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void ListaEquipamentosInfor() {
        try {
            List<Equipamento> equips = new ArrayList();
            sql = " SELECT setor.descricao as Setor, site.descricao AS Site, hd.descricao as HD , memoria.descricao AS MEMORIA ,sisOp.descricao AS Sistema_Operacional, "
                    + " status.descricao as Status ,tipoMaq.descricao as Tipo_Maquina, processador.descricao as Processador , fornecedor.nome as Fornecedor, "
                    + " marca.descricao as Marca, usuario.nome as usuario, usuario.id as tid,fornecedor.id as tid2, empresa.descricao as empresa, e.* "
                    + " FROM Equipamento e "
                    + " LEFT join campos site on e.cod_site = site.cod and site.tabela = 2 and site.cod_negocio = " + cod_negocio + " "
                    + " LEFT join campos setor on e.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos HD on e.cod_tamHD = HD.cod and HD.tabela = 5 and HD.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos memoria on e.cod_mem = memoria.cod and memoria.tabela = 6 and memoria.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos sisOp on e.cod_sOp = sisOp.cod and sisOp.tabela = 7 and sisOp.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos status on e.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos tipoMaq on e.cod_tipoMaq = tipoMaq.cod and tipoMaq.tabela = 9 and tipoMaq.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos processador on e.cod_Processador = processador.cod and processador.tabela = 11 and processador.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos empresa on e.cod_empresa = empresa.cod and empresa.tabela = 16 and empresa.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Fornecedor fornecedor on e.id_Fornecedor = fornecedor.id and fornecedor.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Campos marca on e.cod_marca = marca.cod and marca.tabela = 13 and marca.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario usuario on e.cod_usrResp = usuario.id and usuario.cod_negocio = " + cod_negocio + " "
                    + " where e.cod_tipoEquipamento = 2 and e.cod_negocio =" + cod_negocio + " "
                    + " ORDER BY  e.nome";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Equipamento e = new Equipamento();
            Fornecedor f = new Fornecedor();
            while (rs.next()) {
                e.setSetor(rs.getString(1));
                e.setSite(rs.getString(2));
                e.setHD(rs.getString(3));
                e.setMemoria(rs.getString(4));
                e.setSistemaOperacional(rs.getString(5));
                e.setStatus(rs.getString(6));
                e.setTipoMaq(rs.getString(7));
                e.setProcessador(rs.getString(8));
                e.setFornecedor(rs.getString(9));
                e.setMarca(rs.getString(10));
                e.setUsuario(rs.getString(11));
                e.setCod_usrResp(rs.getLong(12));
                f.setId(rs.getLong(13));
                e.setEmpresa(rs.getString(14));
                e.setId_fornecedor(f);
                e.setModelo(rs.getString("modelo"));
                e.setPeso(rs.getString("peso"));
                e.setNumSerie(rs.getString("numSerie"));
                e.setNfSoftware(rs.getString("NfSoftware"));
                e.setDtCompra(rs.getDate("dtCompra"));
                e.setNome(rs.getString("nome"));
                e.setCapacidade(rs.getString("Capacidade"));
                e.setIp(rs.getString("ip"));
                e.setRevisadoRenato(rs.getBoolean("revisadoRenato"));
                e.setTagLicenca(rs.getString("tagLicenca"));
                e.setTamanho(rs.getString("tamanho"));
                e.setId(rs.getLong("id"));
                e.setCod_Processador(rs.getLong("COD_processador"));
                e.setCod_Status(rs.getLong("cod_status"));
                e.setCod_marca(rs.getLong("cod_marca"));
                e.setCod_mem(rs.getLong("cod_mem"));
                e.setCod_placaMae(rs.getLong("cod_placamae"));
                e.setCod_sOp(rs.getLong("cod_sop"));
                e.setCod_setor(rs.getLong("cod_setor"));
                e.setCod_site(rs.getLong("cod_site"));
                e.setCod_tamHD(rs.getLong("cod_tamHD"));
                e.setCod_tipoEquipamento(rs.getLong("cod_tipoEquipamento"));
                e.setCod_tipoMaq(rs.getLong("cod_tipoMaq"));
                e.setDescricao(rs.getString("descricao"));
                e.setValor(rs.getDouble("VALOR"));
                e.setCod_arquivo(rs.getLong("cod_arquivo"));
                e.setCod_empresa(rs.getLong("cod_empresa"));
                e.setAnexoMovimento(rs.getBoolean("anexoMovimento"));
                e.setObservacao(rs.getString("observacao"));
                if (e.isAnexoMovimento()) {
                    e.setArquivo(aU.listar(e.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento()));
                }
                equips.add(e);
                e = new Equipamento();
                f = new Fornecedor();
            }
            equipamentosInfor = equips;
            equips = null;
            e = null;
            f = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "ListaEquipamentosInfor");
            System.out.println(" public void ListaEquipamentosInfor() Erro:" + e);
        }
    }

    public boolean verificaAcesso(Campos campos) {
        sql = "SELECT tabela FROM campos WHERE Descricao= '" + campos.getDescricao() + "' and tabela = " + campos.getTabela() + " and cod_negocio =" + cod_negocio + " ";
        return ValidacoesBanco.retornaBoolean(sql, "EquipamentoBean", "verificaAcesso");
    }

    public boolean verificaGCM(Equipamento eq) {
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = "SELECT NOME FROM equipamento WHERE nome=? and cod_negocio =" + cod_negocio + " ";
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.setString(1, eq.getNome());
            rs = stmt.executeQuery();
            if (rs.next()) {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return true;
            } else {
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "verificaGCM");
            System.out.println("public boolean verificaGCM(Equipamento e) Erro:" + e);
            return false;
        }
    }

    public String pegarCodigoEquipamento(Equipamento eq) {
        String nome = "999999";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            sql = " SELECT "
                    + " case "
                    + " WHEN SUBSTRING(MAX(nome),5,10)+1 > 1000 THEN  cast((SUBSTRING(MAX(nome),5,10)+1)AS VARCHAR) "
                    + " WHEN SUBSTRING(MAX(nome),5,10)+1 < 10 THEN '000' + cast((SUBSTRING(MAX(nome),5,10)+1) AS VARCHAR) "
                    + " WHEN SUBSTRING(MAX(nome),5,10)+1 BETWEEN 10 AND 99  THEN '00' + cast((SUBSTRING(MAX(nome),5,10)+1) AS VARCHAR) "
                    + " WHEN SUBSTRING(MAX(nome),5,10)+1 >= 100 AND SUBSTRING(MAX(nome),5,10) < 999 THEN '0' + cast((SUBSTRING(MAX(nome),5,10)+1) AS VARCHAR) "
                    + " ELSE '0001' "
                    + " end as NOVO "
                    + " FROM equipamento WHERE nome like '%" + eq.getNome() + "%' and cod_negocio = " + cod_negocio + " ";
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                nome = rs.getString(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "acompanhantes");
            System.out.println("public List<Long> acompanhantes(Long id) Erro:" + e);
        }
        return nome;
    }

    public void salvarGeral() {
        equipamentoGeral.setCod_arquivo(a.getId());
        if (a.isEnviado()) {
            equipamentoGeral.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
        }
        equipamentoGeral.setNome(equipamentoGeral.getNome() + "-" + pegarCodigoEquipamento(equipamentoGeral));
        equipamentoGeral.setCod_tipoEquipamento(1L);
        equipamentoGeral.setId_fornecedor(fornecedor);
        String toUpperCase = equipamentoGeral.getNome().toUpperCase();
        equipamentoGeral.setNome(toUpperCase);
        new GenericDAO<>(Equipamento.class).salvar(equipamentoGeral);
        ListaEquipamentosGeral();
        equipamentoGeral = new Equipamento();
        fornecedor = new Fornecedor();
        a = new ArquivoUpload();
        textoArquivoGeral = "";
        FacesUtil.addMsgInfo("Sucesso!", "Salvo com Sucesso!");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialogGeral').hide();");

    }

    public void salvarInformatica() {
        equipamentoInfor.setCod_arquivo(a.getId());
        if (a.isEnviado()) {
            equipamentoInfor.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
        }
        equipamentoInfor.setNome(equipamentoInfor.getNome() + "-" + pegarCodigoEquipamento(equipamentoInfor));
        equipamentoInfor.setCod_tipoEquipamento(2L);
        equipamentoInfor.setId_fornecedor(fornecedor);
        String toUpperCase = equipamentoInfor.getNome().toUpperCase();
        equipamentoInfor.setNome(toUpperCase);
        new GenericDAO<>(Equipamento.class).salvar(equipamentoInfor);
        ListaEquipamentosInfor();
        equipamentoInfor = new Equipamento();
        fornecedor = new Fornecedor();
        a = new ArquivoUpload();
        textoArquivoInfor = "";
        FacesUtil.addMsgInfo("Sucesso!", "Salvo com Sucesso!");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
    }

    public void editarEquipamentoInfor() {
        editaEquipInfor.setId_fornecedor(fornecedor);
        if (a.isEnviado()) {
            editaEquipInfor.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
            a = new ArquivoUpload();
        }
        new GenericDAO<>(Equipamento.class).update(editaEquipInfor);
        editaEquipInfor = new Equipamento();
        fornecedor = new Fornecedor();
        ListaEquipamentosInfor();
        FacesUtil.addMsgInfo("Sucesso!", "Editado com Sucesso!");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        textoArquivoInfor = "";
    }

    public void editarEquipamentoGeral() {
        editaEquipGeral.setId_fornecedor(fornecedor);
        if (a.isEnviado()) {
            editaEquipGeral.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
            a = new ArquivoUpload();
        }
        new GenericDAO<>(Equipamento.class).update(editaEquipGeral);
        editaEquipGeral = new Equipamento();
        fornecedor = new Fornecedor();
        ListaEquipamentosGeral();
        FacesUtil.addMsgInfo("Sucesso!", "Editado com Sucesso!");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialogGeral').hide();");
        textoArquivoGeral = "";
    }

    public void uploadGeral(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivoGeral += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento());
    }

    public void uploadInfor(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivoInfor += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento());
    }

    public void anexaArquivoGeral() throws IOException {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
    }

    public void anexaArquivoInfor() throws IOException {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
    }

    public void editUploadInfor(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivoInfor += arq.upload(event, editaEquipInfor.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento());
    }

    public void editUploadGeral(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivoGeral += arq.upload(event, editaEquipGeral.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaEquipamento());
    }

    public void deletarArquivoGeral(File file) throws IOException, ParseException {
        arq.delete(file, editaEquipGeral.getId(), "Equipamento");
        ListaEquipamentosGeral();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialogGeral').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEditInfor').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEditGeral').hide();");
    }

    public void deletarArquivoInfor(File file) throws IOException, ParseException, ClassNotFoundException, SQLException {
        arq.delete(file, editaEquipInfor.getId(), "Equipamento");
        ListaEquipamentosInfor();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialogGeral').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEditInfor').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEditGeral').hide();");
    }

    public void editarGeral(Equipamento e) {
        this.editaEquipGeral = e;
        if (editaEquipGeral.getCod_arquivo() == 0) {
            ArquivoUpload a = new ArquivoUpload();
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
            editaEquipGeral.setCod_arquivo(a.getId());
            a = new ArquivoUpload();
        }
        fornecedor.setId(e.getId_fornecedor().getId());
    }

    public void editarInfor(Equipamento e) {
        this.editaEquipInfor = e;
        if (editaEquipInfor.getCod_arquivo() == 0) {
            ArquivoUpload a = new ArquivoUpload();
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
            editaEquipInfor.setCod_arquivo(a.getId());
            a = new ArquivoUpload();
        }
        fornecedor.setId(e.getId_fornecedor().getId());
    }

    public void insereSoftwares(Equipamento e) {
        listaSoftwaresNaoSelecionado();
        listaSoftwaresEmUso(e);
        cod_equipamento = e.getId();
        listModel = new DualListModel<>(new ArrayList<>(softwaresNaoSelecionado), softwaresEmUso);
    }

    public List listaSoftwaresNaoSelecionado() {
        List<Software> softwares = new ArrayList<>();
        sql = " select empresa.descricao as empresa, tipoLicenca.descricao as tipoLicenca, s.* from software s "
                + " JOIN Campos empresa on empresa.cod = s.cod_empresa and empresa.tabela = 16 "
                + " JOIN Campos tipoLicenca on tipoLicenca.cod = s.cod_tipolicenca and tipoLicenca.tabela = 26 "
                + " where empresa.cod_negocio = " + cod_negocio + " and tipoLicenca.cod_negocio = " + cod_negocio + " and s.cod_negocio = " + cod_negocio + " "
                + " and s.id not in ( SELECT se.cod_software FROM Software_Equipamento se where status = 1) and s.status = 1 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Software s = new Software();
            while (rs.next()) {
                s.setId(rs.getLong("id"));
                s.setCod_negocio(rs.getLong("cod_negocio"));
                s.setChave(rs.getString("chave"));
                s.setEmpresa(rs.getString("empresa"));
                s.setTipoLicenca(rs.getString("tipoLicenca"));
                s.setCod_empresa(rs.getLong("cod_empresa"));
                s.setCod_tipoLicenca(rs.getLong("cod_tipoLicenca"));
                s.setDataAquisicao(rs.getDate("dataAquisicao"));
                s.setDescricao(rs.getString("descricao"));
                s.setNumNota(rs.getString("numNota"));
                s.setValidade(rs.getDate("validade"));
                s.setNome(rs.getString("nome"));
                softwares.add(s);
                s = new Software();
            }
            //softwares = null;
            s = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "listaSoftwaresNaoSelecionado");
            System.out.println(" public List listaSoftwaresNaoSelecionado() Erro:" + e);
        }

        return softwaresNaoSelecionado = softwares;
    }

    public List listaSoftwaresEmUso(Equipamento e) {
        List<Software> softwares = new ArrayList<>();
        sql = " select empresa.descricao as empresa, tipoLicenca.descricao as tipoLicenca, s.* from software s "
                + " JOIN Campos empresa on empresa.cod = s.cod_empresa and empresa.tabela = 16 "
                + " JOIN Campos tipoLicenca on tipoLicenca.cod = s.cod_tipolicenca and tipoLicenca.tabela = 26 "
                + " JOIN Software_Equipamento se on se.cod_software = s.id "
                + " JOIN Equipamento e on e.id = se.cod_equipamento "
                + " where empresa.cod_negocio = " + cod_negocio + " and tipoLicenca.cod_negocio = " + cod_negocio + " and s.cod_negocio = " + cod_negocio + " and e.id = " + e.getId() + " and se.status = 1 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Software s = new Software();
            while (rs.next()) {
                s.setId(rs.getLong("id"));
                s.setCod_negocio(rs.getLong("cod_negocio"));
                s.setChave(rs.getString("chave"));
                s.setEmpresa(rs.getString("empresa"));
                s.setTipoLicenca(rs.getString("tipoLicenca"));
                s.setCod_empresa(rs.getLong("cod_empresa"));
                s.setCod_tipoLicenca(rs.getLong("cod_tipoLicenca"));
                s.setDataAquisicao(rs.getDate("dataAquisicao"));
                s.setDescricao(rs.getString("descricao"));
                s.setNumNota(rs.getString("numNota"));
                s.setValidade(rs.getDate("validade"));
                s.setNome(rs.getString("nome"));
                softwares.add(s);
                s = new Software();
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro", "EquipamentoBean", "listaSoftwaresEmUso");
            System.out.println(" public List listaSoftwaresEmUso(Equipamento e) Erro:" + ex);
        }
        return softwaresEmUso = softwares;
    }

    public void onTransfer(TransferEvent event) {
        if (event.isAdd()) {
            for (Object item : event.getItems()) {
                if (removerSoftwares.contains(item.toString())) {
                    removerSoftwares.remove(item.toString());
                }
            }
        } else if (event.isRemove()) {
            for (Object item : event.getItems()) {
                removerSoftwares.add(item.toString());
                //System.out.println("Removou o item" + item.toString());
            }
        }
    }

    public void salvarSoftware() {
        Software_Equipamento se = new Software_Equipamento();
        for (Object s : listModel.getTarget()) {

            se.setCod_equipamento(cod_equipamento);
            se.setCod_software(Long.parseLong(s.toString()));
            se.setStatus(true);
            se.setCod_userInseriu(cod_user);
            se.setDataInclusao(data);
            if (!verificarSoftware(se)) {
                new GenericDAO<>(Software_Equipamento.class).salvar(se);
            }
            se = new Software_Equipamento();
        }
        se = null;
        for (Object item : removerSoftwares) {
            excluirSoftware(Long.parseLong(item.toString()));
        }
        removerSoftwares = new ArrayList<>();
        FacesUtil.addMsgInfo("Salvo com Sucesso", mSucesso);
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('softwareDialog').hide();");
    }

    public String getFormatData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.format(this.data);
    }

    public void excluirSoftware(Long cod) {
        sql = " update Software_Equipamento set status = 0, cod_userRemoveu =" + cod_user + ", dataRemocao='" + getFormatData() + "' where cod_software =" + cod + " and status = 1 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "excluirSoftware");
            System.out.println(" public void excluirSoftware(Long cod) Erro:" + e);
        }
    }

    public boolean verificarSoftware(Software_Equipamento se) {
        boolean b = false;
        sql = " SELECT se.* FROM Software_Equipamento se "
                + " JOIN software s on s.id = se.cod_software "
                + " JOIN Equipamento e on e.id = se.cod_equipamento "
                + " where e.cod_negocio = " + cod_negocio + " and s.cod_negocio =  " + cod_negocio + " "
                + " and se.cod_equipamento = " + se.getCod_equipamento() + " "
                + " and se.cod_software = " + se.getCod_software() + " and se.status=1 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                b = true;
            } else {
                b = false;
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "EquipamentoBean", "verificarSoftware");
            System.out.println(" public boolean verificarSoftware(Software_Equipamento se) Erro:" + e);
        }
        return b;
    }

    public void editarOperacao(Equipamento e) {
        this.editaEquipOperacao = e;
        fornecedor.setId(e.getId_fornecedor().getId());
    }

    public List<Campos> getHD() {
        return HD;
    }

    public void setHD(List<Campos> HD) {
        this.HD = HD;
    }

    public List<Campos> getSite() {
        return site;
    }

    public void setSite(List<Campos> site) {
        this.site = site;
    }

    public List<Campos> getSisOp() {
        return sisOp;
    }

    public void setSisOp(List<Campos> sisOp) {
        this.sisOp = sisOp;
    }

    public List<Campos> getMarca() {
        return marca;
    }

    public void setMarca(List<Campos> marca) {
        this.marca = marca;
    }

    public List<Campos> getSetor() {
        return setor;
    }

    public void setSetor(List<Campos> setor) {
        this.setor = setor;
    }

    public List<Campos> getStatus() {
        return status;
    }

    public void setStatus(List<Campos> status) {
        this.status = status;
    }

    public List<Campos> getMemoria() {
        return memoria;
    }

    public void setMemoria(List<Campos> memoria) {
        this.memoria = memoria;
    }

    public List<Campos> getTipoMaq() {
        return tipoMaq;
    }

    public void setTipoMaq(List<Campos> tipoMaq) {
        this.tipoMaq = tipoMaq;
    }

    public List<Campos> getPlacaMae() {
        return placaMae;
    }

    public void setPlacaMae(List<Campos> placaMae) {
        this.placaMae = placaMae;
    }

    public List<Campos> getProcessador() {
        return processador;
    }

    public void setProcessador(List<Campos> processador) {
        this.processador = processador;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Equipamento> getEquipamentosGeral() {
        return equipamentosGeral;
    }

    public void setEquipamentosGeral(List<Equipamento> equipamentosGeral) {
        this.equipamentosGeral = equipamentosGeral;
    }

    public List<Equipamento> getEquipamentosInfor() {
        return equipamentosInfor;
    }

    public void setEquipamentosInfor(List<Equipamento> equipamentosInfor) {
        this.equipamentosInfor = equipamentosInfor;
    }

    public List<Equipamento> getEquipamentosOperacao() {
        return equipamentosOperacao;
    }

    public void setEquipamentosOperacao(List<Equipamento> equipamentosOperacao) {
        this.equipamentosOperacao = equipamentosOperacao;
    }

    public Equipamento getEquipamentoGeral() {
        return equipamentoGeral;
    }

    public void setEquipamentoGeral(Equipamento equipamentoGeral) {
        this.equipamentoGeral = equipamentoGeral;
    }

    public Equipamento getEquipamentoInfor() {
        return equipamentoInfor;
    }

    public void setEquipamentoInfor(Equipamento equipamentoInfor) {
        this.equipamentoInfor = equipamentoInfor;
    }

    public Equipamento getEquipamentoOperacao() {
        return equipamentoOperacao;
    }

    public void setEquipamentoOperacao(Equipamento equipamentoOperacao) {
        this.equipamentoOperacao = equipamentoOperacao;
    }

    public Equipamento getSelecEquipGeral() {
        return selecEquipGeral;
    }

    public void setSelecEquipGeral(Equipamento selecEquipGeral) {
        this.selecEquipGeral = selecEquipGeral;
    }

    public Equipamento getSelecEquipInfor() {
        return selecEquipInfor;
    }

    public void setSelecEquipInfor(Equipamento selecEquipInfor) {
        this.selecEquipInfor = selecEquipInfor;
    }

    public Equipamento getSelecEquipOperacao() {
        return selecEquipOperacao;
    }

    public void setSelecEquipOperacao(Equipamento selecEquipOperacao) {
        this.selecEquipOperacao = selecEquipOperacao;
    }

    public Equipamento getEditaEquipGeral() {
        return editaEquipGeral;
    }

    public void setEditaEquipGeral(Equipamento editaEquipGeral) {
        this.editaEquipGeral = editaEquipGeral;
    }

    public Equipamento getEditaEquipInfor() {
        return editaEquipInfor;
    }

    public void setEditaEquipInfor(Equipamento editaEquipInfor) {
        this.editaEquipInfor = editaEquipInfor;
    }

    public Equipamento getEditaEquipOperacao() {
        return editaEquipOperacao;
    }

    public void setEditaEquipOperacao(Equipamento editaEquipOperacao) {
        this.editaEquipOperacao = editaEquipOperacao;
    }

    public List<Fornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Fornecedor> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public boolean isRenderizador() {
        return renderizador;
    }

    public void setRenderizador(boolean renderizador) {
        this.renderizador = renderizador;
    }

    public Fornecedor getCadfornecedor() {
        return cadfornecedor;
    }

    public void setCadfornecedor(Fornecedor cadfornecedor) {
        this.cadfornecedor = cadfornecedor;
    }

    public Campos getCamposSite() {
        return camposSite;
    }

    public void setCamposSite(Campos camposSite) {
        this.camposSite = camposSite;
    }

    public Campos getCamposTipoEquip() {
        return camposTipoEquip;
    }

    public void setCamposTipoEquip(Campos camposTipoEquip) {
        this.camposTipoEquip = camposTipoEquip;
    }

    public Campos getCamposStatus() {
        return camposStatus;
    }

    public void setCamposStatus(Campos camposStatus) {
        this.camposStatus = camposStatus;
    }

    public Campos getCamposSetor() {
        return camposSetor;
    }

    public void setCamposSetor(Campos camposSetor) {
        this.camposSetor = camposSetor;
    }

    public Campos getCamposMarca() {
        return camposMarca;
    }

    public void setCamposMarca(Campos camposMarca) {
        this.camposMarca = camposMarca;
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public List<Boolean> getListGeral() {
        return listGeral;
    }

    public void setListGeral(List<Boolean> listGeral) {
        this.listGeral = listGeral;
    }

    public ArquivoUpload getA() {
        return a;
    }

    public void setA(ArquivoUpload a) {
        this.a = a;
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

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }

    public List<Campos> getEmpresa() {
        return empresa;
    }

    public void setEmpresa(List<Campos> empresa) {
        this.empresa = empresa;
    }

    public String getTextoArquivoInfor() {
        return textoArquivoInfor;
    }

    public void setTextoArquivoInfor(String textoArquivoInfor) {
        this.textoArquivoInfor = textoArquivoInfor;
    }

    public String getTextoArquivoGeral() {
        return textoArquivoGeral;
    }

    public void setTextoArquivoGeral(String textoArquivoGeral) {
        this.textoArquivoGeral = textoArquivoGeral;
    }

    public DualListModel<Software> getDualListModelSoftwares() {
        return dualListModelSoftwares;
    }

    public void setDualListModelSoftwares(DualListModel<Software> dualListModelSoftwares) {
        this.dualListModelSoftwares = dualListModelSoftwares;
    }

    public List<Software> getSoftwaresEmUso() {
        return softwaresEmUso;
    }

    public void setSoftwaresEmUso(List<Software> softwaresEmUso) {
        this.softwaresEmUso = softwaresEmUso;
    }

    public List<Software> getSoftwaresNaoSelecionado() {
        return softwaresNaoSelecionado;
    }

    public void setSoftwaresNaoSelecionado(List<Software> softwaresNaoSelecionado) {
        this.softwaresNaoSelecionado = softwaresNaoSelecionado;
    }

    public DualListModel<Software> getListModel() {
        return listModel;
    }

    public void setListModel(DualListModel<Software> listModel) {
        this.listModel = listModel;
    }

    public boolean isBens() {
        return bens;
    }

    public void setBens(boolean bens) {
        this.bens = bens;
    }
}
