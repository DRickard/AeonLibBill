package edu.ucla.library.libservices.aeon.libbill.utility.signatures;

import edu.ucla.library.libservices.invoicing.security.signatures.SignatureCreator;

import java.security.SignatureException;

import java.util.List;

public class SignatureBuilder
{
  public SignatureBuilder()
  {
    super();
  }

  public static String buildSimpleSignature( String method, String uri )
  {
    StringBuffer output;
    output = new StringBuffer( method ).append( "\n" );
    output.append( uri ).append( "\n" );
    output.append( "-30-" );

    return output.toString();
  }

  public static String buildComplexSignature( String method, String uri,
                                              List<String> content )
  {
    StringBuffer output;
    output = new StringBuffer( method ).append( "\n" );
    output.append( uri ).append( "\n" );
    for ( String theLine: content )
    {
      output.append( theLine ).append( "\n" );
    }
    output.append( "-30-" );

    return output.toString();
  }

  public static String computeAuth( String request, String user,
                                    String crypt )
  {
    try
    {
      return user.concat( ":" ).concat( SignatureCreator.hashSignature( request,
                                                                        crypt ) );
    }
    catch ( SignatureException se )
    {
      se.printStackTrace();
      return null;
    }
  }
}
