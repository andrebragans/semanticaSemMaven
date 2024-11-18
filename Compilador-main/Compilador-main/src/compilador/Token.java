package compilador;
public class Token {
    public TipoToken nome;
    public String lexema;

    //Construtor
    public Token(TipoToken nome, String lexema) {
        this.nome = nome;
        this.lexema = lexema;
    }

    //Representação de String da classe Token
    @Override
    public String toString(){
       return "<"+nome+","+lexema+">"; 
    }
}