package org.voicewave.voicewave;

/**
 * Created by chihyun on 15. 8. 5..
 */
public class Comment {
    private String nickname;
    private String description;



    public Comment(String nickname, String description) {
        super();
        this.nickname = nickname;
        this.description = description;

    }
    public String getNickname(){
        return this.nickname;
    }
    public String getDescription(){
        return this.description;
    }
}
