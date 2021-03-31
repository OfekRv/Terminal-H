package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "trh_categories")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @SequenceGenerator(name = "trh_category_seq", sequenceName = "trh_category_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trh_category_seq")
    private Long id;
    @Column(nullable = false, unique = false)
    private String name;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "categoryId")
    private Set<Product> products;

    public Category(String name) {
        this.name = name;
    }
}
