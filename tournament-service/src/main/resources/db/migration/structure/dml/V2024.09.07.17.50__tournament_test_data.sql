INSERT INTO tournaments (event_owner, event_name, max_amount_of_teams, city, street_name, street_number, event_start_date, event_end_date, is_open_recruitment, is_event_started, is_event_finished)
VALUES

('Alice', 'Summer Games', 10, 'NewYork', 'Broadway', 101, '2024-06-01 09:00:00', '2024-06-10 18:00:00', true, false, false),
('Bob', 'Winter Championship', 12, 'LosAngeles', 'Sunset', 22, '2024-12-01 09:00:00', '2024-12-20 18:00:00', false, false, false),
('Charlie', 'Spring Showdown', 8, 'Chicago', 'Michigan', 5, '2024-03-15 09:00:00', '2024-03-20 18:00:00', true, false, false),
('David', 'Autumn Clash', 6, 'Houston', 'Main', 78, '2024-09-10 09:00:00', '2024-09-15 18:00:00', true, false, false),
('Eva', 'New Year Bash', 16, 'Phoenix', 'Camelback', 21, '2025-01-05 09:00:00', '2025-01-10 18:00:00', true, false, false),
('Grace', 'Olympic Trials', 10, 'Dallas', 'Elm', 30, '2024-05-05 09:00:00', '2024-05-15 18:00:00', false, false, false),
('Harry', 'City Showdown', 14, 'Austin', 'Second', 100, '2024-06-20 09:00:00', '2024-06-25 18:00:00', true, false, false),
('Ivy', 'Championship Series', 20, 'Denver', 'Broadway', 88, '2024-07-01 09:00:00', '2024-07-10 18:00:00', true, false, false),
('Jack', 'Global Games', 8, 'SanFrancisco', 'Market', 22, '2024-08-10 09:00:00', '2024-08-15 18:00:00', true, false, false),
('Kelly', 'Champions League', 12, 'Miami', 'Ocean', 32, '2024-09-01 09:00:00', '2024-09-10 18:00:00', false, true, false),

('Linda', 'National Finals', 18, 'Boston', 'Boylston', 55, '2024-10-10 09:00:00', '2024-10-20 18:00:00', true, true, false),
('Mike', 'City Clash', 10, 'Seattle', 'Pine', 90, '2024-11-05 09:00:00', '2024-11-15 18:00:00', true, false, true),
('Nina', 'Battle of Boroughs', 15, 'Portland', 'Burnside', 11, '2024-12-10 09:00:00', '2024-12-20 18:00:00', false, true, true),
('Oscar', 'Winter Showdown', 10, 'LasVegas', 'Main', 45, '2025-01-15 09:00:00', '2025-01-20 18:00:00', true, false, false),
('Peter', 'Summer Clash', 8, 'SanDiego', 'Pacific', 50, '2024-07-15 09:00:00', '2024-07-20 18:00:00', false, true, true),
('Quincy', 'East Coast Tournament', 14, 'Washington', 'Constitution', 33, '2024-08-01 09:00:00', '2024-08-10 18:00:00', true, false, false),
('Rachel', 'West Coast Showdown', 16, 'LosAngeles', 'Hollywood', 150, '2024-09-05 09:00:00', '2024-09-15 18:00:00', true, true, false),
('Steve', 'Super League', 10, 'Houston', 'Westheimer', 75, '2024-10-01 09:00:00', '2024-10-07 18:00:00', false, false, false),
('Tom', 'Elite Championship', 20, 'Dallas', 'Main', 20, '2024-11-01 09:00:00', '2024-11-10 18:00:00', true, false, true),
('Ursula', 'Ultimate Showdown', 10, 'Chicago', 'Wacker', 10, '2024-12-15 09:00:00', '2024-12-25 18:00:00', true, true, true),

('Vicky', 'Grand Finale', 20, 'Phoenix', 'Camelback', 21, '2025-01-05 09:00:00', '2025-01-10 18:00:00', true, false, false),
('Will', 'Super Tournament', 14, 'NewYork', 'Fifth', 123, '2024-06-01 09:00:00', '2024-06-10 18:00:00', true, false, true),
('Xander', 'World Series', 20, 'LosAngeles', 'Sunset', 55, '2024-12-01 09:00:00', '2024-12-20 18:00:00', false, true, false),
('Yara', 'Premier League', 10, 'Chicago', 'Michigan', 10, '2024-03-15 09:00:00', '2024-03-20 18:00:00', true, false, false),
('Zack', 'Ultimate Battle', 16, 'Austin', 'Congress', 70, '2024-09-01 09:00:00', '2024-09-05 18:00:00', true, true, true),
('Amy', 'Pro Series', 12, 'SanDiego', 'LaJolla', 35, '2024-07-15 09:00:00', '2024-07-20 18:00:00', true, false, false),
('Ben', 'Champion of Champions', 16, 'Houston', 'Washington', 45, '2024-09-10 09:00:00', '2024-09-15 18:00:00', false, false, false),
('Cara', 'Winter Games', 14, 'Denver', 'Colfax', 88, '2024-12-05 09:00:00', '2024-12-15 18:00:00', true, false, false),
('Dylan', 'Autumn Showdown', 10, 'SanFrancisco', 'Market', 22, '2024-08-10 09:00:00', '2024-08-15 18:00:00', true, false, false),
('Ella', 'City Champions', 18, 'Miami', 'Collins', 120, '2024-09-01 09:00:00', '2024-09-10 18:00:00', false, true, true),

('Finn', 'Battle Royale', 16, 'Boston', 'Massachusetts', 50, '2024-10-10 09:00:00', '2024-10-20 18:00:00', true, true, false),
('Grace', 'Superstar League', 10, 'Seattle', 'First', 80, '2024-11-05 09:00:00', '2024-11-15 18:00:00', true, false, false),
('Hank', 'Summer Challenge', 12, 'Portland', 'Hawthorne', 67, '2024-07-10 09:00:00', '2024-07-15 18:00:00', false, true, false),
('Ivy', 'Elite Tournament', 14, 'LasVegas', 'LasVegas', 40, '2024-05-10 09:00:00', '2024-05-15 18:00:00', true, false, false),
('Jack', 'Regional Clash', 10, 'SanDiego', 'Gaslamp', 22, '2024-07-20 09:00:00', '2024-07-25 18:00:00', false, true, false),
('Kelly', 'Pro League', 18, 'LosAngeles', 'Hollywood', 55, '2024-08-01 09:00:00', '2024-08-10 18:00:00', true, false, true),
('Linda', 'Ultimate Championship', 16, 'Phoenix', 'Camelback', 35, '2024-12-05 09:00:00', '2024-12-15 18:00:00', true, false, false),
('Mike', 'National Games', 10, 'Houston', 'Westheimer', 75, '2024-10-01 09:00:00', '2024-10-07 18:00:00', false, false, false),
('Nina', 'Finals Series', 14, 'Austin', 'Second', 100, '2024-06-20 09:00:00', '2024-06-25 18:00:00', true, false, false),
('Oscar', 'Global Challenge', 12, 'Denver', 'Colfax', 88, '2024-12-05 09:00:00', '2024-12-15 18:00:00', false, true, false),

('Peter', 'International Showdown', 20, 'Chicago', 'Wacker', 10, '2024-12-15 09:00:00', '2024-12-25 18:00:00', true, true, true),
('Quincy', 'Champion Showdown', 18, 'SanFrancisco', 'Embarcadero', 45, '2024-09-05 09:00:00', '2024-09-15 18:00:00', true, false, true),
('Rachel', 'Winter Finals', 14, 'Miami', 'Ocean', 32, '2024-09-01 09:00:00', '2024-09-10 18:00:00', false, true, false),
('Steve', 'City League', 12, 'Boston', 'Boylston', 55, '2024-10-10 09:00:00', '2024-10-20 18:00:00', true, false, true),
('Tom', 'Ultimate Finals', 20, 'LosAngeles', 'Hollywood', 150, '2024-09-05 09:00:00', '2024-09-15 18:00:00', false, true, false),
('Ursula', 'Elite Showdown', 16, 'Dallas', 'Main', 20, '2024-11-01 09:00:00', '2024-11-10 18:00:00', true, false, false),
('Vicky', 'World Finals', 18, 'Phoenix', 'Camelback', 21, '2025-01-05 09:00:00', '2025-01-10 18:00:00', true, true, false),
('Will', 'Super Series', 14, 'NewYork', 'Broadway', 100, '2024-06-01 09:00:00', '2024-06-10 18:00:00', true, false, false),
('Xander', 'Global Finals', 20, 'LosAngeles', 'Sunset', 150, '2024-09-01 09:00:00', '2024-09-10 18:00:00', false, true, true),
('Yara', 'Summer Showdown', 10, 'Chicago', 'Michigan', 55, '2024-08-10 09:00:00', '2024-08-15 18:00:00', true, false, false);
