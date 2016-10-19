import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CoordinateParser
{
   
   private static final double MAX_LAT = 40.8730228;
   private static final double MIN_LAT = 40.7013019;
   
   private static final double MIN_LONG = -74.0179293;
   private static final double MAX_LONG = -73.9112258;
   
   private static final int MILES_PER_LONGITUDE = 53;
   private static final int MILES_PER_LATITUDE = 69;
 
   private static final int POSSIBLE_LATS = (int)((MAX_LAT - MIN_LAT) * 10E7);
   private static final int POSSIBLE_LONGS = (int)((MAX_LONG - MIN_LONG) * 10E7);
   
   private static final int INSETPIX = 5;
   private static double K_WIDTH;
   private static double K_HEIGHT; 
   
   double latsPerPixel;
   double longsPerPixel;
   double pigeonsPerLat;
   double pigeonsPerLong;
   
   double sumDistances;
   
   double avgLongs;
   double avgLats;
   
   Point[] data;
   
   static int width; 
   static int height;
   
   DensityMap dMap;
   BufferedImage img;
   Graphics g;
   private double averageResponseTime;
   private double[] AMBULANCE_LATS = new double[11];
   private double[] AMBULANCE_LONGS = new double[11];
   private Point[] AMBULANCE_COORDINATE_LOCATIONS = new Point[11];
   
   private static final int NUM_DATA = 173624; //pigeons
   public CoordinateParser(int width, int height, DensityMap d) {
      img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      g = img.getGraphics();
      //g.fillRect(0, 0, img.getWidth(), img.getHeight());
      dMap = d;
      this.width = width;
      this.height = height;
      latsPerPixel = (POSSIBLE_LATS / height);
      longsPerPixel = (POSSIBLE_LONGS / width);
      
      K_WIDTH = (INSETPIX / width) + 1;
      K_HEIGHT = (INSETPIX / height) + 1;
      
      pigeonsPerLat = ((double)NUM_DATA/(double)POSSIBLE_LATS);
      pigeonsPerLong = ((double)NUM_DATA/(double)POSSIBLE_LONGS);
      
      avgLongs = NUM_DATA / longsPerPixel;
      avgLongs = NUM_DATA / latsPerPixel;
      

      AMBULANCE_LATS[0] = 40.844724; // Washington Hts
      AMBULANCE_LATS[1] = 40.784631; // Yorkville
      AMBULANCE_LATS[2] = 40.817372; // Harlem
      AMBULANCE_LATS[3] = 40.739113; // Kips Bay
      AMBULANCE_LATS[4] = 40.747777; // West Side
      AMBULANCE_LATS[5] = 40.710223; // Lower East Side
      
      AMBULANCE_LATS[6] = 40.761231;
      AMBULANCE_LATS[7] = 40.769462;
      AMBULANCE_LATS[8] = 40.796321;
      AMBULANCE_LATS[9] = 40.751963;
      AMBULANCE_LATS[10] = 40.725789;
      
      AMBULANCE_LONGS[0] = -73.935468;
      AMBULANCE_LONGS[1] = -73.942765;
      AMBULANCE_LONGS[2] = -73.938302;
      AMBULANCE_LONGS[3] = -73.975202;
      AMBULANCE_LONGS[4] = -74.005029;
      AMBULANCE_LONGS[5] = -73.986231;
      
      AMBULANCE_LONGS[6] = -73.960461;
      AMBULANCE_LONGS[7] = -73.982231;
      AMBULANCE_LONGS[8] = -73.961241;
      AMBULANCE_LONGS[9] = -73.9902357;
      AMBULANCE_LONGS[10] = -73.995244;
      
      for (int i = 0; i < AMBULANCE_COORDINATE_LOCATIONS.length; i++) {
    	  AMBULANCE_COORDINATE_LOCATIONS[i] = 
    			  new Point(latToPx(AMBULANCE_LATS[i]), longToPx(AMBULANCE_LONGS[i]));
      }
   }
   
   public void print() {
      int count = 0;
      int totalCount = 0;
      sumDistances = 0;
      String csvFile = "/Users/Janine/Documents/aocmm.csv";
      BufferedReader br = null;
      String line = "";
      String cvsSplitBy = ",";
      ArrayList<Point> l = new ArrayList<Point>();
      try {

          br = new BufferedReader(new FileReader(csvFile));
          int i = 0;
          
          while ((line = br.readLine()) != null) {
        	  
             i++;
              // use comma as separator
              String[] coord = line.split(cvsSplitBy);
              if (!coord[4].contains("L") && !coord[4].equals("")) {
            	  totalCount++;
            	 double lat = Double.parseDouble(coord[4]);
            	 double lon = Double.parseDouble(coord[5]);
            	 sumDistances += findClosestAmbulanceDistances(lat, lon);
                 int x = (int)latToPx(lat);
                 int y = (int)longToPx(lon);
                 int[] o;
                 
                 o = getPixelData(img, y,x);
                 float[] hsbvals = new float[3];
                 Color.RGBtoHSB(o[0],o[1],o[2], hsbvals);
                 if (hsbvals[0] == 0.0) {
                    hsbvals[0] = (float) .7;
                 } else if (hsbvals[0] > 0.0) {
                    hsbvals[0] -= (float) 0.003;
                 } else {
                    hsbvals[0] = (float) -.00001;
                 }

                 hsbvals[1] = (float) 1;
                 hsbvals[2] = (float) 1;
                 //System.out.println(hsbvals[0] + " " + hsbvals[1] + " " + hsbvals[2] + " " + i);
                 int newRGB = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
                 o[0]   = (newRGB & 0x00ff0000) >> 16;
                 o[1] = (newRGB & 0x0000ff00) >> 8;
                 o[2]  =  newRGB & 0x000000ff;
                 g.setColor(new Color(o[0], o[1], o[2]));
                 g.drawRect(y, x, 1, 1);
                 
              }
              for (double j = .7; j >= 0; j -= .003) {
                 int[] o = new int[3];
                 int newRGB = Color.HSBtoRGB((float)(j), 1f, 1f);
                 o[0]   = (newRGB & 0x00ff0000) >> 16;
                 o[1] = (newRGB & 0x0000ff00) >> 8;
                 o[2]  =  newRGB & 0x000000ff;
              g.setColor(new Color(o[0], o[1], o[2]));
              g.fillRect((int)((.7 - j) * 200), 0, 1, 5);
              
              }
              int index = 0;
              for (Point p: AMBULANCE_COORDINATE_LOCATIONS) {
            	  index++;
                 g.setColor(Color.WHITE);
                 if (index > 6) {
                	 g.setColor(Color.PINK);
                 }
                 g.fillOval(p.y-4, p.x-4, 9, 9);
                 //System.out.println(p.x + " " + p.y);
              }
              for (int j = 0; i < img.getWidth(); i++) {
                 for (int k = 0; i < img.getHeight(); i++) {
                    int[] rgb = getPixelData(img, j,k);
                    rgb[0] = 255 - rgb[0];
                    rgb[1] = 255 - rgb[1];
                    rgb[2] = 255 - rgb[2];
                    int blah = ((rgb[0]&0x0ff)<<16)|((rgb[1]&0x0ff)<<8)|(rgb[2]&0x0ff);
                    img.setRGB(j, k, blah);
                 }
              }
              
              
          }

      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          if (br != null) {
              try {
                  br.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      double averageDistanceToCrash = sumDistances / totalCount;
      
      double mph = 6.788994033785289;
      System.out.println("Minimum Average Ambulance Speed in mph: " + sumDistances);
      System.out.println("Maximum Average Ambulance Speed in mph: " + totalCount);
      
      averageResponseTime = averageDistanceToCrash * 60 / mph; //average response time in minutes
      
   }
   
   private static int longToPx(double longitude) {
      int px = (int) (((width * (longitude - MIN_LONG))/(MAX_LONG - MIN_LONG)));
      //px *= (K_WIDTH + 1) / (2 * K_WIDTH);
      
      return px;
   }
   
   private static int latToPx(double latitude) {
      int px = (int) (height - ((height * (latitude - MIN_LAT))/(MAX_LAT - MIN_LAT)));
      //px *= (K_HEIGHT + 1) / (2 * K_HEIGHT);
      return px;
   }
   
   public BufferedImage getImage() {
      return img;
   }
   
   private static int[] getPixelData(BufferedImage img, int x, int y) {
      int rgb[] = {47,48,49};
      if (img.getWidth() > x && img.getHeight() > y) {
         int argb = img.getRGB(x, y);
         rgb = new int[] {
               (argb >> 16) & 0xff, //red
               (argb >>  8) & 0xff, //green
               (argb      ) & 0xff  //blue
      };
      }
      //System.out.println("rgb: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
      return rgb;
      }
   
   private double findClosestAmbulanceDistances(double lat, double lon) {
	   double smallestDistance = Integer.MAX_VALUE;
	   for (int i = 0; i < AMBULANCE_COORDINATE_LOCATIONS.length; i++) {
		   double xMiles = (AMBULANCE_LATS[i] - lat) * MILES_PER_LATITUDE;
		   double yMiles = (AMBULANCE_LONGS[i] - lon) * MILES_PER_LONGITUDE;
		   double distance = Math.sqrt(Math.pow(xMiles, 2) + Math.pow(yMiles, 2));
		   if (distance < smallestDistance) {
			   smallestDistance = distance;
		   }
	   }
	   return smallestDistance;
   }
   
   public double getResponseTime() {
	   return averageResponseTime;
   }
   
}
