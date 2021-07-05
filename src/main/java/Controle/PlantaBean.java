/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import DAO.GenericDAO;
import Modelo.Planta;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.FacesUtil;

/**
 *
 * @author ricardo
 */
@ViewScoped
@ManagedBean
public class PlantaBean implements Serializable {

    private List<Planta> plantas = new ArrayList<>();
    private Planta planta = new Planta();
    private Planta editaPlanta;
    private Planta plantaSelecionado = new Planta();
    private List<Boolean> list;
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

    public PlantaBean() {
        list = Arrays.asList(true, true, true, true);
        listarPlantas();
    }

    public void gerarSlide() {
        PrimeFaces.current().executeScript("window.open('')");
    }
 

    public void listarPlantas() {
        plantas = new GenericDAO<>(Planta.class).listarTodos();
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void editar(Planta p) {
        editaPlanta = p;
    }


    public void editarPlanta() {
        new GenericDAO<>(Planta.class
        ).update(editaPlanta);
        editaPlanta = new Planta();
        listarPlantas();
        FacesUtil.addMsgInfo("Editar", "Planta editada com sucesso!");
        PrimeFaces.current().executeScript("PF('editarDialog').hide();");
    }

    public void salvar() {
        new GenericDAO<>(Planta.class
        ).salvar2(planta);
        planta = new Planta();
        listarPlantas();
        FacesUtil.addMsgInfo("Sucesso", "Planta cadastrada com sucesso!");
        PrimeFaces.current().executeScript("PF('cadastrarDialog').hide();");
    }

    public List<Planta> getPlantas() {
        return plantas;
    }

    public void setPlantas(List<Planta> plantas) {
        this.plantas = plantas;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Planta getEditaPlanta() {
        return editaPlanta;
    }

    public void setEditaPlanta(Planta editaPlanta) {
        this.editaPlanta = editaPlanta;
    }

    public Planta getPlantaSelecionado() {
        return plantaSelecionado;
    }

    public void setPlantaSelecionado(Planta plantaSelecionado) {
        this.plantaSelecionado = plantaSelecionado;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }


}
