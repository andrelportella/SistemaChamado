package Converter;

import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

@FacesConverter(value = "primeFacesPickListConverter")
public class PrimeFacesPickListConverter implements Converter {

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        Object ret = null;
        if (arg1 instanceof PickList) {
            Object dualList = ((PickList) arg1).getValue();
            DualListModel dl = (DualListModel) dualList;
            for (Iterator iterator = dl.getSource().iterator(); iterator
                    .hasNext();) {
                Object o = iterator.next();
                String id = (new StringBuilder()).append(
                        ((AbstractBean) o).getId()).toString();
                if (arg2.equals(id)) {
                    ret = o;
                    break;
                }
            }

            if (ret == null) {
                for (Iterator iterator1 = dl.getTarget().iterator(); iterator1
                        .hasNext();) {
                    Object o = iterator1.next();
                    String id = (new StringBuilder()).append(
                            ((AbstractBean) o).getId()).toString();
                    if (arg2.equals(id)) {
                        ret = o;
                        break;
                    }
                }

            }
        }
        return ret;
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        String str = "";
        if (arg2 instanceof AbstractBean) {
            str = ((AbstractBean) arg2).getId().toString();
        }
        return str;
    }
}
