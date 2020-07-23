package net.htlgrieskirchen.aud2.map.generator;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("net.htlgrieskirchen.aud2.map.generator.AutoTest")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TestProcessor extends AbstractProcessor {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static AutoTestParameters convertAnnotationToContainer(AutoTest annotation) {
        Type testInterface = null;
        Type shouldImplementation = null;
        Type testImplementation = null;
        Type[] genericArguments = null;

        try {
            annotation.testInterface();
        } catch(MirroredTypeException e) {
            testInterface = (Type) e.getTypeMirror();
        }

        try {
            annotation.shouldImplementation();
        } catch(MirroredTypeException e) {
            shouldImplementation = (Type) e.getTypeMirror();
        }

        try {
            annotation.testImplementation();
        } catch(MirroredTypeException e) {
            testImplementation = (Type) e.getTypeMirror();
        }

        try {
            annotation.genericArguments();
        } catch(MirroredTypesException e) {
            genericArguments = e.getTypeMirrors().stream().map(mirror -> ((Type) mirror)).toArray(Type[]::new);
        }

        return new AutoTestParameters(testInterface, shouldImplementation, testImplementation, genericArguments);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<? extends Element> collect = annotations.stream().flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream()).collect(Collectors.toList());

        for(Element annotatedElement : collect) {
            AutoTest annotation = annotatedElement.getAnnotation(AutoTest.class);
            try {
                DeclaredType declaredType = (DeclaredType) annotatedElement.asType();

                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(declaredType.toString() + "Generated", annotatedElement);

                try(Writer writer = sourceFile.openWriter()) {
                    writer.write(generateTest((Type.ClassType) declaredType, convertAnnotationToContainer(annotation)));
                }
            } catch(Exception e) {
                e.printStackTrace(System.err);
                System.err.flush();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), annotatedElement);
            }
        }

        return true;
    }

    private String generateTest(Type.ClassType annotatedElement, AutoTestParameters annotation) {
        StringBuilder sourceCode = new StringBuilder();

        sourceCode
                .append("package ").append(annotatedElement.asElement().packge().fullname).append(";\n")
                .append("\n")
                .append("import org.junit.Test;\n")
                .append("import static org.junit.Assert.*;\n")
                .append("\n")
                .append("import ").append(annotation.shouldImplementation.asElement().getQualifiedName()).append(";\n")
                .append("import ").append(annotation.testImplementation.asElement().getQualifiedName()).append(";\n")
                .append("\n")
                .append("public class ").append(annotatedElement.asElement().getSimpleName()).append("Generated {\n")
                .append("\tprivate ").append(annotation.shouldImplementation.asElement().getSimpleName()).append(" expected;\n")
                .append("\tprivate ").append(annotation.testImplementation.asElement().getSimpleName()).append(" testing;\n");

        List<Symbol.MethodSymbol> methods =
                annotation.testInterface.asElement().getEnclosedElements()
                        .stream()
                        .filter(symbol -> symbol instanceof Symbol.MethodSymbol)
                        .map(symbol -> ((Symbol.MethodSymbol) symbol))
                        .filter(symbol -> !(symbol.isDefault() || symbol.isStatic() || symbol.isDeprecated() || symbol.isPrivate()))
                        .collect(Collectors.toList());

        for(Symbol.MethodSymbol method : methods) {
            sourceCode.append("\n")
                    .append("\t@Test\n")
                    .append("\tpublic void test").append(Character.toUpperCase(method.name.charAt(0))).append(method.name.toString().substring(1)).append("() {\n");

            sourceCode.append(generateTestForMethod(method, annotation));

            sourceCode.append("\n\t}\n");
        }

        sourceCode.append("}");
        return sourceCode.toString();
    }

    private String generateTestForMethod(Symbol.MethodSymbol method, AutoTestParameters annotation) {
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("assertEquals(expected, testing);\n");

        String parameters = "";

        if(method.getParameters().size() != 0) {
            parameters = method.getParameters().stream().map(parameter -> parameter.type).map(type -> {
                String typeName = type.asElement().getQualifiedName().toString();

                //If the current parameter is a generic argument
                Optional<Symbol.TypeVariableSymbol> typeParameter = method.getEnclosingElement().getTypeParameters().stream().filter(i -> i.name.toString().equals(type.asElement().getQualifiedName().toString())).findAny();
                if(typeParameter.isPresent()) {
                    int index = method.getEnclosingElement().getTypeParameters().indexOf(typeParameter.get());
                    typeName = annotation.genericArguments[index].asElement().getQualifiedName().toString();
                }

                switch(typeName) {
                    case "java.lang.String":
                        return "\"testing\"";
                    case "java.lang.Object":
                        return "new Object()";
                    case "java.util.Map":
                        return "new java.util.HashMap()";
                    default:
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class " + typeName + " not handled!");
                        return "null";
                }

            }).collect(Collectors.joining(", "));
        }

        Type returnType = method.getReturnType();
        if(returnType.isPrimitive() || !returnType.isPrimitiveOrVoid()) {
            sourceCode.append("assertEquals(")
                    .append("expected.").append(method.name).append("(").append(parameters).append("), ")
                    .append("testing.").append(method.name).append("(").append(parameters).append(")")
                    .append(");\n");
        } else {
            sourceCode
                    .append("expected.").append(method.name).append("(").append(parameters).append(");\n")
                    .append("testing.").append(method.name).append("(").append(parameters).append(");\n");
        }

        sourceCode.append("assertEquals(expected, testing);");

        String[] lines = sourceCode.toString().split("\n");
        return Arrays.stream(lines).map(line -> "\t\t" + line).collect(Collectors.joining("\n"));
    }

    private static class AutoTestParameters {
        private final Type testInterface;
        private final Type shouldImplementation;
        private final Type testImplementation;
        private final Type[] genericArguments;

        public AutoTestParameters(Type testInterface, Type shouldImplementation, Type testImplementation, Type[] genericArguments) {
            this.testInterface = testInterface;
            this.shouldImplementation = shouldImplementation;
            this.testImplementation = testImplementation;
            this.genericArguments = genericArguments;
        }
    }
}
