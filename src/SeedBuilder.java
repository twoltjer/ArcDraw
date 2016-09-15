import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Class for building seeds
 * 
 * @author Thomas Woltjer
 */
public class SeedBuilder {
	private static SeedList seeds;
	private static SeedList tempSeeds;
	private static int newImageWidth;
	private static int newImageHeight;
	private static final int GOAL_LENGTH = 10;

	/**
	 * Make some seeds. This can take a very long time, especially with a great
	 * GOAL_LENGTH
	 * 
	 * @return the seedlist that holds all the final seeds, which have been
	 *         tested once.
	 */
	public static SeedList buildSeeds() {
		seeds = new SeedList();
		tempSeeds = new SeedList();

		seeds.add(new SeedObj());
		System.out.println("Added first seed. Number of seeds: " + seeds.size());
		generateSeeds();
		return seeds;
	}

	/**
	 * Generates seeds. Note that this method is recursive, and only stops
	 * calling itself once seeds are long enough.
	 */
	public static void generateSeeds() {
		if (seeds.get(0).getSeedLength() < GOAL_LENGTH) {
			for (long i = 0; i < seeds.size(); i++) {
				SeedObj seed = seeds.get(i);
				for (int dirNumber = 0; dirNumber < 4; dirNumber++) {
					for (int rotNum = 0; rotNum < 2; rotNum++) {
						SeedObj newSeed = new SeedObj();
						newSeed.mimic(seed);
						char dirChar = ' ';
						char rotChar = ' ';
						if (dirNumber == 0) {
							dirChar = 'N';
						}
						if (dirNumber == 1) {
							dirChar = 'S';
						}
						if (dirNumber == 2) {
							dirChar = 'E';
						}
						if (dirNumber == 3) {
							dirChar = 'W';
						}
						if (rotNum == 0) {
							rotChar = 'c';
						}
						if (rotNum == 1) {
							rotChar = 'C';
						}
						newSeed.buildSeed(dirChar, rotChar);
						tempSeeds.add(newSeed);
						// System.out.println("Temp seeds: " +
						// tempSeeds.size());
					}
				}

			}
			seeds.clear();
			seeds = new SeedList();
			long testCount = 0;
			int testCountPercent = 0;
			int totalPercent = 0;
			for (long i = 0; i < tempSeeds.size(); i++) {
				SeedObj s = tempSeeds.get(i);
				testCount++;
				if (testCount % 2500 == 0) {
					String testCountPercentFormatted = Integer.toString(testCountPercent);
					while (testCountPercentFormatted.length() < 3)
						testCountPercentFormatted = " " + testCountPercentFormatted;
					System.out.println(testCountPercentFormatted + "%");
					testCountPercent += 10;
				}
				if (testCount % 25000 == 0) {
					System.out.println("Testing image " + testCount + " of " + tempSeeds.size());
					System.out.println("Number of successes: " + seeds.size());
					System.out.println("Seed being tested: " + s.getDirSeed() + "x" + s.getRotSeed());
					System.out.println("INArrays: " + seeds.getInListNumber());
					System.out.println("Seed length: " + (seeds.get(0).getSeedLength() - 1));
					testCountPercent = 0;
				}
				try {
					if (testCount % (tempSeeds.size() / 100) == 0) {
						String totalPercentFormatted = Integer.toString(totalPercent);
						while (totalPercentFormatted.length() < 3)
							totalPercentFormatted = " " + totalPercentFormatted;
						System.out.println("\t\t\t" + totalPercentFormatted + "% done with this set");
						totalPercent += 1;
					}
				} catch (Exception e) {
					// toodaloo!
				}

				// System.out.println("Testing image " + i);

				// test seed here, so that we don't overflow on number of seeds
				BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_RGB);
				for (int y = 0; y < newImageHeight; y++) {
					for (int x = 0; x < newImageWidth; x++) {
						newImage.setRGB(x, y, Color.WHITE.getRGB());
					}
				}
				if (Main.makeArcs(newImage, s.getDirSeed(), s.getRotSeed())) {
					seeds.add(s);
					// System.out.println("Test success. Seeds length: " +
					// seeds.size());
				} else {
					// System.out.println("Test fail");
				}
				newImage.flush();
			}
			tempSeeds.clear();

			System.out.println("New seed length: " + seeds.get(0).getSeedLength());
			generateSeeds();
		}
	}

	/**
	 * Sets test information. This is needed to test images here, which saves
	 * much time compared to doing it later.
	 * 
	 * @param newImageWidth
	 *            image width
	 * @param newImageHeight
	 *            image height
	 */
	public static void imageTestInfo(int newImageWidth, int newImageHeight) {
		SeedBuilder.newImageWidth = newImageWidth;
		SeedBuilder.newImageHeight = newImageHeight;
	}

	/**
	 * Gives image dimensions
	 * @return dimensions of the image
	 */
	public static Dimension getImageDimensions() {
		Dimension d = new Dimension(newImageWidth, newImageHeight);
		return d;
	}
}
