INSERT INTO tournament_tags (tag_id, tournament_id)
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Sports' AND tn.event_name = 'National Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Football' AND tn.event_name = 'Football World Cup'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'SummerGames' AND tn.event_name = 'Summer Olympics'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Championship' AND tn.event_name = 'Championship Finals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Indoor' AND tn.event_name = 'Indoor Volleyball Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Athletics' AND tn.event_name = 'Track and Field Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'WinterGames' AND tn.event_name = 'Winter Sports Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Marathon' AND tn.event_name = 'City Marathon Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Soccer' AND tn.event_name = 'National Soccer Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Volleyball' AND tn.event_name = 'Beach Volleyball Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Tennis' AND tn.event_name = 'International Tennis Cup'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'TableTennis' AND tn.event_name = 'Table Tennis Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Cycling' AND tn.event_name = 'Cycling Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Rugby' AND tn.event_name = 'National Rugby Finals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Swimming' AND tn.event_name = 'Swimming Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Hockey' AND tn.event_name = 'Hockey Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Cricket' AND tn.event_name = 'Cricket World Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Golf' AND tn.event_name = 'Golf Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Boxing' AND tn.event_name = 'Boxing Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Wrestling' AND tn.event_name = 'Wrestling National Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Motorsports' AND tn.event_name = 'Motorsports Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Surfing' AND tn.event_name = 'National Surfing Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Rowing' AND tn.event_name = 'Rowing Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Weightlifting' AND tn.event_name = 'Weightlifting Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Triathlon' AND tn.event_name = 'Triathlon Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Skating' AND tn.event_name = 'Skating Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Esports' AND tn.event_name = 'Esports World Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Archery' AND tn.event_name = 'Archery Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Bowling' AND tn.event_name = 'Bowling Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Shooting' AND tn.event_name = 'Shooting Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'HorseRacing' AND tn.event_name = 'Horse Racing Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Billiards' AND tn.event_name = 'Billiards Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Fencing' AND tn.event_name = 'Fencing Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Gymnastics' AND tn.event_name = 'Gymnastics World Cup'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Climbing' AND tn.event_name = 'Climbing Championship'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Badminton' AND tn.event_name = 'Badminton Nationals'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Squash' AND tn.event_name = 'Squash Tournament'
UNION ALL
SELECT t.id, tn.id
FROM tags t, tournaments tn
WHERE t.tag = 'Judo' AND tn.event_name = 'Judo Nationals';