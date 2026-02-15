package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.core.web.component.annotations.LiveComponent;
import fr.kainovaii.obsidian.core.web.component.annotations.State;

@LiveComponent
public class Counter extends fr.kainovaii.obsidian.core.web.component.core.LiveComponent {
    
    @State
    private int count = 0;
    
    @State
    private String message = "Click the buttons to change the count!";
    
    public void increment() {
        count++;
        updateMessage();
    }
    
    public void decrement() {
        count--;
        updateMessage();
    }
    
    public void reset() {
        count = 0;
        message = "Counter reset!";
    }
    
    public void add5() {
        count += 5;
        updateMessage();
    }
    
    private void updateMessage() {
        if (count > 10) {
            message = "Wow! You're on fire! 🔥";
        } else if (count < 0) {
            message = "Going negative! 📉";
        } else if (count == 0) {
            message = "Back to zero! 🎯";
        } else {
            message = "Keep going! 💪";
        }
    }
    
    public int getCount() { return count; }
    public String getMessage() { return message; }
    
    @Override
    public String template() {
        return "components/counter.html";
    }
}
