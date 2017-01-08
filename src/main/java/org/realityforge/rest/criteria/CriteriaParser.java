package org.realityforge.rest.criteria;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.annotation.Nonnull;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.realityforge.rest.criteria.RestCriteriaExprParser.Atomic_conditionContext;
import org.realityforge.rest.criteria.RestCriteriaExprParser.ConditionContext;
import org.realityforge.rest.criteria.RestCriteriaExprParser.ExprContext;
import org.realityforge.rest.criteria.RestCriteriaExprParser.Top_level_conditionContext;
import org.realityforge.rest.criteria.RestCriteriaExprParser.Var_exprContext;
import org.realityforge.rest.criteria.model.AtomicCondition;
import org.realityforge.rest.criteria.model.AtomicCondition.Operator;
import org.realityforge.rest.criteria.model.BinaryCondition;
import org.realityforge.rest.criteria.model.Condition;
import org.realityforge.rest.criteria.model.ConstantExpression;
import org.realityforge.rest.criteria.model.Element;
import org.realityforge.rest.criteria.model.Expression;
import org.realityforge.rest.criteria.model.NullExpression;
import org.realityforge.rest.criteria.model.UnaryCondition;
import org.realityforge.rest.criteria.model.VariableExpression;

public final class CriteriaParser
{
  /**
   * Defines the Mapping between RestCriteriaExprParser operators and AtomicCondition.Operator
   */
  private static Map<Integer, Operator> PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP = new HashMap<>();

  static
  {
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.EQUALS, Operator.EQUALS );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.NOT_EQUALS, Operator.NOT_EQUALS );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.LIKE, Operator.LIKE );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.GREATER_THAN, Operator.GREATER_THAN );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.LESS_THAN, Operator.LESS_THAN );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.GREATER_THAN_OR_EQUALS,
                                                 Operator.GREATER_THAN_OR_EQUALS );
    PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.put( RestCriteriaExprParser.LESS_THAN_OR_EQUALS,
                                                 Operator.LESS_THAN_OR_EQUALS );
  }

  private Condition _condition;

  public CriteriaParser( @Nonnull final String criteria )
  {
    try
    {
      final ANTLRInputStream input = new ANTLRInputStream( new StringReader( criteria ) );
      final RestCriteriaExprLexer lexer = new BailLexer( input );
      final CommonTokenStream tokens = new CommonTokenStream( lexer );
      final RestCriteriaExprParser parser = new RestCriteriaExprParser( tokens );
      parser.setBuildParseTree( true );
      final ParseListener listener = new ParseListener();
      parser.addParseListener( listener );
      parser.setErrorHandler( new BailErrorStrategy() );
      parser.top_level_condition();
      _condition = listener.getCondition();
      // Emit parse tree when debugging class
      //final Top_level_conditionContext context = parser.top_level_condition();
      //System.out.println( context.toStringTree( parser ) );
    }
    catch ( final IOException ioe )
    {
      throw new IllegalStateException( "Poorly formatted criteria: " + criteria, ioe );
    }
  }

  public Condition getCondition()
  {
    return _condition;
  }

  private static class BailLexer
    extends RestCriteriaExprLexer
  {
    public BailLexer( final CharStream input )
    {
      super( input );
    }

    public void recover( final LexerNoViableAltException e )
    {
      throw new ParseCancellationException( e );
    }
  }

  private static class ParseListener
    extends RestCriteriaExprBaseListener
  {
    private final Stack<Element> _stack = new Stack<Element>();
    private Condition _condition;

    Condition getCondition()
    {
      return _condition;
    }

    @Override
    public void exitTop_level_condition( @NotNull final Top_level_conditionContext ctx )
    {
      if ( isInError( ctx ) )
      {
        return;
      }
      if ( 1 != _stack.size() )
      {
        throw new IllegalStateException( "Expected to complete with a single expression on stack" );
      }
      else
      {
        _condition = (Condition) _stack.pop();
      }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void exitCondition( @NotNull final ConditionContext ctx )
    {
      if ( isInError( ctx ) )
      {
        return;
      }
      //condition: atomic_condition | '(' condition ')' | condition WS+ op=( AND | OR ) WS+ condition;

      if ( null != ctx.op )
      {
        final Condition rhs = (Condition) _stack.pop();
        if ( null != ctx.NOT() )
        {
          _stack.push( new UnaryCondition( UnaryCondition.Operator.NOT, rhs ) );
        }
        else
        {
          final Condition lhs = (Condition) _stack.pop();
          if ( null != ctx.AND() )
          {
            _stack.push( new BinaryCondition( BinaryCondition.Operator.AND, lhs, rhs ) );
          }
          else if ( null != ctx.OR() )
          {
            _stack.push( new BinaryCondition( BinaryCondition.Operator.OR, lhs, rhs ) );
          }
          else
          {
            throw new IllegalStateException( "Unknown operation: " + ctx.op );
          }
        }
      }
    }

    @Override
    public void exitAtomic_condition( @NotNull final Atomic_conditionContext ctx )
    {
      if ( isInError( ctx ) )
      {
        return;
      }
      //var_expr WS* op=( EQUALS | NOT_EQUALS | GREATER_THAN | LESS_THAN | GREATER_THAN_OR_EQUALS | LESS_THAN_OR_EQUALS ) WS* (var_expr|expr)
      final Expression rhs = (Expression) _stack.pop();
      final VariableExpression lhs = (VariableExpression) _stack.pop();
      final Operator operator = PARSER_TO_ATOMIC_CONDITION_OPERATOR_MAP.get( ctx.op.getType() );
      if ( operator != null )
      {
        _stack.push( new AtomicCondition( operator, lhs, rhs ) );
      }
      else
      {
        throw new IllegalStateException( "Unknown operation: " + ctx.op );
      }
    }

    @Override
    public void exitExpr( @NotNull final ExprContext ctx )
    {
      if ( isInError( ctx ) )
      {
        return;
      }
      // expr: INT | BOOLEAN | STRING;
      if ( null != ctx.INT() )
      {
        //INT
        try
        {
          _stack.push( new ConstantExpression( Integer.parseInt( ctx.INT().getText() ) ) );
        }
        catch ( final NumberFormatException nfe )
        {
          throw new IllegalStateException( "Error parsing integer", nfe );
        }
      }
      else if ( null != ctx.BOOLEAN() )
      {
        //BOOLEAN
        _stack.push( new ConstantExpression( "true".equals( ctx.BOOLEAN().getText() ) ) );
      }
      else if ( null != ctx.STRING() )
      {
        //STRING
        final String text = ctx.STRING().getText();
        _stack.push( new ConstantExpression( text.substring( 1, text.length() - 1 ) ) );
      }
      else if ( null != ctx.NULL() )
      {
        //NULL
        _stack.push( new NullExpression() );
      }
      else
      {
        throw new IllegalStateException( "Error handling expression" );
      }
    }

    @Override
    public void exitVar_expr( @NotNull final Var_exprContext ctx )
    {
      if ( isInError( ctx ) )
      {
        return;
      }
      // var_expr: ID (DOT var_expr)?;
      final List<TerminalNode> ids = ctx.ID();
      final ArrayList<String> elements = new ArrayList<String>();
      for ( final TerminalNode id : ids )
      {
        final String element = id.getText();
        elements.add( element );
      }
      _stack.push( new VariableExpression( elements.toArray( new String[ elements.size() ] ) ) );
    }

    private boolean isInError( final ParserRuleContext ctx )
    {
      return null != ctx.exception;
    }
  }
}
