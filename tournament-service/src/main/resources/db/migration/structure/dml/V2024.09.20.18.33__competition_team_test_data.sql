-- Insert into competition_team using the test data from competitions and teams
INSERT INTO competition_team (competition_id, team_id)
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Alpha', 'Team Beta', 'Team Gamma', 'Team Delta', 'Team Zeta')
WHERE c.event_name IN ('Spring Showdown', 'Summer Games', 'Autumn Clash', 'Winter Championship', 'New Year Bash')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Eta', 'Team Theta', 'Team Kappa', 'Team Lambda', 'Team Omega')
WHERE c.event_name IN ('Olympic Trials', 'City Showdown', 'Championship Series', 'Global Games', 'Champions League')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Sigma', 'Team Phi', 'Team Chi', 'Team Psi', 'Team Xi')
WHERE c.event_name IN ('National Finals', 'City Clash', 'Battle of Boroughs', 'Winter Showdown', 'Summer Clash')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Omicron', 'Team Rho', 'Team Nu', 'Team Mu', 'Team Tau')
WHERE c.event_name IN ('East Coast Tournament', 'West Coast Showdown', 'Super League', 'Elite Championship', 'Ultimate Showdown')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Iota', 'Team Epsilon', 'Team Upsilon', 'Team Pi', 'Team Sigma Prime')
WHERE c.event_name IN ('Grand Finale', 'Super Tournament', 'World Series', 'Premier League', 'Ultimate Battle')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Theta Prime', 'Team Delta Prime', 'Team Gamma Prime', 'Team Alpha Prime', 'Team Omega Prime')
WHERE c.event_name IN ('Pro Series', 'Champion of Champions', 'Winter Games', 'Autumn Showdown', 'City Champions')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Zeta Prime', 'Team Lambda Prime', 'Team Kappa Prime', 'Team Eta Prime', 'Team Psi Prime')
WHERE c.event_name IN ('Battle Royale', 'Superstar League', 'Summer Challenge', 'Elite Tournament', 'Regional Clash')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Chi Prime', 'Team Omicron Prime', 'Team Sigma Beta', 'Team Gamma Beta', 'Team Alpha Beta')
WHERE c.event_name IN ('Pro League', 'Ultimate Championship', 'National Games', 'Finals Series', 'Global Challenge')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Theta Beta', 'Team Delta Beta', 'Team Kappa Beta', 'Team Lambda Beta', 'Team Zeta Beta')
WHERE c.event_name IN ('International Showdown', 'Champion Showdown', 'Winter Finals', 'City League', 'Ultimate Finals')
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name IN ('Team Omega Beta', 'Team Sigma Omega', 'Team Gamma Omega', 'Team Alpha Omega', 'Team Theta Omega')
WHERE c.event_name IN ('Elite Showdown', 'World Finals', 'Super Series', 'Global Finals', 'Summer Showdown');
