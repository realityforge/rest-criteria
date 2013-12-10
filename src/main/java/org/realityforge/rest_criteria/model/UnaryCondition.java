package org.realityforge.rest_criteria.model;

import javax.annotation.Nonnull;

public class UnaryCondition
  extends Condition
{
  public static enum Operator
  {
    NOT
  }

  private final Operator _operator;
  private final Condition _condition;

  public UnaryCondition( @Nonnull final Operator operator, @Nonnull final Condition condition )
  {
    _operator = operator;
    _condition = condition;
  }

  @Nonnull
  public Operator getOperator()
  {
    return _operator;
  }

  @Nonnull
  public Condition getCondition()
  {
    return _condition;
  }
}
