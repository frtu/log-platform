package com.github.frtu.logs.core.metadata;

/**
 * A helper to assist usage of {@link ExecutionSpan}
 *
 * @author Frédéric TU
 * @since 1.1.3 : Refactored to remove outside dependencies
 */
public class ExecutionHelper {
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
