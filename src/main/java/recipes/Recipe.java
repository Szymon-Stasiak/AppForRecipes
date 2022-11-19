package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Recipe {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @UpdateTimestamp
    private LocalDateTime date;

    @NotBlank
    private String description;

    @NotEmpty
    @Fetch(value = FetchMode.SUBSELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> ingredients;

    @NotEmpty
    @Fetch(value = FetchMode.SUBSELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> directions;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return Objects.equals(getId(), recipe.getId()) && Objects.equals(getName(), recipe.getName()) && Objects.equals(getCategory(), recipe.getCategory()) && Objects.equals(getDate(), recipe.getDate()) && Objects.equals(getDescription(), recipe.getDescription()) && Objects.equals(getIngredients(), recipe.getIngredients()) && Objects.equals(getDirections(), recipe.getDirections()) && Objects.equals(getUser(), recipe.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCategory(), getDate(), getDescription(), getIngredients(), getDirections(), getUser());
    }




}

