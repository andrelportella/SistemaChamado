
import Conexao.ConexaoSQLServer;
import Conexao.JPAUtil;
import DAO.GenericDAO;
import Modelo.AcompanharReserva;
import Modelo.Pergunta;
import Modelo.Resposta;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import util.FacesUtil;
import util.ValidacoesBanco;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ricardo
 */
public class TesteJDBC<T> {

    static PreparedStatement stmt;
    static ResultSet rs;
    static String sql;
    private static Connection ConexaoSQL;
    static List<String> perguntas2 = new ArrayList<>();
    static String[] perguntas;

    public static void main(String[] args) {
        x();
    }

    public static void x() {
        System.out.println("Classe Nome: " + AcompanharReserva.class.getName());
        System.out.println("Classe Nome: " + AcompanharReserva.class.getSimpleName());
        System.out.println("Classe Nome: " + AcompanharReserva.class.getTypeName());
        System.out.println("Classe Nome: " + AcompanharReserva.class.getCanonicalName());
    }

}
