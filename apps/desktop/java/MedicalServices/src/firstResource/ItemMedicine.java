package firstResource;

public class ItemMedicine {
    /** A description of the item. */
    private String description;
    private String name;
    private String type;
    private String time;
    private String quantity;

    public ItemMedicine(String name) {
        super();
        setName(name);
    }

    public ItemMedicine(String name, String type, String time, String quantity, String description) {
        super();
        setName(name);
        setDescription(description);
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
