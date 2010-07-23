package org.netbeans.modules.dtrace.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 * @author Alexey Vladykin
 */
public enum DtraceTokenId implements TokenId {

    WHITESPACE("whitespace"), // NOI18N
    COMMENT("comment"), // NOI18N
    STRING("string"), // NOI18N
    PREPROCESSOR("preprocessor"), // NOI18N
    SEPARATOR("separator"), // NOI18N
    KEYWORD("keyword"), // NOI18N
    IDENTIFIER("identifier"), // NOI18N
    BUILTIN("builtin"); // NOI18N

    private final String category;

    private DtraceTokenId(String category) {
        this.category = category;
    }

    @Override
    public String primaryCategory() {
        return category;
    }

    private static final Language<DtraceTokenId> LANGUAGE =
            new DtraceLanguageHierarchy().language();

    public static Language<DtraceTokenId> language() {
        return LANGUAGE;
    }
}
