package com.tiduswr.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.tiduswr.Lexer;
import com.tiduswr.parser.exceptions.SyntaxError;
import com.tiduswr.Token;
import com.tiduswr.TokenType;

public class ParserInfix implements Closeable{
    
    private static final int BUFFER_SIZE = 10;
    private List<Token> buffer;
    Lexer lexer;
    

    public ParserInfix(Lexer lexer) throws IOException {
        this.lexer = lexer;
        buffer = new ArrayList<>();
        confirmToken();
    }

    private void confirmToken() throws IOException {
    
        if(buffer.size() > 0) buffer.remove(0);
        
        while(buffer.size() < BUFFER_SIZE){
            var next = lexer.readNextToken();

            if(next.type() == TokenType.COMMENT) 
                continue;
                
            buffer.add(next);

            if(next.type() == TokenType.EOF) 
                break;
        }
    
    }
    private Token lookAhead(int k) {
        if(buffer.isEmpty()) return null;

        return k-1 >= buffer.size() ? buffer.get(buffer.size()-1) : buffer.get(k-1);
    }

    private void match(TokenType type){
        var la = lookAhead(1);

        if(la.type() == type){
            try{
                confirmToken();
            }catch(IOException ex){
                System.out.println(ex.getLocalizedMessage());
            }
        }else{
            throw new SyntaxError(la, type);
        }
    }

    public void parse(){
        System.out.print(" INICIO PARSE: "+ lookAhead(1).lexema());
        expr(); //3 -> 
        System.out.print(" FIM PARSE: "+ lookAhead(1).lexema()); 
    }   
    private void expr(){
        
        termo();//3 ->
        while(lookAhead(1).type() == TokenType.OP_SUM || lookAhead(1).type() == TokenType.OP_SUB){
            Token op = lookAhead(1);
            match(op.type());
            System.out.print(" "+ lookAhead(1).lexema()); 
            expr(); 
        }
    }

    private void termo(){
        
        fator(); //3 -> 
        while(lookAhead(1).type() == TokenType.OP_MUL || lookAhead(1).type() == TokenType.OP_DIV){
            Token op = lookAhead(1);
            match(op.type());
            System.out.print(" " + lookAhead(1).lexema()); 
            termo();
        }

    }

    private void fator(){
        Token token = lookAhead(1);
        if (lookAhead(1).type() == TokenType.OP_DIV ||
            lookAhead(1).type() == TokenType.OP_MUL ||
            lookAhead(1).type() == TokenType.OP_SUB ||
            lookAhead(1).type() == TokenType.OP_SUM){
            match(TokenType.CONST_INT);
        }

        if(lookAhead(1).type() == TokenType.CONST_INT || 
           lookAhead(1).type() == TokenType.CONST_FLOAT){

            token = lookAhead(1);
            match(token.type());

            System.out.print(" "+ lookAhead(1).lexema());

            // if (lookAhead(1).type() != TokenType.OP_DIV &&
            //     lookAhead(1).type() != TokenType.OP_MUL &&
            //     lookAhead(1).type() != TokenType.OP_SUB &&
            //     lookAhead(1).type() != TokenType.OP_SUM &&
            //     lookAhead(1).type() != TokenType.PONTO_VIRGULA&&
            //     lookAhead(1).type() != TokenType.FECHA_PAR){
            //     System.out.print(" opa: "+ lookAhead(1).lexema());
            //     match(TokenType.OPERADOR);
            // }
            if (lookAhead(1).type() == TokenType.ABRE_PAR) {
                System.out.print(" opa: "+ lookAhead(1).lexema());
                match(TokenType.OPERADOR);
            }
        }else if(lookAhead(1).type() == TokenType.ABRE_PAR){

            match(TokenType.ABRE_PAR);

            if (lookAhead(1).type() == TokenType.FECHA_PAR) {
                token = lookAhead(1);
                match(TokenType.CONST_INT);
            }

            System.out.print(" "+ lookAhead(1).lexema()); 
            expr(); //1 -> )
            match(TokenType.FECHA_PAR);
            if (lookAhead(1).type() == TokenType.FECHA_PAR) {
                match(TokenType.OPERADOR);
            }

            System.out.print(" "+ lookAhead(1).lexema());
        }else if(lookAhead(1).type() == TokenType.FECHA_PAR){
            match(TokenType.OPERADOR);
            System.out.print(" opa: "+ lookAhead(1).lexema());
        }
        if (lookAhead(1).type() == TokenType.PONTO_VIRGULA) {
            match(TokenType.PONTO_VIRGULA);
            System.out.println(lookAhead(1).lexema() + ' ');
            expr();
        }
    }
    
    @Override
    public void close() throws IOException {
        lexer.close();
    }

}