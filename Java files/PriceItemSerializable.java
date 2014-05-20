import java.math.BigDecimal;
import java.net.URL;


public class PriceItemSerializable implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011677686507557006L;
	public String html;
	public String itemName;
	public String look;
	public BigDecimal originalPrice;
	public BigDecimal currentPrice;
	public BigDecimal notificationPrice;
	public URL url;
	public Priority priority;
	
	//sets all the properties needed to be serialized
	public PriceItemSerializable(PriceItem item) {
		html = item.getHTML();
		itemName = item.getItemName();
		originalPrice = item.getOriginalPrice();
		currentPrice = item.getCurrentPrice();
		notificationPrice = item.getNotificationPrice();
		url = item.getURL();
		look = item.getLook();
		priority = item.getPriority();
	}
}
