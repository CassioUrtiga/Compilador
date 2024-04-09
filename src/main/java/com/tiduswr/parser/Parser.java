package com.tiduswr.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tiduswr.Lexer;
import com.tiduswr.parser.exceptions.SyntaxError;
import com.tiduswr.Token;
import com.tiduswr.TokenType;

public class Parser implements Closeable{
    
    private static final int BUFFER_SIZE = 10;
    private List<Token> buffer;
    Lexer lexer;

    public Parser(Lexer lexer) throws IOException {
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
        System.out.println(" INICIO PARSE"+ lookAhead(1));
        expr(); 
        System.out.println(" FIM PARSE"+ lookAhead(1));
    }   
    private void expr(){
        
        System.out.println("EXPR -> TERMO 1 "+ lookAhead(1));
        termo();  
        System.out.println("RESULT EXPR -> TERMO 1 "+ lookAhead(1));
        while(lookAhead(1).type() == TokenType.OP_SUM || lookAhead(1).type() == TokenType.OP_SUB){
            Token op = lookAhead(1);
            match(op.type());
            System.out.println("EXPR -> TERMO WHILEE"+ lookAhead(1)); 
            expr(); 
            System.out.println("RESULT EXPR -> TERMO WHILEE"+ lookAhead(1));   
        }

    }
    private void termo(){
        System.out.println("TERMO -> FATOR 1"+ lookAhead(1));
        fator(); 
        while(lookAhead(1).type() == TokenType.OP_MUL || lookAhead(1).type() == TokenType.OP_DIV){
            Token op = lookAhead(1);
            match(op.type());
            System.out.println("TERMO -> FATOR WHILEE"+ lookAhead(1)); 
            termo();
            System.out.println("RESULT TERMO -> FATOR WHILEE"+ lookAhead(1)); 
        }
        

    }
    private void fator(){

        Token token = lookAhead(1);
        
        while (token.type() == TokenType.ABRE_PAR || token.type() == TokenType.CONST_INT) {
            if(token.type() == TokenType.ABRE_PAR){
                match(TokenType.ABRE_PAR); 
                System.out.println("FATOR -> EXPR"+ lookAhead(1)); 
                expr(); 
                System.out.println("RESULT FATOR -> EXPR"+ lookAhead(1)); 
                match(TokenType.FECHA_PAR);
                System.out.println("ULTIMA FATOR"+ lookAhead(1)); 
                token = lookAhead(1);
                if(token.type() == TokenType.FECHA_PAR){
                    match(TokenType.CONST_INT);
                }
    
            }else{
                match(TokenType.CONST_INT); 
                token = lookAhead(1);
                if(token.type() == TokenType.FECHA_PAR){
                    match(TokenType.ABRE_PAR);
                }
            } 
        }
    }
    
    @Override
    public void close() throws IOException {
        lexer.close();
    }

}