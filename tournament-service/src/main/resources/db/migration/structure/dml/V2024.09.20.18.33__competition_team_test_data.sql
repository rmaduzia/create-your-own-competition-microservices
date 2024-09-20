INSERT INTO competition_team (competition_id, team_id)
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Alpha'
WHERE c.event_name = 'National Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Beta'
WHERE c.event_name = 'Football World Cup'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Gamma'
WHERE c.event_name = 'Summer Olympics'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Delta'
WHERE c.event_name = 'Championship Finals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Zeta'
WHERE c.event_name = 'Indoor Volleyball Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Eta'
WHERE c.event_name = 'Track and Field Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Theta'
WHERE c.event_name = 'Winter Sports Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Kappa'
WHERE c.event_name = 'City Marathon Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Lambda'
WHERE c.event_name = 'National Soccer Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Omega'
WHERE c.event_name = 'Beach Volleyball Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Sigma'
WHERE c.event_name = 'International Tennis Cup'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Alpha'
WHERE c.event_name = 'Table Tennis Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Zeta'
WHERE c.event_name = 'Cycling Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Beta'
WHERE c.event_name = 'National Rugby Finals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Eta'
WHERE c.event_name = 'Swimming Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Theta'
WHERE c.event_name = 'Hockey Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Lambda'
WHERE c.event_name = 'Cricket World Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Delta'
WHERE c.event_name = 'Golf Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Alpha'
WHERE c.event_name = 'Boxing Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Gamma'
WHERE c.event_name = 'Wrestling National Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Beta'
WHERE c.event_name = 'Motorsports Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Eta'
WHERE c.event_name = 'National Surfing Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Theta'
WHERE c.event_name = 'Rowing Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Omega'
WHERE c.event_name = 'Weightlifting Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Sigma'
WHERE c.event_name = 'Triathlon Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Kappa'
WHERE c.event_name = 'Skating Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Delta'
WHERE c.event_name = 'Esports World Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Alpha'
WHERE c.event_name = 'Archery Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Gamma'
WHERE c.event_name = 'Bowling Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Omega'
WHERE c.event_name = 'Shooting Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Beta'
WHERE c.event_name = 'Horse Racing Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Sigma'
WHERE c.event_name = 'Billiards Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Theta'
WHERE c.event_name = 'Fencing Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Alpha'
WHERE c.event_name = 'Gymnastics World Cup'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Zeta'
WHERE c.event_name = 'Climbing Championship'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Beta'
WHERE c.event_name = 'Badminton Nationals'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Gamma'
WHERE c.event_name = 'Squash Tournament'
UNION ALL
SELECT c.id, t.id
FROM competitions c
JOIN teams t ON t.team_name = 'Team Lambda'
WHERE c.event_name = 'Judo Nationals';