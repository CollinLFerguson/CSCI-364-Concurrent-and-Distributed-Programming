package DiningPhilosophers;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/*
DiningPhilosophers.DiningPhilosophers.java
Collin L. Ferguson
Homework 3: Use semaphores and threads to implement the resource hierarchy solution of Dijkstra's dining philosophers
problem.
 */

/**
 * Runnable class that will act as the philosophers. They will run the problem until each philosopher has
 * their stopDining() method run.
 * @author Collin
 */
class Philosopher implements Runnable {
    /** @param dinnerBell,leftFork,rightFork Controlling semaphores of the philosophers. The dinnerBell controls when
     *                                       the philosophers may start as well as controls the critical sections.
     *                                       The forks are needed for philosophers to start eating.*/
    private final Semaphore dinnerBell, leftFork, rightFork;
    /**@param philosopherID: The identifier of the philosopher.*/
    private final String philosopherID;
    private Boolean keepDining;
    /**@param numThoughts The total number of thoughts the philosopher has had.*/
    private int numThoughts;
    /**@param numMeals The total number of meals the philosopher has had.*/
    private int numMeals;
    /**@param numThoughts The total amount of time the philosopher has been dining.*/
    private long timeCriticalSectionNS;
    /**@param numThoughts The total amount of time the philosopher has been looking for forks.*/
    private long timeTotalNS;

    private static Random randomNum = new Random(); //All philosophers share a random object for number generation.

    public Philosopher(String philosopherID, Semaphore dinnerBell, Semaphore leftFork, Semaphore rightFork) {
        this.philosopherID = philosopherID;
        this.dinnerBell = dinnerBell;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.keepDining = true;
        numThoughts = 0;
        numMeals = 0;
        timeCriticalSectionNS = 0;
        timeTotalNS = 0;
    }

    /**The main driver method of the philosopher. handles the entire dining process. After the dinnerBell Semaphore has
     * been release, all philosophers will begin tracking time. They will then alternate between thinking, looking for
     * forks, and eating. When they cannot find a fork, but are hungry, they will think for a random time
     * between 1 and 100ns.*/
    public void run() {
        try {
            dinnerBell.acquire();
        } catch(Exception e)
        {
            System.out.println(philosopherID + " was interrupted before the dinnerBell.\nEnding problem.");
            System.exit(0);
        }

        long startTimeTotal = System.nanoTime();
        long startTimeCritical;

        while(keepDining) {
            try {
                think();

                //CRITICAL SECTION START
                startTimeCritical = System.nanoTime();
                dinnerBell.acquire();
                while(!leftFork.tryAcquire(1, 10, TimeUnit.NANOSECONDS)){
                    //CRITICAL SECTION END
                    dinnerBell.release();
                    timeCriticalSectionNS += System.nanoTime() - startTimeCritical;
                    think();
                    //CRITICAL SECTION START
                    startTimeCritical = System.nanoTime();
                    dinnerBell.acquire();
                }
                dinnerBell.release();
                timeCriticalSectionNS += System.nanoTime() - startTimeCritical;

                //CRITICAL SECTION START
                startTimeCritical = System.nanoTime();
                dinnerBell.acquire();
                while(!rightFork.tryAcquire(1, 10, TimeUnit.NANOSECONDS)){
                    //CRITICAL SECTION END
                    dinnerBell.release();
                    timeCriticalSectionNS += System.nanoTime() - startTimeCritical;
                    think();
                    //CRITICAL SECTION START
                    startTimeCritical = System.nanoTime();
                    dinnerBell.acquire();
                }
                dinnerBell.release();
                timeCriticalSectionNS += System.nanoTime() - startTimeCritical;

                Thread.sleep(0,100); //Eats for 100 ns.
                numMeals += 1;

                leftFork.release();
                rightFork.release();

                Thread.sleep(0); //Signals to the CPU to give up the current thread and let another take a turn.
                                    //Enforces some randomness to prevent one philosopher from being the only one to run

            } catch (InterruptedException e) {
                System.out.println("The philosopher's dinner was interrupted.\nEnding the problem.");
                System.exit(0);
            }
        }
        timeTotalNS = System.nanoTime() - startTimeTotal;
    }
    public void think() throws InterruptedException {
        //System.out.println(philosopherID + " Is thinking");
        Thread.sleep(0, randomNum.nextInt(1,1000));
        numThoughts++;
    }
    /**Method that signals all of the philosophers to stop dining. They will finish their current cycle before finishing
     * the dining process. */
    public void stopDining()
    {
        keepDining = false;
    }

    public void printPhilosopherInfo(){
        System.out.println("Philosopher: " + philosopherID);
        System.out.format("Thoughts: %3d, Meals: %3d\n", numThoughts, numMeals);
        System.out.format("Critical Section Time (ns): %d\n", timeCriticalSectionNS);
        System.out.format("Total Time (ns): %d\n", timeTotalNS);
        System.out.format("Time Ratio -- Critical section/total: %1.6f\n\n", ((double)timeCriticalSectionNS) / ((double) timeTotalNS));
    }
}

/**Driver class of the program. Stores philosopher runnables, Philosopher threads, and the semaphores.
 * Creates philosophers, the threads, and semaphores, starts the philosophers, then will signal for them to stop
 * and print the results.*/
public class DiningPhilosophers {
    /**@param Philosopher Stores the philosopher runnable objects. Used to call methods after the threads have finished */
    private Philosopher[] philosophers; //The physical philosophers that will exist before and after the threads run
    /**@param Thread Stores the philosopher Thread objects. used to initiate the philosopher threads */
    private Thread[] philosopherThreads; //Variable holding the threads
    /**@param forks Stores the fork Semaphores. Used to prevent philosophers from eating. */
    private Semaphore[] forks; //Semaphores preventing philosophers from picking up 2 forks at once
    /**@param dinnerBell Semaphore used to control access to critical sections.*/
    private Semaphore dinnerBell; // makes sure all philosophers wait for everyone to sit down and all forks to be passed out.
    private final int philosopherNumber = 5;

    /**Initializes all necessary variables for the problem.*/
    private void setUpProblem() {
        forks = new Semaphore[philosopherNumber];
        philosophers = new Philosopher[philosopherNumber];
        philosopherThreads = new Thread[philosopherNumber];

        for (int x = 0; x < philosopherNumber; x++) {
            forks[x] = new Semaphore(1, true);
        }

        dinnerBell = new Semaphore(0, true);

        for (int x = 0; x < philosopherNumber; x++) {
            if(x < 1){
                philosophers[x] = new Philosopher("Philosopher "+ (x+1), dinnerBell, forks[x], forks[philosopherNumber-1]); //"Left-handed" philosopher
            } else{
                philosophers[x] =new Philosopher("Philosopher "+ (x+1), dinnerBell, forks[x-1], forks[x]);
            }
            this.philosopherThreads[x] = new Thread(philosophers[x]);
        }
    }
    /**Starts the threads and then releases the dinnerBell controller variable. */
    private void startDining() {
        for(int x=0; x<philosophers.length; x++) {
            philosopherThreads[x].start();
        }
        dinnerBell.release(philosopherNumber + 1); //Rings the dinner bell, allowing all philosophers to start eating fairly.
                                                            // the + 1 is to allow the critical section.
    }

    /**Signals to the philosophers that they need to stop dining. It will then wait for all threads to join.*/
    private void stopAllDining(){
        for(int x=0; x<philosophers.length; x++) {
            philosophers[x].stopDining();
        }

        for(int x=0; x<philosophers.length; x++) {
            try {
                philosopherThreads[x].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**Prints all the philosopher's gathered statistics.*/
    private void printDiningInfo(){
        for(int x=0; x<philosopherNumber; x++) {
            philosophers[x].printPhilosopherInfo();
        }
    }

    /**Driver method of the program. calls all necessary methods and sleeps in order to give the philosophers time to run.*/
    public static void main(String[] args) {
        long timeLimit = 0;

        if(args.length <1)
        {
            System.out.println("Usage: java DiningPhilosophers.DiningPhilosophers <long timelimit>");
            System.exit(0);
        }
        try{
            timeLimit = Long.parseLong(args[0]);

        } catch(Exception e){
            System.out.println("Usage: java DiningPhilosophers.DiningPhilosophers <long timelimit>");
            e.printStackTrace();
            System.exit(0);
        }

        DiningPhilosophers dpp = new DiningPhilosophers();
        dpp.setUpProblem();
        dpp.startDining();
        try {
            Thread.sleep(timeLimit);
        } catch (InterruptedException e){
            System.out.println("Main's sleep was interrupted.\nExiting program.");
            dpp.stopAllDining();
            System.exit(0);
        }
        dpp.stopAllDining();
        dpp.printDiningInfo();
    }
}
