package com.example.apitestmanager.atm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class DynamicPOJOGenerator {

    private static final ClassLoader CLASS_LOADER = DynamicPOJOGenerator.class.getClassLoader();
    private static final Map<String, Class<?>> CLASS_MAP = new HashMap<>();

    // Generate POJO class from JSON string
    public static Class<?> generatePOJO(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        // Generate root class
        Class<?> rootClass = generateNestedClass("Root", rootNode, mapper);
        return rootClass;
    }

    // Generate a nested class from JSON node
    private static Class<?> generateNestedClass(String className, JsonNode jsonNode, ObjectMapper mapper) throws Exception {
        DynamicType.Builder<Object> builder = new ByteBuddy().subclass(Object.class).name(className);

        for (Iterator<String> fieldNames = jsonNode.fieldNames(); fieldNames.hasNext(); ) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = jsonNode.get(fieldName);

            Class<?> fieldType = determineFieldType(fieldName, fieldValue, mapper);
            builder = builder.defineField(fieldName, fieldType, Visibility.PUBLIC);
        }

        // Create and load the class
        DynamicType.Loaded<Object> loaded = builder.make()
                .load(CLASS_LOADER, ClassLoadingStrategy.Default.CHILD_FIRST);

        Class<?> generatedClass = loaded.getLoaded();
        CLASS_MAP.put(className, generatedClass);
        return generatedClass;
    }

    // Determine the field type based on JSON node
    private static Class<?> determineFieldType(String fieldName, JsonNode fieldValue, ObjectMapper mapper) throws Exception {
        if (fieldValue.isInt()) {
            return Integer.TYPE;
        } else if (fieldValue.isLong()) {
            return Long.TYPE;
        } else if (fieldValue.isFloat() || fieldValue.isDouble()) {
            return Double.TYPE;
        } else if (fieldValue.isBoolean()) {
            return Boolean.TYPE;
        } else if (fieldValue.isTextual()) {
            return String.class;
        } else if (fieldValue.isObject()) {
            // Generate a nested class with a capitalized name
            String nestedClassName = capitalize(fieldName) + "Class";
            return generateNestedClass(nestedClassName, fieldValue, mapper);
        } else if (fieldValue.isArray()) {
            // Generate a class for array elements
            String listClassName = capitalize(fieldName) + "List";
            return generateListType(listClassName, fieldValue, mapper);
        }
        return Object.class;
    }

    // Generate a class for list elements
    private static Class<?> generateListType(String listClassName, JsonNode arrayNode, ObjectMapper mapper) throws Exception {
        Class<?> elementType = determineElementType(arrayNode, mapper);

        DynamicType.Builder<Object> builder = new ByteBuddy().subclass(Object.class)
                .name(listClassName)
                .defineField("_items", List.class, Visibility.PUBLIC);

        DynamicType.Loaded<Object> loaded = builder.make()
                .load(CLASS_LOADER, ClassLoadingStrategy.Default.CHILD_FIRST);

        Class<?> listClass = loaded.getLoaded();
        CLASS_MAP.put(listClassName, listClass);
        return listClass;
    }

    // Determine the element type for arrays
    private static Class<?> determineElementType(JsonNode arrayNode, ObjectMapper mapper) throws Exception {
        if (arrayNode.size() == 0) {
            return Object.class;
        }
        JsonNode firstElement = arrayNode.get(0);
        return determineFieldType("item", firstElement, mapper);
    }

    // Capitalize the first letter of a string
    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    // Access generated classes by name
    public static Class<?> getGeneratedClass(String className) {
        return CLASS_MAP.get(className);
    }

    // Access generated list classes by name
    public static Class<?> getListTypeClass(String listClassName) {
        return CLASS_MAP.get(listClassName);
    }

    // Print all fields of a dynamically generated class
    public static void printClassFields(String className) {
        try {
            Class<?> clazz = getGeneratedClass(className);
            if (clazz != null) {
                System.out.println("Fields of class " + className + ":");
                for (Field field : clazz.getDeclaredFields()) {
                    System.out.println(" - " + field.getName() + " : " + field.getType().getName());
                }
            } else {
                System.out.println("Class " + className + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
