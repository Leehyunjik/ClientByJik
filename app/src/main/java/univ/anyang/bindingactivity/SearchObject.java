package univ.anyang.bindingactivity;

/**
 * Created by Administrator on 2016-10-27.
 */

public class SearchObject {
    String classcode=null;
    String classno=null;
    String classroom=null;
    String week=null;
    String ctime=null;
    SearchObject(String classcode, String classno, String classroom, String week, String ctime){
        this.classcode=classcode;
       this.classno=classno;
       this.classroom=classroom;
       this.week=week;
       this.ctime=ctime;
    }
    SearchObject(){

    }
}
