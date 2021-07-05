
package Converter;


import Modelo.Atendimento;
import javax.faces.convert.FacesConverter;


@FacesConverter("AtendimentoEntityConverter")
public class ModeloConverter extends EntityConverter<Atendimento>{

	public ModeloConverter() {
		super(Atendimento.class);
	}
}
