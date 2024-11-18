package compilador;


import java.util.HashMap;
import java.util.Stack;


public class Semantico {
    // Pilha de escopos para gerenciar variáveis em diferentes níveis de bloco
    private final Stack<HashMap<String, TipoVariavel>> pilhaEscopos;
    private TipoVariavel tipoAtual;

    public Semantico() {
        pilhaEscopos = new Stack<>();
        // Inicializa com um escopo global
        pilhaEscopos.push(new HashMap<>());
    }

    // Método para declarar uma variável no escopo atual
    public void declararVariavel(String nome, TipoVariavel tipo) {
        HashMap<String, TipoVariavel> escopoAtual = pilhaEscopos.peek();
        if (escopoAtual.containsKey(nome)) {
            throw new RuntimeException("Erro semântico: a variável '" + nome + "' já foi declarada no escopo atual.");
        }
        escopoAtual.put(nome, tipo);
        System.out.println("Variável '" + nome + "' declarada com tipo: " + tipo);
    }

    // Método para verificar o uso de uma variável
    public TipoVariavel usarVariavel(String nome) {
        for (int i = pilhaEscopos.size() - 1; i >= 0; i--) {
            HashMap<String, TipoVariavel> escopo = pilhaEscopos.get(i);
            if (escopo.containsKey(nome)) {
                System.out.println("Variável '" + nome + "' usada com tipo: " + escopo.get(nome));
                return escopo.get(nome);
            }
        }
        throw new RuntimeException("Erro semântico: a variável '" + nome + "' não foi declarada.");
    }

    // Método para verificar compatibilidade de tipos

   
    public void verificarCompatibilidade(TipoVariavel tipo1, TipoVariavel tipo2) {
        if (tipo1 == TipoVariavel.REAL && tipo2 == TipoVariavel.INTEIRO) {
            // Compatível: inteiro pode ser convertido para real
            return;
        } else if (tipo1 == TipoVariavel.INTEIRO && tipo2 == TipoVariavel.REAL) {
            // Compatível: real pode ser usado em operações com inteiros
            return;
        } else if (tipo1 != tipo2) {
            throw new RuntimeException("Erro semântico: tipos incompatíveis. Esperado " + tipo1 + ", encontrado " + tipo2);
        }
    }

    // Método para iniciar um novo escopo
    public void iniciarEscopo() {
        pilhaEscopos.push(new HashMap<>());
        System.out.println("Novo escopo iniciado.");
    }

    // Método para encerrar o escopo atual
    public void encerrarEscopo() {
        if (pilhaEscopos.size() > 1) { // Nunca remover o escopo global
            pilhaEscopos.pop();
            System.out.println("Escopo encerrado.");
        } else {
            throw new RuntimeException("Erro semântico: tentativa de remover o escopo global.");
        }
    }

    // Define o tipo atual durante a declaração de variáveis
    public void setTipoAtual(TipoVariavel tipoAtual) {
        this.tipoAtual = tipoAtual;
    }

    public TipoVariavel getTipoAtual() {
        return this.tipoAtual;
    }
}
