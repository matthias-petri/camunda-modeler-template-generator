package org.camunda.community.template.generator;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.community.template.generator.objectmodel.Binding;
import org.camunda.community.template.generator.objectmodel.Choice;
import org.camunda.community.template.generator.objectmodel.Constraint;
import org.camunda.community.template.generator.objectmodel.Property;
import org.camunda.community.template.generator.objectmodel.Template;

/** Parser class for the Template Generator Maven plugin */
public class GeneratorParser {

  public static final String TEMPLATE_ID = "id";

  public static final String ENTRIES_VISIBLE = "entriesVisible";

  public static final String APPLIES_TO = "appliesTo";

  public static final String FUNCTION = "function";

  public static final String FUNCTION_NAME_PROPERTY = "functionNameProperty";

  public static final String DESCRIPTION = "description";

  public static final String INDEX = "index";

  public static final String BINDING = "binding";

  public static final String BINDING_NAME = "bindingName";

  public static final String SCRIPT_FORMAT = "scriptFormat";

  public static final String EDITABLE = "editable";

  public static final String IS_EDITABLE = "isEditable";

  public static final String LABEL = "label";

  public static final String TYPE = "type";

  public static final String NAME = "name";

  public static final String PROPERTIES = "templateProperties";

  public static final String CHOICES = "choices";

  public static final String NOTEMPTY = "notEmpty";

  public static final String VALUE = "value";

  public static final String PROPERTY = "property";

  public static final String PARAMETER_TYPE = "parameterType";

  public static final String CAMUNDA_PREFIX = "camunda:";

  public static final String INPUT_PARAMETER = CAMUNDA_PREFIX + "inputParameter";

  public static final String OUTPUT_PARAMETER = CAMUNDA_PREFIX + "outputParameter";

  public static final String CLASS_PARAMETER = "class";

  private GeneratorParser() {}

  /**
   * Returns a Binding object for a template property based on the provided type and name
   *
   * @param type
   * @param name
   * @param scriptFormat
   * @return Binding object
   */
  public static Binding parseBinding(String type, String name, String scriptFormat) {
    Binding binding = new Binding();

    switch (type) {
      case TemplateProperty.INPUT:
        binding.setType(INPUT_PARAMETER);
        binding.setName(name);
        break;

      case TemplateProperty.OUTPUT:
        binding.setType(OUTPUT_PARAMETER);
        binding.setSource("${" + name + "}");
        break;

      case TemplateProperty.PROPERTY:
        binding.setType(PROPERTY);
        binding.setName(CAMUNDA_PREFIX + name);
        break;

      default:
        break;
    }

    if (!scriptFormat.isBlank()) {
      binding.setScriptFormat(scriptFormat);
    }

    return binding;
  }

  /**
   * Returns a List of choices for a dropdown template property based on the provided array of
   * choice Annotations.
   *
   * @param objectChoices
   * @return List of Choice objects
   */
  public static List<Choice> parseChoices(Object[] objectChoices) {
    List<Choice> choices = new ArrayList<>();

    for (Object choice : objectChoices) {
      AnnotationInfo choiceAnnotationInfo = (AnnotationInfo) choice;

      String choiceName = (String) choiceAnnotationInfo.getParameterValues().getValue(NAME);
      String choiceValue = (String) choiceAnnotationInfo.getParameterValues().getValue(VALUE);

      choices.add(new Choice(choiceName, choiceValue));
    }

    if (choices.isEmpty()) {
      return Collections.emptyList();
    } else {
      return choices;
    }
  }

  /**
   * Returns a Property object that was created based on the provided fieldParameters
   *
   * @param fieldParameters
   * @return Property based on the provided fieldParameters
   */
  public static Property createProperty(Map<String, Object> fieldParameters) {
    Property property = new Property();

    String label = String.valueOf(fieldParameters.get(LABEL));
    if (!label.isBlank()) {
      property.setLabel(label);
    }

    String type = String.valueOf(fieldParameters.get(TYPE));
    if (!type.isBlank()) {
      property.setType(type);
    }

    String value = String.valueOf(fieldParameters.get(VALUE));
    if (!value.isBlank()) {
      property.setValue(value);
    }

    String description = String.valueOf(fieldParameters.get(DESCRIPTION));
    if (!description.isBlank()) {
      property.setDescription(description);
    }

    String bindingName = String.valueOf(fieldParameters.get(BINDING_NAME));
    String scriptFormat = String.valueOf(fieldParameters.get(SCRIPT_FORMAT));
    if (!bindingName.isBlank()) {
      property.setBinding(
          parseBinding(
              String.valueOf(fieldParameters.get(PARAMETER_TYPE)), bindingName, scriptFormat));
    }

    List<Choice> choices = parseChoices((Object[]) fieldParameters.get(CHOICES));
    if (!choices.isEmpty()) {
      property.setChoices(choices.toArray(new Choice[0]));
    }

    property.setEditable(((Boolean) (fieldParameters.get(IS_EDITABLE))).booleanValue());

    property.setConstraint(
        new Constraint(((Boolean) (fieldParameters.get(NOTEMPTY))).booleanValue()));

    return property;
  }

  /**
   * Returns a Template object that was created based on the provided methodParameters.
   *
   * @param methodParameters
   * @return Template based on the provided methodParameters
   */
  public static Template createTemplate(Map<String, Object> methodParameters) {
    Template template = new Template();

    template.setEntriesVisible(((Boolean) (methodParameters.get(ENTRIES_VISIBLE))).booleanValue());

    template.setAppliesTo((String[]) methodParameters.get(APPLIES_TO));

    String templateName = String.valueOf(methodParameters.get(NAME));
    if (!templateName.isBlank()) {
      template.setTemplateName(templateName);
    }

    String templateID = String.valueOf(methodParameters.get(TEMPLATE_ID));
    if (!templateID.isBlank()) {
      template.setTemplateID(templateID);
    }

    // Add method specific properties
    for (Object o : (Object[]) methodParameters.get(PROPERTIES)) {
      template.addTemplateProperty(processSingleProperty((AnnotationInfo) o));
    }

    return template;
  }

  /**
   * Returns a Property object that represents the implementation type property of an activity.
   *
   * @param className
   * @return Property representation of the implementation type
   */
  public static Property createImplementationTypeProperty(String className) {
    Property implementationTypeProperty = new Property();

    implementationTypeProperty.setType(TemplateProperty.HIDDEN);
    implementationTypeProperty.setValue(className);
    implementationTypeProperty.setBinding(parseBinding(PROPERTY, CLASS_PARAMETER, ""));

    return implementationTypeProperty;
  }

  /**
   * Returns a Property object that represents the function field property of an activity.
   *
   * @param function
   * @param functionNameProperty
   * @return Property representation of the function field
   */
  public static Property createFunctionField(String function, String functionNameProperty) {
    Property functionFieldProperty = new Property();

    functionFieldProperty.setType(TemplateProperty.HIDDEN);
    functionFieldProperty.setValue(function);
    functionFieldProperty.setBinding(
        parseBinding(TemplateProperty.INPUT, functionNameProperty, ""));

    return functionFieldProperty;
  }

  /**
   * Returns a SimpleEntry that contains the key and value of the provided AnnotationParameterValue.
   *
   * @param annotationParameter
   * @return SimpleEntry containing key and value of an Annotation property
   */
  public static SimpleEntry<String, Object> processAnnotationParameter(
      AnnotationParameterValue annotationParameter) {
    String annotationParameterName = annotationParameter.getName();
    Object annotationParameterValue = annotationParameter.getValue();

    return new SimpleEntry<>(annotationParameterName, annotationParameterValue);
  }

  /**
   * Returns a Property object that was created based on the annotation values of the provided
   * FieldInfo. Useful when reading a TemplateProperty of a field.
   *
   * @param fieldInfo
   * @return Property representation of the provided FieldInfo
   */
  public static Property processSingleProperty(FieldInfo fieldInfo) {
    AnnotationInfoList fieldAnnotations = fieldInfo.getAnnotationInfo();

    // Iterate through all annotations of one field
    for (AnnotationInfo fieldAnnotationInfo : fieldAnnotations) {
      if (fieldAnnotationInfo.getName().equals(TemplateProperty.class.getName())) {
        AnnotationParameterValueList fieldAnnotationParameters =
            fieldAnnotationInfo.getParameterValues(true);

        Map<String, Object> fieldParameters = new HashMap<>();

        // Iterate through all parameters of one field Annotation
        for (AnnotationParameterValue fieldAnnotationParameter : fieldAnnotationParameters) {
          SimpleEntry<String, Object> fieldEntry =
              processAnnotationParameter(fieldAnnotationParameter);
          fieldParameters.put(fieldEntry.getKey(), fieldEntry.getValue());
        }

        Property property = createProperty(fieldParameters);

        // Create the binding for a property
        String bindingType = String.valueOf(fieldParameters.get(PARAMETER_TYPE));
        String bindingName = String.valueOf(fieldInfo.getConstantInitializerValue());
        String scriptFormat = String.valueOf(fieldParameters.get(SCRIPT_FORMAT));
        Binding binding = parseBinding(bindingType, bindingName, scriptFormat);

        property.setBinding(binding);

        return property;
      }
    }

    return null;
  }

  /**
   * Returns a Property object that was created based on the annotation values of the provided
   * AnnotationInfo. Useful when reading a TemplateProperty annotation inside a parent annotation.
   *
   * @param annotationInfo
   * @return Property representation of the provided AnnotationInfo
   */
  public static Property processSingleProperty(AnnotationInfo annotationInfo) {
    if (annotationInfo.getName().equals(TemplateProperty.class.getName())) {
      AnnotationParameterValueList methodAnnotationParameters =
          annotationInfo.getParameterValues(true);

      Map<String, Object> methodParameters = new HashMap<>();

      // Iterate through all parameters of one method Annotation
      for (AnnotationParameterValue methodAnnotationParameter : methodAnnotationParameters) {
        SimpleEntry<String, Object> fieldEntry =
            processAnnotationParameter(methodAnnotationParameter);
        methodParameters.put(fieldEntry.getKey(), fieldEntry.getValue());
      }

      return createProperty(methodParameters);
    }

    return null;
  }

  /**
   * Returns a Template object that was created based on the annotation values of the provided
   * MethodInfo.
   *
   * @param methodInfo
   * @return Template representation of the provided MethodInfo
   */
  public static Template processSingleTemplate(MethodInfo methodInfo) {
    AnnotationInfoList methodAnnotations = methodInfo.getAnnotationInfo();

    // Iterate through all annotations of one method
    for (AnnotationInfo methodAnnotationInfo : methodAnnotations) {
      if (methodAnnotationInfo
          .getName()
          .equals(org.camunda.community.template.generator.Template.class.getName())) {
        AnnotationParameterValueList methodAnnotationParameters =
            methodAnnotationInfo.getParameterValues(true);

        Map<String, Object> methodParameters = new HashMap<>();

        // Iterate through all parameters of one method Annotation
        for (AnnotationParameterValue methodAnnotationParameter : methodAnnotationParameters) {
          SimpleEntry<String, Object> fieldEntry =
              processAnnotationParameter(methodAnnotationParameter);
          methodParameters.put(fieldEntry.getKey(), fieldEntry.getValue());
        }

        Template template = createTemplate(methodParameters);

        // Add function field if parameters are present
        String function = String.valueOf(methodParameters.get(FUNCTION));
        String functionNameProperty = String.valueOf(methodParameters.get(FUNCTION_NAME_PROPERTY));

        if (!function.isBlank() && !functionNameProperty.isBlank()) {
          template.addTemplateProperty(createFunctionField(function, functionNameProperty));
        }

        return template;
      }
    }

    return null;
  }

  /**
   * Returns a List of processed properties based on the fields of the provided ClassInfo.
   *
   * @param classInfo
   * @return List of processed properties
   */
  public static List<Property> processGlobalProperties(ClassInfo classInfo) {
    List<Property> globalProperties = new ArrayList<>();

    // Iterate through all fields of a class
    for (FieldInfo fieldInfo : classInfo.getFieldInfo()) {
      Property property = processSingleProperty(fieldInfo);

      if (property != null) {
        globalProperties.add(property);
      }
    }

    return globalProperties;
  }

  /**
   * Returns a List of created templates for a given ClassInfo.
   *
   * @param classInfo
   * @return List of processed templates
   */
  public static List<Template> processTemplates(ClassInfo classInfo) {
    List<Template> templates = new ArrayList<>();

    // Iterate through all methods of a class
    for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
      Template template = processSingleTemplate(methodInfo);

      if (template != null) {
        for (Property property : processGlobalProperties(classInfo)) {
          template.addTemplateProperty(property);
        }

        // Add Implementation Type property
        String className = classInfo.getName();
        template.addTemplateProperty(createImplementationTypeProperty(className));

        templates.add(template);
      }
    }

    return templates;
  }
}
