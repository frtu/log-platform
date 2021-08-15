package com.github.frtu.logs.core.metadata;

public class ExecutionSpanSample {
    @ExecutionSpan
    public void simpleSpan(String param1, String param2) {
    }

    @ExecutionSpan({@Tag(tagName = "tag1", tagValue = "value1"),
            @Tag(tagName = "tag2", tagValue = "value2")})
    public void spanWithTags() {
    }

    @ExecutionSpan(@Tag(tagName = "tag1", tagValue = "value1"))
    public void spanWithTags(@ToTag("param-tag") String param1, String param2) {
    }

    @ExecutionSpan
    public void spanForLog(String param1, @ToLog("param2") String param2) {
    }

    public void noAnnotation() {
    }
}
