package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trh_shops")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Shop {
    @Id
    @SequenceGenerator(name = "trh_shop_seq", sequenceName = "trh_shop_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trh_shop_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    private String url;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = true, unique = false)
    private LocalDateTime lastScan = LocalDateTime.now();

    public Shop(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
