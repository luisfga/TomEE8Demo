package br.com.luisfga.service;

import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.entities.AppUserOperationWindow;
import br.com.luisfga.service.exceptions.ForbidenOperationException;
import br.com.luisfga.service.exceptions.InvalidDataException;
import br.com.luisfga.service.exceptions.TimeHasExpiredException;
import java.time.OffsetDateTime;
import java.util.Base64;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.shiro.authc.credential.DefaultPasswordService;

@Stateless
public class PasswordResetUseCase {

    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;
    
    /**
     * Valida se o usuário tem uma janela de operação "aberta".Essa janela serve para estabelecer um tempo limite 
     * para operação de redefinição de senha, por exemplo.A janela se "fecha" após 7 minutos.
     * @param encodedUserEmail - email codificação em Base64, para mascarar sua passagem no request por link.
     * @param token - token gerado pelo sistema e salvo na tabela.
     * @return - retorna o email decodificado, para o seguimento de qualquer operação.
     * @throws com.luisfga.business.exceptions.ForbidenOperationException - para o caso de
     * estar faltando algum parâmetro, o que pode significar tentativa de fraudar a operação.
     * @throws com.luisfga.business.exceptions.TimeHasExpiredException  - para quando já houverem
     * decorridos os 7 minutos do prazo.
     * @throws com.luisfga.business.exceptions.InvalidDataException - quando houver erros nos dados
     */
    public String validateOperationWindow(String encodedUserEmail, String token) 
            throws InvalidDataException, ForbidenOperationException, TimeHasExpiredException, Exception {
        
        String decodedEmail = null;
        
        try {
            if (encodedUserEmail == null || encodedUserEmail.isEmpty() || token == null || token.isEmpty()) {
                throw new InvalidDataException();
            }
            
            byte[] decodedUserEmailBytes = Base64.getDecoder().decode(encodedUserEmail);
            decodedEmail = new String(decodedUserEmailBytes);
            
            Query findByEmail = em.createNamedQuery("AppUser.findByEmail");
            findByEmail.setParameter("email", decodedEmail);

            AppUser appUser = (AppUser) findByEmail.getSingleResult();
            
            AppUserOperationWindow operationWindow = em.find(AppUserOperationWindow.class, appUser.getEmail());
            appUser.setOperationWindow(operationWindow);
            
            if (appUser.getOperationWindow() == null) {
                throw new ForbidenOperationException(decodedEmail);
                
            //se o tempo salvo + 7 minutos for anterior a agora, o limite de 7 minutos já passou
            } else if (appUser
                    .getOperationWindow()
                    .getInitTime().plusMinutes(7).isBefore(OffsetDateTime.now())){
                
                //deleta/fecha janela de operação
                appUser.setOperationWindow(null);
                
                em.merge(appUser);
                
                throw new TimeHasExpiredException();
                
            } else if (appUser.getOperationWindow().getWindowToken() == null 
                    || !appUser.getOperationWindow().getWindowToken().equals(token)){
                
                throw new ForbidenOperationException(decodedEmail);
            }
            
        } catch (NoResultException | IllegalArgumentException ex) {
            throw new Exception(ex);
            
        }
        
        return decodedEmail;
    }
    
    public void resetPassword(String email, String password) {
        
        try {
            Query findByEmail = em.createNamedQuery("AppUser.findByEmail");
            findByEmail.setParameter("email", email);

            AppUser appUser = (AppUser) findByEmail.getSingleResult();
            
            //Hash password atualizado
            DefaultPasswordService defaultPasswordService = new DefaultPasswordService();
            String encryptedPassword = defaultPasswordService.encryptPassword(password);
            appUser.setPassword(encryptedPassword);
            
            //deleta/fecha janela de operação
            appUser.setOperationWindow(null);
            em.merge(appUser);
            
        } catch (NoResultException nrException) {
            //no op;
        }
    }
    
}