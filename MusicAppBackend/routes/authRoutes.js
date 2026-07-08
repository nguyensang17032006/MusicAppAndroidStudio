const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/send-otp', authController.sendOtpEmail);

router.post('/login', authController.login);

router.post('/verify-and-register', authController.verifyAndRegister);

module.exports = router;