package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.core.web.component.annotations.LiveComponent;
import fr.kainovaii.obsidian.core.web.component.annotations.State;

import java.util.*;
import java.util.stream.Collectors;

@LiveComponent
public class SearchFilter extends fr.kainovaii.obsidian.core.web.component.core.LiveComponent
{
    @State
    private String search = "";
    
    @State
    private String category = "all";

    private final Map<String, List<String>> itemsByCategory = new LinkedHashMap<>() {{
        put("Frontend", List.of("React", "Vue", "Angular", "Svelte", "Next.js"));
        put("Backend", List.of("Node.js", "Django", "Spring Boot", "Laravel", "Express"));
        put("Mobile", List.of("React Native", "Flutter", "Swift", "Kotlin", "Ionic"));
        put("Database", List.of("PostgreSQL", "MongoDB", "Redis", "MySQL", "Cassandra"));
        put("DevOps", List.of("Docker", "Kubernetes", "Jenkins", "Terraform", "Ansible"));
    }};

    public void selectCategory(String category) {
        this.category = category;
    }
    
    public void clearAll() {
        this.search = "";
        this.category = "all";
    }
    
    public List<String> getFilteredItems()
    {
        List<String> items;

        if ("all".equals(category)) {
            items = itemsByCategory.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        } else {
            items = itemsByCategory.getOrDefault(category, List.of());
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            items = items.stream()
                .filter(item -> item.toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
        }
        
        return items;
    }
    
    public int getResultCount() {
        return getFilteredItems().size();
    }
    
    public Map<String, List<String>> getItemsByCategory() { return itemsByCategory; }
    public Set<String> getCategories() { return itemsByCategory.keySet(); }
    public String getCategory() { return category; }
    public String getSearch() { return search; }
    
    @Override
    public String template() {
        return "components/search-filter.html";
    }
}
