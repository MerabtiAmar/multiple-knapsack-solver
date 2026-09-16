import java.util.Arrays;
import java.util.Random;

public class Genetique {
    PrioritySet<Solution> population;
    int taillePopulation;
    int maxGen;
    int nb_objets;
    int nb_sacs;
    Integer[][] objets;
    Integer[] sacs;

    public Genetique(PrioritySet<Solution> population, int taillePopulation, int nb_objets,int nb_sacs,Integer[][] objets, Integer[] sacs, int maxGen) {
        this.population = population;
        this.taillePopulation = taillePopulation;
        this.maxGen = maxGen;
        this.nb_objets = nb_objets;
        this.nb_sacs = nb_sacs;
        this.objets = objets;
        this.sacs = sacs;
    }

    public void genererPopulation(){
        for (int i = 0; i < taillePopulation; i++) {
            Solution solution = new Solution(new Integer[nb_objets],0,nb_objets,nb_sacs,objets,sacs);
            Arrays.fill(solution.sol,-1);
            for (int obj = 0; obj < nb_objets; obj++) {
                Random random = new Random();
                int randomize = random.nextInt(5);
                if(randomize != 0) {
                    int attempts = 0;
                    while (attempts < 3) {
                        int sac = random.nextInt(nb_sacs);
                        if (SacADosMultiple.peutAjouter(solution.getSol(), sac, obj, sacs, objets)) {
                            solution.sol[obj] = sac;
                            attempts = 3;
                        }else {
                            attempts++;
                        }
                    }
                }
            }
            population.add(solution);
        }
    }

    public void evaluation(PrioritySet<Solution> population){
        for (Solution sol : population){
            sol.evaluation();
        }
    }

    public PrioritySet<Solution> mutation(PrioritySet<Solution> enfantC, double proba){
        Random random = new Random();
        for(Solution sol : enfantC){
            if(random.nextFloat() < proba) {
                sol.mutation();
            }
        }
        return enfantC;
    }

    public PrioritySet<Solution> croisement(PrioritySet<Solution> parents){
        PrioritySet<Solution> enfantC = new PrioritySet<>(parents.size() * 2);
        for(Solution parent1 : parents){
            for (Solution parent2 : parents){
                if(!parent1.equals(parent2)){
                    Solution enfant1 = parent1.croisement(parent2);
                    Solution enfant2 = parent2.croisement(parent1);
                    if(enfant1 != null)
                        enfantC.add(enfant1);
                    if(enfant2 != null)
                        enfantC.add(enfant2);
                }
            }
        }
        return enfantC;
    }

    public void printPopulation(){
        for (Solution sol : this.population){
            System.out.println(sol);
        }
    }

    public void printPopulation(PrioritySet<Solution> population){
        for (Solution sol : population){
            System.out.println(sol);
        }
    }


    public PrioritySet<Solution> selection(PrioritySet<Solution> population, int m) {
        PrioritySet<Solution> select = new PrioritySet<>(m);
        select.addAll(population.getBest(m));
        return select;
    }

    public void remplacement(PrioritySet<Solution> population, PrioritySet<Solution> enfantC, PrioritySet<Solution> enfantM) {
        population.addAll(enfantM);
        population.addAll(enfantC);
    }

    public Solution meilleurIndividu(PrioritySet<Solution> population) {
        return population.peek();
    }

    public int bestEval(){
        return meilleurIndividu(population).evaluation;
    }
}
