package Controle;

import Conexao.ConexaoSQLServer;
import Modelo.Formulario;
import Modelo.Pergunta;
import Modelo.PerguntaOpcao;
import Modelo.Resposta;
import Modelo.RespostaFormulario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import util.FacesUtil;

@ManagedBean
public class SlideBean {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    String dataDe = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dataDe");
    String dataAte = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dataAte");
    String formulario_id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("formulario_id");
    private Connection ConexaoSQL;
    private List<Pergunta> items;
    private List<Resposta> respostas = new ArrayList<>();

    private List<String> avaliados = new ArrayList<>();
    private List<Long> qtdAvaliacao = new ArrayList<>();

    private List<String> avaliados2 = new ArrayList<>();
    private List<Double> media = new ArrayList<>();

    private List<RespostaFormulario> pioresNotas = new ArrayList<>();

    private String resposta[];
    Pergunta p;
    PerguntaOpcao po;
    private Formulario formulario = new Formulario();
    PreparedStatement stmt2;
    ResultSet rs2;
    String sql2;
    private String capa;
    private String fundoSlide;

    public SlideBean() {
        carregarImagens();
        p = new Pergunta();
        po = new PerguntaOpcao();
        items = new ArrayList<>();
        listaPerguntas();
        listarAvaliados();
        mediaFrentista();

    }

    public void carregarImagens() {
        switch (formulario_id) {
            case "1":
                capa = "../../resources/imagens/PNB.png";
                fundoSlide = "../../resources/imagens/fundoPesquisaPNB.png";
                break;
            case "2":
                capa = "../../resources/imagens/LOJA.png";
                fundoSlide = "../../resources/imagens/fundoPesquisaLOJA.png";
                break;
            default:
                capa = "";
                fundoSlide = "";
                break;
        }
    }

    public void listaPerguntas() {
        sql = "select * from pergunta where id_formulario = " + formulario_id + " and visualizaSlide = 1 ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            int i = 0, y = 0;
            while (rs.next()) {
                p.setId(rs.getLong("id"));
                p.setId_formulario(rs.getLong("id_formulario"));
                p.setOrdem(rs.getInt("ordem"));
                p.setPergunta(rs.getString("pergunta"));
                p.setTipo(rs.getString("tipo"));
                p.setContador(i++);
                p.setObrigatorio(rs.getBoolean("Obrigatorio"));
                if (p.getTipo().equals("Multipla Escolha")) {
                    sql2 = "select * from perguntaOpcao where id_pergunta=" + p.getId() + " ";
                    stmt = ConexaoSQL.prepareStatement(sql2);
                    rs2 = stmt.executeQuery();
                    while (rs2.next()) {
                        po.setId_pergunta(p.getId());
                        po.setOpcao("'" + rs2.getString("opcao") + "'");
                        p.getOpcoes().add(po);
                        po = new PerguntaOpcao();
                    }
                    rs2.close();
                    rs2 = null;
                    sql2 = null;
                } else {
                    po.setId_pergunta(p.getId());
                    po.setOpcao("'OP'");
                    p.getOpcoes().add(po);
                    po = new PerguntaOpcao();
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
            sql = null;

        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "FormularioBean", "listaPerguntas");
            System.out.println(" public void listaPerguntas() Erro:" + e);
        }
    }

    public int listaQtdResposta(Long id_resposta, String opcao) {
        int c = 0;
        sql = " select count(*) from Pergunta p "
                + " join Resposta r "
                + " on p.id = r.id_pergunta "
                + " where p.id = " + id_resposta + " and "
                + " dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' "
                + " and resposta = '" + opcao + "'  ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c = rs.getInt(1);
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
        return c;
    }

    public int qtdTotalResposta(Long id) {
        int c = 0;
        sql = " select count(distinct id_resposta) from Pergunta p "
                + " join Resposta r "
                + " on p.id = r.id_pergunta "
                + " where p.id_formulario  = " + formulario_id + " and "
                + " dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' and "
                + " p.id =" + id + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c = rs.getInt(1);
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
        return c;
    }

    public String qtdTotalAvaliacoes() {
        int id = 0;
        String c = "AVALIAÇÕES REALIZADAS: ";
        if (formulario_id.equals("1")) {
            id = 13;
        } else if (formulario_id.equals("2")) {
            id = 23;
        }
        sql = " select count(distinct id_resposta) from Pergunta p "
                + " join Resposta r "
                + " on p.id = r.id_pergunta "
                + " where p.id_formulario  = " + formulario_id + " and "
                + " dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' and "
                + " p.id =" + id + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c = c + rs.getString(1);
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
        return c;
    }

    public double notaMediaAtendimentoTotal() {
        double c = 0;

        int id = 0;

        if (formulario_id.equals("1")) {
            id = 13;
        } else if (formulario_id.equals("2")) {
            id = 23;
        }

        sql = " select convert(decimal(6,1),(SUM(convert(decimal(6,1), resposta))/COUNT(resposta))) "
                + " from Resposta where id_pergunta =" + id + " and "
                + " dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' ";

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c = rs.getDouble(1);
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
        return c;
    }

    public String qtdTotalAvaliados() {
        int codAvaliado = 0;
        String c = "";
        if (formulario_id.equals("1")) {
            c = "FRENTISTAS AVALIADOS: ";
            codAvaliado = 2;
        } else if (formulario_id.equals("2")) {
            c = "PROMOTORAS AVALIADAS: ";
            codAvaliado = 15;
        }
        sql = " SELECT count(distinct resposta) as TOTAL "
                + " FROM RESPOSTA as R "
                + " JOIN PERGUNTA AS P ON P.ID = R.id_pergunta "
                + " where p.id_formulario = " + formulario_id + " and dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' and p.id = " + codAvaliado + " "
                + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c = c + rs.getString(1);
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
        return c;
    }

    public void listarAvaliados() {
        int codAvaliado = 0;
        if (formulario_id.equals("1")) {
            codAvaliado = 2;
        } else if (formulario_id.equals("2")) {
            codAvaliado = 15;
        }
        sql = " select resposta, COUNT(*) as total from Pergunta p "
                + " join Resposta r "
                + " on p.id = r.id_pergunta "
                + " where p.id_formulario = " + formulario_id + " and dataResposta>='" + dataDe + "' and dataResposta <='" + dataAte + "' and p.id = " + codAvaliado + " "
                + " GROUP BY resposta "
                + " order by total desc, resposta ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Resposta r = new Resposta();
            while (rs.next()) {

                avaliados.add("'" + rs.getString(1) + "'");
                qtdAvaliacao.add(rs.getLong(2));
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

    public void mediaFrentista() {
        int codAvaliado = 0;
        int codNota = 0;

        if (formulario_id.equals("1")) {
            codAvaliado = 2;
            codNota = 13;
        } else if (formulario_id.equals("2")) {
            codAvaliado = 15;
            codNota = 23;
        }

        sql = " SELECT R.RESPOSTA AS FRENTISTA, CAST( AVG(  CAST(R2.RESPOSTA AS NUMERIC (3,1)) ) AS numeric (3,1)) AS NOTA "
                + " FROM RESPOSTA as R "
                + " JOIN RESPOSTA AS R2 ON R2.id_pergunta = '" + codNota + "' AND R2.id_resposta = R.ID_RESPOSTA "
                + " JOIN PERGUNTA AS P ON P.ID = R.id_pergunta "
                + " WHERE R.id_pergunta = '" + codAvaliado + "' AND P.id_formulario = " + formulario_id + ""
                + " and R.dataResposta >='" + dataDe + "'  and R.dataResposta <='" + dataAte + "'"
                + " and R2.dataResposta >='" + dataDe + "'  and R2.dataResposta <='" + dataAte + "'"
                + " GROUP BY R.RESPOSTA "
                + " ORDER BY NOTA desc, FRENTISTA ";

        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Resposta r = new Resposta();
            while (rs.next()) {
                avaliados2.add("'" + rs.getString(1) + "'");
                media.add(rs.getDouble(2));
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

    public void pioresNotas() {
        int codAvaliado = 0;
        int codNota = 0;

        if (formulario_id.equals("1")) {
            codAvaliado = 2;
            codNota = 13;
        } else if (formulario_id.equals("2")) {
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
                + " and R.dataResposta >='" + dataDe + "'  and R.dataResposta <='" + dataAte + "'"
                + " and R2.dataResposta >='" + dataDe + "'  and R2.dataResposta <='" + dataAte + "'"
                + " GROUP BY R.RESPOSTA "
                + " ORDER BY NOTA desc, FRENTISTA ";

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

    public List<Pergunta> getItems() {
        return items;
    }

    public void setItems(List<Pergunta> items) {
        this.items = items;
    }

    public List<String> getAvaliados() {
        return avaliados;
    }

    public void setAvaliados(List<String> avaliados) {
        this.avaliados = avaliados;
    }

    public List<Long> getQtdAvaliacao() {
        return qtdAvaliacao;
    }

    public void setQtdAvaliacao(List<Long> qtdAvaliacao) {
        this.qtdAvaliacao = qtdAvaliacao;
    }

    public List<String> getAvaliados2() {
        return avaliados2;
    }

    public void setAvaliados2(List<String> avaliados2) {
        this.avaliados2 = avaliados2;
    }

    public List<Double> getMedia() {
        return media;
    }

    public void setMedia(List<Double> media) {
        this.media = media;
    }

    public String getCapa() {
        return capa;
    }

    public void setCapa(String capa) {
        this.capa = capa;
    }

    public String getFundoSlide() {
        return fundoSlide;
    }

    public void setFundoSlide(String fundoSlide) {
        this.fundoSlide = fundoSlide;
    }

    public List<RespostaFormulario> getPioresNotas() {
        return pioresNotas;
    }

    public void setPioresNotas(List<RespostaFormulario> pioresNotas) {
        this.pioresNotas = pioresNotas;
    }

}
