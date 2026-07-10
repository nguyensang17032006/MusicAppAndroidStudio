const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/send-otp', authController.sendOtpEmail);

router.post('/login', authController.login);

router.post('/verify-and-register', authController.verifyAndRegister);

router.get('/me/:userId', authController.getUserProfile);

router.post('/update-profile', authController.updateProfile);

router.post('/forgot-password/send-otp', authController.sendOtpForgotPassword);

router.post('/forgot-password/verify', authController.verifyOtpForgotPassword);

router.post('/forgot-password/newpassword', authController.updateNewPassword);
module.exports = router;    