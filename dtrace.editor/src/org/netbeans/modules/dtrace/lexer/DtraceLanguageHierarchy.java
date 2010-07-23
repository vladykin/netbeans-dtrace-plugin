package org.netbeans.modules.dtrace.lexer;

import java.util.Collection;
import java.util.EnumSet;
//import org.netbeans.api.lexer.InputAttributes;
//import org.netbeans.api.lexer.LanguagePath;
//import org.netbeans.api.lexer.Token;
//import org.netbeans.cnd.api.lexer.CppTokenId;
//import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * @author Alexey Vladykin
 */
public class DtraceLanguageHierarchy extends LanguageHierarchy<DtraceTokenId> {

    @Override
    protected Collection<DtraceTokenId> createTokenIds() {
        return EnumSet.allOf(DtraceTokenId.class);
    }

    @Override
    protected Lexer<DtraceTokenId> createLexer(LexerRestartInfo<DtraceTokenId> info) {
        return new DtraceLexer(info);
    }

//  This requires dependency on cnd.lexer.
//    @Override
//    protected LanguageEmbedding<?> embedding(Token<DtraceTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
//        switch (token.id()) {
//            case PREPROCESSOR:
//                return LanguageEmbedding.create(CppTokenId.languagePreproc(), 0, 0);
//            default:
//                return null;
//        }
//    }

    @Override
    protected String mimeType() {
        return "text/x-dtrace"; // NOI18N
    }
}
