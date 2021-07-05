/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.AcompanharGeneric;
import java.io.Serializable;
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

/**
 *
 * @author ricardo
 * @param <T>
 */
public class AcompanharDAO<T>  implements Serializable {

    static PreparedStatement stmt;
    static ResultSet rs;
    static String sql;
    private static Connection ConexaoSQL;        
    private final Class<T> classe;

    public AcompanharDAO(Class<T> classe) {
        this.classe = classe;
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

    public void salvarAcompanhantes(T t, List<Long> listaUsuarios, Long id, Long cod_user, Date data) {

        AcompanharGeneric ac = (AcompanharGeneric) t;
        for (int i = 0; i < listaUsuarios.size(); i++) {
            ac.setCod_usuario(listaUsuarios.get(i));
            ac.setCod_tabela(id);
            ac.setCod_userInseriu(cod_user);
            ac.setDataInclusao(data);
            ac.setStatus(true);
            if (!verificarAcompanhante(t, ac.getCod_tabela(), ac.getCod_usuario())) {
                new GenericDAO<>(AcompanharGeneric.class).salvar(ac);
            }
            ac = new AcompanharGeneric();
        }

    }

    public boolean verificarAcompanhante(T t, Long id, Long codUsuario) {

        sql = "SELECT * FROM " + t.getClass().getName() + " WHERE cod_tabela =" + id + " and cod_usuario =" + codUsuario + " and status = 1";
        return ValidacoesBanco.retornaBoolean(sql,
                "ValidacoesBanco", "verificarAcompanhante");
    }

    public void removerUsuariosAcompanhar(T t, List<Long> listaUsuarios, List<Long> apagarUsuarios, Long id, Long cod_user, Date data) {
        for (int x = 0; x < listaUsuarios.size(); x++) {
            for (int z = 0; z < apagarUsuarios.size(); z++) {
                if (Objects.equals(listaUsuarios.get(x), apagarUsuarios.get(z))) {
                    apagarUsuarios.remove(apagarUsuarios.get(z));
                }
            }
        }
        if (!apagarUsuarios.isEmpty()) {
            for (Long cod_usuario_remover : apagarUsuarios) {
                excluirUsuario(t, id, cod_user, cod_usuario_remover, data);
            }
        }
    }

    public void excluirUsuario(T t, Long id, Long cod_user, Long cod_usuario_remover, Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        sql = " update " + t.getClass().getName() + " set status = 0, cod_userRemoveu =" + cod_user + ", dataRemocao='" + sdf.format(data) + "' where cod_tabela =" + id + " and status = 1 and cod_usuario =" + cod_usuario_remover + " ";

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

}
