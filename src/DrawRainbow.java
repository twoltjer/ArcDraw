import java.awt.Color;

/**
 * Makes the pixels draw colors instead of just plain white. Fun times!
 * 
 * @author Thomas Woltjer
 */
public class DrawRainbow {
	private static boolean binc = true;
	private static boolean rinc = false;
	private static boolean ginc = false;
	private static int bval = 0;
	private static int rval = 512;
	private static int gval = 0;
	private static final int INTERVAL = 4;

	/**
	 * Gives the next color of the rainbow, and sets the next one up. The
	 * interval at which the RGB values are changes is stored in the private
	 * INTERVAL constant.
	 * 
	 * @return the Color object that has a nice rainbow-y color
	 */
	public static Color getNextColor() {
		int rbval;
		if (bval > 255) {
			rbval = 255;
		} else if (bval < 0) {
			rbval = 0;
		} else {
			rbval = bval;
		}
		if (binc) {
			bval += INTERVAL;
			if (bval == 512) {
				binc = false;
			}
		} else {
			bval -= INTERVAL;
			if (bval == -256) {
				binc = true;
			}
		}
		int rrval;
		if (rval > 255) {
			rrval = 255;
		} else if (rval < 0) {
			rrval = 0;
		} else {
			rrval = rval;
		}
		if (rinc) {
			rval += INTERVAL;
			if (rval == 512) {
				rinc = false;
			}
		} else {
			rval -= INTERVAL;
			if (rval == -256) {
				rinc = true;
			}
		}
		int rgval;
		if (gval > 255) {
			rgval = 255;
		} else if (gval < 0) {
			rgval = 0;
		} else {
			rgval = gval;
		}
		if (ginc) {
			gval += INTERVAL;
			if (gval == 512) {
				ginc = false;
			}
		} else {
			gval -= INTERVAL;
			if (gval == -256) {
				ginc = true;
			}
		}
		Color rtn = new Color(rrval, rgval, rbval);
		return rtn;
	}
}
