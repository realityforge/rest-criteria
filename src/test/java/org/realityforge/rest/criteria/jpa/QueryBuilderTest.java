package org.realityforge.rest.criteria.jpa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@SuppressWarnings( "UnusedDeclaration" )
public class QueryBuilderTest
{
  private EntityManager _em;

  @DataProvider( name = "Query" )
  public Object[][] queryProvider()
  {
    return new Object[][]
      {
        { null, new String[]{ "A", "B", "C", "D", "E" } },
        { " ", new String[]{ "A", "B", "C", "D", "E" } },
        { "rank = rank", new String[]{ "A", "B", "C", "D", "E" } },
        { "rank = 3", new String[]{ "C" } },
        { "rank=3", new String[]{ "C" } },
        { "rank > 3", new String[]{ "D", "E" } },
        { "rank >= 3", new String[]{ "C", "D", "E" } },
        { "rank < 3", new String[]{ "A", "B" } },
        { "rank <= 3", new String[]{ "A", "B", "C" } },
        { "code <= 'C'", new String[]{ "A", "B", "C" } },
        { "NOT rank = 3", new String[]{ "A", "B", "D", "E" } },
        { "rank != 3", new String[]{ "A", "B", "D", "E" } },
        { "rank != 3 AND code = 'A'", new String[]{ "A" } },
        { "rank = 3 OR code = 'B'", new String[]{ "B", "C" } },
        { "allDay = false AND code != 'C'", new String[]{ "E" } },
        { "code LIKE 'A'", new String[]{ "A" } },
        { "code LIKE '%'", new String[]{ "A", "B", "C", "D", "E" } },
        { "(code = 'A' OR code = 'D') AND rank > 3", new String[]{ "D" } },
        { "name IS NULL", new String[]{ "E" } },
        { "NOT name IS NULL", new String[]{ "A", "B", "C", "D" } },
        };
  }

  @DataProvider( name = "BadConditions" )
  public Object[][] badConditionsProvider()
  {
    return new Object[][]
      {
        { "name = NULL" },
        { "name LIKE NULL" },
        { "name IS NOT NULL" },
        { "name IS 'FOO'" },
        };
  }

  @Test( dataProvider = "Query" )
  public void query( final String query, final String[] results )
  {
    assertQueryResults( query, results );
  }

  @Test( dataProvider = "BadConditions", expectedExceptions = { BadConditionException.class,
                                                                ParseCancellationException.class } )
  public void badConditions( final String query )
  {
    assertQueryResults( query );
  }

  private void assertQueryResults( final String input, final String... expected )
  {
    final List<String> actual = e2i( performQuery( input ) );
    assertEquals( actual.size(), expected.length );
    for ( final String value : expected )
    {
      assertTrue( actual.contains( value ), "Expecting ID: " + value + " but not found in " + actual );
    }
  }

  private MyEntity createEntity( final String code, final String name, final int rank, final boolean allDay )
  {
    final MyEntity entity = new MyEntity( code, name, rank, allDay );
    _em.persist( entity );
    _em.flush();
    return entity;
  }

  private List<String> e2i( final List<MyEntity> entities )
  {
    final ArrayList<String> list = new ArrayList<>();
    for ( final MyEntity entity : entities )
    {
      list.add( entity.getCode() );
    }
    return list;
  }

  private List<MyEntity> performQuery( @Nullable final String input )
  {
    _em.flush();
    final CriteriaBuilder cb = _em.getCriteriaBuilder();
    TestQueryBuilder builder = new TestQueryBuilder( cb, input );

    final TypedQuery<MyEntity> query =
      _em.createQuery( builder.getCriteriaQuery() );

    builder.applyParameters( query );

    return query.getResultList();
  }

  @BeforeMethod
  public void preTest()
    throws Exception
  {
    final File databaseFile = File.createTempFile( "database", "h2" );
    final Properties properties = initPersistenceUnitProperties( databaseFile );
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory( "TestUnit", properties );
    _em = factory.createEntityManager();
    _em.getTransaction().begin();

    createEntity( "A", "Ace", 1, true );
    createEntity( "B", "Bob", 2, true );
    createEntity( "C", "Cat", 3, false );
    createEntity( "D", "Dog", 4, true );
    createEntity( "E", null, 5, false );
  }

  @AfterMethod
  public void postTest()
  {
    if ( null != _em )
    {
      _em.getTransaction().commit();
      _em.close();
      _em = null;
    }
  }

  private Properties initPersistenceUnitProperties( @Nonnull final File databaseFile )
  {
    final Properties properties = new Properties();
    properties.setProperty( "javax.persistence.jdbc.driver", org.h2.Driver.class.getName() );
    properties.setProperty( "javax.persistence.jdbc.url", "jdbc:h2:" + databaseFile );
    properties.setProperty( "javax.persistence.schema-generation.database.action", "drop-and-create" );
    properties.setProperty( "javax.persistence.schema-generation.drop-source", "metadata" );
    return properties;
  }
}
