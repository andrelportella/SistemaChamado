package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Atendimento;
import Modelo.Campos;
import Modelo.MovimentacaoAtendimento;
import Modelo.MovimentacaoProduto;
import Modelo.Parametros;
import Modelo.Produto;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;
import util.Util;

@ManagedBean
@ViewScoped
public class ProdutoBean implements Serializable {

    private List<Campos> camposProdutos = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();
    private List<Produto> movimentacaoProdutos = new ArrayList<>();
    private List<MovimentacaoAtendimento> listaMovimentosSelecionado = new ArrayList<>();
    private Produto produtoEdit = new Produto();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private Produto produto = new Produto();
    Util util = new Util();
    private Connection ConexaoSQL;
    private Produto produtoSelecionado = new Produto();
    private Produto produtoSelecionadoChamado = new Produto();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private List<Boolean> list;
    private List<Boolean> list2;
    Parametros parametros = new Parametros();
    private Atendimento atendimentoSelecionado = new Atendimento();
    private boolean carregaDescricao;
    Arquivo arq = new Arquivo();
    ArquivoUtil aU = new ArquivoUtil();

    public ProdutoBean() {
        carregaDescricao = cod_negocio == 2L;
        parametros = new GenericDAO<>(Parametros.class).parametro();
        camposProdutos = new GenericDAO<>(Campos.class).listarCamposJDBC(3L);
        listaProdutos();
        list = Arrays.asList(true, true, true, true, true, true, true);
        list2 = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false);
    }

    public void atualizaProduto() {

    }

    public void onToggle2(ToggleEvent e) {
        list2.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void listaProdutos() {
        try {
            List<Produto> Produtos = new ArrayList<>();
            sql = "SELECT c.descricao as produto, (select "
                    + " (SELECT case when SUM(qtdInserida) IS NULL then 0 else SUM(qtdInserida) end from MovimentacaoProduto where cod_produto = P.id  and tipoBaixa = '+') -  "
                    + " (SELECT case when SUM(qtdInserida) IS NULL then 0 else SUM(qtdInserida) end  from MovimentacaoProduto where cod_produto = P.id and tipoBaixa = '-') -  "
                    + " (SELECT case when SUM(qtdInserida) IS NULL then 0 else SUM(qtdInserida) end  from MovimentacaoProduto where cod_produto = P.id  and tipoBaixa = '*')) as saldoProduto, p.* "
                    + " FROM Produto p JOIN CAMPOS c ON p.cod_produto = c.cod  and c.tabela = 3 and c.cod_negocio = " + cod_negocio + ""
                    + " where p.cod_negocio=" + cod_negocio + " "
                    + " order by c.descricao";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Produto p = new Produto();
            while (rs.next()) {
                p.setId(rs.getLong("ID"));
                p.setDescricao(rs.getString("descricao"));
                p.setProduto(rs.getString("produto"));
                p.setCod_produto(rs.getLong("Cod_produto"));
                p.setObs(rs.getString("obs"));
                p.setValor(rs.getDouble("valor"));
                p.setSaldo(rs.getDouble("saldoProduto"));
                p.setStatus(rs.getBoolean("status"));
                p.setObs2(rs.getString("obs2"));
                Produtos.add(p);
                p = new Produto();
            }
            this.produtos = Produtos;
            Produtos = null;
            p = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ProdutoBean", "listaProdutos");
            System.out.println("listaProdutos Erro:" + e);
        }

    }

    public void listaMovimentacaoProduto() {
        try {
            List<Produto> Produtos = new ArrayList<>();
            sql = " "
                    + " SELECT mp.qtdInserida as Qtd, ma.dataMovimento as DataMovimentacao, "
                    + " s.nome as Solicitante , t.nome as Tecnico, "
                    + " case "
                    + " when mp.tipoBaixa = '-' then 'Uso / Consumo' "
                    + " when mp.tipoBaixa = '+' then 'Entrada' "
                    + " when mp.tipoBaixa = '*' then 'Saída' "
                    + " end as TipoMovimentacao, c.descricao, a.id as Ticket, mp.id "
                    + " FROM MovimentacaoProduto mp "
                    + " LEFT JOIN MovimentacaoAtendimento ma on ma.id = mp.id_movimento "
                    + " LEFT JOIN Atendimento a on a.id = mp.cod_atendimento "
                    + " LEFT JOIN Usuario s on s.id = a.id_solicitante "
                    + " LEFT JOIN Usuario t on t.id = a.id_tecnico "
                    + " LEFT JOIN Campos as c on c.cod = a.cod_categoria and c.tabela = 14 and c.cod_negocio = " + cod_negocio + " "
                    + " where mp.cod_atendimento <> 0 and mp.cod_produto = " + produtoSelecionado.getId() + ""
                    + " and a.cod_negocio = " + cod_negocio + " "
                    + " order by DataMovimentacao desc ";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            Produto p = new Produto();
            while (rs.next()) {
                p.setId(rs.getLong("id"));
                p.setQtd(rs.getDouble(1));
                p.setDataMovimentacao(rs.getDate(2));
                p.setSolicitante(rs.getString(3));
                p.setTecnico(rs.getString(4));
                p.setTipoMovimentacao(rs.getString(5));
                p.setCategoria(rs.getString(6));
                p.setCod_atendimento(rs.getLong(7));
                Produtos.add(p);
                p = new Produto();
            }
            this.movimentacaoProdutos = Produtos;
            Produtos = null;
            p = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ProdutoBean", "listaMovimentacaoProduto");
            System.out.println("public void listaMovimentacaoProduto() Erro:" + e);
        }

    }

    public void listaChamadoProduto(Produto p) {
        sql = " SELECT status.descricao, tecnico.nome, equip.NOME +' - '+equip.descricao, "
                + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                + " SUBSTRING(m.obs,1,50) AS RESUMO, DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data, "
                + " bairro.descricao, destino.descricao, produto.descricao, m.* "
                + " FROM Atendimento m "
                + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                + " LEFT join Produto produto on m.cod_produto = produto.id and produto.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos destino on m.cod_destino = destino.cod and destino.tabela = 24 and destino.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos bairro on m.cod_bairro = bairro.cod and bairro.tabela = 25 and bairro.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                + " where m.id = " + p.getCod_atendimento() + "";
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

                this.atendimentoSelecionado = a;
                a = new Atendimento();
                u = new Usuario();
                t = new Usuario();
            }
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
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
            FacesUtil.addMsgError("Erro", e.toString());
            System.out.println(" public void listarAtendimento() Erro:" + e);
        }
    }

    public void listaAtendimentosMovimentosSelecionado(Atendimento a) {
        List<MovimentacaoAtendimento> movimentos = new ArrayList<>();
        sql = "SELECT t.nome, m.* FROM MovimentacaoAtendimento m "
                + " LEFT JOIN Usuario t on t.id = m.id_tecnico "
                + " where m.id_atendimento = " + a.getId() + ""
                + " order by m.DataMovimento";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
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
                Atendimento ab = new Atendimento();
                ab.setId(rs.getLong("Id_atendimento"));
                m.setId_atendimento(ab);
                m.setCod_arquivo(rs.getLong("Cod_arquivo"));
                if (m.isAnexoMovimento()) {
                    m.setArquivo(aU.listar(m.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaChamado()));
                }
                movimentos.add(m);
                m = new MovimentacaoAtendimento();
            }
            m = null;
            listaMovimentosSelecionado = movimentos;
            movimentos = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ProdutoBean", "listaAtendimentosMovimentosSelecionado");
            System.out.println(" public void listaAtendimentosMovimentosSelecionado(Atendimento a) Erro:" + e);
        }
    }

    public void salvar() {
        produto.setSaldo(0L);
        produto.setStatus(true);
        new GenericDAO<>(Produto.class).salvar2(produto);
        MovimentacaoProduto mp = new MovimentacaoProduto();
        mp.setCod_atendimento(0L);
        mp.setCod_produto(produto.getId());
        mp.setQtdInserida(0);
        mp.setTipoBaixa("-");
        new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        mp = new MovimentacaoProduto();
        mp.setCod_atendimento(0L);
        mp.setCod_produto(produto.getId());
        mp.setQtdInserida(0);
        mp.setTipoBaixa("+");
        new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        mp = new MovimentacaoProduto();
        mp.setCod_atendimento(0L);
        mp.setCod_produto(produto.getId());
        mp.setQtdInserida(0);
        mp.setTipoBaixa("*");
        new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        mp = null;
        produto = new Produto();
        listaProdutos();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Cadastrado com sucesso!");
    }

    public void editar(Produto produto) {
        this.produtoEdit = produto;
    }

    public void editarProduto() {
        new GenericDAO<>(Produto.class).salvar(produtoEdit);
        listaProdutos();
        produtoEdit = new Produto();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Editado com sucesso!");
    }

    public List<Campos> getCamposProdutos() {
        return camposProdutos;
    }

    public void setCamposProdutos(List<Campos> camposProdutos) {
        this.camposProdutos = camposProdutos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public Produto getProdutoEdit() {
        return produtoEdit;
    }

    public void setProdutoEdit(Produto produtoEdit) {
        this.produtoEdit = produtoEdit;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Produto getProdutoSelecionado() {
        return produtoSelecionado;
    }

    public void setProdutoSelecionado(Produto produtoSelecionado) {
        this.produtoSelecionado = produtoSelecionado;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public List<Produto> getMovimentacaoProdutos() {
        return movimentacaoProdutos;
    }

    public void setMovimentacaoProdutos(List<Produto> movimentacaoProdutos) {
        this.movimentacaoProdutos = movimentacaoProdutos;
    }

    public Atendimento getAtendimentoSelecionado() {
        return atendimentoSelecionado;
    }

    public void setAtendimentoSelecionado(Atendimento atendimentoSelecionado) {
        this.atendimentoSelecionado = atendimentoSelecionado;
    }

    public List<MovimentacaoAtendimento> getListaMovimentosSelecionado() {
        return listaMovimentosSelecionado;
    }

    public void setListaMovimentosSelecionado(List<MovimentacaoAtendimento> listaMovimentosSelecionado) {
        this.listaMovimentosSelecionado = listaMovimentosSelecionado;
    }

    public List<Boolean> getList2() {
        return list2;
    }

    public void setList2(List<Boolean> list2) {
        this.list2 = list2;
    }

    public Produto getProdutoSelecionadoChamado() {
        return produtoSelecionadoChamado;
    }

    public void setProdutoSelecionadoChamado(Produto produtoSelecionadoChamado) {
        this.produtoSelecionadoChamado = produtoSelecionadoChamado;
    }

    public boolean isCarregaDescricao() {
        return carregaDescricao;
    }

    public void setCarregaDescricao(boolean carregaDescricao) {
        this.carregaDescricao = carregaDescricao;
    }

}
