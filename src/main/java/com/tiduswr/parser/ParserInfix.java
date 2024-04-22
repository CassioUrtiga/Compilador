package com.tiduswr.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tiduswr.Lexer;
import com.tiduswr.parser.exceptions.SyntaxError;
import com.tiduswr.Token;
import com.tiduswr.TokenType;

public class ParserInfix implements Closeable {

    private static final int BUFFER_SIZE = 10;
    private List<Token> buffer;
    Lexer lexer;

    public ParserInfix(Lexer lexer) throws IOException {
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
            System.out.println(" ");
        }
    }

    private void expr() {
        var la = lookAhead(1);

        if (la.type() == TokenType.CONST_INT || la.type() == TokenType.CONST_FLOAT || la.type() == TokenType.ABRE_PAR) {
            termo();
            la = lookAhead(1);
            while (la.type() == TokenType.OP_SUM || la.type() == TokenType.OP_SUB) {
                op1();
                termo();
                la = lookAhead(1);
            }
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.CONST_INT, TokenType.CONST_FLOAT, TokenType.ABRE_PAR);
        }
    }

    private void termo() {
        var la = lookAhead(1);

        if (la.type() == TokenType.CONST_INT || la.type() == TokenType.CONST_FLOAT || la.type() == TokenType.ABRE_PAR) {
            fator();
            la = lookAhead(1);
            while (la.type() == TokenType.OP_MUL || la.type() == TokenType.OP_DIV) {
                op2();
                fator();
                la = lookAhead(1);
            }
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.CONST_INT, TokenType.CONST_FLOAT, TokenType.ABRE_PAR);
        }
    }

    private void fator() {
        var la = lookAhead(1);

        if (la.type() == TokenType.CONST_INT) {
            match(TokenType.CONST_INT);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.CONST_FLOAT) {
            match(TokenType.CONST_FLOAT);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.ABRE_PAR) {
            match(TokenType.ABRE_PAR);
            System.out.println("Token reconhecido: " + la);
            expr();
            la = lookAhead(1);
            if (la.type() == TokenType.FECHA_PAR) {
                match(TokenType.FECHA_PAR);
                System.out.println("Token reconhecido: " + la);
            } else {
                throw new SyntaxError(lookAhead(1), TokenType.FECHA_PAR);
            }
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.CONST_INT, TokenType.CONST_FLOAT, TokenType.ABRE_PAR);
        }
    }

    private void op1() {
        var la = lookAhead(1);

        if (la.type() == TokenType.OP_SUM) {
            match(TokenType.OP_SUM);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.OP_SUB) {
            match(TokenType.OP_SUB);
            System.out.println("Token reconhecido: " + la);
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.OP_SUM,
                    TokenType.OP_SUB);
        }
    }

    private void op2() {
        var la = lookAhead(1);

        if (la.type() == TokenType.OP_MUL) {
            match(TokenType.OP_MUL);
            System.out.println("Token reconhecido: " + la);
        } else if (la.type() == TokenType.OP_DIV) {
            match(TokenType.OP_DIV);
            System.out.println("Token reconhecido: " + la);
        } else {
            throw new SyntaxError(lookAhead(1), TokenType.OP_MUL,
                    TokenType.OP_DIV);
        }
    }

    @Override
    public void close() throws IOException {
        lexer.close();
    }

}