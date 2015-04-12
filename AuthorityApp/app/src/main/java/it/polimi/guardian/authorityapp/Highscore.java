package it.polimi.guardian.authorityapp;

/**
 * Created by Nemanja on 12/04/2015.
 */
public class Highscore implements Comparable{
    private String authorityUsername;
    private int score;

    public String getAuthorityUsername() {
        return authorityUsername;
    }

    public void setAuthorityUsername(String authorityUsername) {
        this.authorityUsername = authorityUsername;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public int compareTo(Object another) {
        Highscore h2 = (Highscore) another;
        Integer obj1 = new Integer(this.getScore());
        Integer obj2 = new Integer(h2.getScore());
        return obj1.compareTo(obj2) * (-1);
    }
}
