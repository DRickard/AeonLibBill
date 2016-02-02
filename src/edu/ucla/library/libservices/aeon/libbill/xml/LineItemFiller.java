package edu.ucla.library.libservices.aeon.libbill.xml;

import edu.ucla.library.libservices.aeon.libbill.beans.LineItem;

import java.io.File;

import java.io.IOException;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class LineItemFiller
{
  private Vector<LineItem> theItems;
  private File source;

  public LineItemFiller()
  {
    super();
  }

  public Vector<LineItem> getTheItems()
  {
    Document document;
    NodeList requestItems;

    theItems = new Vector<LineItem>();
    try
    {
      document =
          DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( getSource() );
      requestItems =
          document.getElementsByTagName( "request-items" ).item( 0 ).getChildNodes();
      for ( int requestIndex = 0; requestIndex < requestItems.getLength();
            requestIndex++ )
      {
        if ( requestItems.item( requestIndex ).getNodeName().equalsIgnoreCase( "request-item" ) )
        {
          NodeList itemNode;
          int quantity;
          int code;
          double price;

          itemNode = requestItems.item( requestIndex ).getChildNodes();
          quantity = 0;
          code = 0;
          price = 0D;

          for ( int itemIndex = 0; itemIndex < itemNode.getLength();
                itemIndex++ )
          {

            if ( itemNode.item( itemIndex ).getNodeName().equalsIgnoreCase( "service-code" ) )
              code = Integer.parseInt( itemNode.item( itemIndex ).getChildNodes().item( 0 ).getTextContent() ); // getCodeID( itemNode.item( itemIndex ).getChildNodes().item( 0 ).getTextContent() );
            if ( itemNode.item( itemIndex ).getNodeName().equalsIgnoreCase( "quantity" ) )
              quantity = Integer.parseInt( itemNode.item( itemIndex ).getChildNodes().item( 0 ).getTextContent() );
            if ( itemNode.item( itemIndex ).getNodeName().equalsIgnoreCase( "custom-price" ) )
              price = Double.parseDouble( itemNode.item( itemIndex ).getChildNodes().item( 0 ).getTextContent() );
          }
          if ( code > 0 )
          {
            theItems.add( new LineItem( code, quantity, price ) );
          }
        }
      }
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

    return theItems;
  }

  public void setSource( File source )
  {
    this.source = source;
  }

  private File getSource()
  {
    return source;
  }

  private int getCodeID( String codeString )
  {
    System.out.println( "code string = " + codeString );
    return Integer.parseInt( codeString.substring( 0,
                                                   codeString.indexOf( " " ) ) );
  }
}
