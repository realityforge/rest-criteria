package org.realityforge.rest.criteria.tree;

import javax.annotation.Nonnull;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.AtomicCondition.Operator;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.Condition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.Expression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

public final class TreeWalker
{
  private TreeWalker()
  {
  }

  public static void walk( @Nonnull final Condition condition, @Nonnull final CriteriaListener listener )
  {
    processCondition( condition, listener );
  }

  protected static void processCondition( @Nonnull final Condition condition,
                                          @Nonnull final CriteriaListener listener )
  {
    if ( condition instanceof AtomicCondition )
    {
      processAtomicPredicate( (AtomicCondition) condition, listener );
    }
    else if ( condition instanceof BinaryCondition )
    {
      processBinaryPredicate( (BinaryCondition) condition, listener );
    }
    else if ( condition instanceof UnaryCondition )
    {
      processUnaryPredicate( (UnaryCondition) condition, listener );
    }
    else
    {
      throw new IllegalStateException( "Invalid condition" );
    }
  }

  protected static void processAtomicPredicate( @Nonnull final AtomicCondition condition,
                                                @Nonnull final CriteriaListener listener )
  {
    final Operator operator = condition.getOperator();
    switch ( operator )
    {
      case EQUALS:
        processEqualsCondition( condition, listener );
        break;
      case NOT_EQUALS:
        processNotEqualsCondition( condition, listener );
        break;
      default:
        throw new IllegalStateException( "Invalid operator in atomic predicate: " + operator );
    }
  }

  protected static void processNotEqualsCondition( @Nonnull final AtomicCondition condition,
                                                   @Nonnull final CriteriaListener listener )
  {
    listener.startNotEqualsCondition( condition );
    listener.variableExpression( condition.getLhs() );
    processExpression( condition.getRhs(), listener );
    listener.endNotEqualsCondition( condition );
  }

  protected static void processEqualsCondition( @Nonnull final AtomicCondition condition,
                                                @Nonnull final CriteriaListener listener )
  {
    listener.startEqualsCondition( condition );
    listener.variableExpression( condition.getLhs() );
    processExpression( condition.getRhs(), listener );
    listener.endEqualsCondition( condition );
  }

  protected static void processExpression( @Nonnull final Expression expression,
                                           @Nonnull final CriteriaListener listener )
  {
    if ( expression instanceof ConstantExpression )
    {
      listener.constantExpression( (ConstantExpression) expression );
    }
    else if ( expression instanceof VariableExpression )
    {
      listener.variableExpression( (VariableExpression) expression );
    }
    else
    {
      throw new IllegalStateException( "Invalid expression" );
    }
  }

  protected static void processBinaryPredicate( @Nonnull final BinaryCondition condition,
                                                @Nonnull final CriteriaListener listener )
  {
    switch ( condition.getOperator() )
    {
      case AND:
        processAndPredicate( condition, listener );
        break;
      case OR:
        processOrPredicate( condition, listener );
        break;
      default:
        throw new IllegalStateException( "Invalid binary predicate" );
    }
  }

  protected static void processOrPredicate( @Nonnull final BinaryCondition condition,
                                            @Nonnull final CriteriaListener listener )
  {
    listener.startOrPredicate( condition );
    processCondition( condition.getLhs(), listener );
    processCondition( condition.getRhs(), listener );
    listener.endOrPredicate( condition );
  }

  protected static void processAndPredicate( @Nonnull final BinaryCondition condition,
                                             @Nonnull final CriteriaListener listener )
  {
    listener.startAndPredicate( condition );
    processCondition( condition.getLhs(), listener );
    processCondition( condition.getRhs(), listener );
    listener.endAndPredicate( condition );
  }

  protected static void processUnaryPredicate( @Nonnull final UnaryCondition condition,
                                               @Nonnull final CriteriaListener listener )
  {
    switch ( condition.getOperator() )
    {
      case NOT:
        processNotPredicate( condition, listener );
        break;
      default:
        throw new IllegalStateException( "Invalid unary predicate" );
    }
  }

  protected static void processNotPredicate( @Nonnull final UnaryCondition condition,
                                             @Nonnull final CriteriaListener listener )
  {
    listener.startNotPredicate( condition );
    processCondition( condition.getCondition(), listener );
    listener.endNotPredicate( condition );
  }
}
