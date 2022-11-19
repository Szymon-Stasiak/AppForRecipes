package recipes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.servlet.function.ServerResponse.status;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public Long create(Recipe recipe, UserDetails details) {
        User currentUser = userRepository.findByEmail(details.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        recipe.setUser(currentUser);
        currentUser.getRecipes()
                .add(recipe);
        userRepository.save(currentUser);
        return userRepository.findByEmail(details.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getRecipes()
                .get(userRepository.findByEmail(details.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                        .getRecipes()
                        .size() - 1)
                .getId();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(RecipeNotFoundException::new);
    }

    @Transactional
    public ResponseEntity<String> update(Recipe recipe, long id, @AuthenticationPrincipal UserDetails details){//toDo
        Recipe currentRecipe = this.getRecipeById(id);
        User currentUser = userRepository.findByEmail(details.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (currentRecipe.getUser().equals(currentUser)) {
            currentRecipe.setName(recipe.getName());
            currentRecipe.setCategory(recipe.getCategory());
            currentRecipe.setDescription(recipe.getDescription());
            currentRecipe.setDate(recipe.getDate());
            currentRecipe.setIngredients(recipe.getIngredients());
            currentRecipe.setDirections(recipe.getDirections());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Transactional
    public ResponseEntity<String> deleteRecipeById(long id, @AuthenticationPrincipal UserDetails details) {
        Recipe currentRecipe = this.getRecipeById(id);
        User currentUser = userRepository.findByEmail(details.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (currentRecipe.getUser().equals(currentUser)) {
            currentUser.deleteFromUserList(currentRecipe);
            recipeRepository.delete(currentRecipe);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    public List<Recipe> getRecipesWithCategory(String category) {
        return recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> getRecipesContainingName(String name) {
        return recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }


}
