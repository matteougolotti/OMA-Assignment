package com.mdvrp;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
	private int countCross = 0;

	public GAInitialSolution(String genes[], Instance instance) throws GAException {
		super(  instance.getCustomersNr()+instance.getVehiclesNr()-1, //size of chromosome (number of customers + number of vehicles)
				300, //population has N chromosomes (eventualmente parametrizzabile)
				//togliamo il primo veicolo che utilizziamo
                0.7, //crossover probability
                0, //random selection chance % (regardless of fitness)
                30, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                genes, //gene space (possible gene values)
                Crossover.ctOnePoint,//ctTwoPoint, //crossover type
                true); //compute statisitics?
	}

	
	/**
     * Create random chromosomes from the given gene space.
     */
    protected void initPopulation()
    {
    	int i,j;
    	int gene = 0;
    	int VehiclesNr = MDVRPTWGA.instance.getVehiclesNr();
    	int CustomersNr = MDVRPTWGA.instance.getCustomersNr();
    	
    	for (i=0; i < populationDim/2; i++)//cromosomi creati casualmente
        {
        	Set<Integer> used = new HashSet<Integer>(); //mappa per memorizzare i cromosomi usati

        	for (j = 0; j < chromosomeDim; j++){//ciclo per assegnare i geni rimanenti
        		
        		gene = myGetRandom(chromosomeDim);//get a random gene
        		
        		int prec_gene = 0; 
        		
        		if(j>0) 
        			prec_gene = Integer.valueOf(this.getChromosome(i).getGene(j-1));
        		if(prec_gene>VehiclesNr)//se il gene precedente era un veicolo il primo random lo scelgo tra i customer
        		{
        			gene=myGetRandom(CustomersNr);
        		}

        		while(used.contains(gene)){// check while is not find a unused gene
        			if(gene>=chromosomeDim)
        				gene=myGetRandom(chromosomeDim);
        			else
        				gene++;
        		}
        		used.add(gene);//metto il gene usato nella mappa
        			
                this.getChromosome(i).setGene(String.valueOf(gene),j);//old :((ChromStrings)this.chromosomes[i]).setGene(getRandomGeneFromPossGenes(), j);
        	}
        }
    	
    	for (i=populationDim/2; i < populationDim; i++)//cromosomi creati con algoritmo cluster
        {
    		this.buildinitialPopulationWithNearestNeighbor(i);
        }
    }
    
    private int getAvailableNearestNeighbor(int startLocation, boolean alreadyServed[]){//ritorna il gene
		int nearestNeighbor = -1;
		Instance instance = MDVRPTWGA.instance;
		
		for(int i=0; i<instance.getCustomersNr(); i++){
			if(alreadyServed[i] == false){
				nearestNeighbor = i;
				break;
			}
		}
		
		for(int currentNeighbor=0; currentNeighbor<instance.getCustomersNr(); currentNeighbor++){
			if(!alreadyServed[currentNeighbor]){
				if(instance.getTravelTime(startLocation, currentNeighbor) < instance.getTravelTime(startLocation, nearestNeighbor)){
				nearestNeighbor = currentNeighbor;
				}
			}
		}
		
		return nearestNeighbor+1;
	}
    
    private class MyRouteCost{
    	public double load = 0;
    	public double distanceTraveled = 0;
    }
    
    private void buildinitialPopulationWithNearestNeighbor(int chrIndex){
    	int numberOfCustomers = MDVRPTWGA.instance.getDepot(0).getAssignedcustomers().size();
		boolean[] customerAlreadyServed = new boolean[numberOfCustomers];
		boolean firstGene = false;
		MyRouteCost myRouteCost = new MyRouteCost();
		
		for(int i=0; i<numberOfCustomers; i++){
			customerAlreadyServed[i] = false;
		}
		
		int vehicleNumber = MDVRPTWGA.instance.getCustomersNr();
		int geneIndex=0;
		int chosenCustomerGene;
		for(int i=0; i<numberOfCustomers;){
			firstGene = false;
			if(geneIndex == 0){
				//Primo inserimento nel cromosoma
				chosenCustomerGene = getAvailableNearestNeighbor(MDVRPTWGA.instance.getCustomersNr(), customerAlreadyServed);
				firstGene = true;
			}else {
				if(Integer.valueOf(this.getChromosome(chrIndex).getGene(geneIndex-1))> numberOfCustomers){
					chosenCustomerGene = getAvailableNearestNeighbor(MDVRPTWGA.instance.getCustomersNr(), customerAlreadyServed);
					firstGene = true;
				}else{
					chosenCustomerGene = getAvailableNearestNeighbor(Integer.valueOf(this.getChromosome(chrIndex).getGene(geneIndex-1)), customerAlreadyServed);
				}
			}
			
			String chosenCustomer = String.valueOf(chosenCustomerGene);
			if(firstGene)
			{
				this.getChromosome(chrIndex).setGene(chosenCustomer, geneIndex);//inserisce nel chromosoma
				geneIndex++;
				customerAlreadyServed[chosenCustomerGene-1] = true;
				myRouteCost.load += MDVRPTWGA.instance.getDepot(0).getAssignedcustomers().get(chosenCustomerGene-1).getCapacity();
				myRouteCost.distanceTraveled += MDVRPTWGA.instance.getTravelTime(Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1))-1, chosenCustomerGene-1);
				i++;
			}else{
				if(routeWouldBeFeasible(myRouteCost, vehicleNumber, Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1))-1, chosenCustomerGene -1)){
					//se la rotta non è finita
					this.getChromosome(chrIndex).setGene(chosenCustomer, geneIndex);//inserisce nel chromosoma
					geneIndex++;
					customerAlreadyServed[chosenCustomerGene-1] = true;
					myRouteCost.load += MDVRPTWGA.instance.getDepot(0).getAssignedcustomers().get(chosenCustomerGene-1).getCapacity();
					myRouteCost.distanceTraveled += MDVRPTWGA.instance.getTravelTime(Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1))-1, chosenCustomerGene-1);
					i++;
				}else{//se la rotta è finita
					myRouteCost.load = 0;
					myRouteCost.distanceTraveled = 0;
					vehicleNumber++;
					this.getChromosome(chrIndex).setGene(String.valueOf(vehicleNumber),geneIndex);//inserisce nel chromosoma
					geneIndex++;
				}
			}
			
		}
		
		for(int i=geneIndex; i<chromosomeDim; i++){
			vehicleNumber++;
			this.getChromosome(chrIndex).setGene(String.valueOf(vehicleNumber), i);
		}
		
    }
    
    /**
     * Checks if, by adding customer to the vehicle, the route stays feasible.
     * @param customer
     * @return true if the route stay feasible, false otherwise
     */
    private boolean routeWouldBeFeasible(MyRouteCost cost, int vehicle, int lastCustomerIndex, int newCustomerIndex){
    	Instance instance = MDVRPTWGA.instance;
    	int MaxTime = MDVRPTWGA.instance.getDepot(0).getEndTw();
    	double MaxLoad = instance.getCapacity(0, 0);
    	
    	cost.load += MDVRPTWGA.instance.getDepot(0).getAssignedcustomers().get(newCustomerIndex).getCapacity();
    	cost.distanceTraveled += instance.getTravelTime(lastCustomerIndex, newCustomerIndex);
    	if(cost.load >= MaxLoad || cost.distanceTraveled >= MaxTime){
    		return false;
    	}else{
    		return true;
    	}	
    }
    
	private int myGetRandom(int range)//tra 1 e range
	{
		Random generator = new Random( (long) (Math.random()*System.currentTimeMillis()));
		int a = generator.nextInt(range-1)+1 ;
		return a;
	}
	
	@Override
	protected void doRandomMutation(int iChromIndex){//inverte casualmente due geni di un cromosoma
		
		int FirstGene = myGetRandom(chromosomeDim)-1;
		
		int SecondGene =  myGetRandom(chromosomeDim)-1;
		
		while(SecondGene == FirstGene)
			SecondGene =  myGetRandom(chromosomeDim)-1;
		
		String temp = this.getChromosome(iChromIndex).getGene(FirstGene);
		
		this.getChromosome(iChromIndex).setGene(this.getChromosome(iChromIndex).getGene(SecondGene), FirstGene);
		this.getChromosome(iChromIndex).setGene(temp, SecondGene);
	}
	
	@Override
	protected void doTwoPtCrossover(Chromosome Chrom1, Chromosome Chrom2){
		ChromStrings chr1 = (ChromStrings)Chrom1;
		ChromStrings chr2 = (ChromStrings)Chrom2;
		
		/*ipotesi farlo per più volte*/
		
		int t1 = myGetRandom(chromosomeDim-2);
		
		String s = chr1.getGene(t1);
		int t2 = 0;
		for(int i = 0; i<chromosomeDim; i++){
			if(chr2.getGene(i).equals(s)){/*t2->i*/
				t2 = i;
				break;
			}
		}
		
		s = chr1.getGene(t1);
		chr1.setGene(chr1.getGene(t2), t1);
		chr1.setGene(s, t2);
		
		s = chr2.getGene(t1);
		chr2.setGene(chr2.getGene(t2), t1);
		chr2.setGene(s, t2);
	}
	
	@Override
	protected void doOnePtCrossover(Chromosome Chrom1, Chromosome Chrom2)
	{
		//ALEX DICE: usare hash map
		
		ChromStrings off1 = new ChromStrings(chromosomeDim);
		ChromStrings off2 = new ChromStrings(chromosomeDim);
		ChromStrings par1 = (ChromStrings)Chrom1;
		ChromStrings par2 = (ChromStrings)Chrom2;
		
		int i,j;
		int t = myGetRandom(chromosomeDim-2);//taglio
		
		for(i = 0; i<t;i++)
		{
			off1.setGene(par2.getGene(i), i);
			off2.setGene(par1.getGene(i), i);
		}
		
		j=t;//indice parent
		for(i=t;i<chromosomeDim;i++)//indice dell'offspring
		{
			while(ChromosomeContainsGene(off1,i,par1.getGene(j)))
			{
				if(j>=chromosomeDim-1)
					j=0;
				else
					j++;
			}
			off1.setGene(par1.getGene(j), i);
			
			if(j>=chromosomeDim-1)
				j=0;
			else
				j++;
		}
		j=t;
		for(i=t;i<chromosomeDim;i++)
		{
			while(ChromosomeContainsGene(off2,i,par2.getGene(j)))
			{
				if(j>=chromosomeDim-1)
					j=0;
				else
					j++;
			}
			off2.setGene(par2.getGene(j), i);
			
			if(j>=chromosomeDim-1)
				j=0;
			else
				j++;
		}
		Chrom1 = off1;
		Chrom2 = off2;
			
		synchronized(this){
			countCross++;
		}
		
	}

	private boolean ChromosomeContainsGene(ChromStrings c, int last, String gene)
	{ 
		for(int i = 0;i<last; i++)
		{
			if(c.getGene(i).equals(gene))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected double getFitness(int chromosomeIndex) {
		String []chromosome = this.getChromosome(chromosomeIndex).getGenes();
		//MySolution mySolution = new MySolution(MDVRPTWGA.instance, chromosome);
		GAFitnessFunction f = new GAFitnessFunction();
		int[] GAResultInt = new int[chromosome.length];
		for(int i=0; i<chromosome.length; i++){
			GAResultInt[i] = Integer.valueOf(chromosome[i]);
		}
		
		return 1/f.getFitness(GAResultInt, MDVRPTWGA.instance.getCustomersNr() + 1);
		
		//return 1/mySolution.getCost().getTotalCost();
		//return Math.random();
	}


	public void InitialFittness() {
		for(int i = 0; i< populationDim;i++)
		{
			String []chromosome = this.getChromosome(i).getGenes();
			GAFitnessFunction f = new GAFitnessFunction();
			int[] GAResultInt = new int[chromosome.length];
			for(int j=0; j<chromosome.length; j++){
				GAResultInt[j] = Integer.valueOf(chromosome[j]);
			}
			
			this.getChromosome(i).setFitness(1/f.getFitness(GAResultInt, MDVRPTWGA.instance.getCustomersNr() + 1));
		}
		
	}
	
}
