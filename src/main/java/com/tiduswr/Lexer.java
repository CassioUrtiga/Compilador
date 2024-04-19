package com.tiduswr;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Lexer implements Closeable {

    private Buffer buffer;

    public Lexer(String programPath) throws FileNotFoundException {
        buffer = new Buffer(programPath);
    }

    public Token readNextToken() throws IOException {
        int charAtual;
        StringBuilder lexema = new StringBuilder();

        while ((charAtual = buffer.readNextChar()) != Buffer.EOF) {
            char charConvertido = (char) charAtual;
            System.out.println("charconvertido: " + charConvertido);
            // IGNORA ESPAÇOS EM BRANCO E PROCESSA O LEXEMA
            if (Character.isWhitespace(charConvertido)) {
                System.out.println("vazio?: " + charConvertido);

                if (lexema.length() > 0) {
                    System.out.println("PROCESSA ESPACO: ");
                    return processaLexema(lexema.toString());
                }
                continue;
            }

            if (Character.isDigit(charConvertido) || charConvertido == '.'
                    || (charConvertido == '-' && lexema.length() == 0)) {
                System.out.println("digito || - || . : " + charConvertido);

                if (charConvertido == '-' && lexema.length() == 0) {
                    lexema.append(charConvertido);
                    continue;
                }

                lexema.append(charConvertido);

                while ((charAtual = buffer.readNextChar()) != Buffer.EOF) {
                    charConvertido = (char) charAtual;
                    if (Character.isDigit(charConvertido) || charConvertido == '.') {
                        lexema.append(charConvertido);
                    } else {
                        buffer.pushback(charAtual);
                        break;
                    }
                }
                System.out.println("PROCESSA: ");
                return processaLexema(lexema.toString());
            }
            //TRATA COMENTARIOS
            if (charConvertido == '#') {
                System.out.println("##?: " + charConvertido);

                while ((charAtual = buffer.readNextChar()) != Buffer.EOF && charAtual != '\n') {
                }
                continue;
            }

            lexema.append(charConvertido);
            System.out.println("PROCESSA: " + lexema);
            return processaLexema(lexema.toString());
        }

        if (lexema.length() > 0) {
            System.out.println("PROCESSA: ");
            return processaLexema(lexema.toString());
        }

        return new Token(TokenType.EOF, null);
    }

    private Token processaLexema(String lexema) {
        switch (lexema) {
            case "(":
                return new Token(TokenType.ABRE_PAR, lexema);
            case ")":
                return new Token(TokenType.FECHA_PAR, lexema);
            case "+":
                return new Token(TokenType.OP_SUM, lexema);
            case "-":
                return new Token(TokenType.OP_SUB, lexema);
            case "*":
                return new Token(TokenType.OP_MUL, lexema);
            case "/":
                return new Token(TokenType.OP_DIV, lexema);
            case ";":
                return new Token(TokenType.PONTO_VIRGULA, lexema);
            default:
                if (lexema.matches("-?\\d+(\\.\\d+)?")) {
                    if (lexema.contains(".")) {
                        return new Token(TokenType.CONST_FLOAT, lexema);
                    } else {
                        return new Token(TokenType.CONST_INT, lexema);
                    }
                } else if (lexema.equals("")) {
                    return new Token(TokenType.EOF, null); // Fim de arquivo
                } else {
                    System.out.println("erro?: ");
                    throw new IllegalArgumentException("Erro de sintaxe: " + lexema);
                }
        }
    }

    @Override
    public void close() throws IOException {
        if (buffer != null)
            buffer.close();
    }

}
