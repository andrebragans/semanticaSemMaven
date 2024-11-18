package compilador;
import compilador.TipoToken;
import compilador.Token;
import compilador.Lexico;
import java.util.ArrayList;
import java.util.List;


public class Sintatico {
    private final static int TAMANHO_BUFFER = 1000;
    List<Token> bufferTokens;
    Lexico lex;
    boolean chegouNoFim = false;

    //construtor do metodo Sentatico
    public Sintatico(Lexico lex){
        this.lex = lex;
        bufferTokens = new ArrayList<>();
        lerToken();
    }

    //Metodo que ler os tokens antes de passar pelo analisador Sintatico
    private void lerToken(){
        if(bufferTokens.size() > 0){
            bufferTokens.remove(0);
        }
        while (bufferTokens.size() < TAMANHO_BUFFER && !chegouNoFim) {
            Token proximo = lex.proximoToken();
            bufferTokens.add(proximo);
            if(proximo.nome == TipoToken.Fim){
                chegouNoFim = true;
            }
        }
        System.out.println("Lido: " + lookahead(1));
    }

    //Verificação se o Buffer de Token esta Vazio
    Token lookahead(int k){
        if(bufferTokens.isEmpty()){
            return null;
        }
        if(k-1 >= bufferTokens.size()){
            return bufferTokens.get(bufferTokens.size() - 1);
        }
        return bufferTokens.get(k - 1);
    }

    //Metodo Auxiliar
    void match(TipoToken tipo){
        if(lookahead(1).nome == tipo){
            System.out.println("Matcha: " + lookahead(1));
            lerToken();
        }else{
            erroSitatico(tipo.toString());
        }
    }

    //Metodo para tratar o erro sintetico
    void erroSitatico(String... tokensEsperados){
        String mensagem = "Erro sintatico: esperando um dos seguintes(";
        for(int i=0; i < tokensEsperados.length; i++){
           mensagem += tokensEsperados[i];
           if(i<tokensEsperados.length -1){
            mensagem += ",";
           } 
        }
        mensagem += "), mas foi encontrado" + lookahead(1);
        throw new RuntimeException(mensagem); //Caso tenha erro, a maquina é parada
    }
// ------------------------------Nova Gramatica----------------------------

//Programa : ':' 'DECLARACOES' listaDeClaracoes ':' 'ALGORITMO' listaComando
    public void programa(){
        match(TipoToken.Delim);
        match(TipoToken.PCDeclaracoes);
        listaDeclaracao();
        match(TipoToken.Delim);
        match(TipoToken.PCAlgoritmo);
        listaComandos();
        match(TipoToken.Fim);
    }

//listaDeClaracoes : declaracao listaDeclaracao | declaracao;
    void listaDeclaracao(){
        if(lookahead(4).nome == TipoToken.Var){
            declaracao();
            listaDeclaracao();
        }else if(lookahead(4).nome == TipoToken.Delim){
            declaracao();
        }else{
            erroSitatico(TipoToken.Delim.toString(), TipoToken.Var.toString());
        }  
    }

//declaracao : VARIAVEL ':' tipoVar;
    void declaracao(){
        match(TipoToken.Var);
        match(TipoToken.Delim);
        tipoVar();
    }

//tipoVar : 'INTEIRO' | 'REAL';
    void tipoVar(){
        if(lookahead(1).nome == TipoToken.PCInteiro){
            match(TipoToken.PCInteiro);
        }else if(lookahead(1).nome == TipoToken.PCReal){
            match(TipoToken.PCReal);
        }else{
            erroSitatico("Inteiro", "Real");
        }
    }

//expressaoAritmetica : expressaoAritmetica '+' termoAritmetico | expressaoAritmetica '-' termoAritmetico | termoAritmetico
    void expressaoAritmetica(){
       termoAritmetico();
       expressaoAritmetica2();
    }

    void expressaoAritmetica2(){
        if(lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.OpAritSub){
            expressaoAritmetica2SubRegra1();
            expressaoAritmetica2();
        }else{
            //Vazio
        }
    }

    void expressaoAritmetica2SubRegra1(){
        if(lookahead(1).nome == TipoToken.OpAritSoma){
            match(TipoToken.OpAritSoma);
            termoAritmetico();
        }else if(lookahead(1).nome == TipoToken.OpAritSub){
            match(TipoToken.OpAritSub);
            termoAritmetico();
        }else{
            erroSitatico("+", "-");
        }
    }

//termoAritmetico : expressaoAritmetica '*' termoAritmetico | expressaoAritmetica '/' termoAritmetico | termoAritmetico
    void termoAritmetico(){
        fatorAritmetico();
        termoAritmetico2();
    }

    void termoAritmetico2(){
        if(lookahead(1).nome == TipoToken.OpArtmult){
            match(TipoToken.OpArtmult);
            fatorAritmetico();
        }else if(lookahead(1).nome == TipoToken.OpAritDiv){
            match(TipoToken.OpAritDiv);
            fatorAritmetico();
        }else{
            erroSitatico("*", "/");
        }
    }

//fatorAritmetico : NUMINT | NUMREAL | VARIAVEL | '(' expressaoAritmetica ')'
    void fatorAritmetico(){
        if(lookahead(1).nome == TipoToken.NumInt){
            match(TipoToken.NumInt);
        }else if (lookahead(1).nome == TipoToken.NumReal){
            match(TipoToken.NumReal);
        }else if (lookahead(1).nome == TipoToken.Var){
            match(TipoToken.Var);
        }else if (lookahead(1).nome == TipoToken.AbrePar){
            match(TipoToken.AbrePar);
            expressaoAritmetica();
            match(TipoToken.FechaPar);
        }else{
            erroSitatico("NumInt", "NumReal", "Variavel", "(");
        }
    }

//expressaoRelacional : expressaoRelacional operadorBoleano termoRelacional |termoRelacional;
    void expressaoRelacional(){
        termoRelacional();
        expressaoRelacional2();
    }

    void expressaoRelacional2(){
        if(lookahead(1).nome == TipoToken.OpBoolE || lookahead(1).nome == TipoToken.OpBoolOu){
            operadorBoleano();
            termoRelacional();
            expressaoRelacional2();
        }else{
            //Vazio
        }
    }
//termoRelacional : expressaoAritmetica OP_REL expressaoAritmetica;
    void termoRelacional(){
        if(lookahead(1).nome == TipoToken.NumInt 
                || lookahead(1).nome == TipoToken.NumReal 
                || lookahead(1).nome == TipoToken.Var 
                || lookahead(1).nome == TipoToken.AbrePar){
            expressaoAritmetica();
            opRel();
            expressaoAritmetica();
        }else{
            erroSitatico("NumInt", "NumReal", "Var", "(");
        }
    }

    void opRel(){
        if(lookahead(1).nome == TipoToken.OpRelDif){
            match(TipoToken.OpRelDif);
        }else if(lookahead(1).nome == TipoToken.OpRelIgual){
            match(TipoToken.OpRelIgual);
        }else if(lookahead(1).nome == TipoToken.OpRelMaior){
            match(TipoToken.OpRelMaior);
        }else if(lookahead(1).nome == TipoToken.OpRelMaiorIgual){
            match(TipoToken.OpRelMaiorIgual);
        }else if(lookahead(1).nome == TipoToken.OpRelMenor){
            match(TipoToken.OpRelMenor);
        }else if(lookahead(1).nome == TipoToken.OpRelMenorIgual){
            match(TipoToken.OpRelMenorIgual);
        }else{
            erroSitatico("<>", "=", ">", ">=", "<", "<=");
        }
    }

//operadorBooleano : 'E' | 'OU';
    void operadorBoleano(){
        if(lookahead(1).nome == TipoToken.OpBoolE){
            match(TipoToken.OpBoolE);
        }else if(lookahead(1).nome == TipoToken.OpBoolOu){
            match(TipoToken.OpBoolOu);
        }else{
            erroSitatico("E", "OU");
        }
    }

//listaComandos : comando listaComandos | comando;
    void listaComandos(){
        comando();
        listaComandosSubRegra1();
    }

   void listaComandosSubRegra1(){
    if(lookahead(1).nome == TipoToken.PCAtribuir 
            || lookahead(1).nome == TipoToken.PCLer 
            || lookahead(1).nome == TipoToken.PCImprimir 
            || lookahead(1).nome == TipoToken.PCSe 
            || lookahead(1).nome == TipoToken.PCEnquanto 
            || lookahead(1).nome == TipoToken.PCinicio){
        listaComandos();
    }else{
        //vazio
        }
    }

//comando : comandoAtribuicao | comandoEntrada | comandoSaida | comandoCondicao | comandoRepeticao | subAlgoritmo
    void comando(){
        if(lookahead(1).nome == TipoToken.PCAtribuir){
            comandoAtribuicao();
        }else if(lookahead(1).nome == TipoToken.PCLer){
            comandoEntrada();
        }else if(lookahead(1).nome == TipoToken.PCImprimir){
            comandoSaida();
        }else if(lookahead(1).nome == TipoToken.PCSe){
            comandoCondicao();
        }else if(lookahead(1).nome == TipoToken.PCEnquanto){
            comandoRepeticao();
        }else if(lookahead(1).nome == TipoToken.PCinicio){
            subAlgoritmo();
        }else{
            erroSitatico("ATRIBUIR", "LER", "IMPRIMIR", "SE", "ENQUANTO", "INICIO");
        }
    }

//comandoAtribuicao : 'ATRIBUIR' expressaoAritmetica 'A' VARIAVEL;
    void comandoAtribuicao(){
        match(TipoToken.PCAtribuir);
        expressaoAritmetica();
        match(TipoToken.PCA);
        match(TipoToken.Var);
    }

//comandoEntrada : 'LER' VARIAVEL
    void comandoEntrada(){
        match(TipoToken.PCLer);
        match(TipoToken.Var);
    }

//comando : 'IMPRIMIR' (VARIAVEL | CADEIA);
    void comandoSaida(){
        match(TipoToken.PCImprimir);
        comandoSaidaSubRegra1();
    }

    void comandoSaidaSubRegra1(){
        if(lookahead(1).nome == TipoToken.Var){
            match(TipoToken.Var);
        }else if(lookahead(1).nome == TipoToken.Cadeia){
            match(TipoToken.Cadeia);
        }else{
            erroSitatico("Var", "Cadeia");
        }
    }

//comandoCondicao : 'SE' expressaoRelacional 'ENTAO' comando | 'SE' expressaoRelacional 'ENTAO'
//comandoCondicao : 'SE' expressaoRelacional 'ENTAO' comando ('SENAO' comando | <<vazio>>)
    void comandoCondicao(){
        match(TipoToken.PCSe);
        expressaoRelacional();
        match(TipoToken.PCEntao);
        comando();
        comandoCondicaoSubRegra1();
    }

    void comandoCondicaoSubRegra1(){
        if(lookahead(1).nome == TipoToken.PCSenao){
            match(TipoToken.PCSenao);
            comando();
        }else{
            //Vazio
        }
    }

//comandoRepeticao : 'ENQUANTO' expressaoRelacional comando;
    void comandoRepeticao(){
        match(TipoToken.PCEnquanto);
        expressaoRelacional();
        comando();
    }

//subAlgoritmo : 'INICIO' listaComandos 'FIM'
    void subAlgoritmo(){
        match(TipoToken.PCinicio);
        listaComandos();
        match(TipoToken.PCFim);
        match(TipoToken.PCinicio);
        listaComandos();
        match(TipoToken.PCFim);
        
    }
     
}
   