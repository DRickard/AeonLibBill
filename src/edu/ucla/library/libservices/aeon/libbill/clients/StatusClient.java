package edu.ucla.library.libservices.aeon.libbill.clients;

import edu.ucla.library.libservices.aeon.libbill.db.procs.UpdateInvoiceProcedure;
import edu.ucla.library.libservices.aeon.libbill.utility.signatures.SignatureBuilder;

import java.util.Properties;

public class StatusClient
{
  private String invoiceNumber;
  private String whoBy;
  private Properties props;

  public StatusClient()
  {
    super();
  }

  public void updateStatus()
  {
    UpdateInvoiceProcedure proc;
    proc = new UpdateInvoiceProcedure();
    proc.setProps( getProps() );
    proc.setInvoiceNumber( getInvoiceNumber() );
    proc.setStatus( "Unpaid" );
    proc.setWhoBy( getWhoBy() );
    
    proc.updateInvoice();
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  private String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }

  public void setWhoBy( String whoBy )
  {
    this.whoBy = whoBy;
  }

  private String getWhoBy()
  {
    return whoBy;
  }
}
