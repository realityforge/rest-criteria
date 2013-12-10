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
}
