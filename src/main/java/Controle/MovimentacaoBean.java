/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;

/**
 *
 * @author ricardo
 */
@ViewScoped
@ManagedBean
public class MovimentacaoBean implements Serializable {
    
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private StreamedContent downloadFile;
    Parametros parametros = new Parametros();
    private Connection ConexaoSQL;
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private List<MovimentacaoAtendimento> movimentacoes = new ArrayList<>();
    private List<MovimentacaoAtendimento> movimentacoesSelecionada = new ArrayList<>();
    private MovimentacaoAtendimento atendimentoSelecionadoMov = new MovimentacaoAtendimento();
    private MovimentacaoAtendimento atendimentoSelecionado = new MovimentacaoAtendimento();
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();
    AtendimentoDAO aDAO = new AtendimentoDAO();
    private Atendimento atendimento = new Atendimento();
    
    public MovimentacaoBean() {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        listaMovimentos();
    }
    
    public void listaMovimentosSelecionado(MovimentacaoAtendimento a) {
        this.movimentacoesSelecionada = aDAO.listaAtendimentosMovimentos(a.getId_atendimento().getId());
    }
    
    public void visualizaAtendimento(MovimentacaoAtendimento ma) {
        sql = " SELECT status.descricao, tecnico.nome, equip.NOME +' -  '+equip.descricao , "
                + " tipoMov.descricao, users.nome, categoria.descricao, users.id as tid, tecnico.id as tid2, "
                + " SUBSTRING(m.obs,1,60) AS RESUMO,  DATEDIFF (DAY,m.atualizadoHa,GETDATE()) as data,"
                + " bairro.descricao, destino.descricao, produto.descricao, "
                + " (SELECT u1.nome + ', ' FROM Usuario u1 WHERE u1.id  in (select cod_usuario FROM Acompanhar a WHERE cod_tabela =m.id ) FOR XML PATH ('')) AS acompanhante, "
                + " m.* FROM Atendimento m "
                + " LEFT join Campos status on m.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario tecnico on m.id_tecnico= tecnico.id and tecnico.cod_negocio = " + cod_negocio + " "
                + " LEFT join Equipamento equip on m.cod_equipamento = equip.id and equip.cod_negocio = " + cod_negocio + " "
                + " LEFT join Produto produto on m.cod_produto = produto.id and produto.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos tipoMov on m.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos destino on m.cod_destino = destino.cod and destino.tabela = 24 and destino.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos bairro on m.cod_bairro = bairro.cod and bairro.tabela = 25 and bairro.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Usuario users on users.id = m.id_solicitante and users.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos categoria on m.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + " "
                + " where m.id =" + ma.getId_atendimento().getId() + " ";
        System.out.println(sql);
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
                atendimento = a;
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
            PrimeFaces.current().executeScript("PF('statusDialog').hide();");
            FacesUtil.addMsgError("Erro", e.toString());
            System.out.println(" public void visualizaAtendimento() Erro:" + e);
        }
        
    }
    
    public void listaMovimentos() {
        List<MovimentacaoAtendimento> campos = new ArrayList<>();
        sql = " SELECT top 2000 t.nome, a.obs, s.nome, categoria.descricao, tipoMov.descricao, produto.descricao, ma.* FROM MovimentacaoAtendimento ma "
                + " JOIN Atendimento a on ma.id_atendimento = a.id "
                + " LEFT JOIN Usuario s on s.id = a.id_solicitante "
                + " LEFT JOIN Usuario t on t.id = ma.id_tecnico "
                + " LEFT join Produto produto on a.cod_produto = produto.id and produto.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos tipoMov on a.cod_tipoAtendimento = tipoMov.cod and tipoMov.tabela = 15 and tipoMov.cod_negocio = " + cod_negocio + " "
                + " RIGHT join Campos categoria on a.cod_categoria = categoria.cod and categoria.tabela = 14 and categoria.cod_negocio = " + cod_negocio + ""
                + " where a.cod_negocio = " + cod_negocio + " "
                + " order by dataMovimento desc";
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
                m.setObsAtendimento(rs.getString("obs"));
                Atendimento ab = new Atendimento();
                ab.setId(rs.getLong("Id_atendimento"));
                ab.setCategoria(rs.getString(4));
                ab.setSolicitante(rs.getString(3));
                m.setId_atendimento(ab);
                m.setPassouEmail(rs.getBoolean("passouEmail"));
                m.setCod_arquivo(rs.getLong("Cod_arquivo"));
                m.setTipoAtendimento(rs.getString(5));
                m.setProduto(rs.getString(6));
                if (m.isAnexoMovimento()) {
                    m.setArquivo(aU.listar(m.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaChamado()));
                }
                campos.add(m);
                m = new MovimentacaoAtendimento();
            }
            this.movimentacoes = campos;
            campos = null;
            m = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "MovimentacaoBean", "listaAtendimentosMovimentos");
            System.out.println("public void listaAtendimentosMovimentos(Atendimento a) Erro:" + e);
        }
    }
    
    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }
    
    public void listarArquivos(MovimentacaoAtendimento a) {
        this.atendimentoSelecionadoMov = a;
    }
    
    public StreamedContent getDownloadFile() {
        return downloadFile;
    }
    
    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }
    
    public List<MovimentacaoAtendimento> getMovimentacoes() {
        return movimentacoes;
    }
    
    public void setMovimentacoes(List<MovimentacaoAtendimento> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }
    
    public List<MovimentacaoAtendimento> getMovimentacoesSelecionada() {
        return movimentacoesSelecionada;
    }
    
    public void setMovimentacoesSelecionada(List<MovimentacaoAtendimento> movimentacoesSelecionada) {
        this.movimentacoesSelecionada = movimentacoesSelecionada;
    }
    
    public MovimentacaoAtendimento getAtendimentoSelecionadoMov() {
        return atendimentoSelecionadoMov;
    }
    
    public void setAtendimentoSelecionadoMov(MovimentacaoAtendimento atendimentoSelecionadoMov) {
        this.atendimentoSelecionadoMov = atendimentoSelecionadoMov;
    }
    
    public MovimentacaoAtendimento getAtendimentoSelecionado() {
        return atendimentoSelecionado;
    }
    
    public void setAtendimentoSelecionado(MovimentacaoAtendimento atendimentoSelecionado) {
        this.atendimentoSelecionado = atendimentoSelecionado;
    }
    
    public Atendimento getAtendimento() {
        return atendimento;
    }
    
    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }
}
