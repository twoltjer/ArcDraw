import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

	public final static boolean debug = false;
	public static int seedLength = 0;

	/**
	 * Main method. Runs when program starts
	 * 
	 * @param args
	 *            arguments (that do nothing)
	 */
	public static void main(String[] args) {

		arcMain(args);

	}

	/**
	 * This method does everything! Mostly though, it just manages other tasks.
	 * 
	 * @param args
	 *            arguments that don't do anything
	 */
	public static void arcMain(String args[]) {
		// Claculate canvas size
		int newImageWidth = 6 * 7 + 12 + 1; // This is pretty silly, but
											// changing it will make the program
											// explode
		int newImageHeight = 6 * 13 + 1; // same

		// Create every possibility of drawing and find the best one
		SeedBuilder.imageTestInfo(newImageWidth, newImageHeight);
		SeedList seeds = SeedBuilder.buildSeeds();

		SeedList workingSeeds = new SeedList();
		int seedCount = 0;
		for (long i = 0; i < seeds.size(); i++) {
			SeedObj seed = seeds.get(i);
			seedCount++;
			System.out.println("Double checking seed " + seedCount + " of " + seeds.size());
			BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < newImageHeight; y++) {
				for (int x = 0; x < newImageWidth; x++) {
					newImage.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
			boolean worked = makeArcs(newImage, seed.getDirSeed(), seed.getRotSeed());
			if (worked) {
				workingSeeds.add(seed);
				if (debug)
					System.out.println("Image success");
			} else {
				if (debug)
					System.out.println("Image fail");
			}
			newImage.flush();
		}
		System.out.println("Done! " + workingSeeds.size() + " good seeds found");

		for (int i = 0; i < workingSeeds.size(); i++) {
			ScoreImage.scoreBoard(workingSeeds.get(i));
		}
		ScoreImage.displayTop(4); // number of top images to show

		// Below is old code for displaying the generated images. Now,
		// ScoreImage.displayTop(int) does that
		/*
		 * int seedCount1 = 0; SeedList displaySeeds = new SeedList(); for(int i
		 * = 0; i < workingSeeds.size(); i++) { SeedObj seed =
		 * workingSeeds.get(i); if(workingSeeds.indexOf(seed)%11133 == 0) {
		 * seedCount1 += 11133; System.out.println("Added seed " +
		 * seed.getDirSeed() + "x" + seed.getRotSeed() +
		 * " to display list  ||  " + ((int) (seedCount1 * 100 /
		 * workingSeeds.size()))); displaySeeds.add(seed); }
		 * 
		 * }
		 * 
		 * for(int i = 0; i < displaySeeds.size(); i++) { JFrame arcFrame = new
		 * JFrame("ARC-" + i + "00");
		 * arcFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * BufferedImage newImage = new BufferedImage(newImageWidth,
		 * newImageHeight, BufferedImage.TYPE_INT_RGB); for (int y = 0; y <
		 * newImageHeight; y++) { for (int x = 0; x < newImageWidth; x++) {
		 * newImage.setRGB(x, y, Color.WHITE.getRGB()); } } makeArcs(newImage,
		 * displaySeeds.get(i).getDirSeed(), displaySeeds.get(i).getRotSeed());
		 * for (int y = 0; y < newImageHeight; y++) { for (int x = 0; x <
		 * newImageWidth; x++) { if(newImage.getRGB(x, y) ==
		 * Color.WHITE.getRGB()) { newImage.setRGB(x, y, Color.BLACK.getRGB());
		 * } } } newImage = quadImage(quadImage(quadImage(newImage)));
		 * arcFrame.add(new JLabel(new ImageIcon(newImage))); arcFrame.pack();
		 * arcFrame.setVisible(true); }
		 */

	}

	/**
	 * Tries painting arcs onto image, which are determined by the direction
	 * seed and rotation seed. Both of these seeds are stored in a typical
	 * SeedObj object.
	 * 
	 * @param drawnImage
	 *            image to draw on
	 * @param seed
	 *            direction seed
	 * @param seed2
	 *            rotation seed
	 * @return success or failure
	 */
	public static boolean makeArcs(BufferedImage drawnImage, String seed, String seed2) {
		int currentX = 0;
		int currentY = 0;
		int seedPos = 0;
		char bannedDirection1 = 'N'; // These are set so that circles and
										// continued curves do not happen
		char bannedDirection2 = 'W'; // Starting in the upper left hand corner,
										// the default bans are North and West
		while (seedPos < seed.length()) {
			boolean rotClockwise = false;
			char directionLetter = seed.charAt(seedPos);
			char curveDirection = ' ';
			/*
			 * if (directionLetter == 'N') {
			 * System.out.println("To the North!"); } else if (directionLetter
			 * == 'S') { System.out.println("To the South!"); } else if
			 * (directionLetter == 'E') { System.out.println("To the East!"); }
			 * else if (directionLetter == 'W') {
			 * System.out.println("To the West!"); } else {
			 * System.out.println("Oh dear, something went wrong!"); }
			 */
			char rotationLetter = seed2.charAt(seedPos);
			if (rotationLetter == 'c') {
				rotClockwise = true;
			} else if (rotationLetter == 'C') {
				rotClockwise = false;
			} else {
				System.out.println("Oh dear, something spun wrong!");
			}

			// Check if the direction requested is banned
			if (directionLetter == bannedDirection1 || directionLetter == bannedDirection2) {
				// System.out.println("Seed requested banned direction");
				return false;
			}

			// find end coords
			int endX = currentX;
			int endY = currentY;
			if (directionLetter == 'N') {
				endY = currentY - 12;
			} else if (directionLetter == 'S') {
				endY = currentY + 12;
			} else if (directionLetter == 'E') {
				endX = currentX + 12;
			} else if (directionLetter == 'W') {
				endX = currentX - 12;
			} else {
				System.out.println("Oh dear, something went wrong!");
			}

			// check if end coords are on the canvas
			if ((endX < 0 || endY < 0) || (endX >= drawnImage.getWidth() || endY >= drawnImage.getHeight())) {
				if (debug)
					System.out.println("Coords not on canvas");
				return false;
			}
			// get curve direction
			if (directionLetter == 'N') {
				if (rotClockwise) {
					curveDirection = 'W';
				} else {
					curveDirection = 'E';
				}
			} else if (directionLetter == 'S') {
				if (!rotClockwise) {
					curveDirection = 'W';
				} else {
					curveDirection = 'E';
				}
			} else if (directionLetter == 'E') {
				if (rotClockwise) {
					curveDirection = 'N';
				} else {
					curveDirection = 'S';
				}
			} else if (directionLetter == 'W') {
				if (!rotClockwise) {
					curveDirection = 'N';
				} else {
					curveDirection = 'S';
				}
			} else {
				System.out.println("Oh dear, something went wrong!");
			}

			// check if curve goes off canvas
			if (curveDirection == 'N') {
				if (currentY < 6) {
					if (debug)
						System.out.println("Arc will go too far up");
					return false;
				}
			} else if (curveDirection == 'S') {
				if (currentY > (drawnImage.getHeight() - 6)) {
					if (debug)
						System.out.println("Arc will go too far down");
					return false;
				}
			} else if (curveDirection == 'E') {
				if (currentX > (drawnImage.getWidth() - 6)) {
					if (debug)
						System.out.println("Arc will go too far right");
					return false;
				}
			} else if (curveDirection == 'W') {
				if (currentX < 6) {
					if (debug)
						System.out.println("Arc will go too far left");
					return false;
				}
			}

			// draw the arc
			// while drawing, check if any of the pixels are changing
			// if not, scrap the image
			boolean sameCurve = false;
			ArrayList<Pixel> points = addPoints(directionLetter, rotClockwise, currentX, currentY);
			for (Pixel p : points) {
				p.setImage(drawnImage);
				int pColor = p.getImage().getRGB(p.getX(), p.getY());
				if (pColor == Color.WHITE.getRGB()) {
					sameCurve = true;
				}
				p.drawColor();
				drawnImage = p.getImage();
				p.clearImage();
			}

			// This had a tendency to call false positives, so it's going to
			// have to stay out for now. Trying to fix it!
			/*
			 * if (!sameCurve) { if(debug) System.out.
			 * println("Same curve! Overridden, continuing processing this arc..."
			 * ); return false; }
			 */
			
			// Final sets
			seedPos++;
			if (directionLetter == 'N') {
				bannedDirection1 = 'N';
				bannedDirection2 = 'S';
				currentY -= 12;
			} else if (directionLetter == 'S') {
				bannedDirection1 = 'N';
				bannedDirection2 = 'S';
				currentY += 12;
			} else if (directionLetter == 'E') {
				bannedDirection1 = 'E';
				bannedDirection2 = 'W';
				currentX += 12;
			} else if (directionLetter == 'W') {
				bannedDirection1 = 'E';
				bannedDirection2 = 'W';
				currentX -= 12;
			} else {
				System.out.println("Oh dear, something sent wrong!");
			}
		}
		return true;
	}

	/**
	 * Create a list of pixels where to draw the arc, based on the arguments given
	 * @param directionLetter the direction the arc is supposed to go in
	 * @param rotClockwise the rotation (clockwise or counter-clockwise) that the arc moves
	 * @param currentX Starting point X
	 * @param currentY Starting point Y
	 * @return A list of pixels where to draw
	 */
	private static ArrayList<Pixel> addPoints(char directionLetter, boolean rotClockwise, int currentX, int currentY) {
		ArrayList<Pixel> points = new ArrayList<Pixel>();

		if (directionLetter == 'N') {
			if (rotClockwise) {
				// set points to color
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX - 1, currentY));
				points.add(new Pixel(currentX - 2, currentY));
				points.add(new Pixel(currentX - 3, currentY - 1));
				points.add(new Pixel(currentX - 4, currentY - 1));
				points.add(new Pixel(currentX - 4, currentY - 2));
				points.add(new Pixel(currentX - 5, currentY - 2));
				points.add(new Pixel(currentX - 5, currentY - 3));
				points.add(new Pixel(currentX - 6, currentY - 4));
				points.add(new Pixel(currentX - 6, currentY - 5));
				points.add(new Pixel(currentX - 6, currentY - 6));
				points.add(new Pixel(currentX - 6, currentY - 7));
				points.add(new Pixel(currentX - 6, currentY - 8));
				points.add(new Pixel(currentX - 5, currentY - 9));
				points.add(new Pixel(currentX - 5, currentY - 10));
				points.add(new Pixel(currentX - 4, currentY - 10));
				points.add(new Pixel(currentX - 4, currentY - 11));
				points.add(new Pixel(currentX - 3, currentY - 11));
				points.add(new Pixel(currentX - 2, currentY - 12));
				points.add(new Pixel(currentX - 1, currentY - 12));
				points.add(new Pixel(currentX, currentY - 12));
			} else {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX + 1, currentY));
				points.add(new Pixel(currentX + 2, currentY));
				points.add(new Pixel(currentX + 3, currentY - 1));
				points.add(new Pixel(currentX + 4, currentY - 1));
				points.add(new Pixel(currentX + 4, currentY - 2));
				points.add(new Pixel(currentX + 5, currentY - 2));
				points.add(new Pixel(currentX + 5, currentY - 3));
				points.add(new Pixel(currentX + 6, currentY - 4));
				points.add(new Pixel(currentX + 6, currentY - 5));
				points.add(new Pixel(currentX + 6, currentY - 6));
				points.add(new Pixel(currentX + 6, currentY - 7));
				points.add(new Pixel(currentX + 6, currentY - 8));
				points.add(new Pixel(currentX + 5, currentY - 9));
				points.add(new Pixel(currentX + 5, currentY - 10));
				points.add(new Pixel(currentX + 4, currentY - 10));
				points.add(new Pixel(currentX + 4, currentY - 11));
				points.add(new Pixel(currentX + 3, currentY - 11));
				points.add(new Pixel(currentX + 2, currentY - 12));
				points.add(new Pixel(currentX + 1, currentY - 12));
				points.add(new Pixel(currentX, currentY - 12));
			}
		}
		if (directionLetter == 'S') {
			if (rotClockwise) {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX + 1, currentY));
				points.add(new Pixel(currentX + 2, currentY));
				points.add(new Pixel(currentX + 3, currentY + 1));
				points.add(new Pixel(currentX + 4, currentY + 1));
				points.add(new Pixel(currentX + 4, currentY + 2));
				points.add(new Pixel(currentX + 5, currentY + 2));
				points.add(new Pixel(currentX + 5, currentY + 3));
				points.add(new Pixel(currentX + 6, currentY + 4));
				points.add(new Pixel(currentX + 6, currentY + 5));
				points.add(new Pixel(currentX + 6, currentY + 6));
				points.add(new Pixel(currentX + 6, currentY + 7));
				points.add(new Pixel(currentX + 6, currentY + 8));
				points.add(new Pixel(currentX + 5, currentY + 9));
				points.add(new Pixel(currentX + 5, currentY + 10));
				points.add(new Pixel(currentX + 4, currentY + 10));
				points.add(new Pixel(currentX + 4, currentY + 11));
				points.add(new Pixel(currentX + 3, currentY + 11));
				points.add(new Pixel(currentX + 2, currentY + 12));
				points.add(new Pixel(currentX + 1, currentY + 12));
				points.add(new Pixel(currentX, currentY + 12));
			} else {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX - 1, currentY));
				points.add(new Pixel(currentX - 2, currentY));
				points.add(new Pixel(currentX - 3, currentY + 1));
				points.add(new Pixel(currentX - 4, currentY + 1));
				points.add(new Pixel(currentX - 4, currentY + 2));
				points.add(new Pixel(currentX - 5, currentY + 2));
				points.add(new Pixel(currentX - 5, currentY + 3));
				points.add(new Pixel(currentX - 6, currentY + 4));
				points.add(new Pixel(currentX - 6, currentY + 5));
				points.add(new Pixel(currentX - 6, currentY + 6));
				points.add(new Pixel(currentX - 6, currentY + 7));
				points.add(new Pixel(currentX - 6, currentY + 8));
				points.add(new Pixel(currentX - 5, currentY + 9));
				points.add(new Pixel(currentX - 5, currentY + 10));
				points.add(new Pixel(currentX - 4, currentY + 10));
				points.add(new Pixel(currentX - 4, currentY + 11));
				points.add(new Pixel(currentX - 3, currentY + 11));
				points.add(new Pixel(currentX - 2, currentY + 12));
				points.add(new Pixel(currentX - 1, currentY + 12));
				points.add(new Pixel(currentX, currentY + 12));
			}
		}
		if (directionLetter == 'E') {
			if (rotClockwise) {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX, currentY - 1));
				points.add(new Pixel(currentX, currentY - 2));
				points.add(new Pixel(currentX + 1, currentY - 3));
				points.add(new Pixel(currentX + 1, currentY - 4));
				points.add(new Pixel(currentX + 2, currentY - 4));
				points.add(new Pixel(currentX + 2, currentY - 5));
				points.add(new Pixel(currentX + 3, currentY - 5));
				points.add(new Pixel(currentX + 4, currentY - 6));
				points.add(new Pixel(currentX + 5, currentY - 6));
				points.add(new Pixel(currentX + 6, currentY - 6));
				points.add(new Pixel(currentX + 7, currentY - 6));
				points.add(new Pixel(currentX + 8, currentY - 6));
				points.add(new Pixel(currentX + 9, currentY - 5));
				points.add(new Pixel(currentX + 10, currentY - 5));
				points.add(new Pixel(currentX + 10, currentY - 4));
				points.add(new Pixel(currentX + 11, currentY - 4));
				points.add(new Pixel(currentX + 11, currentY - 3));
				points.add(new Pixel(currentX + 12, currentY - 2));
				points.add(new Pixel(currentX + 12, currentY - 1));
				points.add(new Pixel(currentX + 12, currentY));
			} else {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX, currentY + 1));
				points.add(new Pixel(currentX, currentY + 2));
				points.add(new Pixel(currentX + 1, currentY + 3));
				points.add(new Pixel(currentX + 1, currentY + 4));
				points.add(new Pixel(currentX + 2, currentY + 4));
				points.add(new Pixel(currentX + 2, currentY + 5));
				points.add(new Pixel(currentX + 3, currentY + 5));
				points.add(new Pixel(currentX + 4, currentY + 6));
				points.add(new Pixel(currentX + 5, currentY + 6));
				points.add(new Pixel(currentX + 6, currentY + 6));
				points.add(new Pixel(currentX + 7, currentY + 6));
				points.add(new Pixel(currentX + 8, currentY + 6));
				points.add(new Pixel(currentX + 9, currentY + 5));
				points.add(new Pixel(currentX + 10, currentY + 5));
				points.add(new Pixel(currentX + 10, currentY + 4));
				points.add(new Pixel(currentX + 11, currentY + 4));
				points.add(new Pixel(currentX + 11, currentY + 3));
				points.add(new Pixel(currentX + 12, currentY + 2));
				points.add(new Pixel(currentX + 12, currentY + 1));
				points.add(new Pixel(currentX + 12, currentY));
			}
		}
		if (directionLetter == 'W') {
			if (rotClockwise) {

				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX, currentY + 1));
				points.add(new Pixel(currentX, currentY + 2));
				points.add(new Pixel(currentX - 1, currentY + 3));
				points.add(new Pixel(currentX - 1, currentY + 4));
				points.add(new Pixel(currentX - 2, currentY + 4));
				points.add(new Pixel(currentX - 2, currentY + 5));
				points.add(new Pixel(currentX - 3, currentY + 5));
				points.add(new Pixel(currentX - 4, currentY + 6));
				points.add(new Pixel(currentX - 5, currentY + 6));
				points.add(new Pixel(currentX - 6, currentY + 6));
				points.add(new Pixel(currentX - 7, currentY + 6));
				points.add(new Pixel(currentX - 8, currentY + 6));
				points.add(new Pixel(currentX - 9, currentY + 5));
				points.add(new Pixel(currentX - 10, currentY + 5));
				points.add(new Pixel(currentX - 10, currentY + 4));
				points.add(new Pixel(currentX - 11, currentY + 4));
				points.add(new Pixel(currentX - 11, currentY + 3));
				points.add(new Pixel(currentX - 12, currentY + 2));
				points.add(new Pixel(currentX - 12, currentY + 1));
				points.add(new Pixel(currentX - 12, currentY));
			} else {
				points.add(new Pixel(currentX, currentY));
				points.add(new Pixel(currentX, currentY - 1));
				points.add(new Pixel(currentX, currentY - 2));
				points.add(new Pixel(currentX - 1, currentY - 3));
				points.add(new Pixel(currentX - 1, currentY - 4));
				points.add(new Pixel(currentX - 2, currentY - 4));
				points.add(new Pixel(currentX - 2, currentY - 5));
				points.add(new Pixel(currentX - 3, currentY - 5));
				points.add(new Pixel(currentX - 4, currentY - 6));
				points.add(new Pixel(currentX - 5, currentY - 6));
				points.add(new Pixel(currentX - 6, currentY - 6));
				points.add(new Pixel(currentX - 7, currentY - 6));
				points.add(new Pixel(currentX - 8, currentY - 6));
				points.add(new Pixel(currentX - 9, currentY - 5));
				points.add(new Pixel(currentX - 10, currentY - 5));
				points.add(new Pixel(currentX - 10, currentY - 4));
				points.add(new Pixel(currentX - 11, currentY - 4));
				points.add(new Pixel(currentX - 11, currentY - 3));
				points.add(new Pixel(currentX - 12, currentY - 2));
				points.add(new Pixel(currentX - 12, currentY - 1));
				points.add(new Pixel(currentX - 12, currentY));

			}
		}
		return points;
	}

	/**
	 * Find the opposite direction letter
	 * @param dir the direction to find the opposite of
	 * @return the opposite direction
	 */
	public static char oppositeDirection(char dir) {
		if (dir == 'N') {
			return 'S';
		} else if (dir == 'S') {
			return 'N';
		} else if (dir == 'E') {
			return 'W';
		} else if (dir == 'W') {
			return 'E';
		}
		System.out.println("Invalid direction to opposite: " + dir);
		System.exit(-1);
		return dir;
	}

	/**
	 * Quadruple size of the image, but doubling each of its dimensions. Each
	 * pixel in the original is represented using four in the returned image.
	 * 
	 * @param orig
	 *            The original image
	 * @return An enlarged image
	 */
	public static BufferedImage quadImage(BufferedImage orig) {
		// quadruples image size, by doubling dimensions
		if (debug)
			System.out.println("QUAD: Orig image dimensions: " + orig.getWidth() + " by " + orig.getHeight());
		BufferedImage newImg = new BufferedImage(orig.getWidth() * 2, orig.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
		if (debug)
			System.out.println("QUAD: New image dimensions: " + newImg.getWidth() + " by " + newImg.getHeight());
		for (int y = 0; y < newImg.getHeight(); y++) {
			for (int x = 0; x < newImg.getWidth(); x++) {
				// System.out.println("QUAD: Setting rgb: New image x: " + x + "
				// New image y: " + y + " Old image x: " + x/2 + " Old image y:
				// " + y/2);
				newImg.setRGB(x, y, orig.getRGB(x / 2, y / 2));
			}
		}
		return newImg;
	}
}
