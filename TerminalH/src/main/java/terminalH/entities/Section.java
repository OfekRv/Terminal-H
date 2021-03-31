package terminalH.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "trh_sections")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Section {
    @Id
    @SequenceGenerator(name = "trh_section_seq", sequenceName = "trh_section_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trh_section_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "sectionId")
    private Set<Category> categories;

    public Section(String name) {
        this.name = name;
    }
}
