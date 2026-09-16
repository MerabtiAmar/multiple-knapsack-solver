import java.util.*;

public class BSO {
    public static int max_iter = 1000;
    public static int flip = 25;
    public static int nb_bees = 1000;
    public static int max_chances = 30;
    public static int nb_chances = max_chances;
    public static int local_iter = 65;
    public static ArrayList<Integer[]> taboo;
    public static ArrayList<Integer[]> bees;
    public static ArrayList<Integer[]> dance;

    static int nb_objets = 10;
    static int nb_sacs = 5;
    static Integer[][] objets = {
            {10, 8, 6, 4, 9, 2, 14, 7, 3, 5}, // valeurs
            {5, 4, 3, 10, 13, 7, 6, 9, 12, 3} // poids
    };
    static Integer[] sacs = {8, 6, 10, 15, 13}; // capacités

    public static Integer[] sref;

    static long start;
    static long end;

    /*public static void main(String[] args) {
        Integer[] best = beeSwarmOptimization(objets,sacs);
        System.out.printf(Arrays.toString(best) + "\t" + SacADosMultiple.evaluation(best,objets));
        System.out.println("\t" + (end-start)/1e9 + " s");
    }*/


    public static Integer[] beeSwarmOptimization(Integer[][] objets, Integer[] sacs) {
        taboo = new ArrayList<>();
        bees = new ArrayList<>();
        dance = new ArrayList<>();
        sref = BeeInit(objets,sacs);
        int nb_iter = 0;
        while(nb_iter < max_iter && !Thread.currentThread().isInterrupted()){
            taboo.add(sref);
            ArrayList<Integer[]> searchPoints = detSearchPoints(sref, nb_bees, flip);
            assignSolutionToBees(searchPoints,bees);
            for (Integer[] bee : bees) {
                Integer[] result = startSearch(bee, local_iter);
                dance.add(result);
            }
            sref = bestSolution(dance,sref,max_chances);
            SacADosGUI.solutionToGrid(sref,SacADosGUI.outputSolution);
            SacADosGUI.console.append("   Best Evaluation : " + SacADosMultiple.evaluation(sref,objets) + "\t" + "Itération : " + nb_iter+"\n");
            nb_iter++;
            if (Thread.currentThread().isInterrupted()) {
                // Clean up or return from the thread gracefully
                return null;
            }
        }
        return sref;
    }

    private static boolean contains(Integer[] sref) {
        for (Integer integer : sref) {
            if (integer == -1)
                return true;
        }
        return false;
    }

    private static Integer[] bestSolution(ArrayList<Integer[]> dance, Integer[] sref, int max_chances) {
        Integer[] bestQuality = getBestQuality(dance);
        int delta_f = SacADosMultiple.evaluation(bestQuality,objets) - SacADosMultiple.evaluation(sref,objets);
        if(delta_f > 0){
            sref = bestQuality;
            if(nb_chances < max_chances){
                nb_chances = max_chances;
            }
        }else {
            nb_chances--;
            if (nb_chances > 0){
                sref = bestQuality;
            }else {
                sref = getBestDiversity(sref, taboo);
                nb_chances = max_chances;
            }
        }
        return sref;
    }

    private static Integer[] getBestDiversity(Integer[] sref, ArrayList<Integer[]> taboo) {
        int max = Integer.MIN_VALUE;
        Integer[] diversity = taboo.getFirst();
        for (Integer[] sol : taboo){
            int dist = distance(sol,sref);
            if(dist > max) {
                diversity = sol;
                max = dist;
            }
        }
        return diversity;
    }

    private static int distance(Integer[] sol, Integer[] sref) {
        int count = 0;
        for (int i = 0; i < sol.length; i++) {
            if(!Objects.equals(sref[i], sol[i]))
                count++;
        }
        return count;
    }

    private static Integer[] getBestQuality(ArrayList<Integer[]> dance) {
        Integer[] bestQuality = dance.getFirst();
        for (Integer[] sol : dance) {
            if (SacADosMultiple.evaluation(sol, objets) > SacADosMultiple.evaluation(bestQuality, objets))
                bestQuality = sol.clone();
        }
        return bestQuality;
    }

    private static Integer[] startSearch(Integer[] bee, int local_iter) {
        ArrayList<Integer[]> local = new ArrayList<>();
        for (int i = 0; i < local_iter; i++) {
            Integer[] searchBee = bee.clone();
            Random random = new Random();
            int attempts = 0;
            while (attempts < 3) {
                int sac = random.nextInt(nb_sacs);
                int obj = random.nextInt(nb_objets);
                if (SacADosMultiple.peutAjouter(searchBee, sac, obj, sacs, objets)) {
                    searchBee[obj] = sac;
                    attempts = 3;
                } else {
                    attempts++;
                }
            }
            local.add(searchBee);
        }
        return getBestQuality(local);
    }

    private static void assignSolutionToBees(ArrayList<Integer[]> searchPoints, ArrayList<Integer[]> bees) {
        bees.clear();
        bees.addAll(searchPoints);
    }

    private static ArrayList<Integer[]> detSearchPoints(Integer[] sref, int nbBees, int flip) {
        ArrayList<Integer[]> searchPoints = new ArrayList<>();
        int i = 0;
        int h = 0;
        while (i < nbBees && h < flip){
            Integer[] s = sref.clone();
            int p = 0;
            while (flip*p + h < nb_objets){
                inverse(s,flip*p + h);
                p++;
            }
            searchPoints.add(s);
            h++;
            i++;
        }
        return searchPoints;
    }

    private static void inverse(Integer[] s, int i) {
        if(s[i] == -1) {
            Random random = new Random();
            int attempts = 0;
            while (attempts < 3) {
                int sac = random.nextInt(nb_sacs);
                if (SacADosMultiple.peutAjouter(s, sac, i, sacs, objets)) {
                    s[i] = sac;
                    return;
                } else {
                    attempts++;
                }
            }
        }else {
            s[i] = -1;
        }
    }

    private static Integer[] BeeInit(Integer[][] objets, Integer[] sacs) {
        Integer[] sol = new Integer[nb_objets];
        Arrays.fill(sol,-1);
        for (int obj = 0; obj < nb_objets; obj++) {
            Random random = new Random();
            int randomize = random.nextInt(5);
            if(randomize != 0) {
                int attempts = 0;
                while (attempts < 3) {
                    int sac = random.nextInt(nb_sacs);
                    if (SacADosMultiple.peutAjouter(sol, sac, obj, sacs, objets)) {
                        sol[obj] = sac;
                        attempts = 3;
                    }else {
                        attempts++;
                    }
                }
            }
        }
        return sol;
    }
}
