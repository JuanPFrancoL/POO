import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Double[] notas = {4.5, 2.7, 3.9, 5.0, 1.8};
        List<Double> listaNotas = Arrays.asList(notas);
        Collections.sort(listaNotas);

        System.out.println("Orden ascendente: " + listaNotas);

    }
}



