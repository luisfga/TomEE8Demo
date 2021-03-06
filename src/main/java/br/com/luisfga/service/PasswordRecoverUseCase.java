package br.com.luisfga.service;

import br.com.luisfga.controller.jsf.LocaleBean;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.entities.AppUserOperationWindow;
import br.com.luisfga.service.exceptions.WrongInfoException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PasswordRecoverUseCase {
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;
    
    @EJB 
    private MailHelper mailHelper;
    
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
    
    public void enviarEmailResetSenha(String email, Locale locale,String windowToken) {
        
        mailHelper.enviarEmailResetSenha(email, locale, windowToken);

    }
    
}