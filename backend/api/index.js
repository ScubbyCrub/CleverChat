/**
 *  Neatly combines the routes into one place
 */
const router = require('express').Router();
const registerRoutes = require('./register');
const signInRoutes = require('./signin');

router.use("/register", registerRoutes);
router.use("/signin", signInRoutes);

module.exports = router;

