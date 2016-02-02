package edu.ucla.library.libservices.aeon.libbill.db.procs;

import edu.ucla.library.libservices.aeon.libbill.db.source.DataSourceFactory;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class InsertInvoiceAeonRequestProcedure
  extends StoredProcedure
{
  private DataSource dataSource;
  private int aeonRequest;
  private String invoiceNumber;
  private Properties props;

  public InsertInvoiceAeonRequestProcedure( JdbcTemplate jdbcTemplate,
                                            String string )
  {
    super( jdbcTemplate, string );
  }

  public InsertInvoiceAeonRequestProcedure( DataSource dataSource,
                                            String string )
  {
    super( dataSource, string );
  }

  public InsertInvoiceAeonRequestProcedure()
  {
    super();
  }

  public void setAeonRequest( int aeonRequest )
  {
    this.aeonRequest = aeonRequest;
  }

  private int getAeonRequest()
  {
    return aeonRequest;
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  private String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  private void makeConnection()
  {
    dataSource = DataSourceFactory.createSource(getProps());
  }
  
  public void linkAeonInvoice()
  {
    Map results;

    makeConnection();
    if ( dataSource != null )
    {
      prepProc();
      results = execute();
    }
    releaseConnection();
  }

  private void prepProc()
  {
    setDataSource( dataSource );
    setFunction( false );
    setSql( "insert_invoice_aeon_request" );
    declareParameter( new SqlParameter( "p_aeon_request", Types.INTEGER ) );
    declareParameter( new SqlParameter( "p_invoice_number", Types.VARCHAR ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;

    out = null;
    input = new HashMap();

    input.put( "p_aeon_request", getAeonRequest() );
    input.put( "p_invoice_number", getInvoiceNumber() );

    out = execute( input );

    return out;
  }

  private void releaseConnection()
  {
    dataSource = null;
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
