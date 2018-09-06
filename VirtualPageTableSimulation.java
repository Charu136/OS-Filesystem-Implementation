package com.vptr.sim;

import java.util.Random;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class VirtualPageTableSimulation {

	// Initialize RAM as a queue (pairs of page #, frame #)
	Queue<RAMEntry> RAM;
	int noOfFrames;
	int noOfPages;
	// Hard drive
	int[] pageSectorsMap;

	// VPTR entries for lookup and display
	List<VPTREntry> register;

	public VirtualPageTableSimulation() {
		RAM = new LinkedList<>();
		pageSectorsMap = new int[1024];
		register = new ArrayList<>();
	}

	public void setNoOfFrames(int noOfFrames) {
		this.noOfFrames = noOfFrames;
	}

	public void setNoOfPages(int noOfPages) {
		this.noOfPages = noOfPages;
	}

	public void loadPageInRAM(int pageNumber, int frameNumber) {

		// Checking if RAM has free space
		if (RAM.size() >= noOfFrames) {
			// Remove first element-FIFO queue
			RAMEntry entryToRemove = RAM.poll();
			// Retrieves the page number of the first entry and updates the valid bit as o
			// in VPTR
			// Denotes no longer available in the RAM
			for (VPTREntry entry : register)
				if (entry.pageNumber == entryToRemove.pageNumber)
					entry.isValid = 0;
		}
		// Adding new entry into the VPTR
		RAM.add(new RAMEntry(pageNumber, frameNumber));
	}

	public void simulatePageDemand() {

		int logicalAddress;
		int pageNumber;
		int offset;
		int frameNumber;
		int sectorNumber;
		int isValid;

		Random randomGenerator = new Random();
		// Logical Address randomly generated - Total virtual pages 1024
		logicalAddress = randomGenerator.nextInt(noOfPages * 16) + 0;

		// Calculate Page number - Page size 16 bytes

		pageNumber = logicalAddress / 16;
		// Calculate Offset
		offset = logicalAddress % 16;
        
		//Checking if the page number calculated is active in RAM
		//If so can't assign same - Generate logical address again to avoid conflict
		while (true) {
			boolean found1 = false;
			logicalAddress = randomGenerator.nextInt(noOfPages * 16) + 0;
			pageNumber = logicalAddress / 16;
			offset = logicalAddress % 16;
			for (VPTREntry entry : register)
				if (entry.pageNumber == pageNumber && entry.isValid == 1)
					found1 = true;
			if (!found1)
				break;
		}

		// Checking if frame number generated is already in the VPTR and active
		// Generate a new frame-can't take the same frame number
		while (true) {
			boolean found2 = false;
			// Generate Frame number from 0 to 63
			frameNumber = randomGenerator.nextInt(63) + 0;
			for (VPTREntry entry1 : register)
				if (entry1.frameNumber == frameNumber && entry1.isValid == 1)
					found2 = true;
			if (!found2)
				break;

		}
		

		// Loading the new page in the RAM
		loadPageInRAM(pageNumber, frameNumber);
		// Calculate sector number and assign to Hard Drive
		sectorNumber = (frameNumber * 16) + offset;
		isValid = 1;
		pageSectorsMap[pageNumber] = sectorNumber;
		// create new VPTR entry
		VPTREntry vpEntry = new VPTREntry(logicalAddress, pageNumber, offset, frameNumber, sectorNumber, isValid);
		register.add(vpEntry);

	}

	public void displayVPTR() {
		// Printing the contents of the Page Table Register
		System.out.println("-------------------------Printing VPTR-------------------------------------------------");
		System.out.println("Logical address\t| Page Number\t| Offset\t| Frame Number\t| Sector Number\t| Valid Bit\t");
		System.out.println("---------------------------------------------------------------------------------------");
		for (VPTREntry entry : register) {
			System.out.println(entry.logicalAddress + "\t\t| " + entry.pageNumber + "\t\t| " + entry.offset + "\t| "
					+ entry.frameNumber + "\t\t| " + entry.sectorNumber + "\t\t| " + entry.isValid);
		}
		System.out.println("---------------------------------------------------------------------------------------");
	}

	public void displayRAM() {
		System.out.println("-------Printing RAM----------");
		System.out.println("Page Number\t| Frame Number\t");
		System.out.println("-----------------------------");
		for (RAMEntry entry : RAM)
			System.out.println(entry.pageNumber + "\t\t|" + entry.frameNumber);

	}

	public void displayHardDisk() {
		System.out.println("------Printing Hard Drive-----");
		System.out.println("Page Number\t| Sector Number\t");
		System.out.println("------------------------------");
		for (int i = 0; i < pageSectorsMap.length; i++) {
			if (pageSectorsMap[i] != 0) {
				System.out.println(i + "\t\t|" + pageSectorsMap[i]);
			}
		}

	}

	public static void main(String[] args) {
		char choice;
		VirtualPageTableSimulation simulation = new VirtualPageTableSimulation();
		Scanner input = new Scanner(System.in);
		do {
			// User input for program pages and frames allocated to it
			System.out.println("Enter the number of pages in your program:");
			int progPages = input.nextInt();
			System.out.println("Enter the number of frames to be allocate:\n");
			int progFrames = input.nextInt();
			simulation.setNoOfFrames(progFrames);
			simulation.setNoOfPages(progPages);
			
			// Pages are generated, loaded into the VPTR and displayed every time
			for (int i = 0; i < progPages; i++) {
				simulation.simulatePageDemand();
				simulation.displayVPTR();
			}
			
			System.out.println("Do you want to continue? y/n ");
			choice = input.next().charAt(0);
			
			// reset valid bits in registry
			for (VPTREntry entry : simulation.register)
				entry.isValid = 0;
			
		} while (choice == 'y');
		input.close();
		// simulation.displayRAM();
		// simulation.displayHardDisk();
	}
}
