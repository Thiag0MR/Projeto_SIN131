# Projeto_SIN131

###Programa 1: simulacao/Simulacao.java
- Simula a computação de um AFD - Autômato Finito Determinístico.
- Recebe três parâmetros:
	- O arquivo contendo a descrição do AFD
	- O arquivo contendo as palavras que serão computadas
	- O arquivo que será gerado contendo o resultado da computação
- Observações:
	- A palavra vazia é representada pelo símbolo "_"
	- O autômato deve ser escrito seguindo a estrutura definida no 		arquivo de exemplo.

**Executando o programa:**
```
java -jar simulacao.jar afd.txt palavras.txt saida.txt
```

###Programa 2: conversao/Converter.java
- Converte um AFN - Autômato Finito Não-Determinístico para um AFD.
- Recebe dois parâmetros:
	- O arquivo contendo a descrição do AFN
	- O arquivo que será gerado contendo a descrição do AFD

**Executando o programa:**
```
java -jar converter.jar afn1.txt afdGerado.txt
```

###Programa 3: minimizacao/Minimizacao.java
- Minimiza um AFD e um AFD Mínimo.
- Recebe dois parâmetros:
	- O arquivo contendo a descrição do AFD que será minimizado
	- O arquivo que será gerado contendo a descrição do AFD Mínimo

**Executando o programa:**
```
java -jar minimizacao.jar afd1.txt afdMinimo.txt
```