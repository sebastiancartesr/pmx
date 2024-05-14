package org.example;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.PmxService.pmxCrossover;
import static org.example.PmxService.pmxFinal;
import static org.example.Utils.*;

public class Main {
    static final int  CANDIDATES_LENGTH = 25;
    public static void main(String[] args) {
        String rutaArchivo = "src/att532.json";
        //String rutaArchivo = "src/berlin52.json";
        JsonService jsonService = new JsonService();
        try {
            int[][] cityDistances = jsonService.readJsonMatrixFromFile(rutaArchivo);
            List<List<Integer>> nodesCandidates = computeCandidates(cityDistances,CANDIDATES_LENGTH);
            long startTime = System.currentTimeMillis();
            Poblacion poblacion = construirPoblacionConcurrente(2,1,cityDistances,nodesCandidates);
            long endTime = System.currentTimeMillis(); // Registro del tiempo final
            long elapsedTime = endTime - startTime;
            System.out.println("Tiempo transcurrido en generar la población: " + elapsedTime + " ms");
            pmxFinal(cityDistances, poblacion.poblacion.get(0), poblacion.poblacion.get(1));
        } catch (IOException e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}