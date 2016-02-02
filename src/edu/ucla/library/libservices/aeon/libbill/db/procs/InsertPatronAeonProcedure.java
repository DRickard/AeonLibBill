package edu.ucla.library.libservices.aeon.libbill.db.procs;

import edu.ucla.library.libservices.aeon.libbill.beans.Patron;

import edu.ucla.library.libservices.aeon.libbill.db.source.DataSourceFactory;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

import java.util.Properties;

import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.object.StoredProcedure;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class InsertPatronAeonProcedure
  extends StoredProcedure
{
  //private DataSource dataSource;
  private DriverManagerDataSource dataSource;
  private Patron thePatron;
  private Properties props;

  public InsertPatronAeonProcedure( JdbcTemplate jdbcTemplate,
                                    String string )
  {
    super( jdbcTemplate, string );
  }

  public InsertPatronAeonProcedure( DataSource dataSource, String string )
  {
    super( dataSource, string );
  }

  public InsertPatronAeonProcedure()
  {
    super();
  }

  public void setThePatron( Patron thePatron )
  {
    this.thePatron = thePatron;
  }

  private Patron getThePatron()
  {
    return thePatron;
  }

  public int addPatron()
  {
    Map results;

    makeConnection();
    results = new HashMap();
    if ( dataSource != null )
    {
      prepProc();
      results = execute();
    }
    releaseConnection();
    
    return Integer.parseInt( results.get( "p_patron_id" ).toString() );
  }

  private void makeConnection()
  {
    dataSource = DataSourceFactory.createSource(getProps());
  }

  private void prepProc()
  {
    setDataSource( dataSource );
    setFunction( false );
    setSql( "insert_patron_aeon" );
    declareParameter( new SqlParameter( "p_username", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_last_name", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_first_name", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_aeon_id", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_email", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_phone_number",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_address1",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_address2",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_city", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_state", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_zip", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_perm_country",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_billing_category",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_address1",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_address2",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_city", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_state", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_zip", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_temp_country",
                                        Types.VARCHAR ) );
    declareParameter( new SqlOutParameter( "p_patron_id",
                                           Types.INTEGER ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;
    int patronID;

    out = null;
    input = new HashMap();
    patronID = -1;

    input.put( "p_username", getThePatron().getAeonUsername() );
    input.put( "p_last_name", getThePatron().getLastName() );
    input.put( "p_first_name", getThePatron().getFirstName() );
    input.put( "p_aeon_id", getThePatron().getAeonID() );
    input.put( "p_email", getThePatron().getEmailAddress() );
    input.put( "p_phone_number", getThePatron().getPhone() );
    input.put( "p_perm_address1", getThePatron().getAddress1() );
    input.put( "p_perm_address2", getThePatron().getAddress2() );
    input.put( "p_perm_city", getThePatron().getCity() );
    input.put( "p_perm_state", getThePatron().getState() );
    input.put( "p_perm_zip", getThePatron().getZip() );
    input.put( "p_perm_country", getThePatron().getCountry() );
    input.put( "p_billing_category", getThePatron().getCategory() );
    input.put( "p_temp_address1", null );
    input.put( "p_temp_address2", null );
    input.put( "p_temp_city", null );
    input.put( "p_temp_state", null );
    input.put( "p_temp_zip", null );
    input.put( "p_temp_country", null );
    input.put( "p_patron_id", patronID );

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
