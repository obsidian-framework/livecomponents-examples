package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.core.web.component.annotations.LiveComponent;
import fr.kainovaii.obsidian.core.web.component.annotations.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@LiveComponent
public class TodoList extends fr.kainovaii.obsidian.core.web.component.core.LiveComponent
{
    @State
    private List<Todo> todos = new ArrayList<>();
    
    @State
    private String newTodoText = "";
    
    @State
    private String filter = "all";
    
    @State
    private int nextId = 1;
    
    public void addTodo() {
        if (newTodoText != null && !newTodoText.trim().isEmpty()) {
            todos.add(new Todo(nextId++, newTodoText.trim(), false));
            newTodoText = "";
        }
    }

    public void toggleTodo(int id) {
        todos.stream()
            .filter(t -> t.id == id)
            .findFirst()
            .ifPresent(t -> t.completed = !t.completed);
    }
    
    public void deleteTodo(int id) {
        todos.removeIf(t -> t.id == id);
    }
    
    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    public void clearCompleted() {
        todos.removeIf(t -> t.completed);
    }

    public List<Todo> getFilteredTodos() {
        switch (filter) {
            case "active":
                return todos.stream().filter(t -> !t.completed).collect(Collectors.toList());
            case "completed":
                return todos.stream().filter(t -> t.completed).collect(Collectors.toList());
            default:
                return todos;
        }
    }
    
    public int getActiveCount() {
        return (int) todos.stream().filter(t -> !t.completed).count();
    }
    
    public int getCompletedCount() {
        return (int) todos.stream().filter(t -> t.completed).count();
    }
    
    public int getTotalCount() {
        return todos.size();
    }
    
    public String getNewTodoText() { return newTodoText; }
    public String getFilter() { return filter; }
    
    @Override
    public String template() {
        return "components/todo-list.html";
    }
    
    public static class Todo {
        public int id;
        public String text;
        public boolean completed;
        
        public Todo() {}
        
        public Todo(int id, String text, boolean completed) {
            this.id = id;
            this.text = text;
            this.completed = completed;
        }
        
        public int getId() { return id; }
        public String getText() { return text; }
        public boolean isCompleted() { return completed; }
    }
}
