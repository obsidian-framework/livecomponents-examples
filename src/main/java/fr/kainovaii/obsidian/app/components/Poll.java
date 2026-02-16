package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.livecomponents.annotations.LiveComponentImpl;
import fr.kainovaii.obsidian.livecomponents.annotations.State;
import fr.kainovaii.obsidian.livecomponents.core.LiveComponent;

import java.util.HashMap;
import java.util.Map;

@LiveComponentImpl
public class Poll extends LiveComponent
{
    @State
    private String question = "What's your favorite programming paradigm?";
    
    @State
    private Map<String, Integer> options = new HashMap<>() {{
        put("Object-Oriented", 0);
        put("Functional", 0);
        put("Procedural", 0);
        put("Declarative", 0);
    }};
    
    @State
    private boolean hasVoted = false;
    
    @State
    private String selectedOption = null;

    public void vote(String option) {
        if (!hasVoted && options.containsKey(option)) {
            options.put(option, options.get(option) + 1);
            selectedOption = option;
            hasVoted = true;
        }
    }
    
    public void reset() {
        hasVoted = false;
        selectedOption = null;
    }
    
    public int getTotalVotes() {
        return options.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<String, Integer> getPercentages() {
        Map<String, Integer> percentages = new HashMap<>();
        int total = getTotalVotes();
        
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            int percentage = (total == 0) ? 0 : (entry.getValue() * 100) / total;
            percentages.put(entry.getKey(), percentage);
        }
        
        return percentages;
    }
    
    public String getWinner() {
        return options.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }
    
    public String getQuestion() { return question; }
    public Map<String, Integer> getOptions() { return options; }
    public boolean isHasVoted() { return hasVoted; }
    public String getSelectedOption() { return selectedOption; }
    
    @Override
    public String template() {
        return "components/poll.html";
    }
}
