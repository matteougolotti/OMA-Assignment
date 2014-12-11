package com.mdvrp;

public class TEST {
//public static MDVRPTWGA GA;	
private static int MAX = 50;
private static Duration d = new Duration();
	
	public static void main(String[] args) {
			
			long minutes=0;
			long seconds=0;
		
			d.start();
			for(int i = 0; i < MAX; i++)
			{
				MDVRPTWGA.main(args);
				d.stop();
				seconds += d.getSeconds();
				if (seconds>=60){
					long m = seconds/60;
					seconds-=60*m;
					minutes+=m;
				}
					
				System.out.println("IT -> "+(i+1)+" elapsed time : "+minutes+"."+seconds+"''");
				d.start();
			}
			
			d.stop();
			minutes += d.getMinutes();
			seconds += d.getSeconds();
			System.out.println("FINE."+" elapsed time : "+minutes+"."+seconds+"''");

	}
}

