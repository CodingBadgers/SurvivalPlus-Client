package uk.codingbadgers.survivalplus.data;

import java.net.URL;

public class IconData {

    public URL url;
    public String hash;

    public IconData(URL url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Icon{");
        sb.append("url='").append(url).append('\'');
        sb.append(", hash='").append(hash).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
