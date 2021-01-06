package thread;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessRings {

	public static void main(String args[]) {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter total number of threads");
		int totalNumOfThreads=sc.nextInt();
		System.out.println("Enter total number of Messages");
		int totalMessage=sc.nextInt();
		PrintJob printJob = new PrintJob(totalNumOfThreads,totalMessage);

		ExecutorService executorService = Executors.newFixedThreadPool(totalNumOfThreads);
		Set<Runnable> runnables = new HashSet<Runnable>();

		for (int i = 1; i <= totalNumOfThreads; i++) {
			MyRunnable command = new MyRunnable(printJob, i);
			runnables.add(command);
			executorService.execute(command);
		}

		executorService.shutdown();

	}
}

class MyRunnable implements Runnable {
	private volatile boolean exit = false;
	PrintJob printJob;
	int threadNum;

	public MyRunnable(PrintJob job, int threadNum) {
		this.printJob = job;
		this.threadNum = threadNum;
	}

	@Override
	public void run() {
		long startTime = System.nanoTime();
		while (!exit) {
			synchronized (printJob) {
				if (threadNum == printJob.counter) {
					printJob.mainCounter++;
					printJob.printStuff();

					if (printJob.counter != printJob.totalNumOfThreads && printJob.mainCounter < printJob.totalMessage) {
						printJob.counter++;
						

					} else if (printJob.mainCounter >= printJob.totalMessage) {
						exit=true;
					} else {
						System.out.println();
						printJob.resetCounter();
					}

					printJob.notifyAll();

				} else {
					try {
						printJob.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}
		long totalTime = System.nanoTime() - startTime;
		System.out.printf("The total time everything took was %.3f ms %n", totalTime / 1e6);

	}
}

class PrintJob {
	int counter = 1;
	int totalNumOfThreads;
	int mainCounter = 0;
	int totalMessage;

	PrintJob(int totalNumOfThreads,int totalMessage) {
		this.totalNumOfThreads = totalNumOfThreads;
		this.totalMessage=totalMessage;
	}

	public void printStuff() {
		System.out.println(Thread.currentThread().getName() + "  Got a Message ->" + "Hello Ring World!");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void resetCounter() {
		this.counter = 1;
	}

}