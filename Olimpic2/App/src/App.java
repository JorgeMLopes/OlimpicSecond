import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

interface IOrdenator<T> {

    public T[] ordenar();

    public void setComp(Comparator<T> comparador);

    public int getComp();

    public int getMovimentacao();

    public double getTempOrd();
}

class Sorter<T> implements IOrdenator<T> {
    private Comparator<T> comparador;
    private double tempoOrdenacao = 0;
    private int qtdMovimentacoes = 0;
    private int qtdComparacoes = 0;
    private final T[] vetor;

    Sorter(T[] vetor) {
        this.vetor = vetor;
    }

    private void swap(int i, int min) {
        T temp = vetor[i];
        vetor[i] = vetor[min];
        vetor[min] = temp;
        qtdMovimentacoes++;
    }

    @Override
    public T[] ordenar() {
        long inicio = System.currentTimeMillis();
        int min;
        for (int i = 0; i < vetor.length - 1; i++) {
            min = i;
            for (int j = i + 1; j < vetor.length; j++) {
                qtdComparacoes++;
                if (comparador.compare(vetor[min], vetor[j]) < 0) {
                    min = j;
                }
            }
            swap(i, min);
        }
        long fim = System.currentTimeMillis();
        tempoOrdenacao = (fim - inicio);
        return vetor;
    }

    @Override
    public void setComp(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    @Override
    public int getComp() {
        return qtdComparacoes;
    }

    @Override
    public int getMovimentacao() {
        return qtdMovimentacoes;
    }

    @Override
    public double getTempOrd() {
        return tempoOrdenacao;
    }

}

enum TipoMedal {
    OURO,
    PRATA,
    BRONZE
}

class Medalhista implements Comparable<Medalhista> {

    private static final int MAX_MEDALHAS = 8;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String country;
    private Medalha[] medals;
    private int medalCount;

    public Medalhista(String nome, String genero, LocalDate nascimento, String pais) {
        name = nome;
        gender = genero;
        birthDate = nascimento;
        country = pais;
        medalCount = 0;
        medals = new Medalha[MAX_MEDALHAS];
    }

    public int insertMedal(Medalha medalha) {
        if (medalCount >= MAX_MEDALHAS) {
            return medalCount;
        }

        Medalha[] nMed = new Medalha[MAX_MEDALHAS];
        int lastMedalCount = medalCount;
        for (int i = 0; i < lastMedalCount; i++) {
            nMed[i] = medals[i];
        }
        medalCount++;
        nMed[lastMedalCount] = medalha;
        medals = nMed;
        return medalCount;
    }

    public int medalT() {
        return medalCount;
    }

    private int medalTotal(TipoMedal tipo) {
        return (int) Arrays.stream(medals).filter(medalha -> medalha != null && medalha.getTipo().equals(tipo)).count();
    }

    public String relatoMedals(TipoMedal tipo) {
        StringBuilder strBuilder = new StringBuilder("");
        for (Medalha medalha : medals) {
            if (medalha == null) {
                continue;
            }
            if (medalha.getTipo().equals(tipo)) {
                strBuilder.append(medalha.toString());
                strBuilder.append("\n");
            }
        }
        if (strBuilder.isEmpty()) {
            return "Nao possui medalha de " + tipo.toString() + "\n";
        }
        return strBuilder.toString();
    }

    public String getPais() {
        return country;
    }

    public LocalDate getNascimento() {
        return LocalDate.from(birthDate);
    }

    public String toString() {
        String dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(birthDate); // DD/MM/AAAA
        return name + ", " + gender + ". Nascimento: " + dataFormatada + ". Pais: " + country;
    }

    @Override
    public int compareTo(Medalhista o) {
        if (o == null)
            return -1;

        int comparacao = o.birthDate.compareTo(this.birthDate);

        if (comparacao != 0)
            return comparacao;

        comparacao = o.name.toUpperCase().compareTo(this.name.toUpperCase());
        return comparacao;
    }

    public void printM() {
        TipoMedal[] tipoMedalhas = { TipoMedal.OURO, TipoMedal.PRATA, TipoMedal.BRONZE };
        for (TipoMedal tipo : tipoMedalhas) {
            int totalDeMedalhas = medalTotal(tipo);
            if (totalDeMedalhas > 0)
                System.out.println("Quantidade de medalhas de " + tipo.name().toLowerCase() + ": " + totalDeMedalhas);
        }
    }
}

class Medalha {
    private String event;
    private LocalDate medalDate;
    private TipoMedal metalType;
    private String discipline;
    

    public Medalha(TipoMedal tipo, LocalDate data, String disciplina, String evento) {
        metalType = tipo;
        medalDate = data;
        discipline = disciplina;
        event = evento;
    }

    public TipoMedal getTipo() {
        return metalType;
    }

    public String toString() {
        String dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(medalDate); // DD/MM/AAAA
        return metalType.toString() + " - " + discipline + " - " + event + " - " + dataFormatada;
    }
}

public class App {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> content = FileReader.read("./tmp/medallists.csv");
        content.remove(0);
        Map<String, Medalhista> dataMap = getData(content);
        int numeroDeAtletas = scanner.nextInt();
        scanner.nextLine();
        Medalhista[] medalhistas = new Medalhista[numeroDeAtletas];
        for (int i = 0; i < numeroDeAtletas; i++) {
            String atleta = scanner.nextLine();
            Medalhista medalhista = dataMap.get(atleta);
            medalhistas[i] = medalhista;
        }
        Sorter<Medalhista> ordenador = new Sorter<Medalhista>(medalhistas);
        ordenador.setComp(Medalhista::compareTo);
        medalhistas = ordenador.ordenar();

        for (Medalhista medalhista : medalhistas) {
            System.out.println(medalhista.toString());
            System.out.println();
        }
        try {
            FileWriter writer = new FileWriter("826631_selecao.txt");
            writer.write("826631\t" + ordenador.getTempOrd() + "ms\t" + ordenador.getComp()
                    + "\t" + ordenador.getMovimentacao());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    private static Map<String, Medalhista> getData(List<String> rawContent) {
        Map<String, Medalhista> map = new HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String line : rawContent) {
            String[] lineData = line.split(",");
            String name = lineData[0];
            TipoMedal medalType = TipoMedal.valueOf(lineData[1]);
            LocalDate medalDate = LocalDate.parse(lineData[2], dateFormatter);
            String gender = lineData[3];
            LocalDate birthDate = LocalDate.parse(lineData[4], dateFormatter);
            String country = lineData[5];
            String discipline = lineData[6];
            String event = lineData[7];
            Medalhista medalhista;
            if (map.containsKey(name)) {
                medalhista = map.get(name);
            } else {
                medalhista = new Medalhista(name, gender, birthDate, country);
                map.put(name, medalhista);
            }
            Medalha medalha = new Medalha(medalType, medalDate, discipline, event);
            medalhista.insertMedal(medalha);
        }
        return map;
    }
}

class FileReader {

    public static List<String> read(String pathname) {
        List<String> content = new ArrayList<>();
        File file = new File(pathname);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possível ler o arquivo");
            e.printStackTrace();
        }

        return content;
    }
}
