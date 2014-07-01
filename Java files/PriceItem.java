import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.text.DecimalFormat;
import javax.swing.*;

@SuppressWarnings("serial")
public class PriceItem extends JPanel implements ActionListener{

	private String html;
	private String itemName;
	private String priceLabelString;
	private String whatToLookFor;	
	private int timesMissed = 0;
	private boolean ableToRun = true;
	private boolean popup;
	
	private URL url;
	private BigDecimal originalPrice;
	private BigDecimal currentPrice;
	private BigDecimal notificationPrice;
	
	private JComboBox<String> comboBox;
	private JTextField textField;
	private JButton goButton;
	private JButton removeButton;
	private JPanel panel1;
	private JPanel panel2;
	private JLabel priceLabel;
	private JLabel title;
	private JLabel notifyLabel;
	private JLabel priorityLabel;

	private Priority priority;
	private DecimalFormat format = new DecimalFormat();
	private mainFrame parentPanel;
	private BoxLayout layout1;
	private Dimension d;
	
	private BoxLayout layout2;
	private BoxLayout panelLayout;
	
	private JEditorPane editor;
		
	public PriceItem(Priority prior, mainFrame p, String html, String item, BigDecimal highlightedPrice, BigDecimal wantedPrice, URL url, String look, AddItem added) {
		this.html = html;
		itemName = item;
		originalPrice = highlightedPrice;
		currentPrice = highlightedPrice;
		notificationPrice = wantedPrice;
		this.url = url;
		parentPanel = p;
		whatToLookFor = look;
		priority = prior;
		popup = added.getPopup();
		
		//set format for decimals
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);

		//create all the components for the top panel
		panel1 = new JPanel();
		title = new JLabel(itemName);
		priceLabelString = "$" + format.format(currentPrice) + " (" + "$" + format.format(originalPrice) + " when added)";
		priceLabel = new JLabel(priceLabelString);
		goButton = new JButton("Go");
		goButton.addActionListener(this);
		
		layout1 = new BoxLayout(panel1, BoxLayout.X_AXIS);
		panel1.setLayout(layout1);
		
		//add components to the top panel
		panel1.add(title);
		panel1.add(priceLabel);
		panel1.add(goButton);
		panel1.setBackground(new Color(171, 217, 233));
		
		//set panel sizes
		Dimension panel1D = new Dimension(450, 30);
		panel1.setPreferredSize(panel1D);
		panel1.setMaximumSize(panel1.getPreferredSize());
		panel1.setMinimumSize(panel1D);
		
		
		//creates all components for the top panel
		panel2 = new JPanel(new SpringLayout());
		priorityLabel = new JLabel("Priority:");
		String[] strings = { "Low", "Medium", "High"};
		comboBox = new JComboBox<String>(strings);	
		comboBox.addActionListener(this);
		notifyLabel = new JLabel("Notify me at: $");
		textField = new JTextField(format.format(notificationPrice).toString(), 5);
		textField.addActionListener(this);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		layout2 = new BoxLayout(panel2, BoxLayout.X_AXIS);
		panel2.setLayout(layout2);
		
		switch (priority){
		case HIGH:
			comboBox.setSelectedIndex(2);
			break;
		case MEDIUM:
			comboBox.setSelectedIndex(1);
			break;
		case LOW:
			comboBox.setSelectedIndex(0);
			break;
		}
		
		//add the components to the bottom panel
		panel2.add(priorityLabel);
		panel2.add(comboBox);
		panel2.add(notifyLabel);
		panel2.add(textField);
		panel2.add(removeButton);
		panel2.setBackground(new Color(171, 217, 233));
		
		//set panel2 size
		panel2.setPreferredSize(panel1D);
		panel2.setMaximumSize(panel2.getPreferredSize());
		panel2.setMinimumSize(panel1D);
		
		
		//add the top and bottom panels to the main panel
		panelLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(panelLayout);
		add(panel1);
		add(panel2);
		
		//set properties for main panel
		setBorder(BorderFactory.createLineBorder(new Color(150, 200, 230)));
		d = new Dimension(450, 60);
		setPreferredSize(d);
		setMaximumSize(this.getPreferredSize());
		
		if ((currentPrice.floatValue() < notificationPrice.floatValue())) {
			changeColor(new Color(166, 217, 106));
		}
		else {
			changeColor(new Color(171, 217, 233));
		}
		
	}
	
	//sets priority based on the combo box
	public void setPriority() {
		String s = comboBox.getSelectedItem().toString();
		
		switch (s) {
		case "Low":
			priority = Priority.LOW;
			break;
		case "Medium":
			priority = Priority.MEDIUM;
			break;
		case "High":
			priority = Priority.HIGH;
			break;
		}
		
		
	}
	
	public void setPriority(Priority p) {
		priority = p;
	}
	
	//change the notification price
	public void setNotificationPrice() {
		String newPrice;
		
		if (textField.getText().length() > 0) {
			newPrice = textField.getText();
			newPrice = newPrice.replaceAll(",", "");
			
			if (newPrice.charAt(0) == '$') {
				newPrice = newPrice.replaceAll("$", "");
			}
		
			notificationPrice = new BigDecimal(Float.parseFloat(newPrice));

			if ((currentPrice.floatValue() < notificationPrice.floatValue())) {
				changeColor(new Color(166, 217, 106));
			}
			else {
				updatePrice();
			}
			
		}
	}
	
	public BigDecimal getNotificationPrice() {
		return notificationPrice;
	}
	
	//update the price
 	public void updatePrice() {
		String line = null, s = null, foundString = null;
		BufferedReader br = null;
		int i = 0, i2 = 0;
		boolean found = false;
		
		try {

			url = new URL(html);
			editor = new JEditorPane(url);
			editor.setVisible(false);
			editor.setEditable(false);
			
			//page seems to take a few seconds to update -- do this to wait for it to be done -- need a fix
			editor.setPage(html);
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(editor.getText().getBytes())));
			Thread.sleep(6000);
			
			editor.setPage(html);
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(editor.getText().getBytes())));
			
			while (br.ready()) {
				line = br.readLine();
				
				if (line.contains(whatToLookFor)) {
					found = true;
					timesMissed = 0;
					i = line.indexOf('$');
					i2 = i + 1;
					
					//get the last index of the price string
					while ((Character.isDigit(line.charAt(i2))) || (line.charAt(i2) == '.') || (line.charAt(i2) == ',')){
						i2++;
					}
					
					//get the substring of numbers and remove all comma's, then update the text field
					s = line.substring(i+1, i2);
					foundString = s.replace(",", "");
					
					for (int i3 = 0; i < foundString.length(); i3++) {
						if (Character.isLetter(foundString.charAt(i3))){
							JOptionPane.showMessageDialog(this, "HTML Error Formatting. This is a problem server-side, and can't be fixed by you or me :(.", "HTML Format Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
					currentPrice = new BigDecimal(Float.parseFloat(foundString));
					priceLabelString = "$" + format.format(currentPrice) + " (" + "$" + format.format(originalPrice) + " when added)";
					priceLabel.setText(priceLabelString);
					
					//close reader and exit loop
					br.close();
					break;
				}	
				
			}
			
			if (!found) {
				timesMissed++;
				if (ableToRun) {
					ableToRun = false;
					updatePrice();
				}
			}
			
			ableToRun = true;
			
			//handle if consistent miss -- HTML may have changed
			if (timesMissed > 2) {
				switch (priority) {
				case HIGH:
					if (timesMissed > 14) {
					changeColor(new Color(215, 48, 39));
					}
					break;
				case MEDIUM:
					if (timesMissed > 7) {
						changeColor(new Color(215, 48, 39));
					}
					break;
				case LOW:
					if (timesMissed > 2) {
						changeColor(new Color(215, 48, 39));
					}
					break;
				}
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parentPanel, "Error Opening URL.", "URL Error", JOptionPane.ERROR_MESSAGE);
		}
		
		if (timesMissed <= 2) {
			if ((currentPrice.floatValue() < notificationPrice.floatValue())) {
				changeColor(new Color(166, 217, 106));
				
				if (popup) {
					int reply = JOptionPane.showConfirmDialog(parentPanel, itemName + " notification price has been reached. Open webpage?");
					if (reply == JOptionPane.YES_OPTION) {
						goToWebsite();
						JOptionPane.getRootFrame().dispose();
					}
					else {
						JOptionPane.getRootFrame().dispose();
					}
				}
			}
			else {
				changeColor(new Color(171, 217, 233));
			}
		}
	}
	
	//change background color of panel
	private void changeColor(Color c) {
		
		//change background colors
		panel2.setBackground(c);
		panel1.setBackground(c);
		
		repaint();
		validate();
		setVisible(true);
		
	}
	
	//open up website in new browser
	private void goToWebsite() {
		
		try {
		if (Desktop.isDesktopSupported()){
			Desktop.getDesktop().browse(new URI(html));
		}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(parentPanel, "Error opening Web Broswer.", "Web Broswer Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	//remove panel from frame
	private void removeMe() {
		parentPanel.callRemove(this);
	}
	
	//return priority
	public Priority getPriority() {
		return priority;
	}

	
	//action listener handlers
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(goButton)){
			goToWebsite();
		}
		else if (e.getSource().equals(comboBox)) {
			setPriority();
		}
		else if (e.getSource().equals(textField)) {
			setNotificationPrice();
		}
		else if (e.getSource().equals(removeButton)) {
			removeMe();
		}
		
	}
	
	public String getHTML() {
		return html;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public URL getURL() {
		return url;
	}
	
	public String getLook() {
		return whatToLookFor;
	}
	
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}
	
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
}
