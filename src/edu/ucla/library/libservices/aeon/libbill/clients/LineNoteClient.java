package edu.ucla.library.libservices.aeon.libbill.clients;

import edu.ucla.library.libservices.aeon.libbill.beans.LineItemNote;

import edu.ucla.library.libservices.aeon.libbill.db.procs.AddLineItemNoteProcedure;

import java.util.Properties;

public class LineNoteClient
{
  private LineItemNote theNote;
  private Properties props;

  public LineNoteClient()
  {
    super();
  }
  
  public void insertNote()
  {
    AddLineItemNoteProcedure proc;
    
    proc = new AddLineItemNoteProcedure();
    proc.setData( getTheNote() );
    proc.setProps( getProps() );
    proc.addNote();
  }
  
  public void setTheNote( LineItemNote theNote )
  {
    this.theNote = theNote;
  }

  private LineItemNote getTheNote()
  {
    return theNote;
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
