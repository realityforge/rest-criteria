package org.realityforge.rest.criteria.jpa;

import javax.annotation.Nonnull;

public class BadConditionException
  extends RuntimeException
{
  public BadConditionException( @Nonnull final String message )
  {
    super( message );
  }
}
