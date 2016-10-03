package MVCOriginal;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements ActionListener {

	private final AtomicBoolean generatorRunning = new AtomicBoolean(false);
	private View view;
	private RandomNumberModel model;
	private Thread thread = null;

	public Controller(RandomNumberModel model) {
		this.model = model;
	}
	
	public void setView(View view) {
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) { 
		try {
			if (view.btnGenerate.getActionCommand().equals(
					e.getActionCommand())) {
				view.listModel.clear();
				startNumGen();
			} else if (view.btnStop.getActionCommand().equals(
					e.getActionCommand())) {
				stopNumGen();
			} else if (view.btnDelete.getActionCommand().equals(
					e.getActionCommand())) {
				deleteElement();
			}
		} 
		catch (ControlException exc) {
			System.out.println(exc);
		}
	}
	
	public void startNumGen() throws ControlException {

		// Don't let a second generation thread start if
		// one is already running.
		// If generator is already running then return.
		// Equivalent to commented code below, BUT, it
		// is not atomic.
		// if( generatorRunning.get())
		//  	return;
		// else
		//  	generatorRunning.set(true);
		if (!generatorRunning.compareAndSet(false, true)) {
		    // already running....
		    return;
		}
		
		thread = new Thread(new Runnable() {
			public void run() {
				try {
					doGeneration();
				}
				catch (ControlException exc) {
					System.out.println("Stopped " + exc);
				} 
				catch (InterruptedException exc) {
					System.out.println("Interrupted " + exc);
				} 
				
				finally {
					view.btnGenerate.setEnabled(true);
					view.btnStop.setEnabled(false);
					generatorRunning.set(false);
				}
			}
		});

		view.btnGenerate.setEnabled(false);
		view.btnStop.setEnabled(true);
		// If thread is only thing left, then kill it
		thread.setDaemon(true);
		thread.start();
	}
		
	private void doGeneration() throws StopGenException, InterruptedException {
		for(int i=0; i<16; i++) {

			final double val = model.getRandomNum();
			
			// check after sleep... 
			// Make sure generator is still running. Otherwise you have the risk 
			// that you stop the generator, and then it still generates a value 
			// half a second later.
	        if( !generatorRunning.get()) {
	            throw new StopGenException();
	        }			

	        // Not necessary because this method is ultimately
	        // Every method which is called by actionPerformed is in EDT. 
	        // You can print the current thread name with 
	        // System.out.println(Thread.currentThread().getName()).
	        // Thus, all the gui requests (e.g. addElement(), etc) are in EDT.
	        // However, this shows what should be done when thread is not in EDT.
			EventQueue.invokeLater(new Runnable() {
	            public void run() {
	            	view.listModel.addElement(val);
	            }
	        });
		}
	}
	
	public void stopNumGen() {
		System.out.println("Stop generation");
		generatorRunning.set(false);
	}
	
	public void deleteElement() {
		System.out.println("Delete number");
		
		// Should use invokeLater() if not in EDT (but it is)
        int[] indices = view.list.getSelectedIndices();

		for (int i = 0; i < indices.length; i++) {
			// Move backwards through the list of selected indices
			// because if you remove from beginning then list indices
			// will change. For example if need to remove 1st and 2nd
			// items, if you remove 1st, then the former second will
			// now be 1st and not removed.
			final int index = indices[indices.length - 1 - i];
			System.out.print("Index: " + index);
			
			// Should use invokeLater() if not in EDT (but it is)
           	double val = view.listModel.get(index);

			System.out.println(", Deleted val: " + val);
			
			// Not necessary, see comment in doGeneration()
			EventQueue.invokeLater(new Runnable() {
	            public void run() {
	    			view.listModel.remove(index);
	            }
	        });
		}
	}
	
	static class ControlException extends Exception {
	}

	static class StopGenException extends ControlException {
	}
}
