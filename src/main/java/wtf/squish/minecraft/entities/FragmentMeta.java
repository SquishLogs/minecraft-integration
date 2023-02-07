package wtf.squish.minecraft.entities;

/**
 * Metadata container for fragments
 * @author Livaco
 */
public class FragmentMeta {
    private String title;
    private String text;

    public FragmentMeta(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
