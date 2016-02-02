package edu.ucla.library.libservices.aeon.libbill.beans;

public class Patron
{
  private String aeonUsername;
  private String aeonID;
  private int voyagerPatronID;
  private String lastName;
  private String firstName;
  private String emailAddress;
  private String phone;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String country;
  private String category;

  public Patron()
  {
    super();
  }

  public void setAeonUsername( String aeonUsername )
  {
    this.aeonUsername = aeonUsername;
  }

  public String getAeonUsername()
  {
    return aeonUsername;
  }

  public void setVoyagerPatronID( int voyagerPatronID )
  {
    this.voyagerPatronID = voyagerPatronID;
  }

  public int getVoyagerPatronID()
  {
    return voyagerPatronID;
  }

  public void setLastName( String lastName )
  {
    this.lastName = lastName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setFirstName( String firstName )
  {
    this.firstName = firstName;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setEmailAddress( String emailAddress )
  {
    this.emailAddress = emailAddress;
  }

  public String getEmailAddress()
  {
    return emailAddress;
  }

  public void setPhone( String phone )
  {
    this.phone = phone;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setAddress1( String address1 )
  {
    this.address1 = address1;
  }

  public String getAddress1()
  {
    return address1;
  }

  public void setAddress2( String address2 )
  {
    this.address2 = address2;
  }

  public String getAddress2()
  {
    return address2;
  }

  public void setCity( String city )
  {
    this.city = city;
  }

  public String getCity()
  {
    return city;
  }

  public void setState( String state )
  {
    this.state = state;
  }

  public String getState()
  {
    return state;
  }

  public void setZip( String zip )
  {
    this.zip = zip;
  }

  public String getZip()
  {
    return zip;
  }

  public void setCountry( String country )
  {
    this.country = country;
  }

  public String getCountry()
  {
    return country;
  }

  public void setCategory( String category )
  {
    this.category = category;
  }

  public String getCategory()
  {
    return category;
  }

  public void setAeonID( String aeonID )
  {
    this.aeonID = aeonID;
  }

  public String getAeonID()
  {
    return aeonID;
  }
}
