package fr.kainovaii.obsidian.app.components;

import fr.kainovaii.obsidian.livecomponents.annotations.LiveComponentImpl;
import fr.kainovaii.obsidian.livecomponents.annotations.State;
import fr.kainovaii.obsidian.livecomponents.core.LiveComponent;

import java.util.Map;

@LiveComponentImpl
public class Tabs extends LiveComponent
{
    @State
    private String activeTab = "profile";
    
    private final Map<String, String> tabs = Map.of(
        "profile", "👤 Profile",
        "settings", "⚙️ Settings",
        "notifications", "🔔 Notifications",
        "security", "🔒 Security"
    );
    
    private final Map<String, String> content = Map.of(
        "profile", "This is your profile page. Here you can update your personal information, avatar, and bio.",
        "settings", "Manage your application settings, theme preferences, and default behaviors.",
        "notifications", "Control how and when you receive notifications from the application.",
        "security", "Update your password, enable two-factor authentication, and manage active sessions."
    );

    public void switchTab(String tab) {
        if (tabs.containsKey(tab)) {
            this.activeTab = tab;
        }
    }
    
    public String getActiveContent() {
        return content.getOrDefault(activeTab, "No content available");
    }
    
    public String getActiveTab() { return activeTab; }
    public Map<String, String> getTabs() { return tabs; }
    
    @Override
    public String template() {
        return "components/tabs.html";
    }
}
