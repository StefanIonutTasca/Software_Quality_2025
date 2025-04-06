package org.jabberpoint.model;

import java.awt.Rectangle;
import org.jabberpoint.core.Style;
import org.jabberpoint.core.JabberPoint;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import java.io.IOException;


/** <p>De klasse voor een Bitmap item</p>
 * <p>Bitmap items have the responsibility to draw themselves.</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
*/

public class BitmapItem extends SlideItem {
  private BufferedImage bufferedImage;
  private String imageName;
  
  protected static final String FILE = "File ";
  protected static final String NOTFOUND = " not found";

// level is equal to item-level; name is the name of the file with the Image
	public BitmapItem(int level, String name) {
		super(level);
		imageName = name;
		
		// Try loading the image from different locations
		tryLoadImage();
	}
	
	private void tryLoadImage() {
		if (imageName == null) {
			return;
		}
		
		// First try: Direct file path (as before)
		try {
			File file = new File(imageName);
			if (file.exists()) {
				bufferedImage = ImageIO.read(file);
				return; // Success!
			}
		} catch (IOException e) {
			System.err.println(FILE + imageName + " could not be loaded: " + e.getMessage());
		}
		
		// Second try: Check in the package directory
		try {
			File packageFile = new File("src/main/java/org/jabberpoint/" + imageName);
			if (packageFile.exists()) {
				bufferedImage = ImageIO.read(packageFile);
				return; // Success!
			}
		} catch (IOException e) {
			System.err.println("Package path: " + imageName + " could not be loaded: " + e.getMessage());
		}
		
		// Third try: As a resource from classpath
		try {
			URL url = getClass().getResource("/" + imageName);
			if (url != null) {
				bufferedImage = ImageIO.read(url);
				return; // Success!
			}
		} catch (IOException e) {
			System.err.println("Resource: " + imageName + " could not be loaded: " + e.getMessage());
		}
		
		// Final attempt: Check in the parent directory structures
		try {
			File parentFile = new File("../" + imageName);
			if (parentFile.exists()) {
				bufferedImage = ImageIO.read(parentFile);
				return; // Success!
			}
		} catch (IOException e) {
			System.err.println("Parent path: " + imageName + " could not be loaded: " + e.getMessage());
		}
		
		// If we get here, all attempts failed
		System.err.println(FILE + imageName + NOTFOUND + " in any location");
	}

// An empty bitmap-item
	public BitmapItem() {
		this(0, null);
	}

// give the filename of the image
	public String getName() {
		return imageName;
	}
	
	/**
	 * Get the name of the image file
	 * @return the name of the image file
	 */
	public String getImageName() {
		return imageName;
	}

// give the  bounding box of the image
	public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle) {
		return new Rectangle((int) (myStyle.indent * scale), 0,
				(int) (bufferedImage.getWidth(observer) * scale),
				((int) (myStyle.leading * scale)) + 
				(int) (bufferedImage.getHeight(observer) * scale));
	}

// draw the image
	public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver observer) {
		// Skip drawing if the bufferedImage is null (image not found)
		if (bufferedImage == null) {
			// Draw a placeholder or error message instead
			int width = x + (int) (myStyle.indent * scale);
			int height = y + (int) (myStyle.leading * scale);
			g.drawString("Image not found: " + imageName, width, height);
			return;
		}
		
		int width = x + (int) (myStyle.indent * scale);
		int height = y + (int) (myStyle.leading * scale);
		g.drawImage(bufferedImage, width, height,(int) (bufferedImage.getWidth(observer)*scale),
                (int) (bufferedImage.getHeight(observer)*scale), observer);
	}

	public String toString() {
		return "BitmapItem[" + getLevel() + "," + imageName + "]";
	}
}



