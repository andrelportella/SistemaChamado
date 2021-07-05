package Modelo;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ControleAcesso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String tipoPerfil;

    private boolean acessaUsuario;
    private boolean acessaTecnico;
    private boolean acessaFornecedores;
    private boolean acessaCampos;
    private boolean acessaEquipamentos;
    private boolean acessaItens;
    private boolean acessaAtendimentos;
    private boolean acessaAtendimentosAdm;
    private boolean acessaChamados;
    private boolean acessaChamadosAdm;
    private boolean acessaChamadosAgendados;
    private boolean acessaRequerimentos;
    private boolean acessaInformatica;
    private boolean acessaAlertas;
    private boolean acessaPainelADM;
    private boolean acessaEmbarcacao;
    private boolean acessaCliente;
    private boolean acessaLocal;
    private boolean acessaNagios;
    private boolean acessaParametros;
    private boolean acessaRequerente;
    private boolean acessaBaseConhecimento;
    private boolean acessaTodosChamados;
    private boolean acessaTodosRequerimentos;
    private boolean acessaReserva;
    private boolean acessaAgenda;
    private boolean acessaTodasReservas;
    private boolean acessaEquipInfor;
    private boolean acessaEquipGeral;
    private boolean acessaFormularios;
    private boolean acessaChamadosJuridicos;
    private boolean acessaPlanta;

    private boolean acessaPerfil;
    private boolean acessaMenuChamado;
    private boolean acessaMenuRequerimento;
    private boolean acessaMenuCadastro;

    private boolean cadastraRamal;
    private boolean cadastraNiver;
    private boolean cadastraNoticia;
    private boolean cadastraInfor;
    private boolean cadastraManut;
    private boolean cadastraGeral;
    private boolean cadastraAtendimento;
    private boolean cadastraCampos;
    private boolean cadastraItem;
    private boolean cadastraChamado;
    private boolean cadastraRequerimento;
    private boolean cadastraAlerta;

    private boolean ativaChamado;
    private boolean apagaChamado;
    private boolean ativaRequerimento;
    private boolean apagaRequerimento;
    private boolean acessaDashChamado;
    private boolean acessaDashRequerimento;
    private boolean acessaSoftware;
    private boolean acessaMovimentacao;
    private boolean perfilAtendimento;

    public ControleAcesso() {
    }

    public boolean isAcessaUsuario() {
        return acessaUsuario;
    }

    public boolean isAcessaTecnico() {
        return acessaTecnico;
    }

    public ControleAcesso(long id, String tipoPerfil, boolean acessaUsuario, boolean acessaTecnico, boolean acessaFornecedores, boolean acessaCampos, boolean acessaEquipamentos, boolean acessaItens, boolean acessaAtendimentos, boolean acessaChamados, boolean acessaRequerimentos, boolean acessaInformatica, boolean acessaAlertas, boolean acessaPainelADM, boolean acessaEmbarcacao, boolean acessaCliente, boolean acessaLocal, boolean acessaRequerente, boolean acessaBaseConhecimento, boolean acessaTodosChamados, boolean acessaTodosRequerimentos, boolean acessaPerfil, boolean acessaMenuChamado, boolean acessaMenuRequerimento, boolean acessaMenuCadastro, boolean cadastraRamal, boolean cadastraNiver, boolean cadastraNoticia, boolean cadastraInfor, boolean cadastraManut, boolean cadastraGeral, boolean cadastraAtendimento, boolean cadastraCampos, boolean cadastraItem, boolean cadastraChamado, boolean cadastraRequerimento, boolean cadastraAlerta, boolean ativaChamado, boolean ativaRequerimento, boolean acessaDashChamado, boolean acessaDashRequerimento, boolean acessaChamadosAgendados, boolean acessaSoftware, boolean acessaMovimentacao, boolean acessaReserva, boolean acessaAgenda, boolean acessaFormularios, boolean acessaChamadosJuridicos, boolean acessaPlanta) {
        this.id = id;
        this.tipoPerfil = tipoPerfil;
        this.acessaUsuario = acessaUsuario;
        this.acessaTecnico = acessaTecnico;
        this.acessaFornecedores = acessaFornecedores;
        this.acessaCampos = acessaCampos;
        this.acessaEquipamentos = acessaEquipamentos;
        this.acessaItens = acessaItens;
        this.acessaAtendimentos = acessaAtendimentos;
        this.acessaChamados = acessaChamados;
        this.acessaRequerimentos = acessaRequerimentos;
        this.acessaInformatica = acessaInformatica;
        this.acessaAlertas = acessaAlertas;
        this.acessaPainelADM = acessaPainelADM;
        this.acessaEmbarcacao = acessaEmbarcacao;
        this.acessaCliente = acessaCliente;
        this.acessaLocal = acessaLocal;
        this.acessaRequerente = acessaRequerente;
        this.acessaBaseConhecimento = acessaBaseConhecimento;
        this.acessaTodosChamados = acessaTodosChamados;
        this.acessaTodosRequerimentos = acessaTodosRequerimentos;
        this.acessaPerfil = acessaPerfil;
        this.acessaMenuChamado = acessaMenuChamado;
        this.acessaMenuRequerimento = acessaMenuRequerimento;
        this.acessaMenuCadastro = acessaMenuCadastro;
        this.cadastraRamal = cadastraRamal;
        this.cadastraNiver = cadastraNiver;
        this.cadastraNoticia = cadastraNoticia;
        this.cadastraInfor = cadastraInfor;
        this.cadastraManut = cadastraManut;
        this.cadastraGeral = cadastraGeral;
        this.cadastraAtendimento = cadastraAtendimento;
        this.cadastraCampos = cadastraCampos;
        this.cadastraItem = cadastraItem;
        this.cadastraChamado = cadastraChamado;
        this.cadastraRequerimento = cadastraRequerimento;
        this.cadastraAlerta = cadastraAlerta;
        this.ativaChamado = ativaChamado;
        this.ativaRequerimento = ativaRequerimento;
        this.acessaDashChamado = acessaDashChamado;
        this.acessaDashRequerimento = acessaDashRequerimento;
        this.acessaChamadosAgendados = acessaChamadosAgendados;
        this.acessaSoftware = acessaSoftware;
        this.acessaMovimentacao = acessaMovimentacao;
        this.acessaReserva = acessaReserva;
        this.acessaAgenda = acessaAgenda;
        this.acessaFormularios = acessaFormularios;
        this.acessaChamadosJuridicos = acessaChamadosJuridicos;
        this.acessaPlanta = acessaPlanta;
    }

    public boolean isAcessaFornecedores() {
        return acessaFornecedores;
    }

    public boolean isAcessaCampos() {
        return acessaCampos;
    }

    public boolean isAcessaEquipamentos() {
        return acessaEquipamentos;
    }

    public boolean isAcessaItens() {
        return acessaItens;
    }

    public boolean isAcessaAtendimentos() {
        return acessaAtendimentos;
    }

    public boolean isAcessaChamados() {
        return acessaChamados;
    }

    public boolean isAcessaRequerimentos() {
        return acessaRequerimentos;
    }

    public boolean isAcessaAlertas() {
        return acessaAlertas;
    }

    public boolean isCadastraRamal() {
        return cadastraRamal;
    }

    public boolean isCadastraNiver() {
        return cadastraNiver;
    }

    public boolean isCadastraNoticia() {
        return cadastraNoticia;
    }

    public boolean isCadastraInfor() {
        return cadastraInfor;
    }

    public boolean isCadastraManut() {
        return cadastraManut;
    }

    public boolean isCadastraGeral() {
        return cadastraGeral;
    }

    public boolean isCadastraAtendimento() {
        return cadastraAtendimento;
    }

    public boolean isCadastraCampos() {
        return cadastraCampos;
    }

    public boolean isCadastraItem() {
        return cadastraItem;
    }

    public boolean isCadastraChamado() {
        return cadastraChamado;
    }

    public boolean isCadastraRequerimento() {
        return cadastraRequerimento;
    }

    public boolean isCadastraAlerta() {
        return cadastraAlerta;
    }

    public void setAcessaUsuario(boolean acessaUsuario) {
        this.acessaUsuario = acessaUsuario;
    }

    public void setAcessaTecnico(boolean acessaTecnico) {
        this.acessaTecnico = acessaTecnico;
    }

    public void setAcessaFornecedores(boolean acessaFornecedores) {
        this.acessaFornecedores = acessaFornecedores;
    }

    public void setAcessaCampos(boolean acessaCampos) {
        this.acessaCampos = acessaCampos;
    }

    public void setAcessaEquipamentos(boolean acessaEquipamentos) {
        this.acessaEquipamentos = acessaEquipamentos;
    }

    public void setAcessaItens(boolean acessaItens) {
        this.acessaItens = acessaItens;
    }

    public void setAcessaAtendimentos(boolean acessaAtendimentos) {
        this.acessaAtendimentos = acessaAtendimentos;
    }

    public void setAcessaChamados(boolean acessaChamados) {
        this.acessaChamados = acessaChamados;
    }

    public void setAcessaRequerimentos(boolean acessaRequerimentos) {
        this.acessaRequerimentos = acessaRequerimentos;
    }

    public void setAcessaAlertas(boolean acessaAlertas) {
        this.acessaAlertas = acessaAlertas;
    }

    public void setCadastraRamal(boolean cadastraRamal) {
        this.cadastraRamal = cadastraRamal;
    }

    public void setCadastraNiver(boolean cadastraNiver) {
        this.cadastraNiver = cadastraNiver;
    }

    public void setCadastraNoticia(boolean cadastraNoticia) {
        this.cadastraNoticia = cadastraNoticia;
    }

    public void setCadastraInfor(boolean cadastraInfor) {
        this.cadastraInfor = cadastraInfor;
    }

    public void setCadastraManut(boolean cadastraManut) {
        this.cadastraManut = cadastraManut;
    }

    public void setCadastraGeral(boolean cadastraGeral) {
        this.cadastraGeral = cadastraGeral;
    }

    public void setCadastraAtendimento(boolean cadastraAtendimento) {
        this.cadastraAtendimento = cadastraAtendimento;
    }

    public void setCadastraCampos(boolean cadastraCampos) {
        this.cadastraCampos = cadastraCampos;
    }

    public void setCadastraItem(boolean cadastraItem) {
        this.cadastraItem = cadastraItem;
    }

    public void setCadastraChamado(boolean cadastraChamado) {
        this.cadastraChamado = cadastraChamado;
    }

    public void setCadastraRequerimento(boolean cadastraRequerimento) {
        this.cadastraRequerimento = cadastraRequerimento;
    }

    public void setCadastraAlerta(boolean cadastraAlerta) {
        this.cadastraAlerta = cadastraAlerta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(String tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public boolean isAcessaInformatica() {
        return acessaInformatica;
    }

    public void setAcessaInformatica(boolean acessaInformatica) {
        this.acessaInformatica = acessaInformatica;
    }

    public boolean isAcessaPainelADM() {
        return acessaPainelADM;
    }

    public void setAcessaPainelADM(boolean acessaPainelADM) {
        this.acessaPainelADM = acessaPainelADM;
    }

    public boolean isAcessaEmbarcacao() {
        return acessaEmbarcacao;
    }

    public void setAcessaEmbarcacao(boolean acessaEmbarcacao) {
        this.acessaEmbarcacao = acessaEmbarcacao;
    }

    public boolean isAcessaCliente() {
        return acessaCliente;
    }

    public void setAcessaCliente(boolean acessaCliente) {
        this.acessaCliente = acessaCliente;
    }

    public boolean isAcessaLocal() {
        return acessaLocal;
    }

    public void setAcessaLocal(boolean acessaLocal) {
        this.acessaLocal = acessaLocal;
    }

    public boolean isAcessaRequerente() {
        return acessaRequerente;
    }

    public void setAcessaRequerente(boolean acessaRequerente) {
        this.acessaRequerente = acessaRequerente;
    }

    public boolean isAcessaPerfil() {
        return acessaPerfil;
    }

    public void setAcessaPerfil(boolean acessaPerfil) {
        this.acessaPerfil = acessaPerfil;
    }

    public boolean isAcessaMenuChamado() {
        return acessaMenuChamado;
    }

    public void setAcessaMenuChamado(boolean acessaMenuChamado) {
        this.acessaMenuChamado = acessaMenuChamado;
    }

    public boolean isAcessaMenuRequerimento() {
        return acessaMenuRequerimento;
    }

    public void setAcessaMenuRequerimento(boolean acessaMenuRequerimento) {
        this.acessaMenuRequerimento = acessaMenuRequerimento;
    }

    public boolean isAcessaMenuCadastro() {
        return acessaMenuCadastro;
    }

    public void setAcessaMenuCadastro(boolean acessaMenuCadastro) {
        this.acessaMenuCadastro = acessaMenuCadastro;
    }

    public boolean isAcessaBaseConhecimento() {
        return acessaBaseConhecimento;
    }

    public void setAcessaBaseConhecimento(boolean acessaBaseConhecimento) {
        this.acessaBaseConhecimento = acessaBaseConhecimento;
    }

    public boolean isAtivaChamado() {
        return ativaChamado;
    }

    public void setAtivaChamado(boolean ativaChamado) {
        this.ativaChamado = ativaChamado;
    }

    public boolean isAtivaRequerimento() {
        return ativaRequerimento;
    }

    public void setAtivaRequerimento(boolean ativaRequerimento) {
        this.ativaRequerimento = ativaRequerimento;
    }

    public boolean isAcessaDashChamado() {
        return acessaDashChamado;
    }

    public void setAcessaDashChamado(boolean acessaDashChamado) {
        this.acessaDashChamado = acessaDashChamado;
    }

    public boolean isAcessaDashRequerimento() {
        return acessaDashRequerimento;
    }

    public void setAcessaDashRequerimento(boolean acessaDashRequerimento) {
        this.acessaDashRequerimento = acessaDashRequerimento;
    }

    public boolean isAcessaTodosChamados() {
        return acessaTodosChamados;
    }

    public void setAcessaTodosChamados(boolean acessaTodosChamados) {
        this.acessaTodosChamados = acessaTodosChamados;
    }

    public boolean isAcessaTodosRequerimentos() {
        return acessaTodosRequerimentos;
    }

    public void setAcessaTodosRequerimentos(boolean acessaTodosRequerimentos) {
        this.acessaTodosRequerimentos = acessaTodosRequerimentos;
    }

    public boolean isAcessaAtendimentosAdm() {
        return acessaAtendimentosAdm;
    }

    public void setAcessaAtendimentosAdm(boolean acessaAtendimentosAdm) {
        this.acessaAtendimentosAdm = acessaAtendimentosAdm;
    }

    public boolean isAcessaChamadosAdm() {
        return acessaChamadosAdm;
    }

    public void setAcessaChamadosAdm(boolean acessaChamadosAdm) {
        this.acessaChamadosAdm = acessaChamadosAdm;
    }

    public boolean isAcessaNagios() {
        return acessaNagios;
    }

    public void setAcessaNagios(boolean acessaNagios) {
        this.acessaNagios = acessaNagios;
    }

    public boolean isAcessaParametros() {
        return acessaParametros;
    }

    public void setAcessaParametros(boolean acessaParametros) {
        this.acessaParametros = acessaParametros;
    }

    public boolean isAcessaChamadosAgendados() {
        return acessaChamadosAgendados;
    }

    public void setAcessaChamadosAgendados(boolean acessaChamadosAgendados) {
        this.acessaChamadosAgendados = acessaChamadosAgendados;
    }

    public boolean isApagaChamado() {
        return apagaChamado;
    }

    public void setApagaChamado(boolean apagaChamado) {
        this.apagaChamado = apagaChamado;
    }

    public boolean isApagaRequerimento() {
        return apagaRequerimento;
    }

    public void setApagaRequerimento(boolean apagaRequerimento) {
        this.apagaRequerimento = apagaRequerimento;
    }

    public boolean isAcessaSoftware() {
        return acessaSoftware;
    }

    public void setAcessaSoftware(boolean acessaSoftware) {
        this.acessaSoftware = acessaSoftware;
    }

    public boolean isAcessaMovimentacao() {
        return acessaMovimentacao;
    }

    public void setAcessaMovimentacao(boolean acessaMovimentacao) {
        this.acessaMovimentacao = acessaMovimentacao;
    }

    public boolean isAcessaReserva() {
        return acessaReserva;
    }

    public void setAcessaReserva(boolean acessaReserva) {
        this.acessaReserva = acessaReserva;
    }

    public boolean isAcessaAgenda() {
        return acessaAgenda;
    }

    public void setAcessaAgenda(boolean acessaAgenda) {
        this.acessaAgenda = acessaAgenda;
    }

    public boolean isAcessaTodasReservas() {
        return acessaTodasReservas;
    }

    public void setAcessaTodasReservas(boolean acessaTodasReservas) {
        this.acessaTodasReservas = acessaTodasReservas;
    }

    public boolean isAcessaEquipInfor() {
        return acessaEquipInfor;
    }

    public void setAcessaEquipInfor(boolean acessaEquipInfor) {
        this.acessaEquipInfor = acessaEquipInfor;
    }

    public boolean isAcessaEquipGeral() {
        return acessaEquipGeral;
    }

    public void setAcessaEquipGeral(boolean acessaEquipGeral) {
        this.acessaEquipGeral = acessaEquipGeral;
    }

    public boolean isPerfilAtendimento() {
        return perfilAtendimento;
    }

    public void setPerfilAtendimento(boolean perfilAtendimento) {
        this.perfilAtendimento = perfilAtendimento;
    }

    public boolean isAcessaFormularios() {
        return acessaFormularios;
    }

    public void setAcessaFormularios(boolean acessaFormularios) {
        this.acessaFormularios = acessaFormularios;
    }

    public boolean isAcessaChamadosJuridicos() {
        return acessaChamadosJuridicos;
    }

    public void setAcessaChamadosJuridicos(boolean acessaChamadosJuridicos) {
        this.acessaChamadosJuridicos = acessaChamadosJuridicos;
    }

    public boolean isAcessaPlanta() {
        return acessaPlanta;
    }

    public void setAcessaPlanta(boolean acessaPlanta) {
        this.acessaPlanta = acessaPlanta;
    }

    
}
