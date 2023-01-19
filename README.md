
# Camunda Modeler Template Generator
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-) [![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community) ![Compatible with: Camunda Platform 7](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%207-26d07c) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project provides a simple Maven plugin to automatically generate template files for the Camunda Modeler based on code annotations. To do this, it uses **ClassGraph** to scan the classpath for specific annotations. It automatically validates the generated template files against the Camunda schema based on the specified version that should be used.

## Features
The main goal of this project is to provide a simple to use Maven plugin that generates template files for the Camunda Modeler without having to write the manually. There are annotations included that can be used in the Java code and which serves as a basis for the generation process. The content of the annotations as well as annotated fields are then scanned with the use of the **ClassGraph** library that scans the classpath. It is also possible to specify a package to speed up the scanning process. The analyzed annotations are then processed into a Java Object Model which is used to serialize into a JSON format using the **Gson** library. The generated JSON Strings are validate against the [Camunda element-templates-json-schema](https://unpkg.com/browse/@camunda/element-templates-json-schema/resources/schema.json). The Generator plugin allows to specify a version for the generated template files to use. This version will also be used to validate against. If there is no version provided it will automatically use the latest version available.

## Getting started
### Installation
To use this Maven plugin in your project, you simply have to add the following code to your `pom.xml` file.
```xml
<plugin>
   <groupId>com.groupId</groupId>
   <artifactId>template-generator-maven-plugin</artifactId>
   <version>1.0.0-SNAPSHOT</version>
   <executions>
      <execution>
         <goals>
            <goal>template-generator</goal>
         </goals>
      </execution>
   </executions>
   <configuration>
	  <schemaVersion>0.10.1</schemaVersion>
      <outputDir>example-output-directory</outputDir>
      <scanPackages>com.mypackage</scanPackages>
   </configuration>
</plugin>
```
The plugin accepts the following arguments to be set in the `configuration` section:
 - `schemaVersion`: The schema version to use for the generated files and for the validation process.
 - `outputDir`: The directory to save the generated files to.
 - `scanPackages`: The java packe to scan for annotations (scans all packages by default).

### Usage
The plugin provides two basic Java annotations that can be used in the code. These annotations will then be scanned by the plugin to generate the template files. There will be one file for each Java class that contains annotations.

`@TemplateProperty`
This annotation can be used to annotate fields and represents one property in a Template in the Modeler.
The following matrix gives an overview about the arguments for this annotation:
| Name | Type | Default value | Optional | Description |
|-|:-:|:-:|:-:|-|
|label			|String		|""		|✔️|The label that will be displayed in the Modeler.|
|type			|String		|		|❌|The property type, e.g. String, Hidden, Dropdown.|
|value			|String		|""		|✔️|The default value selected in the Modeler.|
|choices		|@Choice[]	|{}		|✔️|An array containing the choices available in a Dropdown menu.|
|description	|String		|""		|✔️|The description that is shown at the entry in the Modeler.|
|parameterType	|String		|"input"|✔️|The parameter type of the property, available options are: "input", "output", "property".|
|bindingName	|String		|""		|✔️|The binding name that is used together with the parameter type.|
|scriptFormat	|String		|""		|✔️|The script format to use within the binding.|
|notEmpty		|boolean	|false	|✔️|Option to say whether an input can be empty or not.|
|isEditable		|boolean	|true	|✔️|Option to say whether an input can be edited by the user or not.|

`@Template`
The Template annotation can be used to annotate methods. This annotation is absolutely **necessary** because only classes that contain this annotation will be scanned. It represents a Template in the Modeler and contains the corresponding properties which are read from fields of the same class hierarchy annotated with `TemplateProperty`. Since there might be several activities in one Java class which rely on different properties the `Template` annotation can optionally contain a list of multiple `TemplateProperty` annotations that will be included exclusively in the corresponding Template.
The following matrix gives an overview about the arguments for this annotation:
| Name | Type | Default value | Optional | Description |
|-|:-:|:-:|:-:|-|
|name|String| |❌|The name of the Template shown in the Modeler.|
|id|String|	|❌|The id of the Template, e.g. "com.example.Template".|
|appliesTo|String[]| |❌|What this Template should apply to.|
|function|String|""|✔️|The name of the function that should be called.|
|functionNameProperty|String|""|✔️|The binding name of the function that should be called.|
|templateProperties|@TemplateProperty[]|{}|✔️|An optional additional array of TemplateProperty annotations..|
|entriesVisible|boolean|true|✔️|Option to say if the properties should be visible in the Modeler.|

`@Choice`
This annotation should only be used in the `choices` field of the `TemplateProperty` annotation. It represents one entry of a dropdown menu.
| Name | Type | Default value | Optional | Description |
|-|:-:|:-:|:-:|-|
|name|String| |❌|The name of the dropdown menu entry.
|value|String| |❌|The value of the dropdown menu entry.

#### Example 1
```java
 @TemplateProperty(label = "Input",
            description = "Some example input.",
            parameterType = "input",
            type = "String",
            notEmpty = true)
```

#### Example 2
```java
 @Template(name = "Example",
            id = "com.example.Example",
            appliesTo = { "bpmn:Task" },
            templateProperties = {
                    @TemplateProperty(label = "Example Additional Property",
                            description = "Example of an additional property",
                            parameterType = "input",
                            type = "String",
                            value = "example",
                            bindingName = "exampleAdditionalProperty") })
```

## License
Apache License, Version 2.0
