import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Experimentation {
    static int max_objets = 100;
    static int max_sac = 80;

    public static void main(String[] args) {
        Random random = new Random();
        ArrayList<Integer[]> arrayList = new ArrayList<>();

        for (int i = 20; i <= max_sac; i+=10) {
            Integer[] sacs = new Integer[i];
            for (int j = 0; j < i; j++) {
                int capacity = random.nextInt(30 - 15) + 15;
                sacs[j] = capacity;
            }
            arrayList.add(sacs);
        }


        Integer[][] objets = new Integer[2][max_objets];
        for (int j = 0 ;j<max_objets;j++) {
            int value = random.nextInt(20 - 8) + 8;
            int weight = random.nextInt(20 - 8) + 8;
            objets[0][j] = value;
            objets[1][j] = weight;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results_obj.txt"))) {
            for (int i = 0; i < arrayList.size(); i++) {

                long start = System.nanoTime();
                Solution solution = GA.GeneticAlgorithm(500,100,200,100,0.7,max_objets,arrayList.get(i).length,objets,arrayList.get(i));
                long end = System.nanoTime();

                // Calcul du temps d'exécution en millisecondes
                double executionTime1 = (end - start) / 1e9;

                BSO.nb_objets = max_objets;
                BSO.nb_sacs = arrayList.get(i).length;
                BSO.objets = objets;
                BSO.sacs = arrayList.get(i);
                start = System.nanoTime();
                Integer[] sol = BSO.beeSwarmOptimization(objets,arrayList.get(i));
                end = System.nanoTime();

                double executionTime2 = (end - start) / 1e9;

                // Écriture des résultats dans le fichier
                assert solution != null;
                writer.write(max_objets + "," + arrayList.get(i).length + "," + executionTime1 + "," + solution.evaluation + "\t\t" +
                                 max_objets + "," + arrayList.get(i).length + "," + executionTime2 + "," + SacADosMultiple.evaluation(sol, objets) + "\n");
                writer.newLine();

                System.out.println("Generation: " + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
