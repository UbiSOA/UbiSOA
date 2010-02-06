package firstResource;

public class ItemGuide {


    /** Name of the item. */
    private String image;
    private String location;


    public ItemGuide(String image) {
        super();
        setImage(image);
    }

    public ItemGuide(String image, String location)
    {
    	super();
        setImage(image);
        setLocation(location);
    }   

    public String getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
