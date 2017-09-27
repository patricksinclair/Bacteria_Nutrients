import java.util.*;

public class Bacteria {

    //m corresponds to the current genotype of the bacterium
    private int m;
    //finalM is the total no. of genotypes in this evolutionary path
    private int finalM;
    private final int initialM = 1;
    //migration rate: probability the bacterium migrates from one microhabitat to a neighbouring one
    private double b = 0.1;
    //death rate
    private double d = 0.1;
    //mutation rate
    private double mu = 1e-4;

    public Bacteria(int m){
        this.m = m;
        this.finalM = 6;
    }

    //this constructor allows for the length of the mutational pathways to be specified
    public Bacteria(int m, int finalM){
        this.m = m;
        this.finalM = finalM;
    }

    //get and sets for genotype
    public int getM(){
        return m;
    }
    public void setM(int m){
        this.m = m;
    }

    public int getFinalM(){
        return finalM;
    }
    //@get and sets for migration rate
    public double getB(){
        return b;
    }
    public void setB(double b){
        this.b = b;
    }

    //get and sets for death rate
    public double getD(){
        return d;
    }
    public void setD(double d){
        this.d = d;
    }

    public double getMu(){return mu;}
    public void setMu(double mu){
        this.mu = mu;
    }


    //This is the minimum inhibitory concentration.  Used to determine a bacterium's ability to
    //replicate based on its genotype
    public double MIC(){
        return Math.pow(4., ((double) getM() - 1.));
    }


    //calculates the growth rate of the bacterium based on the concentration of the antibiotic present
    public double growthRate(double c){

        double phi_c = 1. - (c/MIC())*(c/MIC());

        if(phi_c < 0.) return 0.;
        else return phi_c;
    }


    //calculates the replication rate of the bacterium based on growth rates and population density
    public double replicationRate(double c, int N, int K){

        return growthRate(c)*(1. - (double)(N/K));
    }

    public double replicationRate_Nutrients(double c, double K_prime, int S){

        return growthRate(c)*((double)S/(K_prime + (double)S));
    }


    public void increaseGenotype(){
        if(m < finalM){
            m++;
        }
    }
    public void decreaseGenotype(){
        if(m > initialM){
            m--;
        }
    }





    public static ArrayList<Bacteria> initialPopulation(int K, int initM){

        ArrayList<Bacteria> initPop = new ArrayList<Bacteria>(K);

        for(int i = 0; i < K; i++){
            initPop.add(new Bacteria(initM));
        }
        return initPop;
    }
}
