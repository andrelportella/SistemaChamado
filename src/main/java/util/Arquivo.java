package util;

import Conexao.ConexaoSQLServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ricardo
 */
public class Arquivo {

    ArquivoUtil aU = new ArquivoUtil();

    public String upload(FileUploadEvent event, Long cod_arquivo, String diretorio, String pasta) throws IOException {
        UploadedFile uploadedFile = event.getFile();
        aU.escrever(uploadedFile.getFileName(), uploadedFile.getContents(), cod_arquivo, diretorio, pasta);
        return uploadedFile.getFileName() + "<br/>";
    }

    public StreamedContent download(File file) throws IOException {
        StreamedContent downloadFile;
        InputStream inputStream = new FileInputStream(file);
        downloadFile = new DefaultStreamedContent(inputStream,
                Files.probeContentType(file.toPath()), file.getName());
        return downloadFile;
    }

    public void delete(File file, Long idRegistro, String nomeTabela) {
        (new File(file.getAbsolutePath())).delete();
        File f = new File(file.getParent());
        if (f.listFiles().length == 0) {
            atualizaArquivo(idRegistro, nomeTabela);
        }
    }

    public void atualizaArquivo(Long cod, String nomeTabela) {
        try {
            String sql = "UPDATE " + nomeTabela + " SET ANEXOMOVIMENTO = 0 WHERE ID = " + cod + "";
            Connection ConexaoSQL = new ConexaoSQLServer().getConnection();
            PreparedStatement stmt = ConexaoSQL.prepareStatement(sql);
            stmt.execute();
            ConexaoSQL.close();
            stmt.close();
            sql = null;
            ConexaoSQL = null;
            stmt = null;
            sql = null;
        } catch (ClassNotFoundException | SQLException e) {
            FacesUtil.addMsgFatalSQL(e, "Erro", "Arquivo", "atualizaArquivo");
            System.out.println("public  void atualizaArquivo Erro:" + e);
        }

    }

}
