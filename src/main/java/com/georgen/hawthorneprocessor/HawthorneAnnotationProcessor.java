package com.georgen.hawthorneprocessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

import static com.georgen.hawthorneprocessor.HawthorneAnnotationMessage.*;

@SupportedAnnotationTypes({
        "com.georgen.hawthorne.api.annotations.SingletonEntity",
        "com.georgen.hawthorne.api.annotations.EntityCollection",
        "com.georgen.hawthorne.api.annotations.Id",
        "com.georgen.hawthorne.api.annotations.BinaryData"
})
public class HawthorneAnnotationProcessor extends AbstractProcessor {
    private TypeElement singletonEntityElement;
    private TypeElement entityCollectionElement;
    private TypeElement idElement;
    private TypeElement binaryDataElement;

    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();

        this.singletonEntityElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.SingletonEntity");
        this.entityCollectionElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.EntityCollection");
        this.idElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.Id");
        this.binaryDataElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.BinaryData");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.errorRaised() && !roundEnv.processingOver()){
            processRound(annotations, roundEnv);
        }

        return false;
    }

    private void processRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        for (TypeElement annotation : annotations){
           for (Element element : roundEnv.getElementsAnnotatedWith(annotation)){
               processElement(element);
           }
        }
    }

    private void processElement(Element element){
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        List<VariableElement> fields = ElementFilter.fieldsIn(enclosedElements);

        processClassAnnotations(element, fields);
        processFieldAnnotations(element);
    }

    private void processClassAnnotations(Element element, List<VariableElement> fields){
        boolean hasEntityCollectionAnnotation = false;
        boolean hasSingletonEntityAnnotation = false;

        for (AnnotationMirror annotation : element.getAnnotationMirrors()){
            Element annotationElement = annotation.getAnnotationType().asElement();

            if (isEntityCollection(annotationElement)){
                processEntityCollection(element, fields);
                hasEntityCollectionAnnotation = true;
            }

            if (isSingletonEntity(annotationElement)){
                hasSingletonEntityAnnotation = true;
            }
        }

        if (hasEntityCollectionAnnotation && hasSingletonEntityAnnotation){
            printMessage(CLASS_ANNOTATION_DUPLICATE, element);
        }
    }

    private void processEntityCollection(Element element, List<VariableElement> fields){
        boolean hasIdAnnotation = false;

        for (VariableElement field : fields){
            List<? extends AnnotationMirror> annotations = field.getAnnotationMirrors();

            if (hasAnnotation(annotations, this.idElement)) hasIdAnnotation = true;
        }

        if (!hasIdAnnotation){
            printMessage(ENTITY_COLLECTION_HAS_NO_ID, element);
        }
    }

    private void processFieldAnnotations(Element element){
        if (hasAnnotation(element, this.idElement)) processId(element);
        if (hasAnnotation(element, this.binaryDataElement)) processBinaryData(element);
    }

    private void processId(Element element){
        Element enclosingElement = element.getEnclosingElement();
        List<? extends AnnotationMirror> annotations = enclosingElement.getAnnotationMirrors();

        if (!hasAnnotation(annotations, this.entityCollectionElement)){
            printMessage(ID_HAS_NO_ENTITY_COLLECTION, element);
        }
    }

    private void processBinaryData(Element element){
        if (!isInClassWithEntityAnnotation(element)){
            printMessage(BINARY_DATA_HAS_NO_CLASS_ANNOTATION, element);
        }
    }

    private boolean isSingletonEntity(Element element){ return this.singletonEntityElement.equals(element); }

    private boolean isEntityCollection(Element element){ return this.entityCollectionElement.equals(element); }

    private boolean isInClassWithEntityAnnotation(Element element){
        Element enclosingElement = element.getEnclosingElement();
        List<? extends AnnotationMirror> annotations = enclosingElement.getAnnotationMirrors();

        boolean hasEntityCollectionAnnotation = hasAnnotation(annotations, this.entityCollectionElement);
        boolean hasSingletonEntityAnnotation = hasAnnotation(annotations, this.singletonEntityElement);

        return hasEntityCollectionAnnotation || hasSingletonEntityAnnotation;
    }

    private boolean hasAnnotation(List<? extends AnnotationMirror> annotations, Element referenceType){
        for (AnnotationMirror annotation : annotations){
            DeclaredType annotationType = annotation.getAnnotationType();
            if (referenceType.equals(annotationType.asElement())) return true;
        }
        return false;
    }

    private boolean hasAnnotation(Element element, Element referenceType){
        for (AnnotationMirror annotation : element.getAnnotationMirrors()){
            Element annotationElement = annotation.getAnnotationType().asElement();
            if (referenceType.equals(annotationElement)) return true;
        }
        return false;
    }

    private void printMessage(HawthorneAnnotationMessage message, Element element){
        this.messager.printMessage(message.getKind(), message.getMessage(), element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
