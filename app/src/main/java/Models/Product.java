package Models;

public class Product {
    private String nombre;
    private double precio;
    private String imagen;
    private String ubicacion;
    private String vendedor;
    private String categoria;

    public Product() {
        this.nombre = "Default";
        this.precio = 0;
        this.imagen = "Default";
        this.ubicacion = "Default";
        this.vendedor = "Default";
        this.categoria = "Default";
    }

    public Product(String nombre, double precio, String imagen, String ubicacion, String vendedor, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.ubicacion = ubicacion;
        this.vendedor = vendedor;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Product{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", imagen='" + imagen + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", vendedor='" + vendedor + '\'' +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
