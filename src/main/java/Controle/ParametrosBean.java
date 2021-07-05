package Controle;

import DAO.GenericDAO;
import Modelo.Campos;
import Modelo.Parametros;
import Modelo.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import util.FacesUtil;
import util.Util;

/**
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class ParametrosBean implements Serializable {

    private Parametros parametro = new Parametros();
    private Parametros parametroEdit = new Parametros();
    String mErro = "ERRO", mSucesso = "SUCESSO", mInfor = "INFORMAÇÃO";
    Util util = new Util();
    private Parametros parametroSelecionado = new Parametros();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private List<Campos> tipoAtendimento = new ArrayList<>();
    private List<Campos> tabelas = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();

    public ParametrosBean() {
        parametro = new GenericDAO<>(Parametros.class).parametro();
        tipoAtendimento = new GenericDAO<>(Campos.class).listarCamposJDBC(15L);
        usuarios = new GenericDAO<>(Usuario.class).listarUsuarios();
        listaTabelas();
    }

    public void listaTabelas() {
        String sql = "select c from Campos c where c.tabela = 0 and c.cod_negocio = " + cod_negocio + "";
        tabelas = new GenericDAO<>(Campos.class).listarTodosSQL(sql);
    }

    public void salvar() {
        new GenericDAO<>(Parametros.class).update(parametro);
        FacesUtil.addMsgInfo(mSucesso, "Salvo com Sucesso!!");
    }

    public Parametros getParametro() {
        return parametro;
    }

    public void setParametro(Parametros parametro) {
        this.parametro = parametro;
    }

    public Parametros getParametroEdit() {
        return parametroEdit;
    }

    public void setParametroEdit(Parametros parametroEdit) {
        this.parametroEdit = parametroEdit;
    }

    public Parametros getParametroSelecionado() {
        return parametroSelecionado;
    }

    public void setParametroSelecionado(Parametros parametroSelecionado) {
        this.parametroSelecionado = parametroSelecionado;
    }

    public List<Campos> getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(List<Campos> tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Campos> getTabelas() {
        return tabelas;
    }

    public void setTabelas(List<Campos> tabelas) {
        this.tabelas = tabelas;
    }

}
