package edu.ucla.library.libservices.aeon.libbill.main;

import edu.ucla.library.libservices.aeon.libbill.beans.Header;
import edu.ucla.library.libservices.aeon.libbill.beans.LineItem;
import edu.ucla.library.libservices.aeon.libbill.beans.LineItemNote;
import edu.ucla.library.libservices.aeon.libbill.beans.Patron;
import edu.ucla.library.libservices.aeon.libbill.beans.SftpUserInfo;
import edu.ucla.library.libservices.aeon.libbill.clients.HeaderClient;
import edu.ucla.library.libservices.aeon.libbill.clients.LineItemClient;
import edu.ucla.library.libservices.aeon.libbill.clients.LineNoteClient;
import edu.ucla.library.libservices.aeon.libbill.clients.NoteClient;
//import edu.ucla.library.libservices.aeon.libbill.clients.StatusClient;
import edu.ucla.library.libservices.aeon.libbill.db.procs.InsertInvoiceAeonRequestProcedure;
import edu.ucla.library.libservices.aeon.libbill.db.procs.InsertPatronAeonProcedure;
import edu.ucla.library.libservices.aeon.libbill.db.source.DataSourceFactory;
import edu.ucla.library.libservices.aeon.libbill.email.ErrorMailer;
import edu.ucla.library.libservices.aeon.libbill.xml.HeaderFiller;
import edu.ucla.library.libservices.aeon.libbill.xml.LineItemFiller;
import edu.ucla.library.libservices.aeon.libbill.xml.PatronFiller;

import edu.ucla.library.libservices.aeon.libbill.xml.UploadWriter;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;

import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InvoiceNote;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Date;
//import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.sql.DataSource;

import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import org.apache.log4j.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

public class ProcessInvoice
{
  private static final String PATRON_COUNT =
    "SELECT count(*) FROM patron_vw WHERE patron_barcode = ?";
  private static final String PATRON_ID =
    "SELECT patroN_id FROM patron_vw WHERE patron_barcode = ?";
  private static final String CHARGE_TYPE =
    "SELECT require_custom_price FROM location_service_vw WHERE location_service_id = ?";
  private static final String HEADER_NOTE =
    ": If you have any questions regarding the charges listed on this invoice," +
    " please email speccoll-duplication@library.ucla.edu";
  private static final String PRICE_NOTE =
    "THIS CHARGE NEEDS TO BE CORRECTED TO A CUSTOM AMOUNT";
  private static final String LINE_NOTE = "Line generated from Aeon request ";
  private static final Logger logger =
    Logger.getLogger( ProcessInvoice.class );

  private static DataSource ds;
  private static Properties props;
  private static File keyFile;
  private static FileObject src = null;
  private static FileSystemManager fsManager = null;
  private static FileSystemOptions opts = null;
  private static Header theHeader;
  private static Map<String, Vector<String>> patronFiles;
  private static Vector<String> yrl;
  private static Vector<String> biomed;

  public ProcessInvoice()
  {
    super();
  }

  public static void main( String[] args )
  {
    //get config properties for application
    loadProperties( args[ 0 ] );

    //get sftp connection
    getSftpConnect();

    if ( downloadFiles() )
    {
      File localDir;
      File[] files;

      //get db connection
      makeDbConnection();

      localDir = new File( props.getProperty( "invoice.download.local" ) );
      files = localDir.listFiles();

      if ( files != null && files.length != 0 )
      {
        //group files per patron
        groupPatrons( files );
        if ( patronFiles.keySet().size() > 0 )
        {
          //process patron from first file per patron
          for ( String patronKey: patronFiles.keySet() )
          {
            int patronID;
            patronID =
                processPatron( new File( patronFiles.get( patronKey ).get( 0 ) ) );

            if ( patronID != 0 )
            {
              //group files by location
              groupLibraries( patronFiles.get( patronKey ) );

              //build invoice per library
              if ( yrl.size() > 0 )
              {
                generateInvoice( patronID, yrl );
                removeRemoteFiles();
              }
              if ( biomed.size() > 0 )
              {
                generateInvoice( patronID, biomed );
                removeRemoteFiles();
              }
            }
            else
            {
              logger.fatal( "could not match/create patron for Aeon user name " +
                            patronKey );
              /*mailError( "Could not match/create patron for Aeon user name " +
                         patronKey, "",
                         patronFiles.get( patronKey ).get( 0 ).substring( 0,
                                                                          patronFiles.get( patronKey ).get( 0 ).indexOf( "." ) ) );*/
            }
          }
        }
        else
        {
          logger.fatal( " no patrons retrieved from files; exiting" );
          /*mailError( "No patrons retrieved from files", "",
                     concatReqIDs( files ) );*/
          System.exit( -5 );
        }
        removeLocalFiles();
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

  private static boolean downloadFiles()
  {
    FileObject[] children;
    FileObject sftpFile;
    String startPath;

    startPath =
        "sftp://" + props.getProperty( "sftp.user" ) + "@" + props.getProperty( "sftp.host" ) +
        "/" + props.getProperty( "invoice.download.remote" );
    try
    {
      sftpFile = fsManager.resolveFile( startPath, opts );
      children = sftpFile.getChildren();
      if ( children.length > 0 )
      {
        for ( FileObject theChild: children )
        {
          LocalFile localFile;
          StringBuffer localUrl;

          localUrl =
              new StringBuffer( "file://" ).append( props.getProperty( "invoice.download.local" ) ).append( File.separatorChar ).append( theChild.getName().getBaseName() );
          localFile =
              ( LocalFile ) fsManager.resolveFile( localUrl.toString() );
          if ( !localFile.getParent().exists() )
          {
            localFile.getParent().createFolder();
          }
          localFile.copyFrom( theChild, new AllFileSelector() );
        }
        src = children[ 0 ];
        return true;
      }
      else
      {
        return false;
      }
    }
    catch ( FileSystemException fse )
    {
      logger.fatal( " error during download process: " +
                    fse.getMessage() );
      fse.printStackTrace();
      System.exit( -3 );
    }
    return true;
  }

  private static void makeDbConnection()
  {
    ds = DataSourceFactory.createSource( props );
  }

  private static int processPatron( File theFile )
  {
    int patronID;
    PatronFiller patronFiller;
    Patron thePatron;

    patronID = 0;

    patronFiller = new PatronFiller();
    patronFiller.setSource( theFile );
    thePatron = patronFiller.getThePatron();

    logger.info( "downloaded patron " + thePatron.getLastName() + ", " +
                 thePatron.getFirstName() + "\tID = " +
                 thePatron.getVoyagerPatronID() );
    if ( thePatron.getVoyagerPatronID() > 0 )
      patronID = thePatron.getVoyagerPatronID();
    else if ( ( new JdbcTemplate( ds ).queryForInt( PATRON_COUNT,
                                                    new Object[]
        { thePatron.getAeonUsername() } ) ) != 0 )
      patronID =
          new JdbcTemplate( ds ).queryForInt( PATRON_ID, new Object[]
            { thePatron.getAeonUsername() } );
    else
    {
      InsertPatronAeonProcedure proc;
      proc = new InsertPatronAeonProcedure();
      proc.setThePatron( thePatron );
      proc.setProps( props );
      patronID = proc.addPatron();
    }

    logger.info( "patron ID = " + patronID );
    return patronID;
  }

  private static String createHeader( Vector<String> theFiles,
                                      int patronID )
  {
    String invoice;
    HeaderClient theClient;
    HeaderFiller headerFiller;
    InsertHeaderBean insertHeader;

    invoice = "";
    headerFiller = new HeaderFiller();
    headerFiller.setSources( theFiles );
    try
    {
      theHeader = headerFiller.getTheHeader();

      insertHeader = new InsertHeaderBean();
      insertHeader.setBranchCode( theHeader.getLibrary().equalsIgnoreCase( "YRL" ) ?
                                  "SC": "BC" );
      insertHeader.setCreatedBy( "aeon_user" );
      insertHeader.setInvoiceDate( new Date() );
      insertHeader.setOnPremises( ( theHeader.getOnPremises() == null ||
                                    theHeader.getOnPremises().equalsIgnoreCase( "yes" ) ) ?
                                  "Y": "N" );
      insertHeader.setPatronID( patronID );
      insertHeader.setStatus( "Pending" );

      theClient = new HeaderClient();
      theClient.setProps( props );
      theClient.setTheHeader( insertHeader );

      invoice = theClient.insertHeader();

      System.out.println( "invoice # = " + invoice );
    }
    catch ( Exception e )
    {
      logger.error( " problem in header creation: " + e.getMessage() );
    }
    return invoice;
  }

  private static int addLineItems( File theFile, String invoiceNo,
                                   int lineCount )
  {
    LineItemClient theClient;
    LineItemFiller lineFiller;
    Vector<LineItem> items;

    lineFiller = new LineItemFiller();
    lineFiller.setSource( theFile );

    items = lineFiller.getTheItems();

    theClient = new LineItemClient();
    theClient.setProps( props );

    for ( LineItem anItem: items )
    {
      LineItemBean bean;
      boolean priceRequired;
      String reqID;
      bean = new LineItemBean();

      try
      {
        priceRequired = requirePrice( anItem.getCode() );
        reqID = theFile.getName().substring( 0, theFile.getName().indexOf( "." ) );
        bean.setBranchServiceID( anItem.getCode() );
        bean.setCreatedBy( "aeon_user" );
        bean.setCreatedDate( new Date() );
        bean.setInvoiceNumber( invoiceNo );
        bean.setQuantity( anItem.getQuantity() );
        bean.setUnitPrice( priceRequired ? 0.01D: 0.00D );

        theClient.setTheLine( bean );
        theClient.insertLine();
        lineCount += 1;
        addLineNote( invoiceNo, lineCount, LINE_NOTE.concat( reqID ),  false );
        if ( priceRequired )
          addLineNote( invoiceNo, lineCount, PRICE_NOTE,  true );
      }
      catch ( Exception e )
      {
        System.out.println( "problem with line-item entry: " + e.getMessage() );
        logger.error( "problem with line-item entry: " + e.getMessage() );
        //return lineCount;
        /*mailError( "Problem creating line item with item code " +
                   bean.getBranchServiceID(), invoiceNo,
                   theFile.getName().substring( 0,
                                                theFile.getName().indexOf( "." ) ) );*/
      }
      /*
       * need to add logic to increment/determine line number
       * add public line note of request ID that line comes from
       * plus private note if requirePrice( anItem.getCode() )
       */
    }
    return lineCount;
  }

  private static void addNote( String invoiceNo )
  {
    InvoiceNote theNote;
    NoteClient theClient;

    try
    {
      theNote = new InvoiceNote();
      theNote.setCreatedBy( "aeon_user" );
      theNote.setCreatedDate( new Date() );
      theNote.setInternal( false );
      theNote.setInvoiceNumber( invoiceNo );
      theNote.setNote( HEADER_NOTE );

      theClient = new NoteClient();
      theClient.setProps( props );
      theClient.setTheNote( theNote );
      theClient.insertNote();
    }
    catch ( Exception e )
    {
      logger.error( "problem with note entry: " + e.getMessage() );
    }
  }

  /*private static void setStatus( String invoiceNo, String reqID )
  {
    StatusClient theClient;

    theClient = new StatusClient();
    theClient.setInvoiceNumber( invoiceNo );
    theClient.setProps( props );
    theClient.setWhoBy( "aeon_user" );

    try
    {

    }
    catch ( Exception e )
    {
      logger.error( "problem with setting invoice status: " +
                    e.getMessage() );
      mailError( "Problem setting invoice status", invoiceNo, reqID );
    }
    theClient.updateStatus();
  }*/

  private static void linkInvoiceRequests( String invoiceNo )
  {
    for ( String aeonReq: theHeader.getAeonRequest() )
    {
      InsertInvoiceAeonRequestProcedure proc;
      proc = new InsertInvoiceAeonRequestProcedure();
      proc.setInvoiceNumber( invoiceNo );
      proc.setAeonRequest( Integer.parseInt( aeonReq ) );
      proc.setProps( props );
      try
      {
        proc.linkAeonInvoice();
      }
      catch ( Exception e )
      {
        System.err.println( "problem with linking invoice " + invoiceNo +
                            " and request " + aeonReq + ": " +
                            e.getMessage() );
        logger.error( "problem with linking invoice " + invoiceNo +
                      " and request " + aeonReq + ": " + e.getMessage() );
      }
    }
  }

  private static void removeRemoteFiles()
  {
    FileObject sftpFile;
    String startPath;
    
    System.out.println( "entering removeRemoteFiles" );
    
    startPath =
        "sftp://" + props.getProperty( "sftp.user" ) + "@" + props.getProperty( "sftp.host" ) +
        "/" + props.getProperty( "invoice.download.remote" );
    System.out.println( "removeRemoteFiles startPath = " + startPath );
    for ( String aeonReq: theHeader.getAeonRequest() )
    {
      System.out.println( "working with file " + startPath.concat( "/".concat( aeonReq.concat( ".txt" ) ) ) );
      try
      {
        sftpFile =
            fsManager.resolveFile( startPath.concat( "/".concat( aeonReq.concat( ".txt" ) ) ),
                                   opts );
        if ( sftpFile.exists() )
        {
          sftpFile.delete();
        }
      }
      catch ( FileSystemException fse )
      {
        logger.fatal( "error during file delete process" + fse );
        //System.exit( -4 );
      }
    }
  }

  private static void makeLinkFiles( String invoiceNo )
  {
    for ( String aeonReq: theHeader.getAeonRequest() )
    {
      UploadWriter writer;
      writer = new UploadWriter();
      writer.setAeonID( Integer.parseInt( aeonReq ) );
      writer.setInvoiceNumber( invoiceNo );
      writer.setProps( props );
      try
      {
        writer.writeFile();
      }
      catch ( Exception e )
      {
        logger.error( "problem with linking file for invoice " +
                      invoiceNo + " and request " + aeonReq + ": " +
                      e.getMessage() );
      }
    }
  }

  private static void uploadLinkFiles()
  {
    File file;
    FileObject localFile;
    FileObject remoteFile;
    StandardFileSystemManager manager;
    String directory;
    String fileName;
    String startPath;

    directory = props.getProperty( "invoice.upload.local" );
    for ( String aeonReq: theHeader.getAeonRequest() )
    {
      fileName = aeonReq.concat( ".txt" );
      file = new File( directory.concat( "/" ).concat( fileName ) );

      startPath =
          "sftp://" + props.getProperty( "sftp.user" ) + "@" + props.getProperty( "sftp.host" ) +
          "/" + props.getProperty( "invoice.upload.remote" ) + "/" +
          fileName;

      manager = new StandardFileSystemManager();

      try
      {
        manager.init();
        localFile = manager.resolveFile( file.getAbsolutePath() );
        remoteFile = manager.resolveFile( startPath, opts );
        remoteFile.copyFrom( localFile, Selectors.SELECT_SELF );
      }
      catch ( FileSystemException fse )
      {
        logger.error( " error uploading link files: " + fse.getMessage() );
      }
      finally
      {
        manager.close();
      }
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

  private static void groupLibraries( Vector<String> files )
  {
    Document document;
    String library;

    yrl = new Vector<String>();
    biomed = new Vector<String>();
    for ( String fileName: files )
    {
      try
      {
        document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new File( fileName ) );
        library =
            document.getElementsByTagName( "aeon-location" ).item( 0 ).getChildNodes().item( 0 ).getTextContent();
        if ( library.equalsIgnoreCase( "YRL" ) )
          yrl.add( fileName );
        else
          biomed.add( fileName );
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

  private static void generateInvoice( int patronID, Vector<String> files )
  {
    String invoiceNo;
    int lineNumber = 0;

    invoiceNo = createHeader( files, patronID );
    if ( invoiceNo != null && invoiceNo.length() > 0 )
    {
      System.out.println( "new invoice number: " + invoiceNo );
      for ( String fileName: files )
      {
        File source;
        source = new File( fileName );
        lineNumber = addLineItems( source, invoiceNo, lineNumber );
      }
      System.out.println( "line count = " + lineNumber );
      addNote( invoiceNo );
      //setStatus( invoiceNo, concatReqIDs( files.toArray() ) );
      linkInvoiceRequests( invoiceNo );
      makeLinkFiles( invoiceNo );
      uploadLinkFiles();
    }
    else
    {
      /*mailError( " Problem creating invoice header", "",
                 concatReqIDs( files.toArray() ) );*/
    }
  }

  /*private static String concatNotes()
  {
    StringBuffer buffer;
    Set<String> notes;

    buffer = new StringBuffer();
    notes = theHeader.getNote();

    for ( String note: notes )
      buffer.append( note ).append( "; " );

    if ( buffer.length() > 1000 )
      buffer.setLength( 1000 );

    return buffer.toString();
  }*/

  private static void removeLocalFiles()
  {
    File downDir;
    File upDir;
    File[] filesToDelete;

    downDir = new File( props.getProperty( "invoice.download.local" ) );
    filesToDelete = downDir.listFiles();
    for ( File delete: filesToDelete )
    {
      try
      {
        System.out.println( "deleteing file " + delete.getCanonicalPath() );
      }
      catch ( IOException ioe )
      {
        logger.warn( ioe.getMessage() );
      }
      delete.delete();
    }

    upDir = new File( props.getProperty( "invoice.upload.local" ) );
    filesToDelete = upDir.listFiles();
    for ( File delete: filesToDelete )
    {
      try
      {
        System.out.println( "deleteing file " + delete.getCanonicalPath() );
      }
      catch ( IOException ioe )
      {
        logger.warn( ioe.getMessage() );
      }
      delete.delete();
    }
  }

  private static String concatReqIDs( File[] files )
  {
    StringBuffer buffer;
    buffer = new StringBuffer();

    for ( File theFile: files )
    {
      buffer.append( theFile.getName().substring( 0,
                                                  theFile.getName().indexOf( "." ) ) ).append( ";" );
    }

    return buffer.toString();
  }


  private static String concatReqIDs( Object[] files )
  {
    StringBuffer buffer;
    buffer = new StringBuffer();

    for ( Object theFile: files )
    {
      String fileName;
      fileName = theFile.toString();
      buffer.append( fileName.substring( 0,
                                         fileName.indexOf( "." ) ) ).append( ";" );
    }

    return buffer.toString();
  }

  private static void mailError( String errorMessage, String invoice,
                                 String reqID )
  {
    ErrorMailer theMailer;

    theMailer = new ErrorMailer();
    theMailer.sendMessage( errorMessage, invoice, reqID,
                           props.getProperty( "mail.toaddress.libbill" ) );
  }

  private static boolean requirePrice( int code )
  {
    String result =
      new JdbcTemplate( ds ).queryForObject( CHARGE_TYPE, new Object[]
        { code }, String.class ).toString();
    return result.equalsIgnoreCase( "Y" ) ? true: false;
  }

  private static void addLineNote( String invoiceNo, int lineNumber,
                                   String note, boolean isPublic )
  {
    LineItemNote theNote;
    LineNoteClient theClient;

    theNote = new LineItemNote();
    theNote.setCreatedBy( "aeon_user" );
    theNote.setCreatedDate( new Date() );
    theNote.setInternal( isPublic );
    theNote.setInvoiceNumber( invoiceNo );
    theNote.setLineNumber( lineNumber );
    theNote.setNote( note );

    theClient = new LineNoteClient();
    theClient.setProps( props );
    theClient.setTheNote( theNote );
    theClient.insertNote();
  }
}
