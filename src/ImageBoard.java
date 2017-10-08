import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImageBoard extends JPanel {

	Image image;
	
	public ImageBoard(Image i) {
		
		this.image = i;
		
	}
	
	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	        g.drawImage(image, 0, 0, null);
	}
}
