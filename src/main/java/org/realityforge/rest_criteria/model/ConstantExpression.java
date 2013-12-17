package org.realityforge.rest_criteria.model;

import javax.annotation.Nonnull;

public class ConstantExpression
  extends Expression
{
  private final Object _value;

  public ConstantExpression( @Nonnull final Object value )
  {
    _value = value;
    if ( !isText() && !isBoolean() && !isNumeric() )
    {
      throw new IllegalArgumentException();
    }
  }

  public Object getValue()
  {
    return _value;
  }

  public boolean isBoolean()
  {
    return _value instanceof Boolean;
  }

  public boolean isNumeric()
  {
    return _value instanceof Integer;
  }

  public boolean isText()
  {
    return _value instanceof String;
  }

  public Boolean asBoolean()
  {
    if ( !isBoolean() )
    {
      throw new IllegalStateException();
    }
    return (Boolean) _value;
  }

  public Long asNumeric()
  {
    if ( !isNumeric() )
    {
      throw new IllegalStateException();
    }
    return (Long) _value;
  }

  public String asText()
  {
    if ( !isText() )
    {
      throw new IllegalStateException();
    }
    return (String) _value;
  }
}
