package com.tiduswr;

import java.io.IOException;

import com.tiduswr.parser.Parser;

public class Main {
    public static void main(String[] args) throws IOException {
        // executa a parte sintática
        /* 
        var lexer = new Lexer("arquivo.mat");
        try(var parser = new Parser(lexer)){
            parser.parse();
        }*/

        // executa a parte léxica
        try (var lexer = new Lexer("arquivo.mat")){
            Token token;
            while ((token = lexer.readNextToken()).type() != TokenType.EOF){
                System.out.print(" ");
                if (token.type() == TokenType.PONTO_VIRGULA){
                    System.out.println(token);
                }else{
                    System.out.print(token);
                }
            }
            
        } catch (Exception e) {}

    }
}