import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Stores pixel data, allows drawing to images, and gets information from the image.
 * @author Thomas Woltjer
 */
public class Pixel {
	private int x;
	private int y;
	private BufferedImage i;
	/**
	 * Creates a Pixel object
	 * @param x The pixel's x-coordinate
	 * @param y The pixel's y-coordinate
	 */
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the x-coordinate of the pixel
	 * @return The x-coord
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Gets the y-coordinate of the pixel
	 * @return The y-coord
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Sets the image that this pixel will be drawing on
	 * @param i The image
	 */
	public void setImage(BufferedImage i) {
		this.i = i;
	}
	
	/**
	 * Return true if the pixel is not white, and false if it is
	 * @return a boolean value
	 */
	public boolean isNotWhite() {
		int pix = this.i.getRGB(x, y);
		int col = Color.WHITE.getRGB();
		//System.out.println("Pixel RGB is: " + pix);
		//System.out.println("Black RGB is: " + col);
		
		boolean isWhite = (pix == col);
		boolean isNotWhite = !isWhite;
		return isNotWhite;
	}
	
	/**
	 * Draw a color (from the DrawRainbow class) to the pixel
	 */
	public void drawColor() {
		this.i.setRGB(x, y, DrawRainbow.getNextColor().getRGB());
	}
	
	/**
	 * Gives the image back, once processing is done.
	 * @return the image
	 */
	public BufferedImage getImage() {
		return this.i;
	}
	
	/**
	 * Clears the image from memory
	 */
	public void clearImage() {
		this.i.flush();
	}
}