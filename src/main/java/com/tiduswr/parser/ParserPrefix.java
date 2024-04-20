package com.tiduswr.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tiduswr.Lexer;
import com.tiduswr.parser.exceptions.SyntaxError;
import com.tiduswr.Token;
import com.tiduswr.TokenType;

public class ParserPrefix implements Closeable {

    private static final int BUFFER_SIZE = 10;
    private List<Token> buffer;
    Lexer lexer;

    public ParserPrefix(Lexer lexer) throws IOException {
        this.lexer = lexer;
        buffer = new ArrayList<>();
        confirmToken();
    }

    private void confirmToken() throws IOException {

        if (buffer.size() > 0)
            buffer.remove(0);

        while (buffer.size() < BUFFER_SIZE) {
            var next = lexer.readNextToken();

            if (next.type() == TokenType.COMMENT)
                continue;

            buffer.add(next);

            if (next.type() == TokenType.EOF)
                break;
        }

    }

    private Token lookAhead(int k) {
        if (buffer.isEmpty())
            return null;

        return k - 1 >= buffer.size() ? buffer.get(buffer.size() - 1) : buffer.get(k - 1);
    }

    private void match(TokenType type) {
        var la = lookAhead(1);

        if (la.type() == type) {
            try {
                confirmToken();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        } else {
            throw new SyntaxError(la, type);
        }
    }

    public void parse() {
        while (lookAhead(1).type() != TokenType.EOF) {
            expr();
            match(TokenType.PONTO_VIRGULA);
            System.out.println(' ');
        }
    }

    private void expr() {
        op();
        fator();
        fator();
    }

    private void fator() {
        var la = lookAhead(1);

        if (la.type() == TokenType.CONST_INT || la.type() == TokenType.CONST_FLOAT) {
            match(la.type());
            System.out.println("Token reconhecido: " + la);
        } else {
            expr();
        }
    }

    private void op() {
        var la = lookAhead(1);

        if (la.type() == TokenType.OP_SUM) {
            match(TokenType.OP_SUM);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.OP_SUB) {
            match(TokenType.OP_SUB);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.OP_MUL) {
            match(TokenType.OP_MUL);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.OP_DIV) {
            match(TokenType.OP_DIV);
            System.out.println("Token reconhecido: " + la);
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.OP_SUM,
                    TokenType.OP_SUB, TokenType.OP_MUL, TokenType.OP_DIV);
        }
    }

    @Override
    public void close() throws IOException {
        lexer.close();
    }

}
