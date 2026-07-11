const express = require('express');
const router = express.Router();
const streakController = require('../controllers/streakController');

router.get('/:userId', streakController.getStreak);
router.post('/track-time', streakController.trackTime);

module.exports = router;
