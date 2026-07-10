const db = require('../config/db');

const getDashboardStats = async (req, res) => {
    try {
        // 1. Get counts
        const [[{ count: songsCount }]] = await db.query('SELECT COUNT(*) AS count FROM songs');
        const [[{ count: artistsCount }]] = await db.query('SELECT COUNT(*) AS count FROM artists');
        const [[{ count: genresCount }]] = await db.query('SELECT COUNT(*) AS count FROM genres');
        const [[{ count: playlistsCount }]] = await db.query('SELECT COUNT(*) AS count FROM playlists');
        const [[{ count: usersCount }]] = await db.query('SELECT COUNT(*) AS count FROM users');
        const [[{ total_views }]] = await db.query('SELECT SUM(views) AS total_views FROM songs');
        const [[{ count: interactionsCount }]] = await db.query('SELECT COUNT(*) AS count FROM interaction_logs');
        const [[{ count: likesCount }]] = await db.query('SELECT COUNT(*) AS count FROM user_liked_songs');

        // 2. Get trending songs (Top 5 by views)
        const [songs] = await db.query('SELECT * FROM songs ORDER BY views DESC LIMIT 5');
        
        if (songs.length > 0) {
            const songIds = songs.map(s => s.id);
            
            // Get artist details for these songs
            const [songArtists] = await db.query(`
                SELECT sa.song_id, sa.artist_id, sa.is_main_artist, a.name 
                FROM song_artists sa 
                JOIN artists a ON sa.artist_id = a.id
                WHERE sa.song_id IN (?)
            `, [songIds]);

            // Get genre details for these songs
            const [songGenres] = await db.query(`
                SELECT sg.song_id, sg.genre_id, g.name 
                FROM song_genres sg 
                JOIN genres g ON sg.genre_id = g.id
                WHERE sg.song_id IN (?)
            `, [songIds]);

            const artistsMap = {};
            songArtists.forEach(sa => {
                if (!artistsMap[sa.song_id]) {
                    artistsMap[sa.song_id] = [];
                }
                artistsMap[sa.song_id].push({
                    artist_id: sa.artist_id,
                    name: sa.name,
                    is_main_artist: sa.is_main_artist === 1 || sa.is_main_artist === true
                });
            });

            const genresMap = {};
            songGenres.forEach(sg => {
                if (!genresMap[sg.song_id]) {
                    genresMap[sg.song_id] = [];
                }
                genresMap[sg.song_id].push({
                    genre_id: sg.genre_id,
                    name: sg.name
                });
            });

            songs.forEach(s => {
                s.artists = artistsMap[s.id] || [];
                s.genres = genresMap[s.id] || [];
            });
        }

        // 3. Get popular genres (Top 5 based on song count)
        const [popularGenres] = await db.query(`
            SELECT g.id, g.name, COUNT(sg.song_id) AS song_count
            FROM genres g
            LEFT JOIN song_genres sg ON g.id = sg.genre_id
            GROUP BY g.id, g.name
            ORDER BY song_count DESC
            LIMIT 5
        `);

        // 4. Get recent users (Top 5 most recently created)
        const [recentUsers] = await db.query(`
            SELECT id, email, gender, avatar_url, created_at
            FROM users
            ORDER BY created_at DESC
            LIMIT 5
        `);

        res.status(200).json({
            success: true,
            data: {
                counts: {
                    songs: songsCount || 0,
                    artists: artistsCount || 0,
                    genres: genresCount || 0,
                    playlists: playlistsCount || 0,
                    users: usersCount || 0,
                    views: parseInt(total_views, 10) || 0,
                    interactions: interactionsCount || 0,
                    likes: likesCount || 0
                },
                trendingSongs: songs,
                popularGenres: popularGenres,
                recentUsers: recentUsers
            }
        });
    } catch (error) {
        console.error('Error fetching dashboard stats:', error);
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getDashboardStats
};
