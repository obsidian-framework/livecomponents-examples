package fr.kainovaii.obsidian.app.components;

import com.obsidian.core.livecomponents.annotations.Action;
import com.obsidian.core.livecomponents.annotations.LiveComponentImpl;
import com.obsidian.core.livecomponents.annotations.State;
import com.obsidian.core.livecomponents.core.LiveComponent;

@LiveComponentImpl
public class Counter extends LiveComponent
{
    @State
    private int count = 0;
    
    @State
    private String message = "Click the buttons to change the count!";
    
    @Action
    public void increment() {
        count++;
        updateMessage();
    }
    
    @Action
    public void decrement() {
        count--;
        updateMessage();
    }
    
    @Action
    public void reset() {
        count = 0;
        message = "Counter reset!";
    }
    
    @Action
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
