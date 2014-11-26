package com.mdvrp;

import com.TabuSearch.MySolution;
import com.softtechdesign.ga.Crossover;
import com.softtechdesign.ga.GAException;
import com.softtechdesign.ga.GAStringsSeq;

public class GAInitialSolution extends GAStringsSeq {
	private static Instance instance;
	
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

	@Override
	protected double getFitness(int chromosomeIndex) {
		String []chromosome = this.getChromosome(chromosomeIndex).getGenes();
		MySolution mySolution = new MySolution(MDVRPTWGA.instance, chromosome);
		
		return mySolution.getCost().getTotalCost();
	}

}
