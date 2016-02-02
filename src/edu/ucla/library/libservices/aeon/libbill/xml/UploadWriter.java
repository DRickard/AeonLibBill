package edu.ucla.library.libservices.aeon.libbill.xml;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;

import java.util.Properties;

public class UploadWriter
{
  private Properties props;
  private String invoiceNumber;
  private int aeonID;

  public UploadWriter()
  {
    super();
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  private String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setAeonID( int aeonID )
  {
    this.aeonID = aeonID;
  }

  private int getAeonID()
  {
    return aeonID;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }

  public void writeFile()
  {
    BufferedWriter writer;
    String directory;
    String fileName;

    directory = getProps().getProperty( "invoice.upload.local" );
    fileName = String.valueOf( getAeonID() ).concat( ".txt" );

    try
    {
      writer =
          new BufferedWriter( new FileWriter( new File( directory.concat( "/" ).concat( fileName ) ) ) );
      writer.write( "<libbill-invoice>" );
      writer.newLine();
      writer.write( "<aeon-request-id>" + getAeonID() +
                    "</aeon-request-id>" );
      writer.newLine();
      writer.write( "<libbill-invoice-number>" + getInvoiceNumber() +
                    "</libbill-invoice-number>" );
      writer.newLine();
      writer.write( "</libbill-invoice>" );
      writer.newLine();
      writer.flush();
      writer.close();
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }
}
