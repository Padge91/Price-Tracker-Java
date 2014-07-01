import java.awt.Component;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

@SuppressWarnings("serial")
public class mainFrame extends JFrame{

	private Timer timer = new Timer();
	private Timer timer2 = new Timer();
	private Timer timer3 = new Timer();
	
	private JPanel itemsPanel = new JPanel();
	private JScrollPane scrollPane;
	AddItem added;
	
	private BoxLayout boxLayout = new BoxLayout(itemsPanel, BoxLayout.Y_AXIS);
	
	public mainFrame() {
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkForUpdates(Priority.HIGH);
			}
		}, 1*60*1000, 1*60*1000);
		
		timer2.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkForUpdates(Priority.MEDIUM);
			}
		}, 2*24*60*60*1000, 2*24*60*60*1000);
		
		timer3.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkForUpdates(Priority.LOW);
			}
		}, 7*24*60*60*1000, 7*24*60*60*1000);
		
		itemsPanel.setLayout(boxLayout);
		
		added = new AddItem(this);
		itemsPanel.add(added);

		loadItems();
		scrollPane = new JScrollPane(itemsPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				saveItems();
			}
		});
		
		
		setSize(450, 500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		
		checkForUpdates(Priority.HIGH);
		checkForUpdates(Priority.MEDIUM);
		checkForUpdates(Priority.LOW);
	}
	
	//method to add new item
	public void addItem(PriceItem item) {
		//removes old add component and then puts it back
		//this makes the add panel always be last in the list
		itemsPanel.remove(itemsPanel.getComponentCount() - 1);
		itemsPanel.add(item);
		itemsPanel.add(new AddItem(this));
		itemsPanel.repaint();
		revalidate();
	}

	//load items from a saved file - persistent items
	@SuppressWarnings("unchecked")
	private void loadItems() {
		ArrayList<Object> list2 = null;
		PriceItemSerializable  itemS= null;
		PriceItem pItem = null;
		
		try {
			
			File f = new File("Items.Data");
			if (!f.exists()) {
				f.createNewFile();
			}
			
			FileInputStream fIn = new FileInputStream("Items.data");
			ObjectInputStream oIn = new ObjectInputStream(fIn);
			
			list2 = (ArrayList<Object>) oIn.readObject();
			
			oIn.close();
			fIn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		if (list2 != null) {
			for (int i = 0; i < list2.size(); i++) {
				//need to convert the serializable items to PriceItem items
				itemS = (PriceItemSerializable)list2.get(i);
				pItem = new PriceItem(itemS.priority, this, itemS.html, itemS.itemName, itemS.originalPrice, itemS.notificationPrice, itemS.url, itemS.look, added);
				addItem(pItem);
				//addItem
			}
		}
	}
	
	//save items to a saved file - persistent items
	private void saveItems() {
		Component[] list = itemsPanel.getComponents();
		ArrayList<Object> list2 = new ArrayList<Object>();
		PriceItemSerializable itemS = null;
		
		try {
			
			File f = new File("Items.Data");
			if (!f.exists()) {
				f.createNewFile();
			}
			
			FileOutputStream fOut = new FileOutputStream("Items.data");
			ObjectOutputStream oOut = new ObjectOutputStream(fOut);
			
			for (int i = 0; i < itemsPanel.getComponentCount(); i++) {
				if ((list[i].getClass().getName().toString().equals("PriceItem"))){
					//converts PriceItems to serializable items
					itemS = new PriceItemSerializable((PriceItem)list[i]);
					list2.add(itemS);
				}
			}
			
			oOut.writeObject((Object) list2);
			
			oOut.close();
			fOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//remove an item from the panel
	public void callRemove(PriceItem p) {
		itemsPanel.remove(p);
		itemsPanel.repaint();
		revalidate();
	}
	
	//update all items in the list
	private void checkForUpdates(Priority p) {
		Component[] list = itemsPanel.getComponents();
		
		for (int i = 0; i < itemsPanel.getComponentCount(); i++) {
			if ((list[i].getClass().getName().toString().equals("PriceItem") && (((PriceItem) list[i]).getPriority() == p))){
				((PriceItem) list[i]).updatePrice();
			}
		}
		
		itemsPanel.repaint();
		revalidate();
	}

}
