package dev.rafandoo.finv.enums;

import dev.rafandoo.cup.enums.TypedValue;

public enum Parameters {

    USER_AGENT("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (HTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"),
    REQUEST_TIMEOUT(15000),;

    private final TypedValue value;

    Parameters(Object value) {
        this.value = new TypedValue(value);
    }

    public TypedValue get() {
        return this.value;
    }
}
