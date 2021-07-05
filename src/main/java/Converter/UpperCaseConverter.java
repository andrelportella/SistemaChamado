package Converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ricardo
 */
@FacesConverter("upperCaseConverter")
public class UpperCaseConverter implements Converter {
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (value != null) ? value.toString().toUpperCase() : null;
    }
 
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return (value != null) ? value.toUpperCase() : null;
    }
}