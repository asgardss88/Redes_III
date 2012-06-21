
package redesiii;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author cesar
 */
public class Correo {
    
    public String mensaje;
    public String asunto;
    public String destino;
    public String pass;

    public Correo(String mensaje, String asunto, String destino, String pass) {
        this.mensaje = mensaje;
        this.asunto = asunto;
        this.destino = destino;
        this.pass = pass;
    }

   
    
    public void enviar(){
        
        try
        {
            // Propiedades de la conexión
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", destino);
            props.setProperty("mail.smtp.auth", "true");

            // Preparamos la sesion
            Session session = Session.getDefaultInstance(props);

            // Construimos el mensaje
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("SelfAdmin@aplication.com"));
            message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress(destino));
            message.setSubject(asunto);
            message.setText(mensaje);

            // Lo enviamos.
            Transport t = session.getTransport("smtp");
            t.connect(destino, pass);
            t.sendMessage(message, message.getAllRecipients());

            // Cierre.
            t.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    
    
    }
    
    
    public static void main(String[] args)
    {
        
        Correo c = new Correo("correo enviado desde java", "prueba", "redesiii2012@gmail.com","claveclave");
        c.enviar();
    }
    
   
    
}
