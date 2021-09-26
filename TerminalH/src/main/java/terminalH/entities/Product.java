package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import terminalH.entities.enums.Gender;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String url;
    @Column(columnDefinition = "TEXT", nullable = true, unique = false)
    private String pictureUrl;
    @Column(nullable = false, unique = false)
    private String name;
    @Column(nullable = true, unique = false)
    @Nullable
    private Gender gender;
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
    @Column(nullable = true, unique = false)
    private LocalDateTime lastScan;

    public Product(Shop shop,
                   String url,
                   String pictureUrl,
                   String name,
                   Category category,
                   Brand brand,
                   Gender gender,
                   String description,
                   float price,
                   LocalDateTime lastScan) {
        this.shop = shop;
        this.url = url;
        this.pictureUrl = pictureUrl;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.gender = gender;
        this.description = description;
        this.price = price;
        this.lastScan = lastScan;
    }
}