import java.util.ArrayList;

/*
 * This class allows me to hold over 4 pentillion seeds in a list, rather than the 2,147,483,647 that an ArrayList supports. Testing reveals that while this supports higher numbers, it also drastically increases the processing power needed to run operations. Therefore, this is ideal for the large numbers of pixels that we are using
 */

public class SeedList {
	private final int MAX_IN_LIST_SIZE = 20000000;
	private ArrayList<ArrayList<SeedObj>> seedList;
	private long size;

	public SeedList() {
		size = 0;
		seedList = new ArrayList<ArrayList<SeedObj>>();
		seedList.add(new ArrayList<SeedObj>());
	}

	public long indexOf(SeedObj seed) {
		long index = 0;
		for (int i = 0; i < seedList.size(); i++) {
			if (seedList.get(i).indexOf(seed) != -1) {
				index += seedList.get(i).indexOf(seed);
				return index;
			}
			index += MAX_IN_LIST_SIZE;
		}

		return -1;
	}

	public SeedObj get(long seedNumber) {
		int inListNum = 0;
		while (seedNumber >= MAX_IN_LIST_SIZE) {
			inListNum++;
			seedNumber -= MAX_IN_LIST_SIZE;
		}
		return seedList.get(inListNum).get((int) seedNumber);
	}

	public void clear() {
		for (ArrayList<SeedObj> inList : seedList) {
			inList.clear();
		}
		seedList.clear();
		size = 0;
	}

	public long size() {
		return size;
	}

	public void add(SeedObj seed) {
		int indexOfFirstNonFullList = 0;
		boolean foundNonFullList = false;
		// check if it exists
		while (!foundNonFullList) {
			try {
				int nonFullListSize = seedList.get(indexOfFirstNonFullList).size();
				if (nonFullListSize == MAX_IN_LIST_SIZE) {
					// list full
					indexOfFirstNonFullList++;
				} else {
					// list instantiated and not full!
					foundNonFullList = true;
				}
			} catch (Exception e) {
				// need to instantiate new list
				foundNonFullList = true;
				seedList.add(new ArrayList<SeedObj>());
			}
		}
		seedList.get(indexOfFirstNonFullList).add(seed);
		// System.out.println("Index of first non full list: " +
		// indexOfFirstNonFullList);
		// System.out.println("Size of that list: " +
		// seedList.get(indexOfFirstNonFullList).size());
		size++;
	}

	public int getFirstSeedLength() {
		try {
			ArrayList<SeedObj> insideArray = seedList.get(0);
			SeedObj firstSeed = insideArray.get(0);
			return firstSeed.getSeedLength();
		} catch (Exception e) {
			return 1;
		}
	}

	public int getInListNumber() {
		return seedList.size() - 1;
	}
}
