package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Aniversariantes;
import Modelo.Campos;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.FacesUtil;
import util.Util;

@ManagedBean
@ViewScoped
@FacesValidator(value = "validarEmail")
public class AniversarianteBean implements Serializable, Validator {

    private Aniversariantes niver = new Aniversariantes();
    private Aniversariantes niverSelecionado = new Aniversariantes();
    private Aniversariantes editaNiver = new Aniversariantes();
    String sql;
    PreparedStatement stmt;
    ResultSet rs;
    private List<Boolean> list;
    private Connection ConexaoSQL;
    private List<Campos> empresa = new ArrayList<>();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    private List<Aniversariantes> aniversariantes = new ArrayList<>();
    private List<Aniversariantes> todosAniversariantes = new ArrayList<>();
    Util util = new Util();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    boolean mostraTodos = true;
    private String nome = "Próximos Aniversários do Mês";
    private String nomeBotao = "Todos os Aniversariantes do Mês";
    String codigo_negocio = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("negocio");

    public AniversarianteBean() {
        if (cod_negocio == null) {
            cod_negocio = Long.parseLong(codigo_negocio);
        }
        aniversariantesDoDia();
        if (cod_negocio == 1L) {
            importar();
        }
        empresa = new GenericDAO<>(Campos.class).listarCamposJDBC(16L);
        list = Arrays.asList(true, true, true);

    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void mostraTodos() {
        if (mostraTodos) {
            nomeBotao = "Próximos Aniversáriantes do Mês";
            nome = "Todos os Aniversáriantes do Mês";
            allAniversariantes();
            mostraTodos = false;
        } else {
            nomeBotao = "Todos os Aniversariantes do Mês";
            nome = "Próximos Aniversáriantes do Mês";
            mostraTodos = true;
            aniversariantesDoDia();
        }
    }

    public void allAniversariantes() {
        List<Aniversariantes> result = new ArrayList();
        sql = "SELECT DISTINCT empresa, dataAniversario,nome FROM Aniversariantes WHERE SUBSTRING(dataAniversario,4,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2)";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Aniversariantes a = new Aniversariantes();
                a.setEmpresa(rs.getString("EMPRESA"));
                a.setDataAniversario(rs.getString("DataAniversario"));
                a.setNome(rs.getString("NOME"));
                result.add(a);
            }
            aniversariantes = result;
            result = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AniversarianteBean", "allAniversariantes");
            System.out.println("public void allAniversariantes() Erro:" + e);
        }
    }

    public void aniversariantesDoDia() {
        List<Aniversariantes> result = new ArrayList();
        sql = " select distinct a.cod_negocio,a.dataAniversario,a.empresa,a.nome from Aniversariantes a "
                + " where substring(dataAniversario,4,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) and"
                + " substring(dataAniversario,1,2)>= SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),9,2)";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Aniversariantes a = new Aniversariantes();
                a.setEmpresa(rs.getString("EMPRESA"));
                a.setDataAniversario(rs.getString("DataAniversario"));
                a.setNome(rs.getString("NOME"));
                result.add(a);
            }
            aniversariantes = result;
            rs.close();
            stmt.close();
            ConexaoSQL.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
            result = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AniversarianteBean", "aniversariantesDoDia");
            System.out.println(" public void aniversariantesDoDia() Erro:" + e);
        }
    }

    public boolean verificaAniversariante(Aniversariantes a) {
        try {
            sql = "SELECT cod_negocio FROM ANIVERSARIANTES WHERE NOME = '" + a.getNome() + "' and empresa ='" + a.getEmpresa() + "'";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                rs.close();
                stmt.close();
                ConexaoSQL.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return true;
            } else {
                rs.close();
                stmt.close();
                ConexaoSQL.close();
                rs = null;
                ConexaoSQL = null;
                stmt = null;
                sql = null;
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AniversarianteBean", "verificaAniversariante");
            System.out.println("public boolean verificaAniversariante(Aniversariantes a) Erro:" + e);
            return false;
        }
    }

    public void salvar() {
        if (verificaAniversariante(niver)) {
            new GenericDAO<>(Aniversariantes.class).salvar(niver);
            niver = new Aniversariantes();
        } else {
            FacesUtil.addMsgError("Aniversariante já Cadastrado!", mErro);
        }

    }

    public void editar(Aniversariantes a) {
        editaNiver = a;
    }

    public void editarAniversariante() {
        new GenericDAO<>(Aniversariantes.class).update(editaNiver);
        editaNiver = null;
    }

    public void importar() {
        try {
            sql = "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,5,8) AS NASCIMENTO "
                    + "FROM SRA010 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2)  AND CTT_DESC01 <> 'AUTONOMO' "
                    + " UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA020 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA030 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA040 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA050 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA060 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA070 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA080 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA090 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA100 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA110 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA120 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA130 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "UNION "
                    + "SELECT DISTINCT RA_NOME AS NOME, CTT_DESC01 AS EMPRESA, SUBSTRING(RA_NASC,7,2) +'/' + SUBSTRING(RA_NASC,5,2) AS NASCIMENTO "
                    + "FROM SRA140 AS RA "
                    + "JOIN CTT010 AS CTT ON RA.RA_CC = CTT.CTT_CUSTO AND RA_SITFOLH <>'D' "
                    + "WHERE SUBSTRING(RA_NASC,5,2) = SUBSTRING(CAST(CONVERT (date, GETDATE()) AS VARCHAR),6,2) AND CTT_DESC01 <> 'AUTONOMO' "
                    + "order by NASCIMENTO, NOME, EMPRESA";
            Connection ConexaoSQLMP10 = new ConexaoSQLServer().getConnectionAniversario();
            PreparedStatement stmt = ConexaoSQLMP10.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            Aniversariantes a = new Aniversariantes();
            while (rs.next()) {
                a.setEmpresa(rs.getString("EMPRESA"));
                a.setDataAniversario(rs.getString("NASCIMENTO"));
                a.setNome(rs.getString("NOME"));
                if (!verificaAniversariante(a)) {
                    new GenericDAO<>(Aniversariantes.class).salvar(a);
                }
                a = new Aniversariantes();
            }
            a = null;
            rs.close();
            stmt.close();
            ConexaoSQLMP10.close();
            rs = null;
            ConexaoSQLMP10 = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AniversarianteBean", "importar");
            System.out.println("public void importar() Erro:" + e);
        }
    }

    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String email = String.valueOf(value);
        boolean valid = true;
        if (value == null) {
            valid = false;
        } else if (!email.contains("@")) {
            valid = false;
        } else if (!email.contains(".")) {
            valid = false;
        } else if (email.contains(" ")) {
            valid = false;
        }
        if (!valid) {
            FacesUtil.addMsgError("Email invalido!", "Erro");
            //throw new ValidatorException();
        }
    }

    public List<Aniversariantes> getAniversariantes() {
        return aniversariantes;
    }

    public void setAniversariantes(List<Aniversariantes> aniversariantes) {
        this.aniversariantes = aniversariantes;
    }

    public Aniversariantes getNiver() {
        return niver;
    }

    public void setNiver(Aniversariantes niver) {
        this.niver = niver;
    }

    public List<Campos> getEmpresa() {
        return empresa;
    }

    public void setEmpresa(List<Campos> empresa) {
        this.empresa = empresa;
    }

    public Aniversariantes getNiverSelecionado() {
        return niverSelecionado;
    }

    public void setNiverSelecionado(Aniversariantes niverSelecionado) {
        this.niverSelecionado = niverSelecionado;
    }

    public Aniversariantes getEditaNiver() {
        return editaNiver;
    }

    public void setEditaNiver(Aniversariantes editaNiver) {
        this.editaNiver = editaNiver;
    }

    public List<Aniversariantes> getTodosAniversariantes() {
        return todosAniversariantes;
    }

    public void setTodosAniversariantes(List<Aniversariantes> todosAniversariantes) {
        this.todosAniversariantes = todosAniversariantes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeBotao() {
        return nomeBotao;
    }

    public void setNomeBotao(String nomeBotao) {
        this.nomeBotao = nomeBotao;
    }
}
