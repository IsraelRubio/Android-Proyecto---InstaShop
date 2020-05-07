package Models;

public class Product {
    private String name;
    private String price;
    private String image;
    private String location;
    private String seller;
    private String category;
    private String mobile;

    public Product() {
        this.name = "Default";
        this.price = "Default";
        this.image = "Default";
        this.location = "Default";
        this.seller = "Default";
        this.category = "Default";
        this.mobile = "Default";
    }

    public Product(String name, String price, String image, String location, String seller, String category, String mobile) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.location = location;
        this.seller = seller;
        this.category = category;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
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
