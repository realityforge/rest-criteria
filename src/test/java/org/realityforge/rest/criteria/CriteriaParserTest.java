package org.realityforge.rest.criteria;

import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.BinaryCondition.Operator;
import org.realityforge.rest.criteria.model.Condition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class CriteriaParserTest
{
  @DataProvider( name = "Criterion" )
  public Object[][] criteriaProvider()
  {
    return new Object[][]
      {
        // simple nonsense criteria
        { "a=true", true },
        { "a = true", true },
        { "a = false", true },
        { "a = 1", true },
        { "a = -1", true },
        { "a=true AND b=false", true },
        { "a = true AND b = true", true },
        { "a = true OR b = true", true },
        { "a != true OR b != true", true },
        { "a=true AND b=1 AND C=3", true },
        { "myvar = 'str'", true },
        { "myvar = \"str\"", true },
        { "myvar != \"str\"", true },

        { "CamelCase = 1", true },
        { "rails_case = 33", true },
        { "number1 != -2", true },

        // simple criteria with $ criteria
        { "$a=true", true },
        { "$a = true", true },
        { "$a = false", true },
        { "$a = 1", true },
        { "$a = -1", true },
        { "$a=true AND $b=false", true },
        { "$a = true AND $b = true", true },
        { "$a = true OR $b = true", true },
        { "$a != true OR $b != true", true },
        { "$a=true AND $b=1 AND $C=3", true },
        { "$myvar = 'str'", true },
        { "$myvar = \"str\"", true },
        { "$myvar != \"str\"", true },

        { "$CamelCase = 1", true },
        { "$rails_case = 33", true },
        { "$number1 != -2", true },
        { "$Camel$Case = 1", true },

        // Constants on the left...
        { "1 = 1", false },
        { "44 = 1", false },
        { "'myvar' = 1", false },
        { "'myvar' = 1", false },
        { "\"myvar\" = 1", false },
        { "\"myvar\" = true", false },

        // Trailing whitespace
        { "a=true ", true },

        // Leading whitespace
        { " a=true", true },

        // Surrounding brackets
        { "(a = true)", true },
        // Surrounding brackets with spaces
        { "( a = true )", true },

        // Not without brackets
        { "NOT a.b.c.d = a.b.e", true },
        // Not with brackets
        { "NOT (a.b.c.d = a.b.e)", true },

        //Expression that is not a condition
        { "true", false },

        { "a = true", true },
        { "a.b = true", true },
        { "a.b.c = true", true },
        { "a.b.c = true", true },
        { "a.b.c = a.b.c", true },
        { "a.b.c.d = a.b.e AND a.b.e = 2 AND NOT (a.b.e = 3)", true },

        // Bad path variable
        { "a.b. = true", false },
        };
  }

  @SuppressWarnings( "PMD.EmptyCatchBlock" )
  @Test( dataProvider = "Criterion" )
  public void evaluateCriteria( final String criteria, final boolean valid )
  {
    boolean parsed = false;
    try
    {
      parse( criteria );
      parsed = true;
    }
    catch ( final LexerNoViableAltException e )
    {
      //ignored
    }
    catch ( final ParseCancellationException e )
    {
      //ignored
    }
    catch ( final RecognitionException e )
    {
      //ignored
    }

    assertEquals( parsed, valid );
  }

  @Test
  public void parseSimpleExpression()
  {
    final Condition condition = parse( "a = true" );
    assertTrue( condition instanceof AtomicCondition );
    final AtomicCondition lhs = (AtomicCondition) condition;
    final VariableExpression v = lhs.getLhs();
    final String[] path = v.getPath();
    assertEquals( path.length, 1 );
    assertEquals( path[ 0 ], "a" );
    assertTrue( lhs.getRhs() instanceof ConstantExpression );
    final ConstantExpression c = (ConstantExpression) lhs.getRhs();
    assertEquals( c.getValue(), Boolean.TRUE );
  }

  @Test
  public void notA()
  {
    final Condition topCondition = parse( "NOT a = true" );
    assertTrue( topCondition instanceof UnaryCondition );
    final UnaryCondition top = (UnaryCondition) topCondition;
    final Condition condition = top.getCondition();
    assertTrue( condition instanceof AtomicCondition );
    final AtomicCondition lhs = (AtomicCondition) condition;
    final VariableExpression v = lhs.getLhs();
    final String[] path = v.getPath();
    assertEquals( path.length, 1 );
    assertEquals( path[ 0 ], "a" );
    assertTrue( lhs.getRhs() instanceof ConstantExpression );
    final ConstantExpression c = (ConstantExpression) lhs.getRhs();
    assertEquals( c.getValue(), Boolean.TRUE );
  }

  @Test
  public void andOrCondition()
  {
    final Condition topCondition = parse( "a = 1 AND b = 2 OR c = 3" );
    assertTrue( topCondition instanceof BinaryCondition );
    final BinaryCondition top = (BinaryCondition) topCondition;
    assertEquals( top.getOperator(), Operator.OR );
    assertTrue( top.getRhs() instanceof AtomicCondition );
    assertTrue( top.getLhs() instanceof BinaryCondition );
  }

  private Condition parse( final String criteria )
  {
    return new CriteriaParser( criteria ).getCondition();
  }
}
