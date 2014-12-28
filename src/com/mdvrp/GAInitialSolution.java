package com.mdvrp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
		super(  instance.getCustomersNr()+instance.getVehiclesNr()-2, //size of chromosome (number of customers + number of vehicles - 1 - the first vehicle)
				300, //population has N chromosomes (eventualmente parametrizzabile)
				//togliamo il primo veicolo che utilizziamo
                0.7, //crossover probability
                10,//0, //random selection chance % (regardless of fitness)
                60, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.1,//0.06, //chromosome mutation prob.
                0, //number of decimal places in chrom
                genes, //gene space (possible gene values)
                Crossover.ctTwoPoint,//ctTwoPoint, //crossover type
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
        			
                this.getChromosome(i).setGene(String.valueOf(gene),j);
        	}
        }
    	
    	for (i=populationDim/2; i < populationDim; i++)
        {
    		this.buildInitialPopulationWithNearestNeighbor(i);
        }
    }
    
    private int getAvailableNearestNeighbor(int startLocation, boolean alreadyServed[]){
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
		
		return nearestNeighbor;
	}
    
    private class MyRouteCost{
    	public double load = 0;
    	public double distanceTraveled = 0;
    }
    
    private void buildInitialPopulationWithNearestNeighbor(int chrIndex){
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
			if(geneIndex == 0){
				//Primo inserimento nel cromosoma
				chosenCustomerGene = getAvailableNearestNeighbor(MDVRPTWGA.instance.getCustomersNr(), customerAlreadyServed);
				firstGene = true;
			}else {
				if(Integer.valueOf(this.getChromosome(chrIndex).getGene(geneIndex-1))> numberOfCustomers){
					chosenCustomerGene = getAvailableNearestNeighbor(MDVRPTWGA.instance.getCustomersNr(), customerAlreadyServed);
					firstGene = true;
				}else{
					chosenCustomerGene = getAvailableNearestNeighbor(Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1)), customerAlreadyServed);
					firstGene = false;
				}
			}
			
			Customer customer = null;
			for(int j=0; j<numberOfCustomers; j++){
				if(MDVRPTWGA.instance.getDepot(0).getAssignedCustomer(j).getNumber() == chosenCustomerGene){
					customer = MDVRPTWGA.instance.getDepot(0).getAssignedCustomer(j);
				}	
			}
			
			String chosenCustomer = String.valueOf(chosenCustomerGene);
			if(firstGene)
			{
				getChromosome(chrIndex).setGene(chosenCustomer, geneIndex);
				geneIndex++;
				customerAlreadyServed[chosenCustomerGene] = true;
				myRouteCost.load += customer.getCapacity();
				myRouteCost.distanceTraveled += MDVRPTWGA.instance.getTravelTime(Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1)), chosenCustomerGene);
				i++;
			}else{
				if(routeWouldBeFeasible(myRouteCost, vehicleNumber, Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1)), chosenCustomerGene)){
					//se la rotta non è finita
					this.getChromosome(chrIndex).setGene(chosenCustomer, geneIndex);
					geneIndex++;
					customerAlreadyServed[chosenCustomerGene] = true;
					myRouteCost.load += customer.getCapacity();
					myRouteCost.distanceTraveled += MDVRPTWGA.instance.getTravelTime(Integer.valueOf(getChromosome(chrIndex).getGene(geneIndex-1)), chosenCustomerGene);
					i++;
				}else{//se la rotta è finita
					myRouteCost.load = 0;
					myRouteCost.distanceTraveled = 0;
					vehicleNumber++;
					this.getChromosome(chrIndex).setGene(String.valueOf(vehicleNumber),geneIndex);
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
    	
    	Customer customer = null;
		for(int j=0; j<instance.getCustomersNr(); j++){
			if(MDVRPTWGA.instance.getDepot(0).getAssignedCustomer(j).getNumber() == newCustomerIndex){
				customer = MDVRPTWGA.instance.getDepot(0).getAssignedCustomer(j);
			}	
		}
    	
    	cost.load += customer.getCapacity();
    	cost.distanceTraveled += instance.getTravelTime(lastCustomerIndex, newCustomerIndex);
    	if(cost.load >= MaxLoad || cost.distanceTraveled >= MaxTime){
    		return false;
    	}else{
    		return true;
    	}	
    }
    
	private int myGetRandom(int range)//tra 0 e range
	{
		Random generator = new Random( (long) (Math.random()*System.currentTimeMillis()));
		int a = generator.nextInt(range);
		return a;
	}
	
	@Override
	protected void doRandomMutation(int iChromIndex){//inverte casualmente due geni di un cromosoma
		
		int FirstGene = myGetRandom(chromosomeDim);
		
		int SecondGene =  myGetRandom(chromosomeDim);
		
		while(SecondGene == FirstGene)
			SecondGene =  myGetRandom(chromosomeDim);
		
		String temp = this.getChromosome(iChromIndex).getGene(FirstGene);
		
		this.getChromosome(iChromIndex).setGene(this.getChromosome(iChromIndex).getGene(SecondGene), FirstGene);
		this.getChromosome(iChromIndex).setGene(temp, SecondGene);
	}
	
	/*@Override //doTwoPtCrossover di Matteo simile ad una mutazione
	protected void doTwoPtCrossover(Chromosome Chrom1, Chromosome Chrom2){
		ChromStrings chr1 = (ChromStrings)Chrom1;
		ChromStrings chr2 = (ChromStrings)Chrom2;
		
		//ipotesi farlo per più volte
		
		int t1 = myGetRandom(chromosomeDim-2);
		
		String s = chr1.getGene(t1);
		int t2 = 0;
		for(int i = 0; i<chromosomeDim; i++){
			if(chr2.getGene(i).equals(s)){//t2->i
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
	}*/
	
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
		
		return 1000000 - f.getFitness(GAResultInt, MDVRPTWGA.instance.getCustomersNr());
		
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
			
			this.getChromosome(i).setFitness(1/f.getFitness(GAResultInt, MDVRPTWGA.instance.getCustomersNr()));
		}
		
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.softtechdesign.ga.GAStringsSeq#doTwoPtCrossover(com.softtechdesign.ga.Chromosome, com.softtechdesign.ga.Chromosome)
	 * idea: crossover che tenga conto delle route, mescolo le rotte e prendo quelle col costo minore(off1) e maggiore(off2) 
	 */
	protected void doTwoPtCrossover(Chromosome Chrom1, Chromosome Chrom2){	
		
		//lista delle rotte
		List<Set<String>> routes = new ArrayList<Set<String>>();
		routes.add(0, new HashSet<String>());
		routes.add(1, new HashSet<String>());
		
		//lista dei costi
		List<Double> routesCosts = new ArrayList<Double>();
		routesCosts.add(0, 0.0);
		routesCosts.add(1, 0.0);
		
		ChromStrings par1 = (ChromStrings)Chrom1;
		ChromStrings par2 = (ChromStrings)Chrom2;
		
		//creo una lista con le route dei due parenti ed una con il loro costo
		boolean inRoutePar1 = false;
		boolean inRoutePar2 = false;
		int routesIndex = 1;
		int routeIndexPar1 = 0;
		int routeIndexPar2 = 1;
		
		for(int i = 0; i<chromosomeDim; i++)
		{
			//controllo se sono in una route oppure no
			if(Integer.valueOf(par1.getGene(i))>=MDVRPTWGA.instance.getCustomersNr())
			{
				//calcolo il costo della route precedente
				GAFitnessFunction f = new GAFitnessFunction();
				
				//creazione array di int
				Object [] routesObj = routes.get(routeIndexPar1).toArray();
				int [] routesInt = new int[routesObj.length];
				for(int j = 0 ; j< routesObj.length; j++)
				{
					routesInt[j] =Integer.parseInt(String.valueOf(routesObj[j]));
				}
				
				Double cost = 1/f.getFitness(routesInt, MDVRPTWGA.instance.getCustomersNr());
				routesCosts.add(routeIndexPar1, cost);
				//non sono in una route quindi creo una nuova route di par1
				inRoutePar1 = false;
				routesIndex ++;
				routeIndexPar1 = routesIndex;	
				routes.add(routesIndex, new HashSet<String>());
			}
			else//sono in una route
				inRoutePar1 = true;
			
			if(Integer.valueOf(par2.getGene(i))>=MDVRPTWGA.instance.getCustomersNr())
			{
				//calcolo il costo della route precedente
				GAFitnessFunction f = new GAFitnessFunction();
				
				//creazione array di int
				Object [] routesObj = routes.get(routeIndexPar2).toArray();
				int [] routesInt = new int[routesObj.length];
				for(int j = 0 ; j< routesObj.length; j++)
				{
					routesInt[j] =Integer.parseInt(String.valueOf(routesObj[j]));
				}
				
				Double cost = 1/f.getFitness(routesInt, MDVRPTWGA.instance.getCustomersNr());
				routesCosts.add(routeIndexPar2, cost);
				
				//non sono in una route quindi creo una nuova route di par2
				inRoutePar2 = false;
				routesIndex ++;
				routeIndexPar2 = routesIndex;
				routes.add(routesIndex, new HashSet<String>());
			}
			else//sono in una route
				inRoutePar2 = true;
			
			if(inRoutePar1)
			{
				//aggiungo client alla route di par1 e aggiorno il costo
				routes.get(routeIndexPar1).add(par1.getGene(i));
			}
			if(inRoutePar2)
			{
				//aggiungo client alla route di par2  e aggiorno il costo
				routes.get(routeIndexPar2).add(par2.getGene(i));
			}
		}
		
		//ordinamento routes per costo crescente e creazione off1 assemblando le routes
		SortAscendent(routes,routesCosts);	
		Chrom1 = CreateOffspring((ChromStrings)Chrom1,routes);
		
		//ordinamento routes per costo decrescente e creazione off2 assemblando le routes
		SortDescendent(routes,routesCosts);
		Chrom2 = CreateOffspring((ChromStrings)Chrom2,routes);
	}

	private ChromStrings CreateOffspring(ChromStrings off1, List<Set<String>> routes) {
		// TODO Auto-generated method stub
		int customers = 0;
		int routeIndex = 0;
		int geneIndex = 0;
		int veichle = MDVRPTWGA.instance.getCustomersNr();
		Set<String> used = new HashSet<String>();
		while(customers<MDVRPTWGA.instance.getCustomersNr() && routeIndex<routes.size())
		{
			int conta = 0;//flag contatore duplicati
			
			if(routes.get(routeIndex).size()<1)
				conta++;
			for(int i=0; i<routes.get(routeIndex).size() && conta<1;i++)//controllo se la route contiene dei custumers utilizzati
			{
				if(used.contains(routes.get(routeIndex).toArray()[i]))
					conta++;
			}
			if(conta <1)//se non ho ancora inserito nessun dei customer aggiungo la rotta
			{
				for(int i=0; i<routes.get(routeIndex).size();i++)
				{
					String gene = (String) routes.get(routeIndex).toArray()[i];
					off1.setGene(gene, geneIndex);
					used.add(gene);
					geneIndex++;
					customers++;
				}
				if(veichle < chromosomeDim)//se non sono nell'ultima rotta aggiungo il prossimo veicolo
				{	
					off1.setGene(String.valueOf(veichle), geneIndex);
					geneIndex++;
					veichle++;
				}
							
			}
			routeIndex++;
			//altrimenti vado avanti e uso un'altra route
		}
		//quando esco inserisco i customers e i veicoli rimanenti
		if(customers<MDVRPTWGA.instance.getCustomersNr())//controllo di aver messo tutti i customer
		{
			for(int i=0; i<MDVRPTWGA.instance.getCustomersNr();i++)
			{
				String gene = String.valueOf(i);
				if(!used.contains(gene))
				{
					off1.setGene(gene, geneIndex);
					geneIndex++;
					customers++;
				}	
			}
		}
		if(veichle<chromosomeDim)//controllo di aver messo tutti i veicoli
		{
			while(veichle<chromosomeDim)
			{
				off1.setGene(String.valueOf(veichle), geneIndex);
				geneIndex++;
				veichle++;
			}	
		}
		return off1;
	}


	private void SortAscendent(List<Set<String>> routes, List<Double> routesCosts) {
		// TODO Auto-generated method stub
		for(int i = 0; i<routes.size() ; i++)
		{
			for(int j = 0; j< routes.size() ; j++)
			{
				
				if(routesCosts.get(i)<routesCosts.get(j))
				{
					//swap costs
					Double tmp1 = routesCosts.get(i);
					routesCosts.set(i, routesCosts.get(j));
					routesCosts.set(j, tmp1);
					//swap routes
					Set<String> tmp2 = new HashSet<String>();
					tmp2 = (routes.get(i));
					routes.set(i, routes.get(j));
					routes.set(j, tmp2);
				}
			}
		}
	}
	private void SortDescendent(List<Set<String>> routes, List<Double> routesCosts) {
		// TODO Auto-generated method stub
		for(int i = 0; i<routes.size() ; i++)
		{
			for(int j = 0; j< routes.size() ; j++)
			{
				
				if(routesCosts.get(i)>routesCosts.get(j))
				{
					//swap costs
					Double tmp1 = routesCosts.get(i);
					routesCosts.set(i, routesCosts.get(j));
					routesCosts.set(j, tmp1);
					//swap routes
					Set<String> tmp2 = new HashSet<String>();
					tmp2 = (routes.get(i));
					routes.set(i, routes.get(j));
					routes.set(j, tmp2);
				}
			}
		}
	}
	
	
}
