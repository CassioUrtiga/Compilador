package com.tiduswr.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tiduswr.Lexer;
import com.tiduswr.parser.exceptions.SyntaxError;
import com.tiduswr.Token;
import com.tiduswr.TokenType;

public class ParserPrefix implements Closeable{
    
    private static final int BUFFER_SIZE = 10;
    private List<Token> buffer;
    Lexer lexer;
    

    public ParserPrefix(Lexer lexer) throws IOException {
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
        expr(); 
        System.out.print(" FIM PARSE: "+ lookAhead(1).lexema()); 
        
    }   

    private void expr(){
        if (lookAhead(1).type() == TokenType.OP_SUB ||
            lookAhead(1).type() == TokenType.OP_SUM) {
            operador1(); //+ -> 3
            termo(); //3 -> 4
        }
        termo();//4 -> ;//
    }
    private void termo(){
        if (lookAhead(1).type() == TokenType.OP_DIV ||
            lookAhead(1).type() == TokenType.OP_MUL) {
            operador2();
            operando();
        }
        operando();//3 -> 4// 4 -> ;

    }

    private void operando(){
        if (lookAhead(1).type() == TokenType.CONST_INT) {
            match(TokenType.CONST_INT);
            System.out.print(lookAhead(1).lexema() + " ");
        }
        if (lookAhead(1).type() == TokenType.OP_SUB ||
            lookAhead(1).type() == TokenType.OP_SUM ||
            lookAhead(1).type() == TokenType.OP_DIV ||
            lookAhead(1).type() == TokenType.OP_MUL) {
            expr();
        }
        if (lookAhead(1).type() == TokenType.PONTO_VIRGULA) {
            match(TokenType.PONTO_VIRGULA);
            System.out.println(lookAhead(1).lexema() + " ");
            expr();
        }
    }

    private void operador1(){
        Token op1 = lookAhead(1);
        match(op1.type());
        System.out.print(lookAhead(1).lexema() + " ");
    }

    private void operador2(){
        Token op2 = lookAhead(1);
        match(op2.type());
        System.out.print(lookAhead(1).lexema() + " ");
    }
    

    
    
    @Override
    public void close() throws IOException {
        lexer.close();
    }

}