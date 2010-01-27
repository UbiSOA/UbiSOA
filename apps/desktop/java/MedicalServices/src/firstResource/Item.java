package firstResource;

public class Item {
    /** A description of the item. */
    private String description;

    /** Name of the item. */
    private String name;
    private String type;
    private String time;
    private String quantity;

    public Item(String name) {
        super();
        setName(name);
    }

    public Item(String name, String description)
    {
    	super();
        setName(name);
        setDescription(description);
    }
    
    public Item(String name, String description, String quantity, String type, String time) {
        super();
        setName(name);
        setDescription(description);
        setType(type);
        setTime(time);
        setQuantity(quantity);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTime() {
        return time;
    }
    
    public String getQuantity() {
        return quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
