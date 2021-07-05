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
import org.primefaces.PrimeFaces;
import util.FacesUtil;
import util.ModeloEmail;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class RespostaBean implements Serializable {

    private List<Pergunta> items;
    private List<Resposta> respostas = new ArrayList<>();
    private String resposta[];
    private String[] respostaObs;
    Pergunta p;
    PerguntaOpcao po;
    private Formulario formulario = new Formulario();
    private String opcao;
    String formulario_id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("formulario_id");
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    PreparedStatement stmt2;
    ResultSet rs2;
    String sql2;
    private Connection ConexaoSQL;
    ModeloEmail me = new ModeloEmail();

    public RespostaBean() {
        formulario = new GenericDAO<>(Formulario.class).formulario(Long.parseLong(formulario_id));
        items = new ArrayList<>();
        p = new Pergunta();
        po = new PerguntaOpcao();
        listaPerguntas();
        resposta = new String[items.size()];
        respostaObs = new String[items.size()];
    }

    public void listaPerguntas() {
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

    public void salvar() {
        Resposta r = new Resposta();
        Long id_resposta = id_resposta() + 1L;
        for (int i = 0; i < items.size(); i++) {
            r.setResposta(resposta[i]);
            r.setId_pergunta(items.get(i).getId());
            r.setId_resposta(id_resposta);
            r.setObsResposta(respostaObs[i]);
            new GenericDAO<>(Resposta.class).salvar(r);
            r = new Resposta();
        }
        if (formulario.isEnviaEmail()) {
            formulario.setResposta_id(id_resposta);
            me.sendMailFormulario(formulario);
        }
        FacesUtil.addMsgInfo("Sucesso", "Enviado com Sucesso!");
//        resposta = new String[items.size()];
//        PrimeFaces.current().ajax().update("form_pesq");
//        PrimeFaces.current().executeScript("PF('statusDialog').hide()");
        PrimeFaces.current().executeScript("location.reload();");
    }

    public Long id_resposta() {
        sql = " select max(r.id_resposta) from resposta r "
                + " join pergunta p on r.id_pergunta = p.id "
                + " where p.id_formulario = " + formulario_id + "";
        Long i;
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                i = rs.getLong(1);
            } else {
                i = 0L;
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
            return null;
        }
        return i;
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
