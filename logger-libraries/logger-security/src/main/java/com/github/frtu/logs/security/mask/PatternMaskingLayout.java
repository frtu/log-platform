package com.github.frtu.logs.security.mask;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @see <a href="https://www.schibsted.pl/blog/logback-pattern-gdpr/">PatternMaskingLayout</a>
 */
public class PatternMaskingLayout extends ch.qos.logback.classic.PatternLayout {

    private Pattern multilinePattern;
    private List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) { // invoked for every single entry in the xml
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(
                maskPatterns.stream()
                        .collect(Collectors.joining("|")), // build pattern using logical OR
                Pattern.MULTILINE
        );
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event)); // calling superclass method is required
    }

    private String maskMessage(String message) {
        if (multilinePattern == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group))
                            .forEach(i -> sb.setCharAt(i, '*')); // replace each character with asterisk
                }
            });
        }
        return sb.toString();
    }

}
