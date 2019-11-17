package com.github.frtu.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Result of an annotation method scan {@link AnnotationMethodScanner#scan(Method)}.
 *
 * @param <MethodAnno> Annotation class used on method
 * @param <ParamAnno> Annotation class used on method parameter
 * @author fred
 * @since 0.9.1
 */
public class AnnotationMethodScan<MethodAnno extends Class<? extends Annotation>, ParamAnno extends Class<? extends Annotation>> {
    public final static AnnotationMethodScan EMPTY = new AnnotationMethodScan(null, null, null);

    private Method method;
    private Map<String, Object> annotationAttributes;
    private Annotation[] paramAnnotation;

    AnnotationMethodScan(Method method, Map<String, Object> annotationAttributes, Annotation[] paramAnnotation) {
        this.method = method;
        this.annotationAttributes = annotationAttributes;
        this.paramAnnotation = paramAnnotation;
    }

    /**
     * The scanned {@link Method}.
     *
     * @return method scanned
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Raw fields contained in the method annotation.
     *
     * @return KV of all the annotation fields
     */
    public Map<String, Object> getAnnotationAttributes() {
        return annotationAttributes;
    }

    /**
     * Shortcut for annotation field 'value' that returns an array of annotations.
     *
     * @param <NestedAnno> Nested annotation type for 'value'
     * @return all the annotations object
     */
    public <NestedAnno extends Annotation> NestedAnno[] getAnnotationValueArray() {
        final NestedAnno[] annotationArray = (NestedAnno[]) annotationAttributes.get("value");
        return annotationArray;
    }

    /**
     * Array of annotations from each fields.
     *
     * @param <ParamAnno> Annotation class used on method parameter
     * @return Empty array when no annotation found
     */
    public <ParamAnno extends Annotation> ParamAnno[] getParamAnnotations() {
        return (ParamAnno[]) paramAnnotation;
    }

    @Override
    public String toString() {
        return "AnnotationMethodScan{" +
                "method=" + method +
                ", annotationAttributes=" + annotationAttributes +
                ", paramAnnotation=" + Arrays.toString(paramAnnotation) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationMethodScan<?, ?> that = (AnnotationMethodScan<?, ?>) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(annotationAttributes, that.annotationAttributes) &&
                Arrays.equals(paramAnnotation, that.paramAnnotation);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method, annotationAttributes);
        result = 31 * result + Arrays.hashCode(paramAnnotation);
        return result;
    }
}
