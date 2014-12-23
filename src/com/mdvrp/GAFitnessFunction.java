package com.mdvrp;

public class GAFitnessFunction {

	
	
	public double getFitness(int[] chr, int firstVehicleValue){
		
		Instance instance = MDVRPTWGA.instance;
		double cost = 0;
		boolean firstRouteCustomer = true;
		int currentVehicleIndex = 0;
		int currentGeneIndex;
		double[] vehicleTime = new double[instance.getVehiclesNr()];
		double[] arrivalTimes = new double[instance.getCustomersNr()];
		Customer firstCustomer, secondCustomer;
		
		//for(int j=0; j<instance.getCustomersNr(); j++)
			//System.out.print(" " + chr[j] + " ");
		
		for(currentGeneIndex=0; currentGeneIndex < chr.length; currentGeneIndex++){
			if(chr[currentGeneIndex] >= firstVehicleValue){
				//Siamo in un veicolo
				firstRouteCustomer = true;
				currentVehicleIndex++;
			}else{
				//Siamo in un customer
				if(firstRouteCustomer){
					firstCustomer = getCustomer(chr[currentGeneIndex]);
					double x1 = firstCustomer.getXCoordinate();
					double y1 = firstCustomer.getYCoordinate();
					double x2 = instance.getDepot(0).getXCoordinate();
					double y2 = instance.getDepot(0).getYCoordinate();
				
					double distance = Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2);
					arrivalTimes[chr[currentGeneIndex]] = vehicleTime[currentVehicleIndex] + distance;
					vehicleTime[currentVehicleIndex] += distance + firstCustomer.getServiceDuration();
					cost += distance;
					firstRouteCustomer = false;
					
				}else{
					firstCustomer = getCustomer(chr[currentGeneIndex]);
					secondCustomer = getCustomer(chr[currentGeneIndex-1]);
					double x1 = firstCustomer.getXCoordinate();
					double y1 = firstCustomer.getYCoordinate();
					double x2 = secondCustomer.getXCoordinate();
					double y2 = secondCustomer.getYCoordinate();
				
					double distance = Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2);
					arrivalTimes[chr[currentGeneIndex]] = vehicleTime[currentVehicleIndex] + distance;
					vehicleTime[currentVehicleIndex] += distance + firstCustomer.getServiceDuration();
					cost += distance;
				}
				
				cost += firstCustomer.getCapacity();
				cost += firstCustomer.getServiceDuration();
				cost += Math.max(0, firstCustomer.getStartTw() - arrivalTimes[chr[currentGeneIndex]]);
				
			}
			
		}
		return cost;
	}
	
	private Customer getCustomer(int customerNumber){
		Instance instance = MDVRPTWGA.instance;
		
		/*for(int i=0; i<instance.getDepot(0).getAssignedCustomersNr(); i++)
			System.out.println("Customer: " + i + "Number: " + instance.getDepot(0).getAssignedCustomer(i).getNumber() + " ");
		
		System.out.println("\n\n");
		*/
		for(int i=0; i<instance.getCustomersNr(); i++){
			if(instance.getDepot(0).getAssignedCustomer(i).getNumber() == (customerNumber)){
				return instance.getDepot(0).getAssignedCustomer(i);
			}
		}
		
		return null;
	}
		
}
