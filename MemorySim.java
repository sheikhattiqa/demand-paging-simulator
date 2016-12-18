import java.util.Scanner;

class MemorySim {
	RefString rs;
	int[] removed;
	int[] pageChanged;
	int rsLen;
	int numOfPhysicalFrames;
	int numOfVirtualFrames;
	int[][] physicalMemory;
	Frame[] frameArray;
	String algoType;

	MemorySim(RefString refs, int phys, int virt) {
		rs = refs;
		rsLen = rs.getLength();
		removed = new int[rsLen];
		pageChanged = new int[rsLen];
		numOfPhysicalFrames = phys;
		numOfVirtualFrames = virt;
		physicalMemory = new int[rs.getLength()][phys];
		frameArray = new Frame[virt];
	}

	void generateFifo() {
		initialize();
		algoType = "FIFO";
		int currentSlice = 0;
		int frameToInsert;
		int empty;
		int frameToReplace;
		int[] listOfFrames;
		while (currentSlice < rsLen) {
			frameToInsert = rs.getAtIndex(currentSlice);
			empty = findIndex(physicalMemory[currentSlice], -1);
			// if the page we need is already in physical memory...
			if (findIndex(physicalMemory[currentSlice], frameToInsert) != -1) {
			}
			// if it's not in memory but there's an empty space for it...
			else if (empty >= 0) {
				pageChanged[currentSlice] = empty;
				physicalMemory[currentSlice][empty] = frameToInsert;
				frameArray[frameToInsert].setInserted(currentSlice);
			}
			// not in memory and no empty space
			else {
				// find the oldest frame
				frameToReplace = findOldest(physicalMemory[currentSlice]);
				// record removed frame
				removed[currentSlice] = physicalMemory[currentSlice][frameToReplace];
				// record new frame spot
				pageChanged[currentSlice] = frameToReplace;
				// put the new frame in that spot
				physicalMemory[currentSlice][frameToReplace] = frameToInsert;
				// update insertion time
				frameArray[frameToInsert].setInserted(currentSlice);


			}
			if ((currentSlice + 1) < rsLen) {
				for (int i = 0; i < numOfPhysicalFrames; i ++) {
					physicalMemory[currentSlice +1][i] = physicalMemory[currentSlice][i];
				}
			}
			currentSlice += 1;
		}
	}

	void generateOpt() {
		initialize();
		algoType = "OPT";
		int currentSlice = 0;
		int frameToInsert;
		int empty;
		int frameToReplace;
		int[] listOfFrames;

		while (currentSlice < rsLen) {
			// calculate next use for every frame
			calculateNextUse(currentSlice);

			frameToInsert = rs.getAtIndex(currentSlice);
			empty = findIndex(physicalMemory[currentSlice], -1);
			// if it's already in memory...
			if (findIndex(physicalMemory[currentSlice], frameToInsert) != -1) {
			}
			// if it's not in memory but there's an empty space for it...
			else if (empty >= 0) {
				pageChanged[currentSlice] = empty;
				physicalMemory[currentSlice][empty] = frameToInsert;
				frameArray[frameToInsert].setInserted(currentSlice);
			}
			// if it's not in memory and there's no empty space...
			else {
				// find the least optimal page
				frameToReplace = findLeastOptimal(physicalMemory[currentSlice]);
				// record replaced frame
				removed[currentSlice] = physicalMemory[currentSlice][frameToReplace];
				// record new frame spot
				pageChanged[currentSlice] = frameToReplace;
				// put in the new frame at that spot
				physicalMemory[currentSlice][frameToReplace] = frameToInsert;
			}
			if ((currentSlice + 1) < rsLen) {
				for (int i = 0; i < numOfPhysicalFrames; i ++) {
					physicalMemory[currentSlice +1][i] = physicalMemory[currentSlice][i];
				}
			}

			currentSlice += 1;

		}
	}

	void generateLru() {
		initialize();
		algoType = "LRU";
		int currentSlice = 0;
		int frameToInsert;
		int empty;
		int frameToReplace;
		int[] listOfFrames;
		while (currentSlice < rsLen) {
			// calculate next use for every frame
			frameToInsert = rs.getAtIndex(currentSlice);
			empty = findIndex(physicalMemory[currentSlice], -1);
			// if it's already in memory...
			if (findIndex(physicalMemory[currentSlice], frameToInsert) != -1) {
			}
			// if it's not in memory but there's an empty space for it...
			else if (empty >= 0) {
				pageChanged[currentSlice] = empty;
				physicalMemory[currentSlice][empty] = frameToInsert;
				frameArray[frameToInsert].setInserted(currentSlice);
			}
			// if it's not in memory and there's no empty space...
			else {
				// find least recently used
				frameToReplace = findLru(physicalMemory[currentSlice]);
				// record removed
				removed[currentSlice] = physicalMemory[currentSlice][frameToReplace];
				// record new frame spot
				pageChanged[currentSlice] = frameToReplace;
				// replace it
				physicalMemory[currentSlice][frameToReplace] = frameToInsert;
			}
			if ((currentSlice + 1) < rsLen) {
				for (int i = 0; i < numOfPhysicalFrames; i ++) {
					physicalMemory[currentSlice +1][i] = physicalMemory[currentSlice][i];
				}
			}
			frameArray[frameToInsert].setLastUse(currentSlice);
			currentSlice += 1;
		}
	}

	void generateLfu() {
		initialize();
		algoType = "LFU";
		int currentSlice = 0;
		int frameToInsert;
		int empty;
		int frameToReplace;
		int[] listOfFrames;
		while (currentSlice < rsLen) {
			// calculate next use for every frame
			frameToInsert = rs.getAtIndex(currentSlice);
			empty = findIndex(physicalMemory[currentSlice], -1);
			// if it's already in memory...
			if (findIndex(physicalMemory[currentSlice], frameToInsert) != -1) {
			}
			// if it's not in memory but there's an empty space for it...
			else if (empty >= 0) {
				pageChanged[currentSlice] = empty;
				physicalMemory[currentSlice][empty] = frameToInsert;
				frameArray[frameToInsert].setInserted(currentSlice);
			}
			// if it's not in memory and there's no empty space...
			else {
				// find least recently used
				frameToReplace = findLfu(physicalMemory[currentSlice]);
				// record it
				removed[currentSlice] = physicalMemory[currentSlice][frameToReplace];
				// record new frame spot
				pageChanged[currentSlice] = frameToReplace;
				// replace it
				physicalMemory[currentSlice][frameToReplace] = frameToInsert;
			}
			if ((currentSlice + 1) < rsLen) {
				for (int i = 0; i < numOfPhysicalFrames; i ++) {
					physicalMemory[currentSlice +1][i] = physicalMemory[currentSlice][i];
				}
			}
			frameArray[frameToInsert].incrementTimesUsed();
			currentSlice += 1;
		}
	}

	int findLfu(int[] a) {
		int lfuIndex = 0;
		int lfuTimesUsed = frameArray[a[lfuIndex]].getTimesUsed();

		for (int i = 1; i < a.length; i++) {
			int temp = a[i];
			int tempTimesUsed = frameArray[a[i]].getTimesUsed();

			if (tempTimesUsed < lfuTimesUsed) {
				lfuIndex = i;
				lfuTimesUsed = tempTimesUsed;
			}
		}

		return lfuIndex;
	}
	int findLru(int[] a) {
		int lruIndex = 0;
		int lruLastUse = frameArray[a[lruIndex]].getLastUse();

		for (int i = 1; i < a.length; i++) {
			int temp = a[i];
			int tempLastUse = frameArray[a[i]].getLastUse();

			if (tempLastUse < lruLastUse) {
				lruIndex = i;
				lruLastUse = tempLastUse;

			}
		}

		return lruIndex;
	}

	int findLeastOptimal(int[] a) {
		int leastOptimal = a[0];
		int leastOptimalIndex = 0;
		int leastOptNextUse = frameArray[leastOptimal].getNextUse();
		for (int i = 1; i < a.length; i++) {
			int temp = a[i];
			int tempNextUse = frameArray[temp].getNextUse();
			if (tempNextUse > leastOptNextUse) {
				leastOptimal = temp;
				leastOptNextUse = frameArray[leastOptimal].getNextUse();
				leastOptimalIndex = i;
			}
		}
		return leastOptimalIndex;
	}

	void calculateNextUse(int n) {
		for (int i = 0; i < numOfVirtualFrames; i++) {
			frameArray[i].setNextUse(rsLen + 1);
		}
		for (int i = rsLen - 1; i >= n; i--) {
			int called = rs.getAtIndex(i);
			frameArray[called].setNextUse(i);
		}
	}

	void initialize() {
		// set removed to -1s
		for (int i = 0; i < removed.length; i++) {
			removed[i] = -1;
		}
		// set pages changed to -1s
		for (int i = 0; i < pageChanged.length; i++) {
			pageChanged[i] = -1;
		}
		// set clean array of frames:
		for (int i = 0; i < numOfVirtualFrames; i++) {
			frameArray[i] = new Frame(i);
		}
		// clean array of slices
		for (int i = 0; i < rsLen; i++) {
			for (int j = 0; j < numOfPhysicalFrames; j ++) {
				physicalMemory[i][j] = -1;
			}
		}
		algoType = "";
	}

	int findOldest(int[] a) {
		int oldest = frameArray[a[0]].getInserted();
		int oldestIndex = 0;
		int checking;
		for (int i = 1; i < a.length; i++) {
			checking = frameArray[a[i]].getInserted();
			if (checking < oldest) {
				oldest = checking;
				oldestIndex = i;
			}
		}
		return oldestIndex;
	}



	void print() {
		System.out.println("Basic information: ");
		System.out.println("Algo type: " + algoType);
		System.out.println("Length of reference string: " + rsLen);
		System.out.println("Number of virtual pages: " + numOfVirtualFrames);
		System.out.println("Number of physical pages: " + numOfPhysicalFrames);
		System.out.println("[brackets] around a page number indicate it was changed.");
		System.out.println("Press enter to step through snapshots of physical memory after each string call. Or, enter \"q\" at any time to return to main menu.");

		Scanner sc = new Scanner(System.in);
		int steppingSlice = 0;
		String prompt;
		int frameNum;
		int removedInt;
		while (steppingSlice < rsLen) {
			prompt = sc.nextLine();
			if (prompt.equals("q")) {
				System.out.println("Quitting printout.");
				break;
			}
			System.out.println("Snapshot at time " + steppingSlice + ":");
			System.out.println("Program called virtual frame # " + rs.getAtIndex(steppingSlice));
			for (int i = 0; i < numOfPhysicalFrames; i ++) {
				System.out.print("Physical frame " + i + ":");
				frameNum = physicalMemory[steppingSlice][i];
				if (frameNum >= 0) {
					if (i == pageChanged[steppingSlice]) {
						System.out.println("[" + frameNum + "]");
					} else {
						System.out.println(" " + frameNum);
					}
				} else {
					System.out.println("x");
				}
			}
			removedInt = removed[steppingSlice];
			System.out.println("Frame removed: " + (removedInt == -1 ? "None." : removedInt));
			steppingSlice += 1;
		}
		System.out.print("Simluation finished. Press enter to continue.");
		sc.nextLine();
	}

	int findIndex(int[] a, int n) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == n) {
				return i;
			}
		}
		return -1;
	}
}
