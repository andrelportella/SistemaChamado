/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Noticia;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class PaginaNoticiaBean {

    String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
    String diretorio = "C:\\SistemaChamadoBM\\";
    String pasta = "anexosChamado";
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    private Noticia visualizaNoticia = new Noticia();

    public PaginaNoticiaBean() throws ClassNotFoundException, SQLException {
        verNoticia();
    }

    public Noticia verNoticia() throws ClassNotFoundException, SQLException {
        Noticia n = new Noticia();
        sql = "select u.nome as Autor, n.* from Noticia n "
                + "JOIN Usuario u on u.id = n.cod_autor "
                + "where n.id =" + id + " ";
        ConexaoSQL = new ConexaoSQLServer().getConnection();
        stmt = ConexaoSQL.prepareStatement(sql);
        rs = stmt.executeQuery();

        while (rs.next()) {
            n.setAutor(rs.getString(1));
            n.setId(rs.getLong("id"));
            n.setDataPublicacao(rs.getDate("dataPublicacao"));
            n.setDescricao(rs.getString("descricao"));
            n.setTitulo(rs.getString("titulo"));
            n.setNomeArquivo(rs.getString("NomeArquivo"));
        }
        ConexaoSQL.close();
        rs.close();
        stmt.close();
        rs = null;
        ConexaoSQL = null;
        stmt = null;
        sql = null;
        return visualizaNoticia = n;
    }

    public Noticia getVisualizaNoticia() {
        return visualizaNoticia;
    }

    public void setVisualizaNoticia(Noticia visualizaNoticia) {
        this.visualizaNoticia = visualizaNoticia;
    }
}
