package org.netbeans.modules.dtrace.lexer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * @author Alexey Vladykin
 */
public class DtraceLexer implements Lexer<DtraceTokenId> {

    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
            "char",
            "const",
            "double",
            "enum",
            "float",
            "inline",
            "int",
            "long",
            "self",
            "short",
            "string",
            "struct",
            "this",
            "typedef",
            "void"));

    // http://www.solarisinternals.com/wiki/index.php/DTrace_Topics_Cheatsheets_Checklists
    private static final Set<String> BUILTINS = new HashSet<String>(Arrays.asList(
            // variables
            "arg0",
            "arg1",
            "arg2",
            "arg3",
            "arg4",
            "arg5",
            "arg6",
            "arg7",
            "arg8",
            "arg9",
            "args",
            "caller",
            "curlwpsinfo",
            "curpsinfo",
            "curthread",
            "epid",
            "errno",
            "execname",
            "fds",
            "id",
            "ipl",
            "NULL",
            "pid",
            "probefunc",
            "probemod",
            "probename",
            "probeprov",
            "stackdepth",
            "tid",
            "timestamp",
            "vtimestamp",
            "walltimestamp",

            // tracing functions
            "breakpoint",
            "chill",
            "clear",
            "commit",
            "discard",
            "exit",
            "panic",
            "printa",
            "printf",
            "speculate",
            "stack",
            "stop",
            "trace",
            "tracemem",
            "trunc",
            "ustack",

            // aggregating functions
            "avg",
            "count",
            "lquantize",
            "max",
            "min",
            "sum",
            "quantize",

            // builtin functions
            "copyin",
            "copyinstr",
            "copyout",
            "copyoutstr",
            "mutex_owned",
            "mutex_owner",
            "mutex_type_adaptive",
            "mutex_type_spin",
            "offsetof",
            "progenyof",
            "raise",
            "rand",
            "rw_iswriter",
            "rw_read_held",
            "rw_write_held",
            "sizeof",
            "speculation",
            "stringof",
            "strjoin",
            "strlen",

            // types
            "chipid_t",
            "cpuinfo_t",
            "gid_t",
            "id_t",
            "int32_t",
            "int64_t",
            "intptr_t",
            "kmutex_t",
            "krwlock_t",
            "kthread_t",
            "lgrp_id_t",
            "lwpsinfo_t",
            "processorid_t",
            "psetid_t",
            "psinfo_t",
            "size_t",
            "uid_t",
            "uint_t",
            "uint32_t",
            "uint64_t",
            "uintptr_t"
            ));

    private final LexerRestartInfo<DtraceTokenId> info;

    public DtraceLexer(LexerRestartInfo<DtraceTokenId> info) {
        this.info = info;
    }

    @Override
    public Token<DtraceTokenId> nextToken() {
        final LexerInput input = info.input();
        int c = input.read();
        switch (c) {
            case LexerInput.EOF:
                return null;

            case '\t':
            case '\n':
            case '\r':
            case ' ':
                consumeWhitespace();
                return info.tokenFactory().createToken(DtraceTokenId.WHITESPACE);

            case '"':
            case '\'':
                consumeString((char) c);
                return info.tokenFactory().createToken(DtraceTokenId.STRING);

            case '.':
            case ',':
            case '(':
            case ')':
            case ';':
            case ':':
            case '-':
            case '+':
            case '{':
            case '}':
            case '=':
            case '<':
            case '>':
            case '*':
            case '!':
            case '&':
            case '|':
            case '[':
            case ']':
                return info.tokenFactory().createToken(DtraceTokenId.SEPARATOR);

            case '/':
                if (input.read() == '*') {
                    consumeComment();
                    return info.tokenFactory().createToken(DtraceTokenId.COMMENT);
                } else {
                    input.backup(1);
                    return info.tokenFactory().createToken(DtraceTokenId.SEPARATOR);
                }

            case '#':
                consumePreproc();
                return info.tokenFactory().createToken(DtraceTokenId.PREPROCESSOR);

            default:
                consumeIdentifier();
                return createIdentifierOrKeyword(input.readText().toString());
        }
    }

    private void consumeWhitespace() {
        LexerInput input = info.input();
        for (;;) {
            int c = input.read();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    // ok, continue reading
                    break;

                default:
                    input.backup(1);
                    return;
            }
        }
    }

    private void consumeComment() {
        LexerInput input = info.input();
        for (;;) {
            int c = input.read();
            switch (c) {
                case LexerInput.EOF:
                    return;

                case '*':
                    if (input.read() == '/') {
                        return;
                    } else {
                        input.backup(1);
                    }
                    break;
            }
        }
    }

    private void consumePreproc() {
        LexerInput input = info.input();
        for (;;) {
            int c = input.read();
            switch (c) {
                case '\\':
                    input.read();
                    break;

                case '\n':
                case LexerInput.EOF:
                    input.backup(1);
                    return;
            }
        }
    }

    private void consumeIdentifier() {
        LexerInput input = info.input();
        for (;;) {
            int c = input.read();
            if ('a' <= c && c <= 'z'
                    || 'A' <= c && c <= 'Z'
                    || '0' <= c && c <= '9'
                    || c == '_') {
                // continue
            } else {
                input.backup(1);
                return;
            }
        }
    }

    private void consumeString(char barrier) {
        LexerInput input = info.input();
        for (;;) {
            int c = input.read();
            switch (c) {
                case LexerInput.EOF:
                    return;

                case '\'':
                case '"':
                    if (barrier == c) {
                        return;
                    }
                    break;

                case '\\':
                    input.read();
                    break;
            }
        }
    }

    private Token<DtraceTokenId> createIdentifierOrKeyword(String token) {
        if (KEYWORDS.contains(token)) {
            return info.tokenFactory().createToken(DtraceTokenId.KEYWORD);
        } else if (BUILTINS.contains(token)) {
            return info.tokenFactory().createToken(DtraceTokenId.BUILTIN);
        } else {
            return info.tokenFactory().createToken(DtraceTokenId.IDENTIFIER);
        }
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
        // nothing to do
    }
}
