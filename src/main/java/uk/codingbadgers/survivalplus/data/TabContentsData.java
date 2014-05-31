package uk.codingbadgers.survivalplus.data;

import java.util.Arrays;

public class TabContentsData {

    public String tab;
    public String title;
    public String[] content;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TabContentsData{");
        sb.append("tab='").append(tab).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", content=").append(Arrays.toString(content));
        sb.append('}');
        return sb.toString();
    }
}
