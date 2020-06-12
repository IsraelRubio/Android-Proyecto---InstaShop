package Models;

public class Product {
    private String name;
    private double price;
    private String image;
    private String location;
    private String seller;
    private String category;
    private String email;

    public Product() {
        this.name = "Default";
        this.price = 0;
        this.image = "Default";
        this.location = "Default";
        this.seller = "Default";
        this.category = "Default";
        this.email = "Default";
    }

    public Product(String name, double price, String image, String location, String seller, String category, String email) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.location = location;
        this.seller = seller;
        this.category = category;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", location='" + location + '\'' +
                ", seller='" + seller + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
