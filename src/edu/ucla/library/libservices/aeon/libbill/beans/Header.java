package edu.ucla.library.libservices.aeon.libbill.beans;

import java.util.List;
import java.util.Set;

public class Header
{
  private List<String> aeonRequest;
  private String library;
  private Set<String> note;
  private String onPremises;
  
  public Header()
  {
    super();
  }

  public void setAeonRequest( List<String> aeonRequest )
  {
    this.aeonRequest = aeonRequest;
  }

  public List<String> getAeonRequest()
  {
    return aeonRequest;
  }

  public void setLibrary( String library )
  {
    this.library = library;
  }

  public String getLibrary()
  {
    return library;
  }

  public void setNote( Set<String> note )
  {
    this.note = note;
  }

  public Set<String> getNote()
  {
    return note;
  }

  public void setOnPremises( String onPremises )
  {
    this.onPremises = onPremises;
  }

  public String getOnPremises()
  {
    return onPremises;
  }
}
