package org.realityforge.rest.criteria.jpa;

import java.util.LinkedList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.realityforge.rest.criteria.CriteriaParser;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.AtomicCondition.Operator;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.Condition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

@SuppressWarnings("unchecked")
public abstract class AbstractQueryBuilder<T>
{
  private final CriteriaQuery<T> _criteriaQuery;

  protected interface ParameterSetter<T>
  {
    void apply( @Nonnull TypedQuery<T> typedQuery );
  }

  private final CriteriaBuilder _cb;
  private final Root<T> _root;

  private final LinkedList<ParameterSetter<T>> _parameterSetters = new LinkedList<>();

  protected AbstractQueryBuilder( @Nonnull final Class<T> type,
                                  @Nonnull final CriteriaBuilder cb,
                                  @Nullable final String input )
  {
    _cb = cb;
    _criteriaQuery = _cb.createQuery( type );
    _root = _criteriaQuery.from( type );
    _criteriaQuery.select( _root );
    applyRestrictions( input );
  }

  @Nonnull
  protected final CriteriaBuilder getCriteriaBuilder()
  {
    return _cb;
  }

  protected final void applyRestrictions( @Nullable final String input )
  {
    if ( null != input && !"".equals( input.trim() ) )
    {
      applyRestriction( parse( input ) );
    }
    else
    {
      applyRestriction( null );
    }
  }

  /**
   * Sub-classes can override this restriction to further constrain the search.
   * The predicate parameter is null if an empty or null search criteria was provided.
   */
  protected void applyRestriction( @Nullable final Predicate predicate )
  {
    if ( null != predicate )
    {
      _criteriaQuery.where( predicate );
    }
  }

  @Nonnull
  protected Root<T> getRoot()
  {
    return _root;
  }

  @Nonnull
  public final CriteriaQuery<T> getCriteriaQuery()
  {
    return _criteriaQuery;
  }

  public final void applyParameters( @Nonnull final TypedQuery<T> query )
  {
    for ( final ParameterSetter<T> setter : _parameterSetters )
    {
      setter.apply( query );
    }
  }

  @Nonnull
  protected Predicate parse( @Nonnull final String criteria )
  {
    final Condition condition = parseCondition( criteria );
    return processCondition( condition );
  }

  @Nonnull
  protected Condition parseCondition( @Nonnull final String criteria )
  {
    final CriteriaParser parser = new CriteriaParser( criteria );
    return parser.getCondition();
  }

  @Nonnull
  protected Predicate processCondition( @Nonnull final Condition condition )
  {
    if ( condition instanceof AtomicCondition )
    {
      return processAtomicPredicate( (AtomicCondition) condition );
    }
    else if ( condition instanceof BinaryCondition )
    {
      return processBinaryPredicate( (BinaryCondition) condition );
    }
    else if ( condition instanceof UnaryCondition )
    {
      return processUnaryPredicate( (UnaryCondition) condition );
    }
    else
    {
      throw new BadConditionException( "Invalid condition" );
    }
  }

  @Nonnull
  protected Predicate processAtomicPredicate( @Nonnull final AtomicCondition condition )
  {
    final Operator operator = condition.getOperator();
    switch ( operator )
    {
      case EQUALS:
        return processEqualsCondition( condition );
      case NOT_EQUALS:
        return processNotEqualsCondition( condition );
      case GREATER_THAN:
        return processGreaterThanCondition( condition );
      case LESS_THAN:
        return processLessThanCondition( condition );
      case GREATER_THAN_OR_EQUALS:
        return processGreaterThanOrEqualsCondition( condition );
      case LESS_THAN_OR_EQUALS:
        return processLessThanOrEqualsCondition( condition );
      case LIKE:
        return processLikeCondition( condition );
      default:
        throw new BadConditionException( "Invalid operator in atomic predicate: " + operator );
    }
  }

  @Nonnull
  protected Predicate processNotEqualsCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().notEqual( processVariableExpression( condition.getLhs() ),
                                          processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected Predicate processEqualsCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().equal( processVariableExpression( condition.getLhs() ),
                                       processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected <Y extends Comparable<? super Y>> Predicate processGreaterThanCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().greaterThan( (Expression<Y>) processVariableExpression( condition.getLhs() ),
                                             (Expression<Y>) processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected <Y extends Comparable<? super Y>> Predicate processLessThanCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().lessThan( (Expression<Y>) processVariableExpression( condition.getLhs() ),
                                          (Expression<Y>) processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected <Y extends Comparable<? super Y>> Predicate processGreaterThanOrEqualsCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().greaterThanOrEqualTo( (Expression<Y>) processVariableExpression( condition.getLhs() ),
                                                      (Expression<Y>) processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected <Y extends Comparable<? super Y>> Predicate processLessThanOrEqualsCondition( @Nonnull final AtomicCondition condition )
  {
    return getCriteriaBuilder().lessThanOrEqualTo( (Expression<Y>) processVariableExpression( condition.getLhs() ),
                                                   (Expression<Y>) processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected Predicate processLikeCondition( @Nonnull final AtomicCondition condition )
  {
    //noinspection unchecked
    return getCriteriaBuilder().like( (Expression<String>) processVariableExpression( condition.getLhs() ),
                                      (Expression<String>) processExpression( condition.getRhs() ) );
  }

  @Nonnull
  protected Expression<?> processExpression( @Nonnull final org.realityforge.rest.criteria.model.Expression expression )
  {
    if ( expression instanceof ConstantExpression )
    {
      return processConstantExpression( (ConstantExpression) expression );
    }
    else if ( expression instanceof VariableExpression )
    {
      return processVariableExpression( (VariableExpression) expression );
    }
    else
    {
      throw new BadConditionException( "Invalid expression" );
    }
  }

  @Nonnull
  protected Expression<?> processConstantExpression( @Nonnull final ConstantExpression expression )
  {
    if ( expression.isBoolean() )
    {
      final ParameterExpression<Boolean> p = getCriteriaBuilder().parameter( Boolean.class );
      addParameterSetter( new ParameterSetter<T>()
      {
        @Override
        public void apply( @Nonnull final TypedQuery<T> typedQuery )
        {
          typedQuery.setParameter( p, expression.asBoolean() );
        }
      } );
      return p;
    }
    else if ( expression.isNumeric() )
    {
      final ParameterExpression<Number> p = getCriteriaBuilder().parameter( Number.class );
      addParameterSetter( new ParameterSetter<T>()
      {
        @Override
        public void apply( @Nonnull final TypedQuery<T> typedQuery )
        {
          typedQuery.setParameter( p, expression.asNumeric() );
        }
      } );
      return p;
    }
    else if ( expression.isText() )
    {
      final ParameterExpression<String> p = getCriteriaBuilder().parameter( String.class );
      addParameterSetter( new ParameterSetter<T>()
      {
        @Override
        public void apply( @Nonnull final TypedQuery<T> typedQuery )
        {
          typedQuery.setParameter( p, expression.asText() );
        }
      } );
      return p;
    }
    else
    {
      throw new BadConditionException( "Invalid constant expression: " + expression.getValue() );
    }
  }

  protected void addParameterSetter( @Nonnull final ParameterSetter<T> setter )
  {
    _parameterSetters.add( setter );
  }

  @Nonnull
  protected abstract Expression<?> processVariableExpression( @Nonnull VariableExpression expression );

  @Nonnull
  protected Predicate processBinaryPredicate( @Nonnull final BinaryCondition condition )
  {
    switch ( condition.getOperator() )
    {
      case AND:
        return processAndPredicate( condition );
      case OR:
        return processOrPredicate( condition );
      default:
        throw new BadConditionException( "Invalid binary predicate" );
    }
  }

  @Nonnull
  protected Predicate processOrPredicate( @Nonnull final BinaryCondition condition )
  {
    return getCriteriaBuilder().or( processCondition( condition.getLhs() ), processCondition( condition.getRhs() ) );
  }

  @Nonnull
  protected Predicate processAndPredicate( @Nonnull final BinaryCondition condition )
  {
    return getCriteriaBuilder().and( processCondition( condition.getLhs() ),
                                     processCondition( condition.getRhs() ) );
  }

  @Nonnull
  protected Predicate processUnaryPredicate( @Nonnull final UnaryCondition condition )
  {
    switch ( condition.getOperator() )
    {
      case NOT:
        return processNotPredicate( condition );
      default:
        throw new BadConditionException( "Invalid unary predicate" );
    }
  }

  @Nonnull
  protected Predicate processNotPredicate( @Nonnull final UnaryCondition condition )
  {
    return getCriteriaBuilder().not( processCondition( condition.getCondition() ) );
  }
}
