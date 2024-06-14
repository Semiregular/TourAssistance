package org.example;

public class NoteMessage {
    int id;
    String name;
    String title;
    String content;
    String timeCreated;
    String timeModified;
    int view;//浏览量
    String[] tag;//长度至多为三
    int uid;
    int pid;

    public void Copy(NoteMessage input){
        id=input.id;
        name=input.name;
        title=input.title;
        content=input.content;
        timeCreated=input.timeCreated;
        timeModified=input.timeModified;
        view=input.view;
        uid=input.uid;
        pid=input.pid;
        tag =new String[input.tag.length];
        System.arraycopy(input.tag, 0, tag, 0, input.tag.length);
    }
}
