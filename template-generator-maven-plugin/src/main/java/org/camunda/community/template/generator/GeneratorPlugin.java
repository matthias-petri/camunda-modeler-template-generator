package org.camunda.community.template.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.camunda.community.template.generator.objectmodel.Template;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/** The main class of the modeler template generator */
@Mojo(
    name = "template-generator",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class GeneratorPlugin extends AbstractMojo {

  private static final String SCHEMA_BASE_URL =
      "https://unpkg.com/@camunda/element-templates-json-schema";

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(property = "template-generator.schemaVersion", defaultValue = "null")
  private String schemaVersion;

  @Parameter(property = "template-generator.outputDir")
  private String outputDir;

  @Parameter(property = "template-generator.scanPackages", defaultValue = "*")
  private String scanPackages;

  @SuppressWarnings("unchecked")
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final PluginDescriptor pluginDescriptor =
        (PluginDescriptor) getPluginContext().get("pluginDescriptor");
    final ClassRealm classRealm = pluginDescriptor.getClassRealm();
    try {
      project
          .getCompileClasspathElements()
          .forEach(
              c -> {
                try {
                  classRealm.addURL(new URL("file:///" + c.toString()));
                } catch (MalformedURLException e1) {
                  throw new IllegalArgumentException("Error create file URL: " + c, e1);
                }
              });
    } catch (DependencyResolutionRequiredException e1) {
      throw new MojoExecutionException("Dependency resolution failed", e1);
    }

    getLog().info("Schema version: " + schemaVersion);
    getLog().info("Output directory: " + outputDir);
    getLog().info("Scanned package: " + scanPackages);

    // Download schema for validation
    String schemaURL =
        SCHEMA_BASE_URL
            + (schemaVersion.equals("null") ? "" : "@" + schemaVersion)
            + "/resources/schema.json";
    String schema = downloadSchema(schemaURL);

    getLog().info("Scanning for annotations ...");

    ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan();
    ClassInfoList classInfoList =
        scanResult.getClassesWithMethodAnnotation(
            org.camunda.community.template.generator.Template.class.getName());

    // Iterate through all classes containing a Template annotation
    for (ClassInfo classInfo : classInfoList) {
      // Set template file output path
      String filePath = outputDir + File.separator + classInfo.getSimpleName() + "Templates.json";

      // Parse templates of the current class
      List<Template> templates = GeneratorParser.processTemplates(classInfo);
      for (Template template : templates) {
        template.setSchemaURL(schemaURL);
      }

      // Serialize object model to JSON
      String resultJSON = (new GsonBuilder()).setPrettyPrinting().create().toJson(templates);
      writeJsonToFile(filePath, resultJSON);

      // Validate JSON file
      validateJsonFile(filePath, schema);
    }
  }

  /**
   * Writes the JSON String to a specific file path.
   *
   * @param filePath The path where to save the specified JSON String
   * @param json The JSON String to write
   * @throws MojoExecutionException
   */
  private void writeJsonToFile(String filePath, String json) throws MojoExecutionException {
    File file = new File(filePath);
    file.getParentFile().mkdirs();

    try (FileWriter outputFile = new FileWriter(file)) {
      outputFile.write(json);
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to write output file " + filePath, e);
    }
  }

  /**
   * Downloads a schema from the provided URL
   *
   * @param schemaURL The URL from where to download the schema
   * @return The schema as String
   */
  private String downloadSchema(String schemaURL) {
    StringBuilder schema = new StringBuilder();

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new URL(schemaURL).openStream()))) {

      String line;
      while ((line = reader.readLine()) != null) {
        schema.append(line);
      }

      getLog().info("Successfully downloaded schema from: " + schemaURL);
    } catch (MalformedURLException mue) {
      getLog().error("Failed to download schema! - Malformed URL Exception raised");
    } catch (IOException ie) {
      getLog().error("Failed to download schema! - IOException raised");
    }

    return schema.toString();
  }

  /**
   * Validates a JSON file against the provided schema
   *
   * @param filePath The file path to validate
   * @param schemaTemplate The schema template to use for validation
   * @throws MojoExecutionException
   */
  private void validateJsonFile(String filePath, String schemaTemplate)
      throws MojoExecutionException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(VersionFlag.V7);

    try {
      File file = new File(filePath);
      JsonNode json = objectMapper.readTree(new FileInputStream(file));
      JsonSchema schema =
          schemaFactory.getSchema(new ByteArrayInputStream(schemaTemplate.getBytes()));
      Set<ValidationMessage> validationResult = schema.validate(json);

      // print validation errors
      if (validationResult.isEmpty()) {
        getLog().info(file.getName() + ": Validation successful");
      } else {
        validationResult.forEach(vm -> getLog().warn(file.getName() + ": " + vm.getMessage()));
      }
    } catch (Exception e) {
      throw new MojoExecutionException("JSON validation failed! File: " + filePath, e);
    }
  }
}
