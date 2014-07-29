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
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.Condition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

public abstract class AbstractQueryBuilder<T>
{
  private final CriteriaQuery<T> _criteriaQuery;

  private interface ParameterSetter<T>
  {
    void apply( TypedQuery<T> typedQuery );
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

  protected Root<T> getRoot()
  {
    return _root;
  }

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

  protected final Predicate parse( final String criteria )
  {
    final CriteriaParser parser = new CriteriaParser( criteria );
    final Condition condition = parser.getCondition();
    return processCondition( condition );
  }

  private Predicate processCondition( final Condition condition )
  {
    if ( condition instanceof AtomicCondition )
    {
      return processAtomicCondition( (AtomicCondition) condition );
    }
    else if ( condition instanceof BinaryCondition )
    {
      return processBinaryCondition( (BinaryCondition) condition );
    }
    else if ( condition instanceof UnaryCondition )
    {
      return processUnaryCondition( (UnaryCondition) condition );
    }
    else
    {
      throw new BadConditionException( "Invalid condition" );
    }
  }

  private Predicate processAtomicCondition( final AtomicCondition condition )
  {
    final Expression<?> lhsExpression = processVariableExpression( condition.getLhs() );
    final Expression<?> rhsExpression = processExpression( condition.getRhs() );

    switch ( condition.getOperator() )
    {
      case EQUALS:
        return getCriteriaBuilder().equal( lhsExpression, rhsExpression );
      case NOT_EQUALS:
        return getCriteriaBuilder().notEqual( lhsExpression, rhsExpression );
      default:
        throw new BadConditionException( "Invalid operator" );
    }
  }

  private Expression<?> processExpression( final org.realityforge.rest.criteria.model.Expression expression )
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

  private Expression<?> processConstantExpression( final ConstantExpression expression )
  {
    if ( expression.isBoolean() )
    {
      final ParameterExpression<Boolean> p = getCriteriaBuilder().parameter( Boolean.class );
      addParameterSetter( new ParameterSetter<T>()
      {
        @Override
        public void apply( final TypedQuery<T> typedQuery )
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
        public void apply( final TypedQuery<T> typedQuery )
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
        public void apply( final TypedQuery<T> typedQuery )
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

  protected void addParameterSetter( final ParameterSetter<T> setter )
  {
    _parameterSetters.add( setter );
  }

  protected abstract Expression<?> processVariableExpression( @Nonnull VariableExpression expression );

  private Predicate processBinaryCondition( final BinaryCondition condition )
  {
    final Predicate lhsPredicate = processCondition( condition.getLhs() );
    final Predicate rhsPredicate = processCondition( condition.getRhs() );
    switch ( condition.getOperator() )
    {
      case AND:
        return getCriteriaBuilder().and( lhsPredicate, rhsPredicate );
      case OR:
        return getCriteriaBuilder().or( lhsPredicate, rhsPredicate );
      default:
        throw new BadConditionException( "Invalid binary operator" );
    }
  }

  private Predicate processUnaryCondition( final UnaryCondition condition )
  {
    switch ( condition.getOperator() )
    {
      case NOT:
        return getCriteriaBuilder().not( processCondition( condition.getCondition() ) );
      default:
        throw new BadConditionException( "Invalid unary operator" );
    }
  }
}
