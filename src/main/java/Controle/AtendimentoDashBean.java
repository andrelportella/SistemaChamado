package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Atendimento;
import Modelo.Dashboard;
import Modelo.DashboardTecnico;
import Modelo.MovimentacaoAtendimento;
import Modelo.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import util.ArquivoUtil;
import util.FacesUtil;

@ManagedBean
@ViewScoped
public class AtendimentoDashBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    PreparedStatement stmt2;
    ResultSet rs2;
    private Connection ConexaoSQL;
    private Date dataDe;
    private Date dataAte;
    private boolean renderizador = false;
    private String headerCategoria = "Categorias";
    private String headerSolicitante = "Solicitantes";
    private String headerEquipamentos = "Equipamentos";
    private String headerChamados = "Chamado";
    private String headerEspecie1 = "Espécie";

    private List<Dashboard> categoria = new ArrayList<>();
    private List<Atendimento> chamadoCategoria = new ArrayList<>();
    private Atendimento categoriaSelecionado = new Atendimento();
    private List<MovimentacaoAtendimento> listaHistoricoCategoria = new ArrayList<>();
    String sqlCat;
    String sql;
    String sqlchamadoCat;

    private List<Dashboard> solicitante = new ArrayList<>();
    private List<Atendimento> chamadoSolicitante = new ArrayList<>();
    private Atendimento solicitanteSelecionado = new Atendimento();
    private List<MovimentacaoAtendimento> listaHistoricoSolicitante = new ArrayList<>();
    String sqlSol;
    String sqlchamadoSol;

    private List<Dashboard> equipamento = new ArrayList<>();
    private List<Atendimento> chamadoEquipamento = new ArrayList<>();
    private Atendimento equipamentoSelecionado = new Atendimento();
    private List<MovimentacaoAtendimento> listaHistoricoEquipamento = new ArrayList<>();
    String sqlEquip;
    String sqlchamadoEquip;

    private List<DashboardTecnico> dashEspecie = new ArrayList<>();
    private DashboardTecnico dashTecnico = new DashboardTecnico();
    private DashboardTecnico dashEspecie1 = new DashboardTecnico();

    String sqldashTec;

    private Long id_tecnico;
    private List<Usuario> tecnicos = new ArrayList<>();

    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private BarChartModel modeloTecnico;
    private BarChartModel dashModeloEspecie;
    private StreamedContent downloadFile;
    String diretorio = "C:\\SistemaChamadoBM\\";
    String pasta = "anexosChamado";
    ArquivoUtil aU = new ArquivoUtil();

    @PostConstruct
    public void init() {
        criarGrafico();
        criarEspecie();
    }
    private List<Boolean> list;

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public AtendimentoDashBean() {
        id_tecnico = (Long) session.getAttribute("idTec");
        tecnicos = new GenericDAO<>(Usuario.class).listarTecnicosChamado();
        list = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, false, false, false);
    }

    public void download(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        downloadFile = new DefaultStreamedContent(inputStream,
                Files.probeContentType(file.toPath()), file.getName());
    }

    public String getFormatDataDe() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.format(this.dataDe);
    }

    public String getFormatDataAte() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.format(this.dataAte);
    }

    public void listaAtendimentosMovimentosSelecionado(Atendimento a) {
        List<MovimentacaoAtendimento> movimentos = new ArrayList<>();
        sql = "SELECT t.nome, m.* FROM MovimentacaoAtendimento m "
                + " LEFT JOIN TECNICO t on t.id = m.id_tecnico "
                + " where m.id_atendimento = " + a.getId() + ""
                + " order by m.DataMovimento";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            MovimentacaoAtendimento m = new MovimentacaoAtendimento();
            Atendimento ab = new Atendimento();
            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setDataMovimento(rs.getDate("DataMovimento"));
                m.setHoraMovimento(rs.getTime("HoraMovimento"));
                m.setObservacao(rs.getString("observacao"));
                m.setStatusMovimento(rs.getBoolean("statusmovimento"));
                m.setTecnico(rs.getString("nome"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setAnexo(rs.getString("anexo"));
                m.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                m.setPassouEmail(rs.getBoolean("passouEmail"));
                ab.setId(rs.getLong("Id_atendimento"));
                m.setId_atendimento(ab);
                movimentos.add(m);
                m = new MovimentacaoAtendimento();
                ab = new Atendimento();
            }
            listaHistoricoSolicitante = movimentos;
            movimentos = null;
            m = null;
            ab = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaAtendimentosMovimentosSelecionado");
            System.out.println(" public void listaAtendimentosMovimentosSelecionado(Atendimento a) Erro:" + e);
        }
    }

    public void listaAtendimentosMovimentosSelecionadoCategoria(Atendimento a) {
        List<MovimentacaoAtendimento> movimentos = new ArrayList<>();
        sql = "SELECT t.nome, m.* FROM MovimentacaoAtendimento m "
                + " LEFT JOIN TECNICO t on t.id = m.id_tecnico "
                + " where m.id_atendimento = " + a.getId() + ""
                + " order by m.DataMovimento";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            MovimentacaoAtendimento m = new MovimentacaoAtendimento();
            Atendimento ab = new Atendimento();
            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setDataMovimento(rs.getDate("DataMovimento"));
                m.setHoraMovimento(rs.getTime("HoraMovimento"));
                m.setObservacao(rs.getString("observacao"));
                m.setStatusMovimento(rs.getBoolean("statusmovimento"));
                m.setTecnico(rs.getString("nome"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setAnexo(rs.getString("anexo"));
                m.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                m.setPassouEmail(rs.getBoolean("passouEmail"));
                ab.setId(rs.getLong("Id_atendimento"));
                m.setId_atendimento(ab);
                movimentos.add(m);
                m = new MovimentacaoAtendimento();
                ab = new Atendimento();
            }
            this.listaHistoricoCategoria = movimentos;
            movimentos = null;
            m = null;
            ab = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaAtendimentosMovimentosSelecionadoCategoria");
            System.out.println("   public void listaAtendimentosMovimentosSelecionadoCategoria(Atendimento a)  Erro:" + e);
        }
    }

    public void listaAtendimentosMovimentosSelecionadoEquipamento(Atendimento a) {
        List<MovimentacaoAtendimento> movimentos = new ArrayList<>();
        sql = "SELECT t.nome, m.* FROM MovimentacaoAtendimento m "
                + " LEFT JOIN TECNICO t on t.id = m.id_tecnico "
                + " where m.id_atendimento = " + a.getId() + ""
                + " order by m.DataMovimento";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Atendimento ab = new Atendimento();
            MovimentacaoAtendimento m = new MovimentacaoAtendimento();
            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setDataMovimento(rs.getDate("DataMovimento"));
                m.setHoraMovimento(rs.getTime("HoraMovimento"));
                m.setObservacao(rs.getString("observacao"));
                m.setStatusMovimento(rs.getBoolean("statusmovimento"));
                m.setTecnico(rs.getString("nome"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setAnexo(rs.getString("anexo"));
                m.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                m.setPassouEmail(rs.getBoolean("passouEmail"));
                ab.setId(rs.getLong("Id_atendimento"));
                m.setId_atendimento(ab);
                movimentos.add(m);
                ab = new Atendimento();
                m = new MovimentacaoAtendimento();
            }
            this.listaHistoricoEquipamento = movimentos;
            movimentos = null;
            m = null;
            ab = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaAtendimentosMovimentosSelecionadoEquipamento");
            System.out.println("public void listaAtendimentosMovimentosSelecionadoEquipamento(Atendimento a) Erro:" + e);
        }
    }

    public void listar() {
        listaCategoria();
        listaSolicitante();
        listaEquipamento();
        listaDashboard();
        renderizador = true;
        criarEspecie();
    }

    public void listaCategoria() {
        List<Dashboard> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlCat = " select TOP 10 C.descricao as Categoria , COUNT(a.cod_categoria) AS QTD, a.cod_categoria from Atendimento a "
                    + " JOIN Campos c on a.cod_categoria = c.cod and c.tabela =14 "
                    + " WHERE a.cod_negocio = " + cod_negocio + " and c.cod_negocio =" + cod_negocio + " AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' "
                    + " GROUP BY C.descricao,a.cod_categoria "
                    + " order by QTD desc, categoria asc";
            headerCategoria = "Todas as Categorias x Chamados Finalizados";
        } else {
            sqlCat = " select TOP 10 C.descricao as Categoria , COUNT(a.cod_categoria) AS QTD,a.cod_categoria from Atendimento a "
                    + " JOIN Campos c on a.cod_categoria = c.cod and c.tabela =14 "
                    + " WHERE a.cod_negocio = " + cod_negocio + " and c.cod_negocio =" + cod_negocio + " AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' "
                    + " AND a.id_tecnico=" + id_tecnico + ""
                    + " GROUP BY C.descricao, a.cod_categoria "
                    + " order by QTD desc, categoria asc";
            headerCategoria = "Todas as Categorias x Chamados Finalizados do Técnico";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlCat);
            rs = stmt.executeQuery();
            int i = 1;
            Dashboard cat = new Dashboard();
            while (rs.next()) {
                cat.setInformacao(rs.getString(1));
                cat.setQuantidade(rs.getInt(2));
                cat.setCodigo(rs.getInt(3));
                cat.setPosicao(i++);
                c.add(cat);
                cat = new Dashboard();
            }
            this.categoria = c;
            c = null;
            cat = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqlCat = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaCategoria");
            System.out.println(" public void listaCategoria() Erro:" + e);
        }

    }

    public void listaSolicitante() {
        List<Dashboard> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlSol = " select TOP 10 u.nome as Solicitante , COUNT(a.id_solicitante) AS QTD, id_solicitante from Atendimento a "
                    + " JOIN Usuario u on a.id_solicitante = u.id "
                    + " WHERE a.cod_negocio = " + cod_negocio + " and u.cod_negocio =" + cod_negocio + " AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' and a.id_solicitante not in (88)  "
                    + " GROUP BY U.nome, a.id_solicitante "
                    + " order by QTD desc, Solicitante asc";
            headerSolicitante = "Todos os Solicitante x Chamados Finalizados";
        } else {
            sqlSol = " select TOP 10 u.nome as Solicitante , COUNT(a.id_solicitante) AS QTD, id_solicitante from Atendimento a "
                    + " JOIN Usuario u on a.id_solicitante = u.id "
                    + " WHERE a.cod_negocio = " + cod_negocio + " and u.cod_negocio =" + cod_negocio + " AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' and a.id_solicitante not in (88)  "
                    + " AND a.id_tecnico=" + id_tecnico + ""
                    + " GROUP BY U.nome , a.id_solicitante "
                    + " order by QTD desc, Solicitante asc";
            headerSolicitante = "Todos os Solicitante x Chamados Finalizados do Técnico";
        }

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlSol);
            rs = stmt.executeQuery();
            int i = 1;
            Dashboard s = new Dashboard();
            while (rs.next()) {
                s.setInformacao(rs.getString(1));
                s.setQuantidade(rs.getInt(2));
                s.setCodigo(rs.getInt(3));
                s.setPosicao(i++);
                c.add(s);
                s = new Dashboard();
            }
            s = null;
            this.solicitante = c;
            c = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqlSol = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaSolicitante");
            System.out.println(" public void listaSolicitante() Erro:" + e);
        }

    }

    public void listaChamadoSolicitante(Long id_solicitante) {
        List<Atendimento> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlchamadoSol = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.id_solicitante =" + id_solicitante + ""
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";

        } else {
            sqlchamadoSol = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.id_solicitante =" + id_solicitante + " and id_tecnico =" + id_tecnico + " "
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlchamadoSol);
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
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
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
                a.setAcompanha(rs.getString(10));
                a.setTipoData(rs.getInt(11));
                a.setId_solicitante(u);
                a.setId_tecnico(t);
                a.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                a.setPassouEmail(rs.getBoolean("passouEmail"));
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
                a.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (a.isAnexoMovimento()) {
                    a.setArquivo(aU.listar(a.getCod_arquivo(), diretorio, pasta));
                }

                if (!a.isStatusAtendimento()) {
                    a.setTipoData(0);
                    a.setStatusChamado("Finalizado");
                } else {
                    a.setStatusChamado("Aberto");
                }
                c.add(a);
                a = new Atendimento();
                u = new Usuario();
                t = new Usuario();

            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            this.chamadoSolicitante = c;
            c = null;
            u = null;
            t = null;
            a = null;
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaChamadoSolicitante");
            System.out.println("  public void listaChamadoSolicitante(Long id_solicitante) Erro:" + e);
        }

    }

    public void listaChamadoCategoria(Long cod_categoria) {
        List<Atendimento> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlchamadoCat = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.cod_categoria =" + cod_categoria + ""
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";

        } else {
            sqlchamadoCat = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.cod_categoria =" + cod_categoria + " and id_tecnico =" + id_tecnico + " "
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";
        }

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlchamadoCat);
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
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
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
                a.setAcompanha(rs.getString(10));
                a.setTipoData(rs.getInt(11));
                a.setId_solicitante(u);
                a.setId_tecnico(t);
                a.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                a.setPassouEmail(rs.getBoolean("passouEmail"));
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
                a.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (a.isAnexoMovimento()) {
                    a.setArquivo(aU.listar(a.getCod_arquivo(), diretorio, pasta));
                }
                if (!a.isStatusAtendimento()) {
                    a.setTipoData(0);
                    a.setStatusChamado("Finalizado");
                } else {
                    a.setStatusChamado("Aberto");
                }

                c.add(a);
                a = new Atendimento();
                u = new Usuario();
                t = new Usuario();

            }
            this.chamadoCategoria = c;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqlchamadoCat = null;
            c = null;
            a = null;
            u = null;
            t = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaChamadoCategoria");
            System.out.println(" public void listaChamadoCategoria(Long cod_categoria) Erro:" + e);
        }

    }

    public void listaChamadoEquipamento(Long cod_equipamento) {
        List<Atendimento> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlchamadoEquip = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.cod_equipamento =" + cod_equipamento + ""
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";

        } else {
            sqlchamadoEquip = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                    + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                    + " SUBSTRING(m.obs,1,60) AS RESUMO,acompanha.nome as acompanhante ,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                    + " m.* FROM Atendimento m "
                    + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Usuario acompanha on m.acompanhaSolic= acompanha.id and acompanha.cod_negocio = " + cod_negocio + " "
                    + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                    + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                    + " where m.cod_negocio =" + cod_negocio + " AND M.dataFechamento >='" + getFormatDataDe() + "' AND M.dataFechamento <='" + getFormatDataAte() + "' and M.cod_equipamento =" + cod_equipamento + " and id_tecnico =" + id_tecnico + " "
                    + " order by m.statusAtendimento DESC, m.dataAbertura desc";
        }

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlchamadoEquip);
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
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
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
                a.setAcompanha(rs.getString(10));
                a.setTipoData(rs.getInt(11));
                a.setId_solicitante(u);
                a.setId_tecnico(t);
                a.setAnexoMovimento(rs.getBoolean("AnexoMovimento"));
                a.setPassouEmail(rs.getBoolean("passouEmail"));
                a.setAcompanhaSolic(rs.getLong("AcompanhaSolic"));
                a.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (a.isAnexoMovimento()) {
                    a.setArquivo(aU.listar(a.getCod_arquivo(), diretorio, pasta));
                }
                if (!a.isStatusAtendimento()) {
                    a.setTipoData(0);
                    a.setStatusChamado("Finalizado");
                } else {
                    a.setStatusChamado("Aberto");
                }

                c.add(a);
                a = null;
                a = new Atendimento();
                u = new Usuario();
                t = new Usuario();
            }
            this.chamadoEquipamento = c;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqlchamadoEquip = null;
            c = null;
            a = null;
            u = null;
            t = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaChamadoEquipamento");
            System.out.println(" public void listaChamadoEquipamento(Long cod_equipamento)  Erro:" + e);
        }
    }

    public void listaEquipamento() {
        List<Dashboard> c = new ArrayList<>();
        if (id_tecnico == null) {
            sqlEquip = " select TOP 10 e.NOME + ' - ' + e.descricao as Equipamento, u.nome as Usuario , COUNT(a.cod_equipamento) AS QTD, a.cod_equipamento from Atendimento a "
                    + " JOIN Equipamento E on a.cod_equipamento = e.id "
                    + " JOIN Usuario u on u.id = e.cod_usrResp "
                    + " WHERE a.cod_negocio = " + cod_negocio + "  and e.cod_negocio =" + cod_negocio + "  AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' and a.cod_equipamento not in (178,0)  "
                    + " GROUP BY e.NOME , u.nome , e.descricao , a.cod_equipamento"
                    + " order by QTD desc, Equipamento asc";
            headerEquipamentos = "Todos os Equipamentos x Chamados Finalizados";
        } else {
            sqlEquip = " select TOP 10 e.NOME + ' - ' + e.descricao as Equipamento, u.nome as Usuario , COUNT(a.cod_equipamento) AS QTD, a.cod_equipamento from Atendimento a "
                    + " JOIN Equipamento E on a.cod_equipamento = e.id "
                    + " JOIN Usuario u on u.id = e.cod_usrResp "
                    + " WHERE a.cod_negocio = " + cod_negocio + "  and e.cod_negocio =" + cod_negocio + "  AND A.dataFechamento >='" + getFormatDataDe() + "' AND A.dataFechamento <='" + getFormatDataAte() + "' and a.cod_equipamento not in (178,0)  "
                    + " AND a.id_tecnico=" + id_tecnico + ""
                    + " GROUP BY e.NOME , u.nome , e.descricao, a.cod_equipamento"
                    + " order by QTD desc, Equipamento asc";
            headerEquipamentos = "Todos os Equipamentos x Chamados Finalizados do Técnico";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqlEquip);
            rs = stmt.executeQuery();
            int i = 1;
            Dashboard e = new Dashboard();
            while (rs.next()) {
                e.setInformacao(rs.getString(1));
                e.setInformacao2(rs.getString(2));
                e.setQuantidade(rs.getInt(3));
                e.setCodigo(rs.getInt(4));
                e.setPosicao(i++);
                c.add(e);
                e = new Dashboard();
            }
            this.equipamento = c;
            c = null;
            e = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqlEquip = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaEquipamento");
            System.out.println("   public void listaEquipamento()  Erro:" + e);
        }

    }

    public void listaDashboard() {
        if (id_tecnico == null) {
            sqldashTec = "SELECT "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' ) AS Chamados_Criados, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 1 ) AS Em_Aberto, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 0 ) AS Finalizado,  "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 0 AND 2) AS _0_HA_2, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 3 AND 5) AS _3_HA_5, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 6 AND 9) AS _6_HA_9, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 10 AND 19) _10_HA_19, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) = 20) AS _20_HA_20, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) >= 21) AS _21_HA_21, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) < 0) AS _0_HA_0,"
                    + " (SELECT ('Todos') AS Tecnico) AS Tecnico "
                    + " ";
            headerChamados = "Todos Chamados Finalizados no Periodo";
        } else {
            sqldashTec = "SELECT "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' ) AS Chamados_Criados, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 1 ) AS Em_Aberto, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 0 ) AS Finalizado,  "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 0 AND 2) AS _0_HA_2, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 3 AND 5) AS _3_HA_5, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 6 AND 9) AS _6_HA_9, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 10 AND 19) _10_HA_19, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) = 20) AS _20_HA_20, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) >= 21) AS _21_HA_21, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + "  AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) < 0) AS _0_HA_0,"
                    + " (SELECT NOME FROM Usuario WHERE ID=" + id_tecnico + ") AS Tecnico "
                    + " ";
            headerChamados = "Todos Chamados Finalizados do Técnico no Periodo";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqldashTec);
            rs = stmt.executeQuery();

            while (rs.next()) {
                DashboardTecnico dt = new DashboardTecnico();
                dt.setChamadosCriados(rs.getInt(1));
                dt.setEmAberto(rs.getInt(2));
                dt.setFinalizado(rs.getInt(3));
                dt.setZeroHadois(rs.getInt(4));
                dt.setTresHacinco(rs.getInt(5));
                dt.setSeisHaNove(rs.getInt(6));
                dt.setDezHaDezenove(rs.getInt(7));
                dt.setVinteHaVinte(rs.getInt(8));
                dt.setVinteHaVinteum(rs.getInt(9));
                dt.setZeroHaZero(rs.getInt(10));
                dt.setTecnico(rs.getString(11));
                dashTecnico = dt;

            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqldashTec = null;
            criarGrafico();
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaDashboard");
            System.out.println("public void listaDashboard() Erro:" + e);
        }
    }

    public int atendimento() {
        int id = 0;
        sql = "SELECT MAX(cod) FROM Campos WHERE tabela = 15 and cod_negocio =" + cod_negocio + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "atendimento");
            System.out.println(" public int atendimento()  Erro:" + e);
        }
        return id;

    }

    public DashboardTecnico listaDashboardEspecie1(int tipo) {
        DashboardTecnico c = new DashboardTecnico();
        if (id_tecnico == null) {
            sqldashTec = "SELECT "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' ) AS Chamados_Criados, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 1 ) AS Em_Aberto, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 0 ) AS Finalizado,  "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 0 AND 2) AS _0_HA_2, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 3 AND 5) AS _3_HA_5, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 6 AND 9) AS _6_HA_9, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 10 AND 19) _10_HA_19, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) = 20) AS _20_HA_20, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) >= 21) AS _21_HA_21, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE cod_negocio = " + cod_negocio + "  AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) < 0) AS _0_HA_0,"
                    + " (SELECT descricao FROM Campos WHERE tabela = 15 and cod = " + tipo + " and cod_negocio =" + cod_negocio + ") AS Tecnico "
                    + " ";
            headerEspecie1 = "Todos os Chamado Finalizados no Periodo por Espécie";
        } else {
            sqldashTec = "SELECT "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' ) AS Chamados_Criados, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 1 ) AS Em_Aberto, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataAbertura >='" + getFormatDataDe() + "' AND dataAbertura <='" + getFormatDataAte() + "' and statusAtendimento = 0 ) AS Finalizado,  "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 0 AND 2) AS _0_HA_2, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 3 AND 5) AS _3_HA_5, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 6 AND 9) AS _6_HA_9, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) between 10 AND 19) _10_HA_19, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) = 20) AS _20_HA_20, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) >= 21) AS _21_HA_21, "
                    + " (SELECT COUNT(ID) FROM Atendimento WHERE id_tecnico = " + id_tecnico + " AND cod_negocio = " + cod_negocio + " AND cod_tipoAtendimento =" + tipo + " AND dataFechamento >='" + getFormatDataDe() + "' AND dataFechamento <='" + getFormatDataAte() + "' and statusAtendimento = 0 and DATEDIFF (DAY,(dataAbertura),dataFechamento) < 0) AS _0_HA_0,"
                    + " (SELECT descricao FROM Campos WHERE tabela = 15 and cod = " + tipo + " and cod_negocio =" + cod_negocio + ") AS Tecnico "
                    + " ";
            headerEspecie1 = "Todos os Chamado Finalizados do Técnico no Periodo por Espécie ";
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sqldashTec);
            rs = stmt.executeQuery();
            DashboardTecnico dt = new DashboardTecnico();
            while (rs.next()) {
                dt.setChamadosCriados(rs.getInt(1));
                dt.setEmAberto(rs.getInt(2));
                dt.setFinalizado(rs.getInt(3));
                dt.setZeroHadois(rs.getInt(4));
                dt.setTresHacinco(rs.getInt(5));
                dt.setSeisHaNove(rs.getInt(6));
                dt.setDezHaDezenove(rs.getInt(7));
                dt.setVinteHaVinte(rs.getInt(8));
                dt.setVinteHaVinteum(rs.getInt(9));
                dt.setZeroHaZero(rs.getInt(10));
                dt.setTecnico(rs.getString(11));
                c = dt;
                dt = new DashboardTecnico();
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sqldashTec = null;
            dt = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDashBean", "listaDashboardEspecie1");
            System.out.println(" public DashboardTecnico listaDashboardEspecie1(int tipo) Erro:" + e);
        }
        return c;

    }

    private void criarGrafico() {
        modeloTecnico = initBarModel(dashTecnico);
        modeloTecnico.setTitle(headerChamados);
        modeloTecnico.setAnimate(true);
        modeloTecnico.setLegendPosition("ne");
        modeloTecnico.setMouseoverHighlight(true);
        modeloTecnico.setShowPointLabels(true);
        Axis yAxis;
        yAxis = modeloTecnico.getAxis(AxisType.Y);
        yAxis.setMin(0);
        //yAxis.setMax(dashTecnico.getChamadosCriados());
    }

    private void criarEspecie() {
        dashModeloEspecie = modeloEspecie();
        dashModeloEspecie.setTitle(headerEspecie1);
        dashModeloEspecie.setAnimate(true);
        dashModeloEspecie.setLegendPosition("ne");
        dashModeloEspecie.setMouseoverHighlight(true);
        dashModeloEspecie.setShowPointLabels(true);
        Axis yAxis;
        yAxis = dashModeloEspecie.getAxis(AxisType.Y);
        yAxis.setMin(0);
    }

    private BarChartModel initBarModel(DashboardTecnico dt) {
        BarChartModel model = new BarChartModel();

        ChartSeries chamados = new ChartSeries();
        chamados.setLabel(dt.getTecnico());
        chamados.set("Criados", dt.getChamadosCriados());
        chamados.set("Em Aberto", dt.getEmAberto());
        chamados.set("Finalizado", dt.getFinalizado());
        chamados.set("0 Há 2 Dias ", dt.getZeroHadois());
        chamados.set("3 Há 5 Dias ", dt.getTresHacinco());
        chamados.set("6 Há 9 Dias ", dt.getSeisHaNove());
        chamados.set("10 Há 19 Dias ", dt.getDezHaDezenove());
        chamados.set("20 Dias ", dt.getVinteHaVinte());
        chamados.set("Maior Que 21 Dias ", dt.getVinteHaVinteum());
        model.addSeries(chamados);
        return model;
    }

    public BarChartModel modeloEspecie() {
        BarChartModel model = new BarChartModel();
        DashboardTecnico dt = new DashboardTecnico();
        DashboardTecnico dt2 = new DashboardTecnico();
        DashboardTecnico dt3 = new DashboardTecnico();
        DashboardTecnico dt4 = new DashboardTecnico();
        DashboardTecnico dt5 = new DashboardTecnico();
        if (renderizador) {
            dt = listaDashboardEspecie1(1);
            dt2 = listaDashboardEspecie1(2);
            dt3 = listaDashboardEspecie1(3);
            dt4 = listaDashboardEspecie1(4);
            dt5 = listaDashboardEspecie1(5);
        }

        ChartSeries chamados = new ChartSeries();
        ChartSeries chamados2 = new ChartSeries();
        ChartSeries chamados3 = new ChartSeries();
        ChartSeries chamados4 = new ChartSeries();
        ChartSeries chamados5 = new ChartSeries();

        chamados.setLabel(dt.getTecnico());
        chamados.set("Criados", dt.getChamadosCriados());
        chamados.set("Em Aberto", dt.getEmAberto());
        chamados.set("Finalizado", dt.getFinalizado());
        chamados.set("0 Há 2 Dias ", dt.getZeroHadois());
        chamados.set("3 Há 5 Dias ", dt.getTresHacinco());
        chamados.set("6 Há 9 Dias ", dt.getSeisHaNove());
        chamados.set("10 Há 19 Dias ", dt.getDezHaDezenove());
        chamados.set("20 Dias ", dt.getVinteHaVinte());
        chamados.set("Maior Que 21 Dias ", dt.getVinteHaVinteum());
        //------------------------------------------------------------//
        chamados2.setLabel(dt2.getTecnico());
        chamados2.set("Criados", dt2.getChamadosCriados());
        chamados2.set("Em Aberto", dt2.getEmAberto());
        chamados2.set("Finalizado", dt2.getFinalizado());
        chamados2.set("0 Há 2 Dias ", dt2.getZeroHadois());
        chamados2.set("3 Há 5 Dias ", dt2.getTresHacinco());
        chamados2.set("6 Há 9 Dias ", dt2.getSeisHaNove());
        chamados2.set("10 Há 19 Dias ", dt2.getDezHaDezenove());
        chamados2.set("20 Dias ", dt2.getVinteHaVinte());
        chamados2.set("Maior Que 21 Dias ", dt2.getVinteHaVinteum());
        //------------------------------------------------------------//
        chamados3.setLabel(dt3.getTecnico());
        chamados3.set("Criados", dt3.getChamadosCriados());
        chamados3.set("Em Aberto", dt3.getEmAberto());
        chamados3.set("Finalizado", dt3.getFinalizado());
        chamados3.set("0 Há 2 Dias ", dt3.getZeroHadois());
        chamados3.set("3 Há 5 Dias ", dt3.getTresHacinco());
        chamados3.set("6 Há 9 Dias ", dt3.getSeisHaNove());
        chamados3.set("10 Há 19 Dias ", dt3.getDezHaDezenove());
        chamados3.set("20 Dias ", dt3.getVinteHaVinte());
        chamados3.set("Maior Que 21 Dias ", dt3.getVinteHaVinteum());
        //------------------------------------------------------------//
        chamados4.setLabel(dt4.getTecnico());
        chamados4.set("Criados", dt4.getChamadosCriados());
        chamados4.set("Em Aberto", dt4.getEmAberto());
        chamados4.set("Finalizado", dt4.getFinalizado());
        chamados4.set("0 Há 2 Dias ", dt4.getZeroHadois());
        chamados4.set("3 Há 5 Dias ", dt4.getTresHacinco());
        chamados4.set("6 Há 9 Dias ", dt4.getSeisHaNove());
        chamados4.set("10 Há 19 Dias ", dt4.getDezHaDezenove());
        chamados4.set("20 Dias ", dt4.getVinteHaVinte());
        chamados4.set("Maior Que 21 Dias ", dt4.getVinteHaVinteum());
        //------------------------------------------------------------//
        chamados5.setLabel(dt5.getTecnico());
        chamados5.set("Criados", dt5.getChamadosCriados());
        chamados5.set("Em Aberto", dt5.getEmAberto());
        chamados5.set("Finalizado", dt5.getFinalizado());
        chamados5.set("0 Há 2 Dias ", dt5.getZeroHadois());
        chamados5.set("3 Há 5 Dias ", dt5.getTresHacinco());
        chamados5.set("6 Há 9 Dias ", dt5.getSeisHaNove());
        chamados5.set("10 Há 19 Dias ", dt5.getDezHaDezenove());
        chamados5.set("20 Dias ", dt5.getVinteHaVinte());
        chamados5.set("Maior Que 21 Dias ", dt5.getVinteHaVinteum());

        model.addSeries(chamados);
        model.addSeries(chamados2);
        model.addSeries(chamados3);
        model.addSeries(chamados4);
        model.addSeries(chamados5);
        return model;

    }

    public Long getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(Long id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public void setDataDe(Date dataDe) {
        this.dataDe = dataDe;
    }

    public void setDataAte(Date dataAte) {
        this.dataAte = dataAte;
    }

    public List<Dashboard> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<Dashboard> categoria) {
        this.categoria = categoria;
    }

    public List<Dashboard> getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(List<Dashboard> solicitante) {
        this.solicitante = solicitante;
    }

    public List<Dashboard> getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(List<Dashboard> equipamento) {
        this.equipamento = equipamento;
    }

    public Date getDataDe() {
        return dataDe;
    }

    public Date getDataAte() {
        return dataAte;
    }

    public boolean isRenderizador() {
        return renderizador;
    }

    public void setRenderizador(boolean renderizador) {
        this.renderizador = renderizador;
    }

    public BarChartModel getModeloTecnico() {
        return modeloTecnico;
    }

    public void setModeloTecnico(BarChartModel modeloTecnico) {
        this.modeloTecnico = modeloTecnico;
    }

    public String getHeaderCategoria() {
        return headerCategoria;
    }

    public void setHeaderCategoria(String headerCategoria) {
        this.headerCategoria = headerCategoria;
    }

    public String getHeaderSolicitante() {
        return headerSolicitante;
    }

    public void setHeaderSolicitante(String headerSolicitante) {
        this.headerSolicitante = headerSolicitante;
    }

    public String getHeaderEquipamentos() {
        return headerEquipamentos;
    }

    public void setHeaderEquipamentos(String headerEquipamentos) {
        this.headerEquipamentos = headerEquipamentos;
    }

    public String getHeaderChamados() {
        return headerChamados;
    }

    public void setHeaderChamados(String headerChamados) {
        this.headerChamados = headerChamados;
    }

    public DashboardTecnico getDashTecnico() {
        return dashTecnico;
    }

    public void setDashTecnico(DashboardTecnico dashTecnico) {
        this.dashTecnico = dashTecnico;
    }

    public DashboardTecnico getDashEspecie1() {
        return dashEspecie1;
    }

    public void setDashEspecie1(DashboardTecnico dashEspecie1) {
        this.dashEspecie1 = dashEspecie1;
    }

    public List<DashboardTecnico> getDashEspecie() {
        return dashEspecie;
    }

    public void setDashEspecie(List<DashboardTecnico> dashEspecie) {
        this.dashEspecie = dashEspecie;
    }

    public BarChartModel getDashModeloEspecie() {
        return dashModeloEspecie;
    }

    public void setDashModeloEspecie(BarChartModel dashModeloEspecie) {
        this.dashModeloEspecie = dashModeloEspecie;
    }

    public List<Usuario> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<Usuario> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public List<Atendimento> getChamadoSolicitante() {
        return chamadoSolicitante;
    }

    public void setChamadoSolicitante(List<Atendimento> chamadoSolicitante) {
        this.chamadoSolicitante = chamadoSolicitante;
    }

    public Atendimento getSolicitanteSelecionado() {
        return solicitanteSelecionado;
    }

    public void setSolicitanteSelecionado(Atendimento solicitanteSelecionado) {
        this.solicitanteSelecionado = solicitanteSelecionado;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public List<MovimentacaoAtendimento> getListaHistoricoSolicitante() {
        return listaHistoricoSolicitante;
    }

    public void setListaHistoricoSolicitante(List<MovimentacaoAtendimento> listaHistoricoSolicitante) {
        this.listaHistoricoSolicitante = listaHistoricoSolicitante;
    }

    public List<Atendimento> getChamadoCategoria() {
        return chamadoCategoria;
    }

    public void setChamadoCategoria(List<Atendimento> chamadoCategoria) {
        this.chamadoCategoria = chamadoCategoria;
    }

    public Atendimento getCategoriaSelecionado() {
        return categoriaSelecionado;
    }

    public void setCategoriaSelecionado(Atendimento categoriaSelecionado) {
        this.categoriaSelecionado = categoriaSelecionado;
    }

    public List<MovimentacaoAtendimento> getListaHistoricoCategoria() {
        return listaHistoricoCategoria;
    }

    public void setListaHistoricoCategoria(List<MovimentacaoAtendimento> listaHistoricoCategoria) {
        this.listaHistoricoCategoria = listaHistoricoCategoria;
    }

    public List<Atendimento> getChamadoEquipamento() {
        return chamadoEquipamento;
    }

    public void setChamadoEquipamento(List<Atendimento> chamadoEquipamento) {
        this.chamadoEquipamento = chamadoEquipamento;
    }

    public Atendimento getEquipamentoSelecionado() {
        return equipamentoSelecionado;
    }

    public void setEquipamentoSelecionado(Atendimento equipamentoSelecionado) {
        this.equipamentoSelecionado = equipamentoSelecionado;
    }

    public List<MovimentacaoAtendimento> getListaHistoricoEquipamento() {
        return listaHistoricoEquipamento;
    }

    public void setListaHistoricoEquipamento(List<MovimentacaoAtendimento> listaHistoricoEquipamento) {
        this.listaHistoricoEquipamento = listaHistoricoEquipamento;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

}
