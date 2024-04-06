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
        //Cascata de criação das regras da gramatica começa aqui
        //Crie uma função para cada regra da gramática
    }

    @Override
    public void close() throws IOException {
        lexer.close();
    }

}