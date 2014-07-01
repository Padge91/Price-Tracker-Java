import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import javax.swing.*;

@SuppressWarnings("serial")
public class AddingFrame extends JFrame implements ActionListener{

	private JEditorPane editor;
	
	private String html = "";
	private String stringPrice;
	private String name;
	private String whatToLookFor;
	
	private JButton firstNextButton;
	private JButton nextButton;
	private JButton finalNextButton;
	private JTextField textField1;
	private JTextField priceField;
	private JTextField descriptionField;
	private JPanel masterPanel = null;
	private JPanel panel2;
	private JPanel subPanel;
	private JPanel panel1;
	private JPanel subPanel2;
	private JPanel panel3;
	private JLabel promptLabel;
	private JLabel instructionLabel;
	private JLabel instructionLabel2;

	private URL url;
	private PriceItem priceItem;
	private CardLayout card = new CardLayout();
	private AddItem item;
	private BoxLayout vBox1;
	private BoxLayout box1;
	private BoxLayout box2;
	private BoxLayout b1;
	private BoxLayout b2;
	
	private BufferedReader br;
	
	public AddingFrame(final AddItem a) {
		item = a;
		masterPanel = new JPanel();
		masterPanel.setLayout(card);

		
		//panel 1 to ask for html
		panel1 = new JPanel();
		promptLabel = new JLabel("Enter HTML:");
		textField1 = new JTextField(20);
		firstNextButton = new JButton("Next");
		firstNextButton.addActionListener(this);
		vBox1 = new BoxLayout(panel1, BoxLayout.X_AXIS);
		
		//add all components to panel1
		panel1.setLayout(vBox1);
		panel1.add(promptLabel);
		panel1.add(textField1);
		panel1.add(firstNextButton);
		
		//add panel 1 as card 1
		masterPanel.add(panel1, "card 1");
		
		//components for panel 2
		panel2 = new JPanel();
		subPanel = new JPanel();
		instructionLabel = new JLabel("Highlight the price in the above window, or enter it in the field below.");
		priceField = new JTextField(10);
		nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		
		//add all components for panel 2
		box1 = new BoxLayout(panel2, BoxLayout.Y_AXIS);
		box2 = new BoxLayout(subPanel, BoxLayout.X_AXIS);
		subPanel.setLayout(box2);
		subPanel.add(priceField);
		subPanel.add(nextButton);
		panel2.setLayout(box1); 
		panel2.add(instructionLabel);
		panel2.add(subPanel);
			
		//add panel 2 as card 2
		masterPanel.add(panel2, "card 2");
		
		//components for panel 3
		panel3 = new JPanel();
		subPanel2 = new JPanel();
		instructionLabel2 = new JLabel("Highlight the description in the above window, or enter it in the field below.");
		descriptionField = new JTextField(30);
		finalNextButton = new JButton("Finish");
		finalNextButton.addActionListener(this);
		
		//add all components for panel 3
		b1 = new BoxLayout(panel3, BoxLayout.Y_AXIS);
		b2 = new BoxLayout(subPanel2, BoxLayout.X_AXIS);		
		subPanel2.setLayout(b2);
		panel3.setLayout(b1);
		subPanel2.add(descriptionField);
		subPanel2.add(finalNextButton);
		panel3.add(instructionLabel2);
		panel3.add(subPanel2);
		
		//add panel 3 as card 3
		masterPanel.add(panel3, "card 3");
		
		//add the master panel for the frame
		add(masterPanel);
		
		//set default operations for frame
		setVisible(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(500, 60);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	//make new item with fields from frames
	public void makeItem() {
		//could put handleURLStuff in here
		priceItem = new PriceItem(Priority.LOW, item.getFrame(), html, name, new BigDecimal(Float.parseFloat(stringPrice)), new BigDecimal(0.00), url, whatToLookFor, item);
	}
	
	//method to call and show cards
	private void showCard(String s) {
			card.show(masterPanel, s);
	}

	//action listener implementations
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(firstNextButton)){
			
			//make sure text field is not empty, and make sure URL is valid
			if (textField1.getText().length() > 0){
				
				try {
					html = textField1.getText();
					url = new URL(html);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, "Not valid URL. Please confirm address.", "URL Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			
				showCard("card 2");
				setSize(500, 100);
			}
			else {
				JOptionPane.showMessageDialog(this, "Address field must not be empty.", "URL Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource().equals(nextButton)) {
			boolean found = false;
			
			//check if empty and re-format it for compatibility
			if (priceField.getText().length() > 0) {
				String s = null, s2 = null;
				
				s = priceField.getText();
				s2 = s.replace(",", "");
				stringPrice = s2.replace("$", "");
								
				for (int i = 0; i < stringPrice.length(); i++) {
					if (Character.isLetter(stringPrice.charAt(i))){
						JOptionPane.showMessageDialog(this, "Price must not have letters.", "Price Format Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				found = handleUrlStuff();
				
				if (found) {
					showCard("card 3");
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "Price field must not be empty.", "Price Format Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource().equals(finalNextButton)) {
			
			//make sure not empty and make new object. Make sure it isn't null, then send it to parent frame
			if (descriptionField.getText().length() > 0) {
				
				name = descriptionField.getText();
				
				while (name.length() < 25) {
					name = name + " ";
				}
				
				if (name.length() > 25) {
					name = name.substring(0, 20) + "     ";
				}
				
				makeItem();
				
				if (priceItem == null) {
					JOptionPane.showMessageDialog(this, "Error when making new item.", "Item Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				item.giveItem(priceItem);
				dispose();
			}
			else {
				JOptionPane.showMessageDialog(this, "Description field must not be empty.", "Description Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean handleUrlStuff() {
		String line = null;
		int i = 0, firstCounter = 0;
		boolean found = false;
		
		try {
			url = new URL(html);
			editor = new JEditorPane();
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
				
				if ((line.contains("> $" + stringPrice)) || (line.contains(">$" + stringPrice))){
					found = true;
					i = line.indexOf('$');
					
					while ((line.charAt(firstCounter) == ' ') || (line.charAt(firstCounter) == '\t')){
						firstCounter++;
					}
					
					whatToLookFor = line.substring(firstCounter, i);				
					break;
				}	
			}
			br.close();
			
			if (!found) {
				JOptionPane.showMessageDialog(this, "Price not found in HTML code. Ensure price and URL are entered correctly.", "Price Not Found", JOptionPane.ERROR_MESSAGE);
				return found;
			}
			
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error Opening URL.", "URL Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return found;
	}
}
