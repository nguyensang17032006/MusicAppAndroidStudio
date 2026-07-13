const express = require('express');
const router = express.Router();
const artistController = require('../controllers/artistController');
const { upload } = require('../config/cloudinary');

router.get('/', artistController.getAllArtists);
router.post('/', upload.single('avatar'), artistController.saveArtist);
router.delete('/:id', artistController.deleteArtist);

module.exports = router;
