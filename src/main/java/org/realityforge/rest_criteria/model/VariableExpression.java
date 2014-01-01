package org.realityforge.rest_criteria.model;

import javax.annotation.Nonnull;

public class VariableExpression
  extends Expression
{
  private final String[] _path;

  public VariableExpression( @Nonnull final String[] path )
  {
    _path = path;
  }

  @Nonnull
  public String[] getPath()
  {
    return _path;
  }

  @Nonnull
  public String getPathAsString()
  {
    if ( _path.length == 0 )
    {
      return "";
    }

    final StringBuilder b = new StringBuilder();
    b.append( _path[ 0 ] );
    for ( int i = 1; i < _path.length; i++ )
    {
      b.append( "." ).append( _path[ i ] );
    }
    return b.toString();
  }
}
