package edu.ucla.library.libservices.aeon.libbill.beans;

public class LineItem
{
  private int code;
  private int quantity;
  private double price;
  
  public LineItem()
  {
    super();
  }

  public LineItem(int _code, int _quantity, double _price )
  {
    super();
    code = _code;
    quantity = _quantity;
    price = _price;
  }

  public void setCode( int code )
  {
    this.code = code;
  }

  public int getCode()
  {
    return code;
  }

  public void setQuantity( int quantity )
  {
    this.quantity = quantity;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public void setPrice( double price )
  {
    this.price = price;
  }

  public double getPrice()
  {
    return price;
  }
}
