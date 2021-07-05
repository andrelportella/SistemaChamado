package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Atendimento;
import Modelo.MovimentacaoAtendimento;
import Modelo.Parametros;
import Modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.StreamedContent;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;

@ViewScoped
@ManagedBean
public class InformacaoBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    PreparedStatement stmt2;
    ResultSet rs2;
    String sql;
    private Connection ConexaoSQL;
    private List<Atendimento> atendimentos = new ArrayList<>();
    private Atendimento atendimentoSelecionado = new Atendimento();
    private MovimentacaoAtendimento atendimentoSelecionadoMov = new MovimentacaoAtendimento();
    private List<MovimentacaoAtendimento> listaMovimentos = new ArrayList<>();
    String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
    String cod_negocio = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio");
    private StreamedContent downloadFile;
    Parametros parametros = new Parametros();
    AtendimentoDAO aDAO = new AtendimentoDAO();
    private boolean exportaPNB;
    Arquivo arq = new Arquivo();
    ArquivoUtil aU = new ArquivoUtil();

    public InformacaoBean() {
        exportaPNB = cod_negocio.equals("2");
        parametros = new GenericDAO<>(Parametros.class).parametro();
        listarAtendimento();
        listaAtendimentosMovimentos();
    }

    public void listarArquivos(MovimentacaoAtendimento a) {
        this.atendimentoSelecionadoMov = a;
    }

    public void listarAtendimento() {
        Atendimento a = new Atendimento();
        sql = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                + " SUBSTRING(m.obs,1,60) AS RESUMO,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                + " bairro.descricao, destino.descricao, produto.descricao, m.* FROM Atendimento m "
                + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                + " LEFT join Produto produto on m.cod_produto = produto.id and produto.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos destino on m.cod_destino = destino.cod and destino.tabela = 24 and destino.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos bairro on m.cod_bairro = bairro.cod and bairro.tabela = 25 and bairro.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                + " where m.id =" + id + " and m.cod_negocio =" + cod_negocio + ""
                + " order by m.statusAtendimento DESC, m.dataAbertura desc";

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
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
                a.setQtdProduto(rs.getInt("qtdProduto"));
                a.setCod_bairro(rs.getLong("cod_bairro"));
                a.setCod_destino(rs.getLong("cod_destino"));
                a.setValor(rs.getDouble("valor"));
                a.setRating(rs.getInt("rating"));
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

            }
            this.atendimentoSelecionado = a;
            a = null;
            t = null;
            u = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "InformacaoBean", "listarAtendimento");
            System.out.println(" public void listarAtendimento() Erro:" + e);
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
        }
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void listaAtendimentosMovimentos() {
        Atendimento a = new Atendimento();
        a.setId(Long.parseLong(id));
        listaMovimentos = aDAO.listaAtendimentosMovimentos(a.getId());
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public List<MovimentacaoAtendimento> getListaMovimentos() {
        return listaMovimentos;
    }

    public void setListaMovimentos(List<MovimentacaoAtendimento> listaMovimentos) {
        this.listaMovimentos = listaMovimentos;
    }

    public Atendimento getAtendimentoSelecionado() {
        return atendimentoSelecionado;
    }

    public void setAtendimentoSelecionado(Atendimento atendimentoSelecionado) {
        this.atendimentoSelecionado = atendimentoSelecionado;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

    public MovimentacaoAtendimento getAtendimentoSelecionadoMov() {
        return atendimentoSelecionadoMov;
    }

    public void setAtendimentoSelecionadoMov(MovimentacaoAtendimento atendimentoSelecionadoMov) {
        this.atendimentoSelecionadoMov = atendimentoSelecionadoMov;
    }

    public boolean isExportaPNB() {
        return exportaPNB;
    }

    public void setExportaPNB(boolean exportaPNB) {
        this.exportaPNB = exportaPNB;
    }

}
