require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'

desc "RestCriteria: Simple library for parsing criteria in rest services"
define 'rest-criteria' do
  project.group = 'org.realityforge.rest_criteria'
  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  compile.with Buildr::Antlr4.runtime_dependencies, :javax_annotation

  compile.from compile_antlr(_('src/main/antlr/RestCriteriaExpr.g4'), :package => 'org.realityforge.rest_criteria')

  test.using :testng

  package(:jar)
  package(:sources)
end

desc "Generate source artifacts"
task('domgen:all').enhance([file(File.expand_path("#{File.dirname(__FILE__)}/generated/antlr/main/java"))])
