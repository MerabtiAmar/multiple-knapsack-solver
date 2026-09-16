import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SacADosMultiple {
    static int nb_objet = 4;
    static int nb_sac = 2;
    static Integer[][] objets = {
            {10, 8, 6, 4}, // valeurs
            {5, 4, 3, 10}  // poids
    };
    static Integer[] sacs = {8, 6, 10}; // capacités

    // sol = [0, 2, 1, -1]

    static Integer[] solDFS = new Integer[nb_objet];
    static Integer[] solBFS = new Integer[nb_objet];
    static Integer[] solAetoil = new Integer[nb_objet];

    public static long stA;
    public static long etA;
    static long stBFS;
    static long etBFS;
    static long stDFS;
    static long etDFS;
    static long noeudsA = 0;
    static long noeudsA1 = 0;
    static long noeudsA2 = 0;
    static long noeudsA3 = 0;
    static long noeudsDFS = 0;
    static long noeudsBFS = 0;

    final static int VALEUR = 0;
    final static int POIDS = 1;

    public static Integer[] DFS(Integer[] sacs, Integer[][] objets){
        noeudsDFS = 0;
        Stack<Integer[]> ouvert = new Stack<>();
        ArrayList<Integer[]> ferme = new ArrayList<>();
        Integer[] bestSol = new Integer[objets[0].length];
        Arrays.fill(bestSol,-1);
        Integer[] racine = new Integer[objets[0].length];
        Arrays.fill(racine,-1);
        ouvert.push(racine);
        while (!ouvert.empty() && !Thread.currentThread().isInterrupted()){
            Integer[] n = ouvert.pop();
            if(!contains(n,-1)){
                return n;
            }
            noeudsDFS++;
            ferme.add(n);
            //afficherSol(n);
            if(etatFinal(n,sacs,objets) && evaluation(n, objets) > evaluation(bestSol, objets)){
                mettreAjour(bestSol,n);
            }else{
                ArrayList<Integer[]> nEnfants = successeurs(n, sacs, objets);
                for (Integer[] enfant : nEnfants) {
                    if (!containsArray(ferme, enfant) && !containsStack(ouvert, enfant)) {
                        ouvert.push(enfant);
                    }
                }
            }
            //System.out.println(ouvert.size());
        }
        if (Thread.currentThread().isInterrupted()) {
            // Clean up or return from the thread gracefully
            return null;
        }
        return bestSol;
    }

    public static Integer[] BFS(Integer[] sacs, Integer[][] objets){
        noeudsBFS = 0;
        LinkedList<Integer[]> ouvert = new LinkedList<>();
        ArrayList<Integer[]> ferme = new ArrayList<>();
        Integer[] bestSol = new Integer[objets[0].length];
        Arrays.fill(bestSol,-1);
        Integer[] racine = new Integer[objets[0].length];
        Arrays.fill(racine,-1);
        ouvert.add(racine);
        while (!ouvert.isEmpty() && !Thread.currentThread().isInterrupted()){
            Integer[] n = ouvert.remove();
            if(!contains(n,-1)){
                return n;
            }
            noeudsBFS++;
            ferme.add(n);
            //afficherSol(n);
            if(etatFinal(n,sacs,objets) && evaluation(n, objets) > evaluation(bestSol, objets)){
                mettreAjour(bestSol,n);
            }else{
                ArrayList<Integer[]> nEnfants = successeurs(n, sacs, objets);
                for (Integer[] enfant : nEnfants) {
                    if (!containsArray(ferme, enfant) && !containsList(ouvert, enfant)) {
                        ouvert.add(enfant);
                    }
                }
            }
            //System.out.println(ouvert.size());
            if (Thread.currentThread().isInterrupted()) {
                // Clean up or return from the thread gracefully
                return null;
            }
        }
        return bestSol;
    }

    public static Integer[] Aetoil(Integer[] sacs, Integer[][] objets, int typeHeuristique){
        switch (typeHeuristique){
            case 1 : noeudsA1 = 0; break;
            case 2 : noeudsA2 = 0; break;
            case 3 : noeudsA3 = 0; break;
        }
        noeudsA = 0;
        PriorityQueue<Solution> ouvert = new PriorityQueue<>(Comparator.comparingDouble(s -> s.f));
        HashSet<Integer[]> ferme = new HashSet<>();
        Integer[] best = new Integer[objets[0].length];
        Arrays.fill(best,-1);
        Solution bestSol = new Solution(best,fonction_cout(best,objets) + heuristique(best,sacs,objets,typeHeuristique));
        Integer[] racine = new Integer[objets[0].length];
        Arrays.fill(racine,-1);
        ouvert.add(new Solution(racine,fonction_cout(racine,objets) + heuristique(racine,sacs,objets,typeHeuristique)));
        while (!ouvert.isEmpty() && !Thread.currentThread().isInterrupted()){
            Solution n = ouvert.poll();
            if(!contains(n.getSol(),-1)){
                return n.sol;
            }
            switch (typeHeuristique){
                case 1 : noeudsA1++; break;
                case 2 : noeudsA2++; break;
                case 3 : noeudsA3++; break;
            }
            noeudsA++;
            ferme.add(n.sol);

            //afficherSol(n);
            if(etatFinal(n.getSol(),sacs,objets)){
                return n.sol;
            }
            ArrayList<Integer[]> nEnfants = successeurs(n.getSol(),sacs,objets);
            for(Integer[] enfant : nEnfants){
                if(!ferme.contains(enfant) && !ouvert.contains(new Solution(enfant,fonction_cout(enfant,objets) + heuristique(enfant,sacs,objets,typeHeuristique)))){
                    float g = fonction_cout(enfant,objets);
                    float h = heuristique(enfant,sacs,objets,typeHeuristique);
                    ouvert.add(new Solution(enfant,g + h));
                }
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            // Clean up or return from the thread gracefully
            return null;
        }
        return bestSol.sol;
    }

    private static boolean contains(Integer[] sol, int val){
        for (Integer integer : sol) {
            if (integer == val) return true;
        }
        return false;
    }

    public static int poidsActuel(Integer[] sol, int sac, Integer[][] objets){
        int poids = 0;
        for (int i = 0; i < objets[0].length; i++) {
            if(sol[i] == sac) poids += objets[POIDS][i];
        }
        return poids;
    }

    private static int poidsTotale(Integer[] sol, Integer[][] objets){
        int poids = 0;
        for (Integer s : sol){
            poids += poidsActuel(sol,s,objets);
        }
        return poids;
    }

    private static int valeurActuel(Integer[] sol, int sac, Integer[][] objets){
        int valeur = 0;
        for (int i = 0; i < objets[0].length; i++) {
            if(sol[i] == sac) valeur += objets[VALEUR][i];
        }
        return valeur;
    }

    private static int valeurTotale(Integer[] sol, Integer[][] objets){
        Integer[] temps = new Integer[sol.length];
        Arrays.fill(temps,-1);
        if(Arrays.equals(sol,temps)){
            return Integer.MIN_VALUE;
        }
        int val = 0;
        for (Integer s : sol){
            val += valeurActuel(sol,s,objets);
        }
        return val;
    }

    public static boolean setObjet(Integer[] sol, int sac, int objet, Integer[] sacs, Integer[][] objets){
        if(peutAjouter(sol,sac,objet,sacs,objets)){
            sol[objet] = sac;
            return true;
        }
        return false;
    }

    public static boolean peutAjouter(Integer[] sol, int sac, int objet, Integer[] sacs, Integer[][] objets){
        //poid acteul du sac + poid du l'objet <= capacité max du sac
        return sol[objet] == -1 && poidsActuel(sol,sac,objets) + objets[POIDS][objet] <= sacs[sac];
    }

    private static ArrayList<Integer[]> successeurs(Integer[] n,Integer[] sacs, Integer[][] objets) {
        ArrayList<Integer[]> successeurs = new ArrayList<>();
        for (int obj = 0; obj < objets[0].length; obj++) {
            for (int sac = 0; sac < sacs.length; sac++) {
                Integer[] succ = Arrays.copyOf(n,n.length);
                if(setObjet(succ,sac,obj,sacs,objets) && !Arrays.equals(succ, n) && !containsArray(successeurs,succ))
                    successeurs.add(succ);
            }
        }
        return successeurs;
    }

    public static boolean validateSolution(Integer[] sol, int nb_sac, Integer[] sacs, Integer[][] objets){
        for (int i = 0; i < nb_sac; i++) {
            if(poidsActuel(sol,i,objets) > sacs[i]){
                return false;
            }
        }
        return true;
    }

    private static void mettreAjour(Integer[] bestSol, Integer[] n) {
        System.arraycopy(n, 0, bestSol, 0, n.length);
    }

    public static int evaluation(Integer[] sol, Integer[][] objets) {
        if(sol == null)
            return 0;
        int eval = 0;
        for (int i = 0; i < sol.length; i++) {
            if(sol[i] != -1){
                eval += objets[VALEUR][i];
            }
        }
        return eval;
    }

    private static boolean etatFinal(Integer[] n, Integer[] sacs, Integer[][] objets) {
        for (int sac = 0; sac < sacs.length; sac++) {
            for (int obj = 0; obj < objets[0].length; obj++) {
                if(!containsElement(n,obj) && peutAjouter(n,sac,obj,sacs,objets)) return false;
            }
        }
        return true;
    }

    private static boolean containsArray(ArrayList<Integer[]> list, Integer[] element) {
        for (Integer[] array : list) {
            if (Arrays.equals(array, element)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsList(LinkedList<Integer[]> list, Integer[] element) {
        for (Integer[] array : list) {
            if (Arrays.equals(array, element)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsStack(Stack<Integer[]> list, Integer[] element){
        for (Integer[] array : list) {
            if (Arrays.equals(array, element)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsElement(Integer[] list, int element){
        return list[element] != -1;
    }

    public static String afficherSol(String algo, String heuristique, String temps, int[] params, Integer[] sol, long noeuds, Integer[][] objets){
        if(sol != null) {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            return switch (algo) {
                case "BSO" -> "\n   Algorithme : " + algo + "\n" +
                        "   Temps d'exécution : " + temps + " s" + "\n" +
                        "   Nombre d'itération : " + params[0] + "\n" +
                        "   Flip : " + params[1] + "\n" +
                        "   Nombre d'abeille : " + params[2] + "\n" +
                        "   Nombre de chances maximum : " + params[3] + "\n" +
                        "   Nombre d'itération locale : " + params[4] + "\n" +
                        "   Solution Trouvés : " + Arrays.toString(sol) + "\n" +
                        "   Evaluation : " + evaluation(sol, objets) + "\n\n" +
                        "   Date et heure d'exécution : " + formattedDateTime + "\n\n";
                default -> "\n   Algorithme : " + algo + "\n" +
                        "   Heuristique : " + heuristique + "\n" +
                        "   Temps d'exécution : " + temps + " s" + "\n" +
                        "   Nombre de noeuds générés : " + noeuds + "\n" +
                        "   Solution Trouvés : " + Arrays.toString(sol) + "\n" +
                        "   Evaluation : " + evaluation(sol, objets) + "\n\n" +
                        "   Date et heure d'exécution : " + formattedDateTime + "\n\n";
            };
        }else return "";
    }


    public static String afficherSol(String algo, String temps, double[] params, Integer[] sol, Integer[][] objets) {
        if (sol != null) {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            return "\n   Algorithme : " + algo + "\n" +
                    "   Temps d'exécution : " + temps + " s" + "\n" +
                    "   Taille de la population : " + params[0] + "\n" +
                    "   Nombre de générations : " + params[1] + "\n" +
                    "   probabilité de mutation : " + params[4] + "\n" +
                    "   Solution Trouvés : " + Arrays.toString(sol) + "\n" +
                    "   Evaluation : " + evaluation(sol, objets) + "\n\n" +
                    "   Date et heure d'exécution : " + formattedDateTime + "\n\n";
        }else return "";
    }



    private static int fonction_cout(Integer[] sol, Integer[][] objets){
        int poids = 0;
        for (int i = 0;i < sol.length;i++){
            if(sol[i] != -1)
                poids += objets[POIDS][i];
        }
        return poids;
    }

    private static float heuristique1(Integer[] sol,Integer[] sacs, Integer[][] objets){
        Integer[] none = new Integer[objets[0].length];
        Arrays.fill(none,-1);
        if(Arrays.equals(sol,none)){
            return Float.MAX_VALUE;
        }else if(etatFinal(sol,sacs,objets)){
            return 0;
        }
        float valeurs = 0;
        float poids = 0;
        for (int i = 0; i < sol.length; i++) {
            if(sol[i] == -1){
                valeurs += objets[VALEUR][i];
            }else {
                poids += sacs[sol[i]] - poidsActuel(sol,sol[i],objets); //poids restant
            }
        }
        return 10*valeurs/poids;
    }

    private static float relaxationLineaire(Integer[] sol, int[] capacitesRestantes,int nb_sac, Integer[][] objets) {
        float valeurEstimee = 0;
        for (int j = 0; j < nb_sac; j++) {
            float capaciteRestante = capacitesRestantes[j];
            for (int i = 0; i < objets[0].length; i++) {
                if (sol[i] == -1 && objets[POIDS][i] <= capaciteRestante) {
                    float objetsAjoutes = capaciteRestante / objets[POIDS][i];
                    valeurEstimee += objetsAjoutes * objets[VALEUR][i];
                    capaciteRestante -= objetsAjoutes * objets[POIDS][i];
                }
            }
        }
        return valeurEstimee;
    }

    private static float heuristique2(Integer[] sol,Integer[] sacs, Integer[][] objets) {
        int[] capacitesRestantes = new int[sacs.length];
        for (int j = 0; j < sacs.length; j++) {
            capacitesRestantes[j] = sacs[j] - poidsActuel(sol, j,objets);
        }
        return relaxationLineaire(sol, capacitesRestantes,sacs.length,objets);
    }

    private static float heuristique3(Integer[] sol, Integer[][] objets) {
        int valeur = 0;
        float equilibrePenalise;
        int minPoids = Integer.MAX_VALUE;
        int maxPoids = Integer.MIN_VALUE;

        for (int i = 0; i < objets[0].length; i++) {
            if (sol[i] != -1) {
                valeur += objets[0][i];
                int poids = poidsActuel(sol, sol[i],objets);
                minPoids = Math.min(minPoids, poids);
                maxPoids = Math.max(maxPoids, poids);
            }
        }

        int differenceMaximale = maxPoids - minPoids;

        float facteurPond = 0.6f;

        equilibrePenalise = facteurPond * differenceMaximale;

        return valeur - equilibrePenalise;
    }

    public static float heuristique(Integer[] sol, Integer[] sacs, Integer[][] objets, int typeHeuristique) {
        return switch (typeHeuristique) {
            case 2 -> heuristique2(sol, sacs, objets);
            case 3 -> heuristique3(sol, objets);
            default -> heuristique1(sol, sacs, objets);
        };
    }


}
