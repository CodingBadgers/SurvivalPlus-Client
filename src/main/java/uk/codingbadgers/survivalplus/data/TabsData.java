package uk.codingbadgers.survivalplus.data;

import java.util.Arrays;

public class TabsData {

    public Tab[] tabs;

    public static class Tab {
        public boolean global;
        public String id;
        public String name;
        public IconData icon;

        public Tab(String id, String name, IconData icon, boolean global) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.global = global;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Tab{");
            sb.append("id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", icon='").append(icon).append('\'');
            sb.append(", global='").append(global).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TabsData{");
        sb.append("tabs=").append(Arrays.toString(tabs));
        sb.append('}');
        return sb.toString();
    }

}
