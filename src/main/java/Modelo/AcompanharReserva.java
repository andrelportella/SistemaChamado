/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author ricardo
 */
@Entity
public class AcompanharReserva extends AcompanharGeneric {

    private static final long serialVersionUID = 1L;

    public AcompanharReserva() {
    }

    public AcompanharReserva(Long cod_usuario, Long cod_userInseriu, Long cod_userRemoveu, boolean status, Date dataRemocao, Date dataInclusao, Long cod_tabela) {
        super(cod_usuario, cod_userInseriu, cod_userRemoveu, status, dataRemocao, dataInclusao, cod_tabela);
    }

    
}
