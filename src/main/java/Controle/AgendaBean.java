/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.Agenda;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.FacesUtil;
import util.ModeloEmail;

/**
 *
 * @author ricardo
 */
@ManagedBean
@ViewScoped
public class AgendaBean implements Serializable {

    private ScheduleModel eventModel = new DefaultScheduleModel();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Agenda agenda = new Agenda();
    private Agenda agendaSelecionada = new Agenda();
    private Agenda editaAgenda = new Agenda();
    private List<Agenda> agendas = new ArrayList<>();
    private Connection ConexaoSQL;
    private List<Usuario> participantes = new ArrayList<>();
    private List<Boolean> list;
    List<Long> apagarLista = new ArrayList<>();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    ModeloEmail modeloEmail = new ModeloEmail();
    Date data = new Date(System.currentTimeMillis());
    Long cod_user = (Long) session.getAttribute("idUser");
    private ScheduleEvent event = new DefaultScheduleEvent();

    public AgendaBean() {
        listarAgenda();
    }

    public void listarAgenda() {
        ScheduleModel em = new DefaultScheduleModel();
        sql = "SELECT * FROM AGENDA where cod_negocio =" + cod_negocio + " ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            DefaultScheduleEvent a = new DefaultScheduleEvent();
            while (rs.next()) {
                a.setDescription(rs.getString("descricao"));
                a.setStartDate(rs.getDate("dataDe"));
                a.setEndDate(rs.getDate("dataAte"));
                a.setTitle(rs.getString("titulo"));
                a.setId(rs.getString("id"));
                a.setAllDay(rs.getBoolean("diaTodo"));
                em.addEvent(a);
                a = new DefaultScheduleEvent();
            }
            this.eventModel = em;
            em = null;
            a = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
            //System.out.println(new ConexaoSQLServer().getConnection().isClosed());
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "AgendaBean", "listarAgenda");
            System.out.println("public void listarAgenda() Erro:" + e);
        }
    }

    public void addEvent() {
        if (agenda.getId() == null) {
            new GenericDAO<>(Agenda.class).salvar2(agenda);
            DefaultScheduleEvent a = new DefaultScheduleEvent();
            a.setId(agenda.getId().toString());
            a.setAllDay(agenda.isDiaTodo());
            a.setDescription(agenda.getDescricao());
            a.setStartDate(agenda.getDataDe());
            a.setEndDate(agenda.getDataAte());
            a.setTitle(agenda.getTitulo());
            eventModel.addEvent(a);
        } else {
            new GenericDAO<>(Agenda.class).update(agenda);
            DefaultScheduleEvent ab = new DefaultScheduleEvent();
            ab.setId(agenda.getId().toString());
            ab.setAllDay(agenda.isDiaTodo());
            ab.setDescription(agenda.getDescricao());
            ab.setStartDate(agenda.getDataDe());
            ab.setEndDate(agenda.getDataAte());
            ab.setTitle(agenda.getTitulo());
            eventModel.updateEvent(ab);
        }
        event = new DefaultScheduleEvent();
        agenda = new Agenda();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        //agenda.setId(Long.parseLong(event.getId()));
//        agenda.setDataDe(event.getStartDate());
//        agenda.setDataAte(event.getEndDate());
//        agenda.setDiaTodo(event.isAllDay());
//        agenda.setDescricao(event.getDescription());
//        agenda.setTitulo(event.getTitle());
//        System.out.println("Clicou nesse aqui");
//        System.out.println(agenda.getTitulo());
//        System.out.println(agenda.getDescricao());
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        System.out.println("Clicou nesse");
        //agenda.setId(Long.parseLong(event.getId()));
//        agenda.setDataDe(event.getStartDate());
//        agenda.setDataAte(event.getEndDate());
//        agenda.setDiaTodo(event.isAllDay());
//        agenda.setDescricao(event.getDescription());
//        agenda.setTitulo(event.getTitle());
//        System.out.println(agenda.getTitulo());
//        System.out.println(agenda.getDescricao());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesUtil.addMsgInfo("Mover", "Dia " + event.getDayDelta() + ", Minuto " + event.getMinuteDelta());
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesUtil.addMsgInfo("Maximizar", "Dia " + event.getDayDelta() + ", Minuto " + event.getMinuteDelta());
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public Agenda getAgendaSelecionada() {
        return agendaSelecionada;
    }

    public void setAgendaSelecionada(Agenda agendaSelecionada) {
        this.agendaSelecionada = agendaSelecionada;
    }

    public Agenda getEditaAgenda() {
        return editaAgenda;
    }

    public void setEditaAgenda(Agenda editaAgenda) {
        this.editaAgenda = editaAgenda;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

}
