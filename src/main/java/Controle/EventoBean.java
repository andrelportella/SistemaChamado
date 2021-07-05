package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.AcompanharEvento;
import Modelo.Evento;
import Modelo.TipoEvento;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
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
import util.ValidacoesBanco;

@ManagedBean()
@ViewScoped
public class EventoBean implements Serializable {

    private static final long serialVersionUID = -7899310419263581187L;
    private Evento evento;
    private List<Evento> listaEvento;
    private List<Usuario> acompanhar;
    private ScheduleModel eventModel;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    Long cod_user = (Long) session.getAttribute("idUser");
    Date data = new Date(System.currentTimeMillis());
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    List<Long> apagarLista;

    public List<Evento> getListaEvento() {
        return listaEvento;

    }

    public void setListaEvento(List<Evento> listaEvento) {
        this.listaEvento = listaEvento;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public void listarEventos() {
        try {
            List<Evento> eventos = new ArrayList<>();
            Evento e = new Evento();
            sql = " SELECT E.* FROM Evento e "
                    + " LEFT JOIN AcompanharEvento ae on e.id = ae.cod_evento "
                    + " WHERE e.cod_negocio = " + cod_negocio + " and (e.codUserPrincipal = " + cod_user + " or"
                    + " ae.cod_usuario = " + cod_user + " ) and (ae.status = 1 or ae.status is null)"
                    + " GROUP BY E.ID, CODUSERPRINCIPAL, COD_NEGOCIO, DESCRICAO, INICIO, FIM, E.STATUS, TITULO, TIPOEVENTO, DIATODO ";
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                e.setId(rs.getLong("id"));
                e.setDescricao(rs.getString("descricao"));
                e.setTitulo(rs.getString("titulo"));
                e.setInicio(rs.getTimestamp("inicio"));
                e.setFim(rs.getTimestamp("fim"));
                e.setCodUserPrincipal(rs.getLong("codUserPrincipal"));
                e.setStatus(rs.getBoolean("status"));
                e.setTipoEvento(rs.getString("tipoEvento"));
                e.setDiaTodo(rs.getBoolean("diaTodo"));
                eventos.add(e);
                e = new Evento();
            }
            this.listaEvento = eventos;
            e = null;
            eventos = null;
            rs.close();
            ConexaoSQL.close();
            stmt.close();
            rs = null;
            ConexaoSQL = null;
            stmt = null;
        } catch (ClassNotFoundException | SQLException ex) {
            FacesUtil.addMsgFatalSQL(ex, "Erro ao listar!", "EventoBean", "listarEventos");
        }
    }

    @PostConstruct
    public void inicializar() {
        evento = new Evento();
        eventModel = new DefaultScheduleModel();
        acompanhar = new GenericDAO<>(Usuario.class).listarUsuarios();
        try {
            listarEventos();
        } catch (Exception e) {
            FacesUtil.addMsgError("Erro na inicialização! " + e, "Erro");
        }

        for (Evento ev : listaEvento) {
            DefaultScheduleEvent evt = new DefaultScheduleEvent();
            evt.setEndDate(ev.getFim());
            evt.setStartDate(ev.getInicio());
            evt.setTitle(ev.getTitulo());
            evt.setData(ev.getId());
            evt.setDescription(ev.getDescricao());
            evt.setAllDay(ev.isDiaTodo());
            evt.setEditable(true);
            evt.setStyleClass(ev.getTipoEvento());
            eventModel.addEvent(evt);
        }
    }

    public void quandoSelecionado(SelectEvent selectEvent) {
        ScheduleEvent auxEvent = (ScheduleEvent) selectEvent.getObject();
        for (Evento ev : listaEvento) {
            if (Objects.equals(ev.getId(), (Long) auxEvent.getData())) {
                evento = ev;
                evento.setListaAcompanhante(acompanhantes(evento.getId()));
                apagarLista = acompanhantes(evento.getId());
                break;
            }
        }
    }

    public void quandoNovo(SelectEvent selectEvent) {
        ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(),
                (Date) selectEvent.getObject());
        evento = new Evento();
        evento.setStatus(true);
        evento.setCodUserPrincipal(cod_user);
        evento.setInicio(new java.sql.Date(event.getStartDate().getTime()));
        evento.setFim(new java.sql.Date(event.getEndDate().getTime()));
        evento.setTipoEvento("padrao");
    }

    public void Salvar() {
        if (evento.getInicio().getTime() <= evento.getFim().getTime()) {
            if (evento.getId() == null) {
                try {
                    new GenericDAO<>(Evento.class).salvar2(evento);
                    if (!evento.getListaAcompanhante().isEmpty()) {
                        ValidacoesBanco.salvarAcompanhantes(AcompanharEvento.class.getSimpleName(), evento.getListaAcompanhante(), evento.getId(), cod_user, data);
                    }
                    inicializar();
                    data = new Date(System.currentTimeMillis());
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('caixaDialog').hide();");
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                    FacesUtil.addMsgInfo("Sucesso!", "Salvo com sucesso!");
                } catch (Exception e) {
                    FacesUtil.addMsgFatalSQL(e, "Erro ao Salvar!", "EventoBean", "Salvar");;
                }
                evento = new Evento();
            } else {
                try {
                    System.out.println(evento.getListaAcompanhante().size());
                    new GenericDAO<>(Evento.class).update(evento);
                    if (!evento.getListaAcompanhante().isEmpty()) {
                        ValidacoesBanco.salvarAcompanhantes(AcompanharEvento.class.getSimpleName(), evento.getListaAcompanhante(), evento.getId(), cod_user, data);
                    }
                    ValidacoesBanco.removerUsuariosAcompanhar(AcompanharEvento.class.getSimpleName(), evento.getListaAcompanhante(), apagarLista, evento.getId(), cod_user, data);
                    inicializar();
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('caixaDialog').hide();");
                    org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('statusDialog').hide();");
                    FacesUtil.addMsgInfo("Sucesso!", "Atualizado com sucesso!");
                } catch (Exception e) {
                    FacesUtil.addMsgFatalSQL(e, "Erro em Atualizar!", "EventoBean", "Salvar");
                }
            }
        } else {
            FacesUtil.addMsgError("Erro", "Erro: Data inicial não pode ser menor que Final!");
        }

    }

    public List<Long> acompanhantes(Long id) {
        sql = "SELECT * FROM AcompanharEvento WHERE cod_tabela =" + id + " and status = 1";
        return ValidacoesBanco.acompanhantes(sql, "AtendimentoBean", "acompanhantes");
    }

    public void excluir() {
        try {
            new GenericDAO<>(Evento.class).update(evento);
            inicializar();
        } catch (Exception e) {
            FacesUtil.addMsgFatalSQL(e, "Erro ao excluir!", "EventoBean", "excluir");
        }
    }

    public void quandoMovido(ScheduleEntryMoveEvent event) {

        for (Evento ev : listaEvento) {
            if (Objects.equals(ev.getId(), (Long) event.getScheduleEvent().getData())) {
                evento = ev;
                try {
                    new GenericDAO<>(Evento.class).update(evento);
                    inicializar();
                } catch (Exception e) {
                    FacesUtil.addMsgFatalSQL(e, "Erro ao mover!", "EventoBean", "quandoMovido");
                }
                break;
            }

        }
    }

    public void quandoRedimencionado(ScheduleEntryResizeEvent event) {

        for (Evento ev : listaEvento) {
            if (Objects.equals(ev.getId(), (Long) event.getScheduleEvent().getData())) {
                evento = ev;
                try {
                    new GenericDAO<>(Evento.class).update(evento);
                    inicializar();
                } catch (Exception e) {
                    FacesUtil.addMsgFatalSQL(e, "Erro ao redimencionar!", "EventoBean", "quandoRedimencionado");
                }
                break;
            }

        }
    }

    public TipoEvento[] getTiposEventos() {
        return TipoEvento.values();
    }

    public List<Usuario> getAcompanhar() {
        return acompanhar;
    }

    public void setAcompanhar(List<Usuario> acompanhar) {
        this.acompanhar = acompanhar;
    }
}
