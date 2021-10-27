const router = require("express").Router();

/**
 * this will handle the register routes
 */

router.get("/", (req, res) => {
    res.status(200).send("Get request sent to register endpoint");
})

router.post("/", (req, res) => {
    res.status(200).send("Post request sent to register endpoint");
})

router.put("/:id", (req, res) => {
    res.status(200).send("Put request sent to register endpoint");
})

router.delete("/:id", (req, res) => {
    res.status(200).send("Delete request sent to register endpoint");
})


module.exports = router;

