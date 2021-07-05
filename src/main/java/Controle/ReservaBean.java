package Controle;

import Conexao.ConexaoSQLServer;
import DAO.GenericDAO;
import Modelo.AcompanharReserva;
import Modelo.Campos;
import Modelo.Reserva;
import Modelo.Usuario;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import util.FacesUtil;
import util.ModeloEmail;
import util.ValidacoesBanco;

@ViewScoped
@ManagedBean
public class ReservaBean implements Serializable {

    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Reserva reserva = new Reserva();
    private Reserva reservaSelecionada = new Reserva();
    private Reserva editaReserva = new Reserva();
    private List<Reserva> reservas = new ArrayList<>();
    private Connection ConexaoSQL;
    private List<Campos> salas = new ArrayList<>();
    private List<Usuario> solicitantes = new ArrayList<>();
    private List<Usuario> participantes = new ArrayList<>();
    private List<Boolean> list;
    List<Long> apagarLista = new ArrayList<>();
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    ModeloEmail modeloEmail = new ModeloEmail();
    Date data = new Date(System.currentTimeMillis());
    Long cod_user = (Long) session.getAttribute("idUser");
    private String nomeCampo;
    // SETADO COMO 2 PARA VIM AS RESERVAS DO SOLICITANTE
    int campooTabela = 2;
    private String nomeTabela;

    public ReservaBean() {
        //lista todos os solicitantes
        solicitantes = new GenericDAO<>(Usuario.class).listarUsuarios();
        //lista todos os partipantes
        participantes = new GenericDAO<>(Usuario.class).listarUsuarios();
        //lista todos os recursos
        salas = new GenericDAO<>(Campos.class).listarCamposJDBC(27L);
        //lista as reservas
        listaReservas();
        //padrão de exibição dos campos da página HTML no Datatable
        list = Arrays.asList(true, true, true, true, true, true, true);
    }

    public void reservasParticipante() {
        this.campooTabela = 3;
        listaReservas();
    }

    public void reservasTodos() {
        this.campooTabela = 1;
        listaReservas();
    }

    public void reservasSomenteEu() {
        this.campooTabela = 2;
        listaReservas();
    }

    //função para exibir ou ocultar os campos da Datatable
    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public boolean validaHora() {
        //formata a data para o padrão do banco de dados
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        //formata a hora para o padrão do banco de dados
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
        stf.setLenient(false);
        //Verifica se já existe alguma reserva no mesmo dia e horários que está sendo feito na reserva atual
        sql = "SELECT * FROM Reserva where dataReserva ='" + sdf.format(reserva.getDataReserva()) + "' "
                + " and ((horaFim >='" + stf.format(reserva.getHoraInicio()) + "' and indeterminado = 0) "
                + " or (horaInicio <='" + stf.format(reserva.getHoraInicio()) + "'"
                + " and indeterminado = 1)) and cod_recurso =" + reserva.getCod_recurso() + "";

        return ValidacoesBanco.retornaBoolean(sql, "ReservaBean", "validaHora()");
    }

    public void listaReservas() {
        List<Reserva> campos = new ArrayList<>();
        switch (campooTabela) {
            //Mostra todas as reservas
            case 1:
                sql = " SELECT recurso.descricao as sala, u.nome as solicitante , r.* from Reserva r "
                        + " JOIN Usuario u on r.cod_solicitante = u.id and u.cod_negocio = " + cod_negocio + " "
                        + " JOIN Campos recurso on r.cod_recurso = recurso.cod and recurso.tabela = 27 and recurso.cod_negocio = " + cod_negocio + ""
                        + " order by status, dataReserva ";
                nomeCampo = "Mostrar Suas Reservas";
                nomeTabela = "Todas as Reservas";
                campooTabela = 2;
                break;
            //Mostra as reservas que o usuario solicitou
            case 2:
                sql = " SELECT recurso.descricao as sala, u.nome as solicitante , r.* from Reserva r "
                        + " JOIN Usuario u on r.cod_solicitante = u.id and u.cod_negocio = " + cod_negocio + " "
                        + " JOIN Campos recurso on r.cod_recurso = recurso.cod and recurso.tabela = 27 and recurso.cod_negocio = " + cod_negocio + ""
                        + " where r.cod_solicitante = " + cod_user + " "
                        + "order by status, dataReserva ";
                nomeCampo = "Mostrar Todas as Reservas";
                nomeTabela = "Suas Reservas";
                campooTabela = 1;
                break;
            //Mostra as reservas que o usuário está participando
            case 3:
                sql = " SELECT recurso.descricao as sala, u.nome as solicitante , r.* from Reserva r "
                        + " JOIN Usuario u on r.cod_solicitante = u.id and u.cod_negocio = " + cod_negocio + " "
                        + " JOIN Campos recurso on r.cod_recurso = recurso.cod and recurso.tabela = 27 and recurso.cod_negocio = " + cod_negocio + ""
                        + " JOIN AcompanharReserva ar on ar.cod_reserva = r.id "
                        + " where ar.cod_usuario = " + cod_user + ""
                        + " group by recurso.descricao, u.nome, R.ID, cod_solicitante, R.cod_negocio, R.COD_RECURSO, dataReserva, horaFim,horaInicio, indeterminado, observacao, R.status,cod_sala ";
                nomeTabela = "Reservas Que Estou Participando";
                nomeCampo = "Mostrar Suas Reservas";
                campooTabela = 2;
                break;
            default:
                break;
        }
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            Reserva m = new Reserva();
            while (rs.next()) {
                m.setId(rs.getLong("id"));
                m.setCod_recurso(rs.getLong("cod_recurso"));
                m.setCod_solicitante(rs.getLong("cod_solicitante"));
                m.setDataReserva(rs.getDate("dataReserva"));
                m.setHoraInicio(rs.getTime("horaInicio"));
                m.setHoraFim(rs.getTime("horaFim"));
                m.setObservacao(rs.getString("observacao"));
                m.setIndeterminado(rs.getBoolean("indeterminado"));
                m.setStatus(rs.getBoolean("status"));
                m.setRecurso(rs.getString(1));
                m.setSolicitante(rs.getString(2));
                campos.add(m);
                m = new Reserva();
            }
            this.reservas = campos;
            campos = null;
            m = null;
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            stmt = null;
            ConexaoSQL = null;
            sql = null;
        } catch (SQLException | ClassNotFoundException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "ReservaBean", "listaReservas");
            System.out.println("public void listaReservas() Erro:" + e);
        }
    }

    public void save() {
        if (!validaHora()) {
            new GenericDAO<>(Reserva.class).salvar2(reserva);
            //verifica se a lista de acompanhantes está vazia
            if (!reserva.getListaAcompanhante().isEmpty()) {
                //salva todos os acompanhantes marcados
                ValidacoesBanco.salvarAcompanhantes(AcompanharReserva.class.getSimpleName(), reserva.getListaAcompanhante(), reserva.getId(), cod_user, data);
            }
            // lista as reservas do solicitante
            campooTabela = 2;
            //lista todas as reservas
            listaReservas();
            //envia o email para o solicitante e participantes
            modeloEmail.sendMailReserva(reserva);
            //fecha o dialog de cadastro
            org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
            //emite a mensagem de sucesso
            FacesUtil.addMsgInfo("Sucesso", "Reservado com sucesso!");
            // limpa o objeto criado
            reserva = new Reserva();

        } else {
            //emite a mensagem de erro
            FacesUtil.addMsgError("Erro", "Já existe uma reserva marcada nesta DATA e HORÁRIO que termina depois de começar a reserva atual!");
        }
    }

    public void salvar() {
        //verifica se o horario final é maior que o horario inicial
        if (reserva.isIndeterminado()) {
            save();
        } else {
            if (reserva.getHoraFim().getTime() > reserva.getHoraInicio().getTime()) {
                save();
            } else {
                //Emite a mensagem de erro
                FacesUtil.addMsgError("Erro", "O horário final não pode ser maior que o inicial!");
            }
        }
    }
    // pega as informações da reserva na pagina html

    public void editar(Reserva reserva) {
        this.editaReserva = reserva;
        editaReserva.setListaAcompanhante(acompanhantes(editaReserva.getId()));
        apagarLista = editaReserva.getListaAcompanhante();
    }

    //finaliza a reserva
    public void finalizar(Reserva reserva) {
        sql = "UPDATE reserva set status = 1 where id = " + reserva.getId() + "";
        //executa a ação no banco
        ValidacoesBanco.update(sql, "ReservaBean", "finalizar");
        // lista as reservas do solicitante
        campooTabela = 2;
        //lista as reserva
        listaReservas();
    }

    public void editarReserva() {
        //verifica se a lista está vazia
        if (!editaReserva.getListaAcompanhante().isEmpty()) {
            //verifica se adicionou algum participante na lista, se adicionou ele salva no banco.
            ValidacoesBanco.salvarAcompanhantes(AcompanharReserva.class
                    .getSimpleName(), editaReserva.getListaAcompanhante(), editaReserva.getId(), cod_user, data);

        }
        //verifica se removeu algum participante da lista, se removeu, ele apaga do banco.
        ValidacoesBanco.removerUsuariosAcompanhar(AcompanharReserva.class
                .getSimpleName(), editaReserva.getListaAcompanhante(), apagarLista, editaReserva.getId(), cod_user, data);

        //atualiza a resera se alterou alguma informação dela.
        new GenericDAO<>(Reserva.class
        ).update(editaReserva);
        // lista as reservas do solicitante
        campooTabela = 2;
        //lista as reservas
        listaReservas();
        //fechar o dialog de editar
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        //emite a mensagem de edição
        FacesUtil.addMsgInfo("Sucesso", "Editado com sucesso!");
        //limpa o objeto
        editaReserva = new Reserva();
    }

    //lista todos os participantes da reserva.
    public List<Long> acompanhantes(Long id) {
        sql = "SELECT * FROM AcompanharReserva WHERE cod_tabela =" + id + " and status = 1";
        return ValidacoesBanco.acompanhantes(sql, "ReservaBean", "acompanhantes");
    }

    //GETTERS E SETTERES
    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Reserva getReservaSelecionada() {
        return reservaSelecionada;
    }

    public void setReservaSelecionada(Reserva reservaSelecionada) {
        this.reservaSelecionada = reservaSelecionada;
    }

    public Reserva getEditaReserva() {
        return editaReserva;
    }

    public void setEditaReserva(Reserva editaReserva) {
        this.editaReserva = editaReserva;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public List<Campos> getSalas() {
        return salas;
    }

    public void setSalas(List<Campos> salas) {
        this.salas = salas;
    }

    public List<Usuario> getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(List<Usuario> solicitantes) {
        this.solicitantes = solicitantes;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

}
