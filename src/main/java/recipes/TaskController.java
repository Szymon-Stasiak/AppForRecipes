package recipes;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;



@RestController
@Validated
@RequiredArgsConstructor
public class TaskController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RecipeService recipeService;

    @PostMapping("/api/register")
    public ResponseEntity<String> register(@RequestBody @Valid User user) {
        if (userRepository.findByEmail(user.getEmail())
                          .isEmpty()) {
            user.setPassword(encoder.encode(user.getPassword()));
            user.setRole("ROLE_USER");
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                                 .body("User with email = %s has been registered.".formatted(user.getEmail()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("User with email = %s already exists.".formatted(user.getEmail()));
    }


    @PostMapping("/api/recipe/new")
    public RecipeId postRecipe(@Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails details) {
        long newId = recipeService.create(recipe, details);
        return new RecipeId(newId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<String> updateRecipes(@PathVariable Long id, @Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails details) {
        return recipeService.update(recipe, id, details );
    }

    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipes(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @GetMapping("/api/recipe/search/")
    public ResponseEntity<List<Recipe>> getRecipesFromCategory(@RequestParam(required = false) String category, String name) {
        if (category != null && name != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (name != null) {
            return ResponseEntity.ok(recipeService.getRecipesContainingName(name));
        } else if (category != null) {
            return ResponseEntity.ok(recipeService.getRecipesWithCategory(category));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<String> deleteRecipes(@PathVariable Long id, @AuthenticationPrincipal UserDetails details) {
        return recipeService.deleteRecipeById(id, details);
    }

    record RecipeId(long id) {}
}
