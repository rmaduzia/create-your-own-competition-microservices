INSERT INTO tournament_teams (tournament_id, team_id)
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Alpha'
WHERE t.event_name = 'Champions League'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Beta'
WHERE t.event_name = 'World Cup'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Gamma'
WHERE t.event_name = 'Olympics'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Delta'
WHERE t.event_name = 'Grand Slam'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Zeta'
WHERE t.event_name = 'National Tournament'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Eta'
WHERE t.event_name = 'Winter Games'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Theta'
WHERE t.event_name = 'European Cup'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Kappa'
WHERE t.event_name = 'State Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Lambda'
WHERE t.event_name = 'Beach Volleyball Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Omega'
WHERE t.event_name = 'City Marathon'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Sigma'
WHERE t.event_name = 'World Athletics Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Alpha'
WHERE t.event_name = 'Table Tennis Open'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Zeta'
WHERE t.event_name = 'Track and Field Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Beta'
WHERE t.event_name = 'Cycling Tour'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Eta'
WHERE t.event_name = 'Rowing Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Theta'
WHERE t.event_name = 'Swimming Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Lambda'
WHERE t.event_name = 'National Tennis Open'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Delta'
WHERE t.event_name = 'Motorsports Finals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Alpha'
WHERE t.event_name = 'Ski Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Gamma'
WHERE t.event_name = 'Weightlifting Tournament'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Beta'
WHERE t.event_name = 'Rugby World Cup'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Eta'
WHERE t.event_name = 'Esports Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Theta'
WHERE t.event_name = 'Climbing Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Omega'
WHERE t.event_name = 'Surfing World Cup'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Sigma'
WHERE t.event_name = 'Triathlon Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Kappa'
WHERE t.event_name = 'Skating Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Delta'
WHERE t.event_name = 'Shooting Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Alpha'
WHERE t.event_name = 'Gymnastics Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Gamma'
WHERE t.event_name = 'Boxing Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Omega'
WHERE t.event_name = 'Billiards Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Beta'
WHERE t.event_name = 'Badminton Nationals'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Sigma'
WHERE t.event_name = 'Wrestling World Cup'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Theta'
WHERE t.event_name = 'Hockey Championship'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Alpha'
WHERE t.event_name = 'Archery Tournament'
UNION ALL
SELECT t.id, tm.id
FROM tournaments t
JOIN teams tm ON tm.team_name = 'Team Zeta'
WHERE t.event_name = 'Fencing Nationals';