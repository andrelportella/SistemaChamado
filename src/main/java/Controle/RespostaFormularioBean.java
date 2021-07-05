/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Formulario;
import Modelo.Pergunta;
import Modelo.PerguntaOpcao;
import Modelo.Resposta;
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
import util.FacesUtil;
import util.ModeloEmail;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class RespostaFormularioBean implements Serializable {

    private List<Pergunta> items;
    private List<Resposta> respostas = new ArrayList<>();
    private String resposta[];
    private String[] respostaObs;
    Pergunta p;
    PerguntaOpcao po;
    private Formulario formulario = new Formulario();
    private String opcao;
    String formulario_id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("formulario_id");
    String resposta_id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("resposta_id");
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    PreparedStatement stmt2;
    ResultSet rs2;
    String sql2;
    private Connection ConexaoSQL;
    ModeloEmail me = new ModeloEmail();

    public RespostaFormularioBean() {
        formulario = new GenericDAO<>(Formulario.class).formulario(Long.parseLong(formulario_id));
        items = new ArrayList<>();
        p = new Pergunta();
        po = new PerguntaOpcao();
        listaPerguntasRespondida();
        resposta = new String[items.size()];
        respostaObs = new String[items.size()];
        listarRespostas();
    }

    public void listarRespostas() {
        sql = " select r.resposta,r.obsresposta from pergunta p "
                + " join resposta r on p.id = r.id_pergunta "
                + " where id_formulario = " + formulario_id + " and id_resposta = " + resposta_id + " "
                + " order by ordem ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                resposta[i] = rs.getString(1);
                respostaObs[i] = rs.getString(2);
                i++;
            }
            stmt.close();
            ConexaoSQL.close();
            rs.close();
            stmt = null;
            ConexaoSQL = null;
            rs = null;
            sql = null;

        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FormularioBean", "listaPerguntas");
            System.out.println(" public void listaPerguntas() Erro:" + e);
        }
    }

    public void listaPerguntasRespondida() {
        sql = "select * from pergunta where id_formulario = " + formulario_id + " order by ordem";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                p.setId(rs.getLong("id"));
                p.setId_formulario(rs.getLong("id_formulario"));
                p.setOrdem(rs.getInt("ordem"));
                p.setPergunta(rs.getString("pergunta"));
                p.setTipo(rs.getString("tipo"));
                p.setContador(i++);
                p.setObrigatorio(rs.getBoolean("Obrigatorio"));
                p.setTextoExibicao(rs.getString("TextoExibicao"));
                p.setRespostaCerta(rs.getString("respostaCerta"));
                p.setExibeTexto(rs.getBoolean("ExibeTexto"));
                if (p.getTipo().equals("Multipla Escolha")) {
                    sql2 = "select * from perguntaOpcao where id_pergunta=" + p.getId() + " ";
                    stmt = ConexaoSQL.prepareStatement(sql2);
                    rs2 = stmt.executeQuery();
                    while (rs2.next()) {
                        po.setId_pergunta(p.getId());
                        po.setOpcao(rs2.getString("opcao"));
                        p.getOpcoes().add(po);
                        po = new PerguntaOpcao();
                    }
                    rs2.close();
                }
                items.add(p);
                p = new Pergunta();
            }
            stmt.close();
            ConexaoSQL.close();
            rs.close();
            stmt = null;
            ConexaoSQL = null;
            rs = null;
            rs2 = null;
            sql = null;
            sql2 = null;
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FormularioBean", "listaPerguntas");
            System.out.println(" public void listaPerguntas() Erro:" + e);
        }
    }

    public List<Pergunta> getItems() {
        return items;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public String getOpcao() {
        return opcao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public List<Resposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }

    public String[] getResposta() {
        return resposta;
    }

    public void setResposta(String[] resposta) {
        this.resposta = resposta;
    }

    public String[] getRespostaObs() {
        return respostaObs;
    }

    public void setRespostaObs(String[] respostaObs) {
        this.respostaObs = respostaObs;
    }

}
