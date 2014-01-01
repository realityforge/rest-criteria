package org.realityforge.rest.criteria.model;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ConstantExpressionTest
{
  @Test
  public void numericValues()
  {
    ensureNumeric( 1L );
    ensureNumeric( 1 );
    ensureNumeric( (short) 1 );
    ensureNumeric( (byte) 1 );
    ensureNumeric( 1.0F );
    ensureNumeric( 1.0D );
  }

  @Test
  public void booleanValue()
  {
    final ConstantExpression c = new ConstantExpression( Boolean.TRUE );
    assertTrue( c.isBoolean() );
    assertFalse( c.isText() );
    assertFalse( c.isNumeric() );
    try
    {
      c.asNumeric();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    try
    {
      c.asText();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    assertEquals( c.asBoolean(), Boolean.TRUE );
  }

  @Test
  public void textValue()
  {
    final String foo = "foo";
    final ConstantExpression c = new ConstantExpression( foo );
    assertFalse( c.isBoolean() );
    assertTrue( c.isText() );
    assertFalse( c.isNumeric() );
    try
    {
      c.asNumeric();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    try
    {
      c.asBoolean();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    assertEquals( c.asText(), foo );
  }

  private void ensureNumeric( final Number value )
  {
    final ConstantExpression c = new ConstantExpression( value );
    assertFalse( c.isBoolean() );
    assertFalse( c.isText() );
    assertTrue( c.isNumeric() );
    try
    {
      c.asBoolean();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    try
    {
      c.asText();
      fail();
    }
    catch ( IllegalStateException ise )
    {
      //Ignored
    }
    assertEquals( c.asNumeric(), value );
  }
}
