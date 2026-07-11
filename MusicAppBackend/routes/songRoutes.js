const express = require('express');
const router = express.Router();
const { upload } = require('../config/cloudinary');
const songController = require('../controllers/songController');

router.get('/', songController.getAllSongs);
router.post('/', upload.fields([{ name: 'audio', maxCount: 1 }, { name: 'cover', maxCount: 1 }]), songController.saveSong);
router.post('/:id/view', songController.incrementView);
router.delete('/:id', songController.deleteSong);

module.exports = router;