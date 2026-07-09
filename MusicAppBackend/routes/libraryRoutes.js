const express = require('express');
const router = express.Router();
const libraryController = require('../controllers/libraryController');

// Liked Songs
router.get('/liked-songs/:userId', libraryController.getLikedSongs);
router.post('/liked-songs/toggle', libraryController.toggleLikeSong);

// Playlists
router.get('/playlists/:userId', libraryController.getUserPlaylists);
router.post('/playlists/create', libraryController.createPlaylist);
router.post('/playlists/add-song', libraryController.addSongToPlaylist);

// Followed Artists
router.get('/followed-artists/:userId', libraryController.getFollowedArtists);
router.post('/followed-artists/toggle', libraryController.toggleFollowArtist);

module.exports = router;
