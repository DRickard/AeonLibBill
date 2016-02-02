package edu.ucla.library.libservices.aeon.libbill.clients;

import edu.ucla.library.libservices.aeon.libbill.db.procs.AddInvoiceNoteProcedure;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InvoiceNote;

import java.util.Properties;

public class NoteClient
{
  private InvoiceNote theNote;
  private Properties props;

  public NoteClient()
  {
    super();
  }

  public void insertNote()
  {
    AddInvoiceNoteProcedure proc;
    
    proc = new AddInvoiceNoteProcedure();
    proc.setData( getTheNote() );
    proc.setProps( getProps() );
    proc.addNote();
  }

  public void setTheNote( InvoiceNote theNote )
  {
    this.theNote = theNote;
  }

  private InvoiceNote getTheNote()
  {
    return theNote;
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
