INSERT INTO tournament_tags (tournament_id, tag_id)
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Summer Games
WHERE t.event_name = 'Summer Games' AND tg.tag IN ('SummerGames', 'OutdoorEvents', 'NationalLeague', 'RoundOf16', 'GroupD')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Winter Championship
WHERE t.event_name = 'Winter Championship' AND tg.tag IN ('WinterGames', 'InternationalCup', 'QuarterFinals', 'GroupA', 'TieBreaker')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Spring Showdown
WHERE t.event_name = 'Spring Showdown' AND tg.tag IN ('Sports', 'TeamCompetition', 'QualifierRound', 'SemiFinals', 'PlayOffs')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Autumn Clash
WHERE t.event_name = 'Autumn Clash' AND tg.tag IN ('Athletics', 'FriendlyMatch', 'GroupStage', 'Champion', 'MedalWinner')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- New Year Bash
WHERE t.event_name = 'New Year Bash' AND tg.tag IN ('IndoorGames', 'Final', 'PenaltyShootout', 'AwardCeremony', 'Underdog')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Olympic Trials
WHERE t.event_name = 'Olympic Trials' AND tg.tag IN ('Olympics', 'FairPlay', 'ClosingCeremony', 'GoldenGoal', 'TeamSpirit')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Championship Series
WHERE t.event_name = 'Championship Series' AND tg.tag IN ('Championship', 'GroupB', 'ExtraTime', 'NationalRecord', 'SemiFinals')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Global Games
WHERE t.event_name = 'Global Games' AND tg.tag IN ('WorldCup', 'FinalStage', 'Underdog', 'Final', 'RunnerUp')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- Champions League
WHERE t.event_name = 'Champions League' AND tg.tag IN ('PremierLeague', 'NationalLeague', 'FairPlay', 'TieBreaker', 'AwardCeremony')
UNION ALL
SELECT t.id, tg.id
FROM tournaments t
JOIN tags tg
-- City Showdown
WHERE t.event_name = 'City Showdown' AND tg.tag IN ('TeamCompetition', 'QuarterFinals', 'PlayOffs', 'GroupC', 'FairPlay');
