package conversao;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


class Conjunto {
    private LinkedHashSet<String> conjunto;

    public Conjunto () {
        conjunto = new LinkedHashSet<String>();
    }

    public LinkedHashSet<String> getConjunto () {
        return conjunto;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = this.getConjunto().iterator();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        return sb.toString();
    }

}

class AFD {
    private String tipoAutomato;
    // É usado um Linked.. para preservar a ordem de inserção
    private LinkedHashSet<String> conjuntoEstados;
    private LinkedHashSet<String> conjuntoSimbolos;
    private HashSet<String> conjuntoEstadosFinais;
    private String estadoInicial;

    // Como essa função de transição é construída dinâmicamente, optei por usar um Map (estado + simbolo) -> novoEstado 
    private HashMap<String, String> funcaoTransicao;
    // É a mesma função de transição, porém os estados compostos são mapeados para um único estado. Ex: <q0q1q2> -> q1
    private HashMap<String, String> novaFuncaoTransicao;
    // O novoConjuntoEstados é um mapeamento do estado antigo para o novo estado. Necessário pois ao pesquisar pelo estado antigo
    // no novoConjuntoEstados obtem-se o novo estado
    private LinkedHashMap<String, String> novoConjuntoEstados;
    private HashSet<String> novoConjuntoEstadosFinais;


    public AFD (AFN afn) {
        tipoAutomato = "AFD";
        conjuntoEstados = new LinkedHashSet<>();
        conjuntoSimbolos = new LinkedHashSet<>(afn.getConjuntoSimbolos().keySet());
        conjuntoEstadosFinais = new HashSet<>();
        estadoInicial = afn.getEstadoInicial();
        funcaoTransicao = new HashMap<>();
        novaFuncaoTransicao = new HashMap<>();
        novoConjuntoEstados = new LinkedHashMap<>();
        novoConjuntoEstadosFinais = new HashSet<>();
    }

    // Gera estados únicos. Ex: <q0q1q2> -> p0
    public void simplicarAFD() {

        // Cria o novo conjunto de estados
        int i = 0;
        for (String estado : this.getConjuntoEstados()) {
            String novoEstado = Integer.toString(i);
            this.getNovoConjuntoEstados().put(estado, novoEstado);
            i++;
        }

        // Cria o novo conjunto de estados finais
        for (String estadoFinal : this.getConjuntoEstadosFinais()) {
            String novoEstadoFinal = this.getNovoConjuntoEstados().get(estadoFinal);
            this.getNovoConjuntoEstadosFinais().add(novoEstadoFinal);
        }

        // Cria a nova função de transição
        for (String estado : this.getConjuntoEstados()) {
            for (String simbolo : this.getConjuntoSimbolos()) {
                if (this.getFuncaoTransicao().containsKey(estado + simbolo)) {
                    String novoEstado = this.getNovoConjuntoEstados().get(estado);
                    String novaChave = novoEstado + simbolo;
                    String proximoEstado = this.getFuncaoTransicao().get(estado + simbolo);
                    String novoProximoEstado = this.getNovoConjuntoEstados().get(proximoEstado);
                    this.getNovaFuncaoTransicao().put(novaChave, novoProximoEstado);
                    // System.out.printf(" Antes: δ(%s, %s) = %s\n",estado, simbolo, this.getFuncaoTransicao().get(estado + simbolo));
                    // System.out.printf(" Depois: δ(%s, %s) = %s\n",this.getNovoConjuntoEstados().get(estado), simbolo, this.getNovaFuncaoTransicao().get(novaChave));
                }
            }
        }


    }

    public void escreverArquivoSaida (String arquivoSaidaDescricaoAFD) {
        
        try {
            PrintWriter pw = new PrintWriter(arquivoSaidaDescricaoAFD, StandardCharsets.UTF_8);

            // Escreve o tipo do automato (AFD, AFN)
            pw.println(this.getTipoAutomato() + " # (Linha 1) Representação do formalismo");

            // Escreve os estados
            pw.print(this.getNovoConjuntoEstados().size());
            // As chaves são os estados antigos, e o valores são os novos estados
            for (String estado : this.getNovoConjuntoEstados().values()) {
                pw.printf(" %s", estado);
            }
            pw.printf(" # (Linha 2) %d estados: ", this.getNovoConjuntoEstados().size());
            int i = 0;
            for (String estado : this.getNovoConjuntoEstados().values()) {
                pw.print(estado);
                if (i == (this.getNovoConjuntoEstados().size() - 2)) {
                    pw.print(" e ");
                } else {
                    if (i != (this.getNovoConjuntoEstados().size() - 1)) {
                        pw.print(", ");
                    }
                }
                i++;
            }
            pw.println();

            // Escreve os simbolos
            pw.print(this.getConjuntoSimbolos().size());
            for (String simbolo : this.getConjuntoSimbolos()) {
                pw.printf(" %s", simbolo);
            }
            pw.printf(" # (Linha 3) %d símbolos: ", this.getConjuntoSimbolos().size());
            i = 0;
            for (String simbolo : this.getConjuntoSimbolos()) {
                pw.print(simbolo);
                if (i == (this.getConjuntoSimbolos().size() - 2)) {
                    pw.print(" e ");
                } else {
                    if (i != (this.getConjuntoSimbolos().size() - 1)) {
                        pw.print(", ");
                    }
                }
                i++;
            }
            pw.println();

            // Escreve o estado inicial
            pw.println(this.getEstadoInicial() + " # (Linha 4) O estado inicial é o " + this.getEstadoInicial());

            // Escreve os estados finais
            pw.print(this.getNovoConjuntoEstadosFinais().size());
            for (String estadoFinal : this.getNovoConjuntoEstadosFinais()) {
                pw.printf(" %s", estadoFinal);
            }
            if (this.getNovoConjuntoEstadosFinais().size() == 1) {
                pw.printf(" # (Linha 5) Possui %d estado final: o ", this.getNovoConjuntoEstadosFinais().size());
            }else {
                pw.printf(" # (Linha 5) Possui %d estados finais: ", this.getNovoConjuntoEstadosFinais().size());
            }
            i = 0;
            for (String estadoFinal : this.getNovoConjuntoEstadosFinais()) {
                pw.print(estadoFinal);
                if (i == (this.getNovoConjuntoEstadosFinais().size() - 2)) {
                    pw.print(" e ");
                } else {
                    if (i != (this.getNovoConjuntoEstadosFinais().size() - 1)) {
                        pw.print(", ");
                    }
                }
                i++;
            }
            pw.println();

            // Escreve as transições
            i = 0;
            for (String estado : this.getNovoConjuntoEstados().values()) {
                for (String simbolo : this.getConjuntoSimbolos()) {
                    if (this.getNovaFuncaoTransicao().containsKey(estado + simbolo)) {
                        if (i == 0) {
                            pw.printf("%s %s %s", estado, simbolo, this.getNovaFuncaoTransicao().get(estado + simbolo));
                            pw.printf(" # (Linha 6 em diante) δ(%s, %s) = %s\n",estado, simbolo, this.getNovaFuncaoTransicao().get(estado + simbolo));
                        } else {
                            pw.printf("%s %s %s", estado, simbolo, this.getNovaFuncaoTransicao().get(estado + simbolo));
                            pw.printf(" # δ(%s, %s) = %s\n",estado, simbolo, this.getNovaFuncaoTransicao().get(estado + simbolo));
                        }
                    }
                    i++;
                }
            }


            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    // Imprime o AFD após a conversão, ainda sem a simplificação dos estados
    public void imprimirAFDNaoSimplificado () {

        System.out.println();

        // Retorna todos os simbolos
        Supplier<String> simbolos = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoSimbolos().size();
            sb.append("{");
            for (String estado : this.getConjuntoSimbolos()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados
        Supplier<String> estados = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoEstados().size();
            sb.append("{");
            for (String estado : this.getConjuntoEstados()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados finais
        Supplier<String> estadosFinais = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoEstadosFinais().size();
            sb.append("{");
            for (String estadoFinal : this.getConjuntoEstadosFinais()) {
                sb.append(estadoFinal);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        System.out.println(this.getTipoAutomato() + 
            " = (" + simbolos.get() + ", " + estados.get() + ", δD, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");
        
        //Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);

        System.out.printf(formatarString.apply("δD"));
        for (String simbolo : this.getConjuntoSimbolos()) {
            System.out.printf(formatarString.apply(simbolo));
        }
        System.out.println();
        for (String estado : this.getConjuntoEstados()) {
            System.out.printf(formatarString.apply("<" + estado + ">"));
            for (String simbolo : this.getConjuntoSimbolos()) {
                // System.out.printf("%s %s %s\n", estado, simbolo, afd.getFuncaoTransicao()
                //     .get(afd.getConjuntoEstados().get(estado)).get(afd.getConjuntoSimbolos().get(simbolo)));
                String key = estado + simbolo;
                if (this.getFuncaoTransicao().containsKey(key)) {
                    System.out.printf(formatarString.apply("<" + this.getFuncaoTransicao().get(key) + ">"));
                } else {
                    System.out.printf(formatarString.apply("-"));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void imprimirAFDFinal () {

        System.out.println();

        // Retorna todos os simbolos
        Supplier<String> simbolos = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoSimbolos().size();
            sb.append("{");
            for (String estado : this.getConjuntoSimbolos()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados
        Supplier<String> estados = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getNovoConjuntoEstados().size();
            sb.append("{");
            for (String estado : this.getNovoConjuntoEstados().values()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados finais
        Supplier<String> estadosFinais = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getNovoConjuntoEstadosFinais().size();
            sb.append("{");
            for (String estadoFinal : this.getNovoConjuntoEstadosFinais()) {
                sb.append(estadoFinal);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        System.out.println(this.getTipoAutomato() + 
            " = (" + simbolos.get() + ", " + estados.get() + ", δD, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");
        
        //Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);

        System.out.printf(formatarString.apply("δD"));
        for (String simbolo : this.getConjuntoSimbolos()) {
            System.out.printf(formatarString.apply(simbolo));
        }
        System.out.println();
        for (String estado : this.getNovoConjuntoEstados().values()) {
            System.out.printf(formatarString.apply(estado));
            for (String simbolo : this.getConjuntoSimbolos()) {
                String key = estado + simbolo;
                if (this.getNovaFuncaoTransicao().containsKey(key)) {
                    System.out.printf(formatarString.apply(this.getNovaFuncaoTransicao().get(key)));
                } else {
                    System.out.printf(formatarString.apply("-"));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public String getTipoAutomato() {
        return tipoAutomato;
    }

    public HashSet<String> getConjuntoEstados() {
        return conjuntoEstados;
    }

    public HashSet<String> getConjuntoSimbolos() {
        return conjuntoSimbolos;
    }

    public HashSet<String> getConjuntoEstadosFinais() {
        return conjuntoEstadosFinais;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public HashMap<String, String> getFuncaoTransicao() {
        return funcaoTransicao;
    }

    public HashMap<String, String> getNovaFuncaoTransicao() {
        return novaFuncaoTransicao;
    }

    public HashMap<String, String> getNovoConjuntoEstados() {
        return novoConjuntoEstados;
    }

    public HashSet<String> getNovoConjuntoEstadosFinais() {
        return novoConjuntoEstadosFinais;
    }
}

class AFN {
    private String tipoAutomato;

    // É usado um LinkedHashMap para armazenar os estados e os simbolos, onde c/ estado ou simbolo é associado a um indice. Ex: estado -> indice
    // Esse indice é usado para acessar a função de transição, onde busca-se o estado ou símbolo e é retornado o índice. Esse
    // índice é atribuído conforme a entrada. Um LinkedHashMap lembra a ordem de inserção.
    private LinkedHashMap<String,Integer> conjuntoEstados;
    private LinkedHashMap<String,Integer> conjuntoSimbolos;
    private HashSet<String> conjuntoEstadosFinais;
    private String estadoInicial;
    private Conjunto[][] funcaoTransicao;

    public AFN (String arquivoEntradaDescricaoAFN) {
        tipoAutomato = null;
        conjuntoEstados = new LinkedHashMap<>();
        conjuntoSimbolos = new LinkedHashMap<>();
        conjuntoEstadosFinais = new HashSet<>();
        estadoInicial = null;
        funcaoTransicao = null;
        inicializar(arquivoEntradaDescricaoAFN);
    }

    private void inicializar (String arquivoEntradaDescricaoAFN) {
        try{
            Scanner sc = new Scanner (Paths.get(arquivoEntradaDescricaoAFN), StandardCharsets.UTF_8);
            int linha = 1;
            while (sc.hasNextLine()) {
                if (linha == 1) {
                    this.setTipoAutomato(sc.next());
                    sc.nextLine();
                }else if (linha == 2) {
                    int n = sc.nextInt();
                    for (int i = 0; i < n; i++) {
                        this.getConjuntoEstados().put(sc.next(), i);
                    }
                    sc.nextLine();
                } else if (linha == 3) {
                    int n = sc.nextInt();
                    for (int i = 0; i < n; i++) {
                        this.getConjuntoSimbolos().put(sc.next(), i);
                    }
                    sc.nextLine();
                } else if (linha == 4) {
                    this.setEstadoInicial(sc.next());
                    sc.nextLine();
                } else if (linha == 5) {
                    int n = sc.nextInt();
                    for (int i = 0; i < n; i++) {
                        this.getConjuntoEstadosFinais().add(sc.next());
                    }
                    sc.nextLine();
                } else {
                    // Cria-se uma matriz de tamanho conjuntoEstados.size() x conjuntoSimbolos.size() que armazena um elemento do tipo Conjunto
                    this.setFuncaoTransicao(new Conjunto[this.getConjuntoEstados().size()][this.getConjuntoSimbolos().size()]);
                    
                    
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        // Se acontecer da última linha ser em branco
                        if (!line.matches("^$")) {
                            String[] words = line.split(" ");
                            Integer l = this.getConjuntoEstados().get(words[0]);
                            Integer c = this.getConjuntoSimbolos().get(words[1]);
                            for (int i = 2; i < words.length; i++) {
                                if (words[i].compareTo("#") == 0) {
                                    break;
                                }
                                if (funcaoTransicao[l][c] == null) {
                                    funcaoTransicao[l][c] = new Conjunto();
                                }
                                funcaoTransicao[l][c].getConjunto().add(words[i]);
                            }
                        }
                    }
                }
                linha++;
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public AFD converterAFNparaAFD () {
        // Passo a referencia do afn para criar os atributos que são iguais
        AFD afd = new AFD (this);

        // δ({estados}, simbolo) = estados
        BiFunction<Conjunto, String, Conjunto> processaSimbolo = (Conjunto estadoAtual, String simbolo) -> {
            Conjunto novoEstadoDoAFD = new Conjunto();
            for (String estado : estadoAtual.getConjunto()) {
                Conjunto temp = funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];
                if (temp != null) {
                    novoEstadoDoAFD.getConjunto().addAll(temp.getConjunto());
                }
                
            }
            if (novoEstadoDoAFD.getConjunto().size() == 0) {
                return null;
            }
            return novoEstadoDoAFD;
        };

        // A fila auxilia a execução do algoritmo armazenando os estados que não foram processados
        Queue<Conjunto> fila = new ArrayDeque<>();
        // Para saber quem está na fila 
        HashSet<Conjunto> elementosDaFila = new HashSet<>();

        // Inicialmente é o <q0> (estado inicial)
        Conjunto estadoAtual = new Conjunto();

        estadoAtual.getConjunto().add(this.getEstadoInicial());

        // Adiciona o estado inicial na fila
        fila.add(estadoAtual);
        elementosDaFila.add(estadoAtual);
        
        while (fila.size() != 0) {
            
            // Remove da fila
            estadoAtual = fila.remove();
            elementosDaFila.remove(estadoAtual);

            // Adiciona no conjunto de estados do afd
            String estadoAtualString = estadoAtual.toString();
            afd.getConjuntoEstados().add(estadoAtualString);

            // Verifica se esse conjunto possui algum estado final
            for (String estado : estadoAtual.getConjunto()) {
                if (this.getConjuntoEstadosFinais().contains(estado)) {
                    afd.getConjuntoEstadosFinais().add(estadoAtualString);
                    break;
                }
            }

            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                
                // Obtem um novo estado
                Conjunto novo = processaSimbolo.apply(estadoAtual, simbolo);

                if (novo != null) {
                    String novoString = novo.toString();
                    // Se não estiver no conjunto de estados do afd e na fila
                    if (afd.getConjuntoEstados().contains(novoString) == false && elementosDaFila.contains(novo) == false) {
                        // Adiciono na fila para processamento futuro
                        fila.add(novo);
                    }


                    afd.getFuncaoTransicao().put((estadoAtualString + simbolo), novoString);
                } 
            }
        } 

        return afd;
    }

    public void imprimirAFN () {

        System.out.println();

        // Retorna todos os simbolos
        Supplier<String> simbolos = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoSimbolos().size();
            sb.append("{");
            for (String estado : this.getConjuntoSimbolos().keySet()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados
        Supplier<String> estados = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoEstados().size();
            sb.append("{");
            for (String estado : this.getConjuntoEstados().keySet()) {
                sb.append(estado);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        // Retorna todos os estados finais
        Supplier<String> estadosFinais = () -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int limite = this.getConjuntoEstadosFinais().size();
            sb.append("{");
            for (String estadoFinal : this.getConjuntoEstadosFinais()) {
                sb.append(estadoFinal);
                if (i != limite) {
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
            return sb.toString();
        };

        System.out.println(this.getTipoAutomato() + 
            " = (" + simbolos.get() + ", " + estados.get() + ", δN, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");
        

        //Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);
       
        System.out.printf(formatarString.apply("δN"));
        for (String simbolo : this.getConjuntoSimbolos().keySet()) {
            System.out.printf(formatarString.apply(simbolo));
        }
        System.out.println();
        for (String estado : this.getConjuntoEstados().keySet()) {
            System.out.printf(formatarString.apply(estado));
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                Conjunto o = funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];
                if (o == null) {
                    System.out.printf(formatarString.apply("-"));
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    Iterator<String> it = o.getConjunto().iterator();
                    while (it.hasNext()) {
                        sb.append(it.next());
                        if (it.hasNext()) {
                            sb.append(",");
                        }
                    }
                    sb.append("}");
                    System.out.printf(formatarString.apply(sb.toString()));
                }
                
            }
            System.out.println();
        }
        System.out.println();
    }

    public LinkedHashMap<String, Integer> getConjuntoSimbolos() {
        return conjuntoSimbolos;
    }

    public LinkedHashMap<String,Integer> getConjuntoEstados() {
        return conjuntoEstados;
    }

    public HashSet<String> getConjuntoEstadosFinais() {
        return conjuntoEstadosFinais;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public String getTipoAutomato() {
        return tipoAutomato;
    }

    public void setTipoAutomato(String tipoAutomato) {
        this.tipoAutomato = tipoAutomato;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setFuncaoTransicao(Conjunto[][] funcaoTransicao) {
        this.funcaoTransicao = funcaoTransicao;
    }
}

public class Converter {
    public static void main (String args[]) {
        String arquivoEntradaDescricaoAFN = args[0];
        String arquivoSaidaDescricaoAFD = args[1];

        AFN afn = new AFN (arquivoEntradaDescricaoAFN);

        afn.imprimirAFN();

        AFD afdGerado = afn.converterAFNparaAFD ();

        afdGerado.imprimirAFDNaoSimplificado();

        // Gera estados únicos
        afdGerado.simplicarAFD();

        afdGerado.imprimirAFDFinal();

        afdGerado.escreverArquivoSaida(arquivoSaidaDescricaoAFD);
    }
}
