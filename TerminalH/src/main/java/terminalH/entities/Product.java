package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "trh_products")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @SequenceGenerator(name = "trh_product_seq", sequenceName = "trh_product_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trh_product_seq")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "shopId")
    private Shop shop;
    @Column(nullable = false, unique = true)
    private String url;
    @Column(nullable = true, unique = false)
    private String pictureUrl;
    @Column(nullable = false, unique = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "categoryId")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "brandId")
    private Brand brand;
    @Column(columnDefinition = "TEXT", nullable = true, unique = false)
    private String description;
    @Column(nullable = false, unique = false)
    private float price;

    public Product(Shop shop, String url, String pictureUrl, String name, Category category, Brand brand, String description, float price) {
        this.shop = shop;
        this.url = url;
        this.pictureUrl = pictureUrl;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.description = description;
        this.price = price;
    }
}