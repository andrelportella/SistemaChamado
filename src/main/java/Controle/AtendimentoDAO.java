/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Acompanhar;
import Modelo.ArquivoUpload;
import Modelo.Atendimento;
import Modelo.Equipamento;
import Modelo.MovimentacaoAtendimento;
import Modelo.MovimentacaoProduto;
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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import util.ArquivoUtil;
import util.FacesUtil;
import util.ModeloEmail;
import util.ValidacoesBanco;

/**
 *
 * @author ricardo
 */
public class AtendimentoDAO implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String sql2;
    PreparedStatement stmt2;
    ResultSet rs2;
    private Connection ConexaoSQL;
    Date data;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    Long cod_user;
    Parametros parametros;
    ModeloEmail modeloEmail;
    ArquivoUtil aU;

    public AtendimentoDAO() {

    }

    public List onToggle() {
        List<Boolean> list;
        return list = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false);
    }

    public void apagaChamado(Atendimento a, int tipo) {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        if (verificaChamado(a) <= parametros.getQtdRegistroApagaChamado()) {
            sql = "DELETE Atendimento WHERE ID = " + a.getId() + "";
            sql2 = "DELETE MovimentacaoAtendimento where id_atendimento = " + a.getId() + "";
            String sql3 = "DELETE MovimentacaoProduto where cod_atendimento = " + a.getId() + "";
            ValidacoesBanco.update(sql2, "AtendimentoDAO", "apagaChamado SQL2");
            ValidacoesBanco.update(sql3, "AtendimentoDAO", "apagaChamado SQL3");
            ValidacoesBanco.update(sql, "AtendimentoDAO", "apagaChamado SQL");
            FacesUtil.addMsgInfo("Sucesso", "Tarefa excluida com Sucesso!!");
        } else {
            FacesUtil.addMsgError("Erro", "A Tarefa já tem várias movimentações, para exclusão por favor informe ao setor de TI.");
        }
    }

    public int verificaChamado(Atendimento a) {
        sql = "SELECT count(id) FROM MovimentacaoAtendimento where id_atendimento = " + a.getId() + "";
        return ValidacoesBanco.retornaInt(sql, "AtendimentoDAO", "verificaChamado");
    }

    public void ativaChamado(Atendimento a, int tipo) {
        data = new Date(System.currentTimeMillis());
        sql = "update MovimentacaoAtendimento set statusMovimento = 1 where id_atendimento= " + a.getId() + "";
        a.setStatusAtendimento(true);
        a.setDataFechamento(null);
        a.setHoraFechamento(null);
        a.setDataReAtivacao(data);
        a.setHoraReativacao(data);
        a.setCod_usuarioReativou((Long) session.getAttribute("idUser"));
        new GenericDAO<>(Atendimento.class).update(a);
        ValidacoesBanco.update(sql, "AtendimentoDAO", "ativaChamado");
    }

    public int totalAtendimento() {
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + "";
        return ValidacoesBanco.retornaInt(sql, "AtendimentoDAO", "totalAtendimento");
    }

    public int totalAtendimento(int l) {
        sql = "SELECT count(id) FROM atendimento where statusAtendimento = " + l + " and cod_negocio =" + cod_negocio + "";
        return ValidacoesBanco.retornaInt(sql, "AtendimentoDAO", "totalAtendimento");
    }

    public List listaAtendimentosMovimentos(Long a) {
        aU = new ArquivoUtil();
        parametros = new GenericDAO<>(Parametros.class).parametro();
        List<MovimentacaoAtendimento> campos = new ArrayList<>();
        sql = " SELECT t.nome,a.obs, p.descricao, p.obs as codb2c, p.valor, m.* "
                + " FROM MovimentacaoAtendimento m "
                + " LEFT JOIN Usuario t on t.id = m.id_tecnico "
                + " LEFT JOIN Atendimento a on  a.id = m.id_atendimento "
                + " LEFT JOIN Produto p on p.id = m.cod_produto "
                + " where m.id_atendimento = " + a + " "
                + " order by m.DataMovimento desc , m.horaMovimento desc ";
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
                m.setId_atendimento(ab);
                m.setPassouEmail(rs.getBoolean("passouEmail"));
                m.setCod_arquivo(rs.getLong("Cod_arquivo"));
                m.setQtdProduto(rs.getDouble("qtdProduto"));
                m.setProduto(rs.getString("descricao"));
                m.setCodB2c(rs.getString(4));
                m.setValorProduto(rs.getDouble(5));
                if (m.isAnexoMovimento()) {
                    m.setArquivo(aU.listar(m.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaChamado()));
                }
                campos.add(m);
                m = new MovimentacaoAtendimento();
            }
            m = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
            return campos;
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDAO", "listaAtendimentosMovimentos");
            System.out.println("public void listaAtendimentosMovimentos(Atendimento a) Erro:" + e);
            return null;
        }

    }

    public int pegaSaldo(Long id) {
        sql = "SELECT saldo FROM produto where id = " + id + " and cod_negocio =" + cod_negocio + "";
        return ValidacoesBanco.retornaInt(sql, "AtendimentoDAO", "pegaSaldo");
    }

    public void atualizaSaldo(int saldo, Long id) {
        sql = "UPDATE PRODUTO SET SALDO =" + saldo + " where id = " + id + " ";
        ValidacoesBanco.update(sql, "AtendimentoDAO", "atualizaSaldo");
    }

    public void salvar(Atendimento atendimento, ArquivoUpload a, MovimentacaoAtendimento movimentacaoAtendimento, Usuario usuario, Usuario tecnico, MovimentacaoProduto mp, boolean ativaSuprimento) {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        data = new Date(System.currentTimeMillis());
        modeloEmail = new ModeloEmail();
        cod_user = (Long) session.getAttribute("idUser");
        atendimento.setCod_arquivo(a.getId());
        if (a.isEnviado()) {
            atendimento.setAnexoMovimento(a.isEnviado());
            new GenericDAO<>(ArquivoUpload.class).salvar(a);
        }
        atendimento.setId_solicitante(usuario);
        atendimento.setId_tecnico(tecnico);
        atendimento.setAtualizadoHa(atendimento.getDataAbertura());
        if (!atendimento.isStatusAtendimento()) {
            atendimento.setDataFechamento(atendimento.getDataAbertura());
            atendimento.setHoraFechamento(atendimento.getHoraAbertura());
        }
        new GenericDAO<>(Atendimento.class).salvar2(atendimento);
        movimentacaoAtendimento.setId_atendimento(atendimento);
        movimentacaoAtendimento.setId_tecnico(tecnico);
        movimentacaoAtendimento.setDataMovimento(atendimento.getDataAbertura());
        movimentacaoAtendimento.setHoraMovimento(atendimento.getHoraAbertura());
        movimentacaoAtendimento.setObservacao(atendimento.getObs());
        movimentacaoAtendimento.setStatusMovimento(atendimento.isStatusAtendimento());
        movimentacaoAtendimento.setAnexoMovimento(atendimento.isAnexoMovimento());
        movimentacaoAtendimento.setCod_arquivo(atendimento.getCod_arquivo());
        movimentacaoAtendimento.setCod_produto(atendimento.getCod_produto());
        movimentacaoAtendimento.setQtdProduto(atendimento.getQtdProduto());
        if (!atendimento.getListaAcompanhante().isEmpty()) {
            System.out.println(Acompanhar.class.getSimpleName());
            ValidacoesBanco.salvarAcompanhantes(Acompanhar.class.getSimpleName(), atendimento.getListaAcompanhante(), atendimento.getId(), cod_user, data);
        }
        new GenericDAO<>(MovimentacaoAtendimento.class).salvar2(movimentacaoAtendimento);
        if (ativaSuprimento && parametros.getCodSuprimentoCompra() == atendimento.getCod_tipoAtendimento()) {
            mp.setCod_atendimento(atendimento.getId());
            mp.setCod_produto(atendimento.getCod_produto());
            mp.setTipoBaixa("+");
            mp.setQtdInserida(atendimento.getQtdProduto());
            mp.setId_movimento(movimentacaoAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        } else if (ativaSuprimento && parametros.getCodSuprimentoTroca() == atendimento.getCod_tipoAtendimento()) {
            mp.setCod_atendimento(atendimento.getId());
            mp.setCod_produto(atendimento.getCod_produto());
            mp.setTipoBaixa("-");
            mp.setQtdInserida(atendimento.getQtdProduto());
            mp.setId_movimento(movimentacaoAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        } else if (ativaSuprimento && parametros.getCodPerdaRouboVencimento() == atendimento.getCod_tipoAtendimento()) {
            mp.setCod_atendimento(atendimento.getId());
            mp.setCod_produto(atendimento.getCod_produto());
            mp.setTipoBaixa("*");
            mp.setQtdInserida(atendimento.getQtdProduto());
            mp.setId_movimento(movimentacaoAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        }
        modeloEmail.sendMail(atendimento);
        if (atendimento.getCod_equipamento() != 0) {
            atualizaEquipamento(atendimento);
        }
    }

    public void deletarArquivo(File file, Atendimento editaAtendimento, MovimentacaoAtendimento atendimentoSelecionadoMov, Atendimento atendimentoSelecionado) throws IOException, ParseException, ClassNotFoundException {
        (new File(file.getAbsolutePath())).delete();
        File f = new File(file.getParent());
        if (f.listFiles().length == 0) {
            if (editaAtendimento.getId() != null) {
                atualizaArquivo(editaAtendimento.getId(), 1);
                atualizaArquivo(pegaidMovimentacaoMin(editaAtendimento.getId()), 0);
            } else if (atendimentoSelecionadoMov.getId() != null) {
                atualizaArquivo(atendimentoSelecionadoMov.getId(), 0);
            } else {
                atualizaArquivo(atendimentoSelecionado.getId(), 0);
            }
        }
    }

    public Long pegaidMovimentacaoMin(Long id) {
        sql = "select min(id) from movimentacaoAtendimento where id_atendimento = " + id + "";
        return ValidacoesBanco.retornaLong(sql, "AtendimentoDAO", "pegaidMovimentacaoMin");
    }

    public void atualizaArquivo(Long cod, int tipo) {
        if (tipo == 1) {
            sql = "UPDATE ATENDIMENTO SET ANEXOMOVIMENTO = 0 WHERE ID = " + cod + "";
        } else {
            sql = "UPDATE MOVIMENTACAOATENDIMENTO SET ANEXOMOVIMENTO = 0 WHERE ID = " + cod + "";
        }
        ValidacoesBanco.update(sql, "AtendimentoDAO", "atualizaArquivo");
    }

    public void salvarMovimento(Atendimento finalizarAtendimento, ArquivoUpload aMov, MovimentacaoAtendimento movimentarAtendimento, boolean opcaoTecnico, Long cod_tecnico, boolean ativaSuprimento) {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        modeloEmail = new ModeloEmail();
        finalizarAtendimento.setAtualizadoHa(movimentarAtendimento.getDataMovimento());
        new GenericDAO<>(Atendimento.class).update(finalizarAtendimento);
        MovimentacaoProduto mp = new MovimentacaoProduto();
        if (aMov.isEnviado()) {
            movimentarAtendimento.setAnexoMovimento(aMov.isEnviado());
            movimentarAtendimento.setCod_arquivo(aMov.getId());
            new GenericDAO<>(ArquivoUpload.class).salvar(aMov);
        }
        if (movimentarAtendimento.isStatusMovimento()) {
            if (opcaoTecnico) {
                mudarTecnico(movimentarAtendimento);
                Usuario t = new Usuario();
                t.setId(cod_tecnico);
                movimentarAtendimento.setId_tecnico(t);
                new GenericDAO<>(MovimentacaoAtendimento.class).salvar2(movimentarAtendimento);
                modeloEmail.sendMailMov(movimentarAtendimento);
                //movimentarAtendimento = new MovimentacaoAtendimento();
                FacesUtil.addMsgInfo("Sucesso", "Chamado Movimentado com Sucesso!");
            } else {
                new GenericDAO<>(MovimentacaoAtendimento.class).salvar2(movimentarAtendimento);
                modeloEmail.sendMailMov(movimentarAtendimento);
                //movimentarAtendimento = new MovimentacaoAtendimento();
                FacesUtil.addMsgInfo("Sucesso", "Chamado Movimentado com Sucesso!");
            }
        } else {
            if (opcaoTecnico) {
                mudarTecnico(movimentarAtendimento);
                Usuario t = new Usuario();
                t.setId(cod_tecnico);
                movimentarAtendimento.setId_tecnico(t);
                new GenericDAO<>(MovimentacaoAtendimento.class).salvar2(movimentarAtendimento);
                modeloEmail.sendMailMov(movimentarAtendimento);
                finalizarAtendimento.setHoraFechamento(movimentarAtendimento.getHoraMovimento());
                finalizarAtendimento.setDataFechamento(movimentarAtendimento.getDataMovimento());
                finalizarAtendimento.setStatusAtendimento(movimentarAtendimento.isStatusMovimento());
                finalizarAtendimento.setRating(movimentarAtendimento.getRating());
                new GenericDAO<>(Atendimento.class).update(finalizarAtendimento);
                FacesUtil.addMsgInfo("Sucesso", "Chamado Finalizado com Sucesso!");
            } else {
                new GenericDAO<>(MovimentacaoAtendimento.class).salvar2(movimentarAtendimento);
                modeloEmail.sendMailMov(movimentarAtendimento);
                finalizarAtendimento.setHoraFechamento(movimentarAtendimento.getHoraMovimento());
                finalizarAtendimento.setDataFechamento(movimentarAtendimento.getDataMovimento());
                finalizarAtendimento.setStatusAtendimento(movimentarAtendimento.isStatusMovimento());
                finalizarAtendimento.setRating(movimentarAtendimento.getRating());
                new GenericDAO<>(Atendimento.class).update(finalizarAtendimento);
                FacesUtil.addMsgInfo("Sucesso", "Chamado Finalizado com Sucesso!");
            }
        }

        if (movimentarAtendimento.getCod_produto() != null && (ativaSuprimento && parametros.getCodSuprimentoCompra() == finalizarAtendimento.getCod_tipoAtendimento())) {
            mp.setCod_atendimento(finalizarAtendimento.getId());
            mp.setCod_produto(movimentarAtendimento.getCod_produto());
            mp.setTipoBaixa("+");
            mp.setQtdInserida(movimentarAtendimento.getQtdProduto());
            mp.setId_movimento(movimentarAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        } else if (movimentarAtendimento.getCod_produto() != null && (ativaSuprimento && parametros.getCodSuprimentoTroca() == finalizarAtendimento.getCod_tipoAtendimento())) {
            mp.setCod_atendimento(finalizarAtendimento.getId());
            mp.setCod_produto(movimentarAtendimento.getCod_produto());
            mp.setTipoBaixa("-");
            mp.setQtdInserida(movimentarAtendimento.getQtdProduto());
            mp.setId_movimento(movimentarAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        } else if (movimentarAtendimento.getCod_produto() != null && (ativaSuprimento && parametros.getCodPerdaRouboVencimento() == finalizarAtendimento.getCod_tipoAtendimento())) {
            mp.setCod_atendimento(finalizarAtendimento.getId());
            mp.setCod_produto(movimentarAtendimento.getCod_produto());
            mp.setTipoBaixa("*");
            mp.setQtdInserida(movimentarAtendimento.getQtdProduto());
            mp.setId_movimento(movimentarAtendimento.getId());
            new GenericDAO<>(MovimentacaoProduto.class).salvar(mp);
        }
    }

    public void mudarTecnico(MovimentacaoAtendimento ma) {
        sql = "UPDATE atendimento set id_tecnico =" + ma.getId_tecnico().getId() + " where id = " + ma.getId_atendimento().getId() + "";
        ValidacoesBanco.update(sql, "AtendimentoDAO", "mudarTecnico");
    }

    public void atualizaEquipamento(Atendimento a) {
        sql = "update Equipamento set cod_status =" + a.getCod_status() + "  where id =  " + a.getCod_equipamento() + "";
        ValidacoesBanco.update(sql, "AtendimentoDAO", "atualizaEquipamento");
    }

    public Equipamento filtraStatus(Atendimento atendimento) {
        Equipamento eq = new Equipamento();
        sql = "SELECT * FROM EQUIPAMENTO where id=" + atendimento.getCod_equipamento() + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                eq.setId(rs.getLong("id"));
                eq.setNome(rs.getString("nome"));
                eq.setCod_Status(rs.getLong("cod_status"));
                eq.setCod_usrResp(rs.getLong("Cod_usrResp"));
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
            validaEquipamento(atendimento);
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDAO", "filtraStatus");
            System.out.println(" public void filtraStatus() Erro:" + e);
        }
        return eq;
    }

    public boolean validaEquipamento(Atendimento atendimento) {
        sql = "SELECT * FROM atendimento where cod_equipamento=" + atendimento.getCod_equipamento() + " and statusAtendimento = 1 and cod_negocio=" + cod_negocio + "  and cod_tipoAtendimento <> 2 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                long i = rs.getLong("cod_equipamento");
                System.out.println(i);
                if (i == 178L || i == 0) {
                    //statusBotao = false;
                    ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    stmt = null;
                    ConexaoSQL = null;
                    sql = null;
                    return true;
                } else {
                    FacesUtil.addMsgWarn("Atenção", "Equipamento já está em atendimento");
                    //statusBotao = false;
                    ConexaoSQL.close();
                    rs.close();
                    stmt.close();
                    stmt = null;
                    ConexaoSQL = null;
                    sql = null;
                    return true;
                }

            } else {
                //statusBotao = false;
                ConexaoSQL.close();
                rs.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return true;
            }

        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AtendimentoDAO", "validaEquipamento");
            //statusBotao = true;
            return false;
        }
    }

    public void atualizaSaldoProduto(double saldo, Long id, long cod_produto) {
        sql = "UPDATE MovimentacaoProduto SET qtdInserida =" + saldo + ", cod_produto =" + cod_produto + "  where cod_atendimento = " + id + " ";
        ValidacoesBanco.update(sql, "AtendimentoDAO", "atualizaSaldoProduto SQL 1");
        sql = "UPDATE MovimentacaoAtendimento SET qtdProduto =" + saldo + ", cod_produto =" + cod_produto + "  where id = " + pegaId(id) + " ";
        ValidacoesBanco.update(sql, "AtendimentoDAO", "atualizaSaldoProduto SQL 2");

    }

    public Long pegaId(Long id) {
        sql = " SELECT MIN(ID) FROM MovimentacaoAtendimento WHERE id_atendimento = " + id + "";
        return ValidacoesBanco.retornaLong(sql, "AtendimentoDAO", "pegaId");
    }

    public void editarObsMov(Atendimento a) {
        String x = a.getObs().replaceAll("[']", "");
        if (a.isAnexoMovimento()) {
            sql = "UPDATE MovimentacaoAtendimento SET OBSERVACAO ='" + x + "', AnexoMovimento = 1 where id = " + pegaId(a.getId()) + " ";
        } else {
            sql = "UPDATE MovimentacaoAtendimento SET OBSERVACAO ='" + x + "', AnexoMovimento = 0 where id = " + pegaId(a.getId()) + " ";
        }
        ValidacoesBanco.update(sql, "AtendimentoDAO", "editarObsMov");
    }

    public void editarAtendimento(Atendimento editaAtendimento, Usuario usuario, Usuario tecnico,
            boolean ativaSuprimento, List<Long> apagarLista, ArquivoUpload a) {
        data = new Date(System.currentTimeMillis());
        parametros = new GenericDAO<>(Parametros.class).parametro();
        cod_user = (Long) session.getAttribute("idUser");
        editaAtendimento.setId_solicitante(usuario);
        editaAtendimento.setId_tecnico(tecnico);
        if (ativaSuprimento && parametros.getCodSuprimentoCompra() == editaAtendimento.getCod_tipoAtendimento()) {
            atualizaSaldoProduto(editaAtendimento.getQtdProduto(), editaAtendimento.getId(), editaAtendimento.getCod_produto());
        } else if (ativaSuprimento && parametros.getCodSuprimentoTroca() == editaAtendimento.getCod_tipoAtendimento()) {
            atualizaSaldoProduto(editaAtendimento.getQtdProduto(), editaAtendimento.getId(), editaAtendimento.getCod_produto());
        } else if (ativaSuprimento && parametros.getCodPerdaRouboVencimento() == editaAtendimento.getCod_tipoAtendimento()) {
            atualizaSaldoProduto(editaAtendimento.getQtdProduto(), editaAtendimento.getId(), editaAtendimento.getCod_produto());
        }
        if (!editaAtendimento.getListaAcompanhante().isEmpty()) {
            ValidacoesBanco.salvarAcompanhantes(Acompanhar.class.getSimpleName(), editaAtendimento.getListaAcompanhante(), editaAtendimento.getId(), cod_user, data);
        }
        ValidacoesBanco.removerUsuariosAcompanhar(Acompanhar.class.getSimpleName(), editaAtendimento.getListaAcompanhante(), apagarLista, editaAtendimento.getId(), cod_user, data);

        if (a.isEnviado()) {
            editaAtendimento.setAnexoMovimento(true);
        }

        editarObsMov(editaAtendimento);
        new GenericDAO<>(Atendimento.class).update(editaAtendimento);
    }

    public List<Long> acompanhantes(Long id) {
        sql = "SELECT * FROM " + Acompanhar.class.getSimpleName() + " WHERE cod_atendimento =" + id + " and status = 1";
        return ValidacoesBanco.acompanhantes(sql, "AtendimentoDAO", "acompanhantes");
    }

}
