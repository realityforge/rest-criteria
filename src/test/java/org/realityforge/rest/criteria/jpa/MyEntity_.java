package org.realityforge.rest.criteria.jpa;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@SuppressWarnings( { "UnusedDeclaration", "JavaDoc" } )
@StaticMetamodel(MyEntity.class)
public class MyEntity_
{
  public static volatile SingularAttribute<MyEntity, Integer> id;
  public static volatile SingularAttribute<MyEntity, String> code;
  public static volatile SingularAttribute<MyEntity, String> name;
  public static volatile SingularAttribute<MyEntity, Integer> rank;
  public static volatile SingularAttribute<MyEntity, Boolean> allDay;
}
