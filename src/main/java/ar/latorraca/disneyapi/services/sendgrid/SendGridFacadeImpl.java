package ar.latorraca.disneyapi.services.sendgrid;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import ar.latorraca.disneyapi.services.exception.SendMailException;

@Service
@PropertySource("classpath:secrets.properties")
public class SendGridFacadeImpl implements SendGridFacade {

	@Value("${SENDGRID_API_KEY}")
	private String SENDGRID_API_KEY;
	
	@Override
	public void sendMail(String subject, String to, String content) {
	    Email from_ = new Email("roberto@latorraca.ar");
	    String subject_ = subject;
	    Email to_ = new Email(to);
	    Content content_ = new Content("text/plain", content);
	    Mail mail = new Mail(from_, subject_, to_, content_);
		
	    SendGrid sg = new SendGrid(SENDGRID_API_KEY);
	    Request request = new Request();
	    try {
	    	request.setMethod(Method.POST);
	    	request.setEndpoint("mail/send");
	    	request.setBody(mail.build());
	    	Response response = sg.api(request);
	    	System.out.println(response.getStatusCode());
	    	System.out.println(response.getBody());
	    	System.out.println(response.getHeaders());
	    } catch (IOException ex) {
	    	throw new SendMailException(ex.getMessage());
	    }
	}

}
