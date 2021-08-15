package com.github.frtu.logs.core.metadata;

/**
 * A helper to assist usage of {@link ExecutionSpan}
 *
 * @author Frédéric TU
 * @since 1.1.3 : Refactored to remove outside dependencies
 */
public class ExecutionHelper {
    private boolean isFullClassName;

    public ExecutionHelper() {
        this(false);
    }

    public ExecutionHelper(boolean isFullClassName) {
        this.isFullClassName = isFullClassName;
    }

    /**
     * Get span name based on declaringType and methodName
     *
     * @param declaringType   Classname based on isFullClassName will use long or short name
     * @param methodName      method name
     * @return String signature name
     */
    public String getName(Class declaringType, String methodName) {
        return getName(declaringType, methodName, this.isFullClassName);
    }

    /**
     * Get span name based on declaringType and methodName
     *
     * @param declaringType   Classname
     * @param methodName      method name
     * @param isFullClassName if should return class canonical name or short name
     * @return String signature name
     */
    public String getName(Class declaringType, String methodName, boolean isFullClassName) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (isFullClassName) {
            stringBuilder.append(declaringType.getCanonicalName());
        } else {
            stringBuilder.append(declaringType.getSimpleName());
        }
        return stringBuilder.append('.').append(methodName).toString();
    }
}
