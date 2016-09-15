import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Class to score images and keep track of/display the top few
 * 
 * @author Thomas Woltjer
 */
public class ScoreImage {
	/**
	 * Image to be saved
	 */
	public static BufferedImage saveImage;
	/**
	 * Number image being scored. Gives the user some information while they are
	 * being scored.
	 */
	public static int imgNum = 0;
	/**
	 * The highest scoring 10 seeds
	 */
	public static ArrayList<SeedObj> highestScoringSeeds = new ArrayList<SeedObj>();

	/**
	 * Get the score of an image
	 * 
	 * @param img
	 *            the image to score
	 * @return the score of the image
	 */
	public static int scoreRainbowImage(BufferedImage img) {
		int score = 0;
		System.out.println("Scoring image " + imgNum);
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				if (img.getRGB(x, y) != Color.BLACK.getRGB()) {
					score += ((img.getWidth() - x) * (img.getWidth() - y));
					// To get a random image displayed, comment out the above
					// and uncomment below
					// score += (int) (Math.random() * 100);
				}
			}
		}
		imgNum++;
		System.out.println("Score: " + score);
		return score;
	}

	/**
	 * Test a seed's score (using score()) and put it onto the scoreboard if it
	 * scores high
	 * 
	 * @param seed
	 *            the seed object
	 */
	public static void scoreBoard(SeedObj seed) {
		boolean addToScoreboard = false;
		int seedScore = score(seed);
		if (highestScoringSeeds.size() < 10) {
			addToScoreboard = true;
		} else {
			if (seedScore > score(highestScoringSeeds.get(9))) {
				addToScoreboard = true;
			}
		}
		if (addToScoreboard) {
			int seedPlace = 9;
			try {
				while (seedScore > score(highestScoringSeeds.get(seedPlace - 1))) {
					seedPlace -= 1;
				}
				highestScoringSeeds.add(seedPlace, seed);
			} catch (Exception e) {
				highestScoringSeeds.add(0, seed);
			}
			System.out.println("Won place " + seedPlace);
		}
	}

	/**
	 * Internal score method. Keeping this separate makes things cleaner.
	 * 
	 * @param seed
	 *            the seed object
	 * @return the score of the seed
	 */
	public static int score(SeedObj seed) {
		System.out.println("Entering score method");
		Dimension imgSize = SeedBuilder.getImageDimensions();
		BufferedImage img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < imgSize.height; y++) {
			for (int x = 0; x < imgSize.getWidth(); x++) {
				img.setRGB(x, y, Color.BLACK.getRGB());
			}
		}
		int score = -1;
		if (Main.makeArcs(img, seed.getDirSeed(), seed.getRotSeed())) {
			// Tested successful
			score = scoreRainbowImage(img);
		}
		img.flush();
		System.out.println("Exiting score method with status of " + score);
		return score;
	}

	/**
	 * Displays the top few images in highestScoringSeeds
	 * 
	 * @param numberToDisplay
	 *            the number of images to display. If more than 10, only 10 will
	 *            be displayed, as that's the number held in the scoreboard.
	 */
	public static void displayTop(int numberToDisplay) {
		for (int i = 0; i < lesserOf(numberToDisplay, highestScoringSeeds.size()); i++) {
			SeedObj seed = highestScoringSeeds.get(i);
			Dimension imgSize = SeedBuilder.getImageDimensions();
			BufferedImage img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < imgSize.height; y++) {
				for (int x = 0; x < imgSize.width; x++) {
					img.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			Main.makeArcs(img, seed.getDirSeed(), seed.getRotSeed());

			BufferedImage quadImg = new BufferedImage(imgSize.width * 2, imgSize.height * 2,
					BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < quadImg.getHeight(); y++) {
				for (int x = 0; x < quadImg.getWidth(); x++) {
					quadImg.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			for (int y = 0; y < imgSize.height; y++) {
				for (int x = 0; x < imgSize.width; x++) {
					quadImg.setRGB(imgSize.width - x, imgSize.height - y, img.getRGB(x, y));
					quadImg.setRGB(x + imgSize.width, imgSize.height - y, img.getRGB(x, y));
					quadImg.setRGB(imgSize.width - x, y + imgSize.height, img.getRGB(x, y));
					quadImg.setRGB(x + imgSize.width, y + imgSize.height, img.getRGB(x, y));

				}
			}
			JFrame arcFrame = new JFrame("Place: " + i);
			quadImg = Main.quadImage(Main.quadImage(quadImg));
			img = Main.quadImage(Main.quadImage(Main.quadImage(img)));
			arcFrame.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 0;
			arcFrame.add(new JLabel(new ImageIcon(quadImg)), c);
			class SaveButtonClick implements ActionListener {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton src = (JButton) e.getSource();
					Window srcWin = (Window) src.getTopLevelAncestor();
					System.out.println("Window name : " + srcWin.getName());
					int numTaken = 0;
					boolean nextNum = true;
					File outputFile = null;
					while (nextNum) {
						outputFile = new File("ArcDraw-" + numTaken + ".png");
						nextNum = outputFile.exists();
						numTaken += 1;
					}
					try {
						ImageIO.write(saveImage, "png", outputFile);
						System.out.println("Image saved to " + outputFile.getAbsolutePath());
					} catch (Exception e1) {
						System.err.println("Error saving image");
					}
				}
			}
			JButton saveButton = new JButton("Save image");
			c.gridy = 1;
			saveButton.addActionListener(new SaveButtonClick());
			arcFrame.add(saveButton, c);
			arcFrame.pack();
			arcFrame.setVisible(true);
			arcFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	private static int lesserOf(int a, int b) {
		if (a < b)
			return a;
		return b;
	}

}
