package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Parametros;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import util.FacesUtil;

@ManagedBean
@ViewScoped
public class HomeBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    //long codUser = (Long) session.getAttribute("idTec");
    long codTec = (Long) session.getAttribute("idTec");
    Parametros parametros = new Parametros();

    public HomeBean() throws ClassNotFoundException, SQLException {
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        parametros = new GenericDAO<>(Parametros.class).parametro();
        Agendados();
        Atrasado();
        Aberto();
        Finalizados();
        System.out.println("Passou aqui");
    }

    public String Atrasado() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and DATEDIFF (DAY,atualizadoHa,GETDATE()) > 2 and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "HomeBean", "Atrasado");
            System.out.println("public String Atrasado() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados em atraso.";
        } else {
            return "Você não tem chamado em atraso.";
        }

    }

    public String Agendados() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and cod_tipoAtendimento = " + parametros.getCodPreventiva() + " and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "HomeBean", "Agendados");
            System.out.println(" public String Agendados() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados agendados.";
        } else {
            return "Você não tem chamado agendado.";
        }

    }

    public String Aberto() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + ""
                + "and id_tecnico = " + codTec + " and cod_tipoAtendimento <> " + parametros.getCodPreventiva() + " and statusAtendimento = 1"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "HomeBean", "Aberto");
            System.out.println(" public String Aberto() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados em aberto.";
        } else {
            return "Você não tem chamados em aberto.";
        }

    }

    public String Finalizados() {
        int tam = 0;
        sql = "SELECT count(id) FROM atendimento where cod_negocio = " + cod_negocio + " "
                + " and id_tecnico = " + codTec + "  and statusAtendimento = 0"
                + "";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                tam = rs.getInt(1);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;

        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "HomeBean", "Finalizados");
            System.out.println("  public String Finalizados() Erro:" + e);
        }
        if (tam != 0) {
            return "Você tem " + tam + " chamados finalizados.";
        } else {
            return "Você não tem chamado finalizados.";
        }
    }

}
