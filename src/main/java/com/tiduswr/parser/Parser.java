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
        while (!lookAhead(1).type().equals(TokenType.EOF)) {
            expr();
            match(TokenType.PONTO_VIRGULA);
        }
    }
    
    private void expr(){
        var la = lookAhead(1);
    
        if (la.type() == TokenType.ABRE_PAR) {
            match(TokenType.ABRE_PAR);
            System.out.println("Token reconhecido: " + la.type());
            expr();
            if (lookAhead(1).type() == TokenType.FECHA_PAR) {
                match(TokenType.FECHA_PAR);
                System.out.println("Token reconhecido: " + TokenType.FECHA_PAR);
            } else {
                throw new SyntaxError(la, TokenType.FECHA_PAR);
            }
        } else if (la.type() == TokenType.CONST_INT || la.type() == TokenType.CONST_FLOAT) {
            match(la.type());
            System.out.println("Token reconhecido: " + la.type());
            if (lookAhead(1).type() == TokenType.OP_SUM || lookAhead(1).type() == TokenType.OP_SUB || lookAhead(1).type() == TokenType.OP_MUL) {
                op();
                expr();
            }
        } else if (la.type() == TokenType.OP_SUM || la.type() == TokenType.OP_SUB || la.type() == TokenType.OP_MUL) {
            op();
            expr();
            expr();
        } else if (la.type() == TokenType.PONTO_VIRGULA) {
            // Ignora o ponto e vírgula e termina a expressão
        } else {
            throw new SyntaxError(la, TokenType.ABRE_PAR, TokenType.OP_SUM, TokenType.OP_SUB, TokenType.OP_MUL, TokenType.CONST_INT, TokenType.CONST_FLOAT, TokenType.PONTO_VIRGULA);
        }
    }

    private void op() {
        var la = lookAhead(1);
    
        if (la.type() == TokenType.OP_SUM || la.type() == TokenType.OP_SUB || la.type() == TokenType.OP_MUL || la.type() == TokenType.OP_DIV) {
            match(la.type());
            System.out.println("Operador reconhecido: " + la.type());
        } else {
            throw new SyntaxError(la, TokenType.OP_SUM, TokenType.OP_SUB, TokenType.OP_MUL, TokenType.OP_DIV);
        }
    }
    
    @Override
    public void close() throws IOException {
        lexer.close();
    }

}