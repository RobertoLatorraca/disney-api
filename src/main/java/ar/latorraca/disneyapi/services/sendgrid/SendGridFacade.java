package ar.latorraca.disneyapi.services.sendgrid;

public interface SendGridFacade {

	void sendMail(String subject, String to, String content);
	
}
