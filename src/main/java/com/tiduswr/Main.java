package com.tiduswr;

import java.io.IOException;

import com.tiduswr.parser.ParserInfix;
import com.tiduswr.parser.ParserPrefix;

public class Main {
    public static void main(String[] args) throws IOException {
        int parte = 1;

        switch (parte) {
            case 0:
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
                break;
            case 1:
                // executa a parte sintática infixa
                try(var parser = new ParserInfix(new Lexer("arquivo.mat"))){
                    parser.parse();
                }
                break;
            case 2:
                // executa a parte sintática prefixa
                try(var parser = new ParserPrefix(new Lexer("arquivo.mat"))){
                    parser.parse();
                }
                break;
            default:
                break;
        }
        
    }
}