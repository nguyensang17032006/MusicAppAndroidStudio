const express = require('express');
const router = express.Router();
const chatController = require('../controllers/chatController');

router.get('/:userId1/:userId2', chatController.getChatHistory);

module.exports = router;
