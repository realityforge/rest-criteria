package org.realityforge.rest.criteria.model;

import javax.annotation.Nonnull;

public class AtomicCondition
  extends Condition
{
  public static enum Operator
  {
    EQUALS, NOT_EQUALS
  }

  private final Operator _operator;
  private final VariableExpression _lhs;
  private final Expression _rhs;

  public AtomicCondition( @Nonnull final Operator operator,
                          @Nonnull final VariableExpression lhs,
                          @Nonnull final Expression rhs )
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
  public VariableExpression getLhs()
  {
    return _lhs;
  }

  @Nonnull
  public Expression getRhs()
  {
    return _rhs;
  }
}
