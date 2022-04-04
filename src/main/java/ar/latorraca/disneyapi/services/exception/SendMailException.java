package ar.latorraca.disneyapi.services.exception;

public class SendMailException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ERROR_TYPE = "Error sending email.";
	
	public SendMailException(String errorDetails) {
		super(ERROR_TYPE + " " + errorDetails);
	}

}
