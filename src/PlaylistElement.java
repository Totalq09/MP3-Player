import javafx.beans.property.SimpleStringProperty;

/**
 * Created by piter on 2017-03-17.
 */
public class PlaylistElement {
    private final SimpleStringProperty songTitle;
    private final SimpleStringProperty songPath;

    public PlaylistElement(String t){

        int pos = t.lastIndexOf('/');
        int pos2 = t.lastIndexOf('.');

        String temp = t.substring(pos+1,pos2);
        temp = temp.replace("%20", " ");
        if(temp.length() > 28){
            temp = temp.substring(0,28);
            temp += "...";
        }

        songPath = new SimpleStringProperty(t);
        songTitle = new SimpleStringProperty(temp);
    }

    public void setSongTitle(String t){
        int pos = t.lastIndexOf('/');
        int pos2 = t.lastIndexOf('.');

        String temp = t.substring(pos+1,pos2);
        temp = temp.replace("%20", " ");
        if(temp.length() > 28){
            temp = temp.substring(0,28);
            temp += "...";
        }

        songTitle.set(temp);
    }

    public String getSongTitle(){
        return songTitle.get();
    }

    public void setSongPath(String t){
        songPath.set(t);
    }

    public String getSongPath(){
        return songPath.get();
    }
}
