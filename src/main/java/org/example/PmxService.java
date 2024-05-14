package org.example;

import java.util.*;

public class PmxService {
    public static Nodo pmxCrossover(Nodo parent1, Nodo parent2, int startIndex, int endIndex) {
        // Crear un hijo inicialmente como una copia de parent1
        Nodo child = copyNodes(parent1);

        // Mapear la sección seleccionada del parent1 a la sección correspondiente del parent2
        Map<Integer, Integer> mapping = new HashMap<>();
        Nodo p1 = parent1;
        Nodo p2 = parent2;

        // Recorrer hasta el índice de inicio
        for (int i = 0; i < startIndex; i++) {
            p1 = p1.getSiguiente();
            p2 = p2.getSiguiente();
        }

        // Copiar genes del parent2 al hijo dentro del rango especificado
        Nodo childCurrent = child;
        while (p1 != null && p2 != null && p1 != parent1 && p2 != parent2 && p1.getPosicion() <= endIndex) {
            int geneP1 = p1.getId();
            int geneP2 = p2.getId();
            mapping.put(geneP1, geneP2);
            p1 = p1.getSiguiente();
            p2 = p2.getSiguiente();
        }

        // Resolver conflictos y copiar genes del parent2 al hijo
        childCurrent = child;
        while (childCurrent != null) {
            int geneId = childCurrent.getId();
            if (geneId >= startIndex && geneId <= endIndex) {
                int mappedGeneId = mapping.getOrDefault(geneId, geneId);
                childCurrent.setId(mappedGeneId);
            }
            childCurrent = childCurrent.getSiguiente();
        }

        return child;
    }

    // Método auxiliar para copiar nodos (genes) a un nuevo hijo
    private static Nodo copyNodes(Nodo parent) {
        if (parent == null) {
            return null;
        }
        Nodo firstChild = new Nodo(parent.getId(), parent.getPosicion(), null, null);
        Nodo currentParent = parent.getSiguiente();
        Nodo currentChild = firstChild;

        // Copiar los nodos restantes
        while (currentParent != null && currentParent != parent) {
            Nodo newNode = new Nodo(currentParent.getId(), currentParent.getPosicion(), currentChild, null);
            currentChild.setSiguiente(newNode);
            currentChild = newNode;
            currentParent = currentParent.getSiguiente();
        }

        // Conectar el último nodo con el primero para formar una lista circular
        currentChild.setSiguiente(firstChild);
        firstChild.setAnterior(currentChild);

        return firstChild;
    }
    public static int obtenerUnTercio(int numero) {
        // Calculamos el tercio del número usando división entera
        int tercio = numero / 3;

        // Si el número es divisible por 3, simplemente retornamos el tercio
        if (numero % 3 == 0) {
            return tercio;
        } else {
            // Si el número no es divisible por 3, ajustamos el tercio para redondear al entero más cercano
            // Dependiendo del residuo al dividir por 3
            int residuo = numero % 3;
            if (residuo == 1) {
                // Si el residuo es 1, necesitamos sumar 1 al tercio para redondear hacia arriba
                return tercio + 1;
            } else {
                // Si el residuo es 2, necesitamos restar 1 al tercio para redondear hacia abajo
                return tercio;
            }
        }
    }
    public static List<Integer> concatenarArreglos(List<Integer> padre, List<Integer> madre,int seccion ) {
        List<Integer> seccion1 = padre.subList(0, seccion);
        List<Integer> seccion2 = madre.subList(seccion, seccion*2);
        List<Integer> seccion3 = padre.subList(seccion*2, padre.size());
        List<Integer> listaConcatenada = new ArrayList<>();
        listaConcatenada.addAll(seccion1);
        listaConcatenada.addAll(seccion2);
        listaConcatenada.addAll(seccion3);
        return listaConcatenada;
    }
    public static boolean validarNumeros(List<Integer> lista,int size) {
        // Crear un HashSet con los números del 0 al 51
        Set<Integer> numerosEsperados = new HashSet<>();
        for (int i = 0; i <= size-1; i++) {
            numerosEsperados.add(i);
        }

        // Crear un HashSet con los números presentes en la lista
        Set<Integer> numerosEnLista = new HashSet<>(lista);

        // Verificar si la lista contiene todos los números del 0 al 51
        boolean contieneTodos = numerosEnLista.containsAll(numerosEsperados);

        if (!contieneTodos) {
            // Identificar los números que faltan en la lista
            Set<Integer> numerosFaltantes = new HashSet<>(numerosEsperados);
            numerosFaltantes.removeAll(numerosEnLista); // Quitar los números presentes de los esperados
            System.out.println("Números que faltan en la lista: " + numerosFaltantes);
        }

        return contieneTodos;
    }
    public static List<List<Integer>> pmxCrossover(List<Integer> padre, List<Integer> madre) {
        // Crear dos mapas para registrar las correspondencias entre genes intercambiados
        Map<Integer, Integer> map1 = new HashMap<>();
        Map<Integer, Integer> map2 = new HashMap<>();

        // Elegir dos puntos de cruce aleatorios
        int x1 = (int) (Math.random() * (padre.size() - 1));
        int x2 = x1 + (int) (Math.random() * (padre.size() - x1));

        // Crear una copia de los padres
        List<Integer> hijo1 = new ArrayList<>(padre);
        List<Integer> hijo2 = new ArrayList<>(madre);

        // Realizar el cruce PMX entre los padres en las secciones definidas por x1 y x2
        for (int i = x1; i < x2; i++) {
            // Intercambiar genes entre los hijos
            hijo1.set(i, madre.get(i));
            map1.put(madre.get(i), padre.get(i));

            hijo2.set(i, padre.get(i));
            map2.put(padre.get(i), madre.get(i));
        }

        // Resolver los conflictos en las secciones restantes fuera de x1 y x2
        resolverConflictos(hijo1, map1, x1, x2);
        resolverConflictos(hijo2, map2, x1, x2);

        // Retornar los hijos generados
        List<List<Integer>> offspring = new ArrayList<>();
        offspring.add(hijo1);
        offspring.add(hijo2);
        return offspring;
    }

    // Función para resolver conflictos en las secciones restantes
    private static void resolverConflictos(List<Integer> hijo, Map<Integer, Integer> map,
                                           int x1, int x2) {
        for (int i = 0; i < hijo.size(); i++) {
            if (i < x1 || i >= x2) {
                int current = hijo.get(i);
                while (map.containsKey(current)) {
                    current = map.get(current);
                }
                hijo.set(i, current);
            }
        }
    }
    public static List<Integer> pmxIntento(List<Integer> padre, List<Integer> madre){
        int longitud = padre.size();
        int tercio = longitud / 3;  // Punto de inicio de la sección
        int dosTercios = 2 * tercio; // Punto de fin de la sección
        // Obtener la sección del padre y de la madre
        List<Integer> seccionPadre = padre.subList(tercio, dosTercios);
        // Crear una copia del padre para el hijo
        List<Integer> hijo = new ArrayList<>(padre);
        // Reemplazar la sección del padre con la sección de la madre en el hijo
        for (int i = tercio; i < dosTercios; i++) {
            int genMadre = madre.get(i); // Gen correspondiente de la madre
            // Si el gen del padre no está en la sección reemplazada del hijo, hacer el intercambio
            if (!seccionPadre.contains(madre.get(i))) {
                hijo.set(i, genMadre);
            } else {
                // Resolver conflicto: encontrar el gen equivalente en la madre y reemplazar en el hijo
                int indexMadre = madre.indexOf(padre.get(i)); // Posición del gen correspondiente en la madre
                hijo.set(i, genMadre);
            }
        }
        boolean isCorrect = validarNumeros(hijo,hijo.size());
        return hijo;
    }

    public static void  pmxFinal(int[][] mapa, Nodo[] padre,Nodo[] madre){
        Random random = new Random();
        int largo = padre.length;
        Nodo[] hijo = new Nodo[largo];
        for (int i = 0; i < largo; i++) {
            hijo[i] = new Nodo(i, -1, null, null);
        }

        int corte1 = random.nextInt(largo + 1);
        Nodo ptr_p0 = padre[corte1];
        int corte2 = corte1;
        while (corte2 == corte1) {
            corte2 = random.nextInt(largo);
        }
        Nodo ptr_p3 = padre[corte2];
        int distanciaEntreCortes = ptr_p3.getPosicion() - ptr_p0.getPosicion();
        if(distanciaEntreCortes<0) {
            distanciaEntreCortes += largo;
        }
        if (distanciaEntreCortes >= largo / 2) {
            Nodo temp = ptr_p0;
            ptr_p0 = ptr_p3;
            ptr_p3 = temp;
        }
        int offset= padre[0].getPosicion();
        Nodo aux=ptr_p0;
        int pos=aux.getPosicion()-offset;
        if (pos<0){
            pos+=largo;
        }
        int nPos=pos;
        Nodo ptr_m0= madre[0];
        if (nPos <= (largo / 2)) {
            while ((nPos--) > 0) {
                ptr_m0 = ptr_m0.getSiguiente();
            }
        } else {
            nPos = largo - nPos;
            while ((nPos--) > 0) {
                ptr_m0 = ptr_m0.getAnterior();
            }
        }
        int[] reemplazos = new int[largo];
        Arrays.fill(reemplazos, -1);
        Nodo aux_m=ptr_m0;
        while (aux!=ptr_p3.getSiguiente()){
            Nodo nodoHijo=hijo[aux.getId()];
            nodoHijo.setId(aux.getId());
            pos++; if (pos==largo)pos=0;
            nodoHijo.setPosicion(pos);
            nodoHijo.setSiguiente(hijo[aux.getSiguiente().getId()]);
            nodoHijo.setAnterior(hijo[aux.getAnterior().getId()]);
            if (aux.getId()!=aux_m.getId())reemplazos[aux.getId()]=aux_m.getId();
            aux=aux.getSiguiente();
            aux_m = aux_m.getSiguiente();
        }

        Nodo anterior=hijo[ptr_p3.getId()];
        while (aux_m!=ptr_m0) {
            int id=getIdReemplazo(reemplazos, aux_m.getId());
            Nodo nodoHijo=hijo[id];
            nodoHijo.setId(id);
            pos++; if (pos==largo)pos=0;
            nodoHijo.setPosicion(pos);
            nodoHijo.setAnterior(anterior);
            nodoHijo.getAnterior().setSiguiente(nodoHijo);
            anterior=nodoHijo;
            aux_m=aux_m.getSiguiente();
        }
        hijo[ptr_p0.getId()].setAnterior(hijo[getIdReemplazo(reemplazos,ptr_m0.getAnterior().getId())]);
        hijo[ptr_p0.getId()].getAnterior().setSiguiente(hijo[ptr_p0.getId()]);
        final boolean valido = validarNodos(hijo);
        final boolean conexa = isConexa(hijo);
    }
    private static int getIdReemplazo(int[] reemplazos, int id) {
        while (reemplazos[id] != -1) {
            id = reemplazos[id];
        }
        return id;
    }
    private static boolean validarNodos(Nodo[] hijo){
        HashSet<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < hijo.length; i++) {
            ids.add(hijo[i].getId());
        }
        return ids.size()==hijo.length;
    }
    private static boolean isConexa(Nodo[] nodos){
        Nodo inicio = nodos[0];
        Nodo actual = inicio.getSiguiente();
        int cont=1;
        int pos = inicio.getPosicion();
        pos++;
        if (pos==nodos.length){
            pos=0;
        }
        while (actual!=inicio){
            if(pos!=actual.getPosicion()){
                return false;
            }
            pos++;
            if (pos==nodos.length){
                pos=0;
            }
            cont++;
            if(actual.getSiguiente().getAnterior() != actual){
                return false;
            }
            actual = actual.getSiguiente();
            if(cont>nodos.length){
                return false;
            }
        }
        return cont==nodos.length;
    }
}
