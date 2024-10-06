INSERT INTO competition_tag (tag_id, competition_id)
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('Sports', 'Football', 'SummerGames', 'WorldCup', 'Championship')
AND c.event_name IN ('Summer Games', 'Winter Championship', 'Spring Showdown', 'Autumn Clash', 'New Year Bash')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('PremierLeague', 'Tournament2024', 'Athletics', 'IndoorGames', 'OutdoorEvents')
AND c.event_name IN ('Olympic Trials', 'City Showdown', 'Championship Series', 'Global Games', 'Champions League')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('TeamCompetition', 'NationalLeague', 'InternationalCup', 'Olympics', 'WinterGames')
AND c.event_name IN ('National Finals', 'City Clash', 'Battle of the Boroughs', 'Winter Showdown', 'Summer Clash')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('FriendlyMatch', 'QualifierRound', 'FinalStage', 'GroupStage', 'KnockoutStage')
AND c.event_name IN ('East Coast Tournament', 'West Coast Showdown', 'Super League', 'Elite Championship', 'Ultimate Showdown')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('SemiFinals', 'QuarterFinals', 'RoundOf16', 'Final', 'PenaltyShootout')
AND c.event_name IN ('Grand Finale', 'Super Tournament', 'World Series', 'Premier League', 'Ultimate Battle')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('ExtraTime', 'GroupA', 'GroupB', 'GroupC', 'GroupD')
AND c.event_name IN ('Pro Series', 'Champion of Champions', 'Winter Games', 'Autumn Showdown', 'City Champions')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('GroupE', 'GroupF', 'GroupG', 'GroupH', 'CupFinals')
AND c.event_name IN ('Battle Royale', 'Superstar League', 'Summer Challenge', 'Elite Tournament', 'Regional Clash')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('OpeningCeremony', 'ClosingCeremony', 'WorldRecord', 'NationalRecord', 'TieBreaker')
AND c.event_name IN ('Winter Showdown', 'Championship Clash', 'Final Battle', 'Superstar Showdown', 'Ultimate Clash')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('Underdog', 'Champion', 'RunnerUp', 'MedalWinner', 'AwardCeremony')
AND c.event_name IN ('Pro League', 'Winter Clash', 'Summer Bash', 'City Finals', 'Ultimate Series')

UNION ALL

SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag IN ('TeamSpirit', 'FairPlay', 'Sportsmanship', 'GoldenGoal', 'PlayOffs')
AND c.event_name IN ('Global Showdown', 'Champion Showdown', 'Grand Battle', 'Winter League', 'Summer Finals');
