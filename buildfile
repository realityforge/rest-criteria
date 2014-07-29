require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/gpg'
require 'buildr/custom_pom'

PROVIDED_DEPS = [:javax_javaee, :javax_annotation]
TEST_DEPS = [:eclipselink, :h2db]

desc 'RestCriteria: Simple library for parsing criteria in rest services'
define 'rest-criteria' do
  project.group = 'org.realityforge.rest.criteria'
  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/rest-criteria')
  pom.add_developer('realityforge', 'Peter Donald')
  pom.provided_dependencies.concat PROVIDED_DEPS

  compile.with Buildr::Antlr4.runtime_dependencies, PROVIDED_DEPS

  compile.from compile_antlr(_('src/main/antlr/RestCriteriaExpr.g4'), :package => 'org.realityforge.rest.criteria')

  test.using :testng
  test.with TEST_DEPS

  package(:jar)
  package(:sources)
  package(:javadoc)
end

desc 'Generate source artifacts'
task('domgen:all').enhance([file(File.expand_path("#{File.dirname(__FILE__)}/generated/antlr/main/java"))])
