package generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimplePlantUMLCodeGenerator {
    private String plantUmlFilePath;
    private String outputDirectory;
    
    // Data structures to store parsed information
    private List<String> abstractClasses = new ArrayList<>();
    private Map<String, List<String>> classFields = new HashMap<>();
    private Map<String, List<String>> classMethods = new HashMap<>();
    private Map<String, String> classInheritance = new HashMap<>();
    
    public SimplePlantUMLCodeGenerator(String plantUmlFilePath, String outputDirectory) {
        this.plantUmlFilePath = plantUmlFilePath;
        this.outputDirectory = outputDirectory;
    }
    
    public void generate() {
        try {
            // Parse the PlantUML file
            parsePlantUmlFile();
            
            // Create output directory if it doesn't exist
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate Java files
            for (String className : abstractClasses) {
                generateAbstractClassFile(className);
            }
            
            for (String className : classMethods.keySet()) {
                if (!abstractClasses.contains(className) && !className.equals("App")) {
                    generateConcreteClassFile(className);
                }
            }
            
            generateAppFile();
            
            System.out.println("Code generation completed successfully!");
        } catch (IOException e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parsePlantUmlFile() throws IOException {
        String currentClass = null;
        boolean inClass = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(plantUmlFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and UML directives
                if (line.isEmpty() || line.startsWith("@") || line.startsWith("!") || line.startsWith("top") || line.startsWith("skinparam")) {
                    continue;
                }
                
                // Parse abstract class declaration
                if (line.startsWith("abstract class")) {
                    currentClass = line.substring("abstract class".length()).trim();
                    if (currentClass.endsWith("{")) {
                        currentClass = currentClass.substring(0, currentClass.length() - 1).trim();
                    }
                    abstractClasses.add(currentClass);
                    classFields.put(currentClass, new ArrayList<>());
                    classMethods.put(currentClass, new ArrayList<>());
                    inClass = true;
                    continue;
                }
                
                // Parse regular class declaration
                if (line.startsWith("class")) {
                    currentClass = line.substring("class".length()).trim();
                    if (currentClass.endsWith("{")) {
                        currentClass = currentClass.substring(0, currentClass.length() - 1).trim();
                    }
                    classFields.put(currentClass, new ArrayList<>());
                    classMethods.put(currentClass, new ArrayList<>());
                    inClass = true;
                    continue;
                }
                
                // Parse inheritance
                if (line.contains("<|--")) {
                    String[] parts = line.split("<\\|--");
                    String parent = parts[0].trim();
                    String child = parts[1].trim();
                    classInheritance.put(child, parent);
                    continue;
                }
                
                // Parse class members
                if (inClass && currentClass != null) {
                    // End of class definition
                    if (line.equals("}")) {
                        inClass = false;
                        continue;
                    }
                    
                    // Parse field
                    if (line.contains(":") && !line.contains("(")) {
                        String[] parts = line.split(":");
                        String visibility = parts[0].trim().substring(0, 1);
                        String fieldName = parts[0].trim().substring(1).trim();
                        String fieldType = parts[1].trim();
                        
                        classFields.get(currentClass).add(visibility + ":" + fieldName + ":" + fieldType);
                        continue;
                    }
                    
                    // Parse method
                    if (line.contains("(")) {
                        String visibility = line.trim().substring(0, 1);
                        String methodSignature = line.trim().substring(1).trim();
                        
                        boolean isAbstract = methodSignature.contains("{abstract}");
                        if (isAbstract) {
                            methodSignature = methodSignature.replace("{abstract}", "").trim();
                        }
                        
                        String methodName = methodSignature.substring(0, methodSignature.indexOf("(")).trim();
                        String parameters = methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")")).trim();
                        String returnType = "";
                        
                        if (methodSignature.contains(":")) {
                            returnType = methodSignature.substring(methodSignature.lastIndexOf(":") + 1).trim();
                        } else {
                            returnType = "void";
                        }
                        
                        classMethods.get(currentClass).add(visibility + ":" + (isAbstract ? "abstract:" : "") + methodName + ":" + parameters + ":" + returnType);
                    }
                }
            }
        }
    }
    
    private void generateAbstractClassFile(String className) throws IOException {
        String filePath = outputDirectory + File.separator + className + ".java";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("package figuranew;\n\n");
            writer.write("import java.util.Scanner;\n\n");
            writer.write("public abstract class " + className + " {\n");
            
            // Write fields
            for (String field : classFields.get(className)) {
                String[] parts = field.split(":");
                String visibility = parts[0];
                String fieldName = parts[1];
                String fieldType = parts[2];
                
                String javaVisibility = "private";
                if (visibility.equals("+")) javaVisibility = "public";
                else if (visibility.equals("#")) javaVisibility = "protected";
                
                writer.write("    " + javaVisibility + " " + fieldType + " " + fieldName);
                
                // Initialize Scanner
                if (fieldName.equals("scanner") && fieldType.equals("Scanner")) {
                    writer.write(" = new Scanner(System.in)");
                }
                
                writer.write(";\n");
            }
            
            writer.write("\n");
            
            // Write methods
            for (String method : classMethods.get(className)) {
                String[] parts = method.split(":");
                String visibility = parts[0];
                boolean isAbstract = parts.length > 1 && parts[1].equals("abstract");
                String methodName = parts[isAbstract ? 2 : 1];
                String parameters = parts[isAbstract ? 3 : 2];
                String returnType = parts[isAbstract ? 4 : 3];
                
                String javaVisibility = "private";
                if (visibility.equals("+")) javaVisibility = "public";
                else if (visibility.equals("#")) javaVisibility = "protected";
                
                if (isAbstract) {
                    writer.write("    " + javaVisibility + " abstract " + returnType + " " + methodName + "(" + parameters + ");\n");
                } else {
                    writer.write("    " + javaVisibility + " " + returnType + " " + methodName + "(" + parameters + ") {\n");
                    
                    // Implementation for mostrarResultado
                    if (methodName.equals("mostrarResultado")) {
                        writer.write("        System.out.println(\"El área es: \" + " + parameters + ");\n");
                    } else {
                        writer.write("        // Implementation\n");
                    }
                    
                    writer.write("    }\n");
                }
                
                writer.write("\n");
            }
            
            writer.write("}\n");
        }
    }
    
    private void generateConcreteClassFile(String className) throws IOException {
        String filePath = outputDirectory + File.separator + className + ".java";
        String parentClass = classInheritance.getOrDefault(className, "Object");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("package figuranew;\n\n");
            writer.write("public class " + className + " extends " + parentClass + " {\n");
            
            // Write fields
            for (String field : classFields.get(className)) {
                String[] parts = field.split(":");
                String visibility = parts[0];
                String fieldName = parts[1];
                String fieldType = parts[2];
                
                String javaVisibility = "private";
                if (visibility.equals("+")) javaVisibility = "public";
                else if (visibility.equals("#")) javaVisibility = "protected";
                
                writer.write("    " + javaVisibility + " " + fieldType + " " + fieldName + ";\n");
            }
            
            writer.write("\n");
            
            // Write methods
            for (String method : classMethods.get(className)) {
                String[] parts = method.split(":");
                String visibility = parts[0];
                boolean isAbstract = parts.length > 1 && parts[1].equals("abstract");
                String methodName = parts[isAbstract ? 2 : 1];
                String parameters = parts[isAbstract ? 3 : 2];
                String returnType = parts[isAbstract ? 4 : 3];
                
                String javaVisibility = "private";
                if (visibility.equals("+")) javaVisibility = "public";
                else if (visibility.equals("#")) javaVisibility = "protected";
                
                writer.write("    @Override\n");
                writer.write("    " + javaVisibility + " " + returnType + " " + methodName + "(" + parameters + ") {\n");
                
                // Implementation based on method name and class
                if (methodName.equals("calculateArea")) {
                    switch (className) {
                        case "Circle":
                            writer.write("        double area = Math.PI * radio * radio;\n");
                            writer.write("        mostrarResultado(area);\n");
                            break;
                        case "Square":
                            writer.write("        double area = base * altura;\n");
                            writer.write("        mostrarResultado(area);\n");
                            break;
                        case "Rectangle":
                            writer.write("        double area = base * altura;\n");
                            writer.write("        mostrarResultado(area);\n");
                            break;
                        case "Triangle":
                            writer.write("        double area = (base * altura) / 2;\n");
                            writer.write("        mostrarResultado(area);\n");
                            break;
                        case "Pentagon":
                            writer.write("        double area = (5 * lado * apotema) / 2;\n");
                            writer.write("        mostrarResultado(area);\n");
                            break;
                        default:
                            writer.write("        // Implementation\n");
                    }
                } else if (methodName.equals("pedirDatos")) {
                    switch (className) {
                        case "Circle":
                            writer.write("        System.out.print(\"Ingrese el radio del círculo: \");\n");
                            writer.write("        radio = scanner.nextDouble();\n");
                            break;
                        case "Square":
                            writer.write("        System.out.print(\"Ingrese el lado del cuadrado: \");\n");
                            writer.write("        base = altura = scanner.nextDouble();\n");
                            break;
                        case "Rectangle":
                            writer.write("        System.out.print(\"Ingrese la base del rectángulo: \");\n");
                            writer.write("        base = scanner.nextDouble();\n");
                            writer.write("        System.out.print(\"Ingrese la altura del rectángulo: \");\n");
                            writer.write("        altura = scanner.nextDouble();\n");
                            break;
                        case "Triangle":
                            writer.write("        System.out.print(\"Ingrese la base del triángulo: \");\n");
                            writer.write("        base = scanner.nextDouble();\n");
                            writer.write("        System.out.print(\"Ingrese la altura del triángulo: \");\n");
                            writer.write("        altura = scanner.nextDouble();\n");
                            break;
                        case "Pentagon":
                            writer.write("        System.out.print(\"Ingrese el lado del pentágono: \");\n");
                            writer.write("        lado = scanner.nextDouble();\n");
                            writer.write("        System.out.print(\"Ingrese el apotema del pentágono: \");\n");
                            writer.write("        apotema = scanner.nextDouble();\n");
                            break;
                        default:
                            writer.write("        // Implementation\n");
                    }
                } else {
                    writer.write("        // Implementation\n");
                }
                
                writer.write("    }\n\n");
            }
            
            writer.write("}\n");
        }
    }
    
    private void generateAppFile() throws IOException {
        String filePath = outputDirectory + File.separator + "App.java";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("package figuranew;\n\n");
            writer.write("import java.util.Scanner;\n\n");
            writer.write("public class App {\n");
            writer.write("    public static void main(String[] args) {\n");
            writer.write("        Scanner scanner = new Scanner(System.in);\n");
            writer.write("        int option;\n");
            writer.write("        do {\n");
            writer.write("            System.out.println(\"Calculadora de Área - Seleccione una opción:\");\n");
            writer.write("            System.out.println(\"1. Círculo\");\n");
            writer.write("            System.out.println(\"2. Cuadrado\");\n");
            writer.write("            System.out.println(\"3. Triángulo\");\n");
            writer.write("            System.out.println(\"4. Rectángulo\");\n");
            writer.write("            System.out.println(\"5. Pentágono\");\n");
            writer.write("            System.out.println(\"0. Salir\");\n");
            writer.write("            System.out.print(\"Opción: \");\n");
            writer.write("            option = scanner.nextInt();\n");
            writer.write("            \n");
            writer.write("            Figura figura = null;\n");
            writer.write("            \n");
            writer.write("            switch (option) {\n");
            writer.write("                case 1:\n");
            writer.write("                    figura = new Circle();\n");
            writer.write("                    break;\n");
            writer.write("                case 2:\n");
            writer.write("                    figura = new Square();\n");
            writer.write("                    break;\n");
            writer.write("                case 3:\n");
            writer.write("                    figura = new Triangle();\n");
            writer.write("                    break;\n");
            writer.write("                case 4:\n");
            writer.write("                    figura = new Rectangle();\n");
            writer.write("                    break;\n");
            writer.write("                case 5:\n");
            writer.write("                    figura = new Pentagon();\n");
            writer.write("                    break;\n");
            writer.write("                case 0:\n");
            writer.write("                    System.out.println(\"Saliendo...\");\n");
            writer.write("                    break;\n");
            writer.write("                default:\n");
            writer.write("                    System.out.println(\"Opción no válida\");\n");
            writer.write("            }\n");
            writer.write("            \n");
            writer.write("            if (figura != null) {\n");
            writer.write("                figura.pedirDatos();\n");
            writer.write("                figura.calcularArea();\n");
            writer.write("            }\n");
            writer.write("            \n");
            writer.write("        } while (option != 0);\n");
            writer.write("        scanner.close();\n");
            writer.write("    }\n");
            writer.write("}\n");
        }
    }
}