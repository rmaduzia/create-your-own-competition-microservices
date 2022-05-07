package pl.createcompetition.util;

import java.util.*;

public class MatchTeamsInTournament {

    public static HashMap<String, String> matchTeamsInTournament(List<String> teamsName) {

        char[] alphabetChars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        HashMap<String, String> matchedTeams = new HashMap<>();
        String team1;
        String team2;
        int charIndex = 0;

        do {
            Collections.shuffle(teamsName);
            team1 = teamsName.get(0);
            teamsName.remove(0);
            Collections.shuffle(teamsName);
            team2 = teamsName.get(0);
            teamsName.remove(0);
            matchedTeams.put(String.valueOf(alphabetChars[charIndex]), team1 + " VS " + team2);
            charIndex += 1;
        } while(teamsName.size()>1);

        return matchedTeams;
    }


    public static HashMap<String, String> matchTeamsWithEachOtherInTournament(List<String> teamsName) {
        int index =0;
        HashMap<String, String> matchedTeams = new HashMap<>();
        for (Iterator<String> iterator = teamsName.iterator(); iterator.hasNext();) {
            String teamName = iterator.next();
            for (String oponentTeamname : teamsName) {
                if (!teamName.equals(oponentTeamname)) {
                    matchedTeams.put(String.valueOf(index), teamName + " VS " + oponentTeamname);
                    index += 1;
                }
            }
            iterator.remove();
        }

        return matchedTeams;
    }
}
