package com.obsidian.app.components;

import com.obsidian.core.livecomponents.annotations.Action;
import com.obsidian.core.livecomponents.annotations.LiveComponentImpl;
import com.obsidian.core.livecomponents.annotations.State;
import com.obsidian.core.livecomponents.core.LiveComponent;

import java.util.ArrayList;
import java.util.List;

@LiveComponentImpl
public class Notification extends LiveComponent
{
    @State
    private List<NotificationItem> notifications = new ArrayList<>();
    
    @State
    private int nextId = 1;
    
    @Action
    public void addSuccess() {
        notifications.add(new NotificationItem(nextId++, "success", "Success!", "Operation completed successfully."));
    }
    
    @Action
    public void addError() {
        notifications.add(new NotificationItem(nextId++, "error", "Error!", "Something went wrong. Please try again."));
    }
    
    @Action
    public void addWarning() {
        notifications.add(new NotificationItem(nextId++, "warning", "Warning!", "Please review your recent changes."));
    }
    
    @Action
    public void addInfo() {
        notifications.add(new NotificationItem(nextId++, "info", "Info", "New features are now available."));
    }

    @Action
    public void dismiss(int id) {
        notifications.removeIf(n -> n.id == id);
    }
    
    @Action
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
