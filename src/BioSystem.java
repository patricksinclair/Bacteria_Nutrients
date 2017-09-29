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
    private boolean populationDead = false;

    private int highestGenotypeReached = 0;

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


    public BioSystem(int L, int K, int S, int finalM, double alpha, double d){

        this.L = L;
        this.K = K;
        this.S = S;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){
            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i, S);
        }
        microhabitats[0].fillWithWildType(finalM, d);
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

    public boolean getPopulationDead(){return populationDead;}
    public void setPopulationDead(boolean populationDead){this.populationDead = populationDead;}

    public int getHighestGenotypeReached(){return highestGenotypeReached;}

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
        //the bacterium which is going to be replicated and its associated properties
        Bacteria parentBac = microhabitats[currentL].getBacteria(bacteriumIndex);
        int m = parentBac.getM();
        int finalM = parentBac.getFinalM();
        double d = parentBac.getD();

        Bacteria childBac = new Bacteria(m, finalM, d);

        //these are used to determine whether or not the replicated bacterium is a mutant
        double mu = parentBac.getMu();
        double s = rand.nextDouble();

        if(s < mu/2.) {
            parentBac.increaseGenotype();
            childBac.increaseGenotype();

            if(childBac.getM() > highestGenotypeReached) highestGenotypeReached = childBac.getM();
            if(childBac.getM() == childBac.getFinalM()) resistanceReached = true;

        } else if(s >= mu/2. && s < mu) {
            parentBac.decreaseGenotype();
            childBac.decreaseGenotype();
        }

        microhabitats[currentL].addABacterium(childBac);
    }



    public void performAction(){

        //selects a random bacteria from the total population
        if(!populationDead) {

            int randomIndex = rand.nextInt(getCurrentPopulation());
            int indexCounter = 0;
            int microHabIndex = 0;
            int bacteriaIndex = 0;

            forloop:
            for(int i = 0; i < getL(); i++) {

                //added <= now get Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: bound must be positive
                if((indexCounter + microhabitats[i].getN()) <= randomIndex) {

                    indexCounter += microhabitats[i].getN();
                    continue forloop;
                } else {
                    microHabIndex = i;
                    bacteriaIndex = randomIndex - indexCounter;
                    //if(bacteriaIndex > 0) bacteriaIndex -= 1;
                    break forloop;
                }

            }

            Microhabitat randMicroHab = microhabitats[microHabIndex];
            int S = randMicroHab.getS();
            double K_prime = randMicroHab.getK_prime(), c = randMicroHab.getC();
            Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

            double migRate = randBac.getB();
            double deaRate = randBac.getD();
            double repliRate = randBac.replicationRate_Nutrients(c, K_prime, S);
            double R_max = 1.2;
            double rando = rand.nextDouble()*R_max;

            if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
            else if(rando >= migRate && rando < (migRate + deaRate)) death(microHabIndex, bacteriaIndex);
            else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate))
                replicate(microHabIndex, bacteriaIndex);

            timeElapsed += 1./((double)getCurrentPopulation()*R_max);
            if(getCurrentPopulation() == 0) populationDead = true;
        }
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



    public static void nutrientHeatMap(){

        int nPoints = 20, nReps = 10;
        String filename = "nutrients_vs_deathRate";

        int L = 500, K = 100;
        int finalM = 6; double alpha = 0.02;

        ArrayList<Double> dVals = new ArrayList<Double>();
        ArrayList<Double> sVals = new ArrayList<Double>();
        ArrayList<Double> mVals = new ArrayList<Double>();

        double initD = 0.01, finalD = 0.1;
        double dIncrement = (finalD - initD)/(double)nPoints;

        int initS = 100, finalS = 1500;
        int SIncrement = (finalS - initS)/nPoints;


        for(int i = 0; i < nPoints; i++){

            double avgMaxGenotype = 0.;
            boolean recordedD = false, recordedS = false;

            ArrayList<Double> tempMVals = new ArrayList<Double>();

            for(int r = 0; r < nReps; r++){
            //actual experiment
            ////////////////////////////////////////////////////////////////////////////////////////////
                for(double d = initD; d <= finalD; d+=dIncrement){
                    if(!recordedD) dVals.add(d);
                    recordedD = true;

                    for(int S = initS; S <= finalS; S+=SIncrement){
                        if(!recordedS) sVals.add((double)S);
                        recordedS = true;

                        BioSystem bs = new BioSystem(L, K, S, finalM, alpha, d);

                        whileloop:
                        while(true){
                            bs.performAction();
                            if(bs.getResistanceReached() || bs.getPopulationDead()) break whileloop;
                        }

                        tempMVals.add((double)bs.getHighestGenotypeReached());
                    }
                }

                avgMaxGenotype += tempMVals.stream().mapToDouble(val -> val).average().getAsDouble();
             ////////////////////////////////////////////////////////////////////////////////////////////
            }
            mVals.add(avgMaxGenotype/(double)nReps);
        }

        Toolbox.writeThreeArraylistsToFile(dVals, sVals, mVals, filename);
    }
}
