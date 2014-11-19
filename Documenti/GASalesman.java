package com.softtechdesign.ga.examples;

import com.softtechdesign.ga.*;
import java.util.*;

/**
    Traveling salesman problem. A salesman has to travel to N different cities. In what
  sequence should he visit each city to minimize the total distance traveled. Each city
  is represented in the chromosome string as a letter (e.g. 'A' or 'B').

  To simplify the mathematics of the fitness function, this example reduces the coordinate
  space to one dimension. Each city (or node) has a position given by one coordinate.
  This model can be extrapolated to 2 or 3 dimensions by giving each city (or node) a 2
  dimensional (X,Y) or 3 dimensional (X,Y,Z) coordinate and then modifying the distance
  calculating function accordingly.

  If a chromosome = 'ABCDEFGHIJKLMNOPQRST', then the fitness is evaluated as
    fitness = Dist(A, B) + Dist(B, C) + Dist(C, D)....+ Dist(S, T)
  Higher fitness values mean a higher probability that this chromosome will reproduce.

  The possible combinations (sequences of cities) is N factorial. For 20 cities, the possible
  combinations = 20! or 2.432902008177e+18.
  This is an enormous number. If you tried every combination of sequences and could test
  10,000 of those sequences per second (looking for the minium), it would take your
  computer 8 million years to randomly come up with the minimum (ideal) sequence.
*/

public class GASalesman extends GASequenceList
{
    public GASalesman() throws GAException
    {
        super(  20, //size of chromosome
                300, //population has N chromosomes
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                2000, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                "ABCDEFGHIJKLMNOPQRST", //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statisitics?
        /*
         super(  20, //size of chromosome
                200, //population has N chromosomes
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                750, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                20, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                "ABCDEFGHIJKLMNOPQRST", //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statisitics?
*/
        setInitialSequence();
    }

    void setInitialSequence()
    {
        //initialize one dimensional locations (e.g. traveling salesman's cities)
        for (int i = 0; i < chromosomeDim; i++)
            this.sequence[i] = i + 1;
    }
    
    /** Fitness function for GASalesman now access genes directly through genes[] array
     * Old benchmark: 29 sec. New benchmark 16 sec.
     */
    protected double getFitness(int iChromIndex)
    {
        double rDist, rLocCity1, rLocCity2;
        int geneIndex1, geneIndex2;

        rDist = 0;
        char genes[] = this.getChromosome(iChromIndex).getGenes();
        int lenChromosome = genes.length;
        
        for (int i = 0; i < lenChromosome - 1; i++)
        {
            geneIndex1 = this.possGeneValues.indexOf(genes[i]);  //optimize this
            geneIndex2 = this.possGeneValues.indexOf(genes[i+1]);

            rLocCity1 = this.sequence[geneIndex1];
            rLocCity2 = this.sequence[geneIndex2];
            rDist += Math.abs(rLocCity1 - rLocCity2);
        }

        if (Math.abs(rDist) > 1e-12)
            return (1 / rDist); //this minimizes dist (find shortest dist)
        else
            return (1 / 1e-12);
    }
    
/*    
    protected double getFitness(int iChromIndex)
    {
        double rDist, rLocCity1, rLocCity2;
        int index1, index2;
        String sChromosome, sGene1, sGene2;

        rDist = 0;
        sChromosome = this.getChromosome(iChromIndex).getGenesAsStr();
        int lenChromosome = sChromosome.length();

        for (int i = 0; i < lenChromosome - 1; i++)
        {
            sGene1 = "" + sChromosome.charAt(i);
            index1 = this.possGeneValues.indexOf(sGene1);

            sGene2 = "" + sChromosome.charAt(i + 1);
            index2 = this.possGeneValues.indexOf(sGene2);

            rLocCity1 = this.sequence[index1];
            rLocCity2 = this.sequence[index2];
            rDist += Math.abs(rLocCity1 - rLocCity2);
        }

        if (Math.abs(rDist) > 1e-12)
            return (1 / rDist); //this minimizes dist (find shortest dist)
        else
            return (1 / 1e-12);
    }
*/    
    public static void main(String[] args)
    {
        String startTime = new Date().toString();
        System.out.println("GASalesman GA..." + startTime);
        
        try
        {
            GASalesman salesman = new GASalesman();
            Thread threadSalesman = new Thread(salesman);
            threadSalesman.start();
        }
        catch (GAException gae)
        {
            System.out.println(gae.getMessage());
        }
        
        System.out.println("Process started at " + startTime + ". Process completed at " +  new Date().toString());
    }

}