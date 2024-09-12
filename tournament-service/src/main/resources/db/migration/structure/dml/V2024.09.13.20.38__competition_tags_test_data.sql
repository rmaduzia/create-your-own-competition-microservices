INSERT INTO competition_tag (tag_id, competition_id)
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Sports' AND c.event_name = 'World Cup'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Football' AND c.event_name = 'Premier League'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'SummerGames' AND c.event_name = 'Olympics'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Championship' AND c.event_name = 'Champions League'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Indoor' AND c.event_name = 'Basketball Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Athletics' AND c.event_name = 'Track and Field'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'WinterGames' AND c.event_name = 'Winter Olympics'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Marathon' AND c.event_name = 'City Marathon'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Soccer' AND c.event_name = 'National Soccer League'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Volleyball' AND c.event_name = 'Beach Volleyball Tournament'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Tennis' AND c.event_name = 'US Open'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'TableTennis' AND c.event_name = 'National Table Tennis Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Cycling' AND c.event_name = 'Tour de France'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Rugby' AND c.event_name = 'World Rugby Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Swimming' AND c.event_name = 'National Swimming Meet'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Hockey' AND c.event_name = 'International Hockey League'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Cricket' AND c.event_name = 'Cricket World Cup'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Golf' AND c.event_name = 'Masters Tournament'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Boxing' AND c.event_name = 'World Boxing Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Wrestling' AND c.event_name = 'Wrestling National Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Motorsports' AND c.event_name = 'Formula 1 Grand Prix'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Surfing' AND c.event_name = 'Surfing World Cup'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Rowing' AND c.event_name = 'National Rowing Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Weightlifting' AND c.event_name = 'World Weightlifting Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Triathlon' AND c.event_name = 'International Triathlon Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Skating' AND c.event_name = 'Figure Skating World Cup'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Esports' AND c.event_name = 'World Esports Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Archery' AND c.event_name = 'Archery World Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Bowling' AND c.event_name = 'National Bowling Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Shooting' AND c.event_name = 'International Shooting Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'HorseRacing' AND c.event_name = 'Royal Ascot'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Billiards' AND c.event_name = 'World Billiards Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Fencing' AND c.event_name = 'Fencing National Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Gymnastics' AND c.event_name = 'World Gymnastics Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Climbing' AND c.event_name = 'Climbing World Cup'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Badminton' AND c.event_name = 'World Badminton Championship'
UNION ALL
SELECT t.id, c.id
FROM tags t, competitions c
WHERE t.tag = 'Squash' AND c.event_name = 'World Squash Tournament';