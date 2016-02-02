package edu.ucla.library.libservices.aeon.libbill.beans;

import java.util.Date;

public class LineItemNote
{
  private String invoiceNumber;
  private int lineNumber;
  private int sequenceNumber;
  private boolean internal;
  private String createdBy;
  private Date createdDate;
  private String note;

  public LineItemNote()
  {
    super();
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setLineNumber( int lineNumber )
  {
    this.lineNumber = lineNumber;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setSequenceNumber( int sequenceNumber )
  {
    this.sequenceNumber = sequenceNumber;
  }

  public int getSequenceNumber()
  {
    return sequenceNumber;
  }

  public void setInternal( boolean internal )
  {
    this.internal = internal;
  }

  public boolean isInternal()
  {
    return internal;
  }

  public void setCreatedBy( String createdBy )
  {
    this.createdBy = createdBy;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedDate( Date createdDate )
  {
    this.createdDate = createdDate;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setNote( String note )
  {
    this.note = note;
  }

  public String getNote()
  {
    return note;
  }
}
