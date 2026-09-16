import java.util.Arrays;
import java.util.Random;

public class Solution {
    Integer[] sol;
    float f;
    int evaluation;
    int nb_objet;
    int nb_sacs;
    Integer[][] objets;
    Integer[] sacs;

    public Solution(Integer[] sol, float f) {
        this.sol = sol;
        this.f = f;
    }

    public Solution(Integer[] sol, int evaluation, int nb_objet, int nb_sacs, Integer[][] objets, Integer[] sacs) {
        this.sol = sol;
        this.evaluation = evaluation;
        this.nb_objet = nb_objet;
        this.nb_sacs = nb_sacs;
        this.objets = objets;
        this.sacs = sacs;
    }

    public Integer[] getSol() {
        return sol;
    }

    public void mutation() {
        Random random = new Random();
        int obj = random.nextInt(this.nb_objet);
        int sac = random.nextInt(this.nb_sacs);
        int oldSac = this.sol[obj];
        this.sol[obj] = sac;
        if(!SacADosMultiple.validateSolution(sol,this.nb_sacs,sacs,objets))
            this.sol[obj] = oldSac;
    }

    public Solution croisement(Solution parent2) {
        Solution enfant = new Solution(null,0,nb_objet,nb_sacs,objets,sacs);
        enfant.sol = new Integer[parent2.sol.length];
        enfant.sol = this.sol.clone();
        for (int i = parent2.sol.length/2; i < parent2.sol.length; i++) {
            enfant.sol[i] = -1;
        }
        if (parent2.sol.length - parent2.sol.length / 2 >= 0)
            System.arraycopy(parent2.sol, parent2.sol.length / 2, enfant.sol, parent2.sol.length / 2, parent2.sol.length - parent2.sol.length / 2);
        if(SacADosMultiple.validateSolution(enfant.sol,nb_sacs,sacs,objets))
            return enfant;
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution = (Solution) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(sol, solution.sol);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sol);
    }

    public void evaluation() {
        this.evaluation = SacADosMultiple.evaluation(this.sol, this.objets);
    }
}
