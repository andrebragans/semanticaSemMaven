package compilador;
public enum TipoToken {
    //Representação de todos os tipos de Tokens
    PCDeclaracoes, PCAlgoritmo, PCInteiro, PCReal, PCAtribuir, PCA, PCLer,
    PCImprimir, PCSe, PCEntao, PCSenao,PCEnquanto, PCinicio, PCFim,
    OpArtmult, OpAritDiv, OpAritSoma, OpAritSub,
    OpRelMenor, OpRelMenorIgual, OpRelMaiorIgual,
    OpRelMaior, OpRelIgual, OpRelDif,
    OpBoolE, OpBoolOu,
    Delim, AbrePar, FechaPar, Var, NumInt, NumReal, Cadeia, Fim
}