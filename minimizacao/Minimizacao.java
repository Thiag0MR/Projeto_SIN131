package minimizacao;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class AFDMinimizado {
    private String tipoAutomato;
    private LinkedHashMap<String,Integer> conjuntoEstados;
    private LinkedHashMap<String,Integer> conjuntoSimbolos;
    private HashSet<String> conjuntoEstadosFinais;
    private String estadoInicial;
    private String[][] funcaoTransicao;
    
    public AFDMinimizado (AFD afd) {
        this.tipoAutomato = "AFD";
        this.conjuntoEstados = new LinkedHashMap<>(afd.getConjuntoEstados());
        this.conjuntoEstadosFinais = new HashSet<>(afd.getConjuntoEstadosFinais());
        this.conjuntoSimbolos = new LinkedHashMap<>(afd.getConjuntoSimbolos());
        this.estadoInicial = afd.getEstadoInicial();
    }

    public void imprimirAFD () {
        
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

        System.out.println(this.getTipoAutomato() + " Minimizado" +  
            " = (" + simbolos.get() + ", " + estados.get() + ", δM, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");


        // Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);

        System.out.printf(formatarString.apply("δM"));
        for (String simbolo : this.getConjuntoSimbolos().keySet()) {
            System.out.printf(formatarString.apply(simbolo));
        }
        System.out.println();
        for (String estado : this.getConjuntoEstados().keySet()) {
            System.out.printf(formatarString.apply(estado));
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                String proximoEstado = funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];
                if ( proximoEstado != null) {
                    System.out.printf(formatarString.apply(proximoEstado));
                } else {
                    System.out.printf(formatarString.apply("-"));
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void escreverArquivoSaida (String arquivoSaidaDescricaoAFDMinimo) {
        
        try {
            PrintWriter pw = new PrintWriter(arquivoSaidaDescricaoAFDMinimo, StandardCharsets.UTF_8);

            // Escreve o tipo do automato (AFD, AFN)
            pw.println(this.getTipoAutomato() + " # (Linha 1) Representação do formalismo");

            // Escreve os estados
            pw.print(this.getConjuntoEstados().size());

            for (String estado : this.getConjuntoEstados().keySet()) {
                pw.printf(" %s", estado);
            }
            pw.printf(" # (Linha 2) %d estados: ", this.getConjuntoEstados().size());
            int i = 0;
            for (String estado : this.getConjuntoEstados().keySet()) {
                pw.print(estado);
                if (i == (this.getConjuntoEstados().size() - 2)) {
                    pw.print(" e ");
                } else {
                    if (i != (this.getConjuntoEstados().size() - 1)) {
                        pw.print(", ");
                    }
                }
                i++;
            }
            pw.println();

            // Escreve os simbolos
            pw.print(this.getConjuntoSimbolos().size());
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                pw.printf(" %s", simbolo);
            }
            pw.printf(" # (Linha 3) %d símbolos: ", this.getConjuntoSimbolos().size());
            i = 0;
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
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
            pw.print(this.getConjuntoEstadosFinais().size());
            for (String estadoFinal : this.getConjuntoEstadosFinais()) {
                pw.printf(" %s", estadoFinal);
            }
            if (this.getConjuntoEstadosFinais().size() == 1) {
                pw.printf(" # (Linha 5) Possui %d estado final: o ", this.getConjuntoEstadosFinais().size());
            }else {
                pw.printf(" # (Linha 5) Possui %d estados finais: ", this.getConjuntoEstadosFinais().size());
            }
            i = 0;
            for (String estadoFinal : this.getConjuntoEstadosFinais()) {
                pw.print(estadoFinal);
                if (i == (this.getConjuntoEstadosFinais().size() - 2)) {
                    pw.print(" e ");
                } else {
                    if (i != (this.getConjuntoEstadosFinais().size() - 1)) {
                        pw.print(", ");
                    }
                }
                i++;
            }
            pw.println();

            // Escreve as transições
            i = 0;
            for (String estado : this.getConjuntoEstados().keySet()) {
                for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                    String proximoEstado = this.getFuncaoTransicao(this.getConjuntoEstados().get(estado), this.getConjuntoSimbolos().get(simbolo));

                    if (proximoEstado != null) {
                        if (i == 0) {
                            pw.printf("%s %s %s", estado, simbolo, proximoEstado);
                            pw.printf(" # (Linha 6 em diante) δ(%s, %s) = %s\n",estado, simbolo, proximoEstado);
                        } else {
                            pw.printf("%s %s %s", estado, simbolo, proximoEstado);
                            pw.printf(" # δ(%s, %s) = %s\n",estado, simbolo, proximoEstado);
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

    // Verifica se a partir de um estado é possível chegar a um estado final
    public boolean levaAUmEstadoFinal(String estadoAtual, HashSet<String> estadosJaVisitados, boolean b) {

        if (estadoAtual.equals("d")) {
            b = false;
            return b;
        } else if (this.getConjuntoEstadosFinais().contains(estadoAtual)) {
            b = true;
            return b;
        }

        if (estadosJaVisitados.contains(estadoAtual)) {
            return false;
        } else {
            estadosJaVisitados.add(estadoAtual);
        }


        for (String simbolo : this.getConjuntoSimbolos().keySet()) {
            String proximoEstado = this.getFuncaoTransicao(this.getConjuntoEstados().get(estadoAtual), this.getConjuntoSimbolos().get(simbolo));
            b = levaAUmEstadoFinal(proximoEstado, estadosJaVisitados, b);
            if (b == true) {
                break;
            }
        } 

        estadosJaVisitados.remove(estadoAtual);


        return b;
    }

    public String getTipoAutomato() {
        return tipoAutomato;
    }

    public LinkedHashMap<String, Integer> getConjuntoEstados() {
        return conjuntoEstados;
    }

    public LinkedHashMap<String, Integer> getConjuntoSimbolos() {
        return conjuntoSimbolos;
    }

    public HashSet<String> getConjuntoEstadosFinais() {
        return conjuntoEstadosFinais;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public void setFuncaoTransicao(String[][] funcaoTransicao) {
        this.funcaoTransicao = funcaoTransicao;
    }

    public void setFuncaoTransicao(int linha, int coluna, String s) {
        this.funcaoTransicao[linha][coluna] = s;
    }

    public String getFuncaoTransicao(int l, int c) {
        return funcaoTransicao[l][c];
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

}

class AFD {
    private String tipoAutomato;
    private LinkedHashMap<String,Integer> conjuntoEstados;
    private LinkedHashMap<String,Integer> conjuntoSimbolos;
    private HashSet<String> conjuntoEstadosFinais;
    private String estadoInicial;
    private String[][] funcaoTransicao;

    // Auxiliar o processo de minimização
    private boolean[][] tabelaDeEstados;
    private HashMap<String, LinkedList<Par>> lista;

    private class Par { 
        String u;
        String v;

        public Par (String u, String v) {
            this.u = u;
            this.v = v;
        }

        public String toString() {
            return v + u;
        }
    }
    

    public AFD (String arquivoEntradaDescricaoAFD) {
        conjuntoEstados = new LinkedHashMap<>();
        conjuntoSimbolos = new LinkedHashMap<>();
        conjuntoEstadosFinais = new HashSet<>();
        lista = new HashMap<>();
        inicializar(arquivoEntradaDescricaoAFD);
    }

    private void inicializar (String arquivoEntradaDescricaoAFD) {
        try{
            Scanner sc = new Scanner (Paths.get(arquivoEntradaDescricaoAFD), StandardCharsets.UTF_8);
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
                    // Cria-se uma matriz de tamanho conjuntoEstados.size() + 1 x conjuntoSimbolos.size() que armazena objetos
                    this.setFuncaoTransicao(new String[conjuntoEstados.size() + 1][conjuntoSimbolos.size()]);
                    
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        if (!line.matches("^$")) {
                            String[] words = line.split(" ");
                            Integer l = this.getConjuntoEstados().get(words[0]);
                            Integer c = this.getConjuntoSimbolos().get(words[1]);
                            funcaoTransicao[l][c] = words[2];
                        }
                    }
                }
                linha++;
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void imprimirAFD () {
        
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
            " = (" + simbolos.get() + ", " + estados.get() + ", δD, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");


        // Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);

        System.out.printf(formatarString.apply("δD"));
        for (String simbolo : this.getConjuntoSimbolos().keySet()) {
            System.out.printf(formatarString.apply(simbolo));
        }
        System.out.println();
        for (String estado : this.getConjuntoEstados().keySet()) {
            System.out.printf(formatarString.apply(estado));
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                String proximoEstado = funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];
                if ( proximoEstado != null) {
                    System.out.printf(formatarString.apply(proximoEstado));
                } else {
                    System.out.printf(formatarString.apply("-"));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public AFDMinimizado minimizar () {

        verificarPreRequisitos();
        
        // Novo conjunto de estados  onde o acesso aos estados é feito pelo índice 
        LinkedHashMap<Integer, String> conjuntoEstadosPeloIndice = new LinkedHashMap<>();
        for (String estado : this.getConjuntoEstados().keySet()) {
            conjuntoEstadosPeloIndice.put(this.getConjuntoEstados().get(estado), estado);
        }

        // Algumas funções auxiliares

        // Testa se os estados são pares do tipo {estado final, estado não-final}
        BiPredicate<String, String> eParFinaleNaoFinal = (s1, s2) -> {
            if (this.getConjuntoEstadosFinais().contains(s1)) {
                if (!this.getConjuntoEstadosFinais().contains(s2)) {
                    return true;
                }
            }else {
                if (this.getConjuntoEstadosFinais().contains(s2)) {
                    return true;
                }
            }
            return false;
        };

        BiFunction<String, String, String> δ = (estado, simbolo) -> {
            return funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];
        };

        BiPredicate<String, String> estaMarcado = (s1, s2) -> {
            return tabelaDeEstados[this.getConjuntoEstados().get(s1)][this.getConjuntoEstados().get(s2)];
        };


        // Passo 1 - Criar uma tabela de estados
        this.setTabelaDeEstados(new boolean[this.getConjuntoEstados().size()][this.getConjuntoEstados().size()]);

    
        // Passo 2 - Marcar os pares {final, não-final}
        int i = 0;
        while (i < this.getConjuntoEstados().size()) {
            int j = 0;
            while (i > j) {
                Par q = new Par(conjuntoEstadosPeloIndice.get(i), conjuntoEstadosPeloIndice.get(j));
               
                // Marcar os estados não equivalentes
                if (eParFinaleNaoFinal.test(q.u, q.v)) {
                    tabelaDeEstados[i][j] = true;
                }
                j++;
            }
            i++;
        }

        // Passo 3 - Verificar para as entradas não marcadas na tabela
        i = 0;
        while (i < this.getConjuntoEstados().size()) {
            int j = 0;
            while (i > j) {
                if (tabelaDeEstados[i][j] == false) {
                    Par q = new Par(conjuntoEstadosPeloIndice.get(i), conjuntoEstadosPeloIndice.get(j));

                    for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                        Par p = new Par(δ.apply(q.u, simbolo), δ.apply(q.v, simbolo));
                        

                        // É preciso garantir que o par {pu,pv} esteja dentro do range da tabela, para isso o índice do pu deve
                        // ser maior que o pv. Caso contrário, é preciso trocar ambos de lugar. O par {qu, qv} está correto pois
                        // ele foi obtido a partir do i e j

                        if (this.getConjuntoEstados().get(p.u) < this.getConjuntoEstados().get(p.v)) {
                            String aux = p.u;
                            p.u = p.v;
                            p.v = aux;
                        }
                        
                        // Se pu != pv e {pu,pv} não está marcado
                        if (!p.u.equals(p.v) && !estaMarcado.test(p.u, p.v)) {
                            if (!lista.containsKey(p.toString())) {
                                lista.put((p.toString()), new LinkedList<>());
                            }
                            // {qu,qv} é incluído na lista encabeçada por {pu,pv}
                            if (!lista.get(p.toString()).contains(q)) {
                                // Caso não exista o par, adiciona o mesmo na lista
                                lista.get(p.toString()).add(q);
                            }

                        // Se pu != pv e {pu,pv} está marcado
                        } else if (!p.u.equals(p.v) && estaMarcado.test(p.u, p.v)) {
                            
                            // Se {qu,qv} não é equivalente, marcar; ou seja, se {pu, pv} é um par do tipo {final, não-final}
                            // significa que {qu, qv} não é equivalente, caso contrário, se {pu, pv} for do tipo {final, final}
                            // ou {não-final, não-final} então {qu, qv} é equivalente

                            // Como {pu,pv} está marcado (entrou no loop), marca {qu,qv} ({qu,qv} não é equivalente)
                            tabelaDeEstados[this.getConjuntoEstados().get(q.u)][this.getConjuntoEstados().get(q.v)] = true;
                            
                            // Se {qu,qv} encabeça uma lista, marcar todos os pares da lista recursivamente
                            if (this.getLista().containsKey(q.toString())) {
                                marcarParesDaListaRecursivamente(q.toString(), new HashSet<>()); 
                            }
                        }
                    }
                }
                j++;
            }
            i++;
        }

        // Passo 4 - Unificação dos estados (pares não marcados são equivalentes)

        AFDMinimizado afdMinimizado = new AFDMinimizado(this);

        // Obtem os pares não marcados
        HashMap<String, HashSet<Par>> pares = new HashMap<>();
        pares.put("Finais", new HashSet<>());
        pares.put("NaoFinais", new HashSet<>());
        i = 0;
        while (i < this.getConjuntoEstados().size()) {
            int j = 0;
            while (i > j) {
                if (tabelaDeEstados[i][j] == false) {
                    Par q = new Par(conjuntoEstadosPeloIndice.get(i), conjuntoEstadosPeloIndice.get(j));

                    // q é um par de estados finais
                    if (this.getConjuntoEstadosFinais().contains(q.u) && this.getConjuntoEstadosFinais().contains(q.v)) {
                        pares.get("Finais").add(q);
                    } else {
                        // q é um par de estados não finais
                        pares.get("NaoFinais").add(q);
                    }
                }
                j++;
            }
            i++;
        }

        Function<HashSet<String>, String> getEstadoUnificado = (set) -> {
            String aux = set.toString().substring(1, set.toString().length() - 1);
            StringBuilder sb = new StringBuilder();
            
            for (String s : aux.split(",")) {
                sb.append(s);
            }

            return sb.toString().replaceAll("\\s+", "");

        };

        // Os pares de estados finais equivalentes geram um único estado final
        HashSet<String> parFinalUnificadoSet = new HashSet<>();
        for (Par parFinal : pares.get("Finais")) {
            parFinalUnificadoSet.add(parFinal.u);
            parFinalUnificadoSet.add(parFinal.v);
            
            // Remove os pares que foram unificados do conjunto de estados do afd minimizado
            afdMinimizado.getConjuntoEstados().remove(parFinal.u);
            afdMinimizado.getConjuntoEstados().remove(parFinal.v);

            // Remove os pares que foram unificados do conjunto de estados finais do afd minimizado
            afdMinimizado.getConjuntoEstadosFinais().remove(parFinal.u);
            afdMinimizado.getConjuntoEstadosFinais().remove(parFinal.v);

        }
        // Se existir algum par de estados finais 
        String parFinalUnificado = null;
        if (parFinalUnificadoSet.size() > 0) {
            parFinalUnificado = getEstadoUnificado.apply(parFinalUnificadoSet);
            // Adiciona o par no conjunto de estados
            afdMinimizado.getConjuntoEstados().put(parFinalUnificado, 0);
            afdMinimizado.getConjuntoEstadosFinais().add(parFinalUnificado);
        }
        

        
        // Os pares de estados não finais equivalentes geram um único estado não final
        HashSet<String> parNaoFinalUnificadoSet = new HashSet<>();
        for (Par parNaoFinal : pares.get("NaoFinais")) {
            parNaoFinalUnificadoSet.add(parNaoFinal.u);
            parNaoFinalUnificadoSet.add(parNaoFinal.v);

            // Remove os pares que foram unificados do conjunto de estados do afd minimizado
            afdMinimizado.getConjuntoEstados().remove(parNaoFinal.u);
            afdMinimizado.getConjuntoEstados().remove(parNaoFinal.v);
        }
        // Se existir algum par de estados não finais
        String parNaoFinalUnificado = null;
        if (parNaoFinalUnificadoSet.size() > 0) {
            parNaoFinalUnificado = getEstadoUnificado.apply(parNaoFinalUnificadoSet);
            afdMinimizado.getConjuntoEstados().put(parNaoFinalUnificado, 0);
        }
        

        // Atualiza os índices do conjunto de estados do afd minimizado
        i = 0;
        for (String estado : afdMinimizado.getConjuntoEstados().keySet()) {
            afdMinimizado.getConjuntoEstados().replace(estado, i++);
        }

        // Estrutura para auxiliar na criação da função de transição do afdMinimizado
        // Armazena o estado antigo, simbolo, e o proximo estado (talvez unificado) no afd minimizado
        HashMap<String,HashMap<String,String>> mapeamentoEstadoAntigoSimboloProximoEstado = new HashMap<>();
        for (String estado : this.getConjuntoEstados().keySet()) {
            mapeamentoEstadoAntigoSimboloProximoEstado.put(estado, new HashMap<>());
        }

        for (String estado : this.getConjuntoEstados().keySet()) {
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                String proximoEstado = funcaoTransicao[this.getConjuntoEstados().get(estado)][this.getConjuntoSimbolos().get(simbolo)];

                if (parFinalUnificadoSet.contains(proximoEstado)) {
                    mapeamentoEstadoAntigoSimboloProximoEstado.get(estado).put(simbolo, parFinalUnificado);
                } else if (parNaoFinalUnificadoSet.contains(proximoEstado)) {
                    mapeamentoEstadoAntigoSimboloProximoEstado.get(estado).put(simbolo, parNaoFinalUnificado);
                } else {
                    mapeamentoEstadoAntigoSimboloProximoEstado.get(estado).put(simbolo, proximoEstado);
                }
            }
        }

        HashMap<String, String> mapeamentoEstadoAntigoNovoEstado = new HashMap<>();
        for (String estado : this.getConjuntoEstados().keySet()) {
            if (parFinalUnificadoSet.contains(estado)) {
                mapeamentoEstadoAntigoNovoEstado.put(estado, parFinalUnificado);
            } else if (parNaoFinalUnificadoSet.contains(estado)) {
                mapeamentoEstadoAntigoNovoEstado.put(estado, parNaoFinalUnificado);
            } else {
                mapeamentoEstadoAntigoNovoEstado.put(estado, estado);
            }
        }
        

        // Cria a nova função de transição
        afdMinimizado.setFuncaoTransicao(new String[afdMinimizado.getConjuntoEstados().size()][afdMinimizado.getConjuntoSimbolos().size()]);

        for (String estado : this.getConjuntoEstados().keySet()) {
            for (String simbolo : this.getConjuntoSimbolos().keySet()) {
                
                String novoEstado = mapeamentoEstadoAntigoNovoEstado.get(estado);
                String novoProximoEstado = mapeamentoEstadoAntigoSimboloProximoEstado.get(estado).get(simbolo);

                int indiceLinha = afdMinimizado.getConjuntoEstados().get(novoEstado);
                int indiceColuna = afdMinimizado.getConjuntoSimbolos().get(simbolo);

                afdMinimizado.setFuncaoTransicao(indiceLinha, indiceColuna, novoProximoEstado);
                
            }
        }

        // Passo 5 - Exclusão dos estados inúteis

        HashSet<String> estadosInuteis = new HashSet<>();

        Predicate<String> eEstadoInutil = (estado) -> {
            // Não é final
            if (!afdMinimizado.getConjuntoEstadosFinais().contains(estado)) {
                // Verifica se existe transições do estado atual até um estado final
                if (afdMinimizado.levaAUmEstadoFinal(estado, new HashSet<>(), false)) {
                    // Se leva a um estado final então o estado é não-inútil
                    return false;
                }else {
                    // Se não leva a um estado final então o estado é inútil
                    return true;
                }
            }

            // O estado é final 
            return false;
        };

        // Identifica os estados inúteis para remover as transições que levam a esses estados na função de transição
        for (String estado : afdMinimizado.getConjuntoEstados().keySet()) {
            if (eEstadoInutil.test(estado)) {
                estadosInuteis.add(estado);
            }
        }

        // Remove os estados inúteis
        afdMinimizado.getConjuntoEstados().keySet().removeIf(s -> estadosInuteis.contains(s));

        // Exclui as transições que levam a estados inúteis
        for (String estado : afdMinimizado.getConjuntoEstados().keySet()) {
            for (String simbolo : afdMinimizado.getConjuntoSimbolos().keySet()) {
                int l = afdMinimizado.getConjuntoEstados().get(estado);
                int c = afdMinimizado.getConjuntoSimbolos().get(simbolo);
                if (estadosInuteis.contains(afdMinimizado.getFuncaoTransicao(l, c))) {
                    afdMinimizado.setFuncaoTransicao(l, c, null);
                }
            }
        }

        return afdMinimizado;

    }

    // Usada na função Minimizar
    private void marcarParesDaListaRecursivamente(String cabecaLista, HashSet<String> cabecaListaJaVisitada) {

        if (cabecaListaJaVisitada.contains(cabecaLista)) {
            return;
        } else {
            cabecaListaJaVisitada.add(cabecaLista);
        }

        // Percorre a lista encabeçada pela 'cabecaLista'
        for (Par par : this.getLista().get(cabecaLista)) {
            // Se 'par' encabeça um lista, marcar todos os pares dessa lista
            if (this.getLista().containsKey(par.toString())) {
                marcarParesDaListaRecursivamente(par.toString(), cabecaListaJaVisitada);
            }
            tabelaDeEstados[this.getConjuntoEstados().get(par.u)][this.getConjuntoEstados().get(par.v)] = true;
        }
        
        return;
    }
    
    private void verificarPreRequisitos() {
        // Pré-requisitos: 

        // 1 - O autômato deve ser um AFD

        // Espera-se que o autômato seja um AFD
        // if (!this.getTipoAutomato().equals("AFD")) {
        //     return false;
        // }

        // 2 - Todos os estados são alcançáveis a partir do estado inicial

        HashSet<String> estados = new HashSet<>(this.getConjuntoEstados().keySet());
        HashSet<String> estadosJaVisitados = new HashSet<>();
        verificarEstadosInacessiveis(estados, estadosJaVisitados, this.getEstadoInicial());
        if (estados.size() > 0) {
            // Remove os estados inacessíveis caso existam
            this.getConjuntoEstados().keySet().removeIf((s) -> estados.contains(s));
            // Atualiza os índices do conjunto de estados
            int i = 0;
            for (String estado : this.getConjuntoEstados().keySet()) {
                this.getConjuntoEstados().replace(estado, i++);
            }
        }
        

        // 3 - A função programa deve ser total

        // Cria a função programa total se necessário (adiciona um estado 'd' onde todas transições não previstas
        // ligam a esse estado)
        boolean acrescentarEstado = false;
        for (int i = 0; i < this.getConjuntoEstados().size(); i++) {
            for (int j = 0; j < this.getConjuntoSimbolos().size(); j++) {
                if (funcaoTransicao[i][j] == null) {
                    funcaoTransicao[i][j] = "d";
                    acrescentarEstado = true;
                }
            }
        }
        if (acrescentarEstado) {
            int tam = this.getConjuntoEstados().size();
            this.getConjuntoEstados().put("d", tam);
            for (int i = 0; i < this.getConjuntoSimbolos().size(); i++) {
                funcaoTransicao[this.getConjuntoEstados().size() - 1][i] = "d";
            }
        }

        // return true;
    }

    private void verificarEstadosInacessiveis(HashSet<String> estados, HashSet<String> estadosJaVisitados, String estadoAtual) {

        estados.remove(estadoAtual);

        if (estadosJaVisitados.contains(estadoAtual)) {
            return;
        } else {
            estadosJaVisitados.add(estadoAtual);
        }

        for (String simbolo : this.getConjuntoSimbolos().keySet()) {
            String proximoEstado = funcaoTransicao[this.getConjuntoEstados().get(estadoAtual)][this.getConjuntoSimbolos().get(simbolo)];
            
            if (proximoEstado != null) {
                verificarEstadosInacessiveis(estados, estadosJaVisitados, proximoEstado);
            }
        }

        estadosJaVisitados.remove(estadoAtual);

        return;
    }

    public String getTipoAutomato() {
        return tipoAutomato;
    }

    public void setTipoAutomato(String tipoAutomato) {
        this.tipoAutomato = tipoAutomato;
    }

    public HashMap<String, Integer> getConjuntoEstados() {
        return conjuntoEstados;
    }

    public HashMap<String, Integer> getConjuntoSimbolos() {
        return conjuntoSimbolos;
    }

    public HashSet<String> getConjuntoEstadosFinais() {
        return conjuntoEstadosFinais;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setFuncaoTransicao(String[][] funcaoTransicao) {
        this.funcaoTransicao = funcaoTransicao;
    }

    public void setTabelaDeEstados(boolean[][] tabelaDeEstados) {
        this.tabelaDeEstados = tabelaDeEstados;
    }

    public HashMap<String, LinkedList<Par>> getLista() {
        return lista;
    }
}

public class Minimizacao {

    public static void main (String args[]) {
        String arquivoEntradaDescricaoAFD = args[0];
        String arquivoSaidaDescricaoAFDMinimo = args[1];

        AFD afd = new AFD (arquivoEntradaDescricaoAFD);

        afd.imprimirAFD();

        AFDMinimizado afdMinimizado = afd.minimizar();

        afdMinimizado.imprimirAFD();

        afdMinimizado.escreverArquivoSaida(arquivoSaidaDescricaoAFDMinimo);
    }
    
}
