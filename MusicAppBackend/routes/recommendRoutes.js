const express = require('express');
const router = express.Router();
const recommendController = require('../controllers/recommendController');

router.post('/log', recommendController.logInteraction);

router.get('/', recommendController.getRecommendations);

module.exports = router;