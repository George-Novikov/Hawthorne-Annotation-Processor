package com.georgen.hawthorneprocessor;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

@SupportedAnnotationTypes({
        "com.georgen.hawthorne.api.annotations.SingletonEntity",
        "com.georgen.hawthorne.api.annotations.EntityCollection",
        "com.georgen.hawthorne.api.annotations.Id",
        "com.georgen.hawthorne.api.annotations.BinaryData"
})
public class AnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;

    private TypeElement singletonEntityElement;
    private TypeElement entityCollectionElement;
    private TypeElement idElement;
    private TypeElement binaryDataElement;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();

        this.singletonEntityElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.SingletonEntity");
        this.entityCollectionElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.EntityCollection");
        this.idElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.Id");
        this.idElement = elementUtils.getTypeElement("com.georgen.hawthorne.api.annotations.BinaryData");
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

    }
}
