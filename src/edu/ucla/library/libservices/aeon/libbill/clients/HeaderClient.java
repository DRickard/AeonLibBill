package edu.ucla.library.libservices.aeon.libbill.clients;

import edu.ucla.library.libservices.aeon.libbill.db.procs.AddInvoiceProcedure;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;

import java.util.Properties;

public class HeaderClient
{
  private InsertHeaderBean theHeader;
  private Properties props;

  public HeaderClient()
  {
    super();
  }

  public String insertHeader()
  {
    AddInvoiceProcedure proc;
    String invoiceNumber;
    
    invoiceNumber = null;
    
    proc = new AddInvoiceProcedure();
    proc.setData( getTheHeader() );
    proc.setProps( getProps() );
    
    invoiceNumber = proc.addInvoice();
    
    return invoiceNumber;
  }

  public void setTheHeader( InsertHeaderBean theHeader )
  {
    this.theHeader = theHeader;
  }

  private InsertHeaderBean getTheHeader()
  {
    return theHeader;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }
}
/*import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import edu.ucla.library.libservices.aeon.libbill.utility.signatures.SignatureBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

private static final DateFormat df = new SimpleDateFormat( "MM/dd/yyyy" );
private Client client;
private WebResource webResource;
  private String resourceURI;
  private String uriBase;
  private String user;
  private String crypt;

client = null;
webResource = null;

client = Client.create();
webResource =
client.resource( getUriBase().concat( getResourceURI() ) );
System.out.println( "authorization = " + makeAuthorization( getResourceURI() ) );
invoiceNumber =
webResource.header( "Authorization", makeAuthorization( getResourceURI() ) ).type( "application/xml" ).put( String.class,


private Object makeAuthorization( String request )
{
return SignatureBuilder.computeAuth( SignatureBuilder.buildComplexSignature( "PUT",
       request,
       buildContentList() ),
getUser(), getCrypt() );
}

private List<String> buildContentList()
{
List<String> content;

content = new ArrayList<String>( 9 );
content.add( "<invoice>\n" );
content.add( "<branchCode>" + getTheHeader().getBranchCode() +
"</branchCode>" );
content.add( "<invoiceDate>" + getTheHeader().getInvoiceDate() +
"</invoiceDate>" );
content.add( "<status>Pending</status>" );
content.add( "<createdBy>aeon_user</createdBy>" );
content.add( "<patronID>" + getTheHeader().getPatronID() +
"</patronID>" );
content.add( "<onPremises>" + getTheHeader().getOnPremises() +
"</onPremises>" );
content.add( "</invoice>" );

System.out.println( "content list" );
for ( String theLine : content )
System.out.print( theLine );
System.out.print( "\n" );
return content;
}

private Object buildContentString()
{
StringBuffer content;

content = new StringBuffer();
content.append( "<invoice>\n" );
content.append( "<branchCode>" + getTheHeader().getBranchCode() +
"</branchCode>\n" );
content.append( "<invoiceDate>" + getTheHeader().getInvoiceDate() +
"</invoiceDate>\n" );
content.append( "<status>Pending</status>\n" );
content.append( "<createdBy>aeon_user</createdBy>\n" );
content.append( "<patronID>" + getTheHeader().getPatronID() +
"</patronID>\n" );
content.append( "<onPremises>" + getTheHeader().getOnPremises() +
"</onPremises>\n" );
content.append( "</invoice>" );

System.out.println( "content list" );
System.out.println( content );
System.out.print( "\n" );
return content.toString();
}


  public void setResourceURI( String resourceURI )
  {
    this.resourceURI = resourceURI;
  }

  private String getResourceURI()
  {
    return resourceURI;
  }

  public void setUriBase( String uriBase )
  {
    this.uriBase = uriBase;
  }

  private String getUriBase()
  {
    return uriBase;
  }

  public void setUser( String user )
  {
    this.user = user;
  }

  private String getUser()
  {
    return user;
  }

  public void setCrypt( String crypt )
  {
    this.crypt = crypt;
  }

  private String getCrypt()
  {
    return crypt;
  }
*/
