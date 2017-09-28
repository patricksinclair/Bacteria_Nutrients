import java.util.ArrayList;

public class Microhabitat {

    //this is the carrying capacity of the microhabitat: tha max no. of occupying bacteria
    private int K;
    //this is the concentration of antibiotic present in the microhabitat
    private double c;
    //the amount of food nutrients present (s for sustenance)
    private int S;
    //factor used in nutirent growth rate calculations
    private double K_prime = 33.;
    //arraylist used to store the bacteria
    private ArrayList<Bacteria> population;

    public Microhabitat(){

    }

    public Microhabitat(int K){
        this.K = K;
    }

    public Microhabitat(int K, double c){
        this.K = K;
        this.c = c;
        this.population = new ArrayList<Bacteria>(K);
    }

    public Microhabitat(int K, double c, int S){
        this.K = K;
        this.c = c;
        this.S = S;
        this.population = new ArrayList<Bacteria>(K);
    }

    public int getK(){return K;}
    public void setK(int K){
        this.K = K;
    }

    public double getC(){return c;}
    public void setC(double c){
        this.c = c;
    }

    public double getK_prime(){return K_prime;}
    public void setK_prime(double K_prime){this.K_prime = K_prime;}

    public int getS(){return S;}
    public void setS(int S){this.S = S;}

    public int getN(){
        return population.size();
    }

    public ArrayList<Bacteria> getPopulation(){
        return population;
    }

    public void setPopulation(ArrayList<Bacteria> population){
        this.population = population;
    }


    public Bacteria getBacteria(int i){
        return population.get(i);
    }


    public double getAverageGenotype(){
        int n = population.size();
        double counter = 0.;
        for(int i = 0; i < n; i++){
            counter += (double)population.get(i).getM();
        }
        return counter/(double)n;
    }


    public void fillWithWildType(){

        int initGenotype = 1;

        for(int i = 0; i < K; i++){
            population.add(new Bacteria(initGenotype));
        }
    }


    public void fillWithWildType(int finalGenotype){

        int initGenotype = 1;

        for(int i = 0; i < K; i++){
            population.add(new Bacteria(initGenotype, finalGenotype));
        }
    }

    public void consumeNutrients(){
        if(S > 0) S--;
    }


    public void removeABacterium(int i){
        population.remove(i);
    }

    public void addABacterium(Bacteria newBact){
        population.add(newBact);
    }

}
