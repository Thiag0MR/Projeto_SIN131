package simulacao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

class AFD {
    private String tipoAutomato;
    // O estado é armazenado como uma String. A cada estado é atribuído um inteiro (índice). Isso permite o estado ser qualquer
    // coisa (q0, q1, q2) e não somente números (0, 1, 2);
    private LinkedHashMap<String,Integer> conjuntoEstados;
    private LinkedHashMap<String,Integer> conjuntoSimbolos;
    private HashSet<String> conjuntoEstadosFinais;
    private String estadoInicial;
    private String[][] funcaoTransicao;

    public AFD (String arquivoDescricaoAFD) {
        tipoAutomato = null;
        conjuntoEstados = new LinkedHashMap<>();
        conjuntoSimbolos = new LinkedHashMap<>();
        conjuntoEstadosFinais = new HashSet<>();
        estadoInicial = null;
        funcaoTransicao = null;
        inicializar(arquivoDescricaoAFD);
    }

    private void inicializar (String arquivoDescricaoAFD) {
        try{
            Scanner sc = new Scanner (Paths.get(arquivoDescricaoAFD), StandardCharsets.UTF_8);
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
                    this.setFuncaoTransicao(new String[this.getConjuntoEstados().size()][this.getConjuntoSimbolos().size()]);
                    
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        // Se acontecer da última linha ser em branco
                        if (!line.matches("^$")) {
                            String[] words = line.split(" ");
                            Integer l = this.getConjuntoEstados().get(words[0]);
                            Integer c = this.getConjuntoSimbolos().get(words[1]);
                            this.funcaoTransicao[l][c] = words[2];
                        }
                    }
                }
                linha++;
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void computar (String arquivoPalavras, String arquivoSaida) {

        Function<String, String> ACEITA = palavra -> String.format(palavra + " aceita\n");
        Function<String, String> REJEITA = palavra -> String.format(palavra + " rejeita\n");

        try {
            Scanner sc = new Scanner (Paths.get(arquivoPalavras), StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder(); 
            while (sc.hasNextLine()) {
                String palavra  = sc.next();
                sc.nextLine();
                String estadoAtual = this.getEstadoInicial();
                for (int i = 0; i < palavra.length(); i++) {
                    String simboloLido = String.valueOf(palavra.charAt(i));
                    if (simboloLido.equals("_")) {
                        break;
                    }
                    String proximoEstado = funcaoTransicao[this.getConjuntoEstados().get(estadoAtual)][this.getConjuntoSimbolos().get(simboloLido)];
                    if (proximoEstado != null) {
                        estadoAtual = proximoEstado;
                    }else {
                        estadoAtual = null;
                        break;
                    }
                }
                if (this.getConjuntoEstadosFinais().contains(estadoAtual)) {
                    builder.append(ACEITA.apply(palavra));
                } else {
                    builder.append(REJEITA.apply(palavra));
                }
            }
            Files.write(Paths.get(arquivoSaida), builder.toString().getBytes(StandardCharsets.UTF_8));
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
            " = (" + simbolos.get() + ", " + estados.get() + ", δ, " + this.getEstadoInicial() + 
            ", " + estadosFinais.get() + ")");


        // Função de transição

        Function<String, String> formatarString = s -> String.format("%-10s", s);

        System.out.printf(formatarString.apply("δ"));
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


    public String getTipoAutomato() {
        return tipoAutomato;
    }

    public void setTipoAutomato(String tipoAutomato) {
        this.tipoAutomato = tipoAutomato;
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

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setFuncaoTransicao(String[][] funcaoTransicao) {
        this.funcaoTransicao = funcaoTransicao;
    }

}

public class Simulacao {
    public static void main(String[] args) {
        String arquivoDescricaoAFD = args[0];
        String arquivoPalavras = args[1];
        String arquivoSaida = args[2];

        AFD afd = new AFD(arquivoDescricaoAFD);

        afd.imprimirAFD();

        afd.computar(arquivoPalavras, arquivoSaida);

    }
}
