package Conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.FacesUtil;

public class ConexaoSQLServer {

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try {
            return DriverManager.getConnection(
                    "jdbc:sqlserver://srvsql:1433;databaseName=CONTROLE", "siga", "siga");
        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ConexaoSQLServer", "getConnection");
            throw new RuntimeException(e);

        }
    }

    public Connection getConnectionAniversario() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try {
            return DriverManager.getConnection(
                    "jdbc:sqlserver://srvsql:1433;databaseName=MP10", "siga", "siga");
        } catch (SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ConexaoSQLServer", "getConnectionAniversario");
            throw new RuntimeException(e);
        }
    }

}
