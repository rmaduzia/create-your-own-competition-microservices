package pl.createcompetition.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.*;

public class MatchTeamsInTournamentTest {

    @Test
    public void matchTeamsInTournament() {

        List<String> listOfTeams = new ArrayList<>();
        listOfTeams.add("Team1");
        listOfTeams.add("Team2");

        Map<String,String> exceptedMatchedTeams1 = new TreeMap<>();
        exceptedMatchedTeams1.put("A", "Team1 VS Team2");
        Map<String,String> exceptedMatchedTeams2 = new TreeMap<>();
        exceptedMatchedTeams2.put("A", "Team2 VS Team1");

        Map<String,String> matchedTeams = MatchTeamsInTournament.matchTeamsInTournament(listOfTeams);

        assertTrue(matchedTeams.equals(exceptedMatchedTeams1) || matchedTeams.equals(exceptedMatchedTeams2));

    }

    @Test
    public void matchTeamsWithEachOtherInTournament() {

        List<String> listOfTeams = new ArrayList<>();
        listOfTeams.add("Team1");
        listOfTeams.add("Team2");
        listOfTeams.add("Team3");

        Map<String,String> exceptedMatchedTeams = new TreeMap<>();
        exceptedMatchedTeams.put("0", "Team1 VS Team2");
        exceptedMatchedTeams.put("1", "Team1 VS Team3");
        exceptedMatchedTeams.put("2", "Team2 VS Team3");

        Map<String,String> matchedTeams = MatchTeamsInTournament.matchTeamsWithEachOtherInTournament(listOfTeams);

        assertEquals(matchedTeams, exceptedMatchedTeams);
    }
}