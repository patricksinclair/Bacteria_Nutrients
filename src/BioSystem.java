import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;

import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    //the number of microhabitats contained within the system
    private int L;
    private int K;
    //the steepness of the antibiotic gradient
    private double alpha;
    //the concentration of antibiotic for uniform gradients
    private double c;
    private double timeElapsed;
    private int S;
    //boolean which is true when the system mutates to the final possible genotype
    private boolean resistanceReached;

    Random rand = new Random();

    private Microhabitat[] microhabitats;

    public BioSystem(int L, int K, int S, double alpha){
        this.L = L;
        this.K = K;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        this.resistanceReached = false;
        this.S = S;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i, S);
        }
        //fills the first microhabitat with bacteria of genotype 1.
        microhabitats[0].fillWithWildType();
    }

    //This constructor is used to create a BioSystem with a uniform drug concentration of value c
    //the given alpha value is just a placeholder
    public BioSystem(int L, int K, double alpha, double c){
        this.L = L;
        this.K = K;
        this.alpha = 0.;
        this.c = c;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){
            microhabitats[i] = new Microhabitat(K, c);
        }
        microhabitats[0].fillWithWildType();
    }

    //This constructor allows for the length of the bacteria's mutational pathway to be specified
    public BioSystem(int L, int K, double alpha, int finalGenotype){
        this.L = L;
        this.K = K;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i);
        }
        microhabitats[0].fillWithWildType(finalGenotype);
    }

    public BioSystem(int L, int K, double alpha, double c, int finalGenotype){
        this.L = L;
        this.K = K;
        this.alpha = 0.;
        this.c = c;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){
            microhabitats[i] = new Microhabitat(K, c);
        }
        microhabitats[0].fillWithWildType(finalGenotype);
    }

    public BioSystem(){}


    public int getL(){return L;}
    public void setL(int L){this.L = L;}

    public int getK(){return K;}
    public void setK(int K){this.K = K;}

    public int getS(){return S;}
    public void setS(int S){this.S = S;}

    public double getAlpha(){return alpha;}
    public void setAlpha(double alpha){this.alpha = alpha;}

    public double getC(){return c;};
    public void setC(double c){this.c = c;}

    public double getTimeElapsed(){return timeElapsed;}

    public boolean getResistanceReached(){return resistanceReached;}
    public void setResistanceReached(boolean resistanceReached){this.resistanceReached = resistanceReached;}

    //returns the total number of bacteria in the system
    public int getCurrentPopulation(){
        int runningTotal = 0;
        //removed if statement
        for(int i = 0; i < L; i++){
            runningTotal += microhabitats[i].getN();
        }
        return runningTotal;
    }


    public Microhabitat getMicrohabitat(int i){
        return microhabitats[i];
    }

    public Bacteria getBacteria(int l, int k){
        return microhabitats[l].getBacteria(k);
    }

    //migrates a specific bacterium to the next microhabitat
    public void migrate(int currentL, int bacteriumIndex){

        //ensures that migration can't occur past the last habitat
        if(currentL < (L-1)){

            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL + 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));
        }
    }


    //"kills" a bacterium by removing it from the habitat
    public void death(int currentL, int bacteriumIndex){
        microhabitats[currentL].removeABacterium(bacteriumIndex);
    }


    //replicates a bacterium by creating a new bacteria with the same genotype, unless mutation occurs.
    public void replicate(int currentL, int bacteriumIndex){

        microhabitats[currentL].consumeNutrients();
        //the bacterium which is going to be replicated and its associated genotype
        Bacteria parentBac = microhabitats[currentL].getBacteria(bacteriumIndex);
        int m = parentBac.getM();
        int finalM = parentBac.getFinalM();

        //only allows replication if the habitat isn't at carrying capacity //removed


        //these are used to determine whether or not the replicated bacterium is a mutant
        double mu = parentBac.getMu();
        double s = rand.nextDouble();

        Bacteria childBac = new Bacteria(m, finalM);
        if(s < mu/2.) {
            childBac.increaseGenotype();
        } else if(s >= mu/2. && s < mu) {
            childBac.decreaseGenotype();
        }
        microhabitats[currentL].addABacterium(childBac);
        if(childBac.getM() == childBac.getFinalM()) resistanceReached = true;
        //if(childBac.getM() >2  && currentL > 3) System.out.println("random genotype: " + childBac.getM());

    }



    public void performAction(){

        //selects a random bacteria from the total population
        int randomIndex = rand.nextInt(getCurrentPopulation());
        int indexCounter = 0;
        int microHabIndex = 0;
        int bacteriaIndex = 0;

        forloop:
        for(int i = 0; i < getL(); i++){

            if((indexCounter + microhabitats[i].getN()) < randomIndex){

                indexCounter += microhabitats[i].getN();
                continue forloop;
            }
            else{
                microHabIndex = i;
                bacteriaIndex = randomIndex - indexCounter;
                if(bacteriaIndex > 0) bacteriaIndex -= 1;
                break forloop;
            }
        }

        Microhabitat randMicroHab = microhabitats[microHabIndex];
        int S = randMicroHab.getS(); double K_prime = randMicroHab.getK_prime(), c = randMicroHab.getC();
        Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

        double migRate = randBac.getB();
        double deaRate = randBac.getD();
        double repliRate = randBac.replicationRate_Nutrients(c, K_prime, S);
        //System.out.println(S);
        double R_max = 1.2;
        double rando = rand.nextDouble()*R_max;

        if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
        else if(rando >= migRate && rando < (migRate + deaRate)) death(microHabIndex, bacteriaIndex);
        else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate)) replicate(microHabIndex, bacteriaIndex);

        timeElapsed += 1./((double)getCurrentPopulation()*R_max);
    }


    public static void displayPopulationNumbers(){

        int L = 500;
        int K = 100;
        int S = 100;
        double alpha = 0.05;

        BioSystem bs = new BioSystem(L, K, S, alpha);

        int counter = 0;

        while(true){

            bs.performAction();

            if(counter%1000 == 0){
                for(int i = 0; i < L; i++){
                    if(bs.microhabitats[i].getN() > bs.microhabitats[i].getK()) System.out.println("Overflow "+i+
                            ": "+bs.microhabitats[i].getN());
                }
            }

            counter++;
        }
    }


    public static void timeTilTotalResistance(){

        int L = 500;
        int K = 100;
        int S = 100;
        //no. of repetitions the experiments are averaged over
        int nReps = 1;

        double initAlpha = 0.001;
        double finalAlpha = 0.1;
        double increment = 0.002;

        int noOfPoints = (int)((finalAlpha - initAlpha)/increment);
        ArrayList<Double> alphaVals = new ArrayList<Double>(noOfPoints);
        ArrayList<Double> tVals = new ArrayList<Double>(noOfPoints);


        for(double alpha = initAlpha; alpha < finalAlpha; alpha+=increment){

            double runningTotal = 0.;
            for(int i = 0; i < nReps; i++) {

                BioSystem bs = new BioSystem(L, K, S, alpha);

                while(!bs.getResistanceReached()) {
                    bs.performAction();
                }

                runningTotal += bs.getTimeElapsed();
                System.out.println("Current repition: "+i);
            }

            alphaVals.add(alpha);
            tVals.add(runningTotal/(double)nReps);
            System.out.println("Time taken for alpha = "+alpha+": "+runningTotal/(double)nReps);
        }


        Toolbox.write2ArrayListsToFile(alphaVals, tVals, "immunityTimes_newMu_2");
        System.out.println("Experiment complete");
    }



    public static void mutationalPathLengthExperiment(){

        int L = 300;
        int K = 100;
        double alpha = 0.07;
        double c = 0.9;

        //initial and final values for mutational path length
        int initMutPL = 2;
        int finalMutPL = 16;
        int nVals = finalMutPL - initMutPL;
        int nReps = 10;

        String filename_exp = "TTR_f(m)_alpha";
        String filename_const = "TTR_f(m)_const";

        ArrayList<Double> mVals_exp = new ArrayList<Double>(nVals);
        ArrayList<Double>  tVals_exp = new ArrayList<Double>(nVals);
        ArrayList<Double>  mVals_const = new ArrayList<Double>(nVals);
        ArrayList<Double>  tVals_const = new ArrayList<Double>(nVals);

        for(int m = initMutPL; m < finalMutPL; m++){

            double t_exp = 0.;
            double t_const = 0.;

            for(int i = 0; i < nReps; i++){
                BioSystem bs_exp = new BioSystem(L, K, alpha, m);
                BioSystem bs_const = new BioSystem(L, K, alpha, c, m);

                while(!bs_exp.getResistanceReached()) bs_exp.performAction();
                t_exp += bs_exp.getTimeElapsed();
                System.out.println("m = "+m+" rep = "+i+" exp");

                while(!bs_const.getResistanceReached()) bs_const.performAction();
                t_const += bs_const.getTimeElapsed();
                System.out.println("m = "+m+" rep = "+i+" const");
            }

            mVals_exp.add((double)m);
            tVals_exp.add(t_exp/(double)nReps);

            mVals_const.add((double)m);
            tVals_const.add(t_const/(double)nReps);
        }

        Toolbox.write2ArrayListsToFile(mVals_exp, tVals_exp, filename_exp);
        Toolbox.write2ArrayListsToFile(mVals_const, tVals_const, filename_const);
    }


}
