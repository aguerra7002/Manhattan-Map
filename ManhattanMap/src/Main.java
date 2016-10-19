
import javax.swing.JFrame;

public class Main extends JFrame {

   private DensityMap dMap;
   private CoordinateParser cp;
   
   public Main(String name) {
      super(name);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(365, 600);  //damn good ratio 

      setResizable(false);
      dMap = new DensityMap(getWidth(), getHeight());
      
      add(dMap);
      setVisible(true);
   }
   
   
   
    public static void main(String[] args) {

        new Main("Manhattan Simulation");

    }

}
