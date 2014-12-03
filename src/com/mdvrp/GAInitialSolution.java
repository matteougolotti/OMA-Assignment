package com.mdvrp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.TabuSearch.MySolution;
import com.softtechdesign.ga.ChromStrings;
import com.softtechdesign.ga.Chromosome;
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
		super(  instance.getCustomersNr()+instance.getVehiclesNr()-1, //size of chromosome (number of customers + number of vehicles)
				instance.getCustomersNr()+instance.getVehiclesNr()-1, //population has N chromosomes (eventualmente parametrizzabile)
				//togliamo il primo veicolo che utilizziamo
                0.7, //crossover probability
                10, //random selection chance % (regardless of fitness)
                200, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                genes, //gene space (possible gene values)
                Crossover.ctOnePoint, //crossover type
                true); //compute statisitics?
	}

	
	/**
     * Create random chromosomes from the given gene space.
     * 
     * TODO we must rewrite it, so that there are no gene repetitions in the initial population
     */
    protected void initPopulation()
    {
        for (int i=0; i < populationDim; i++)
        {
        	Map used_chromosomes = new HashMap();//mappa per memorizzare i cromosomi usati
        	for (int j=0; j < chromosomeDim; j++){
        		String gene = String.valueOf(getRandom(chromosomeDim));//get a random gene
        		
        		while(used_chromosomes.containsKey(gene)){// check while is not find a unused gene
        			int g = Integer.parseInt(gene);//converto in int
        			
        			if(g>= chromosomeDim)//controllo che non sia il gene più grande(nCustomer+nVeichles)
        				g=0;//riparto da 0
        			else
        				g++;//incremento
        			
        		gene = String.valueOf(g);//riconverto in stringa	
        		}
        		used_chromosomes.put(gene, 1);//metto il gene usato nella mappa
        			
                this.getChromosome(i).setGene(gene,j);//old :((ChromStrings)this.chromosomes[i]).setGene(getRandomGeneFromPossGenes(), j);
        	}
          //this.chromosomes[i].fitness = getFitness(i);
        }
    }
	
	/**
	 * TODO we must rewrite this so that the crossover does not produces gene repetitions
	 * in the offsprings.
	 */
	@Override
	protected void doOnePtCrossover(Chromosome Chrom1, Chromosome Chrom2)
    {
		if(false)//Probability50())
		{
			/* crossover 1 fixed routes change only customers order
			 * select substrings between vehicles genes and then mix them
			 *
			 */
			
		}
		else
		{
			/* crossover 2 change routes and customers
			 * select 1 Crossover point and then mix genes
			 * */
			int iCrossoverPoint = getRandom(chromosomeDim-2);
	        String gene1 = ((ChromStrings)Chrom1).getGene(iCrossoverPoint);
	        String gene2 = ((ChromStrings)Chrom2).getGene(iCrossoverPoint);

	                // CREATE OFFSPRING ONE
	        ((ChromStrings)Chrom1).setGene(gene2, iCrossoverPoint);

	                // CREATE OFFSPRING TWO
	        ((ChromStrings)Chrom2).setGene(gene1, iCrossoverPoint);
		}
        /**/
    }
	
	private boolean Probability50() {
		// TODO Auto-generated method stub
		return (System.currentTimeMillis()%2 == 0)?true:false;
	}
	private int getRandom(int range)
	{
		Random generator = new Random(System.currentTimeMillis());
		int a = generator.nextInt(range-1)+1 ;
		if(a==0)
			return a;
		return a;
	}

	/**
	 * TODO this method must compute the optimal routes, give the chosen customers order. 
	 * 
	 * @param chromosome
	 * @return
	 */
	@Override
	protected double getFitness(int chromosomeIndex) {
		String []chromosome = this.getChromosome(chromosomeIndex).getGenes();
		//String[] solution = computeBestRoutes(chromosome);
		MySolution mySolution = new MySolution(MDVRPTWGA.instance);
		
		return mySolution.getCost().getTotalCost();
	}

}
