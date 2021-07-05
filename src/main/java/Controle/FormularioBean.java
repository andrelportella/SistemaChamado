/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.AcompanharFormulario;
import Modelo.Formulario;
import Modelo.Pergunta;
import Modelo.PerguntaOpcao;
import Modelo.Resposta;
import Modelo.RespostaFormulario;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.FacesUtil;
import util.ValidacoesBanco;

/**
 *
 * @author ricardo
 */
@ViewScoped
@ManagedBean
public class FormularioBean implements Serializable {

    private List<Formulario> formularios = new ArrayList<>();
    private List<Pergunta> perguntas = new ArrayList<>();
    private List<Resposta> respostas = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private Formulario formulario = new Formulario();
    private Formulario editaformulario;
    private Formulario formularioSelecionado = new Formulario();
    private Pergunta pergunta = new Pergunta();
    Long pOpcao;
    private Pergunta editaPergunta = new Pergunta();
    private Pergunta p = new Pergunta();
    Formulario f = new Formulario();
    private PerguntaOpcao pO = new PerguntaOpcao();
    private PerguntaOpcao pOEdit = new PerguntaOpcao();
    private List<PerguntaOpcao> opcoes = new ArrayList<>();
    private List<Boolean> list;
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    private String perg;
    private Date dataDe;
    private Date dataAte;
    Long formulario_id;
    Resposta[] r;
    private String[] perguntas2;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_user = (Long) session.getAttribute("idUser");
    Date data = new Date(System.currentTimeMillis());
    List<Long> apagarLista = new ArrayList<>();
    private List<RespostaFormulario> pioresNotas = new ArrayList<>();

    public FormularioBean() {
        list = Arrays.asList(true, true, true, true);
        listarFormularios();
        usuarios = new GenericDAO<>(Usuario.class).listarUsuarios();
    }

    public void gerarSlide() {
        PrimeFaces.current().executeScript("window.open('/SistemaChamadoBM/slides/select/pesquisa.xhtml?dataDe=" + getFormatDataDe() + "&dataAte=" + getFormatDataAte() + "&formulario_id=" + formulario_id + "')");
    }

    
    
    public void gerarSlidePioresNotas() {
        pioresNotasMensal();
        //PrimeFaces.current().ajax().update("campos");
        //PrimeFaces.current().executeScript("PF('statusDialog').hide()");
    }

    public void pioresNotasMensal() {
        int codAvaliado = 0;
        int codNota = 0;

        if (formulario_id == 1) {
            codAvaliado = 2;
            codNota = 13;
        } else if (formulario_id == 2) {
            codAvaliado = 15;
            codNota = 23;
        }

        sql = " SELECT "
                + " R.RESPOSTA AS FRENTISTA, "
                + " MIN(CAST(R2.RESPOSTA AS NUMERIC (3,1))) AS NOTA, "
                + " MAX(CAST(R.id_resposta AS bigint)) as ID_RESPOSTA "
                + " FROM RESPOSTA as R "
                + " JOIN RESPOSTA AS R2 ON R2.id_pergunta = '" + codNota + "' AND R2.id_resposta = R.ID_RESPOSTA "
                + " JOIN PERGUNTA AS P ON P.ID = R.id_pergunta "
                + " WHERE R.id_pergunta = '" + codAvaliado + "' AND P.id_formulario = " + formulario_id + ""
                + " and R.dataResposta >='" + getFormatDataDe() + "'  and R.dataResposta <='" + getFormatDataAte() + "'"
                + " and R2.dataResposta >='" + getFormatDataDe() + "'  and R2.dataResposta <='" + getFormatDataAte() + "'"
                + " and r.resposta is not null "
                + " GROUP BY R.RESPOSTA "
                + " ORDER BY NOTA, FRENTISTA ";
        System.out.println(sql);
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            RespostaFormulario r = new RespostaFormulario();
            pioresNotas = new ArrayList<>();
            while (rs.next()) {
                r.setNome(rs.getString(1));
                r.setNota(rs.getDouble(2));
                r.setId_resposta(rs.getLong(3));
                pioresNotas.add(r);
                r = new RespostaFormulario();
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

    public void onRowReorder(ReorderEvent event) {
        Pergunta pOrdem = new Pergunta();
        for (int i = 0; i < perguntas.size(); i++) {
            pOrdem.setId(perguntas.get(i).getId());
            pOrdem.setOrdem(i + 1);
            reordarPerguntas(pOrdem);
            pOrdem = new Pergunta();
        }
        listaPerguntas(formularioSelecionado.getId(), formularioSelecionado.getTitulo());
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

    public void selecionaSlide(Long formulario_id) {
        this.formulario_id = formulario_id;
    }

    public void selecionaFormulario(Long formulario_id) {
        listarRespostas(formulario_id);
    }

    public void listarRespostas(Long formulario_id) {
        List<Resposta> resp = new ArrayList<>();
        sql = " select p.pergunta,r.resposta,r.dataResposta,r.horaResposta,r.id_resposta from Resposta r "
                + " join Pergunta p on r.id_pergunta = p.id "
                + " where p.id_formulario = " + formulario_id + " "
                + " order by id_resposta, ordem, dataResposta ";

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Resposta r = new Resposta();
            while (rs.next()) {
                r.setPergunta(rs.getString(1));
                r.setResposta(rs.getString(2));
                r.setDataResposta(rs.getDate(3));
                r.setHoraResposta(rs.getTime(4));
                r.setId_resposta(rs.getLong(5));
                resp.add(r);
                r = new Resposta();
            }
            respostas = resp;
            stmt.close();
            ConexaoSQL.close();
            rs.close();
            stmt = null;
            ConexaoSQL = null;
            rs = null;
            sql = null;

        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FormularioBean", "listaRespostas");

        }

    }

    public void listarFormularios() {
        formularios = new GenericDAO<>(Formulario.class).listarTodos();
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void editar(Formulario f) {
        editaformulario = f;
        editaformulario.setListaAcompanhante(acompanhantes(editaformulario.getId()));
        apagarLista = editaformulario.getListaAcompanhante();
    }

    public void editarPergunta(Pergunta p) {
        editaPergunta = p;
    }

    public void excluirPergunta(Pergunta p) {
        excluirOpcoes(p);

        new GenericDAO<>(Pergunta.class
        ).excluir(p);
        listaPerguntas(this.p.getId_formulario(), p.getDescricao());
        FacesUtil.addMsgInfo("Sucesso", "Pergunta excluida com sucesso!");
        PrimeFaces.current().ajax().update("formInsere:insereCampos");
    }

    public void excluirOpcoes(Pergunta p) {
        sql = "DELETE PerguntaOpcao WHERE id_pergunta = " + p.getId() + " ";
        ValidacoesBanco.update(sql, "FormularioBean", "excluirOpcoes");
    }

    public void reordarPerguntas(Pergunta p) {
        sql = "update Pergunta set ordem =" + p.getOrdem() + "  where id = " + p.getId() + " ";
        ValidacoesBanco.update(sql, "FormularioBean", "reordarPerguntas");
    }

    public void adicionarOpcao(Pergunta p) {
        pOpcao = p.getId();
        perg = p.getPergunta();
        listarOpcao();
    }

    public void listarOpcao() {
        sql = "select c from PerguntaOpcao c where c.id_pergunta = " + pOpcao + "";
        opcoes
                = new GenericDAO<>(PerguntaOpcao.class
                ).listarTodosSQL(sql);
    }

    public void salvarOpcao() {
        pO.setId_pergunta(pOpcao);

        new GenericDAO<>(PerguntaOpcao.class
        ).salvar(pO);
        pO = new PerguntaOpcao();
        listarOpcao();
        PrimeFaces.current().ajax().update("opcaoPergDetal");
        PrimeFaces.current().ajax().update("grid");
        FacesUtil.addMsgInfo("Sucesso", "Opção salva com sucesso!");
    }

    public void salvarEdicaoOpcao() {
        new GenericDAO<>(PerguntaOpcao.class
        ).update(pOEdit);
        listarOpcao();
        PrimeFaces.current().ajax().update("opcaoPergDetal");
        PrimeFaces.current().ajax().update("grid");
        PrimeFaces.current().executeScript("PF('editarPergOpcaoDialog').hide()");
        FacesUtil.addMsgInfo("Sucesso", "Opção editada com sucesso!");
    }

    public void editarOpcao(PerguntaOpcao op) {
        pOEdit = op;

    }

    public List<Long> acompanhantes(Long id) {
        sql = "SELECT * FROM AcompanharFormulario WHERE cod_tabela =" + id + " and status = 1";
        return ValidacoesBanco.acompanhantes(sql, "AtendimentoBean", "acompanhantes");
    }

    public void excluirOpcao(PerguntaOpcao op) {
        new GenericDAO<>(PerguntaOpcao.class
        ).excluir(op);
        listarOpcao();
        listarOpcao();
        PrimeFaces.current().ajax().update("opcaoPergDetal");
        PrimeFaces.current().ajax().update("grid");
        FacesUtil.addMsgInfo("Sucesso", "Opção excluida com sucesso!");
    }

    public void salvarEdicaoPergunta() {
        new GenericDAO<>(Pergunta.class
        ).update(editaPergunta);
        editaPergunta = new Pergunta();
        listaPerguntas(p.getId_formulario(), p.getDescricao());
        FacesUtil.addMsgInfo("Sucesso", "Pergunta editada com sucesso!");
        PrimeFaces.current().ajax().update("formInsere:insereCampos");
        PrimeFaces.current().executeScript("PF('editarPergDialog').hide()");
    }

    public void listaPerguntas(Long id_formulario, String descricao) {
        p.setId_formulario(id_formulario);
        p.setDescricao(descricao);
        sql = "select c from Pergunta c where c.id_formulario = " + id_formulario + " order by c.ordem ";
        perguntas = new GenericDAO<>(Pergunta.class).listarTodosSQL(sql);
    }

    public void exibeTexto() {

    }

    public void editarFormulario() {
        new GenericDAO<>(Formulario.class
        ).update(editaformulario);
        if (!editaformulario.getListaAcompanhante().isEmpty()) {
            ValidacoesBanco.salvarAcompanhantes(AcompanharFormulario.class.getSimpleName(), editaformulario.getListaAcompanhante(), editaformulario.getId(), cod_user, data);
        }
        ValidacoesBanco.removerUsuariosAcompanhar(AcompanharFormulario.class.getSimpleName(), editaformulario.getListaAcompanhante(), apagarLista, editaformulario.getId(), cod_user, data);
        editaformulario = new Formulario();
        listarFormularios();
        FacesUtil.addMsgInfo("Editar", "Formulário editado com sucesso!");
        PrimeFaces.current().executeScript("PF('editarDialog').hide();");
    }

    public void salvar() {
        new GenericDAO<>(Formulario.class
        ).salvar2(formulario);
        if (!formulario.getListaAcompanhante().isEmpty()) {
            ValidacoesBanco.salvarAcompanhantes(AcompanharFormulario.class.getSimpleName(), formulario.getListaAcompanhante(), formulario.getId(), cod_user, data);
        }
        formulario = new Formulario();
        listarFormularios();
        FacesUtil.addMsgInfo("Sucesso", "Formulário cadastrado com sucesso!");
        PrimeFaces.current().executeScript("PF('cadastrarDialog').hide();");
    }

    public int ordemPergunta(Long id_formulario) {
        sql = "select MAX(ordem) from Pergunta where id_formulario =" + id_formulario + " ";
        return (ValidacoesBanco.retornaInt(sql, "FormularioBean", "ordemPergunta") + 1);
    }

    public void salvarPergunta() {
        pergunta.setId_formulario(p.getId_formulario());
        pergunta.setOrdem(ordemPergunta(p.getId_formulario()));

        new GenericDAO<>(Pergunta.class
        ).salvar(pergunta);
        pergunta = new Pergunta();
        listaPerguntas(p.getId_formulario(), p.getDescricao());
        FacesUtil.addMsgInfo("Sucesso", "Pergunta cadastrada com sucesso!");
        PrimeFaces.current().ajax().update("formInsere:insereCampos");
    }

    public List<Formulario> getFormularios() {
        return formularios;
    }

    public void setFormularios(List<Formulario> formularios) {
        this.formularios = formularios;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public Formulario getEditaformulario() {
        return editaformulario;
    }

    public void setEditaformulario(Formulario editaformulario) {
        this.editaformulario = editaformulario;
    }

    public Formulario getFormularioSelecionado() {
        return formularioSelecionado;
    }

    public void setFormularioSelecionado(Formulario formularioSelecionado) {
        this.formularioSelecionado = formularioSelecionado;
    }

    public List<Pergunta> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(List<Pergunta> perguntas) {
        this.perguntas = perguntas;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }

    public Pergunta getEditaPergunta() {
        return editaPergunta;
    }

    public void setEditaPergunta(Pergunta editaPergunta) {
        this.editaPergunta = editaPergunta;
    }

    public PerguntaOpcao getpO() {
        return pO;
    }

    public void setpO(PerguntaOpcao pO) {
        this.pO = pO;
    }

    public List<PerguntaOpcao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<PerguntaOpcao> opcoes) {
        this.opcoes = opcoes;
    }

    public PerguntaOpcao getpOEdit() {
        return pOEdit;
    }

    public void setpOEdit(PerguntaOpcao pOEdit) {
        this.pOEdit = pOEdit;
    }

    public Pergunta getP() {
        return p;
    }

    public void setP(Pergunta p) {
        this.p = p;
    }

    public String getPerg() {
        return perg;
    }

    public void setPerg(String perg) {
        this.perg = perg;
    }

    public Date getDataDe() {
        return dataDe;
    }

    public void setDataDe(Date dataDe) {
        this.dataDe = dataDe;
    }

    public Date getDataAte() {
        return dataAte;
    }

    public void setDataAte(Date dataAte) {
        this.dataAte = dataAte;
    }

    public String[] getPerguntas2() {
        return perguntas2;
    }

    public void setPerguntas2(String[] perguntas2) {
        this.perguntas2 = perguntas2;
    }

    public List<Resposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<RespostaFormulario> getPioresNotas() {
        return pioresNotas;
    }

    public void setPioresNotas(List<RespostaFormulario> pioresNotas) {
        this.pioresNotas = pioresNotas;
    }

}
