package org.camunda.community.template.generator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Template annotation used by the GeneratorPlugin to create template files for the Camunda Modeler
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Template {

  public static final String SERVICE_TASK = "bpmn:ServiceTask";

  /**
   * @return name of the activity
   */
  public String name();

  /**
   * @return id of the activity
   */
  public String id();

  /**
   * @return types the activity should apply to
   */
  public String[] appliesTo();

  /**
   * @return function of the activity
   */
  public String function() default "";

  /**
   * @return function name of the activity
   */
  public String functionNameProperty() default "";

  /**
   * @return list of additional TemplateProperty annotations for the activity
   */
  public TemplateProperty[] templateProperties() default {};

  /**
   * @return flag to display entries
   */
  public boolean entriesVisible() default true;
}
