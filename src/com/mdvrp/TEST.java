package com.mdvrp;

import java.io.IOException;

public class TEST {
//public static MDVRPTWGA GA;	
private static int MAX = 10;
private static Duration d = new Duration();
	
	public static void main(String[] args) throws IOException {
			
			long minutes=0;
			long seconds=0;
			
			int n = args.length-1;
		
			d.start();
			for(int j = 1; j<n; j++)
			{
				for(int i = 0; i < MAX; i++)
				{
					String[] file = new String[2];
					file[0]="-if";
					file[1]=args[j];
					MDVRPTWGA.main(file);
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
				//String outSol = "ANALISI :";
				//Parameters parameters = MDVRPTWGA.instance.getParameters();
		        //FileWriter fw = null;
				/*try {
					fw = new FileWriter(parameters.getOutputFileName(),true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        fw.write(outSol);
		        fw.close();*/
				System.out.println("FINE."+" elapsed time : "+minutes+"."+seconds+"''");
			}
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
				
				System.out.println("##################################");
				System.out.println(" ");
				System.out.println("IT -> "+(i+1)+" elapsed time : "+minutes+"."+seconds+"''");
				System.out.println(" ");
				System.out.println("##################################");
				d.start();
			}
			
			d.stop();
			minutes += d.getMinutes();
			seconds += d.getSeconds();
			/*String outSol = "ANALISI :";
			Parameters parameters = MDVRPTWGA.instance.getParameters();
	        FileWriter fw = null;
			try {
				fw = new FileWriter(parameters.getOutputFileName(),true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        fw.write(outSol);
	        fw.close();*/
			System.out.println("FINE."+" elapsed time : "+minutes+"."+seconds+"''");

	}
}

