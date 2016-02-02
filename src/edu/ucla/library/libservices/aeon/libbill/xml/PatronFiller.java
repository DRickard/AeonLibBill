package edu.ucla.library.libservices.aeon.libbill.xml;

import edu.ucla.library.libservices.aeon.libbill.beans.Patron;

import java.io.File;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class PatronFiller
{
  private Patron thePatron;
  private File source;

  public PatronFiller()
  {
    super();
  }

  public Patron getThePatron()
  {
    Document document;
    thePatron = new Patron();

    try
    {
      document =
          DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( getSource() );
      thePatron.setAddress1( document.getElementsByTagName( "address1" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setAeonUsername( document.getElementsByTagName( "aeon-username" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setAddress2( !isEmpty( document.getElementsByTagName( "address2" ).item( 0 ).getChildNodes() ) ?
                             document.getElementsByTagName( "address2" ).item( 0 ).getChildNodes().item( 0 ).getTextContent():
                             "" );
      thePatron.setCategory( document.getElementsByTagName( "category" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setCity( document.getElementsByTagName( "city" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setCountry( document.getElementsByTagName( "country" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setEmailAddress( document.getElementsByTagName( "email-address" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setFirstName( document.getElementsByTagName( "first-name" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setLastName( document.getElementsByTagName( "last-name" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setPhone( document.getElementsByTagName( "phone" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
      thePatron.setState( document.getElementsByTagName( "state" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );

      if ( !isEmpty( document.getElementsByTagName( "voyager-patron-id" ).item( 0 ).getChildNodes() )  )
      {
        String tempID;
        tempID =
            document.getElementsByTagName( "voyager-patron-id" ).item( 0 ).getChildNodes().item( 0 ).getTextContent();
        thePatron.setVoyagerPatronID( isPatronIDNumeric( tempID ) ?
                                      Integer.parseInt( tempID ): -1 );
      }
      else
        thePatron.setVoyagerPatronID( -1 );

      thePatron.setZip( document.getElementsByTagName( "zip" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
    }
    catch ( ParserConfigurationException pce )
    {
      pce.printStackTrace();
    }
    catch ( SAXException saxe )
    {
      saxe.printStackTrace();
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
    catch ( NumberFormatException nfe )
    {
      nfe.printStackTrace();
    }

    return thePatron;
  }

  public void setSource( File source )
  {
    this.source = source;
  }

  private File getSource()
  {
    return source;
  }

  private boolean isPatronIDNumeric( String maybeNumber )
  {
    boolean isNumeric;

    isNumeric = true;

    if ( maybeNumber != null && maybeNumber.length() > 0 )
    {
      if ( maybeNumber.length() > 1 )
      {
        for ( int index = 0; index < maybeNumber.length(); index++ )
        {
          if ( !Character.isDigit( maybeNumber.charAt( index ) ) )
          {
            isNumeric = false;
            break;
          }
        }
      }
      else
        isNumeric = Character.isDigit( maybeNumber.charAt( 0 ) );
    }
    return isNumeric;
  }

  private boolean isEmpty( NodeList o )
  {
    return ( o == null || o.getLength() == 0 );
  }
}
