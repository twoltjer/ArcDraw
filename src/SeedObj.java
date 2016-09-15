
public class SeedObj {
	private String dirSeed;
	private String rotSeed;
	private int seedLength;
	
	public SeedObj() {
		dirSeed = "";
		rotSeed = "";
		seedLength = 0;
	}
	
	public void buildSeed(char nextDirSeed, char nextRotSeed) {
		dirSeed += nextDirSeed;
		rotSeed += nextRotSeed;
		seedLength++;
	}
	
	public int getSeedLength() {
		return seedLength;
	}
	
	public String getDirSeed() {
		return dirSeed;
	}
	
	public String getRotSeed() {
		return rotSeed;
	}
	
	public void mimic(SeedObj seed) {
		this.dirSeed = seed.getDirSeed();
		this.rotSeed = seed.getRotSeed();
		this.seedLength = seed.getSeedLength();
	}
}
