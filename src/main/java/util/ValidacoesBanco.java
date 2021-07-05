/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Acompanhar;
import Modelo.AcompanharEvento;
import Modelo.AcompanharFormulario;
import Modelo.AcompanharReserva;
import Modelo.Parametros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class ValidacoesBanco {

    static PreparedStatement stmt;
    static ResultSet rs;
    static String sql;
    private static Connection ConexaoSQL;
    static FacesContext fc = FacesContext.getCurrentInstance();
    static HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    static Long cod_negocio = (Long) session.getAttribute("idNeg");

    public static boolean retornaBoolean(String query, String classe, String metodo) {
        try {
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return true;
            } else {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return false;
            }
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public static boolean retornaBoolean Erro:" + e);
            return false;
        }
    }

    public static String retornaString(String query, String classe, String metodo) {
        String retorno = "";
        try {
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getString(1);
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            } else {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            }
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public static boolean retornaBoolean Erro:" + e);
            return retorno;
        }
    }

    public static Long retornaLong(String query, String classe, String metodo) {
        Long retorno = 0L;
        try {
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getLong(1);
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            } else {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            }
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public static boolean retornaBoolean Erro:" + e);
            return retorno;
        }
    }

    public static double retornaDouble(String query, String classe, String metodo) {
        double retorno = 0;
        try {
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getDouble(1);
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            } else {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            }
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public static boolean retornaBoolean Erro:" + e);
            return retorno;
        }
    }

    public static int retornaInt(String query, String classe, String metodo) {
        int retorno = 0;
        try {
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = rs.getInt(1);
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            } else {
                rs.close();
                ConexaoSQL.close();
                stmt.close();
                stmt = null;
                ConexaoSQL = null;
                sql = null;
                return retorno;
            }
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public static boolean retornaBoolean Erro:" + e);
            return retorno;
        }
    }

    public static List<Long> acompanhantes(String query, String classe, String metodo) {
        try {
            List<Long> cod = new ArrayList<>();
            sql = query;
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cod.add(rs.getLong("cod_usuario"));
            }
            rs.close();
            ConexaoSQL.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
            return cod;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println("public List<Long> acompanhantes(Long id) Erro:" + e);
            return null;
        }
    }

    public static void salvarAcompanhantes(String tipo, List<Long> listaUsuarios, Long id, Long cod_user, Date data) {
        switch (tipo) {
            case "Acompanhar":
                Acompanhar ac = new Acompanhar();
                for (int i = 0; i < listaUsuarios.size(); i++) {
                    ac.setCod_usuario(listaUsuarios.get(i));
                    ac.setCod_tabela(id);
                    ac.setCod_userInseriu(cod_user);
                    ac.setDataInclusao(data);
                    ac.setStatus(true);
                    if (!verificarAcompanhante(tipo, ac.getCod_tabela(), ac.getCod_usuario())) {
                        new GenericDAO<>(Acompanhar.class).salvar(ac);
                    }
                    ac = new Acompanhar();
                }
                break;
            case "AcompanharReserva":
                AcompanharReserva ar = new AcompanharReserva();
                for (int i = 0; i < listaUsuarios.size(); i++) {
                    ar.setCod_usuario(listaUsuarios.get(i));
                    ar.setCod_tabela(id);
                    ar.setCod_userInseriu(cod_user);
                    ar.setDataInclusao(data);
                    ar.setStatus(true);
                    if (!verificarAcompanhante(tipo, ar.getCod_tabela(), ar.getCod_usuario())) {
                        new GenericDAO<>(AcompanharReserva.class).salvar(ar);
                    }
                    ar = new AcompanharReserva();
                }
                break;
            case "AcompanharEvento":
                AcompanharEvento ag = new AcompanharEvento();
                for (int i = 0; i < listaUsuarios.size(); i++) {
                    ag.setCod_usuario(listaUsuarios.get(i));
                    ag.setCod_tabela(id);
                    ag.setCod_userInseriu(cod_user);
                    ag.setDataInclusao(data);
                    ag.setStatus(true);
                    if (!verificarAcompanhante(tipo, ag.getCod_tabela(), ag.getCod_usuario())) {
                        new GenericDAO<>(AcompanharEvento.class).salvar(ag);
                    }
                    ag = new AcompanharEvento();
                }
                break;
            case "AcompanharFormulario":
                AcompanharFormulario af = new AcompanharFormulario();
                for (int i = 0; i < listaUsuarios.size(); i++) {
                    af.setCod_usuario(listaUsuarios.get(i));
                    af.setCod_tabela(id);
                    af.setCod_userInseriu(cod_user);
                    af.setDataInclusao(data);
                    af.setStatus(true);
                    if (!verificarAcompanhante(tipo, af.getCod_tabela(), af.getCod_usuario())) {
                        new GenericDAO<>(AcompanharFormulario.class).salvar(af);
                    }
                    af = new AcompanharFormulario();
                }
                break;
        }
    }

    public static boolean verificarAcompanhante(String classe, Long id, Long codUsuario) {
        sql = "SELECT * FROM " + classe + " WHERE cod_tabela =" + id + " and cod_usuario =" + codUsuario + " and status = 1";
        return ValidacoesBanco.retornaBoolean(sql, "ValidacoesBanco", "verificarAcompanhante");
    }

    public static void removerUsuariosAcompanhar(String tabela, List<Long> listaUsuarios, List<Long> apagarUsuarios, Long id, Long cod_user, Date data) {
        for (int x = 0; x < listaUsuarios.size(); x++) {
            for (int z = 0; z < apagarUsuarios.size(); z++) {
                if (Objects.equals(listaUsuarios.get(x), apagarUsuarios.get(z))) {
                    apagarUsuarios.remove(apagarUsuarios.get(z));
                }
            }
        }
        if (!apagarUsuarios.isEmpty()) {
            for (Long cod_usuario_remover : apagarUsuarios) {
                excluirUsuario(tabela, id, cod_user, cod_usuario_remover, data);
            }
        }
    }

    public static void excluirUsuario(String tabela, Long id, Long cod_user, Long cod_usuario_remover, Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        sql = " update " + tabela + " set status = 0, cod_userRemoveu =" + cod_user + ", dataRemocao='" + sdf.format(data) + "' where cod_tabela =" + id + " and status = 1 and cod_usuario =" + cod_usuario_remover + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ValidacoesBanco", "excluirSoftware");
            System.out.println(" public static void excluirUsuario(int tipo, Long id, Long cod_user, Long cod_usuario_remover, Date data) Erro:" + e);
        }
    }

    public static void update(String query, String classe, String metodo) {
        sql = query;
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", classe, metodo);
            System.out.println(" public static void update Erro:" + e);
        }
    }

}
