package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.livecomponents.annotations.LiveComponentImpl;
import fr.kainovaii.obsidian.livecomponents.annotations.State;
import fr.kainovaii.obsidian.livecomponents.core.LiveComponent;

import java.util.ArrayList;
import java.util.List;

@LiveComponentImpl
public class Notification extends LiveComponent
{
    @State
    private List<NotificationItem> notifications = new ArrayList<>();
    
    @State
    private int nextId = 1;
    
    public void addSuccess() {
        notifications.add(new NotificationItem(nextId++, "success", "Success!", "Operation completed successfully."));
    }
    
    public void addError() {
        notifications.add(new NotificationItem(nextId++, "error", "Error!", "Something went wrong. Please try again."));
    }
    
    public void addWarning() {
        notifications.add(new NotificationItem(nextId++, "warning", "Warning!", "Please review your recent changes."));
    }
    
    public void addInfo() {
        notifications.add(new NotificationItem(nextId++, "info", "Info", "New features are now available."));
    }

    public void dismiss(int id) {
        notifications.removeIf(n -> n.id == id);
    }
    
    public void dismissAll() {
        notifications.clear();
    }
    
    public List<NotificationItem> getNotifications() { return notifications; }
    public int getCount() { return notifications.size(); }
    
    @Override
    public String template() {
        return "components/notification.html";
    }
    
    public static class NotificationItem {
        public int id;
        public String type;
        public String title;
        public String message;
        
        public NotificationItem() {}
        
        public NotificationItem(int id, String type, String title, String message) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.message = message;
        }
        
        public int getId() { return id; }
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
    }
}
