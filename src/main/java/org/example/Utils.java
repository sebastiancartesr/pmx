package org.example;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.example.PmxService.*;

public class Utils {
    static final int  CANDIDATES_LENGTH = 25;
    static final int  ITERACIONES = 100;
    static final int CANTIDAD_POBLACION = 100;
    public static List<List<Integer>> computeCandidates(int[][] cityDistances, int candidatesLength) {
        int n = cityDistances.length;
        List<List<Integer>> nodesCandidates = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            List<NodeCandidates> cola = new ArrayList<>();

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    cola.add(new NodeCandidates(cityDistances[i][j],j));
                }
            }

            // Ordenar la lista 'cola' basada en la distancia (coste)
            Collections.sort(cola, Comparator.comparingInt(a -> a.coste));

            List<Integer> candidates = new ArrayList<>();
            for (int k = 0; k < candidatesLength && k < cola.size(); k++) {
                candidates.add(cola.get(k).indice);
            }

            nodesCandidates.add(candidates);
        }

        return nodesCandidates;
    }
    public static List<Integer> generateRandomRoute(int cityCount, boolean random) {
        List<Integer> route = new ArrayList<>();

        if (random) {
            // Agregar ciudades del 1 al cityCount-1 (excluyendo el 0)
            for (int i = 1; i < cityCount; i++) {
                route.add(i);
            }

            // Barajar la lista de ciudades (después de la ciudad 0)
            Collections.shuffle(route, new Random());

            // Insertar la ciudad 0 al inicio de la ruta
            route.add(0, 0); // Añade 0 en la posición 0 (inicio de la ruta)
        } else {
            // Agregar ciudades del 0 al cityCount-1 (incluyendo todas las ciudades)
            for (int i = 0; i < cityCount; i++) {
                route.add(i);
            }
        }

        return route;
    }
    public static Nodo[] generateNodes(List<Integer> routeArray) {
        Nodo[] nodesArray = new Nodo[routeArray.size()];
        Nodo nextNode = null;

        for (int i = 0; i < routeArray.size() - 1; i++) {
            int currentNodeId = routeArray.get(i);
            Nodo currentNode = nodesArray[currentNodeId];

            if (currentNode == null) {
                currentNode = new Nodo(currentNodeId, i, null, null);
                nodesArray[currentNodeId] = currentNode;
            }

            int nextNodeId = routeArray.get(i + 1);
            nextNode = new Nodo(nextNodeId, i + 1, currentNode, null);
            currentNode.setSiguiente(nextNode);
            nodesArray[nextNodeId] = nextNode;
        }

        // Conectar el último nodo con el primero
        if (nextNode != null && nodesArray[0] != null) {
            nextNode.setSiguiente(nodesArray[0]);
            nodesArray[0].setAnterior(nextNode);
        }

        return nodesArray;
    }
    public static Nodo[] crearSolucion(int cityCount){
        List<Integer> randomRoute = generateRandomRoute(cityCount,true);
        return generateNodes(randomRoute);
    }
    static class NodeCandidates {
        int coste;
        int indice;

        NodeCandidates(int coste, int indice) {
            this.coste = coste;
            this.indice = indice;
        }
    }
    public static class NodeMejor {
        Nodo[] routeNodes;
        int routeCost;

        NodeMejor(Nodo[] routeNodes, int routeCost) {
            this.routeNodes = routeNodes;
            this.routeCost = routeCost;
        }
    }
    public static class Poblacion {
        ArrayList<Nodo[]> poblacion;
        int mayor;
        int menor;
        double desviacion;
        double promedio;
        Poblacion(){
            this.poblacion = new ArrayList<>();
            this.mayor = 0;
            this.menor = Integer.MAX_VALUE;
            this.desviacion= 0;
            this.promedio = 0;
        }
    }
    public static class CandidateResult {
        private Nodo t2;
        private Nodo t3;
        private Nodo t4;
        private Nodo t5;
        private int revenue;

        public CandidateResult(Nodo t2, Nodo t3, Nodo t4, Nodo t5, int revenue) {
            this.t2 = t2;
            this.t3 = t3;
            this.t4 = t4;
            this.t5 = t5;
            this.revenue = revenue;
        }

        public CandidateResult(Nodo t2, Nodo t3, int revenue) {
            this.t2 = t2;
            this.t3 = t3;
            this.revenue = revenue;
        }

        // Getters para los nodos y la ganancia
        public Nodo getT2() {
            return t2;
        }

        public Nodo getT3() {
            return t3;
        }

        public Nodo getT4() {
            return t4;
        }

        public Nodo getT5() {
            return t5;
        }

        public int getRevenue() {
            return revenue;
        }
    }
    public static int calculateTotalDistance(Nodo[] nodes, int[][] cityDistances) {
        int totalDistance = 0;
        Nodo currentNode = nodes[0]; // El primer nodo (inicio de la ruta)

        do {
            Nodo nextNode = currentNode.getSiguiente();
            totalDistance += cityDistances[currentNode.getId()][nextNode.getId()];
            currentNode = nextNode;
        } while (currentNode != nodes[0]); // Volver al primer nodo para cerrar el ciclo

        return totalDistance;
    }
    public static NodeMejor findOptimalSolution(Nodo[] routeNodes, NodeMejor mejor, List<List<Integer>> nodesCandidates,int[][] cityDistances){
        int routeCost = calculateTotalDistance(routeNodes,cityDistances);
        Nodo t0;
        Nodo t1;
        int cont = 0;
        while (cont < cityDistances.length){
            t0 = getRandomNode(routeNodes);
            t1 = t0.getSiguiente();
            if(mejor != null){
                if(mejor.routeNodes[t0.getId()].getSiguiente().getId() == t1.getId() ||
                        mejor.routeNodes[t0.getId()].getAnterior().getId() == t1.getId()){
                    cont++;
                    continue;
                }
            }
            CandidateResult moveCandidates = getCandidates(routeNodes,t0,t1,nodesCandidates,cityDistances);
            if(moveCandidates != null){
                if(moveCandidates.t4 == null){
                    makeMove(t0, t1, moveCandidates.t2, moveCandidates.t3, routeNodes);
                }else{
                    makeMove(t0, t1, moveCandidates.t2, moveCandidates.t3, routeNodes);
                    makeMove(t0, moveCandidates.t3, moveCandidates.t4, moveCandidates.t5, routeNodes);
                }
                routeCost = routeCost - moveCandidates.revenue;
                cont = 0;
            }else{
                cont++;
            }
        }
        return new NodeMejor(routeNodes,routeCost);
    }
    public static Nodo getRandomNode(Nodo[] nodes){
        int randomIndex = getRandomIndex(nodes.length);
        return nodes[randomIndex];
    }
    public static int getRandomIndex(int maxSize) {
        Random random = new Random();
        return random.nextInt(maxSize);
    }
    public static int calculateNodesCost(Nodo nodeA, Nodo nodeB, int[][] cityDistances) {
        return cityDistances[nodeA.getId()][nodeB.getId()];
    }
    public static void move(Nodo t0, Nodo t1, Nodo t2, Nodo t3, int routeSize) {
        Nodo actualNodeMove = t1;
        int position = t3.getPosicion();
        while (actualNodeMove != t2) {
            actualNodeMove.setPosicion(position);
            position--;

            if (position < 0) {
                position = routeSize - 1;
            }
            Nodo aux = actualNodeMove.getSiguiente();
            actualNodeMove.setSiguiente(actualNodeMove.getAnterior());
            actualNodeMove.setAnterior(aux);
            actualNodeMove = actualNodeMove.getAnterior();
        }
        t0.setSiguiente(t3);
        t3.setAnterior(t0);
        t1.setSiguiente(t2);
        t2.setAnterior(t1);
    }
    public static void makeMove(Nodo t0, Nodo t1, Nodo t2, Nodo t3, Nodo[] routeNodes) {
        int routeSize = routeNodes.length;
        int nodesQtyT3t1 = t3.getPosicion() - t1.getPosicion();
        if (nodesQtyT3t1 < 0) {
            nodesQtyT3t1 += routeSize;
        }
        int nodesQtyT0t2 = t0.getPosicion() - t2.getPosicion();
        if (nodesQtyT0t2 < 0) {
            nodesQtyT0t2 += routeSize;
        }
        if (t0.getSiguiente() == t1) {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                move(t0, t1, t2, t3, routeSize);
            } else {
                move(t3, t2, t1, t0, routeSize);
            }
        } else {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                move(t1, t0, t3, t2, routeSize);
            } else {
                move(t2, t3, t0, t1, routeSize);
            }
        }
    }
    public static boolean between(Nodo minor, Nodo mayor, Nodo between) {
        int betweenPos = between.getPosicion();
        int minorPos = minor.getPosicion();
        int mayorPos = mayor.getPosicion();
        if (minorPos <= betweenPos && betweenPos <= mayorPos) {
            return true;
        }
        if (mayorPos < minorPos) {
            if (minorPos <= betweenPos || betweenPos <= mayorPos) {
                return true;
            }
        }
        return false;
    }
    public static CandidateResult getCandidates2(
            Nodo[] routeNodes, Nodo t0, Nodo t1, List<List<Integer>> nodesCandidates, int[][] cityDistances) {
        int t0t1ActualCost = calculateNodesCost(t0, t1, cityDistances);
        for (Integer t2CandidateID : nodesCandidates.get(t1.getId())) {
            Nodo t2Candidate = routeNodes[t2CandidateID];
            if (t2Candidate == t1 || t2Candidate == t1.getSiguiente() || t2Candidate == t1.getAnterior()) {
                continue;
            }
            int t1t2NewCost = calculateNodesCost(t1, t2Candidate, cityDistances);
            int g0 = t0t1ActualCost - t1t2NewCost;
            if (g0 <= 0) {
                continue;
            }
            Nodo t3Candidate = t2Candidate.getAnterior();
            int t2t3ActualCost = calculateNodesCost(t2Candidate, t3Candidate, cityDistances);
            int t0t3NewCost = calculateNodesCost(t0, t3Candidate, cityDistances);
            int g1 = g0 + t2t3ActualCost - t0t3NewCost;
            if (g1 <= 0) {
                continue;
            } else {
                // Se encontró una mejora con 2-OPT, retornar los detalles
                return new CandidateResult(t2Candidate, t3Candidate, g1);
            }
        }
        // Si no se encontró una mejora, retornar null
        return null;
    }
    public static CandidateResult getCandidates(Nodo[] routeNodes, Nodo t0, Nodo t1, List<List<Integer>> nodesCandidates, int[][] cityDistances) {
        int t0t1ActualCost = calculateNodesCost(t0, t1, cityDistances);
        for (Integer t2CandidateID : nodesCandidates.get(t1.getId())) {
            Nodo t2Candidate = routeNodes[t2CandidateID];
            if (t2Candidate == t1 || t2Candidate == t1.getSiguiente() || t2Candidate == t1.getAnterior()) {
                continue;
            }
            int t1t2NewCost = calculateNodesCost(t1, t2Candidate, cityDistances);
            int g0 = t0t1ActualCost - t1t2NewCost;
            if (g0 <= 0) {
                continue;
            }
            Nodo t3Candidate = t2Candidate.getAnterior();
            int t2t3ActualCost = calculateNodesCost(t2Candidate, t3Candidate, cityDistances);
            int t0t3NewCost = calculateNodesCost(t0, t3Candidate, cityDistances);
            int g1 = g0 + t2t3ActualCost - t0t3NewCost;
            if (g1 <= 0) {
                for (Integer t4CandidateID : nodesCandidates.get(t3Candidate.getId())) {
                    Nodo t4Candidate = routeNodes[t4CandidateID];
                    if (t4Candidate == t3Candidate.getSiguiente() || t4Candidate == t3Candidate.getAnterior()) {
                        continue;
                    }
                    int t3t4NewCost = calculateNodesCost(t3Candidate, t4Candidate, cityDistances);
                    int g1t4 = g0 + (t2t3ActualCost - t3t4NewCost);
                    if (g1t4 <= 0) {
                        continue;
                    }
                    boolean isBetween = between(t1, t3Candidate, t4Candidate);
                    Nodo t5Candidate = isBetween ? t4Candidate.getSiguiente() : t4Candidate.getAnterior();
                    int t4t5ActualCost = calculateNodesCost(t4Candidate, t5Candidate, cityDistances);
                    int t0t5NewCost = calculateNodesCost(t5Candidate, t0, cityDistances);
                    int g2 = g1t4 + (t4t5ActualCost - t0t5NewCost);
                    if (g2 > 0) {
                        return new CandidateResult(t2Candidate, t3Candidate, t4Candidate, t5Candidate, g2);
                    }
                }
            } else {
                return new CandidateResult(t2Candidate, t3Candidate, g1);
            }
        }
        return null;
    }
    public static NodeMejor optimizacionTest (String rutaArchivo){
        JsonService jsonService = new JsonService();
        try {
            int[][] cityDistances = jsonService.readJsonMatrixFromFile(rutaArchivo);
            List<List<Integer>> nodesCandidates = computeCandidates(cityDistances,CANDIDATES_LENGTH);
            NodeMejor mejor = null;
            for (var i=1; i<=ITERACIONES ; i++){
                Nodo[] routeNodes = crearSolucion(cityDistances.length);
                int routeCost = calculateTotalDistance(routeNodes,cityDistances);
                if (i == 1) {
                    System.out.println("Costo inicial: " + routeCost);
                }
                NodeMejor auxMejor = findOptimalSolution(routeNodes, mejor, nodesCandidates, cityDistances);
                if (mejor == null) {
                    mejor = auxMejor;
                    System.out.println("Costo inicial: " + mejor.routeCost);
                }
                else if (auxMejor.routeCost < mejor.routeCost){
                    mejor = auxMejor;
                    System.out.println("Costo inicial: " +  mejor.routeCost);
                }

            }
            return mejor;

        } catch (IOException e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static NodeMejor optimizacion (int[][] cityDistances,List<List<Integer>> nodesCandidates){
        NodeMejor mejor = null;
        for (var i=1; i<=ITERACIONES ; i++){
            Nodo[] routeNodes = crearSolucion(cityDistances.length);
            NodeMejor auxMejor = findOptimalSolution(routeNodes, mejor, nodesCandidates, cityDistances);
            if (mejor == null) {
                mejor = auxMejor;
            }
            else if (auxMejor.routeCost < mejor.routeCost){
                mejor = auxMejor;

            }
        }
        return mejor;
    }
    public static double calcularPromedio(ArrayList<Integer> arr) {
        int suma = 0;
        for (int valor : arr) {
            suma += valor;
        }
        return (double) suma / arr.size();
    }
    public static List<Integer> getRecorrido (Nodo[] arrayNodes){
        List<Integer> recorrido = new ArrayList<>();
        Nodo inicio = arrayNodes[0];
        Nodo ptr = arrayNodes[0];
        do{
            recorrido.add(ptr.getId());
            ptr = ptr.getSiguiente();
        }while (inicio!=ptr);
        return  recorrido;
    }
    public static double calcularDesviacionEstandar(ArrayList<Integer> arr) {
        double promedio = calcularPromedio(arr);
        double sumaCuadrados = 0;

        for (int valor : arr) {
            double diferencia = valor - promedio;
            sumaCuadrados += diferencia * diferencia;
        }

        return Math.sqrt(sumaCuadrados / arr.size());
    }
    public static int[] generarDosNumerosNoRepetidos(int maximo) {
        Random random = new Random();
        Set<Integer> numerosGenerados = new HashSet<>();
        int[] numeros = new int[2];

        // Generar dos números aleatorios no repetidos
        for (int i = 0; i < 2; i++) {
            int numeroAleatorio;
            do {
                numeroAleatorio = random.nextInt(maximo + 1);
            } while (numerosGenerados.contains(numeroAleatorio));

            numeros[i] = numeroAleatorio;
            numerosGenerados.add(numeroAleatorio);
        }

        return numeros;
    }
    public static void algoritmoGenetico(int[][] cityDistances, List<List<Integer>> nodesCandidates){
        Poblacion poblacion = new Poblacion();
        ArrayList<Integer> listadoCostos = new ArrayList<>();
        for (int i = 0; i < CANTIDAD_POBLACION; i++) {
            NodeMejor muestra = optimizacion( cityDistances, nodesCandidates );
            poblacion.poblacion.add(muestra.routeNodes);
            listadoCostos.add(muestra.routeCost);
            if(i==0){
                poblacion.menor = muestra.routeCost;
                poblacion.mayor = muestra.routeCost;
            }else if(poblacion.menor>muestra.routeCost){
                poblacion.menor = muestra.routeCost;
            }else if(poblacion.mayor< muestra.routeCost){
                poblacion.mayor = muestra.routeCost;
            }
            System.out.println("Muestra: " + i + " " +  muestra.routeCost);
        }
        poblacion.promedio = calcularPromedio(listadoCostos);
        poblacion.desviacion = calcularDesviacionEstandar(listadoCostos);
        System.out.println("Menor: "+  poblacion.menor);
        System.out.println("Mayor: "+  poblacion.mayor);
    }
    public static Poblacion construirPoblacionConcurrente(int numArreglos, int numHilos,int[][] cityDistances, List<List<Integer>> nodesCandidates){
        Poblacion poblacion = new Poblacion();
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        for (int i = 0; i < numArreglos; i++) {
            executor.submit(() -> {
                NodeMejor muestra = optimizacion( cityDistances, nodesCandidates );
                Nodo[] arregloNodos = muestra.routeNodes;
                synchronized (poblacion.poblacion) {
                    poblacion.poblacion.add(arregloNodos);
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return poblacion;
    }
}
