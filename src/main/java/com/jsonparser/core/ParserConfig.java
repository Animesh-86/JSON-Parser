package com.jsonparser.core;

public class ParserConfig {
    private boolean allowComments = false;
    private boolean allowSingleQuotes = false;
    private boolean allowUnquotedKeys = false;
    private boolean allowTrailingCommas = false;
    private boolean allowJson5Numbers = false; // hex, + sign, Infinity, NaN

    public static ParserConfig strict() {
        return new ParserConfig();
    }

    public static ParserConfig json5() {
        ParserConfig config = new ParserConfig();
        config.allowComments = true;
        config.allowSingleQuotes = true;
        config.allowUnquotedKeys = true;
        config.allowTrailingCommas = true;
        config.allowJson5Numbers = true;
        return config;
    }

    public boolean isAllowComments() { return allowComments; }
    public ParserConfig setAllowComments(boolean allowComments) { this.allowComments = allowComments; return this; }

    public boolean isAllowSingleQuotes() { return allowSingleQuotes; }
    public ParserConfig setAllowSingleQuotes(boolean allowSingleQuotes) { this.allowSingleQuotes = allowSingleQuotes; return this; }

    public boolean isAllowUnquotedKeys() { return allowUnquotedKeys; }
    public ParserConfig setAllowUnquotedKeys(boolean allowUnquotedKeys) { this.allowUnquotedKeys = allowUnquotedKeys; return this; }

    public boolean isAllowTrailingCommas() { return allowTrailingCommas; }
    public ParserConfig setAllowTrailingCommas(boolean allowTrailingCommas) { this.allowTrailingCommas = allowTrailingCommas; return this; }

    public boolean isAllowJson5Numbers() { return allowJson5Numbers; }
    public ParserConfig setAllowJson5Numbers(boolean allowJson5Numbers) { this.allowJson5Numbers = allowJson5Numbers; return this; }
}
