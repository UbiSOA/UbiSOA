package firstResource;

public class ItemIngredient {


    /** Name of the item. */
    private String name;
    private String portion;


    public ItemIngredient(String name) {
        super();
        setName(name);
    }

    public ItemIngredient(String name, String portion)
    {
    	super();
        setName(name);
        setPortion(portion);
    }
    
    

    public String getPortion() {
        return portion;
    }

    public String getName() {
        return name;
    }
    
    public void setPortion(String portion) {
        this.portion = portion;
    }

    public void setName(String name) {
        this.name = name;
    }

}
