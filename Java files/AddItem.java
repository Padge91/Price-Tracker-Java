import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class AddItem extends JPanel{
	
	private mainFrame parentFrame;
	private JButton button;
	private FlowLayout grid;
	private Dimension d;
	private JCheckBox checkBox;
	private boolean showPopup = false;
	
	public AddItem(mainFrame frame) {
		//create frame, button, dimensions and layouts
		parentFrame = frame;
		button = new JButton("Add");
		d = new Dimension(450, 40);
		grid = new FlowLayout();
		checkBox = new JCheckBox("Popup");
		
		//set size for frame
		setPreferredSize(d);
		setMaximumSize(this.getPreferredSize());
		setMinimumSize(d);
		
		//action listener for button calls makeCardFrame() method
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeCardFrame();
			}
		});
		
		 checkBox.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 showPopup = checkBox.isEnabled();
			 }
		 });
		
		//set layout and add button to frame
		setLayout(grid);
		add(button, FlowLayout.LEFT);
		
		//set background color and border color
		setBackground(new Color(171, 217, 200));
		setBorder(BorderFactory.createLineBorder(new Color(150, 200, 200)));
	}
	
	//create new frame to add an object
	private void makeCardFrame() {
		new AddingFrame(this);
	}
	
	//returns mainFrame
	public mainFrame getFrame() {
		return parentFrame;
	}
	
	//give the priceItem created to the main frame
	public void giveItem(PriceItem t) {
		PriceItem price = t;
		parentFrame.addItem(price);
	}
	
	public boolean getPopup() {
		return showPopup;
	}

}
