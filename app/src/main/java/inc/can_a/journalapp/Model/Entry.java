package inc.can_a.journalapp.Model;

/**
 * Created by CAN-A on 6/25/2018.
 */

public class Entry {
    private int id;
    private String title;
    private String note;

    public Entry(){
    }


    public Entry(int id,String title,String note){
        this.id = id;
        this.title = title;
        this.note = note;
    }

    public int getId(){
        return  this.id;
    }
    public  void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return  this.title;
    }
    public  void setTitle(String title){
        this.title = title;
    }

    public String getNote(){
        return  this.note;
    }
    public  void setNote(String note){
        this.note = note;
    }
}
