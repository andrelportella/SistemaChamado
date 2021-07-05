package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class ArquivoUtil {

    public File escrever(String name, byte[] contents, Long tipo, String diretorio, String pasta) throws IOException {

        File file = new File(diretorioRaizParaArquivos(tipo, diretorio, pasta), name);
        OutputStream out = new FileOutputStream(file);
        out.write(contents);
        out.close();
        return file;
    }

    public List<File> listar(Long tipo, String diretorio, String pasta) {
        File dir = diretorioRaizParaArquivos(tipo, diretorio, pasta);        
        return Arrays.asList(dir.listFiles());
    }

    public File exibirImagem(Long tipo, String diretorio, String pasta) {
        File dir = diretorioRaizParaArquivos(tipo, diretorio, pasta);        
        return (dir);
    }

    public java.io.File diretorioRaizParaArquivos(Long tipo, String diretorio, String pasta) {
        File dir = new File(diretorioRaiz(diretorio, pasta), tipo.toString());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public File diretorioRaiz(String diretorio, String pasta) {
        File dir = new File(diretorio, pasta);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

}
