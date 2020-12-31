package br.com.luisfga.controller.jsf.converter;

import br.com.luisfga.controller.jsf.LocaleBean;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author luisfga
 */
@FacesConverter("LocalDateConverter")
public class LocalDateConverter implements Converter{

    private final String localDatePattern = "yyyy-MM-dd";
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) throws ConverterException {
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(localDatePattern);
        
        return (string==null||string.isEmpty())?null:LocalDate.parse(string, dateTimeFormatter);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object t) throws ConverterException {
        return (t==null)?null:((LocalDate)t).toString();
    }
    
}
