package org.realityforge.rest_criteria.model;

import javax.annotation.Nonnull;

public class BinaryCondition
  extends Condition
{
  public static enum Operator
  {
    AND, OR
  }

  private final Operator _operator;
  private final Condition _lhs;
  private final Condition _rhs;

  public BinaryCondition( @Nonnull final Operator operator,
                          @Nonnull final Condition lhs,
                          @Nonnull final Condition rhs )
  {
    _operator = operator;
    _lhs = lhs;
    _rhs = rhs;
  }

  @Nonnull
  public Operator getOperator()
  {
    return _operator;
  }

  @Nonnull
  public Condition getLhs()
  {
    return _lhs;
  }

  @Nonnull
  public Condition getRhs()
  {
    return _rhs;
  }
}
