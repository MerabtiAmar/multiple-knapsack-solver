import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GA {

    public static long start,end;

    public static void main(String[] args) {

        Integer[][] objets;
        Integer[] sacs;

        int taillepopulation;
        int maxGen;
        int nb_objets;
        int nb_sacs;
        int selection;
        int stagnationLimit;
        double mutationProba;


        Scanner scanner = new Scanner(System.in);
        System.out.print("Donner le la taille de la poulation : ");
        taillepopulation = scanner.nextInt();
        System.out.print("Donner le nombre max de generations : ");
        maxGen = scanner.nextInt();
        System.out.print("Donner la proba du mutation : ");
        mutationProba = scanner.nextDouble();
        System.out.print("Donner le nombre d'objets : ");
        nb_objets = scanner.nextInt();
        System.out.print("Donner le nombre de sacs : ");
        nb_sacs = scanner.nextInt();
        System.out.print("Donner le nombre d'individus dans chaque selection : ");
        selection = scanner.nextInt();
        System.out.print("Donner le nombre max d'iteration a accepter si on trouve pas de meilleurs solution : ");
        stagnationLimit = scanner.nextInt();
        objets = new Integer[2][nb_objets];
        sacs = new Integer[nb_sacs];
        Random random = new Random();
        for (int i = 0 ;i<nb_objets;i++) {
            int value = random.nextInt(15 - 8) + 8;
            int weight = random.nextInt(15 - 8) + 8;
            objets[0][i] = value;
            objets[1][i] = weight;
        }

        for (int i = 0;i<nb_sacs;i++) {
            int capacity = random.nextInt(20 - 15) + 15;
            sacs[i] = capacity;
        }
        long start = System.nanoTime();
        Solution best = GeneticAlgorithm(taillepopulation,maxGen,selection,stagnationLimit,mutationProba,nb_objets,nb_sacs,objets,sacs);
        long end = System.nanoTime();
        System.out.println(Arrays.toString(best.sol));
        System.out.println("Execution time : " + (end-start)/1e9 + " s");
    }

    public static Solution GeneticAlgorithm(int taillePopulation, int maxGen, int selection, int stagnationLimit,double mutationProba,int nb_objets, int nb_sacs, Integer[][] objets, Integer[] sacs) {
        Genetique gen = new Genetique(new PrioritySet<>(taillePopulation),taillePopulation,nb_objets,nb_sacs,objets,sacs,maxGen);
        gen.genererPopulation();
        gen.evaluation(gen.population);
        int checkStagnation = 0;
        int currenteval = gen.bestEval();
        for (int i = 0;i<gen.maxGen && !Thread.currentThread().isInterrupted();i++){
            PrioritySet<Solution> parents = gen.selection(gen.population,selection);
            PrioritySet<Solution> enfantC = gen.croisement(parents);
            PrioritySet<Solution> enfantM = gen.mutation(enfantC,mutationProba);
            gen.evaluation(enfantM);
            gen.evaluation(enfantC);
            gen.remplacement(gen.population,enfantC,enfantM);

            if (currenteval == gen.bestEval()) {
                checkStagnation++;
                if(checkStagnation > stagnationLimit){
                    System.out.println(stagnationLimit + " Iterations with no improvement !");
                    return gen.meilleurIndividu(gen.population);
                }
            }else {
                checkStagnation = 0;
                currenteval = gen.bestEval();
            }

            SacADosGUI.solutionToGrid(gen.meilleurIndividu(gen.population).getSol(),SacADosGUI.outputSolution);
            SacADosGUI.console.append("   Best evaluation : " + gen.bestEval() + "\tstagnation : " + checkStagnation + "\t\t generation : " + i + "\n");
        }
        if (Thread.currentThread().isInterrupted()) {
            // Clean up or return from the thread gracefully
            return null;
        }
        System.out.println("Max iterations reached");
        return gen.meilleurIndividu(gen.population);
    }
}
