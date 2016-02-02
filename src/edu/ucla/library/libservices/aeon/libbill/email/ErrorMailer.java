package edu.ucla.library.libservices.aeon.libbill.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class ErrorMailer
{
  private static final String FROM_ADDRESS = "no-reply@library.ucla.edu";
  final static Logger logger = Logger.getLogger( ErrorMailer.class );

  public ErrorMailer()
  {
    super();
  }

  public void sendMessage( String errorMessage, String invoice, String reqID,
                           String to )
  {
    StringBuffer messageBody;
    InternetAddress[] address;
    Message message;
    MimeBodyPart content;
    Multipart letter;
    Properties sysProps;
    Session mailSession;

    messageBody = new StringBuffer();

    sysProps = System.getProperties();
    sysProps.put( "mail.smtp.host", "em.library.ucla.edu" );
    mailSession = Session.getDefaultInstance( sysProps, null );
    message = new MimeMessage( mailSession );
    try
    {
      String[] tos = to.split( ";" );
      message.setFrom( new InternetAddress( FROM_ADDRESS ) );
      address = new InternetAddress[ tos.length ];
      for ( int i = 0; i < tos.length; i++ )
        address[ i ] = new InternetAddress( tos[ i ] );
      message.setRecipients( Message.RecipientType.TO, address );
      message.setSubject( "Error creating invoice for Aeon request(s) " + reqID );
      message.setSentDate( new Date() );
      content = new MimeBodyPart();
      messageBody.append( "A problem occurred while trying to create an invoice " );
      messageBody.append( " from Aeon request " + reqID + ".\n" );
      if ( invoice != null && invoice.length() != 0 )
        messageBody.append( "Invoice number: " + invoice + ".\n" );
      messageBody.append( "The following error occurred: " + errorMessage + ".\n" );
      content.setText( messageBody.toString() );
      letter = new MimeMultipart();
      letter.addBodyPart( content );
      message.setContent( letter );
      Transport.send( message );
    }
    catch ( MessagingException me )
    {
      logger.error( " error mailing message: " + me.getMessage() );
    }
  }
}
