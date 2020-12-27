package br.com.luisfga.business;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Stateless
public class MailHelper {

    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;
    
    @Resource
    private Session applicationMailSession;

    @Resource(name = "appBaseHostName")
    private String appBaseHostName;
    
    private String getUserName(String email){
        Query findUserNameByEmail = em.createNamedQuery("AppUser.findUserNameByEmail");
        findUserNameByEmail.setParameter("email", email);
        return (String) findUserNameByEmail.getSingleResult();
    }
    
    public void enviarEmailResetSenha(String contextPath, String email, String windowToken) {

        Executors.newSingleThreadExecutor().execute(() -> {
            
            try {

                Message message = new MimeMessage(applicationMailSession);
                message.setFrom(new InternetAddress(applicationMailSession.getProperty("mail.smtp.user"), contextPath.replace("/", ""))); // Remetente
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Redefinição de Senha");// Assunto

                //corpo da mensagem
                String style = "<style>"
                                + ".button{"
                                    + "background-color: #0099ff; color: white; padding: 5px 10px 5px 10px; "
                                    + "vertical-align: middle; text-align: center; text-decoration: none; border-radius: 20px; "
                                    + "font-size: 15px;"
                                + "}"
                            + "</style>";

                String msg = style
                        + "<h2>Olá, "+getUserName(email)+".</h2>"
                        + "<h4>Utilize o botão abaixo para acessar a página de redefinição de senha</h4>"
                        + "<a class=\"button\" href=\""+appBaseHostName+contextPath+"/passwordReset"
                        + "?encodedUserEmail="+Base64.getEncoder().encodeToString(email.getBytes("UTF-8"))
                        + "&windowToken="+windowToken+"\">Redefinir Senha</a><br/><br/>"
                        + "*Se não foi você que solicitou a redefinição de senha. Desconsidere essa mensagem.<br/><br/>"
                        + "*Este link só funcionará uma única vez. Se necessário, solicite novamente.<br/><br/>";

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(msg, "text/html; charset=UTF-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);
                
            } catch (AddressException ex){
                
            } catch (MessagingException | UnsupportedEncodingException ex) {
                
            }
        });
    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String contextPath, String email) {

        Executors.newSingleThreadExecutor().execute(() -> {
            
            try {
                
                Message message = new MimeMessage(applicationMailSession);

                message.setFrom(new InternetAddress(applicationMailSession.getProperty("mail.smtp.user"), contextPath.replace("/", ""))); // Remetente
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Confirmação de Nova Conta");// Assunto

                //corpo da mensagem
                String msg = ""
                        + "<style>"
                        + ".button{background-color: #0099ff; color: white; padding: 5px 10px 5px 10px; "
                        + "vertical-align: middle; text-align: center; text-decoration: none; border-radius: 20px; "
                        + "font-size: 15px;}"
                        + "</style>"
                        + "<h2>Olá, " + getUserName(email) + ".</h2>"
                        + "<h4>Utilize o botão abaixo para confirmar sua conta recém criada</h4>"
                        + "<a class=\"button\" href=\""
                        + appBaseHostName + contextPath + "/confirmRegistration"
                        + "?encodedUserEmail=" + Base64.getEncoder().encodeToString(email.getBytes("UTF-8"))  + "\">Confirmar</a><br/><br/>";

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(msg, "text/html; charset=UTF-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);
                
            } catch (AddressException ex){
                
            } catch (MessagingException | UnsupportedEncodingException ex) {
                
            }
            
        });
    }

}