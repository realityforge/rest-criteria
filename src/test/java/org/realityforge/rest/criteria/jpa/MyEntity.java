package org.realityforge.rest.criteria.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MyEntity
{
  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY )
  @Column( name = "ID" )
  private Integer id;

  @Column( name = "Code" )
  private String code;

  @Column( name = "Name" )
  private String name;

  @Column( name = "Rank" )
  private int rank;

  @Column( name = "AllDay" )
  private boolean allDay;

  public MyEntity()
  {
  }

  public MyEntity( final String code, final String name, final int rank, final boolean allDay )
  {
    this.code = code;
    this.name = name;
    this.rank = rank;
    this.allDay = allDay;
  }

  public Integer getId()
  {
    return id;
  }

  public String getCode()
  {
    return code;
  }

  public String getName()
  {
    return name;
  }

  public int getRank()
  {
    return rank;
  }

  public boolean isAllDay()
  {
    return allDay;
  }
}
