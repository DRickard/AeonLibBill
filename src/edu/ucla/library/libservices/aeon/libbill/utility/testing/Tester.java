package edu.ucla.library.libservices.aeon.libbill.utility.testing;

import edu.ucla.library.libservices.aeon.libbill.beans.SftpUserInfo;
import edu.ucla.library.libservices.aeon.libbill.main.ProcessInvoice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Map;
import java.util.Properties;

import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;


public class Tester
{
  private static final Logger logger =
    Logger.getLogger( ProcessInvoice.class );
  private static Properties props;
  private static File keyFile;
  private static FileSystemOptions opts = null;
  private static FileSystemManager fsManager = null;
  private static Map<String, Vector<String>> patronFiles;
  private static Vector<String> yrl;
  private static Vector<String> biomed;

  public Tester()
  {
    super();
  }

  public static void main( String[] args )
  {
    File localDir;
    File[] files;

    //get config properties for application
    loadProperties( args[ 0 ] );

    //get sftp connection
    getSftpConnect();
    localDir = new File( props.getProperty( "invoice.download.local" ) );
    files = localDir.listFiles();

    if ( files != null && files.length != 0 )
    {
      groupPatrons( files );
      if ( patronFiles.keySet().size() > 0 )
      {
      }
    }
  }

  private static void loadProperties( String propFile )
  {
    props = new Properties();
    try
    {
      props.load( new FileInputStream( new File( propFile ) ) );
    }
    catch ( IOException ioe )
    {
      logger.fatal( " problem with props file: " + ioe.getMessage() );
      System.exit( -1 );
    }
  }

  private static void getSftpConnect()
  {
    try
    {
      opts = new FileSystemOptions();
      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking( opts,
                                                                          "no" );
      SftpFileSystemConfigBuilder.getInstance().setUserInfo( opts,
                                                             new SftpUserInfo() );
      keyFile = new File( props.getProperty( "keyfile.path" ) );
      SftpFileSystemConfigBuilder.getInstance().setIdentities( opts,
                                                               new File[]
          { keyFile } );
      fsManager = VFS.getManager();
    }
    catch ( FileSystemException fse )
    {
      logger.fatal( " problem with sftp connect: " + fse.getMessage() );
      System.exit( -2 );
    }
  }

  private static void groupPatrons( File[] files )
  {
    patronFiles = new TreeMap<String, Vector<String>>();
    for ( File theFile: files )
    {
      Document document;
      String patron;

      try
      {
        document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( theFile );
        patron =
            document.getElementsByTagName( "aeon-username" ).item( 0 ).getChildNodes().item( 0 ).getTextContent();
        if ( patronFiles.containsKey( patron ) )
          patronFiles.get( patron ).add( theFile.getAbsolutePath() );
        else
        {
          Vector<String> temp;
          temp = new Vector<String>();
          temp.add( theFile.getAbsolutePath() );
          patronFiles.put( patron, temp );
        }
      }
      catch ( ParserConfigurationException pce )
      {
        logger.fatal( pce.getMessage() );
      }
      catch ( SAXException saxe )
      {
        logger.fatal( saxe.getMessage() );
      }
      catch ( IOException ioe )
      {
        logger.fatal( ioe.getMessage() );
      }
    }
  }
}
