package Modelo;

import java.util.Date;
import javax.persistence.Entity;

/**
 * @author ricardo
 */
@Entity
public class AcompanharFormulario extends AcompanharGeneric {

    private static final long serialVersionUID = 1L;

    public AcompanharFormulario() {
    }

    public AcompanharFormulario(Long cod_usuario, Long cod_userInseriu, Long cod_userRemoveu, boolean status, Date dataRemocao, Date dataInclusao, Long cod_tabela) {
        super(cod_usuario, cod_userInseriu, cod_userRemoveu, status, dataRemocao, dataInclusao, cod_tabela);
    }

}
