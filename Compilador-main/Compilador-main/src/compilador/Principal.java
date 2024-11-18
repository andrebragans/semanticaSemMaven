package compilador;
public class Principal {
    public static void main(String[] args) {
        Lexico lex = new Lexico(args[0]);
        Sintatico sin = new Sintatico(lex);
        sin.programa();
    }
}
