package org.realityforge.rest_criteria.model;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class VariableExpressionTest
{

  @Test
  public void getPath()
  {
    final String[] path = { "a", "b", "c" };
    assertEquals( variableExpression( path ).getPath(), path );
  }

  @Test
  public void getPathAsString()
  {
    assertEquals( path( "" ), "" );
    assertEquals( path( "a" ), "a" );
    assertEquals( path( "a", "b", "c" ), "a.b.c" );
  }

  private String path( final String... path )
  {
    return variableExpression( path ).getPathAsString();
  }

  private VariableExpression variableExpression( final String... path )
  {
    return new VariableExpression( path );
  }

}