package org.realityforge.rest.criteria.tree;

import javax.annotation.Nonnull;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

public interface CriteriaListener
{
  void startNotEqualsCondition( @Nonnull AtomicCondition condition );

  void endNotEqualsCondition( @Nonnull AtomicCondition condition );

  void startEqualsCondition( @Nonnull AtomicCondition condition );

  void endEqualsCondition( @Nonnull AtomicCondition condition );

  void variableExpression( @Nonnull VariableExpression expression );

  void constantExpression( @Nonnull ConstantExpression expression );

  void endOrPredicate( @Nonnull BinaryCondition condition );

  void startOrPredicate( @Nonnull BinaryCondition condition );

  void startAndPredicate( @Nonnull BinaryCondition condition );

  void endAndPredicate( @Nonnull BinaryCondition condition );

  void endNotPredicate( @Nonnull UnaryCondition condition );

  void startNotPredicate( @Nonnull UnaryCondition condition );
}
