package pl.createcompetition.util.email;

import java.io.Serializable;
import java.time.LocalDate;

public class EmailMassageTemplate implements Serializable {

    private static EmailMassageTemplate instance = new EmailMassageTemplate();

    private EmailMassageTemplate() {
    }

    public static String topic(String changedData) {
        return "Your " + changedData +" has been changed";
    }

    public static String welcome(String name) {
        return "Welcome : " + name + ".";
    }

    public static String message(String changedData, String dataValue) {
        return "Your " + changedData + " has been changed " +
                LocalDate.now().toString() + "to : " + dataValue + ".";
    }

    public static String buttonName(String changedData) {
        return "Change : " + changedData + ".";
    }

    public static String changeLink(String link) {
        return link;
    }

    public static String goodbye(){
        return "Best regards,";
    }

    public static String appName(){
        return "Junior Start app";
    }

    /** Method for protecting class from serialization.
     */
    protected Object readResolve() {
        return instance;
    }
}