-- Insert into tournament_teams using the test data from tournaments and teams
INSERT INTO tournament_teams (tournament_id, team_id)
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Alpha', 'Team Beta', 'Team Gamma', 'Team Delta', 'Team Zeta')
WHERE t.event_name IN ('Summer Games', 'Winter Championship', 'Spring Showdown', 'Autumn Clash', 'New Year Bash')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Eta', 'Team Theta', 'Team Kappa', 'Team Lambda', 'Team Omega')
WHERE t.event_name IN ('Olympic Trials', 'City Showdown', 'Championship Series', 'Global Games', 'Champions League')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Sigma', 'Team Phi', 'Team Chi', 'Team Psi', 'Team Xi')
WHERE t.event_name IN ('National Finals', 'City Clash', 'Battle of Boroughs', 'Winter Showdown', 'Summer Clash')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Omicron', 'Team Rho', 'Team Nu', 'Team Mu', 'Team Tau')
WHERE t.event_name IN ('East Coast Tournament', 'West Coast Showdown', 'Super League', 'Elite Championship', 'Ultimate Showdown')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Iota', 'Team Epsilon', 'Team Upsilon', 'Team Pi', 'Team Sigma Prime')
WHERE t.event_name IN ('Grand Finale', 'Super Tournament', 'World Series', 'Premier League', 'Ultimate Battle')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Theta Prime', 'Team Delta Prime', 'Team Gamma Prime', 'Team Alpha Prime', 'Team Omega Prime')
WHERE t.event_name IN ('Pro Series', 'Champion of Champions', 'Winter Games', 'Autumn Showdown', 'City Champions')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Zeta Prime', 'Team Lambda Prime', 'Team Kappa Prime', 'Team Eta Prime', 'Team Psi Prime')
WHERE t.event_name IN ('Battle Royale', 'Superstar League', 'Summer Challenge', 'Elite Tournament', 'Regional Clash')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Chi Prime', 'Team Omicron Prime', 'Team Sigma Beta', 'Team Gamma Beta', 'Team Alpha Beta')
WHERE t.event_name IN ('Pro League', 'Ultimate Championship', 'National Games', 'Finals Series', 'Global Challenge')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Theta Beta', 'Team Delta Beta', 'Team Kappa Beta', 'Team Lambda Beta', 'Team Zeta Beta')
WHERE t.event_name IN ('International Showdown', 'Champion Showdown', 'Winter Finals', 'City League', 'Ultimate Finals')
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name IN ('Team Omega Beta', 'Team Sigma Omega', 'Team Gamma Omega', 'Team Alpha Omega', 'Team Theta Omega')
WHERE t.event_name IN ('Elite Showdown', 'World Finals', 'Super Series', 'Global Finals', 'Summer Showdown');
