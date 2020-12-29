package Banker;

import java.util.Scanner;

/**
 * The Bank
 */
public class BankImpl implements Bank {
    int numberOfCustomers;
    int  numberOfResources;
    int [] available;
    int [][] maximum;
    int [][] allocation;
    int [][] need;
    boolean safeState = false;
    
    public BankImpl(int[] resources){
        
        
	numberOfResources = resources.length;
        numberOfCustomers = Customer.COUNT;
        // initialize the resources array
	available = new int[numberOfResources];
	System.arraycopy(resources,0,available,0,numberOfResources);
	maximum = new int[numberOfCustomers][];
	allocation = new int[numberOfCustomers][];
	need = new int[numberOfCustomers][];
       
    }

    @Override
    public void addCustomer(int threadNum, int[] maxDemand) {
        
        maximum[threadNum] = new int[numberOfResources];
	allocation[threadNum] = new int[numberOfResources];
	need[threadNum] = new int[numberOfResources];
	System.arraycopy(maxDemand, 0, maximum[threadNum], 0, maxDemand.length);
	System.arraycopy(maxDemand, 0, need[threadNum], 0, maxDemand.length);
        
            
    }
    

    @Override
    public void getState() {
        System.out.print("Available = \t[");
		for (int i = 0; i < numberOfResources-1; i++)
			System.out.print(available[numberOfResources-1]+" ");
		System.out.println(available[-1]+"]");
		System.out.print("\nAllocation = \t");
		for (int i = 0; i < numberOfCustomers; i++) {
			System.out.print("[");
			for (int j = 0; j < numberOfResources-1; j++)
				System.out.print(allocation[i][j]+" ");
			System.out.print(allocation[i][numberOfResources-1]+"]");
		}
		System.out.print("\nMax = \t\t");
		for (int i = 0; i < numberOfCustomers; i++) {
			System.out.print("[");
			for (int j = 0; j < numberOfResources-1; j++)
				System.out.print(maximum[i][j]+" ");
			System.out.print(maximum[i][numberOfResources-1]+"]");
		}
		System.out.print("\nNeed = \t\t");
		for (int i = 0; i < numberOfCustomers; i++) {
			System.out.print("[");
			for (int j = 0; j < numberOfResources-1; j++)
				System.out.print(need[i][j]+" ");
			System.out.print(need[i][numberOfResources-1]+"]");
		}
		System.out.println(" ");
		for (int i = 0;i<numberOfCustomers;i++){
                    
		    for (int j=0;j<1;j++){
			if (need[i][j]<=available[j]&& need[i][j+1]<=available[j+1]&&need[i][j+2]<=available[j+2]){
                            
                            safeState= true;
			}
		    }
		}
		if (safeState)
			{
			System.out.println("no deadlock :D");
			}
		if (!safeState)
			{
			System.out.println("deadlock waring ");
			}
	    System.out.println();
       
       
    
       
    }
    
    private boolean isSafeState (int threadNum, int[] request) {
        
	System.out.print("\n Customer # " + threadNum + " requesting ");
	for (int i = 0; i < numberOfResources; i++) 
             System.out.print(request[i] + " ");
		
	System.out.print("Available = ");
	for (int i = 0; i < numberOfResources; i++)
	    System.out.print(available[i] + "  ");
		
	// check if resources enough or not 
	for (int i = 0; i < numberOfResources; i++){
            
	    if (request[i] > available[i]) {
		System.out.println("Insufficient Resources");
		return false;
	    }
            
        }
        
	boolean[] done = new boolean[numberOfCustomers];
	for (int i = 0; i < numberOfCustomers; i++)
		done[i] = false;
		
	// copy the available matrix to another one
	int[] avail = new int[numberOfResources];
	System.arraycopy(available,0,avail,0,available.length);
		
	//if request less than or equal avaiavle, do the following
	for (int i = 0; i < numberOfResources; i++) {
            
            avail[i] = avail[i] - request[i];
            allocation[threadNum][i] = allocation[threadNum][i]+ request[i];
	    need[threadNum][i] = need[threadNum][i] - request[i];
	    
	}
		
	/**
	* Now try to find an ordering of threads so that
	* each thread can finish.
	*/
		
	for (int i = 0; i < numberOfCustomers; i++) {
	    // first find a thread that can finish
	    for (int j = 0; j < numberOfCustomers; j++) {
		if (!done[j]) {
		    boolean temp = true;
		    for (int k = 0; k < numberOfResources; k++) {
			if (need[j][k] > avail[k])
			    temp = false;
		    }
		    if (temp) { // if this thread can finish
			done[j] = true;
			for (int x = 0; x < numberOfResources; x++)
                            // when thread finish, the allocated is add to the available
                            // to be used b the next thread
			    avail[x] =   avail[x] + allocation[j][x]; 
                        
		    }
	        }	
	    }
	}
		
	// restore the value of need and allocation for this thread
	for (int i = 0; i < numberOfResources; i++) {
            need[threadNum][i] = need[threadNum][i] + request[i];
            allocation[threadNum][i] = need[threadNum][i] - request[i];
	}
		
	// now go through the boolean array and see if all threads could complete
	boolean returnValue = true;
	for (int i = 0; i < numberOfCustomers; i++)
	    if (!done[i]) {
		returnValue = false;
		break;
	    }
		
	    return returnValue;
    }
	
    

    @Override
    public synchronized  boolean requestResources(int threadNum, int[] request) {
        
        if (!isSafeState(threadNum,request)) {
		return false;
	}
        
        for(int j=0;j<numberOfResources;j++) {
            available[j] =  available[j]- request[j];
	    allocation[threadNum][j] =  allocation[threadNum][j]+ request[j];
	    need[threadNum][j] = maximum[threadNum][j] - allocation[threadNum][j];
        }
        
      return true;
        
    }

    @Override
    public synchronized  void releaseResources(int threadNum, int[] release) {
        
        System.out.print("\n Customer # " + threadNum + " releasing ");
	for (int i = 0; i < numberOfResources; i++) 
            System.out.print(release[i] + " ");
		
	for (int i = 0; i < numberOfResources; i++) {
	    available[i] = available[i]+ release[i];
	    allocation[threadNum][i] = allocation[threadNum][i] - release[i];
	    need[threadNum][i] = maximum[threadNum][i] + allocation[threadNum][i];
	}
		
	System.out.print("Available = ");
	for (int i = 0; i < numberOfResources; i++)
        System.out.print(available[i] + "  ");
		
	System.out.print("Allocated = [");
	for (int i = 0; i < numberOfResources; i++)
	    System.out.print(allocation[threadNum][i] + "  "); 
	System.out.print("]"); 
    }

}
