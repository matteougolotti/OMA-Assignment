package com.mdvrp;

import com.softtechdesign.ga.Crossover;
import com.softtechdesign.ga.GAException;
import com.softtechdesign.ga.GASequenceList;

public class GAInitialSolution extends GASequenceList {

	public GAInitialSolution() throws GAException {
		super(  20, //size of chromosome (number of customers + number of vehicles)
                300, //population has N chromosomes (eventualmente parametrizzabile)
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
		
	}

	@Override
	protected double getFitness(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
