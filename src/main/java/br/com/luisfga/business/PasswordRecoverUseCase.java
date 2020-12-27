package br.com.luisfga.business;

import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.entities.AppUserOperationWindow;
import br.com.luisfga.business.exceptions.WrongInfoException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PasswordRecoverUseCase extends UseCase{
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    public EntityManager em;
    
    @EJB private MailHelper mailHelper;

    public void prepareRecovery(String email, LocalDate birthday, String token) 
            throws WrongInfoException {
        
        try {
            Query findByEmail = em.createNamedQuery("AppUser.findByEmail");
            findByEmail.setParameter("email", email);

            AppUser appUser = (AppUser) findByEmail.getSingleResult();
            
            if (!appUser.getBirthday().equals(birthday)) {
                throw new WrongInfoException();
            }

            //reaproveitamento de janela de operação. Caso já exista uma, apenas atualiza a existente ao invés de excluir e criar uma nova.
            AppUserOperationWindow operationWindow = em.find(AppUserOperationWindow.class, appUser.getEmail());
            appUser.setOperationWindow(operationWindow);
            if (appUser.getOperationWindow() == null) {
                operationWindow = new AppUserOperationWindow();
                operationWindow.setAppUser(appUser);
            }
            
            operationWindow.setWindowToken(token);
            operationWindow.setInitTime(OffsetDateTime.now());
            em.persist(operationWindow);
            
        } catch (NoResultException nrException) {
            throw new WrongInfoException();
            
        }
    }
    
    public void enviarEmailResetSenha(String contextName, String email, String windowToken) {
        
        mailHelper.enviarEmailResetSenha(contextName, email, windowToken);

    }
    
}