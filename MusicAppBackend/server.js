const express = require('express');
const cors = require('cors');
require('dotenv').config();
const { urlencoded } = require('express');

const db = require('./config/db');
const cloudinaryConfig = require('./config/cloudinary');
const supabase = require('./config/supabase');

const http = require('http');
const { Server } = require('socket.io');

const app = express();
const PORT = process.env.PORT || 3000;

const server = http.createServer(app);
const io = new Server(server, {
    cors: { origin: '*' }
});

const connectedUsers = {};

io.on('connection', (socket) => {
    socket.on('user_connected', (userId) => {
        if (!userId) return;
        connectedUsers[userId] = {
            socketId: socket.id,
            isOnline: true,
            currentSong: null
        };
        io.emit('friend_status_changed', { userId, isOnline: true, currentSong: null });
    });

    socket.on('playing_song', (data) => {
        const { userId, songTitle } = data;
        if (userId && connectedUsers[userId]) {
            connectedUsers[userId].currentSong = songTitle;
            io.emit('friend_status_changed', { userId, isOnline: true, currentSong: songTitle });
        }
    });

    socket.on('disconnect', () => {
        for (const [userId, info] of Object.entries(connectedUsers)) {
            if (info.socketId === socket.id) {
                delete connectedUsers[userId];
                io.emit('friend_status_changed', { userId, isOnline: false, currentSong: null });
                break;
            }
        }
    });
});

app.use((req, res, next) => {
    req.io = io;
    req.connectedUsers = connectedUsers;
    next();
});

app.use(cors());
app.use(express.json());
app.use(urlencoded({ extended: true }));


const songRoutes = require('./routes/songRoutes');
const authRoutes = require('./routes/authRoutes');
const artistRoutes = require('./routes/artistRoutes');
const genreRoutes = require('./routes/genreRoutes');
const recommendRoutes = require('./routes/recommendRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const statsRoutes = require('./routes/statsRoutes');
const libraryRoutes = require('./routes/libraryRoutes');
const streakRoutes = require('./routes/streakRoutes');
const { startStreakCron } = require('./utils/streakCron');

// Run table creation check on startup
db.query(`
    CREATE TABLE IF NOT EXISTS user_streaks (
        user_id VARCHAR(255) PRIMARY KEY,
        current_streak INT DEFAULT 0,
        max_streak INT DEFAULT 0,
        last_completed_date DATE NULL,
        today_listening_time INT DEFAULT 0,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );
`).then(() => {
    console.log("Database table 'user_streaks' initialized successfully.");
}).catch(err => {
    console.error("Error creating table 'user_streaks':", err.message);
});

// Start streak cron job
startStreakCron();

app.use('/api/auth', authRoutes);
app.use('/api/songs', songRoutes);
app.use('/api/artists', artistRoutes);
app.use('/api/genres', genreRoutes);
app.use('/api/recommendations', recommendRoutes);
app.use('/api/upload', uploadRoutes);
app.use('/api/stats', statsRoutes);
app.use('/api/library', libraryRoutes);
app.use('/api/streak', streakRoutes);

app.use((err, req, res, next) => {
    if (err) {
        return res.status(400).json({ success: false, message: err.message });
    }
    next();
});

server.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});