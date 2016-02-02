package edu.ucla.library.libservices.aeon.libbill.clients;

import edu.ucla.library.libservices.aeon.libbill.db.procs.AddLineItemProcedure;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;
import java.util.Properties;

public class LineItemClient
{
  private LineItemBean theLine;
  private Properties props;

  public LineItemClient()
  {
    super();
  }

  public void insertLine()
  {
    AddLineItemProcedure proc;
    
    proc = new  AddLineItemProcedure();
    proc.setData( getTheLine() );
    proc.setProps( getProps() );
    proc.addLineItem();
  }

  public void setTheLine( LineItemBean theLine )
  {
    this.theLine = theLine;
  }

  private LineItemBean getTheLine()
  {
    return theLine;
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
