package org.realityforge.rest.criteria.jpa;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import org.realityforge.rest.criteria.model.VariableExpression;

final class TestQueryBuilder
  extends AbstractQueryBuilder<MyEntity>
{
  public TestQueryBuilder( @Nonnull final CriteriaBuilder cb,
                           @Nullable final String input )
  {
    super( MyEntity.class, cb, input );
  }

  @Override
  @Nonnull
  protected Expression<?> processVariableExpression( @Nonnull final VariableExpression expression )
  {
    final String[] path = expression.getPath();
    if ( 1 != path.length )
    {
      throw new BadConditionException( "Invalid variable expression: " + expression.getPathAsString() );
    }
    final String variable = path[ 0 ];
    switch ( variable )
    {
      case "code":
        return getRoot().get( MyEntity_.code );
      case "name":
        return getRoot().get( MyEntity_.name );
      case "rank":
        return getRoot().get( MyEntity_.rank );
      case "allDay":
        return getRoot().get( MyEntity_.allDay );
      default:
        throw new BadConditionException( "Invalid variable expression: " + variable );
    }
  }
}
