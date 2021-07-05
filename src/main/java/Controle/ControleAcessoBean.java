package Controle;

import DAO.GenericDAO;
import Modelo.ControleAcesso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import util.FacesUtil;

@ManagedBean
@ViewScoped
public class ControleAcessoBean implements Serializable {

    private List<ControleAcesso> perfis = new ArrayList();
    private ControleAcesso perfil = new ControleAcesso();
    private ControleAcesso editaPerfil = new ControleAcesso();
    private ControleAcesso perfilSelecionado = new ControleAcesso();

    public ControleAcessoBean() {
        perfis = new GenericDAO<>(ControleAcesso.class).listarPerfis();
    }

    public List<ControleAcesso> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<ControleAcesso> perfis) {
        this.perfis = perfis;
    }

    public ControleAcesso getPerfil() {
        return perfil;
    }

    public void setPerfil(ControleAcesso perfil) {
        this.perfil = perfil;
    }

    public ControleAcesso getEditaPerfil() {
        return editaPerfil;
    }

    public void setEditaPerfil(ControleAcesso editaPerfil) {
        this.editaPerfil = editaPerfil;
    }

    public ControleAcesso getPerfilSelecionado() {
        return perfilSelecionado;
    }

    public void setPerfilSelecionado(ControleAcesso perfilSelecionado) {
        this.perfilSelecionado = perfilSelecionado;
    }

    public void salvar() {
        new GenericDAO<>(ControleAcesso.class).salvar(perfil);
        perfil = new ControleAcesso();
        perfis = new GenericDAO<>(ControleAcesso.class).listarPerfis();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Perfil cadastrado com sucesso!");
    }

    public void editar(ControleAcesso r) {
        editaPerfil = r;
    }

    public void editarPerfil() {
        new GenericDAO<>(ControleAcesso.class).update(editaPerfil);
        editaPerfil = new ControleAcesso();
        perfis = new GenericDAO<>(ControleAcesso.class).listarPerfis();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        FacesUtil.addMsgInfo("Sucesso", "Perfil editado com sucesso!");
    }

}
