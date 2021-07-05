package Conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import util.FacesUtil;

public class ConexaoSRVSistema {

    private final String usr = "renato";
    private final String senha = "gcm@1234";
    private final String servidor = "srvsistema:3306";
    private final String dataBase = "bahiamarina";

    public Connection getConnection() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + servidor + "/" + dataBase + "", usr, senha);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Falha ao Conectar ao Servidor da Base de Dados: " + servidor + " \n \n" + e, "ERRO", JOptionPane.ERROR_MESSAGE, null);
            FacesUtil.addMsgFatalSQL(e, "Erro", "ConexaoSRVSistema", "getConnection");
            throw new RuntimeException("Error :( \n " + e);

        }
    }

    public String getUsr() {
        return usr;
    }

    public String getSenha() {
        return senha;
    }

    public String getServidor() {
        return servidor;
    }

    public String getDataBase() {
        return dataBase;
    }

}
