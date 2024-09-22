INSERT INTO drawed_teams_in_tournament (tournament_id, id, duel)
SELECT t.id, '1', 'Team Alpha vs Team Beta'
FROM tournaments t
WHERE t.event_name = 'Champions League'
UNION ALL
SELECT t.id, '2', 'Team Gamma vs Team Delta'
FROM tournaments t
WHERE t.event_name = 'World Cup'
UNION ALL
SELECT t.id, '3', 'Team Zeta vs Team Eta'
FROM tournaments t
WHERE t.event_name = 'Olympics'
UNION ALL
SELECT t.id, '4', 'Team Theta vs Team Kappa'
FROM tournaments t
WHERE t.event_name = 'Grand Slam'
UNION ALL
SELECT t.id, '5', 'Team Lambda vs Team Omega'
FROM tournaments t
WHERE t.event_name = 'National Tournament'
UNION ALL
SELECT t.id, '6', 'Team Sigma vs Team Alpha'
FROM tournaments t
WHERE t.event_name = 'Winter Games'
UNION ALL
SELECT t.id, '7', 'Team Beta vs Team Gamma'
FROM tournaments t
WHERE t.event_name = 'European Cup'
UNION ALL
SELECT t.id, '8', 'Team Delta vs Team Zeta'
FROM tournaments t
WHERE t.event_name = 'State Championship'
UNION ALL
SELECT t.id, '9', 'Team Eta vs Team Theta'
FROM tournaments t
WHERE t.event_name = 'Beach Volleyball Championship'
UNION ALL
SELECT t.id, '10', 'Team Kappa vs Team Lambda'
FROM tournaments t
WHERE t.event_name = 'City Marathon'
UNION ALL
SELECT t.id, '11', 'Team Omega vs Team Sigma'
FROM tournaments t
WHERE t.event_name = 'World Athletics Championship'
UNION ALL
SELECT t.id, '12', 'Team Alpha vs Team Beta'
FROM tournaments t
WHERE t.event_name = 'Table Tennis Open'
UNION ALL
SELECT t.id, '13', 'Team Gamma vs Team Zeta'
FROM tournaments t
WHERE t.event_name = 'Track and Field Nationals'
UNION ALL
SELECT t.id, '14', 'Team Eta vs Team Delta'
FROM tournaments t
WHERE t.event_name = 'Cycling Tour'
UNION ALL
SELECT t.id, '15', 'Team Theta vs Team Kappa'
FROM tournaments t
WHERE t.event_name = 'Rowing Championship'
UNION ALL
SELECT t.id, '16', 'Team Lambda vs Team Omega'
FROM tournaments t
WHERE t.event_name = 'Swimming Championship'
UNION ALL
SELECT t.id, '17', 'Team Sigma vs Team Alpha'
FROM tournaments t
WHERE t.event_name = 'National Tennis Open'
UNION ALL
SELECT t.id, '18', 'Team Beta vs Team Gamma'
FROM tournaments t
WHERE t.event_name = 'Motorsports Finals'
UNION ALL
SELECT t.id, '19', 'Team Zeta vs Team Delta'
FROM tournaments t
WHERE t.event_name = 'Ski Championship'
UNION ALL
SELECT t.id, '20', 'Team Eta vs Team Theta'
FROM tournaments t
WHERE t.event_name = 'Weightlifting Tournament'
UNION ALL
SELECT t.id, '21', 'Team Kappa vs Team Lambda'
FROM tournaments t
WHERE t.event_name = 'Rugby World Cup'
UNION ALL
SELECT t.id, '22', 'Team Omega vs Team Sigma'
FROM tournaments t
WHERE t.event_name = 'Esports Championship'
UNION ALL
SELECT t.id, '23', 'Team Alpha vs Team Beta'
FROM tournaments t
WHERE t.event_name = 'Climbing Nationals'
UNION ALL
SELECT t.id, '24', 'Team Gamma vs Team Zeta'
FROM tournaments t
WHERE t.event_name = 'Surfing World Cup'
UNION ALL
SELECT t.id, '25', 'Team Delta vs Team Eta'
FROM tournaments t
WHERE t.event_name = 'Triathlon Nationals'
UNION ALL
SELECT t.id, '26', 'Team Theta vs Team Kappa'
FROM tournaments t
WHERE t.event_name = 'Skating Nationals'
UNION ALL
SELECT t.id, '27', 'Team Lambda vs Team Omega'
FROM tournaments t
WHERE t.event_name = 'Shooting Nationals'
UNION ALL
SELECT t.id, '28', 'Team Sigma vs Team Alpha'
FROM tournaments t
WHERE t.event_name = 'Gymnastics Championship'
UNION ALL
SELECT t.id, '29', 'Team Beta vs Team Gamma'
FROM tournaments t
WHERE t.event_name = 'Boxing Championship'
UNION ALL
SELECT t.id, '30', 'Team Zeta vs Team Delta'
FROM tournaments t
WHERE t.event_name = 'Billiards Championship'
UNION ALL
SELECT t.id, '31', 'Team Eta vs Team Theta'
FROM tournaments t
WHERE t.event_name = 'Badminton Nationals'
UNION ALL
SELECT t.id, '32', 'Team Kappa vs Team Lambda'
FROM tournaments t
WHERE t.event_name = 'Wrestling World Cup'
UNION ALL
SELECT t.id, '33', 'Team Omega vs Team Sigma'
FROM tournaments t
WHERE t.event_name = 'Hockey Championship'
UNION ALL
SELECT t.id, '34', 'Team Alpha vs Team Beta'
FROM tournaments t
WHERE t.event_name = 'Archery Tournament'
UNION ALL
SELECT t.id, '35', 'Team Gamma vs Team Zeta'
FROM tournaments t
WHERE t.event_name = 'Fencing Nationals'
UNION ALL
SELECT t.id, '36', 'Team Delta vs Team Eta'
FROM tournaments t
WHERE t.event_name = 'Wrestling Nationals'
UNION ALL
SELECT t.id, '37', 'Team Theta vs Team Kappa'
FROM tournaments t
WHERE t.event_name = 'Table Tennis Nationals'
UNION ALL
SELECT t.id, '38', 'Team Lambda vs Team Omega'
FROM tournaments t
WHERE t.event_name = 'Beach Volleyball Finals'
UNION ALL
SELECT t.id, '39', 'Team Sigma vs Team Alpha'
FROM tournaments t
WHERE t.event_name = 'Rowing Nationals'
UNION ALL
SELECT t.id, '40', 'Team Beta vs Team Gamma'
FROM tournaments t
WHERE t.event_name = 'Climbing World Cup'
UNION ALL
SELECT t.id, '41', 'Team Zeta vs Team Delta'
FROM tournaments t
WHERE t.event_name = 'Motorsports Championship'
UNION ALL
SELECT t.id, '42', 'Team Eta vs Team Theta'
FROM tournaments t
WHERE t.event_name = 'National Basketball League'
UNION ALL
SELECT t.id, '43', 'Team Kappa vs Team Lambda'
FROM tournaments t
WHERE t.event_name = 'World Swimming Championship'
UNION ALL
SELECT t.id, '44', 'Team Omega vs Team Sigma'
FROM tournaments t
WHERE t.event_name = 'Cycling Nationals'
UNION ALL
SELECT t.id, '45', 'Team Alpha vs Team Beta'
FROM tournaments t
WHERE t.event_name = 'Skating Finals';