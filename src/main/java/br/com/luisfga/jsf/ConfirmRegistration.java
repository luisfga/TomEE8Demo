package br.com.luisfga.jsf;

import br.com.luisfga.business.ConfirmRegistrationUseCase;
import br.com.luisfga.business.exceptions.CorruptedLinkageException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class ConfirmRegistration extends JsfBeanSupport{
    
    @EJB private ConfirmRegistrationUseCase confirmRegistrationUseCase;
    
    private static final long serialVersionUID = 1L;
    
    private String encodedUserEmail;
    public String getEncodedUserEmail() {
        return encodedUserEmail;
    }
    public void setEncodedUserEmail(String encodedUserEmail) {
        this.encodedUserEmail = encodedUserEmail;
    }
    
    //TODO as mensagens não são apresentadas, provavelmente pelo modo de ativação desta view action.
    //É preciso estudar melhor o ciclo de vida do JSF para ver como tratar essas mensagens.
    //O código é executado sem error, porém as mensagens não aparecem para o usuário.
    public String execute(){
        
        System.out.println("Encoded User Email = " + encodedUserEmail);
        
        try {
            confirmRegistrationUseCase.confirmRegistration(encodedUserEmail);
            
        } catch (CorruptedLinkageException clException) {
            String errorMessage = getMsgText("global","action.error.email.is.empty");
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, message);
            
            return "login";
        }

        String successMessage = getMsgText("global","action.message.confirmation.completed");

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,successMessage, successMessage);

        FacesContext.getCurrentInstance().addMessage(null, message);
        
        return "login";

    }
    
}