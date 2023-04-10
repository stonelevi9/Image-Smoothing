
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class imageSmooth implements ActionListener {
	private JButton button, button2, button3;
	private BufferedImage original, current;
	private JPanel cPanel;
	// This is a constructor for my Swing GUI.
	public imageSmooth() {
		original = readImage(512, 512, original);
		current = readImage(512, 512, current);
		JFrame frame = new JFrame();
		button = new JButton("Median Smooth");
		button.addActionListener(this);
		button2 = new JButton("Mean Smooth");
		button2.addActionListener(this);
		button3 = new JButton("Reset");
		button3.addActionListener(this);
		JLabel label = new JLabel("Original");
		JLabel label2 = new JLabel("Current");
		JPanel nPanel = new JPanel();
		cPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics j) {
				super.paintComponent(j);
				j.drawImage(original, 300, 200, this);
				j.drawImage(current, 1100, 200, this);
			}
		};
		GridLayout layout = new GridLayout(1, 2);
		layout.setHgap(20);
		label.setHorizontalAlignment(JLabel.CENTER);
		label2.setHorizontalAlignment(JLabel.CENTER);
		nPanel.setLayout(layout);
		
		JPanel sPanel = new JPanel();
		nPanel.add(label);
		nPanel.add(label2);
		sPanel.add(button);
		sPanel.add(button2);
		sPanel.add(button3);
		frame.add(nPanel, BorderLayout.NORTH);
		frame.add(cPanel, BorderLayout.CENTER);
		frame.add(sPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Image Smoother");
		frame.pack();
		frame.setVisible(true);
	}
	// This is my main method that is just responsible for starting my GUI
	public static void main (String args[]) throws IOException {
		new imageSmooth();
	}
	// This method is my readImage method and it's function is to read in (using ImageIO)
	// a new bufferedImage and return that buffered image back.
	public static BufferedImage readImage(int width, int height, BufferedImage image) {
		try {
			File file = new File ("C:\\GoldHill_15per.jpg");
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image = ImageIO.read(file);
		}
		catch(IOException e) {
			System.out.println("Error occured: " + e);
		}
		return image;
	}
	// This method is my readPixels method and it's function is to go through the entirety
	// of a bufferedImage and create a 2D array storing all the individual pixel's color values 
	// in the correct index of the array. Lastly, it returns this array.
	public static int[][] readPixels(int width, int height, BufferedImage image){
		int[][] pixels = new int[width][height];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				pixels[row][col] = image.getRGB(col, row);
				
			}
		}
		return pixels;
	}
	// This method is my average Method and it is responsible for taking in a 2D array of a bufferedImage's
	// color values as integers and iterating through that entire array checking if each pixel is an edge or not
	// by calling edgeCheck and then depending on if it is an edge or not calling the correct method to apply our average method
	// Lastly, it returns the output from the average method.
	public static int[][] average(int[][] pixels) {
		int[][] outputs = new int[pixels.length][pixels[0].length];
		for (int i =0; i<pixels.length; i++) {
			for (int j=0; j<pixels[0].length; j++) {
				if(edgeCheck(pixels, i, j) == true) {
					outputs[i][j] = partialMask(pixels, i, j);
				}
				else {
					outputs[i][j]= mask (pixels, i, j);
				}
			}
		}
		return outputs;
	}
	// This method is my median Method and it is responsible for taking in a 2D array of a bufferedImage's
		// color values as integers and iterating through that entire array checking if each pixel is an edge or not
		// by calling edgeCheck and then depending on if it is an edge or not calling the correct method to apply our median method
		// Lastly, it returns the output from the median method.
	public static int[][] median(int[][] pixels){
		int[][] outputs = new int[pixels.length][pixels[0].length];
		for (int i =0; i<pixels.length; i++) {
			for (int j=0; j<pixels[0].length; j++) {
				if(edgeCheck(pixels, i, j) == true) {
					outputs[i][j] = partialmMask(pixels, i, j);
				}
				else {
					outputs[i][j]= mMask (pixels, i, j);
				}
			}
		}
		return outputs;
	}
	
	
	// This method is my edgeCheck method, it is responsible for determining if a pixel is on the edge
	// of the photo or not and simply returns a boolean depending on if it is or not
	// True = it is an edge.
	public static boolean edgeCheck(int[][] pixels, int row, int col) {
		if(row == 0 || row == 511 || col == 0 || col ==511) {
			return true;
		}
		else {
			return false;
		}
	}
	// This is my mask method, it is essentially the average method for non-edge cases.
	// It works by creating a 2D array which is 3x3 and taking all of our current pixel's
	// neighbors and assigning them to the 2D array. Next we iterate through the each pixel and take 
	// the RGB values for each color and add them to our counters until all of the pixel's
	// RGB values have been added to their corresponding counterparts.
	// Lastly we create a new color that is made by dividing the sum of all RGB values for all pixels
	// by 9 (to get the average) and return this new color.
	public static int mask(int[][] pixels, int row, int col) {
		int[][] mask = new int[3][3];
		int bluecount = 0;
		int greencount = 0;
		int redcount = 0;
		mask[0][0] = pixels[row+1][col-1];
		mask[0][1] = pixels[row+1][col];
		mask[0][2] = pixels[row+1][col+1];
		mask[1][0] = pixels[row][col-1];
		mask[1][1] = pixels[row][col];
		mask[1][2] = pixels[row][col+1];
		mask[2][0] = pixels[row-1][col-1];
		mask[2][1] = pixels[row-1][col];
		mask[2][2] = pixels[row-1][col+1];
		for(int i = 0; i<mask.length; i ++) {
			for (int j =0; j< mask[0].length; j++) {
				int blue = mask[i][j] & 0xff;
				int green = (mask[i][j] & 0xff00) >> 8;
				int red = (mask[i][j] & 0xff0000) >> 16;
				bluecount +=blue;
				greencount += green;
				redcount+= red;
			}
		}
		Color color = new Color(redcount/9, greencount/9, bluecount/9);
		return color.getRGB();
	}
	// This is my mMask method, it is essentially the median method for non-edge cases.
		// It works by creating a 2D array which is 3x3 and taking all of our current pixel's
		// neighbors and assigning them to the 2D array. Next we iterate through the each pixel and take 
		// the RGB values for each color and store them in a array corresponding to which RGB value they are
		// Next we sort all these arrays in order to make it easier to find the median
		// Lastly we create a new color that is made by taking the 5th index of each RGB array (or the middle/ median)
		// and return this new color.
	public static int mMask(int[][] pixels, int row, int col) {
		int[][] mask = new int[3][3];
		int[] blue = new int[9];
		int[] green = new int[9];
		int[] red = new int[9];
		mask[0][0] = pixels[row+1][col-1];
		mask[0][1] = pixels[row+1][col];
		mask[0][2] = pixels[row+1][col+1];
		mask[1][0] = pixels[row][col-1];
		mask[1][1] = pixels[row][col];
		mask[1][2] = pixels[row][col+1];
		mask[2][0] = pixels[row-1][col-1];
		mask[2][1] = pixels[row-1][col];
		mask[2][2] = pixels[row-1][col+1];
		int count = 0;
		for(int i = 0; i<mask.length; i ++) {
			for (int j =0; j< mask[0].length; j++) {
				 blue[count]= mask[i][j] & 0xff;
				green[count] = (mask[i][j] & 0xff00) >> 8;
				red[count] = (mask[i][j] & 0xff0000) >> 16;
				count++;
			}
		}
		Arrays.sort(blue);
		Arrays.sort(red);
		Arrays.sort(green);
		Color color = new Color(red[4], green[4], blue[4]);
	return color.getRGB();
	}
	
	// This is my newImage class. It is responsible for creating a new image once we have created a new 2D array
	// of color values through one of our image smoothing method
	// Lastly, it returns this new image.
	
	public static BufferedImage newImage(int[][] outputs) {
		BufferedImage output = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
		for (int i =0; i<512; i++) {
			for (int j = 0; j<512; j++) {
				output.setRGB(j, i, outputs[i][j]);
			}
		}
		return output;
	}
	// This is my partialMask method and is essentially, my average method for edge cases.
	// We first create our mask array a 3x3 2D array and then through some conditional statements,
	// We initialize our mask array by copying certain edge pixels in for pixel's that don't exist depending on
	// our position on the photo. Next once, we have initialized our mask, we just search the mask and get the average
	// of each RGB value throughout the mask before creating a new color with these values and returning it.
	public static int partialMask(int[][] pixels, int row, int col) {
		int[][] mask = new int[3][3];
		int bluecount = 0;
		int greencount = 0;
		int redcount = 0;
		if(row == 0 && col !=0 && col != 511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row][col-1];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col+1];
		}
		else if (row == 511 && col!=0 && col!= 511) {
			mask[0][0] = pixels[row][col-1];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col+1];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (col == 0 && row!=0 && row!= 511) {
			mask[0][0] = pixels[row+1][col];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (col == 511 && row!=0 && row!=511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col];
		}
		else if (row ==0 && col == 0) {
			mask[0][0] = pixels[row+1][col];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row][col];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col+1];
		}
		else if (row == 0 && col == 511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row][col-1];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col];
		}
		else if (row ==511 && col ==0) {
			mask[0][0] = pixels[row][col];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (row ==511 && col ==511) {
			mask[0][0] = pixels[row][col-1];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col];
		}
		for(int i = 0; i<mask.length; i ++) {
			for (int j =0; j< mask[0].length; j++) {
				int blue = mask[i][j] & 0xff;
				int green = (mask[i][j] & 0xff00) >> 8;
				int red = (mask[i][j] & 0xff0000) >> 16;
				bluecount +=blue;
				greencount += green;
				redcount+= red;
			}
		}
		Color color = new Color(redcount/9, greencount/9, bluecount/9);
		return color.getRGB();
		
	}
	// This is my partialmMask method and is essentially, my median method for edge cases.
		// We first create our mask array a 3x3 2D array and then through some conditional statements,
		// We initialize our mask array by copying certain edge pixels in for pixel's that don't exist depending on
		// our position on the photo. Next once, we have initialized our mask, we just search the mask and get the median
		// of each RGB value throughout the mask before creating a new color with these values and returning it.
	public static int partialmMask(int[][] pixels, int row, int col) {
		int[][] mask = new int[3][3];
		int[] blue = new int[9];
		int[] green = new int[9];
		int[] red = new int[9];
		if(row == 0 && col !=0 && col != 511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row][col-1];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col+1];
		}
		else if (row == 511 && col!=0 && col!= 511) {
			mask[0][0] = pixels[row][col-1];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col+1];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (col == 0 && row!=0 && row!= 511) {
			mask[0][0] = pixels[row+1][col];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (col == 511 && row!=0 && row!=511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col];
		}
		else if (row ==0 && col == 0) {
			mask[0][0] = pixels[row+1][col];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row][col];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col+1];
		}
		else if (row == 0 && col == 511) {
			mask[0][0] = pixels[row+1][col-1];
			mask[0][1] = pixels[row+1][col];
			mask[0][2] = pixels[row+1][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row][col-1];
			mask[2][1] = pixels[row][col];
			mask[2][2] = pixels[row][col];
		}
		else if (row ==511 && col ==0) {
			mask[0][0] = pixels[row][col];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col+1];
			mask[1][0] = pixels[row][col];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col+1];
			mask[2][0] = pixels[row-1][col];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col+1];
		}
		else if (row ==511 && col ==511) {
			mask[0][0] = pixels[row][col-1];
			mask[0][1] = pixels[row][col];
			mask[0][2] = pixels[row][col];
			mask[1][0] = pixels[row][col-1];
			mask[1][1] = pixels[row][col];
			mask[1][2] = pixels[row][col];
			mask[2][0] = pixels[row-1][col-1];
			mask[2][1] = pixels[row-1][col];
			mask[2][2] = pixels[row-1][col];
		}
		int count = 0;
		for(int i = 0; i<mask.length; i ++) {
			for (int j =0; j< mask[0].length; j++) {
				 blue[count]= mask[i][j] & 0xff;
				green[count] = (mask[i][j] & 0xff00) >> 8;
				red[count] = (mask[i][j] & 0xff0000) >> 16;
				count++;
			}
		}
		Arrays.sort(blue);
		Arrays.sort(red);
		Arrays.sort(green);
		Color color = new Color(red[4], green[4], blue[4]);
		return color.getRGB();
	}
	// This is my actionPerformed method and it is responsible for calling the correct methods and updating my GUI
	// whenever a button is pressed.
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == button) {
			int[][] pixels = readPixels(512, 512, current);
			int[][] output = median(pixels);
			current = newImage(output);
			cPanel.repaint();
		}
		else if (arg0.getSource() == button2) {
			int[][] pixels = readPixels(512, 512, current);
			int[][] output = average(pixels);
			current = newImage(output);
			cPanel.repaint();
		}
		else if (arg0.getSource() == button3) {
			current = original;
			cPanel.repaint();
		}
	}

}
