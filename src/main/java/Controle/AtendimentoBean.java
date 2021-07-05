package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.ArquivoUpload;
import Modelo.Campos;
import Modelo.Equipamento;
import Modelo.Atendimento;
import Modelo.MovimentacaoAtendimento;
import Modelo.MovimentacaoProduto;
import Modelo.Parametros;
import Modelo.Produto;
import Modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

@ViewScoped
@ManagedBean
public class AtendimentoBean implements Serializable {

    private Atendimento atendimento = new Atendimento();
    private Atendimento editaAtendimento = new Atendimento();
    private List<Atendimento> atendimentos = new ArrayList<>();
    private List<Equipamento> equipamentos = new ArrayList<>();
    private Campos camposDestino = new Campos();
    private Campos camposBairro = new Campos();
    private List<Produto> produtos = new ArrayList<>();
    private List<Usuario> tecnicos = new ArrayList<>();
    private List<Campos> tipo = new ArrayList<>();
    private List<Campos> tipoAdm = new ArrayList<>();
    private List<Campos> status = new ArrayList<>();
    private List<Campos> categoria = new ArrayList<>();
    private List<Campos> destino = new ArrayList<>();
    private List<Campos> bairro = new ArrayList<>();
    private List<Campos> posicaoProcesso = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private Atendimento atendimentoSelecionado = new Atendimento();
    private MovimentacaoAtendimento atendimentoSelecionadoMov = new MovimentacaoAtendimento();
    private MovimentacaoAtendimento movimentacaoAtendimento = new MovimentacaoAtendimento();
    private MovimentacaoAtendimento movimentarAtendimento = new MovimentacaoAtendimento();
    MovimentacaoProduto mp = new MovimentacaoProduto();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String sql2;
    PreparedStatement stmt2;
    ResultSet rs2;
    private Connection ConexaoSQL;
    Util util = new Util();
    private Usuario usuario = new Usuario();
    private boolean statusBotao = false;
    private List<MovimentacaoAtendimento> listaMovimentos = new ArrayList<>();
    private List<MovimentacaoAtendimento> listaMovimentosSelecionado = new ArrayList<>();
    private MovimentacaoAtendimento movimentarAtendimentoSelecionado = new MovimentacaoAtendimento();
    private Atendimento finalizarAtendimento = new Atendimento();
    private Usuario tecnico = new Usuario();
    private int tamanho;
    private int tamanhoAberto;
    private int tamanhoFinalizados;
    private boolean opcaoTecnico;
    Date data = new Date(System.currentTimeMillis());
    private Usuario movimentarTecnico = new Usuario();
    LoginBean login = new LoginBean();
    private Part file;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private boolean campooTabela;
    private String nomeCampo = "Mostrar Todos as Tarefas";
    private String nomeTabela = "Tarefas em Aberto Para Você";
    private Long cod_tecnico;
    ArquivoUpload a = new ArquivoUpload();
    ArquivoUpload aMov = new ArquivoUpload();
    private UploadedFile uploadedFile;
    private StreamedContent downloadFile;
    private boolean ativaSuprimento = false;
    private boolean ativaPreventiva = false;
    private boolean ativaLocal_Usuario = false;
    private boolean ativaDestino = false;
    private boolean ativaEquipamento = false;
    Parametros parametros = new Parametros();
    boolean carregarCombos;
    private String textoArquivo = "";
    private String textoArquivoMov = "";
    private List<Boolean> list;
    ModeloEmail modeloEmail = new ModeloEmail();
    Long cod_user = (Long) session.getAttribute("idUser");
    List<Long> apagarLista = new ArrayList<>();
    AtendimentoDAO aDAO = new AtendimentoDAO();
    int opcao;
    private boolean agendamento;
    private boolean juridico;
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();

    public AtendimentoBean() {
        opcao = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tipo"));
        agendamento = opcao == 2;
        campooTabela = false;
        parametros = new GenericDAO<>(Parametros.class).parametro();
        tecnico.setId((Long) session.getAttribute("idUser"));
        tecnico.setLogin(session.getAttribute("userName").toString());
        usuario.setLogin(session.getAttribute("userName").toString());
        movimentarAtendimento.setDataMovimento(data);
        movimentarAtendimento.setHoraMovimento(data);
        editaAtendimento.setDataAbertura(data);
        editaAtendimento.setHoraAbertura(data);
        atendimento.setStatusAtendimento(true);
        atendimento.setId_tecnico(tecnico);
        atendimento.setId_solicitante(usuario);
        atendimento.setQtdDiasAgendamento(parametros.getQtdDiasAgendamento());
        movimentarAtendimento.setStatusMovimento(true);
        movimentarAtendimento.setEnviaSolic(parametros.isPadraoEmailTarefa());
        listarAtendimento();
        totalAtendimento();
        totalAtendimento(1);
        totalAtendimento(0);
        renderizaTela();
        suprimentoMenu();
        list = aDAO.onToggle();
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public boolean verificaAcesso(Campos campos) {
        sql = "SELECT tabela FROM campos WHERE Descricao= '" + campos.getDescricao() + "' and tabela = " + campos.getTabela() + " and cod_negocio =" + cod_negocio + " ";
        return ValidacoesBanco.retornaBoolean(sql, "EquipamentoBean", "verificaAcesso");
    }

    public void atualizaCampos() throws IOException {
        if (Boolean.parseBoolean(session.getAttribute("cadCampos").toString()) == true) {
            if (camposDestino.getDescricao() != null) {
                if (!verificaAcesso(camposDestino)) {
                    camposDestino.setCod(verificaCodigo(camposDestino) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposDestino);
                    camposDestino = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarSiteDialog').hide();");
                    destino = new GenericDAO<>(Campos.class).listarCamposJDBC(24L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }
            } else if (camposBairro.getDescricao() != null) {
                if (!verificaAcesso(camposBairro)) {
                    camposBairro.setCod(verificaCodigo(camposBairro) + 1L);
                    new GenericDAO<>(Campos.class).salvar(camposBairro);
                    camposBairro = new Campos();
                    FacesUtil.addMsgInfo("Novo campo cadastrado com sucesso!!", "Sucesso!");
                    //org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarSiteDialog').hide();");
                    bairro = new GenericDAO<>(Campos.class).listarCamposJDBC(25L);
                } else {
                    FacesUtil.addMsgError("Descrição Já Existe!", "Erro");
                }
            }

        } else {
            FacesUtil.addMsgError("Erro", "Você não tem permissão para cadastrar esse tipo de Item!");
        }
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

    public void renderizaTela() {
        switch (opcao) {
            case 1:
                atendimento.setCod_tipoAtendimento(parametros.getCodAtividadeAdm());
                break;
            case 2:
                atendimento.setCod_tipoAtendimento(parametros.getCodPreventiva());
                break;
            case 3:
                atendimento.setCod_tipoAtendimento(parametros.getCodJuridico());
                break;
            default:
                atendimento.setCod_tipoAtendimento(1L);
                break;
        }
    }

    public void carregaLista() {
        atendimento.setDataAbertura(data);
        atendimento.setHoraAbertura(data);
        if (!carregarCombos) {
            carregarCombos = true;
            status = new GenericDAO<>(Campos.class).listarCamposJDBC(8L);
            categoria = new GenericDAO<>(Campos.class).listarCamposJDBC(14L);
            switch (opcao) {
                case 0:
                    tipo = new GenericDAO<>(Campos.class).listarCamposJDBC(15L);
                    break;
                case 1:
                    tipo = new GenericDAO<>(Campos.class).listarCamposJDBCParameter(15L, parametros.getCodAtividadeAdm());
                    break;
                case 2:
                    tipo = new GenericDAO<>(Campos.class).listarCamposJDBCParameter(15L, parametros.getCodPreventiva());
                    break;
                case 3:
                    tipo = new GenericDAO<>(Campos.class).listarCamposJDBCParameter(15L, parametros.getCodJuridico());
                    break;
                default:
                    break;
            }
            destino = new GenericDAO<>(Campos.class).listarCamposJDBC(24L);
            bairro = new GenericDAO<>(Campos.class).listarCamposJDBC(25L);
            produtos = new GenericDAO<>(Campos.class).listarProdutos();
            usuarios = new GenericDAO<>(Usuario.class).listarUsuarios();
            equipamentos = new GenericDAO<>(Usuario.class).listarEquipamentos();
            tecnicos = new GenericDAO<>(Usuario.class).listarTecnicosChamado();
            posicaoProcesso = new GenericDAO<>(Campos.class).listarCamposJDBC(30L);
        }
    }

    public void apagaChamado(Atendimento a) {
        aDAO.apagaChamado(a, opcao);
    }

    public void ativaChamado(Atendimento a) {
        aDAO.ativaChamado(a, opcao);
    }

    public int totalAtendimento() {
        return tamanho = aDAO.totalAtendimento();
    }

    public int totalAtendimento(int l) {
        if (l == 1) {
            return tamanhoAberto = aDAO.totalAtendimento(l);
        } else {
            return tamanhoFinalizados = aDAO.totalAtendimento(l);
        }
    }

    public void listaAtendimentosMovimentos(Atendimento a) {
        listaMovimentos = aDAO.listaAtendimentosMovimentos(a.getId());
    }

    public void listarArquivos(MovimentacaoAtendimento a) {
        this.atendimentoSelecionadoMov = a;
    }

    public void listaAtendimentosMovimentosSelecionado(Atendimento a) {
        listaMovimentosSelecionado = aDAO.listaAtendimentosMovimentos(a.getId());
    }

    public void listarAtendimento() {
        List<Atendimento> listaAtendimentos = new ArrayList<>();
        sql = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                + " SUBSTRING(m.obs,1,60) AS RESUMO,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                + " bairro.descricao, destino.descricao, produto.descricao, "
                + " (SELECT u1.nome + ', ' FROM Usuario u1 WHERE u1.id  in (select cod_usuario FROM Acompanhar a WHERE cod_tabela =m.id ) FOR XML PATH ('')) AS acompanhante, "
                + " processo.descricao, m.* FROM Atendimento m "
                + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                + " LEFT join Produto produto on m.cod_produto = produto.id and produto.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos destino on m.cod_destino = destino.cod and destino.tabela = 24 and destino.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos bairro on m.cod_bairro = bairro.cod and bairro.tabela = 25 and bairro.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos processo on m.Cod_posicaoProcesso = processo.cod and processo.tabela = 30 and processo.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " ";

        switch (opcao) {
            case 0:
                if (campooTabela) {
                    sql = sql
                            + " where m.cod_negocio =" + cod_negocio + " "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Suas Tarefas em Andamento";
                    nomeTabela = "Todas as Tarefas";
                    campooTabela = false;
                } else {
                    sql = sql
                            + " where "
                            + " (DATEDIFF (DAY,m.dataAbertura,GETDATE()) >= -" + parametros.getQtdDiasAgendamento() + " and m.cod_tipoAtendimento = " + parametros.getCodPreventiva() + " AND m.statusAtendimento = 1 and m.cod_negocio = " + cod_negocio + " "
                            + " and ((m.id_tecnico =" + tecnico.getId() + " ) or (m.id_solicitante =" + tecnico.getId() + " ))) "
                            + " or "
                            + " (m.statusAtendimento = 1 and m.cod_negocio =" + cod_negocio + "  and ( (m.id_tecnico =" + tecnico.getId() + ") or (m.id_solicitante =" + tecnico.getId() + " ) ) and cod_tipoAtendimento <>" + parametros.getCodPreventiva() + " ) "
                            + " or "
                            + " (m.id in (SELECT a.cod_tabela FROM Acompanhar a join atendimento at on at.id = a.cod_tabela WHERE cod_tabela = m.id and a.cod_usuario = " + tecnico.getId() + "  and (DATEDIFF (DAY,at.dataAbertura,GETDATE()) >= -5) ) and m.statusAtendimento = 1 )"
                            + " or "
                            + " (m.cod_tipoAtendimento = " + parametros.getCodPreventiva() + " AND m.statusAtendimento = 1 and m.cod_negocio = " + cod_negocio + " and ((m.id_tecnico =" + tecnico.getId() + " ) or (m.id_solicitante =" + tecnico.getId() + " )) "
                            + " and (DATEDIFF (DAY,m.dataAbertura,GETDATE()) >= -m.qtdDiasAgendamento )) "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Todas as Tarefas";
                    nomeTabela = "Suas Tarefas em Andamento";
                    campooTabela = true;
                }
                break;
            case 1:
                if (campooTabela) {
                    sql = sql
                            + " where m.statusAtendimento = 0 and m.cod_tipoAtendimento =" + parametros.getCodAtividadeAdm() + " and m.cod_negocio =" + cod_negocio + " "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Atividades em Andamento";
                    nomeTabela = "Atividades Finalizadas";
                    campooTabela = false;
                } else {
                    sql = sql
                            + " where (m.statusAtendimento = 1 and m.cod_tipoAtendimento =" + parametros.getCodAtividadeAdm() + " and m.cod_negocio =" + cod_negocio + " "
                            + " and ((m.id_tecnico =" + tecnico.getId() + " ) or (m.id_solicitante =" + tecnico.getId() + " ) ))"
                            + " or"
                            + " (m.id in (SELECT cod_tabela FROM Acompanhar WHERE cod_tabela = m.id and acompanhar.cod_usuario = " + tecnico.getId() + " ) and m.statusAtendimento = 1 and m.cod_tipoAtendimento =" + parametros.getCodAtividadeAdm() + " ) "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Atividades Finalizadas";
                    nomeTabela = "Atividades em Andamento";
                    campooTabela = true;
                }
                break;
            case 2:
                sql = sql
                        + " where m.statusAtendimento = 1 and m.cod_tipoAtendimento =" + parametros.getCodPreventiva() + " and m.cod_negocio =" + cod_negocio + " "
                        + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                nomeCampo = "Mostrar Atividades Finalizadas";
                nomeTabela = "Atividades Agendadas";
                campooTabela = false;
                break;
            case 3:
                if (campooTabela) {
                    sql = sql
                            + " where m.statusAtendimento = 0 and m.cod_tipoAtendimento =" + parametros.getCodJuridico() + " and m.cod_negocio =" + cod_negocio + " "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Atividades em Andamento";
                    nomeTabela = "Atividades Juridicas Finalizadas";
                    campooTabela = false;
                } else {
                    sql = sql
                            + " where (m.statusAtendimento = 1 and m.cod_tipoAtendimento =" + parametros.getCodJuridico() + " and m.cod_negocio =" + cod_negocio + " "
                            + " and ((m.id_tecnico =" + tecnico.getId() + " ) or (m.id_solicitante =" + tecnico.getId() + " ) ))"
                            + " or"
                            + " (m.id in (SELECT cod_tabela FROM Acompanhar WHERE cod_tabela = m.id and acompanhar.cod_usuario = " + tecnico.getId() + " ) and m.statusAtendimento = 1 and m.cod_tipoAtendimento =" + parametros.getCodJuridico() + " ) "
                            + " order by m.statusAtendimento DESC, m.dataAbertura desc";
                    nomeCampo = "Mostrar Atividades Finalizadas";
                    nomeTabela = "Atividades Juridicas em Andamento";
                    campooTabela = true;
                }
                break;
            default:
                sql = sql + "where m.id = 0 ";
                break;
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Atendimento a = new Atendimento();
            Usuario u = new Usuario();
            Usuario t = new Usuario();
            while (rs.next()) {
                a.setId(rs.getLong("id"));
                a.setDataAbertura(rs.getDate("dataAbertura"));
                a.setDataFechamento(rs.getDate("dataFechamento"));
                a.setHoraAbertura(rs.getTime("horaAbertura"));
                a.setHoraFechamento(rs.getTime("horaFechamento"));
                a.setCod_status(rs.getLong("cod_status"));
                a.setCod_equipamento(rs.getLong("cod_equipamento"));
                a.setCod_fornecedor(rs.getLong("cod_fornecedor"));
                a.setCod_tipoAtendimento(rs.getLong("cod_tipoAtendimento"));
                a.setCod_categoria(rs.getLong("cod_categoria"));
                a.setStatusAtendimento(rs.getBoolean("statusAtendimento"));
                a.setObs(rs.getString("obs"));
                a.setAtualizadoHa(rs.getDate("AtualizadoHa"));
                a.setStatus(rs.getString(1));
                a.setTecnico(rs.getString(2));
                a.setEquipamento(rs.getString(3));
                a.setTipoAtendimento(rs.getString(4));
                a.setSolicitante(rs.getString(5));
                a.setCategoria(rs.getString(6));
                u.setId(rs.getLong(7));
                t.setId(rs.getLong(8));
                a.setResumo(rs.getString(9));
                a.setTipoData(rs.getInt(10));
                a.setBairro(rs.getString(11));
                a.setDestino(rs.getString(12));
                a.setProduto(rs.getString(13));
                a.setAcompanha(rs.getString(14));
                a.setPosicaoProcesso(rs.getString(15));
                a.setId_solicitante(u);
                a.setId_tecnico(t);
                a.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                a.setCod_arquivo(rs.getLong("cod_arquivo"));
                a.setCod_produto(rs.getLong("cod_produto"));
                a.setCusto(rs.getDouble("custo"));
                a.setQtdProduto(rs.getDouble("qtdProduto"));
                a.setCod_bairro(rs.getLong("cod_bairro"));
                a.setCod_destino(rs.getLong("cod_destino"));
                a.setValor(rs.getDouble("valor"));
                a.setRating(rs.getInt("rating"));
                a.setQtdDiasAgendamento(rs.getInt("QtdDiasAgendamento"));
                a.setCod_posicaoProcesso(rs.getLong("Cod_posicaoProcesso"));
                a.setCodChamadoExterno(rs.getLong("codChamadoExterno"));
                if (a.isAnexoMovimento()) {
                    a.setArquivo(aU.listar(a.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaChamado()));
                }
                a.setPassouEmail(rs.getBoolean("passouEmail"));
                if (!a.isStatusAtendimento()) {
                    a.setTipoData(0);
                    a.setStatusChamado("Finalizado");
                } else {
                    a.setStatusChamado("Aberto");
                }

                listaAtendimentos.add(a);
                a = new Atendimento();
                u = new Usuario();
                t = new Usuario();
            }
            this.atendimentos = listaAtendimentos;
            listaAtendimentos = null;
            a = null;
            u = null;
            t = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
        } catch (SQLException | ClassNotFoundException e) {
            PrimeFaces.current().executeScript("PF('statusDialog').hide();");
            FacesUtil.addMsgError("Erro", e.toString());
            System.out.println(" public void listarAtendimento() Erro:" + e);
        }
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
    }

    public int pegaSaldo(Long id) {
        return aDAO.pegaSaldo(id);
    }

    public void atualizaSaldo(int saldo, Long id) {
        aDAO.atualizaSaldo(saldo, id);
    }

    public void salvar() {
        if ((atendimento.getQtdProduto() == 0 && atendimento.getCod_produto() != null)) {
            FacesUtil.addMsgError("Erro", "Por favor, Informe uma quantidade para o produto!");
        } else {
            campooTabela = false;
            aDAO.salvar(atendimento, a, movimentacaoAtendimento, usuario, tecnico, mp, ativaSuprimento);
            movimentacaoAtendimento = new MovimentacaoAtendimento();
            usuario = new Usuario();
            atendimento = new Atendimento();
            data = new Date(System.currentTimeMillis());
            atendimento.setDataAbertura(data);
            atendimento.setHoraAbertura(data);
            atendimento.setStatusAtendimento(true);
            tecnico = new Usuario();
            tecnico.setId((Long) session.getAttribute("idUser"));
            FacesUtil.addMsgInfo("Sucesso", "Cadastrado com sucesso!");
            a = new ArquivoUpload();
            listarAtendimento();
            suprimentoMenu();
            PrimeFaces.current().executeScript("PF('cadastrarDialog').hide();");
            PrimeFaces.current().ajax().update("usuarios_form:atendimento");
            campooTabela = false;
            textoArquivo = "";
            textoArquivoMov = "";
            renderizaTela();
        }

    }

    public void upload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaChamado());
        //System.out.println(a.getId());
    }

    public void uploadMov(FileUploadEvent event) throws IOException {
        if (aMov.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(aMov);
        }
        aMov.setEnviado(true);
        textoArquivoMov += arq.upload(event, aMov.getId(), parametros.getDiretorioArquivo(), parametros.getPastaChamado());
    }

    public void editUpload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, editaAtendimento.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaChamado());
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void deletarArquivo(File file) throws IOException, ParseException, ClassNotFoundException {
        aDAO.deletarArquivo(file, editaAtendimento, atendimentoSelecionadoMov, atendimentoSelecionado);
        listarAtendimento();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogMov').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('movimentarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEdit').hide();");
    }

    public void sendMail(Atendimento a) {
        modeloEmail.sendMail(a);
    }

    public void anexaArquivo() {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
        carregaLista();
        suprimentoMenu();
    }

    public void editar(Atendimento atendimento) {
        editaAtendimento = atendimento;
        editaAtendimento.setListaAcompanhante(acompanhantes(editaAtendimento.getId()));
        usuario.setId(editaAtendimento.getId_solicitante().getId());
        tecnico.setId(editaAtendimento.getId_tecnico().getId());
        apagarLista = editaAtendimento.getListaAcompanhante();
        suprimentoMenuEditar();
        carregaLista();
        renderizaTela();
    }

    public void movimentar(Atendimento a) {
        if (aMov.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(aMov);
        }
        movimentarAtendimento.setDataMovimento(data);
        movimentarAtendimento.setHoraMovimento(data);
        movimentarAtendimento.setId_atendimento(a);
        movimentarAtendimento.setStatusMovimento(true);
        movimentarAtendimento.setEnviaSolic(parametros.isPadraoEmailTarefa());
        usuario.setId(a.getId_solicitante().getId());
        movimentarTecnico.setId(a.getId_tecnico().getId());
        movimentarAtendimento.setId_tecnico(movimentarTecnico);
        movimentarAtendimento.setObsAtendimento(a.getObs());
        movimentarAtendimento.setCod_equipamento(a.getCod_equipamento());
        movimentarAtendimento.setCod_categoria(a.getCod_categoria());
        movimentarAtendimento.setCod_arquivo(aMov.getId());
        movimentarAtendimento.setCod_tipoAtendimento(a.getCod_tipoAtendimento());
        listaAtendimentosMovimentos(a);
        cod_tecnico = a.getId_tecnico().getId();
        finalizarAtendimento = a;
        carregaLista();
        ativaSuprimento = (a.getCod_tipoAtendimento() == parametros.getCodSuprimentoCompra()) || (a.getCod_tipoAtendimento() == parametros.getCodSuprimentoTroca() || (a.getCod_tipoAtendimento() == parametros.getCodPerdaRouboVencimento()));
    }

    public void salvarMovimento() {

        if ((movimentarAtendimento.getQtdProduto() == 0 && movimentarAtendimento.getCod_produto() != null)) {
            FacesUtil.addMsgError("Erro", "Por favor, Informe uma quantidade para o produto!");
        } else {
            campooTabela = false;
            aDAO.salvarMovimento(finalizarAtendimento, aMov, movimentarAtendimento, opcaoTecnico, cod_tecnico, ativaSuprimento);
            if (!movimentarAtendimento.isStatusMovimento()) {
                finalizarAtendimento = new Atendimento();
                movimentarAtendimento = new MovimentacaoAtendimento();
                aMov = new ArquivoUpload();
                listarAtendimento();
                PrimeFaces.current().ajax().update("usuarios_form:atendimento");
                PrimeFaces.current().executeScript("PF('movimentarDialog').hide();");
            } else {
                movimentar(finalizarAtendimento);
                movimentarAtendimento.setObservacao("");
                movimentarAtendimento.setId(null);
                movimentarAtendimento.setAnexoMovimento(false);
                movimentarAtendimento.setCod_arquivo(null);
                movimentarAtendimento.setArquivo(null);
                movimentarAtendimento.setQtdProduto(0);
                aMov = new ArquivoUpload();
                PrimeFaces.current().ajax().update("movimentos_form:movimentar");
                PrimeFaces.current().ajax().update("formMov");
            }
            campooTabela = false;
            textoArquivo = "";
            textoArquivoMov = "";
            renderizaTela();
        }
    }

    public void filtraStatus() {
        Equipamento eq = aDAO.filtraStatus(atendimento);
        atendimento.setCod_status(eq.getCod_Status());
        usuario.setId(eq.getCod_usrResp());
        eq = null;
    }

    public void suprimentoMenu() {
        //renderizaTela();
        ativaSuprimento = (atendimento.getCod_tipoAtendimento() == parametros.getCodSuprimentoCompra()) || (atendimento.getCod_tipoAtendimento() == parametros.getCodSuprimentoTroca() || (atendimento.getCod_tipoAtendimento() == parametros.getCodPerdaRouboVencimento()));
        ativaPreventiva = (atendimento.getCod_tipoAtendimento() == parametros.getCodPreventiva());
        ativaLocal_Usuario = (atendimento.getCod_tipoAtendimento() == parametros.getCodTrocaLocal());
        ativaDestino = (atendimento.getCod_tipoAtendimento() == parametros.getCodAtividadeAdm());
        agendamento = (atendimento.getCod_tipoAtendimento() == parametros.getCodPreventiva());
        juridico = (atendimento.getCod_tipoAtendimento() == parametros.getCodJuridico());
    }

    public void suprimentoMenuEditar() {
        ativaSuprimento = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodSuprimentoCompra()) || (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodSuprimentoTroca() || (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodPerdaRouboVencimento()));
        ativaPreventiva = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodPreventiva());
        ativaLocal_Usuario = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodTrocaLocal());
        ativaDestino = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodAtividadeAdm());
        agendamento = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodPreventiva());
        juridico = (editaAtendimento.getCod_tipoAtendimento() == parametros.getCodJuridico());
    }

    public void filtraEspecie() {
        if (atendimento.getCod_categoria() == 2L) {
            atendimento.setCod_tipoAtendimento(4L);
        }
        if (atendimento.getCod_categoria() == 31L) {
            atendimento.setCod_tipoAtendimento(1L);
        }
    }

    public void atualizaSaldoProduto(int saldo, Long id, long cod_produto) {
        aDAO.atualizaSaldoProduto(saldo, id, cod_produto);
    }

    public Long pegaId(Long id) {
        return aDAO.pegaId(id);
    }

    public String getFormatData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.format(data);
    }

    public void editarAtendimento() {
        campooTabela = false;
        aDAO.editarAtendimento(editaAtendimento, usuario, tecnico, ativaSuprimento, apagarLista, a);
        tecnico.setId((Long) session.getAttribute("idUser"));
        listarAtendimento();
        PrimeFaces.current().ajax().update("usuarios_form:atendimento");
        editaAtendimento = new Atendimento();
        a = new ArquivoUpload();
        PrimeFaces.current().executeScript("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Editado com Sucesso!");
        campooTabela = false;
        textoArquivo = "";
        textoArquivoMov = "";
    }

    public List<Long> acompanhantes(Long id) {
        sql = "SELECT * FROM Acompanhar WHERE cod_tabela =" + id + " and status = 1";
        return ValidacoesBanco.acompanhantes(sql, "AtendimentoBean", "acompanhantes");
    }

    public List<Campos> getStatus() {
        return status;
    }

    public void setStatus(List<Campos> status) {
        this.status = status;
    }

    public List<Campos> getTipo() {
        return tipo;
    }

    public void setTipo(List<Campos> tipo) {
        this.tipo = tipo;
    }

    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<Equipamento> equipamentos) {
        this.equipamentos = equipamentos;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public Atendimento getEditaAtendimento() {
        return editaAtendimento;
    }

    public void setEditaAtendimento(Atendimento editaAtendimento) {
        this.editaAtendimento = editaAtendimento;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }

    public Atendimento getAtendimentoSelecionado() {
        return atendimentoSelecionado;
    }

    public void setAtendimentoSelecionado(Atendimento atendimentoSelecionado) {
        this.atendimentoSelecionado = atendimentoSelecionado;
    }

    public MovimentacaoAtendimento getMovimentacaoAtendimento() {
        return movimentacaoAtendimento;
    }

    public void setMovimentacaoAtendimento(MovimentacaoAtendimento movimentacaoAtendimento) {
        this.movimentacaoAtendimento = movimentacaoAtendimento;
    }

    public MovimentacaoAtendimento getMovimentarAtendimento() {
        return movimentarAtendimento;
    }

    public void setMovimentarAtendimento(MovimentacaoAtendimento movimentarAtendimento) {
        this.movimentarAtendimento = movimentarAtendimento;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Campos> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<Campos> categoria) {
        this.categoria = categoria;
    }

    public boolean isStatusBotao() {
        return statusBotao;
    }

    public void setStatusBotao(boolean statusBotao) {
        this.statusBotao = statusBotao;
    }

    public List<MovimentacaoAtendimento> getListaMovimentos() {
        return listaMovimentos;
    }

    public void setListaMovimentos(List<MovimentacaoAtendimento> listaMovimentos) {
        this.listaMovimentos = listaMovimentos;
    }

    public MovimentacaoAtendimento getMovimentarAtendimentoSelecionado() {
        return movimentarAtendimentoSelecionado;
    }

    public void setMovimentarAtendimentoSelecionado(MovimentacaoAtendimento movimentarAtendimentoSelecionado) {
        this.movimentarAtendimentoSelecionado = movimentarAtendimentoSelecionado;
    }

    public List<MovimentacaoAtendimento> getListaMovimentosSelecionado() {
        return listaMovimentosSelecionado;
    }

    public void setListaMovimentosSelecionado(List<MovimentacaoAtendimento> listaMovimentosSelecionado) {
        this.listaMovimentosSelecionado = listaMovimentosSelecionado;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public boolean isOpcaoTecnico() {
        return opcaoTecnico;
    }

    public void setOpcaoTecnico(boolean opcaoTecnico) {
        this.opcaoTecnico = opcaoTecnico;
    }

    public int getTamanhoAberto() {
        return tamanhoAberto;
    }

    public void setTamanhoAberto(int tamanhoAberto) {
        this.tamanhoAberto = tamanhoAberto;
    }

    public int getTamanhoFinalizados() {
        return tamanhoFinalizados;
    }

    public void setTamanhoFinalizados(int tamanhoFinalizados) {
        this.tamanhoFinalizados = tamanhoFinalizados;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public boolean isCampooTabela() {
        return campooTabela;
    }

    public void setCampooTabela(boolean campooTabela) {
        this.campooTabela = campooTabela;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public Atendimento getFinalizarAtendimento() {
        return finalizarAtendimento;
    }

    public void setFinalizarAtendimento(Atendimento finalizarAtendimento) {
        this.finalizarAtendimento = finalizarAtendimento;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public Usuario getMovimentarTecnico() {
        return movimentarTecnico;
    }

    public void setMovimentarTecnico(Usuario movimentarTecnico) {
        this.movimentarTecnico = movimentarTecnico;
    }

    public List<Usuario> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<Usuario> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public MovimentacaoAtendimento getAtendimentoSelecionadoMov() {
        return atendimentoSelecionadoMov;
    }

    public void setAtendimentoSelecionadoMov(MovimentacaoAtendimento atendimentoSelecionadoMov) {
        this.atendimentoSelecionadoMov = atendimentoSelecionadoMov;
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

    public boolean isAtivaSuprimento() {
        return ativaSuprimento;
    }

    public void setAtivaSuprimento(boolean ativaSuprimento) {
        this.ativaSuprimento = ativaSuprimento;
    }

    public boolean isAtivaPreventiva() {
        return ativaPreventiva;
    }

    public void setAtivaPreventiva(boolean ativaPreventiva) {
        this.ativaPreventiva = ativaPreventiva;
    }

    public boolean isAtivaLocal_Usuario() {
        return ativaLocal_Usuario;
    }

    public void setAtivaLocal_Usuario(boolean ativaLocal_Usuario) {
        this.ativaLocal_Usuario = ativaLocal_Usuario;
    }

    public List<Campos> getDestino() {
        return destino;
    }

    public void setDestino(List<Campos> destino) {
        this.destino = destino;
    }

    public List<Campos> getBairro() {
        return bairro;
    }

    public void setBairro(List<Campos> bairro) {
        this.bairro = bairro;
    }

    public boolean isAtivaDestino() {
        return ativaDestino;
    }

    public void setAtivaDestino(boolean ativaDestino) {
        this.ativaDestino = ativaDestino;
    }

    public boolean isAtivaEquipamento() {
        return ativaEquipamento;
    }

    public void setAtivaEquipamento(boolean ativaEquipamento) {
        this.ativaEquipamento = ativaEquipamento;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Campos> getTipoAdm() {
        return tipoAdm;
    }

    public void setTipoAdm(List<Campos> tipoAdm) {
        this.tipoAdm = tipoAdm;
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

    public boolean isAgendamento() {
        return agendamento;
    }

    public void setAgendamento(boolean agendamento) {
        this.agendamento = agendamento;
    }

    public Campos getCamposDestino() {
        return camposDestino;
    }

    public void setCamposDestino(Campos camposDestino) {
        this.camposDestino = camposDestino;
    }

    public Campos getCamposBairro() {
        return camposBairro;
    }

    public void setCamposBairro(Campos camposBairro) {
        this.camposBairro = camposBairro;
    }

    public boolean isJuridico() {
        return juridico;
    }

    public void setJuridico(boolean juridico) {
        this.juridico = juridico;
    }

    public List<Campos> getPosicaoProcesso() {
        return posicaoProcesso;
    }

    public void setPosicaoProcesso(List<Campos> posicaoProcesso) {
        this.posicaoProcesso = posicaoProcesso;
    }

}
