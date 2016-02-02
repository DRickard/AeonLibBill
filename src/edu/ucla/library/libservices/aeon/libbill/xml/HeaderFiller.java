package edu.ucla.library.libservices.aeon.libbill.xml;

import java.io.File;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import edu.ucla.library.libservices.aeon.libbill.beans.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.w3c.dom.NodeList;

public class HeaderFiller
{
  private Vector<String> sources;
  private Header theHeader;

  public HeaderFiller()
  {
    super();
  }

  public void setSources( Vector<String> source )
  {
    this.sources = source;
  }

  private Vector<String> getSources()
  {
    return sources;
  }

  public Header getTheHeader()
  {
    theHeader = new Header();
    try
    {
      List<String> aeonNos;
      Set<String> notes;
      setSingletons( getSources().get( 0 ) );

      aeonNos = new ArrayList<String>();
      notes = new TreeSet<String>();
      for ( String fileName: getSources() )
      {
        Document document;
        File source;

        source = new File( fileName );
        document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( source );
        aeonNos.add( document.getElementsByTagName( "aeon-request-id" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
        notes.add( !isEmpty( document.getElementsByTagName( "aeon-request-note" ).item( 0 ).getChildNodes() ) ?
                   document.getElementsByTagName( "aeon-request-note" ).item( 0 ).getChildNodes().item( 0 ).getTextContent():
                   "" );
      }
      theHeader.setAeonRequest( aeonNos );
      theHeader.setNote( notes );
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

    return theHeader;
  }

  private boolean isEmpty( NodeList o )
  {
    return ( o == null || o.getLength() == 0 );
  }

  private void setSingletons( String fileName )
    throws ParserConfigurationException, SAXException, IOException
  {
    Document document;
    document =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new File( fileName ) );
    theHeader.setLibrary( document.getElementsByTagName( "aeon-location" ).item( 0 ).getChildNodes().item( 0 ).getTextContent() );
    theHeader.setOnPremises( !isEmpty( document.getElementsByTagName( "patron-on-premises" ).item( 0 ).getChildNodes() ) ?
                             document.getElementsByTagName( "patron-on-premises" ).item( 0 ).getChildNodes().item( 0 ).getTextContent():
                             "N" );
  }
}
