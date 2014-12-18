public class Tile
{
    int id;
    boolean collision;
    String foreground;
    String background;
    Arraylist<Item> items;
    
    public Tiles(int id, boolean collision, String foreground, String background, Arraylist<Item> items) {
        this.items = items;
        setId(id);
        setCollision(collision);
        setForeground(foreground);
        setBackground(background);
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getForeground() {
        return foreground;
    }
    public String getBackground() {
        return background;
    }
    public void setForeground(String foreground) {
        this.foreground = foreground;
    }
    public void setBackground(String background) {
        this.background = background;
    }
    public void addItem(Item item) {
        items.add(item);
    }
    public void removeItem(Item item) {
        items.remove(item);
    }
    public Arraylist<Item> getItems() {
        return items;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
} 