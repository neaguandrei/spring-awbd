package aneagu.proj.models.domain;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "product_lines")
public class ProductLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String textDescription;

    @Column
    private byte[] image;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "productLine",
            orphanRemoval = true
    )
    private Set<Product> product;
}
