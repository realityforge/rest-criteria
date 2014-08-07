package org.realityforge.rest.criteria.tree;

import javax.annotation.Nonnull;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

public abstract class CriteriaAdapter
  implements CriteriaListener
{
  @Override
  public void startNotEqualsCondition( @Nonnull final AtomicCondition condition )
  {
  }

  @Override
  public void endNotEqualsCondition( @Nonnull final AtomicCondition condition )
  {
  }

  @Override
  public void startEqualsCondition( @Nonnull final AtomicCondition condition )
  {
  }

  @Override
  public void endEqualsCondition( @Nonnull final AtomicCondition condition )
  {
  }

  @Override
  public void variableExpression( @Nonnull final VariableExpression expression )
  {
  }

  @Override
  public void constantExpression( @Nonnull final ConstantExpression expression )
  {
  }

  @Override
  public void endOrPredicate( @Nonnull final BinaryCondition condition )
  {
  }

  @Override
  public void startOrPredicate( @Nonnull final BinaryCondition condition )
  {
  }

  @Override
  public void startAndPredicate( @Nonnull final BinaryCondition condition )
  {
  }

  @Override
  public void endAndPredicate( @Nonnull final BinaryCondition condition )
  {
  }

  @Override
  public void endNotPredicate( @Nonnull final UnaryCondition condition )
  {
  }

  @Override
  public void startNotPredicate( @Nonnull final UnaryCondition condition )
  {
  }
}
