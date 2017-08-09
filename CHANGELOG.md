## v0.9.6:

* Update the version of antlr required to 4.5.1.

## v0.9.5:

* Added support for IS NULL. There no support for <foo> IS NOT NULL, instead
  NOT <foo> IS NULL can be used. Submitted by Ian Caughley.

## v0.9.4:

* Added support for LIKE operator. Submitted by Ian Caughley.

## v0.9.3:

* Add support for  \>, \<, \>=, and \<= operators. Submitted by Ian Caughley.

## v0.9.2:

* Add a TreeWalker and associated listener class for walking the semantic tree.
* Update the AbstractQueryBuilder to be more sub-class friendly.

## v0.9.1:

* Implement the AbstractQueryBuilder.addParameterSetter() method to ease sub-classing.

## v0.9.0:

* Implement the AbstractQueryBuilder.getCriteriaBuilder() method to ease sub-classing.
* Extract AbstractQueryBuilder.applyRestrictions() to make it easy for sub-classes to
  further restrict the results. Useful for time parameters or tables that implement
  logical deletes.

## v0.8.0:

* Require Java7 as a minimum.
* Upgrade to Antlr 4.3.
* Implement correct precedence between AND/OR binary operations.
* Supply a AbstractQueryBuilder for implementers that use a JPA backed data store.

## v0.7.0:

* Support $ in variable expressions.

## v0.6.0:

* Restrict the antlr compile dependencies to runtime dependencies.
* Generate Maven Central compliant POM.
* Repackage code into org.realityforge.rest.criteria.
* Regroup code into org.realityforge.rest.criteria.

## v0.5.0:

* Add VariableExpression.getPathAsString() utility method.

## v0.4.0:

* Correct the parsing of NOT operators.

## v0.3.0:

* Improve the handling of numeric data so that it allows any instance of Number.

## v0.2.0:

* Fix the type checks in ConstantExpression.

## v0.1.0:

* Initial version.
