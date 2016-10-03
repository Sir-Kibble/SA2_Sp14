package MVCOriginal;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import MVCOriginal.Controller.ControlException;
import MVCOriginal.Controller.StopGenException;

public class View extends JPanel {
	
	private final AtomicBoolean generatorRunning = new AtomicBoolean(false);
	private View view;
	private RandomNumberModel model;
	private Thread thread = null;
	
	JList list;
	DefaultListModel<Double> listModel = new DefaultListModel<Double>();
	JTextArea area;
	JTextField field;
	JButton btnGenerate = new JButton("Start Generation");
	JButton btnStop = new JButton("Stop Generation");
	JButton btnDelete = new JButton("Delete selected");
	Controller controller;


	public View() {
		//this.controller = controller;
		this.setSize(new Dimension(600, 300));

		// Create GUI components
		listModel = new DefaultListModel<Double>();
		list = new JList(listModel);
		area = new JTextArea();
		System.out.println("break");
		btnStop.setEnabled(false);
		
		//adding listeners
		btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start(evt);
            }
        });
		btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop(evt);
            }
        });
		btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete(evt);
            }
        });

		// Assemble GUI components
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btnGenerate);
		buttonPanel.add(btnStop);

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		listPanel.add(new JScrollPane(list));
		listPanel.add(btnDelete, BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.add(listPanel, BorderLayout.WEST);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(new JScrollPane(area));
	}//end constructor
	
	private void stop(java.awt.event.ActionEvent evt) {
		generatorRunning.set(false);
	}//end stop
	
	private void delete(java.awt.event.ActionEvent evt) {
		System.out.println("Delete number");
			new SwingWorker<Double,Void>(){
				@Override
				protected Double doInBackground() throws Exception {
					int[] indices = list.getSelectedIndices();

					for (int i = 0; i < indices.length; i++) {
						// Move backwards through the list of selected indices
						// because if you remove from beginning then list indices
						// will change. For example if need to remove 1st and 2nd
						// items, if you remove 1st, then the former second will
						// now be 1st and not removed.
						final int index = indices[indices.length - 1 - i];
						System.out.print("Index: " + index);
						
						// Should use invokeLater() if not in EDT (but it is)
			           	double val = listModel.get(index);

						System.out.println(", Deleted val: " + val);
					
						listModel.remove(index);
					}
					return null;
					
				}
			}.execute();//end swingworker
	}//end delete
	
	private void start(java.awt.event.ActionEvent evt) { 
		System.out.println("starting...");
		listModel.clear();
		
		//starting the swingWorker
		new SwingWorker<Double,Double>(){
			double val=9;//test value
			@Override
			protected Double doInBackground() throws Exception {
				// TODO Auto-generated method stub
				System.out.println("enthinger worker");
				btnGenerate.setEnabled(false);
				btnStop.setEnabled(true);
				if (!generatorRunning.compareAndSet(false, true)) {
					System.out.println("hit is already running");
				    // already running....
				    return 0.0;
				}
		
				//generating...
				try {
					for(int i=0; i<16; i++) {
						Thread.sleep(500);
						val = Math.random();//got rid of the model
						publish(val);//spitting out a value!
						// check after sleep... 
						// Make sure generator is still running. Otherwise you have the risk 
						// that you stop the generator, and then it still generates a value 
						// half a second later.
				        if( !generatorRunning.get()) {
				        	System.out.println("hit generatorrunning");
				            throw new StopGenException();
				        }//end if			
					}//end for
				}//end try
				catch (ControlException exc) {
					System.out.println("Stopped " + exc);
				}//end catch
				
				finally {
					btnGenerate.setEnabled(true);
					btnStop.setEnabled(false);
					generatorRunning.set(false);
				}//end finally
				System.out.println("done");
				return null;
			}//end doinbackground
			
			protected void done() {
				System.out.println("Finished generation! ");
			}//end done
			
			//publish calls process, so our process method will add the numbers to the list
			@Override
			protected void process(List <Double> a){
				 for (final Double val : a) {
					 listModel.addElement(val);
				 }//end for
			}//end process
					      
			
		}.execute();//end and execute new swingworker
		
	}//end start
}//end class
