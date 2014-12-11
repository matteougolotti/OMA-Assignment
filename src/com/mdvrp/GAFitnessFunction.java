package com.mdvrp;

public class GAFitnessFunction {

	
	
	public double getFitness(int[] chr, int firstVehicleValue){
		
		Instance instance = MDVRPTWGA.instance;
		double cost = 0;
		int currentCustomerIndex = 0;
		int currentVehicleIndex = 0;
		int currentGeneIndex;
		double[] vehicleTime = new double[instance.getVehiclesNr()];
		double[] arrivalTimes = new double[instance.getCustomersNr()];
		
		for(currentGeneIndex=0; currentGeneIndex < chr.length; currentGeneIndex++){
			if(chr[currentGeneIndex] >= firstVehicleValue){
				//Siamo in un veicolo
				currentCustomerIndex = 0;
				currentVehicleIndex++;
				
			}else{
				//Siamo in un customer
				if(currentCustomerIndex == 0){
					double x1 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getXCoordinate();
					double y1 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getYCoordinate();
					double x2 = instance.getDepot(0).getXCoordinate();
					double y2 = instance.getDepot(0).getYCoordinate();
				
					double distance = Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2);
					arrivalTimes[chr[currentGeneIndex]-1] = vehicleTime[currentVehicleIndex] + distance;
					vehicleTime[currentVehicleIndex] += distance + instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getServiceDuration();
					cost += distance;
					
				}else{
					double x1 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getXCoordinate();
					double y1 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getYCoordinate();
					double x2 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex-1]-1).getXCoordinate();
					double y2 = instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex-1]-1).getYCoordinate();
				
					double distance = Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2);
					arrivalTimes[chr[currentGeneIndex]-1] = vehicleTime[currentVehicleIndex] + distance;
					vehicleTime[currentVehicleIndex] += distance + instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getServiceDuration();
					cost += distance;
				}
				
				cost += instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getCapacity();
				cost += instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getServiceDuration();
				cost += Math.max(0, instance.getDepot(0).getAssignedCustomer(chr[currentGeneIndex]-1).getStartTw() - arrivalTimes[chr[currentGeneIndex]-1]);
				
			}
			
		}
		return cost;
	}
	
		
}
