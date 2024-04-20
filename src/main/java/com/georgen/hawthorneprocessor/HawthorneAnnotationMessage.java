package com.georgen.hawthorneprocessor;

import javax.tools.Diagnostic;

public enum HawthorneAnnotationMessage {
    ID_HAS_NO_ENTITY_COLLECTION(
            Diagnostic.Kind.ERROR,
            "The @Id annotation must only be present in classes marked with the @EntityCollection annotation."
    ),

    ENTITY_COLLECTION_HAS_NO_ID(
            Diagnostic.Kind.ERROR,
            "Classes marked with the @EntityCollection must have also a field annotated with @Id."
    ),

    BINARY_DATA_HAS_NO_CLASS_ANNOTATION(
            Diagnostic.Kind.ERROR,
            "The @BinaryData annotation must only be present in classes marked with the @SingletonEntity or @EntityCollection annotation."
    ),

    CLASS_ANNOTATION_DUPLICATE(
            Diagnostic.Kind.ERROR,
            "A class cannot have the @SingletonEntity and @EntityCollection annotations at the same time."
    );

    private final Diagnostic.Kind kind;
    private final String message;

    HawthorneAnnotationMessage(Diagnostic.Kind kind, String message) {
        this.kind = kind;
        this.message = message;
    }

    public Diagnostic.Kind getKind() {
        return kind;
    }

    public String getMessage() {
        return message;
    }
}
