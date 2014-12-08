package com.mdvrp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.coinor.opents.Solution;

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
	//private Instance instance;
	public GAInitialSolution(String genes[], Instance instance) throws GAException {
		super(  instance.getCustomersNr()+instance.getVehiclesNr()-1, //size of chromosome (number of customers + number of vehicles)
				40, //population has N chromosomes (eventualmente parametrizzabile)
				//togliamo il primo veicolo che utilizziamo
                1, //crossover probability
                0, //random selection chance % (regardless of fitness)
                20, //max generations
                0, //num prelim runs (to build good breeding stock for final/full run)
                25, //max generations per prelim run
                0.00, //chromosome mutation prob.
                0, //number of decimal places in chrom
                genes, //gene space (possible gene values)
                Crossover.ctOnePoint,//ctTwoPoint, //crossover type
                true); //compute statisitics?
				//this.instance = new Instance(instance.getParameters());
	}

	
	/**
     * Create random chromosomes from the given gene space.
     * 
     * TODO we must rewrite it, so that there are no gene repetitions in the initial population
     */
	//metodo che se incrementa l'indice se non lo trova anzich� riprovare con un nuovo casuale
    protected void initPopulation()
    {
        for (int i=0; i < populationDim; i++)
        {
        	Set<Integer> used = new HashSet<Integer>(); //mappa per memorizzare i cromosomi usati
        	for (int j=0; j < chromosomeDim; j++){
        		int gene = myGetRandom(chromosomeDim);//get a random gene
        		
        		while(used.contains(gene)){// check while is not find a unused gene
        			if(gene>=chromosomeDim)
        				gene=1;
        			else
        				gene++;
        		}
        		used.add(gene);//metto il gene usato nella mappa
        			
                this.getChromosome(i).setGene(String.valueOf(gene),j);//old :((ChromStrings)this.chromosomes[i]).setGene(getRandomGeneFromPossGenes(), j);
        	}
        	/*
        	* DA RENDER PROTECTED ALTRIMENTI NON LO VEDIAMO!!!
        	*/
        	//calcolo fitness nel metodo InitiaFitness
        }
    }
	
	private boolean Probability50() {
		// TODO Auto-generated method stub
		return (System.currentTimeMillis()%2 == 0)?true:false;
	}
	
	private int myGetRandom(int range)
	{
		Random generator = new Random(System.currentTimeMillis());
		int a = generator.nextInt(range-1)+1 ;
		return a;
	}
	
	@Override
	protected void doTwoPtCrossover(Chromosome Chrom1, Chromosome Chrom2){
		ChromStrings chr1 = (ChromStrings)Chrom1;
		ChromStrings chr2 = (ChromStrings)Chrom2;
		
		/*ipotesi farlo per pi� volte*/
		
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
		
		ChromStrings off1 = new ChromStrings(chromosomeDim);
		ChromStrings off2 = new ChromStrings(chromosomeDim);
		ChromStrings par1 = (ChromStrings)Chrom1;
		ChromStrings par2 = (ChromStrings)Chrom2;
		
		//System.out.println("Do crossover "+this.countCross);
		//System.out.println(par1.toString());
		//System.out.println(par2.toString() + "\n");
		
		int i,j;
		int t = myGetRandom(chromosomeDim-2);
		
		for(i = 0; i<t;i++)
		{
			off1.setGene(par2.getGene(i), i);
			off2.setGene(par1.getGene(i), i);
		}
		
		j=t;
		for(i=t;i<chromosomeDim;i++)
		{
			while(ChromosomeContainsGene(off1,i,par1.getGene(j)))
			{
				if(j>=chromosomeDim-1)
					j=0;
				else
					j++;
			}
			off1.setGene(par1.getGene(j), i);
			//System.out.println(off1.toString());
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
			//System.out.println(off2.toString());
			if(j>=chromosomeDim-1)
				j=0;
			else
				j++;
		}
		
		if(check_repetitions(off1)&&check_repetitions(off2))//controllo che il crossover abbia funzionato
		{
			Chrom1 = off1;
			Chrom2 = off2;
			System.out.println((++countCross)+" cross over ok");;
		}
		else
			System.out.println((++countCross)+" cross over ko!");
			
		
		/*
		int iCrossoverPoint = myGetRandom(chromosomeDim-2);
        String gene1 = ((ChromStrings)Chrom1).getGene(iCrossoverPoint);
        String gene2 = ((ChromStrings)Chrom2).getGene(iCrossoverPoint);

                // CREATE OFFSPRING ONE
        ((ChromStrings)Chrom1).setGene(gene2, iCrossoverPoint);

                // CREATE OFFSPRING TWO
        ((ChromStrings)Chrom2).setGene(gene1, iCrossoverPoint);
        */
		synchronized(this){
			countCross++;
		}
		//System.out.println("Do crossover "+this.countCross);
		//System.out.println(off1.toString());
		//System.out.println(off2.toString() + "\n");
	}
	
	
	private boolean check_repetitions(ChromStrings off1) {
		
		Map<String, Integer> chromosomes = new HashMap<String, Integer>();//mappa per memorizzare i cromosomi usati
		for(int i = 1; i<=chromosomeDim;i++)
		{
			chromosomes.put(String.valueOf(i), 0);
		}
		for(int i = 0;i<off1.getGenes().length;i++)
		{
			Object v =chromosomes.get(off1.getGene(i));
			
			if( v !=null)
			{
				if((int)v == 1)
					return false;//gene gi� utilizzato
				else
				{
					chromosomes.remove(off1.getGene(i));
					chromosomes.put(off1.getGene(i),1);
				}
				
			}
			else
				return false;//gene non presente
			
				
					
		}
		
		return true;
	}


	private boolean ChromosomeContainsGene(ChromStrings c, int last, String gene)
	{ 
		boolean trovato = false;
		
		for(int i = 0;i<last; i++)
		{
			if(c.getGene(i).equals(gene))
			{
				trovato = true;
			}
		}
		return trovato;
	}
	
	@Override
	protected double getFitness(int chromosomeIndex) {
		String []chromosome = this.getChromosome(chromosomeIndex).getGenes();
		MySolution mySolution = new MySolution(MDVRPTWGA.instance, chromosome);
		evaluateAbsolutely(mySolution);
		
		return mySolution.getCost().getTotalCost();
	}
	
	/**
	 * Ho usato il codice qua sotto per calcolare la fitness, e sembra funzionare piu o meno
	 * anche se ritorna un valore di fitness enorme.
	 * Se volete provarlo vi basta togliere i commenti all'inizio e alla fine,
	 *  e i coomenti da getFitness qui sopra
	 */
	
	private void evaluateAbsolutely(Solution solution){
    	MySolution sol = (MySolution)solution;
    	Route route;
    	
    	sol.getCost().initialize();
		for (int i = 0; i < sol.getDepotsNr(); ++i) {
			for(int j = 0; j < sol.getDepotVehiclesNr(i); ++j){
				route = sol.getRoute(i, j);
		    	// do the math only if the route is not empty
				if(!route.isEmpty()) {
					evaluateRoute(route);
					sol.getCost().travelTime += route.getCost().getTravel();
					sol.getCost().load += route.getCost().load;
					sol.getCost().serviceTime += route.getCost().serviceTime;
					sol.getCost().waitingTime += route.getCost().waitingTime;
					sol.getCost().addLoadViol(route.getCost().getLoadViol());
					sol.getCost().addDurationViol(route.getCost().getDurationViol());
					sol.getCost().addTWViol(route.getCost().getTwViol());
					
				} // end if route not empty
			}// end for vehicles
		}// end for depots
		sol.getCost().calculateTotalCostViol();
	}// end method evaluateAbsolutely

	private void evaluateRoute(Route route) {
    	double totalTime = 0;
    	double waitingTime = 0;
    	double twViol = 0;
    	Customer customerK;
    	route.initializeTimes();
    	// do the math only if the route is not empty
		if(!route.isEmpty()){
	    	// sum distances between each node in the route
			for (int k = 0; k < route.getCustomersLength(); ++k){
				// get the actual customer
				customerK = route.getCustomer(k);
				// add travel time to the route
				if(k == 0){
					route.getCost().travelTime += MDVRPTWGA.instance.getTravelTime(route.getDepotNr(), customerK.getNumber());
					totalTime += MDVRPTWGA.instance.getTravelTime(route.getDepotNr(), customerK.getNumber());
				}else{
					route.getCost().travelTime += MDVRPTWGA.instance.getTravelTime(route.getCustomerNr(k -1), customerK.getNumber());
					totalTime += MDVRPTWGA.instance.getTravelTime(route.getCustomerNr(k -1), customerK.getNumber());
				} // end if else
				
				customerK.setArriveTime(totalTime);
				// add waiting time if any
				waitingTime = Math.max(0, customerK.getStartTw() - totalTime);
				route.getCost().waitingTime += waitingTime;
				// update customer timings information
				customerK.setWaitingTime(waitingTime);
				
				totalTime = Math.max(customerK.getStartTw(), totalTime);

				// add time window violation if any
				twViol = Math.max(0, totalTime - customerK.getEndTw());
				route.getCost().addTWViol(twViol);
				customerK.setTwViol(twViol);
				// add the service time to the total
				totalTime += customerK.getServiceDuration();
				// add service time to the route
				route.getCost().serviceTime += customerK.getServiceDuration();
				// add capacity to the route
				route.getCost().load += customerK.getCapacity();
				
			} // end for customers
			
			// add the distance to return to depot: from last node to depot
			totalTime += MDVRPTWGA.instance.getTravelTime(route.getLastCustomerNr(), route.getDepotNr());
			route.getCost().travelTime += MDVRPTWGA.instance.getTravelTime(route.getLastCustomerNr(), route.getDepotNr());
			// add the depot time window violation if any
			twViol = Math.max(0, totalTime - route.getDepot().getEndTw());
			route.getCost().addTWViol(twViol);
			// update route with timings of the depot
			route.setDepotTwViol(twViol);
			route.setReturnToDepotTime(totalTime);
			route.getCost().setLoadViol(Math.max(0, route.getCost().load - route.getLoadAdmited()));
			route.getCost().setDurationViol(Math.max(0, route.getDuration() - route.getDurationAdmited()));
			
			route.getCost().setTravelTime(route.getCost().travelTime);
			// update total violation
			route.getCost().calculateTotalCostViol();
			
		} // end if route not empty
		
    } // end method evaluate route


	public void InitialFittness() {
		// TODO Auto-generated method stub
		for(int i = 0; i< populationDim;i++)
		{
				MySolution mySolution = new MySolution(MDVRPTWGA.instance, this.getChromosome(i).getGenes());
				this.evaluateAbsolutely(mySolution);
				this.getChromosome(i).setFitness(mySolution.getCost().getTotalCost());
		}
		
	}
	
}
