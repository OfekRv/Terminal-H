package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "trh_brands")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Brand {
    @Id
    @SequenceGenerator(name = "trh_brand_seq", sequenceName = "trh_brand_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trh_brand_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    public Brand(String name) {
        this.name = name;
    }
}