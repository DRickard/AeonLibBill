package edu.ucla.library.libservices.aeon.libbill.db.source;

import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceFactory
{
  public DataSourceFactory()
  {
    super();
  }

  public static DriverManagerDataSource createSource(Properties props)
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( props.getProperty( "db.driver" ) );
    ds.setUrl( props.getProperty( "db.invoice.url" ) );
    ds.setUsername( props.getProperty( "db.invoice.user" ) );
    ds.setPassword( props.getProperty( "db.invoice.password" ) );

    return ds;
  }
}
