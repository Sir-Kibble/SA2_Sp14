package MVCOriginal;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MVC {
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				create();
				}
		});
	}//end main
	
    private static void create() {
    	RandomNumberModel model = new RandomNumberModel();
    	//Controller controller = new Controller(model);
    	View view = new View();
    	//controller.setView(view);

        JFrame f = new JFrame("Redesigned to use SwingWorker!");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(view);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }//end create
}
