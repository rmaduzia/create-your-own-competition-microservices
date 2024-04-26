package pl.createcompetition.teamservice.all;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTeamRequest {

  private String teamName;
  private String city;

}
