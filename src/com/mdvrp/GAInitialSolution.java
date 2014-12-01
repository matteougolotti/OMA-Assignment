package com.mdvrp;

import com.TabuSearch.MySolution;
import com.softtechdesign.ga.Crossover;
import com.softtechdesign.ga.GAException;
import com.softtechdesign.ga.GAStringsSeq;

public class GAInitialSolution extends GAStringsSeq {
	
	/**
     * Initialize the GAStringSeq
     * @param chromosomeDim
     * @param populationDim
     * @param crossoverProb
     * @param randomSelectionChance
     * @param maxGenerations
     * @param numPrelimRuns
     * @param maxPrelimGenerations
     * @param mutationProb
     * @param chromDecPts
     * @param possGeneValues
     * @param crossoverType
     * @param computeStatistics
     * @throws GAException
     */
	public GAInitialSolution(String genes[], Instance instance) throws GAException {
		super(  20, //size of chromosome (number of customers + number of vehicles)
                300, //population has N chromosomes (eventualmente parametrizzabile)
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                2000, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                genes, //gene space (possible gene values)
                Crossover.ctTwoPoint, //crossover type
                true); //compute statisitics?
	}

	
	/**
     * Create random chromosomes from the given gene space.
     * 
     * TODO we must rewrite it, so that there are no gene repetitions in the initial population
     */
    /*protected void initPopulation()
    {
        for (int i=0; i < populationDim; i++)
        {
          for (int j=0; j < chromosomeDim; j++)
                ((ChromStrings)this.chromosomes[i]).setGene(getRandomGeneFromPossGenes(), j);
          this.chromosomes[i].fitness = getFitness(i);
        }
    }*/
	
	/**
	 * TODO we must rewrite this so that the crossover does not produces gene repetitions
	 * in the offsprings.
	 */
	/*@Override
	protected void doOnePtCrossover(Chromosome Chrom1, Chromosome Chrom2)
    {
        int iCrossoverPoint = getRandom(chromosomeDim-2);
        String gene1 = ((ChromStrings)Chrom1).getGene(iCrossoverPoint);
        String gene2 = ((ChromStrings)Chrom2).getGene(iCrossoverPoint);

                // CREATE OFFSPRING ONE
        ((ChromStrings)Chrom1).setGene(gene2, iCrossoverPoint);

                // CREATE OFFSPRING TWO
        ((ChromStrings)Chrom2).setGene(gene1, iCrossoverPoint);
    }*/
	
	/**
	 * TODO this method must compute the optimal routes, give the chosen customers order. 
	 * 
	 * @param chromosome
	 * @return
	 */
	private String[] computeBestRoutes(String[] chromosome){
		return null;
	}
	
	@Override
	protected double getFitness(int chromosomeIndex) {
		String []chromosome = this.getChromosome(chromosomeIndex).getGenes();
		String[] solution = computeBestRoutes(chromosome);
		MySolution mySolution = new MySolution(MDVRPTWGA.instance, solution);
		
		return mySolution.getCost().getTotalCost();
	}

}
