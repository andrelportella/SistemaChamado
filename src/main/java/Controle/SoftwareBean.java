/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;

import Conexao.ConexaoSQLServer;
import Converter.AbstractBean;
import DAO.GenericDAO;
import Modelo.ArquivoUpload;
import Modelo.Campos;
import Modelo.Equipamento;
import Modelo.Fornecedor;
import Modelo.Parametros;
import Modelo.Software;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;
import util.Arquivo;
import util.ArquivoUtil;
import util.FacesUtil;

/**
 *
 * @author ricardo
 */
@ViewScoped
@ManagedBean
public class SoftwareBean extends AbstractBean implements Serializable {

    private Software software = new Software();
    private Software softwareSelecionado = new Software();
    private Software editaSoftware = new Software();
    PreparedStatement stmt;
    ResultSet rs;
    String sql;
    private Connection ConexaoSQL;
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
    Long cod_negocio = (Long) session.getAttribute("idNeg");
    private List<Campos> empresa = new ArrayList<>();
    private List<Campos> tipoLicenca = new ArrayList<>();
    private List<Boolean> list;
    private List<Software> softwares = new ArrayList<>();
    private StreamedContent downloadFile;
    private String textoArquivo = "";
    ArquivoUpload a = new ArquivoUpload();
    Parametros parametros = new Parametros();
    Date data = new Date(System.currentTimeMillis());
    private Equipamento selecEquipInfor = new Equipamento();
    ArquivoUtil aU = new ArquivoUtil();
    Arquivo arq = new Arquivo();

    public SoftwareBean() throws ClassNotFoundException, SQLException {
        parametros = new GenericDAO<>(Parametros.class).parametro();
        empresa = new GenericDAO<>(Campos.class).listarCamposJDBC(16L);
        tipoLicenca = new GenericDAO<>(Campos.class).listarCamposJDBC(26L);
        listaSoftwares();
        list = Arrays.asList(true, true, true, true, true, true, true, false, false, true, true);
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void upload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, a.getId(), parametros.getDiretorioArquivo(), parametros.getPastaSoftware());
    }

    public void editUpload(FileUploadEvent event) throws IOException {
        a.setEnviado(true);
        textoArquivo += arq.upload(event, editaSoftware.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaSoftware());
    }

    public void download(File file) throws IOException {
        downloadFile = arq.download(file);
    }

    public void deletarArquivo(File file) throws IOException, ParseException, ClassNotFoundException {
        arq.delete(file, editaSoftware.getId(), "Software");
        listaSoftwares();
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('arquivoDialogEdit').hide();");
    }

    public void anexaArquivo() throws IOException {
        if (a.getId() == null) {
            new GenericDAO<>(ArquivoUpload.class).salvar2(a);
        }
        software.setDataAquisicao(data);
    }

    public List listaSoftwares() {
        List<Software> softwares = new ArrayList<>();
        String sql2 = " select empresa.descricao as empresa, tipoLicenca.descricao as tipoLicenca, "
                + " CASE WHEN e.nome IS NULL THEN 'LIVRE' "
                + " ELSE E.nome + ' - ' + E.descricao "
                + " END AS nomeEquipamento, s.* from software s "
                + " JOIN Campos empresa on empresa.cod = s.cod_empresa and empresa.tabela = 16 "
                + " JOIN Campos tipoLicenca on tipoLicenca.cod = s.cod_tipolicenca and tipoLicenca.tabela = 26 "
                + " LEFT JOIN Software_Equipamento SE on se.cod_software = s.id  and se.status = 1"
                + " LEFT JOIN EQUIPAMENTO E ON E.id = SE.COD_EQUIPAMENTO "
                + " where empresa.cod_negocio = 1 and tipoLicenca.cod_negocio = 1 and s.cod_negocio = 1 "
                + " order by s.nome ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql2);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Software s = new Software();
                s.setId(rs.getLong("id"));
                s.setCod_negocio(rs.getLong("cod_negocio"));
                s.setChave(rs.getString("chave"));
                s.setEmpresa(rs.getString("empresa"));
                s.setTipoLicenca(rs.getString("tipoLicenca"));
                s.setCod_empresa(rs.getLong("cod_empresa"));
                s.setCod_tipoLicenca(rs.getLong("cod_tipoLicenca"));
                s.setDataAquisicao(rs.getDate("dataAquisicao"));
                s.setDescricao(rs.getString("descricao"));
                s.setNumNota(rs.getString("numNota"));
                s.setValidade(rs.getDate("validade"));
                s.setNome(rs.getString("nome"));
                s.setAnexoMovimento(rs.getBoolean("anexoMovimento"));
                s.setCod_arquivo(rs.getLong("COD_ARQUIVO"));
                s.setStatus(rs.getBoolean("status"));
                s.setNomeEquipamento(rs.getString("nomeEquipamento"));
                if (s.isAnexoMovimento()) {
                    s.setArquivo(aU.listar(s.getCod_arquivo(), parametros.getDiretorioArquivo(), parametros.getPastaSoftware()));
                }
                softwares.add(s);
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            sql = null;
            stmt = null;
            rs = null;
            ConexaoSQL = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "SoftwareBean", "listaSoftwares");
            System.out.println("   public List listaSoftwares() Erro:" + e);
        }

        return this.softwares = softwares;
    }

    public void listaEquipamento(Long cod_Software) {
        sql = "	SELECT setor.descricao as Setor, site.descricao AS Site, hd.descricao as HD , memoria.descricao AS MEMORIA ,sisOp.descricao AS Sistema_Operacional, "
                + " status.descricao as Status ,tipoMaq.descricao as Tipo_Maquina, processador.descricao as Processador , fornecedor.nome as Fornecedor, "
                + " marca.descricao as Marca, usuario.nome as usuario, usuario.id as tid,fornecedor.id as tid2, empresa.descricao as empresa, e.* "
                + " FROM Equipamento e "
                + " LEFT join campos site on e.cod_site = site.cod and site.tabela = 2 and site.cod_negocio = " + cod_negocio + " "
                + " LEFT join campos setor on e.cod_setor = setor.cod and setor.tabela = 4 and setor.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos HD on e.cod_tamHD = HD.cod and HD.tabela = 5 and HD.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos memoria on e.cod_mem = memoria.cod and memoria.tabela = 6 and memoria.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos sisOp on e.cod_sOp = sisOp.cod and sisOp.tabela = 7 and sisOp.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos status on e.cod_Status = status.cod and status.tabela = 8 and status.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos tipoMaq on e.cod_tipoMaq = tipoMaq.cod and tipoMaq.tabela = 9 and tipoMaq.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos processador on e.cod_Processador = processador.cod and processador.tabela = 11 and processador.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos empresa on e.cod_Processador = empresa.cod and empresa.tabela = 16 and empresa.cod_negocio = " + cod_negocio + " "
                + " LEFT join Fornecedor fornecedor on e.id_Fornecedor = fornecedor.id and fornecedor.cod_negocio = " + cod_negocio + " "
                + " LEFT join Campos marca on e.cod_marca = marca.cod and marca.tabela = 13 and marca.cod_negocio = " + cod_negocio + " "
                + " LEFT join Usuario usuario on e.cod_usrResp = usuario.id and usuario.cod_negocio = " + cod_negocio + " "
                + " where e.cod_tipoEquipamento = 2 and e.cod_negocio =" + cod_negocio + " and e.id = ("
                + " SELECT e.id FROM Software_Equipamento SE "
                + " JOIN EQUIPAMENTO E ON E.id = SE.COD_EQUIPAMENTO "
                + " WHERE COD_SOFTWARE = " + cod_Software + " AND SE.STATUS = 1 AND E.cod_negocio = " + cod_negocio + ") ";
        try {
            ConexaoSQL = new ConexaoSQLServer().getConnection();
            stmt = ConexaoSQL.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Equipamento e = new Equipamento();
                Fornecedor f = new Fornecedor();
                e.setSetor(rs.getString(1));
                e.setSite(rs.getString(2));
                e.setHD(rs.getString(3));
                e.setMemoria(rs.getString(4));
                e.setSistemaOperacional(rs.getString(5));
                e.setStatus(rs.getString(6));
                e.setTipoMaq(rs.getString(7));
                e.setProcessador(rs.getString(8));
                e.setFornecedor(rs.getString(9));
                e.setMarca(rs.getString(10));
                e.setUsuario(rs.getString(11));
                e.setCod_usrResp(rs.getLong(12));
                f.setId(rs.getLong(13));
                e.setEmpresa(rs.getString(14));
                e.setId_fornecedor(f);
                e.setModelo(rs.getString("modelo"));
                e.setPeso(rs.getString("peso"));
                e.setNumSerie(rs.getString("numSerie"));
                e.setNfSoftware(rs.getString("NfSoftware"));
                e.setDtCompra(rs.getDate("dtCompra"));
                e.setNome(rs.getString("nome"));
                e.setCapacidade(rs.getString("Capacidade"));
                e.setIp(rs.getString("ip"));
                e.setRevisadoRenato(rs.getBoolean("revisadoRenato"));
                e.setTagLicenca(rs.getString("tagLicenca"));
                e.setTamanho(rs.getString("tamanho"));
                e.setId(rs.getLong("id"));
                e.setCod_Processador(rs.getLong("COD_processador"));
                e.setCod_Status(rs.getLong("cod_status"));
                e.setCod_marca(rs.getLong("cod_marca"));
                e.setCod_mem(rs.getLong("cod_mem"));
                e.setCod_placaMae(rs.getLong("cod_placamae"));
                e.setCod_sOp(rs.getLong("cod_sop"));
                e.setCod_setor(rs.getLong("cod_setor"));
                e.setCod_site(rs.getLong("cod_site"));
                e.setCod_tamHD(rs.getLong("cod_tamHD"));
                e.setCod_tipoEquipamento(rs.getLong("cod_tipoEquipamento"));
                e.setCod_tipoMaq(rs.getLong("cod_tipoMaq"));
                e.setDescricao(rs.getString("descricao"));
                e.setValor(rs.getDouble("VALOR"));
                e.setCod_arquivo(rs.getLong("cod_arquivo"));
                e.setCod_empresa(rs.getLong("cod_empresa"));
                e.setAnexoMovimento(rs.getBoolean("anexoMovimento"));
                e.setObservacao(rs.getString("observacao"));
                this.selecEquipInfor = e;
            } else {
                this.selecEquipInfor = new Equipamento();
            }
            ConexaoSQL.close();
            rs.close();
            stmt.close();
            sql = null;
            stmt = null;
            rs = null;
            ConexaoSQL = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "SoftwareBean", "listaEquipamento");
            System.out.println(" public void listaEquipamento(Long cod_Software) Erro:" + e);
        }

    }

    public void editar(Software s) {
        editaSoftware = s;
        s = null;
    }

    public void duplicar(Software s) throws ClassNotFoundException {
        Software ss = new Software();
        ss = s;
        ss.setId(null);
        ss.setCod_arquivo(null);
        ss.setAnexoMovimento(false);
        new GenericDAO<>(Software.class).salvar2(ss);
        s = null;
        ss = null;
        listaSoftwares();
        FacesUtil.addMsgInfo("Duplicar", "Duplicado com Sucesso");
    }

    public void editarSoftware() throws ClassNotFoundException {
        if (a.isEnviado()) {
            editaSoftware.setAnexoMovimento(true);
        }
        new GenericDAO<>(Software.class).update(editaSoftware);
        editaSoftware = new Software();
        listaSoftwares();
        FacesUtil.addMsgInfo("Editado com Sucesso", "Editar");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('editarDialog').hide();");
        textoArquivo = "";
    }

    public void salvar() throws ClassNotFoundException {
        software.setStatus(true);
        software.setCod_arquivo(a.getId());
        if (a.isEnviado()) {
            software.setAnexoMovimento(true);
        }
        new GenericDAO<>(Software.class).salvar(software);
        software = new Software();
        listaSoftwares();
        FacesUtil.addMsgInfo("Salvo com Sucesso", "Salvar");
        org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('cadastrarDialog').hide();");
        textoArquivo = "";
        a = new ArquivoUpload();
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Software getSoftwareSelecionado() {
        return softwareSelecionado;
    }

    public void setSoftwareSelecionado(Software softwareSelecionado) {
        this.softwareSelecionado = softwareSelecionado;
    }

    public Software getEditaSoftware() {
        return editaSoftware;
    }

    public void setEditaSoftware(Software editaSoftware) {
        this.editaSoftware = editaSoftware;
    }

    public List<Campos> getEmpresa() {
        return empresa;
    }

    public void setEmpresa(List<Campos> empresa) {
        this.empresa = empresa;
    }

    public List<Campos> getTipoLicenca() {
        return tipoLicenca;
    }

    public void setTipoLicenca(List<Campos> tipoLicenca) {
        this.tipoLicenca = tipoLicenca;
    }

    public List<Software> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<Software> softwares) {
        this.softwares = softwares;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

    public String getTextoArquivo() {
        return textoArquivo;
    }

    public void setTextoArquivo(String textoArquivo) {
        this.textoArquivo = textoArquivo;
    }

    public Equipamento getSelecEquipInfor() {
        return selecEquipInfor;
    }

    public void setSelecEquipInfor(Equipamento selecEquipInfor) {
        this.selecEquipInfor = selecEquipInfor;
    }

}
